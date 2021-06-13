package de.unistuttgart.iaas.messaging.quantumservice.hateoas;

import de.unistuttgart.iaas.messaging.quantumservice.controller.EventController;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.EventDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EventLinkAssembler extends GenericLinkAssembler<EventDto> {
    @Override
    public void addLinks(EntityModel<EventDto> resource) {
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(EventController.class).getEvent(getName(resource))).withSelfRel());
    }

    private String getName(EntityModel<EventDto> resource) {
        return resource.getContent().getName();
    }
}
