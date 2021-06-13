package de.unistuttgart.iaas.messaging.quantumservice.utils;

import java.lang.reflect.InvocationTargetException;

import de.unistuttgart.iaas.messaging.quantumservice.model.exception.BeanUtilsBeanException;
import de.unistuttgart.iaas.messaging.quantumservice.model.utils.CustomBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;

public class BeanUtils {

    private static final BeanUtilsBean beanUtilsBean = new CustomBeanUtilsBean();

    public static <I, O> void copyProperties(I source, O target) {
        try {
            beanUtilsBean.copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanUtilsBeanException(e.getMessage(), e);
        }
    }
}
