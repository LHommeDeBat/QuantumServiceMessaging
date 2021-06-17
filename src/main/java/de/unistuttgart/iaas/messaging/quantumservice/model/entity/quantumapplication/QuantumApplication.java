package de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.Event;
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

    private String filepath;
    private String executionFilepath;

    @ManyToMany(mappedBy = "quantumApplications")
    private Set<Event> events = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "quantumApplication")
    private Set<Job> jobs = new HashSet<>();
}
