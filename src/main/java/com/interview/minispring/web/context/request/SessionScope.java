package com.interview.minispring.web.context.request;

import com.interview.minispring.beans.factory.ObjectFactory;
import com.interview.minispring.beans.factory.config.Scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionScope implements Scope {
    private final Map<String, Map<String, Object>> sessions = new ConcurrentHashMap<>();
    private SessionIdResolver sessionIdResolver = () -> "default";

    public void setSessionIdResolver(SessionIdResolver sessionIdResolver) {
        this.sessionIdResolver = sessionIdResolver;
    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return sessions
                .computeIfAbsent(sessionIdResolver.getSessionId(), ignored -> new ConcurrentHashMap<>())
                .computeIfAbsent(name, ignored -> objectFactory.getObject());
    }

    @Override
    public Object remove(String name) {
        Map<String, Object> scopedObjects = sessions.get(sessionIdResolver.getSessionId());
        return scopedObjects == null ? null : scopedObjects.remove(name);
    }

    public interface SessionIdResolver {
        String getSessionId();
    }
}
