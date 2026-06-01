package com.interview.minispring.beans.factory.config;

public interface BeanPostProcessor {
    default Object beforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object afterInitialization(Object bean, String beanName) {
        return bean;
    }
}
