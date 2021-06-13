package de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface QuantumApplicationRepository extends CrudRepository<QuantumApplication, UUID> {

    Optional<QuantumApplication> findById(UUID id);

    Optional<QuantumApplication> findByName(String id);

    Set<QuantumApplication> findAll();

    @Query("SELECT e FROM QuantumApplication qa JOIN qa.events e WHERE qa.name = :name")
    Set<Event> findQuantumApplicationEvents(@Param("name") String name);
}
