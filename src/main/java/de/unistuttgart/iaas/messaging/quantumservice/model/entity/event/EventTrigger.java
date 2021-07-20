package de.unistuttgart.iaas.messaging.quantumservice.model.entity.event;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EventTrigger extends HasId {

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @ManyToMany(cascade = CascadeType.MERGE)
    private Set<QuantumApplication> quantumApplications = new HashSet<>();
}
