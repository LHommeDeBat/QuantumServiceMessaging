package de.unistuttgart.iaas.messaging.quantumservice.model.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;

public class CustomBeanUtilsBean extends BeanUtilsBean {
    @Override
    public void copyProperty(Object dest, String name, Object value) throws InvocationTargetException, IllegalAccessException {
        if (value == null) {
            return;
        }
        super.copyProperty(dest, name, value);
    }
}
