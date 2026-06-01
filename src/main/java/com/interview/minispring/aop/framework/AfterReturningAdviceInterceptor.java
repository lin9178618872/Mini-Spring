package com.interview.minispring.aop.framework;

import com.interview.minispring.aop.AfterReturningAdvice;

public class AfterReturningAdviceInterceptor implements MethodInterceptor {
    private final AfterReturningAdvice advice;

    public AfterReturningAdviceInterceptor(AfterReturningAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = invocation.proceed();
        advice.afterReturning(result, invocation.getMethod(), invocation.getArguments(), invocation.getTarget());
        return result;
    }
}
