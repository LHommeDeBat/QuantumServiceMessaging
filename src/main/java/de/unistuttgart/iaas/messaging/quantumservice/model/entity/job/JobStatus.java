package de.unistuttgart.iaas.messaging.quantumservice.model.entity.job;

public enum JobStatus {
    CREATING,
    CREATED,
    VALIDATING,
    VALIDATED,
    QUEUED,
    RUNNING,
    COMPLETED,
    FAILED
}
