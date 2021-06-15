package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.Event;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
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
    private final ScriptExecutionService scriptExecutionService;

    public Set<Job> fireEvent(String name, IBMQEventPayload payload) {
        Set<Job> createdJobs = new HashSet<>();

        Set<QuantumApplication> eventApplications = getEventApplications(name);
        for (QuantumApplication eventApplication : eventApplications) {
            createdJobs.add(scriptExecutionService.executeScript(eventApplication, payload));
        }
        return createdJobs;
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
        existingEvent.setQuantumApplications(new HashSet<>());
        existingEvent = repository.save(existingEvent);
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
