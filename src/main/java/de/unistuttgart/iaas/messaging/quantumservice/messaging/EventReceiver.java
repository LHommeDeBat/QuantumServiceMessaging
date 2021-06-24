package de.unistuttgart.iaas.messaging.quantumservice.messaging;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventType;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
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
        IBMQEventPayload eventPayload = new IBMQEventPayload();

        if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;

            eventPayload.setEventType(EventType.valueOf(mapMessage.getString("type")));
            eventPayload.setDevice(mapMessage.getString("device"));

            eventPayload.setReplyTo(jmsDestinationToString(mapMessage.getJMSReplyTo()));

            if (mapMessage.itemExists("queueSize")) {
                eventPayload.addAdditionalProperty("queueSize", mapMessage.getInt("queueSize"));
            }
        }

        eventProcessor.processEvent(eventPayload);
    }

    private String jmsDestinationToString(Destination destination) {
        String[] replyToSplit = destination.toString().split("/");
        return replyToSplit[replyToSplit.length - 1];
    }
}
