package de.unistuttgart.iaas.messaging.quantumservice.model.dto;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.ParameterType;
import lombok.Data;

@Data
public class QuantumApplicationParameterDto {
    private ParameterType type;
    private String defaultValue;
}
