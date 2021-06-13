package de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.Event;
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

    @ManyToMany(mappedBy = "quantumApplications")
    private Set<Event> events = new HashSet<>();
}
