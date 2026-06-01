package com.interview.minispring.core.convert.support;

import com.interview.minispring.core.TinySpringException;

public class SimpleTypeConverter {
    public Object convert(Object value, Class<?> targetType) {
        if (value == null || targetType.isInstance(value)) {
            return value;
        }
        if (!(value instanceof String text)) {
            return value;
        }
        if (targetType == String.class) {
            return text;
        }
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(text);
        }
        if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(text);
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(text);
        }
        if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(text);
        }
        if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(text);
        }
        throw new TinySpringException("Unsupported conversion from String to " + targetType.getName());
    }
}
