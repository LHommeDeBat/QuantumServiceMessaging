package de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTrigger;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.ExecutionResultEventTrigger;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class QuantumApplication extends HasId {

    @Column(unique = true)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Map<String, QuantumApplicationParameter> parameters = new HashMap<>();

    private String filepath;
    private String executionFilepath;
    private boolean executionEnabled;

    @ManyToMany(cascade = CascadeType.MERGE, mappedBy = "quantumApplications")
    private Set<EventTrigger> eventTriggers = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "executedApplication")
    private ExecutionResultEventTrigger executionResultEventTrigger;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "quantumApplication")
    private Set<Job> jobs = new HashSet<>();
}
