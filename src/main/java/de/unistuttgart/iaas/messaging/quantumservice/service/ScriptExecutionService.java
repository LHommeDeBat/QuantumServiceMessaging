package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.iaas.messaging.quantumservice.configuration.IBMQProperties;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.ExecutionResult;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScriptExecutionService {

    private final IBMQProperties ibmqProperties;
    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper;

    public Job executeScript(QuantumApplication application, String ibmqDevice) {
        log.info("Executing application {} on device {}...", application.getName(), ibmqDevice);
        String[] command = generateCommand(application, ibmqDevice);
        String executionPrint = null;

        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                executionPrint = line;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }


        ExecutionResult result;
        try {
            result = objectMapper.readValue(executionPrint, ExecutionResult.class);
            log.info("Execution of application {} on device {} completed! Job created!", application.getName(), ibmqDevice);
        } catch (JsonProcessingException e) {
            result = null;
            log.error("Could not process result of quantum script!", e);
        }

        // Create running Job
        Job job = new Job();
        job.setIbmqId(result.getJobId());
        job.setStatus(JobStatus.CREATED);
        job.setQuantumApplication(application);
        job = jobRepository.save(job);

        return job;
    }

    private String[] generateCommand(QuantumApplication application, String ibmqDevice) {
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(application.getExecutionFilepath());
        command.add(ibmqProperties.getApiToken());
        command.add(ibmqDevice);
        return command.toArray(new String[0]);
    }
}
