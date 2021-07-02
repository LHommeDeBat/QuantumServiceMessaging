package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.iaas.messaging.quantumservice.configuration.IBMQProperties;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.ExecutionResult;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplicationRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScriptExecutionService {

    private final IBMQProperties ibmqProperties;
    private final QuantumApplicationRepository quantumApplicationRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void executeScript(QuantumApplication application, IBMQEventPayload eventPayload) {
        log.info("Executing application {} on device {}...", application.getName(), eventPayload.getDevice());
        String[] command = generateCommand(application, eventPayload.getDevice());
        String executionPrint = null;
        ZonedDateTime scriptExecutionDate = ZonedDateTime.now();

        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                executionPrint = line;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }


        ExecutionResult result;
        try {
            result = objectMapper.readValue(executionPrint, ExecutionResult.class);
            log.info("Execution of application {} on device {} completed! Job created!", application.getName(), eventPayload.getDevice());
        } catch (JsonProcessingException e) {
            result = null;
            log.error("Could not process result of quantum script!", e);
        }

        // Create running Job
        Job job = new Job();
        job.setIbmqId(result.getJobId());
        job.setStatus(JobStatus.CREATED);
        job.setDevice(eventPayload.getDevice());
        job.setReplyTo(eventPayload.getReplyTo());
        job.setScriptExecutionDate(scriptExecutionDate);
        job.setQuantumApplication(application);

        application.getJobs().add(job);
        application.setExecutionEnabled(false);
        quantumApplicationRepository.save(application);
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
