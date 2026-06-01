package com.interview.minispring.beans.factory.config;

import java.util.ArrayList;
import java.util.List;

public class BeanDefinition {
    public static final String SINGLETON = "singleton";
    public static final String PROTOTYPE = "prototype";

    private final Class<?> beanClass;
    private String scope = SINGLETON;
    private boolean lazyInit;
    private String initMethod;
    private String destroyMethod;
    private final List<PropertyValue> propertyValues = new ArrayList<>();

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope == null || scope.isBlank() ? SINGLETON : scope;
    }

    public boolean isSingleton() {
        return SINGLETON.equals(scope);
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public String getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    public void addPropertyValue(String name, Object value) {
        propertyValues.add(new PropertyValue(name, value));
    }
}
