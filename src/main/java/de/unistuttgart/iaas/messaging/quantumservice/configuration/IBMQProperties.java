package de.unistuttgart.iaas.messaging.quantumservice.configuration;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ibmq")
@Getter
@Setter
public class IBMQProperties {
    private String apiHost;
    private String apiToken;
    private String accessToken;
    private ZonedDateTime tokenExpiry;
}
