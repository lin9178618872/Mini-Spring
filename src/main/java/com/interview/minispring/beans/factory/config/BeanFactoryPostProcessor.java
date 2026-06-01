package com.interview.minispring.beans.factory.config;

import com.interview.minispring.beans.factory.support.DefaultBeanFactory;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(DefaultBeanFactory beanFactory);
}
