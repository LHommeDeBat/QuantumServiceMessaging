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

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "ibmq")
@RequiredArgsConstructor
public class IbmqController {

    private final IbmqService service;

    @Transactional
    @GetMapping("/devices")
    public ResponseEntity<Set<String>> getAvailableIbmqDevices() {
        return new ResponseEntity<>(service.getAvailableIbmqDevices(), HttpStatus.OK);
    }
}
