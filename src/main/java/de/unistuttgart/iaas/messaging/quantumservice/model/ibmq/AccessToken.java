package de.unistuttgart.iaas.messaging.quantumservice.model.ibmq;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class AccessToken {
    private String id;
    private int ttl;
    private ZonedDateTime created;
    private String userId;
}

