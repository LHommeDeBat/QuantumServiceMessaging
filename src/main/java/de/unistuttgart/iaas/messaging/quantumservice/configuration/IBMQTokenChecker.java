package de.unistuttgart.iaas.messaging.quantumservice.configuration;

import java.time.ZonedDateTime;
import java.util.Objects;

import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.AccessToken;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.ApiToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * This class is an aspect that is used for all methods ot the IBMQClient.
 */
@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class IBMQTokenChecker {

    private final IBMQProperties ibmqProperties;
    private final RestTemplate restTemplate;

    /**
     * This method is executed once before any method within the IBMQClient. It checks the freshness of the current
     * Access-Token. If necessary, it uses the provided Api-Token to retrieve a new Api-Token and store it in the
     * IBMQProperties.
     */
    @Before("execution(* de.unistuttgart.iaas.messaging.quantumservice.api.IBMQClient.*(..))")
    public void checkIbmqTokens() {
        ZonedDateTime now = ZonedDateTime.now();
        // If no Access-Token available or not fresh
        if (Objects.isNull(ibmqProperties.getAccessToken()) || now.isAfter(ibmqProperties.getTokenExpiry())) {
            if (Objects.isNull(ibmqProperties.getApiHost())) {
                throw new RuntimeException("No IBMQ API-Token provided. Please provide a valid API-Token for IBMQ!");
            }
            // Get Access-Token using provided Api-Token
            ResponseEntity<AccessToken> response = restTemplate.postForEntity(ibmqProperties.getApiHost() + "/users/loginWithToken", new HttpEntity<>(new ApiToken(ibmqProperties.getApiToken())), AccessToken.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully retrieved IBMQ access token = {}", response.getBody().getId());
                // Store Access-Token and it's estimated TTL
                ibmqProperties.setAccessToken(response.getBody().getId());
                ibmqProperties.setTokenExpiry(now.plusMinutes(15));
            }
        }
    }
}
