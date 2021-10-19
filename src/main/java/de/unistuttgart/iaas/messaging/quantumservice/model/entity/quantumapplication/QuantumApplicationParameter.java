package de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.HasId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class QuantumApplicationParameter extends HasId {

    @Enumerated(EnumType.STRING)
    private ParameterType type;

    @Lob
    private String defaultValue;
}
