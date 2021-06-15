package de.unistuttgart.iaas.messaging.quantumservice.model.entity.job;

import java.time.ZonedDateTime;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Job extends HasId {

    private String ibmqId;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Lob
    @Convert(converter= JobResultConverter.class)
    private JSONObject result;

    private ZonedDateTime creationDate;
    private ZonedDateTime endDate;

    private Boolean success;

    @ManyToOne(fetch = FetchType.LAZY)
    private QuantumApplication quantumApplication;

}
