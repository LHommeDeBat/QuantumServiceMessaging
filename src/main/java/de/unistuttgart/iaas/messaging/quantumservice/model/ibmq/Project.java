package de.unistuttgart.iaas.messaging.quantumservice.model.ibmq;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {
    private String name;
    private String title;
    private Boolean isDefault;
    private String description;
    private ZonedDateTime creationDate;
    private Boolean deleted;
    private Map<String, Device> devices = new HashMap<>();
}
