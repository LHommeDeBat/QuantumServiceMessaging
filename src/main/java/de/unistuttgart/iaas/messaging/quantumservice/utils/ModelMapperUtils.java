package de.unistuttgart.iaas.messaging.quantumservice.utils;

import org.modelmapper.ModelMapper;

public class ModelMapperUtils {

    public static final ModelMapper mapper = initModelMapper();

    public static <D, T> D convert(final T entity, Class<D> outClass) {
        return mapper.map(entity, outClass);
    }

    private static ModelMapper initModelMapper() {
        return new ModelMapper();
    }
}
