package com.interview.minispring.core.convert;

public interface ConversionService {
    boolean canConvert(Class<?> sourceType, Class<?> targetType);

    Object convert(Object source, Class<?> targetType);
}
