package com.interview.minispring.context;

public interface ApplicationListener<E extends ApplicationEvent> {
    void onApplicationEvent(E event);
}
