package com.interview.minispring.beans.factory.support;

import com.interview.minispring.beans.factory.BeanFactory;
import com.interview.minispring.beans.factory.BeanFactoryAware;
import com.interview.minispring.beans.factory.BeanNameAware;
import com.interview.minispring.beans.factory.DisposableBean;
import com.interview.minispring.beans.factory.InitializingBean;
import com.interview.minispring.beans.factory.ObjectFactory;
import com.interview.minispring.beans.factory.config.BeanDefinition;
import com.interview.minispring.beans.factory.config.BeanPostProcessor;
import com.interview.minispring.beans.factory.config.BeanReference;
import com.interview.minispring.beans.factory.config.PropertyValue;
import com.interview.minispring.core.convert.support.SimpleTypeConverter;
import com.interview.minispring.core.TinySpringException;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory implements BeanFactory {
    private final Map<String, BeanDefinition> definitions = new LinkedHashMap<>();
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();
    private final Map<String, DisposableBean> disposableBeans = new LinkedHashMap<>();
    private final List<BeanPostProcessor> postProcessors = new ArrayList<>();
    private final SimpleTypeConverter typeConverter = new SimpleTypeConverter();
    private final Set<String> currentlyCreating = ConcurrentHashMap.newKeySet();

    public void registerBeanDefinition(String name, BeanDefinition definition) {
        definitions.put(name, definition);
    }

    public BeanDefinition getBeanDefinition(String name) {
        BeanDefinition definition = definitions.get(name);
        if (definition == null) {
            throw new TinySpringException("No bean named '" + name + "'");
        }
        return definition;
    }

    public Map<String, BeanDefinition> getBeanDefinitions() {
        return definitions;
    }

    public void addBeanPostProcessor(BeanPostProcessor processor) {
        postProcessors.add(processor);
    }

    @Override
    public Object getBean(String name) {
        Object existing = getSingleton(name);
        if (existing != null) {
            return existing;
        }
        BeanDefinition definition = getBeanDefinition(name);
        if (definition.isSingleton()) {
            synchronized (singletonObjects) {
                Object singleton = singletonObjects.get(name);
                if (singleton == null) {
                    singleton = createBean(name, definition);
                    singletonObjects.put(name, singleton);
                }
                return singleton;
            }
        }
        return createBean(name, definition);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return requiredType.cast(getBean(name));
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        Map<String, T> matches = getBeansOfType(requiredType);
        if (matches.size() != 1) {
            throw new TinySpringException("Expected exactly one bean of type " + requiredType.getName() + " but found " + matches.size());
        }
        return matches.values().iterator().next();
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> result = new LinkedHashMap<>();
        for (Map.Entry<String, BeanDefinition> entry : definitions.entrySet()) {
            if (type.isAssignableFrom(entry.getValue().getBeanClass())) {
                result.put(entry.getKey(), type.cast(getBean(entry.getKey())));
            }
        }
        return result;
    }

    public void preInstantiateSingletons() {
        for (Map.Entry<String, BeanDefinition> entry : definitions.entrySet()) {
            if (entry.getValue().isSingleton() && !entry.getValue().isLazyInit()) {
                getBean(entry.getKey());
            }
        }
    }

    public void destroySingletons() {
        List<String> names = new ArrayList<>(disposableBeans.keySet());
        for (int i = names.size() - 1; i >= 0; i--) {
            disposableBeans.get(names.get(i)).destroy();
        }
        singletonObjects.clear();
        earlySingletonObjects.clear();
        singletonFactories.clear();
        disposableBeans.clear();
    }

    private Object getSingleton(String beanName) {
        Object singleton = singletonObjects.get(beanName);
        if (singleton == null && currentlyCreating.contains(beanName)) {
            singleton = earlySingletonObjects.get(beanName);
            if (singleton == null) {
                ObjectFactory<?> factory = singletonFactories.get(beanName);
                if (factory != null) {
                    singleton = factory.getObject();
                    earlySingletonObjects.put(beanName, singleton);
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singleton;
    }

    private Object createBean(String beanName, BeanDefinition definition) {
        try {
            currentlyCreating.add(beanName);
            Object bean = definition.getBeanClass().getDeclaredConstructor().newInstance();
            if (definition.isSingleton()) {
                Object exposed = bean;
                singletonFactories.put(beanName, () -> exposed);
            }
            injectProperties(bean, definition);
            Object initialized = initializeBean(beanName, bean, definition);
            registerDestroyCallback(beanName, initialized, definition);
            return initialized;
        } catch (Exception ex) {
            throw new TinySpringException("Failed to create bean '" + beanName + "'", ex);
        } finally {
            currentlyCreating.remove(beanName);
            singletonFactories.remove(beanName);
            earlySingletonObjects.remove(beanName);
        }
    }

    private void injectProperties(Object bean, BeanDefinition definition) {
        for (PropertyValue propertyValue : definition.getPropertyValues()) {
            Object value = propertyValue.value();
            if (value instanceof BeanReference reference) {
                value = getBean(reference.name());
            }
            writeProperty(bean, propertyValue.name(), value);
        }
    }

    private void writeProperty(Object bean, String name, Object value) {
        String setterName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        for (Method method : bean.getClass().getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                try {
                    method.invoke(bean, typeConverter.convert(value, method.getParameterTypes()[0]));
                    return;
                } catch (Exception ex) {
                    throw new TinySpringException("Could not set property '" + name + "' by setter", ex);
                }
            }
        }
        try {
            Field field = findField(bean.getClass(), name);
            field.setAccessible(true);
            field.set(bean, typeConverter.convert(value, field.getType()));
        } catch (Exception ex) {
            throw new TinySpringException("Could not set property '" + name + "' on " + bean.getClass().getName(), ex);
        }
    }

    private Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    private Object initializeBean(String beanName, Object bean, BeanDefinition definition) throws Exception {
        if (bean instanceof BeanNameAware aware) {
            aware.setBeanName(beanName);
        }
        if (bean instanceof BeanFactoryAware aware) {
            aware.setBeanFactory(this);
        }
        Object current = bean;
        for (BeanPostProcessor processor : postProcessors) {
            current = processor.beforeInitialization(current, beanName);
        }
        if (current instanceof InitializingBean initializingBean) {
            initializingBean.afterPropertiesSet();
        }
        invokeCustomMethod(current, definition.getInitMethod());
        for (BeanPostProcessor processor : postProcessors) {
            current = processor.afterInitialization(current, beanName);
        }
        return current;
    }

    private void registerDestroyCallback(String beanName, Object bean, BeanDefinition definition) {
        if (!definition.isSingleton()) {
            return;
        }
        if (bean instanceof DisposableBean disposableBean) {
            disposableBeans.put(beanName, disposableBean);
            return;
        }
        if (definition.getDestroyMethod() != null && !definition.getDestroyMethod().isBlank()) {
            disposableBeans.put(beanName, () -> invokeCustomMethod(bean, definition.getDestroyMethod()));
        }
    }

    private void invokeCustomMethod(Object bean, String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return;
        }
        try {
            Method method = bean.getClass().getMethod(methodName);
            method.invoke(bean);
        } catch (Exception ex) {
            throw new TinySpringException("Could not invoke lifecycle method '" + methodName + "' on " + Introspector.decapitalize(bean.getClass().getSimpleName()), ex);
        }
    }
}
