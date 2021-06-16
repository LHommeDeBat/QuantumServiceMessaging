package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.Map;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.Event;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventType;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final EventRepository eventRepository;
    private final ScriptExecutionService scriptExecutionService;

    public void processEvent(EventType type, String ibmqDevice, Map<String, Object> additionalProperties) {
        Set<Event> eventsToTrigger = eventRepository.findByEventData(type, additionalProperties);

        for (Event event : eventsToTrigger) {
            for (QuantumApplication applicationToExecute : event.getQuantumApplications()) {
                scriptExecutionService.executeScript(applicationToExecute, ibmqDevice);
            }
        }
    }
}
