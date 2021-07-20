package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.Optional;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTriggerRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplicationRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is responsible for processing incoming events/execution-requests.
 */
@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final EventTriggerRepository eventTriggerRepository;
    private final QuantumApplicationRepository quantumApplicationRepository;
    private final ScriptExecutionService scriptExecutionService;

    /**
     * This method is responsible for processing an incoming event.
     *
     * @param eventPayload
     */
    @Transactional
    public void processEvent(IBMQEventPayload eventPayload) {
        // Get all Quantum-Applications that should be executed
        Set<QuantumApplication> applicationsToExecute = eventTriggerRepository.findByEventData(eventPayload);

        // Execute the scripts of the Quantum-Applications
        for (QuantumApplication applicationToExecute : applicationsToExecute) {
            scriptExecutionService.executeScript(applicationToExecute, eventPayload);
        }
    }

    /**
     * This method is responsible for invoking a single Quantum-Application.
     * (manually triggered outside of any kind of events)
     *
     * @param name
     * @param eventPayload
     */
    @Transactional
    public void invokeAction(String name, IBMQEventPayload eventPayload) {
        Optional<QuantumApplication> quantumApplicationOptional = quantumApplicationRepository.findByName(name);
        if (quantumApplicationOptional.isPresent()) {
            scriptExecutionService.executeScript(quantumApplicationOptional.get(), eventPayload);
        }
    }
}
