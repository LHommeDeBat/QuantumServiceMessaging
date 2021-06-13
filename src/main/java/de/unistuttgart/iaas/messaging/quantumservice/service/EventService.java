package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.NoSuchElementException;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.Event;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository repository;

    public Event createEvent(Event event) {
        return repository.save(event);
    }

    public Set<Event> getEvents() {
        return repository.findAll();
    }

    public Event getEvent(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("There is no event with name '" + name + "'!"));
    }

    public void deleteEvent(String name) {
        Event existingEvent = getEvent(name);
        repository.delete(existingEvent);
    }
}
