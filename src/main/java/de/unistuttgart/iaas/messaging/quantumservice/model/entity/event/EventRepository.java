package de.unistuttgart.iaas.messaging.quantumservice.model.entity.event;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends CrudRepository<Event, UUID> {

    Optional<Event> findById(UUID id);

    Optional<Event> findByName(String name);

    Set<Event> findAll();

    @Query("SELECT qa FROM Event e JOIN e.quantumApplications qa WHERE e.name = :name")
    Set<QuantumApplication> findEventApplications(@Param("name") String name);

    @Query("SELECT e FROM Event e JOIN e.additionalProperties p WHERE e.type = 'QUEUE_SIZE' AND KEY(p) = 'queueSize' AND p <= :queueSize ")
    Set<Event> findQueueSizeEventToTrigger(@Param("queueSize") Integer queueSize);

    default Set<Event> findByEventData(EventType type, Map<String, Object> eventProperties) {
        if (type == EventType.QUEUE_SIZE) {
            return findQueueSizeEventToTrigger((Integer) eventProperties.get("queueSize"));
        }
        return new HashSet<>();
    }
}
