package com.interview.minispring.context.event;

import com.interview.minispring.context.ApplicationEvent;

public class ContextRefreshedEvent extends ApplicationEvent {
    public ContextRefreshedEvent(Object source) {
        super(source);
    }
}
