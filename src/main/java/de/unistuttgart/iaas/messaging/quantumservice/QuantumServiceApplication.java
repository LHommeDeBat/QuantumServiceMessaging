package de.unistuttgart.iaas.messaging.quantumservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This method starts the Spring-Boot-Application using the embedded tomcat.
 */
@EnableScheduling
@SpringBootApplication
public class QuantumServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantumServiceApplication.class, args);
    }
}
