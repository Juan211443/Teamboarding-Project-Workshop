package com.meli_juan.workshop.infrastructure.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import java.beans.PropertyDescriptor;
import java.util.Arrays;

public class PatchUtils {
    public static void copyNonNullProperties(Object source, Object target){
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper wrapper = new BeanWrapperImpl(source);
        return Arrays.stream(wrapper.getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(name -> {
                    Object value = wrapper.getPropertyValue(name);
                    if (value == null) return true;
                    if (value instanceof String s) return s.isBlank();
                    return false;
                })
                .toArray(String[]::new);
    }
}
