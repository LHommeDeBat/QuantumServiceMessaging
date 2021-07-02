package de.unistuttgart.iaas.messaging.quantumservice.controller;

import de.unistuttgart.iaas.messaging.quantumservice.hateoas.EventTriggerLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.hateoas.QuantumApplicationLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.EventTriggerDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.QuantumApplicationDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTrigger;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import de.unistuttgart.iaas.messaging.quantumservice.service.EventTriggerService;
import de.unistuttgart.iaas.messaging.quantumservice.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/event-triggers")
@RequiredArgsConstructor
public class EventTriggerController {

    private final EventTriggerService service;
    private final EventTriggerLinkAssembler linkAssembler;
    private final QuantumApplicationLinkAssembler quantumApplicationLinkAssembler;

    @Transactional
    @PostMapping("/fire-event")
    public ResponseEntity<Void> fireEvent(@RequestBody IBMQEventPayload payload) {
        service.fireEvent(payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<EntityModel<EventTriggerDto>> createEventTrigger(@Validated @RequestBody EventTriggerDto eventTrigger) {
        EventTrigger createdEvent = service.createEventTrigger(ModelMapperUtils.convert(eventTrigger, EventTrigger.class));
        return new ResponseEntity<>(linkAssembler.toModel(createdEvent, EventTriggerDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<EventTriggerDto>>> getEventTriggers() {
        return new ResponseEntity<>(linkAssembler.toModel(service.getEventTriggers(), EventTriggerDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/{name}")
    public ResponseEntity<EntityModel<EventTriggerDto>> getEventTrigger(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.getEventTrigger(name), EventTriggerDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/{name}/quantum-applications")
    public ResponseEntity<CollectionModel<EntityModel<QuantumApplicationDto>>> getEventTriggerApplications(@PathVariable String name) {
        return new ResponseEntity<>(quantumApplicationLinkAssembler.toModel(service.getEventTriggerApplications(name), QuantumApplicationDto.class), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/{name}/quantum-applications/{applicationName}")
    public ResponseEntity<EntityModel<EventTriggerDto>> registerQuantumApplication(@PathVariable String name, @PathVariable String applicationName) {
        return new ResponseEntity<>(linkAssembler.toModel(service.registerApplication(name, applicationName), EventTriggerDto.class), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/{name}/quantum-applications/{applicationName}")
    public ResponseEntity<EntityModel<EventTriggerDto>> unregisterQuantumApplication(@PathVariable String name, @PathVariable String applicationName) {
        return new ResponseEntity<>(linkAssembler.toModel(service.unregisterApplication(name, applicationName), EventTriggerDto.class), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteEventTrigger(@PathVariable String name) {
        service.deleteEventTrigger(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
