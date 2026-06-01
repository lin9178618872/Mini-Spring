package com.interview.minispring.aop.framework;

public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
