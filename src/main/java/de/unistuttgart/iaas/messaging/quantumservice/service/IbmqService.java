package de.unistuttgart.iaas.messaging.quantumservice.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.api.IBMQClient;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.Device;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.Group;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.Hub;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IbmqService {

    private final IBMQClient ibmqClient;

    public Set<String> getAvailableIbmqDevices() {
        Set<String> availableDevices = new HashSet<>();
        List<Hub> hubs = ibmqClient.getNetworks();

        for (Hub hub: hubs) {
            for (Group group: hub.getGroups().values()) {
                for (Project project: group.getProjects().values()) {
                    for (Device device: project.getDevices().values()) {
                        availableDevices.add(device.getName());
                    }
                }
            }
        }

        return availableDevices;
    }
}
