package com.interview.minispring.aop.framework;

import com.interview.minispring.aop.Advisor;
import com.interview.minispring.aop.AfterReturningAdvice;
import com.interview.minispring.aop.MethodBeforeAdvice;
import com.interview.minispring.core.TinySpringException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ProxyFactory {
    private final Object target;
    private final List<Advisor> advisors;
    private boolean proxyTargetClass;

    public ProxyFactory(Object target, List<Advisor> advisors) {
        this.target = target;
        this.advisors = advisors;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    public Object getProxy() {
        Class<?> targetClass = target.getClass();
        if (!proxyTargetClass && targetClass.getInterfaces().length > 0) {
            return Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(), new JdkHandler());
        }
        return createCglibProxy(targetClass);
    }

    private Object createCglibProxy(Class<?> targetClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback((net.sf.cglib.proxy.MethodInterceptor) this::interceptWithCglib);
        return enhancer.create();
    }

    private Object interceptWithCglib(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        List<MethodInterceptor> chain = buildInterceptorChain(method);
        if (chain.isEmpty()) {
            return method.invoke(target, args);
        }
        return new ReflectiveMethodInvocation(target, method, args, chain).proceed();
    }

    private class JdkHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            List<MethodInterceptor> chain = buildInterceptorChain(targetMethod);
            if (chain.isEmpty()) {
                return targetMethod.invoke(target, args);
            }
            return new ReflectiveMethodInvocation(target, targetMethod, args, chain).proceed();
        }
    }

    private List<MethodInterceptor> buildInterceptorChain(Method method) {
        List<MethodInterceptor> chain = new ArrayList<>();
        for (Advisor advisor : advisors) {
            if (advisor.getPointcut() != null && advisor.getPointcut().matches(target.getClass(), method)) {
                Object advice = advisor.getAdvice();
                if (advice instanceof MethodBeforeAdvice beforeAdvice) {
                    chain.add(new BeforeAdviceInterceptor(beforeAdvice));
                } else if (advice instanceof AfterReturningAdvice afterReturningAdvice) {
                    chain.add(new AfterReturningAdviceInterceptor(afterReturningAdvice));
                } else {
                    throw new TinySpringException("Unsupported advice type: " + advice.getClass().getName());
                }
            }
        }
        return chain;
    }
}
