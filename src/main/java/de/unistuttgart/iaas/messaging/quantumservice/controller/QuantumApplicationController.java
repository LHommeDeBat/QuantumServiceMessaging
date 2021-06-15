package de.unistuttgart.iaas.messaging.quantumservice.controller;

import de.unistuttgart.iaas.messaging.quantumservice.hateoas.EventLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.hateoas.JobLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.hateoas.QuantumApplicationLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.EventDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.JobDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.QuantumApplicationDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.service.QuantumApplicationService;
import de.unistuttgart.iaas.messaging.quantumservice.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "quantum-applications")
@RequiredArgsConstructor
public class QuantumApplicationController {

    private final QuantumApplicationService service;
    private final QuantumApplicationLinkAssembler linkAssembler;
    private final EventLinkAssembler eventLinkAssembler;
    private final JobLinkAssembler jobLinkAssembler;

    @Transactional
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EntityModel<QuantumApplicationDto>> createQuantumApplication(@RequestParam MultipartFile script, @RequestParam QuantumApplicationDto dto) {
        QuantumApplication createdQuantumApplication = service.createQuantumApplication(script, ModelMapperUtils.convert(dto, QuantumApplication.class));
        return new ResponseEntity<>(linkAssembler.toModel(createdQuantumApplication, QuantumApplicationDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<QuantumApplicationDto>>> getQuantumApplications() {
        return new ResponseEntity<>(linkAssembler.toModel(service.getQuantumApplications(), QuantumApplicationDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/{name}")
    public ResponseEntity<EntityModel<QuantumApplicationDto>> getQuantumApplication(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.getQuantumApplication(name), QuantumApplicationDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/{name}/events")
    public ResponseEntity<CollectionModel<EntityModel<EventDto>>> getQuantumApplicationEvents(@PathVariable String name) {
        return new ResponseEntity<>(eventLinkAssembler.toModel(service.getQuantumApplicationEvents(name), EventDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/{name}/jobs")
    public ResponseEntity<CollectionModel<EntityModel<JobDto>>> getQuantumApplicationJobs(@PathVariable String name) {
        return new ResponseEntity<>(jobLinkAssembler.toModel(service.getQuantumApplicationJobs(name), JobDto.class), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteQuantumApplication(@PathVariable String name) {
        service.deleteQuantumApplication(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
