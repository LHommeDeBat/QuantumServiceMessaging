package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.Event;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository repository;
    private final QuantumApplicationService applicationService;
    private final EventProcessor eventProcessor;

    public void fireEvent(IBMQEventPayload eventPayload) {
        eventProcessor.processEvent(eventPayload);
    }

    public Event createEvent(Event event) {
        return repository.save(event);
    }

    public Set<Event> getEvents() {
        return repository.findAll();
    }

    public Event getEvent(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("There is no event with name '" + name + "'!"));
    }

    public Set<QuantumApplication> getEventApplications(String name) {
        return repository.findEventApplications(name);
    }

    public void deleteEvent(String name) {
        Event existingEvent = getEvent(name);

        // Unregister all applications
        existingEvent.setQuantumApplications(new HashSet<>());
        existingEvent = repository.save(existingEvent);

        // Delete Event
        repository.delete(existingEvent);
    }

    public Event registerApplication(String name, String applicationName) {
        Event existingEvent = getEvent(name);
        existingEvent.getQuantumApplications().add(applicationService.getQuantumApplication(applicationName));
        return repository.save(existingEvent);
    }

    public Event unregisterApplication(String name, String applicationName) {
        Event existingEvent = getEvent(name);
        existingEvent.getQuantumApplications().removeIf(application -> application.getName().equals(applicationName));
        return repository.save(existingEvent);
    }
}
