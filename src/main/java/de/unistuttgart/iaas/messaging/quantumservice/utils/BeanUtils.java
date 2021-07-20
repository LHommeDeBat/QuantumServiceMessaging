package de.unistuttgart.iaas.messaging.quantumservice.utils;

import java.lang.reflect.InvocationTargetException;

import de.unistuttgart.iaas.messaging.quantumservice.model.exception.BeanUtilsBeanException;
import de.unistuttgart.iaas.messaging.quantumservice.model.utils.CustomBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * This Utility-Class is used to copy fields from one object into the other.
 * (Usually used when fields of an objects are updated and need to be copied to the database object)
 */
public class BeanUtils {

    private static final BeanUtilsBean beanUtilsBean = new CustomBeanUtilsBean();

    /**
     * This method copies the fields from the source object to the target object
     *
     * @param source
     * @param target
     * @param <I>
     * @param <O>
     */
    public static <I, O> void copyProperties(I source, O target) {
        try {
            beanUtilsBean.copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanUtilsBeanException(e.getMessage(), e);
        }
    }
}
