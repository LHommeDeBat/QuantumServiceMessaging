package de.unistuttgart.iaas.messaging.quantumservice.hateoas;

import de.unistuttgart.iaas.messaging.quantumservice.controller.QuantumApplicationController;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.QuantumApplicationDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class QuantumApplicationLinkAssembler extends GenericLinkAssembler<QuantumApplicationDto> {
    @Override
    public void addLinks(EntityModel<QuantumApplicationDto> resource) {
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(QuantumApplicationController.class).getQuantumApplication(getName(resource))).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(QuantumApplicationController.class).downloadQuantumApplicationScript(getName(resource))).withRel("script"));
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(QuantumApplicationController.class).invokeQuantumApplication(getName(resource), null)).withRel("invoke"));
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(QuantumApplicationController.class).getQuantumApplicationEvents(getName(resource))).withRel("events"));
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(QuantumApplicationController.class).getQuantumApplicationJobs(getName(resource))).withRel("jobs"));
    }

    private String getName(EntityModel<QuantumApplicationDto> resource) {
        return resource.getContent().getName();
    }
}
