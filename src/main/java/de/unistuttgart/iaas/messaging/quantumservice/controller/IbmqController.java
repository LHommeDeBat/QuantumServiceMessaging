package de.unistuttgart.iaas.messaging.quantumservice.controller;

import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.service.IbmqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class represents the represents the REST-Controller of the IBMQ-Jobs. It handles all incoming REST-Requests
 * that involve contacting the IBMQ-API.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "ibmq")
@RequiredArgsConstructor
public class IbmqController {

    private final IbmqService service;

    /**
     * This REST-Endpoint returns all available IBMQ-Devices.
     *
     * @return ibmqDevices
     */
    @Transactional
    @GetMapping("/devices")
    public ResponseEntity<Set<String>> getAvailableIbmqDevices() {
        return new ResponseEntity<>(service.getAvailableIbmqDevices(), HttpStatus.OK);
    }
}
