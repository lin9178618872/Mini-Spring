package com.interview.minispring.core.convert.converter;

public interface Converter<S, T> {
    T convert(S source);
}
