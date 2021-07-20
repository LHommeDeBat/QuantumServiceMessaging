package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTrigger;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTriggerRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplicationRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.exception.QuantumApplicationScriptException;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuantumApplicationService {

    private final QuantumApplicationRepository repository;
    private final EventTriggerRepository eventTriggerRepository;
    private final EventProcessor eventProcessor;

    public QuantumApplication createQuantumApplication(MultipartFile script, QuantumApplication quantumApplication) {
        QuantumApplication createdQuantumApplication = repository.save(quantumApplication);
        storeFileToFileSystem(quantumApplication.getFilepath(), script);
        createExecutionFile(quantumApplication);
        return createdQuantumApplication;
    }

    public void invokeAction(String name, IBMQEventPayload payload) {
        new Thread(() -> eventProcessor.invokeAction(name, payload)).start();
    }

    public ByteArrayResource downloadQuantumApplicationScript(String name) {
        QuantumApplication application = getQuantumApplication(name);
        Path scriptPath = Paths.get(application.getFilepath());

        try {
            return new ByteArrayResource(Files.readAllBytes(scriptPath));
        } catch (IOException e) {
            log.error("Could not read file of quantum application '{}'", application.getName(), e);
            throw new QuantumApplicationScriptException(e.getMessage());
        }
    }

    public Set<QuantumApplication> getQuantumApplications(boolean noResultEventOnly) {
        return repository.findAll(noResultEventOnly);
    }

    public QuantumApplication getQuantumApplication(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("There is no quantum application with name '" + name + "'!"));
    }

    public Set<EventTrigger> getQuantumApplicationEventTriggers(String name) {
        return repository.findQuantumApplicationEventTriggers(name);
    }

    public Set<Job> getQuantumApplicationJobs(String name) {
        return repository.findQuantumApplicationJobs(name);
    }

    public void unlinkExecutionResultEventTrigger(String name) {
        QuantumApplication existingQuantumApplication = getQuantumApplication(name);
        existingQuantumApplication.setExecutionResultEventTrigger(null);
        repository.save(existingQuantumApplication);
    }

    public void deleteQuantumApplication(String name) {
        QuantumApplication existingQuantumApplication = getQuantumApplication(name);

        // Unregister quantum application from all events
        for (EventTrigger event: existingQuantumApplication.getEventTriggers()) {
            event.getQuantumApplications().removeIf(application -> application.getName().equals(name));
            eventTriggerRepository.save(event);
        }

        // Delete quantum application and script from file system
        repository.delete(existingQuantumApplication);
        deleteFileFromFileSystem(existingQuantumApplication.getFilepath(), existingQuantumApplication.getExecutionFilepath());
    }

    private void storeFileToFileSystem(String filepath, MultipartFile script) {
        try {
            File quantumScript = new File(filepath);
            FileUtils.writeByteArrayToFile(quantumScript, script.getBytes());
            if (quantumScript.createNewFile()) {
                throw new QuantumApplicationScriptException("Could not save script!");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new QuantumApplicationScriptException("Could not read script!", e);
        }
    }

    private void createExecutionFile(QuantumApplication application) {
        try {
            FileWriter fw = new FileWriter(application.getExecutionFilepath());
            fw.write("import sys");
            fw.write(System.lineSeparator());
            fw.write("import json");
            fw.write(System.lineSeparator());
            fw.write("import " + application.getName());
            fw.write(System.lineSeparator());
            fw.write(System.lineSeparator());
            fw.write("params = {");
            fw.write(System.lineSeparator());
            fw.write("    \"apiToken\": sys.argv[1],");
            fw.write(System.lineSeparator());
            fw.write("    \"device\": sys.argv[2]");

            if (!Objects.isNull(application.getParameters()) && !application.getParameters().isEmpty()) {
                fw.write(",");
                int argvPosition = 3;
                Iterator<String> iterator = application.getParameters().keySet().iterator();
                while (iterator.hasNext()) {
                    fw.write(System.lineSeparator());
                    String parameter = iterator.next();
                    fw.write("    \"" + parameter + "\": ");

                    switch (application.getParameters().get(parameter).getType()) {
                        case INTEGER:
                            fw.write("int(sys.argv[" + argvPosition + "])");
                            break;
                        case FLOAT:
                            fw.write("float(sys.argv[" + argvPosition + "])");
                            break;
                        default:
                            fw.write("sys.argv[" + argvPosition + "]");
                            break;
                    }

                    if (iterator.hasNext()) {
                        fw.write(",");
                    }
                    argvPosition++;
                }
            }

            fw.write(System.lineSeparator());
            fw.write("}");
            fw.write(System.lineSeparator());
            fw.write(System.lineSeparator());
            fw.write("result = " + application.getName() + ".main(params)");
            fw.write(System.lineSeparator());
            fw.write("print(json.dumps(result))");
            fw.close();
        } catch (IOException e) {
            throw new QuantumApplicationScriptException("Could not generate execution script", e);
        }
    }

    private void deleteFileFromFileSystem(String filepath, String executionFilepath) {
        try {
            Files.deleteIfExists(Path.of(filepath));
            Files.deleteIfExists(Path.of(executionFilepath));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new QuantumApplicationScriptException("Could not delete script!", e);
        }
    }
}
