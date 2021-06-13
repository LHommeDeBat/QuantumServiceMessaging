package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.Event;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplicationRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.exception.QuantumApplicationScriptException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuantumApplicationService {

    private final QuantumApplicationRepository repository;

    public QuantumApplication createQuantumApplication(MultipartFile script, QuantumApplication quantumApplication) {
        QuantumApplication createdQuantumApplication = repository.save(quantumApplication);
        storeFileToFileSystem(quantumApplication.getFilepath(), script);
        return createdQuantumApplication;
    }

    public Set<QuantumApplication> getQuantumApplications() {
        return repository.findAll();
    }

    public QuantumApplication getQuantumApplication(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("There is no quantum application with name '" + name + "'!"));
    }

    public Set<Event> getQuantumApplicationEvents(String name) {
        return repository.findQuantumApplicationEvents(name);
    }

    public void deleteQuantumApplication(String name) {
        QuantumApplication existingQuantumApplication = getQuantumApplication(name);
        repository.delete(existingQuantumApplication);
        deleteFileFromFileSystem(existingQuantumApplication.getFilepath());
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

    private void deleteFileFromFileSystem(String filepath) {
        try {
            Files.deleteIfExists(Path.of(filepath));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new QuantumApplicationScriptException("Could not delete script!", e);
        }
    }
}
