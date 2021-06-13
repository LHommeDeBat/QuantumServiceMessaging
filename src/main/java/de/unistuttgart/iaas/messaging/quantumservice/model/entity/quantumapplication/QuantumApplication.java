package de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.HasId;
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
}
