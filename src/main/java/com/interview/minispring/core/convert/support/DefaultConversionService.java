package com.interview.minispring.core.convert.support;

import com.interview.minispring.core.convert.ConversionService;

public class DefaultConversionService implements ConversionService {
    private final SimpleTypeConverter delegate = new SimpleTypeConverter();

    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        return sourceType == String.class || targetType.isAssignableFrom(sourceType);
    }

    @Override
    public Object convert(Object source, Class<?> targetType) {
        return delegate.convert(source, targetType);
    }
}
