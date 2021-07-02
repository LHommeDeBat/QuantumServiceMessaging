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

@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final EventTriggerRepository eventTriggerRepository;
    private final QuantumApplicationRepository quantumApplicationRepository;
    private final ScriptExecutionService scriptExecutionService;

    @Transactional
    public void processEvent(IBMQEventPayload eventPayload) {
        Set<QuantumApplication> applicationsToExecute = eventTriggerRepository.findByEventData(eventPayload);

        for (QuantumApplication applicationToExecute : applicationsToExecute) {
            scriptExecutionService.executeScript(applicationToExecute, eventPayload);
        }
    }

    @Transactional
    public void invokeAction(String name, IBMQEventPayload eventPayload) {
        Optional<QuantumApplication> quantumApplicationOptional = quantumApplicationRepository.findByName(name);
        if (quantumApplicationOptional.isPresent()) {
            scriptExecutionService.executeScript(quantumApplicationOptional.get(), eventPayload);
        }
    }
}
