package de.unistuttgart.iaas.messaging.quantumservice.model.dto.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.QuantumApplicationDto;
import lombok.Data;

@JsonTypeName("EXECUTION_RESULT")
@Data
public class ExecutionResultEventTriggerDto extends EventTriggerDto {

    private QuantumApplicationDto executedApplication;
}
