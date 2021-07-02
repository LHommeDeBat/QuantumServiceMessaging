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

    @Query("SELECT qa FROM EventTrigger e JOIN e.additionalProperties p JOIN e.quantumApplications qa WHERE e.eventType = 'QUEUE_SIZE' AND KEY(p) = 'queueSize' AND p >= :queueSize AND qa.executionEnabled = TRUE")
    Set<QuantumApplication> findApplicationByQueueSizeEvent(@Param("queueSize") Integer queueSize);

    default Set<QuantumApplication> findByEventData(IBMQEventPayload eventPayload) {
        if (eventPayload.getEventType() == EventType.QUEUE_SIZE) {
            return findApplicationByQueueSizeEvent((Integer) eventPayload.getAdditionalProperties().get("queueSize"));
        }
        return new HashSet<>();
    }
}
