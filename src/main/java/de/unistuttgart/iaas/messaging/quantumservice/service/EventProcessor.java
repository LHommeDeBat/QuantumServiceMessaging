package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.Map;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.Event;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventType;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final EventRepository eventRepository;
    private final ScriptExecutionService scriptExecutionService;

    public void processEvent(IBMQEventPayload eventPayload) {
        Set<QuantumApplication> applicationsToExecute = eventRepository.findByEventData(eventPayload);

        for (QuantumApplication applicationToExecute : applicationsToExecute) {
            scriptExecutionService.executeScript(applicationToExecute, eventPayload);
        }
    }
}
