package de.unistuttgart.iaas.messaging.quantumservice.utils;

import de.unistuttgart.iaas.messaging.quantumservice.model.dto.event.EventTriggerDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.event.ExecutionResultEventTriggerDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.dto.event.QueueSizeEventTriggerDto;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.EventTrigger;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.ExecutionResultEventTrigger;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.event.QueueSizeEventTrigger;
import org.modelmapper.ModelMapper;

public class ModelMapperUtils {

    public static final ModelMapper mapper = initModelMapper();

    public static <D, T> D convert(final T entity, Class<D> outClass) {
        return mapper.map(entity, outClass);
    }

    private static void initializeConverters(ModelMapper mapper) {
        mapper.createTypeMap(QueueSizeEventTrigger.class, EventTriggerDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), QueueSizeEventTriggerDto.class));
        mapper.createTypeMap(QueueSizeEventTriggerDto.class, EventTrigger.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), QueueSizeEventTrigger.class));
        mapper.createTypeMap(ExecutionResultEventTrigger.class, EventTriggerDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), ExecutionResultEventTriggerDto.class));
        mapper.createTypeMap(ExecutionResultEventTriggerDto.class, EventTrigger.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), ExecutionResultEventTrigger.class));
    }

    private static ModelMapper initModelMapper() {
        ModelMapper mapper = new ModelMapper();
        initializeConverters(mapper);
        return mapper;
    }
}
