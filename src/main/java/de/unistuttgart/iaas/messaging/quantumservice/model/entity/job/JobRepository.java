package de.unistuttgart.iaas.messaging.quantumservice.model.entity.job;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, UUID> {

    Optional<Job> findById(UUID id);
    Set<Job> findAll();
    Set<Job> findByStatusNot(JobStatus status);
    Set<Job> findByQuantumApplicationName(String name);

    default Set<Job> findRunningJobs() {
        return findByStatusNot(JobStatus.COMPLETED);
    }
}
