package com.interview.minispring.aop.framework;

import com.interview.minispring.aop.Advisor;
import com.interview.minispring.aop.AfterReturningAdvice;
import com.interview.minispring.aop.MethodBeforeAdvice;
import com.interview.minispring.aop.support.Pointcut;
import com.interview.minispring.beans.factory.BeanFactory;
import com.interview.minispring.beans.factory.BeanFactoryAware;
import com.interview.minispring.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

public class AutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {
    private BeanFactory beanFactory;
    private boolean proxyTargetClass;

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object afterInitialization(Object bean, String beanName) {
        if (bean instanceof Advisor || bean instanceof MethodBeforeAdvice || bean instanceof AfterReturningAdvice || bean instanceof Pointcut) {
            return bean;
        }
        List<Advisor> matched = new ArrayList<>();
        for (Advisor advisor : beanFactory.getBeansOfType(Advisor.class).values()) {
            if (advisor.getPointcut() != null && matchesAnyMethod(bean, advisor)) {
                matched.add(advisor);
            }
        }
        if (matched.isEmpty()) {
            return bean;
        }
        ProxyFactory factory = new ProxyFactory(bean, matched);
        factory.setProxyTargetClass(proxyTargetClass);
        return factory.getProxy();
    }

    private boolean matchesAnyMethod(Object bean, Advisor advisor) {
        for (java.lang.reflect.Method method : bean.getClass().getMethods()) {
            if (advisor.getPointcut().matches(bean.getClass(), method)) {
                return true;
            }
        }
        return false;
    }
}
