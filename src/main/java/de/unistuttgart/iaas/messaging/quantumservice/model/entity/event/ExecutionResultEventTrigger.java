package de.unistuttgart.iaas.messaging.quantumservice.model.entity.event;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("ExecutionResultTrigger")
public class ExecutionResultEventTrigger extends EventTrigger {

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "quantum_application_id")
    private QuantumApplication executedApplication;
}
