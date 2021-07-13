package de.unistuttgart.iaas.messaging.quantumservice.model.dto.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@JsonTypeName("QUEUE_SIZE")
@Data
public class QueueSizeEventTriggerDto extends EventTriggerDto {

    private int queueSize;
}
