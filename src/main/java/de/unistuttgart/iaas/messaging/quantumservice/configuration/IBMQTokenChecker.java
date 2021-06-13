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

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class IBMQTokenChecker {

    private final IBMQProperties ibmqProperties;
    private final RestTemplate restTemplate;

    @Before("execution(* de.unistuttgart.iaas.messaging.quantumservice.api.IBMQClient.*(..))")
    public void checkIbmqTokens() {
        ZonedDateTime now = ZonedDateTime.now();
        if (Objects.isNull(ibmqProperties.getAccessToken()) || now.isAfter(ibmqProperties.getTokenExpiry())) {
            if (Objects.isNull(ibmqProperties.getApiHost())) {
                throw new RuntimeException("No IBMQ API-Token provided. Please provide a valid API-Token for IBMQ!");
            }
            ResponseEntity<AccessToken> response = restTemplate.postForEntity(ibmqProperties.getApiHost() + "/users/loginWithToken", new HttpEntity<>(new ApiToken(ibmqProperties.getApiToken())), AccessToken.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully retrieved IBMQ access token = {}", response.getBody().getId());
                ibmqProperties.setAccessToken(response.getBody().getId());
                ibmqProperties.setTokenExpiry(now.plusMinutes(15));
            }
        }
    }
}
