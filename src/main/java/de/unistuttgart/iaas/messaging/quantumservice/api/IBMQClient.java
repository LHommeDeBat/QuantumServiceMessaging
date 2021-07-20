package de.unistuttgart.iaas.messaging.quantumservice.api;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import de.unistuttgart.iaas.messaging.quantumservice.configuration.IBMQProperties;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.Hub;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQJob;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.JobDownloadUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This class contains methods for communicating with the IBMQ-Rest-API.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IBMQClient {

    private final IBMQProperties ibmqProperties;
    private final RestTemplate restTemplate;

    /**
     * This method returns all available IBMQ-Hubs
     *
     * @return availableIbmqHubs
     */
    public List<Hub> getNetworks() {
        return Arrays.asList(restTemplate.getForEntity(addTokenToUri("/Network"), Hub[].class).getBody());
    }

    /**
     *  This method returns a IBMQ-Job that is stored within a specific project of a specific group in a specific hub.
     *
     * @param hub
     * @param group
     * @param project
     * @param jobId
     * @return job
     */
    public IBMQJob getJob(String hub, String group, String project, String jobId) {
        String path = "/Network/" + hub + "/Groups/" + group + "/Projects/" + project + "/Jobs/" + jobId + "/v/1";
        return restTemplate.getForEntity(addTokenToUri(path), IBMQJob.class).getBody();
    }

    /**
     * This method returns the result of a completed job or 404 Not Found instead.
     *
     * @param hub
     * @param group
     * @param project
     * @param jobId
     * @return jobResult
     */
    public JSONObject getJobResult(String hub, String group, String project, String jobId) {
        String path = "/Network/" + hub + "/Groups/" + group + "/Projects/" + project + "/Jobs/" + jobId + "/resultDownloadUrl";
        // Retrieve Job-JSON-Url via IBMQ-Endpoint
        JobDownloadUrl downloadUrl = restTemplate.getForEntity(addTokenToUri(path), JobDownloadUrl.class).getBody();
        try {
            // Get JSON of result
            return new JSONObject(IOUtils.toString(new URL(downloadUrl.getUrl()), Charset.forName("UTF-8")));
        } catch (IOException exception) {
            return new JSONObject().put("url", downloadUrl.getUrl());
        }
    }

    /**
     * This method adds to Api-Token to the Request-URI
     *
     * @param path
     * @return uriWithToken
     */
    private String addTokenToUri(String path) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ibmqProperties.getApiHost() + path)
                .queryParam("access_token", ibmqProperties.getAccessToken());
        return builder.toUriString();
    }
}
