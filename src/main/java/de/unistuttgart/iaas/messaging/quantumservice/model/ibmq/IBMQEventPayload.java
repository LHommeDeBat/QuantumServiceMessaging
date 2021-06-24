package de.unistuttgart.iaas.messaging.quantumservice.model.ibmq;

import java.util.HashMap;
import java.util.Map;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IBMQEventPayload {
    private EventType eventType;
    private String device;
    private String replyTo;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public void addAdditionalProperty(String key, Object value) {
        additionalProperties.put(key, value);
    }
}
