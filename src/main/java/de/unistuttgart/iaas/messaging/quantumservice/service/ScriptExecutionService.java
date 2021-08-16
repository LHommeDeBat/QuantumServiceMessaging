package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

/**
 * This class is responsible for executing the scripts of Quantum-Applications.
 */
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
        Map<String, String> usedParameters = new HashMap<>();
        // Generate the Python CLI-Command for executing the script
        String[] command = generateCommand(application, eventPayload.getDevice(), eventPayload.getAdditionalProperties(), usedParameters);
        String executionPrint = null;
        ZonedDateTime scriptExecutionDate = ZonedDateTime.now();

        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                executionPrint = line;
                log.info(line);
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
        job.setStatus(JobStatus.CREATING);
        job.setDevice(eventPayload.getDevice());
        job.setReplyTo(eventPayload.getReplyTo());
        job.setScriptExecutionDate(scriptExecutionDate);
        job.setQuantumApplication(application);
        job.setUsedParameters(usedParameters);

        application.getJobs().add(job);
        application.setExecutionEnabled(false);
        quantumApplicationRepository.save(application);
    }

    /**
     * This method generates the Python CLI-Command for running the correct script with correct arguments/parameters.
     * (Example: python someFilename.py argument1 argument2 argument3)
     *
     * @param application
     * @param ibmqDevice
     * @param eventProperties
     * @param usedParameters
     * @return commandAsArray
     */
    private String[] generateCommand(QuantumApplication application, String ibmqDevice, Map<String, Object> eventProperties, Map<String, String> usedParameters) {
        List<String> command = new ArrayList<>();
        // Required stuff that is the same for each Quantum-Application
        command.add("python");
        command.add(application.getExecutionFilepath());
        command.add(ibmqProperties.getApiToken());
        command.add(ibmqDevice);

        // Add optional parameters that are different for each Quantum-Application
        for (String parameter : application.getParameters().keySet()) {
            Object parameterValue = eventProperties.get(parameter);
            // If Event does not provide parameter use stored default value
            if (Objects.isNull(parameterValue)) {
                command.add(application.getParameters().get(parameter).getDefaultValue());
                usedParameters.put(parameter, application.getParameters().get(parameter).getDefaultValue());
            } else {
                command.add(parameterValue.toString());
                usedParameters.put(parameter, parameterValue.toString());
            }
        }

        return command.toArray(new String[0]);
    }
}
