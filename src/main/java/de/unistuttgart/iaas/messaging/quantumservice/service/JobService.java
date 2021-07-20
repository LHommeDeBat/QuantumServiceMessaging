package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * This is a Service-Class performing operations on Jobs.
 */
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository repository;

    /**
     * This method returns all jobs in a paginated manner with the option to use optional filters.
     *
     * @param statusFilter
     * @param pageable
     * @return jobs
     */
    public Page<Job> findAll(Set<JobStatus> statusFilter, Pageable pageable) {
        return repository.findAll(statusFilter, pageable);
    }

    /**
     * This method returns a specific job using it's unique ID.
     *
     * @param id
     * @return job
     */
    public Job findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Job does not exist!"));
    }
}
