package de.unistuttgart.iaas.messaging.quantumservice.model.entity.job;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, UUID> {

    Optional<Job> findById(UUID id);
    Page<Job> findAll(Pageable pageable);
    Page<Job> findByStatusIn(Set<JobStatus> statusFilter, Pageable pageable);
    Set<Job> findByStatusNot(JobStatus status);

    default Set<Job> findRunningJobs() {
        return findByStatusNot(JobStatus.COMPLETED);
    }

    default Page<Job> findAll(Set<JobStatus> statusFilter, Pageable pageable) {
        if (Objects.isNull(statusFilter) || statusFilter.isEmpty()) {
            return findAll(pageable);
        }
        return findByStatusIn(statusFilter, pageable);
    }
}
