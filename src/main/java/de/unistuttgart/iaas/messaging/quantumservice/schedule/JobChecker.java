package de.unistuttgart.iaas.messaging.quantumservice.schedule;

import java.util.List;

import de.unistuttgart.iaas.messaging.quantumservice.api.IBMQClient;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.Hub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobChecker {

    private final IBMQClient ibmqClient;

    @Transactional
    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void checkJobStatus() {
        List<Hub> hubs = ibmqClient.getNetworks();
        log.info("#Hubs: " + hubs.size());
    }
}
