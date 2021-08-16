package de.unistuttgart.iaas.messaging.quantumservice.model.dto;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatusDetails;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "jobs", itemRelation = "job")
@Data
public class JobDto {

    private UUID id;
    private String ibmqId;
    private JobStatus status;
    private Map<JobStatus, JobStatusDetails> statusDetails = new HashMap<>();
    private String device;
    private String result;
    private ZonedDateTime scriptExecutionDate;
    private ZonedDateTime creationDate;
    private ZonedDateTime endDate;
    private Boolean success;

    private Map<String, String> usedParameters = new HashMap<>();

    @JsonIgnore
    private QuantumApplicationDto quantumApplication;
}
