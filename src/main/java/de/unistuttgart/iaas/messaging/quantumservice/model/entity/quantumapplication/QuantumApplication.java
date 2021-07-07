package de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTrigger;
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

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Map<String, ParameterType> parameters = new HashMap<>();

    private String filepath;
    private String executionFilepath;
    private boolean executionEnabled;

    @ManyToMany(cascade = CascadeType.MERGE, mappedBy = "quantumApplications")
    private Set<EventTrigger> eventTriggers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "quantumApplication")
    private Set<Job> jobs = new HashSet<>();
}
