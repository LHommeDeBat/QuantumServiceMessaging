package de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface QuantumApplicationRepository extends CrudRepository<QuantumApplication, UUID> {
    Optional<QuantumApplication> findById(UUID id);
    Optional<QuantumApplication> findByName(String id);
    Set<QuantumApplication> findAll();
}
