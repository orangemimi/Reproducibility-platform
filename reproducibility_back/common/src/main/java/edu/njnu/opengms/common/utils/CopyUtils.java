package edu.njnu.opengms.common.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName edu.njnu.opengms.common.utils.CopyUtils
 * @Description 对Spring提供的BeanUtils.copyProperties进行封装，使其srcClass的空字段不会影响targetClass字段
 * @Author sun_liber
 * @Date 2019/5/30
 * @Version 1.0.0
 */
public class CopyUtils {
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            Object srcValue = beanWrapper.getPropertyValue(propertyName);
            if (srcValue == null) {
                emptyNames.add(propertyName);
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

}