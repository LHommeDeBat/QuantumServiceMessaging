package de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTrigger;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface QuantumApplicationRepository extends CrudRepository<QuantumApplication, UUID> {

    Optional<QuantumApplication> findById(UUID id);

    Optional<QuantumApplication> findByName(String id);

    Set<QuantumApplication> findAll();

    Set<QuantumApplication> findByExecutionResultEventTriggerIsNull();

    @Query("SELECT e FROM QuantumApplication qa JOIN qa.eventTriggers e WHERE qa.name = :name")
    Set<EventTrigger> findQuantumApplicationEventTriggers(@Param("name") String name);

    @Query("SELECT j FROM QuantumApplication qa JOIN qa.jobs j WHERE qa.name = :name")
    Set<Job> findQuantumApplicationJobs(@Param("name") String name);

    default Set<QuantumApplication> findAll(boolean noResultEventOnly) {
        if (noResultEventOnly) {
            return findByExecutionResultEventTriggerIsNull();
        }

        return findAll();
    }
}
