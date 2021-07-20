package de.unistuttgart.iaas.messaging.quantumservice.controller;

import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.messaging.quantumservice.hateoas.JobLinkAssembler;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.JobDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.messaging.quantumservice.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class represents the represents the REST-Controller of the Jobs. It handles all incoming REST-Requests
 * for the Jobs.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobLinkAssembler linkAssembler;
    private final JobService service;

    /**
     * This REST-Endpoint returns all existing Jobs in a paginated manner. Optional parameters allow further filtering
     * of the result.
     *
     * @param statusFilter
     * @param pageable
     * @return jobs
     */
    @Transactional
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<JobDto>>> getJobs(@RequestParam(required = false) Set<JobStatus> statusFilter, Pageable pageable) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findAll(statusFilter, pageable), JobDto.class), HttpStatus.OK);
    }

    /**
     * This REST-Endpoint returns a specific job using it's unique ID.
     *
     * @param id
     * @return job
     */
    @Transactional
    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<JobDto>> getJob(@PathVariable UUID id) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findById(id), JobDto.class), HttpStatus.OK);
    }
}
