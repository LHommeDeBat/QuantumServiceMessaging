package de.unistuttgart.iaas.messaging.quantumservice.controller;

import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.hateoas.EventLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.hateoas.JobLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.hateoas.QuantumApplicationLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.EventDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.JobDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.QuantumApplicationDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.Event;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import de.unistuttgart.iaas.messaging.quantumservice.service.EventService;
import de.unistuttgart.iaas.messaging.quantumservice.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;
    private final EventLinkAssembler linkAssembler;
    private final QuantumApplicationLinkAssembler quantumApplicationLinkAssembler;
    private final JobLinkAssembler jobLinkAssembler;

    @Transactional
    @PostMapping("/{name}")
    public ResponseEntity<CollectionModel<EntityModel<JobDto>>> fireEvent(@PathVariable String name, @RequestBody IBMQEventPayload payload) {
        Set<Job> createdJobs = service.fireEvent(name, payload);
        return new ResponseEntity<>(jobLinkAssembler.toModel(createdJobs, JobDto.class), HttpStatus.OK);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<EntityModel<EventDto>> createEvent(@Validated @RequestBody EventDto event) {
        Event createdEvent = service.createEvent(ModelMapperUtils.convert(event, Event.class));
        return new ResponseEntity<>(linkAssembler.toModel(createdEvent, EventDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<EventDto>>> getEvents() {
        return new ResponseEntity<>(linkAssembler.toModel(service.getEvents(), EventDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/{name}")
    public ResponseEntity<EntityModel<EventDto>> getEvent(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.getEvent(name), EventDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/{name}/quantum-applications")
    public ResponseEntity<CollectionModel<EntityModel<QuantumApplicationDto>>> getEventApplications(@PathVariable String name) {
        return new ResponseEntity<>(quantumApplicationLinkAssembler.toModel(service.getEventApplications(name), QuantumApplicationDto.class), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/{name}/quantum-applications/{applicationName}")
    public ResponseEntity<EntityModel<EventDto>> registerQuantumApplication(@PathVariable String name, @PathVariable String applicationName) {
        return new ResponseEntity<>(linkAssembler.toModel(service.registerApplication(name, applicationName), EventDto.class), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/{name}/quantum-applications/{applicationName}")
    public ResponseEntity<EntityModel<EventDto>> unregisterQuantumApplication(@PathVariable String name, @PathVariable String applicationName) {
        return new ResponseEntity<>(linkAssembler.toModel(service.unregisterApplication(name, applicationName), EventDto.class), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String name) {
        service.deleteEvent(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
