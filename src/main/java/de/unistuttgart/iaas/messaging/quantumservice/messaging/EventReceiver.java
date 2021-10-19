package de.unistuttgart.iaas.messaging.quantumservice.messaging;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventType;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import de.unistuttgart.iaas.messaging.quantumservice.service.EventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class represents a JMS Event-Driven Consumer.
 */
@RequiredArgsConstructor
@Slf4j
public class EventReceiver implements MessageListener {

    private final EventProcessor eventProcessor;

    @Override
    public void onMessage(Message message) {
        log.info("Received Message from Queue");
        IBMQEventPayload eventPayload = new IBMQEventPayload();

        try {
            // Generate IBMQEventPayload object from a MapMessage
            if (message instanceof MapMessage) {
                MapMessage mapMessage = (MapMessage) message;

                eventPayload.setEventType(EventType.valueOf(mapMessage.getString("type")));
                eventPayload.setDevice(mapMessage.getString("device"));

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
