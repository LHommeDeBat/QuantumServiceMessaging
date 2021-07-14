package de.unistuttgart.iaas.messaging.quantumservice.model.entity.event;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQEventPayload;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EventTriggerRepository extends CrudRepository<EventTrigger, UUID> {

    Optional<EventTrigger> findById(UUID id);

    Optional<EventTrigger> findByName(String name);

    Set<EventTrigger> findAll();

    @Query("SELECT qa FROM EventTrigger e JOIN e.quantumApplications qa WHERE e.name = :name")
    Set<QuantumApplication> findEventApplications(@Param("name") String name);

    @Query("SELECT qa FROM QueueSizeEventTrigger e JOIN e.quantumApplications qa WHERE e.eventType = 'QUEUE_SIZE' AND e.queueSize >= :queueSize AND qa.executionEnabled = TRUE")
    Set<QuantumApplication> findApplicationByQueueSizeEvent(@Param("queueSize") Integer queueSize);

    @Query("SELECT qa FROM ExecutionResultEventTrigger e JOIN e.quantumApplications qa JOIN e.executedApplication ea WHERE e.eventType = 'EXECUTION_RESULT' AND ea.name = :applicationName AND qa.executionEnabled = TRUE")
    Set<QuantumApplication> findApplicationByExecutionResultEvent(@Param("applicationName") String applicationName);

    default Set<QuantumApplication> findByEventData(IBMQEventPayload eventPayload) {
        switch (eventPayload.getEventType()) {
            case QUEUE_SIZE:
                return findApplicationByQueueSizeEvent((Integer) eventPayload.getAdditionalProperties().get("queueSize"));
            case EXECUTION_RESULT:
                return findApplicationByExecutionResultEvent(eventPayload.getAdditionalProperties().get("executedApplication").toString());
            default:
                return new HashSet<>();
        }
    }
}
