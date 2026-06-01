package com.interview.minispring.context.support;

import com.interview.minispring.beans.factory.BeanFactory;
import com.interview.minispring.beans.factory.config.BeanFactoryPostProcessor;
import com.interview.minispring.beans.factory.config.BeanPostProcessor;
import com.interview.minispring.beans.factory.support.DefaultBeanFactory;
import com.interview.minispring.beans.factory.xml.XmlBeanDefinitionReader;
import com.interview.minispring.context.ApplicationEvent;
import com.interview.minispring.context.ApplicationListener;
import com.interview.minispring.context.event.ContextClosedEvent;
import com.interview.minispring.context.event.ContextRefreshedEvent;
import com.interview.minispring.context.event.EventPublisher;

import java.util.Map;

public class TinyApplicationContext implements BeanFactory, AutoCloseable {
    private final DefaultBeanFactory beanFactory = new DefaultBeanFactory();
    private final EventPublisher eventPublisher = new EventPublisher();
    private final String configLocation;
    private long startupDate;

    public TinyApplicationContext(String configLocation) {
        this.configLocation = configLocation;
        refresh();
    }

    public void refresh() {
        startupDate = System.currentTimeMillis();
        new XmlBeanDefinitionReader(beanFactory).loadBeanDefinitions(configLocation);
        invokeBeanFactoryPostProcessors();
        registerBeanPostProcessors();
        registerListeners();
        beanFactory.preInstantiateSingletons();
        eventPublisher.publish(new ContextRefreshedEvent(this));
    }

    private void invokeBeanFactoryPostProcessors() {
        for (BeanFactoryPostProcessor processor : beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values()) {
            processor.postProcessBeanFactory(beanFactory);
        }
    }

    private void registerBeanPostProcessors() {
        for (BeanPostProcessor processor : beanFactory.getBeansOfType(BeanPostProcessor.class).values()) {
            beanFactory.addBeanPostProcessor(processor);
        }
    }

    private void registerListeners() {
        for (ApplicationListener<?> listener : beanFactory.getBeansOfType(ApplicationListener.class).values()) {
            eventPublisher.addListener(listener);
        }
    }

    public long getStartupDate() {
        return startupDate;
    }

    public void publishEvent(ApplicationEvent event) {
        eventPublisher.publish(event);
    }

    @Override
    public Object getBean(String name) {
        return beanFactory.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return beanFactory.getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        return beanFactory.getBeansOfType(type);
    }

    @Override
    public void close() {
        eventPublisher.publish(new ContextClosedEvent(this));
        beanFactory.destroySingletons();
    }
}
