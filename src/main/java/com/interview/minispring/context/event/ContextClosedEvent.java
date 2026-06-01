package com.interview.minispring.context.event;

import com.interview.minispring.context.ApplicationEvent;

public class ContextClosedEvent extends ApplicationEvent {
    public ContextClosedEvent(Object source) {
        super(source);
    }
}
