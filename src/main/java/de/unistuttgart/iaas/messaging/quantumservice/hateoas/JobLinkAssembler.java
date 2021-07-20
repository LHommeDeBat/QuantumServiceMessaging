package de.unistuttgart.iaas.messaging.quantumservice.hateoas;

import java.util.UUID;

import de.unistuttgart.iaas.messaging.quantumservice.controller.JobController;
import de.unistuttgart.iaas.messaging.quantumservice.controller.QuantumApplicationController;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.JobDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class is used for adding HATEOAS-Links to Job DTOs.
 */
@Component
public class JobLinkAssembler extends GenericLinkAssembler<JobDto> {
    @Override
    public void addLinks(EntityModel<JobDto> resource) {
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(JobController.class).getJob(getId(resource))).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(QuantumApplicationController.class).getQuantumApplication(getQuantumApplicationName(resource))).withRel("quantumApplication"));
    }

    private String getQuantumApplicationName(EntityModel<JobDto> resource) {
        return resource.getContent().getQuantumApplication().getName();
    }

    private UUID getId(EntityModel<JobDto> resource) {
        return resource.getContent().getId();
    }
}
