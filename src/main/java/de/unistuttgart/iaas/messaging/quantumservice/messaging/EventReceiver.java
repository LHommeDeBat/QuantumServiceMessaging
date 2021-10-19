package de.unistuttgart.iaas.messaging.quantumservice.messaging;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.iaas.messaging.quantumservice.configuration.IBMQProperties;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventType;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import de.unistuttgart.iaas.messaging.quantumservice.service.EventProcessor;
import de.unistuttgart.iaas.messaging.quantumservice.service.EventTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class represents a JMS Event-Driven Consumer.
 */
@RequiredArgsConstructor
@Slf4j
public class EventReceiver implements MessageListener {

    private final EventProcessor eventProcessor;
    private final IBMQProperties ibmqProperties;
    private final EventTriggerService eventTriggerService;
    private final ObjectMapper objectMapper;

    /**
     * This method takes a JMS-Destination object and converts it to a string that can be stored in a database.
     *
     * @param destination
     * @return destinationAsString
     */
    private String jmsDestinationToString(Destination destination) {
        String[] replyToSplit = destination.toString().split("/");
        return replyToSplit[replyToSplit.length - 1];
    }

    @Override
    public void onMessage(Message message) {
        log.info("Received Message from Topic");
        IBMQEventPayload eventPayload = new IBMQEventPayload();

        try {
            // Generate IBMQEventPayload object from a MapMessage
            if (message instanceof MapMessage) {
                MapMessage mapMessage = (MapMessage) message;

                eventPayload.setEventType(EventType.valueOf(mapMessage.getString("type")));
                eventPayload.setDevice(mapMessage.getString("device"));

                eventPayload.setReplyTo(jmsDestinationToString(mapMessage.getJMSReplyTo()));

                if (mapMessage.itemExists("queueSize")) {
                    eventPayload.addAdditionalProperty("queueSize", mapMessage.getInt("queueSize"));
                }

                eventProcessor.processEvent(eventPayload);
            }
        } catch (JMSException e) {
            log.error("Could not understand message!");
        }
    }
}
