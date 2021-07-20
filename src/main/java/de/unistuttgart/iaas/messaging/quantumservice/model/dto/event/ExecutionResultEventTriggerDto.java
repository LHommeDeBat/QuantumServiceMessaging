package de.unistuttgart.iaas.messaging.quantumservice.model.dto.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.QuantumApplicationDto;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "eventTriggers", itemRelation = "eventTrigger")
@JsonTypeName("EXECUTION_RESULT")
@Data
public class ExecutionResultEventTriggerDto extends EventTriggerDto {

    private QuantumApplicationDto executedApplication;
}
