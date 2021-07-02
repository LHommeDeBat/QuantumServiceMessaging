package de.unistuttgart.iaas.messaging.quantumservice.model.entity.job;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, UUID> {

    Optional<Job> findById(UUID id);
    Page<Job> findAll(Pageable pageable);
    Set<Job> findByStatusNot(JobStatus status);

    default Set<Job> findRunningJobs() {
        return findByStatusNot(JobStatus.COMPLETED);
    }
}
