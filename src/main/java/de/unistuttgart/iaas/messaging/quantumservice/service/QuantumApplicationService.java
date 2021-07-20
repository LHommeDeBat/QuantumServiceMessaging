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

/**
 * This is a Service-Class performing operations on Quantum-Applications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuantumApplicationService {

    private final QuantumApplicationRepository repository;
    private final EventTriggerRepository eventTriggerRepository;
    private final EventProcessor eventProcessor;

    /**
     * This method creates a Quantum-Application and stores it into the database.
     *
     * @param script
     * @param quantumApplication
     * @return createdQuantumApplication
     */
    public QuantumApplication createQuantumApplication(MultipartFile script, QuantumApplication quantumApplication) {
        QuantumApplication createdQuantumApplication = repository.save(quantumApplication);
        storeFileToFileSystem(quantumApplication.getFilepath(), script);
        createExecutionFile(quantumApplication);
        return createdQuantumApplication;
    }

    /**
     * This method invokes a specific Quantum-Application with a dynamic payload in a new thread.
     *
     * @param name
     * @param payload
     */
    public void invokeAction(String name, IBMQEventPayload payload) {
        new Thread(() -> eventProcessor.invokeAction(name, payload)).start();
    }

    /**
     * This method returns the script of a specific Quantum-Application as a ByteArrayResource.
     *
     * @param name
     * @return scriptAsByte
     */
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

    /**
     * This method returns all Quantum-Applications with optional parameters for filtering.
     *
     * @param noResultEventOnly (if true only applications are returned that do not belong to a ExecutionResultTrigger)
     * @return quantumApplications
     */
    public Set<QuantumApplication> getQuantumApplications(boolean noResultEventOnly) {
        return repository.findAll(noResultEventOnly);
    }

    /**
     * This method returns a specific Quantum-Application using it's unique name.
     *
     * @param name
     * @return quantumApplication
     */
    public QuantumApplication getQuantumApplication(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("There is no quantum application with name '" + name + "'!"));
    }

    /**
     * This method returns all Event-Triggers of a specific Quantum-Application using it's unique name.
     *
     * @param name
     * @return applicationTriggers
     */
    public Set<EventTrigger> getQuantumApplicationEventTriggers(String name) {
        return repository.findQuantumApplicationEventTriggers(name);
    }

    /**
     * This method returns all Jobs of a specific Quantum-Application using it's unique name.
     *
     * @param name
     * @return applicationJobs
     */
    public Set<Job> getQuantumApplicationJobs(String name) {
        return repository.findQuantumApplicationJobs(name);
    }

    /**
     * This method unlinks a specific Quantum-Application from a ExecutionResultEventTrigger.
     *
     * @param name
     */
    public void unlinkExecutionResultEventTrigger(String name) {
        QuantumApplication existingQuantumApplication = getQuantumApplication(name);
        existingQuantumApplication.setExecutionResultEventTrigger(null);
        repository.save(existingQuantumApplication);
    }

    /**
     * This method deletes a specific Quantum-Application using it's unique name from the database.
     * @param name
     */
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

    /**
     * This method stores a file to the filesystem using a given filepath.
     * (Used to store Quantum-Application scripts)
     *
     * @param filepath
     * @param script
     */
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

    /**
     * This method generates an python file that is used to execute the actual function of the Quantum-Application script
     * with a user friendly input parameter/argument.
     *
     * @param application
     */
    private void createExecutionFile(QuantumApplication application) {
        try {
            // Create FileWriter for execution file
            FileWriter fw = new FileWriter(application.getExecutionFilepath());
            // Write necessary imports
            fw.write("import sys");
            fw.write(System.lineSeparator());
            fw.write("import json");
            fw.write(System.lineSeparator());
            // Import actual script of application
            fw.write("import " + application.getName());
            fw.write(System.lineSeparator());
            fw.write(System.lineSeparator());
            // Generate parameter object with necessary parameters (IBMQ-Token, Device-ID)
            fw.write("params = {");
            fw.write(System.lineSeparator());
            fw.write("    \"apiToken\": sys.argv[1],");
            fw.write(System.lineSeparator());
            fw.write("    \"device\": sys.argv[2]");

            // Add all optional parameters of specific Quantum-Application to parameter-object (with correct types)
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
            // Add execution of method within application script and read results
            fw.write("result = " + application.getName() + ".main(params)");
            fw.write(System.lineSeparator());
            // Print results so they can be retrieved from JAVA
            fw.write("print(json.dumps(result))");
            fw.close();
        } catch (IOException e) {
            throw new QuantumApplicationScriptException("Could not generate execution script", e);
        }
    }

    /**
     * This method deletes a file from the file system using given file paths.
     * (It deletes the execution file and the actual file of a Quantum-Application)
     *
     * @param filepath
     * @param executionFilepath
     */
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
