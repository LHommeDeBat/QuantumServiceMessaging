package de.unistuttgart.iaas.messaging.quantumservice.messaging;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventType;
import de.unistuttgart.iaas.messaging.quantumservice.service.EventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventReceiver {

    private final EventProcessor eventProcessor;

    @Transactional
    @JmsListener(destination = "EVENT.TOPIC")
    public void onEvent(Message message) throws JMSException {
        log.info("Received Message from Topic");
        Map<String, Object> additionalProperties = new HashMap<>();

        EventType eventType = null;
        String device = null;

        if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;

            eventType = EventType.valueOf(mapMessage.getString("type"));
            device = mapMessage.getString("device");

            if (eventType == EventType.QUEUE_SIZE) {
                additionalProperties.put("queueSize", mapMessage.getInt("queueSize"));
            }
        }

        eventProcessor.processEvent(eventType, device, additionalProperties);
    }
}
