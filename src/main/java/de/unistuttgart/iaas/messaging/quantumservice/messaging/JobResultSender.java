package de.unistuttgart.iaas.messaging.quantumservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobResultSender {

    private final JmsTemplate jmsTemplate;

    public void sendJobResult(JSONObject result, String destination) {
        log.info("Sending Job-Result...");
        jmsTemplate.convertAndSend(destination, result.toString());
        log.info("Job-Result was sent!");
    }
}
