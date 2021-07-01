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

@Component
@RequiredArgsConstructor
@Slf4j
public class IBMQClient {

    private final IBMQProperties ibmqProperties;
    private final RestTemplate restTemplate;

    public List<Hub> getNetworks() {
        return Arrays.asList(restTemplate.getForEntity(addTokenToUri("/Network"), Hub[].class).getBody());
    }

    public IBMQJob getJob(String hub, String group, String project, String jobId) {
        String path = "/Network/" + hub + "/Groups/" + group + "/Projects/" + project + "/Jobs/" + jobId + "/v/1";
        return restTemplate.getForEntity(addTokenToUri(path), IBMQJob.class).getBody();
    }

    public JSONObject getJobResult(String hub, String group, String project, String jobId) {
        String path = "/Network/" + hub + "/Groups/" + group + "/Projects/" + project + "/Jobs/" + jobId + "/resultDownloadUrl";
        JobDownloadUrl downloadUrl = restTemplate.getForEntity(addTokenToUri(path), JobDownloadUrl.class).getBody();
        try {
            return new JSONObject(IOUtils.toString(new URL(downloadUrl.getUrl()), Charset.forName("UTF-8")));
        } catch (IOException exception) {
            return new JSONObject().put("url", downloadUrl.getUrl());
        }
    }

    private String addTokenToUri(String path) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ibmqProperties.getApiHost() + path)
                .queryParam("access_token", ibmqProperties.getAccessToken());
        return builder.toUriString();
    }
}
