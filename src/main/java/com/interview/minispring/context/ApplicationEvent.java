package com.interview.minispring.context;

import java.time.Instant;

public abstract class ApplicationEvent {
    private final Object source;
    private final Instant createdAt = Instant.now();

    protected ApplicationEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
