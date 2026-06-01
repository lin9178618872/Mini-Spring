package com.interview.minispring.aop.framework;

import com.interview.minispring.aop.MethodBeforeAdvice;

public class BeforeAdviceInterceptor implements MethodInterceptor {
    private final MethodBeforeAdvice advice;

    public BeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        advice.before(invocation.getMethod(), invocation.getArguments(), invocation.getTarget());
        return invocation.proceed();
    }
}
