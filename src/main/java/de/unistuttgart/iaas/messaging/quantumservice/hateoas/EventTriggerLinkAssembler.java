package de.unistuttgart.iaas.messaging.quantumservice.hateoas;

import de.unistuttgart.iaas.messaging.quantumservice.controller.EventTriggerController;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.EventTriggerDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EventTriggerLinkAssembler extends GenericLinkAssembler<EventTriggerDto> {
    @Override
    public void addLinks(EntityModel<EventTriggerDto> resource) {
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(EventTriggerController.class).getEventTrigger(getName(resource))).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(EventTriggerController.class).getEventTriggerApplications(getName(resource))).withRel("quantumApplications"));
    }

    private String getName(EntityModel<EventTriggerDto> resource) {
        return resource.getContent().getName();
    }
}
