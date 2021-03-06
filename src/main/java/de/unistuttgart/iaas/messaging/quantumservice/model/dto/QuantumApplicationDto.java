package de.unistuttgart.iaas.messaging.quantumservice.model.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.event.ExecutionResultEventTriggerDto;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "quantumApplications", itemRelation = "quantumApplication")
@Data
public class QuantumApplicationDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "The name can only contain letters and numbers!")
    @NotBlank(message = "The name cannot be blank!")
    private String name;

    private Map<String, QuantumApplicationParameterDto> parameters = new HashMap<>();

    @JsonIgnore
    private ExecutionResultEventTriggerDto executionResultEventTrigger;

    @JsonIgnore
    private String filepath;

    @JsonIgnore
    private String executionFilepath;

    @JsonIgnore
    private boolean executionEnabled;
}
