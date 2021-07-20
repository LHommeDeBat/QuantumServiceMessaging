package de.unistuttgart.iaas.messaging.quantumservice.configuration;

import java.util.UUID;

import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * This converter is used to convert incoming UUID-Strings into a UUID-Object
 */
@Configuration
public class UuidConverter implements Converter<String, UUID> {
    @Override
    public UUID convert(@NonNull String s) {
        return UUID.fromString(s);
    }
}
