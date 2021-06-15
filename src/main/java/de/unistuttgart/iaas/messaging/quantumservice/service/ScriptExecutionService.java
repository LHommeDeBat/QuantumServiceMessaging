package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.unistuttgart.iaas.messaging.quantumservice.configuration.IBMQProperties;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScriptExecutionService {

    private final IBMQProperties ibmqProperties;
    private final JobRepository jobRepository;

    public Job executeScript(QuantumApplication application, IBMQEventPayload payload) {
        String[] command = generateCommand(application, payload);
        String jobId = null;

        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                jobId = line;
                log.info("CMD: " + jobId);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        // Create running Job
        Job job = new Job();
        job.setIbmqId(jobId);
        job.setStatus(JobStatus.CREATED);
        job.setQuantumApplication(application);
        job = jobRepository.save(job);

        return job;
    }

    private String[] generateCommand(QuantumApplication application, IBMQEventPayload payload) {
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(application.getFilepath());
        command.add(ibmqProperties.getApiToken());
        command.add(payload.getDevice());
        return command.toArray(new String[0]);
    }
}
