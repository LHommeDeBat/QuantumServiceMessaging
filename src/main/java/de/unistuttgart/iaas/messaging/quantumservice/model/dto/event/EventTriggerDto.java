package de.unistuttgart.iaas.messaging.quantumservice.model.dto.event;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventType;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "eventTriggers", itemRelation = "eventTrigger")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "eventType", visible = true)
@JsonSubTypes( {@JsonSubTypes.Type(value = ExecutionResultEventTriggerDto.class, name = "EXECUTION_RESULT"), @JsonSubTypes.Type(value = QueueSizeEventTriggerDto.class, name = "QUEUE_SIZE")})
@Data
public class EventTriggerDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "The name can only contain letters and numbers!")
    @NotBlank(message = "The name cannot be blank!")
    private String name;

    @NotNull
    private EventType eventType;
}
