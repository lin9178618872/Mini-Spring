package com.interview.minispring.beans.factory.config;

import com.interview.minispring.beans.factory.ObjectFactory;

public interface Scope {
    Object get(String name, ObjectFactory<?> objectFactory);

    Object remove(String name);
}
