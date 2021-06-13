package de.unistuttgart.iaas.messaging.quantumservice.model.dto;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuantumApplicationDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "The name can only contain letters and numbers!")
    @NotBlank(message = "The name cannot be blank!")
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String filepath;
}
