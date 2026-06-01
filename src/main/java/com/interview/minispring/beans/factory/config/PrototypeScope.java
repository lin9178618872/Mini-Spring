package com.interview.minispring.beans.factory.config;

import com.interview.minispring.beans.factory.ObjectFactory;

public class PrototypeScope implements Scope {
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return objectFactory.getObject();
    }

    @Override
    public Object remove(String name) {
        return null;
    }
}
