package de.unistuttgart.iaas.messaging.quantumservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.QuantumApplicationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class QuantumApplicationDtoConverter implements Converter<String, QuantumApplicationDto> {

    private final ObjectMapper objectMapper;
    @Override
    public QuantumApplicationDto convert(String s) {
        try {
            QuantumApplicationDto dto = objectMapper.readValue(s, QuantumApplicationDto.class);
            dto.setFilepath("actions/" + dto.getName() + ".py");
            dto.setExecutionFilepath("actions/execution-" + dto.getName() + ".py");
            return dto;
        } catch (Exception e) {
            log.error("Could not convert JSON-String '{}' to DTO", s);
            return null;
        }
    }
}
