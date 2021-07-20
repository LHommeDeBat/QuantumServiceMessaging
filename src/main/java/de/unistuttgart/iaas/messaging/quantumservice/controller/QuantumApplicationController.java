package de.unistuttgart.iaas.messaging.quantumservice.controller;

import de.unistuttgart.iaas.messaging.quantumservice.hateoas.EventTriggerLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.hateoas.JobLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.hateoas.QuantumApplicationLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.event.EventTriggerDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.JobDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.QuantumApplicationDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import de.unistuttgart.iaas.messaging.quantumservice.service.QuantumApplicationService;
import de.unistuttgart.iaas.messaging.quantumservice.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class represents the represents the REST-Controller of the Quantum-Applications. It handles all incoming REST-Requests
 * for the Quantum-Applications.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "quantum-applications")
@RequiredArgsConstructor
public class QuantumApplicationController {

    private final QuantumApplicationService service;
    private final QuantumApplicationLinkAssembler linkAssembler;
    private final EventTriggerLinkAssembler eventLinkAssembler;
    private final JobLinkAssembler jobLinkAssembler;

    /**
     * This REST-Endpoint creates a new Quantum-Application using a Script (python file) and some metadata (dto object)
     *
     * @param script
     * @param dto
     * @return createdQuantumApplication
     */
    @Transactional
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EntityModel<QuantumApplicationDto>> createQuantumApplication(@RequestParam MultipartFile script, @RequestParam QuantumApplicationDto dto) {
        QuantumApplication createdQuantumApplication = service.createQuantumApplication(script, ModelMapperUtils.convert(dto, QuantumApplication.class));
        return new ResponseEntity<>(linkAssembler.toModel(createdQuantumApplication, QuantumApplicationDto.class), HttpStatus.OK);
    }

    /**
     * This REST-Endpoint manually invokes a specific Quantum-Application using it's unique name.
     *
     * @param name
     * @param payload
     * @return
     */
    @Transactional
    @PostMapping("/{name}/invoke")
    public ResponseEntity<Void> invokeQuantumApplication(@PathVariable String name, @RequestBody IBMQEventPayload payload) {
        service.invokeAction(name, payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This REST-Endpoint returns the script of a specific Quantum-Application using it's unique name.
     *
     * @param name
     * @return scriptBytes
     */
    @Transactional
    @GetMapping("/{name}/script")
    public ResponseEntity<ByteArrayResource> downloadQuantumApplicationScript(@PathVariable String name) {
        ByteArrayResource resource = service.downloadQuantumApplicationScript(name);
        return ResponseEntity.ok().contentLength(resource.contentLength()).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

    /**
     * This REST-Endpoint returns all existing Quantum-Applications. Further optional parameters allow filtering.
     *
     * @param noResultEventOnly (only applications should be returned that don't belong to a ExecutionResultEvent)
     * @return quantumApplications
     */
    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<QuantumApplicationDto>>> getQuantumApplications(@RequestParam(required = false) boolean noResultEventOnly) {
        return new ResponseEntity<>(linkAssembler.toModel(service.getQuantumApplications(noResultEventOnly), QuantumApplicationDto.class), HttpStatus.OK);
    }

    /**
     * This REST-Endpoint returns a specific Quantum-Application using it's unique name.
     *
     * @param name
     * @return quantumApplication
     */
    @Transactional
    @GetMapping("/{name}")
    public ResponseEntity<EntityModel<QuantumApplicationDto>> getQuantumApplication(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.getQuantumApplication(name), QuantumApplicationDto.class), HttpStatus.OK);
    }

    /**
     * This REST-Endpoints returns all Event-Triggers of a specific Quantum-Application using it's unique name.
     *
     * @param name
     * @return applicationTriggers
     */
    @Transactional
    @GetMapping("/{name}/event-triggers")
    public ResponseEntity<CollectionModel<EntityModel<EventTriggerDto>>> getQuantumApplicationEventTriggers(@PathVariable String name) {
        return new ResponseEntity<>(eventLinkAssembler.toModel(service.getQuantumApplicationEventTriggers(name), EventTriggerDto.class), HttpStatus.OK);
    }

    /**
     * This REST-Endpoints returns all Jobs of a specific Quantum-Application using it's unique name.
     *
     * @param name
     * @return applicationJobs
     */
    @Transactional
    @GetMapping("/{name}/jobs")
    public ResponseEntity<CollectionModel<EntityModel<JobDto>>> getQuantumApplicationJobs(@PathVariable String name) {
        return new ResponseEntity<>(jobLinkAssembler.toModel(service.getQuantumApplicationJobs(name), JobDto.class), HttpStatus.OK);
    }

    /**
     * This REST-Endpoints deletes a specific Quantum-Application using it's unique name.
     *
     * @param name
     * @return
     */
    @Transactional
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteQuantumApplication(@PathVariable String name) {
        service.deleteQuantumApplication(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
