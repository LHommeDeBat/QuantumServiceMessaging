package de.unistuttgart.iaas.messaging.quantumservice.messaging;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * This class represents a JMS-MessageSender.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JobResultSender {

    private final JmsTemplate jmsTemplate;

    /**
     * This method uses JMS to send the status-reached events of jobs to the defined Reply-To-Address (destination).
     *
     * @param job Job that is checked for status changes
     */
    public void sendJobStatusReachedEvent(Job job) {
        log.info("Sending Job-Result...");
        for (JobStatus status : JobStatus.values()) {
            if (job.getStatusDetails().get(status) != null && !job.getStatusDetails().get(status).isStatusEventSent()) {
                jmsTemplate.convertAndSend(job.getReplyTo(), createEventAsJson(job, status).toString());
                job.getStatusDetails().get(status).setStatusEventSent(true);
                log.info("Job-Status={} was reached for application={} and event was sent to destination={}!", status, job.getQuantumApplication().getName(), job.getReplyTo());
            }
        }
    }

    private JSONObject createEventAsJson(Job job, JobStatus status) {
        JSONObject event = new JSONObject();
        event.put("executedApplication", job.getQuantumApplication().getName());
        event.put("status", status.toString());
        event.put("statusReached", job.getStatusDetails().get(status).getStatusReached().toString());
        event.put("usedDevice", job.getDevice());
        if (status == JobStatus.COMPLETED) {
            event.put("executionSuccessful", job.getSuccess());
            if (job.getSuccess()) {
                event.put("executionResult", job.getResult());
            }
        }
        return event;
    }
}
