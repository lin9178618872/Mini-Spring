package com.interview.minispring.beans.factory.config;

import com.interview.minispring.beans.factory.ObjectFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonScope implements Scope {
    private final Map<String, Object> objects = new ConcurrentHashMap<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return objects.computeIfAbsent(name, ignored -> objectFactory.getObject());
    }

    @Override
    public Object remove(String name) {
        return objects.remove(name);
    }
}
