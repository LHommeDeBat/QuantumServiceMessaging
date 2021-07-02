package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository repository;

    public Page<Job> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Job findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Job does not exist!"));
    }
}
