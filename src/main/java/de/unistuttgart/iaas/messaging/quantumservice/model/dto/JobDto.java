package de.unistuttgart.iaas.messaging.quantumservice.model.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatus;
import lombok.Data;

@Data
public class JobDto {

    private UUID id;
    private String ibmqId;
    private JobStatus status;
    private String result;
    private ZonedDateTime creationDate;
    private ZonedDateTime endDate;
    private Boolean success;

    @JsonIgnore
    private QuantumApplicationDto quantumApplication;
}
