package de.unistuttgart.iaas.messaging.quantumservice.model.entity.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("QueueSizeTrigger")
public class QueueSizeEventTrigger extends EventTrigger {

    private int queueSize;
}
