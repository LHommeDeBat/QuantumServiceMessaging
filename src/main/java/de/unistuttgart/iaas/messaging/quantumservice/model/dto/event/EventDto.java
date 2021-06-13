package de.unistuttgart.iaas.messaging.quantumservice.model.dto.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventType;
import lombok.Data;

@Data
public class EventDto {

    private UUID id;
    private String name;
    private EventType type;
    private Map<String, Integer> additionalProperties = new HashMap<>();
}
