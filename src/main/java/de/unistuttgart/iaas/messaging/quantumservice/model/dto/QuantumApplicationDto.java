package de.unistuttgart.iaas.messaging.quantumservice.model.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.ParameterType;
import lombok.Data;

@Data
public class QuantumApplicationDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "The name can only contain letters and numbers!")
    @NotBlank(message = "The name cannot be blank!")
    private String name;

    private Map<String, ParameterType> parameters = new HashMap<>();

    @JsonIgnore
    private String filepath;

    @JsonIgnore
    private String executionFilepath;

    @JsonIgnore
    private boolean executionEnabled;
}
