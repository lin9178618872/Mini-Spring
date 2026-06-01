package com.interview.minispring.context.event;

import com.interview.minispring.context.ApplicationEvent;
import com.interview.minispring.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;

public class EventPublisher {
    private final List<ApplicationListener<?>> listeners = new ArrayList<>();

    public void addListener(ApplicationListener<?> listener) {
        listeners.add(listener);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void publish(ApplicationEvent event) {
        for (ApplicationListener listener : listeners) {
            listener.onApplicationEvent(event);
        }
    }
}
