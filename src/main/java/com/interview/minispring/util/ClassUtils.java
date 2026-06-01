package com.interview.minispring.util;

public final class ClassUtils {
    private ClassUtils() {
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader == null ? ClassUtils.class.getClassLoader() : contextClassLoader;
    }
}
