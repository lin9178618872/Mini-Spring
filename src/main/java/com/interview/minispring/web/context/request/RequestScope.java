package com.interview.minispring.web.context.request;

import com.interview.minispring.beans.factory.ObjectFactory;
import com.interview.minispring.beans.factory.config.Scope;

import java.util.HashMap;
import java.util.Map;

public class RequestScope implements Scope {
    private final ThreadLocal<Map<String, Object>> requestObjects = ThreadLocal.withInitial(HashMap::new);

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return requestObjects.get().computeIfAbsent(name, ignored -> objectFactory.getObject());
    }

    @Override
    public Object remove(String name) {
        return requestObjects.get().remove(name);
    }

    public void clear() {
        requestObjects.remove();
    }
}
