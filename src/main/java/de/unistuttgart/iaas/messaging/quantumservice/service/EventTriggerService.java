package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTrigger;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTriggerRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.ExecutionResultEventTrigger;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * This is a Service-Class performing operations on Event-Triggers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventTriggerService {

    private final EventTriggerRepository repository;
    private final QuantumApplicationService applicationService;
    private final EventProcessor eventProcessor;

    /**
     * This method fires a event in a new thread.
     *
     * @param eventPayload
     */
    public void fireEvent(IBMQEventPayload eventPayload) {
        new Thread(() -> eventProcessor.processEvent(eventPayload)).start();
    }

    /**
     * This method creates a new Event-Trigger and stores it in the database.
     *
     * @param eventTrigger
     * @return createdEventTrigger
     */
    public EventTrigger createEventTrigger(EventTrigger eventTrigger) {
        // If Trigger is a ExecutionResultEventTrigger
        if (eventTrigger instanceof ExecutionResultEventTrigger) {
            ExecutionResultEventTrigger executionResultEventTrigger = (ExecutionResultEventTrigger) eventTrigger;
            // Check if application to be linked exists and store it into Trigger
            executionResultEventTrigger.setExecutedApplication(applicationService.getQuantumApplication(executionResultEventTrigger.getExecutedApplication().getName()));
            return repository.save(executionResultEventTrigger);
        }
        return repository.save(eventTrigger);
    }

    /**
     * This method returns all stored Event-Triggers
     *
     * @return eventTriggers
     */
    public Set<EventTrigger> getEventTriggers() {
        return repository.findAll();
    }

    /**
     * This method returns a specific Event-Trigger using it's unique name.
     *
     * @param name
     * @return eventTrigger
     */
    public EventTrigger getEventTrigger(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("There is no event trigger with name '" + name + "'!"));
    }

    /**
     * This method returns all Quantum-Applications of a specific Event-Trigger using it's unique name.
     *
     * @param name
     * @return triggerApplications
     */
    public Set<QuantumApplication> getEventTriggerApplications(String name) {
        return repository.findEventApplications(name);
    }

    /**
     * This method deletes an existing Event-Trigger using it's unique name.
     *
     * @param name
     */
    public void deleteEventTrigger(String name) {
        EventTrigger existingEventTrigger = getEventTrigger(name);

        // Unregister all applications
        existingEventTrigger.setQuantumApplications(new HashSet<>());
        existingEventTrigger = repository.save(existingEventTrigger);

        // Unlink executed Quantum-Application in case of a ExecutionResultEventTrigger as well
        if (existingEventTrigger instanceof ExecutionResultEventTrigger) {
            applicationService.unlinkExecutionResultEventTrigger(((ExecutionResultEventTrigger) existingEventTrigger).getExecutedApplication().getName());
        }

        // Delete Event
        repository.delete(existingEventTrigger);
    }

    /**
     * This method links a Event-Trigger with an Quantum-Application using their unique names.
     *
     * @param name
     * @param applicationName
     * @return linkedEventTrigger
     */
    public EventTrigger registerApplication(String name, String applicationName) {
        EventTrigger existingEventTrigger = getEventTrigger(name);
        existingEventTrigger.getQuantumApplications().add(applicationService.getQuantumApplication(applicationName));
        return repository.save(existingEventTrigger);
    }

    /**
     * This method unlinks a Event-Trigger with an Quantum-Application using their unique names.
     *
     * @param name
     * @param applicationName
     * @return unlinkedEventTrigger
     */
    public EventTrigger unregisterApplication(String name, String applicationName) {
        EventTrigger existingEventTrigger = getEventTrigger(name);
        existingEventTrigger.getQuantumApplications().removeIf(application -> application.getName().equals(applicationName));
        return repository.save(existingEventTrigger);
    }
}
