package de.unistuttgart.iaas.messaging.quantumservice.model.entity.event;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.HasId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Event extends HasId {

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @ElementCollection
    private Map<String, Integer> additionalProperties = new HashMap<>();
}
