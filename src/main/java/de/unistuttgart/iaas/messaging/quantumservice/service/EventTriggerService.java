package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTrigger;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTriggerRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventTriggerService {

    private final EventTriggerRepository repository;
    private final QuantumApplicationService applicationService;
    private final EventProcessor eventProcessor;

    public void fireEvent(IBMQEventPayload eventPayload) {
        new Thread(() -> eventProcessor.processEvent(eventPayload)).start();
    }

    public EventTrigger createEventTrigger(EventTrigger eventTrigger) {
        return repository.save(eventTrigger);
    }

    public Set<EventTrigger> getEventTriggers() {
        return repository.findAll();
    }

    public EventTrigger getEventTrigger(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("There is no event trigger with name '" + name + "'!"));
    }

    public Set<QuantumApplication> getEventTriggerApplications(String name) {
        return repository.findEventApplications(name);
    }

    public void deleteEventTrigger(String name) {
        EventTrigger existingEventTrigger = getEventTrigger(name);

        // Unregister all applications
        existingEventTrigger.setQuantumApplications(new HashSet<>());
        existingEventTrigger = repository.save(existingEventTrigger);

        // Delete Event
        repository.delete(existingEventTrigger);
    }

    public EventTrigger registerApplication(String name, String applicationName) {
        EventTrigger existingEventTrigger = getEventTrigger(name);
        existingEventTrigger.getQuantumApplications().add(applicationService.getQuantumApplication(applicationName));
        return repository.save(existingEventTrigger);
    }

    public EventTrigger unregisterApplication(String name, String applicationName) {
        EventTrigger existingEventTrigger = getEventTrigger(name);
        existingEventTrigger.getQuantumApplications().removeIf(application -> application.getName().equals(applicationName));
        return repository.save(existingEventTrigger);
    }
}
