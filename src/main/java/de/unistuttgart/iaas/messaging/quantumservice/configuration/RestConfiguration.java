package de.unistuttgart.iaas.messaging.quantumservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * This class generates some beans necessary to perform REST-Communication.
 */
@Configuration
public class RestConfiguration {

    /**
     * This class creates a Spring-Bean of a RestTemplate.
     *
     * @return restTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
