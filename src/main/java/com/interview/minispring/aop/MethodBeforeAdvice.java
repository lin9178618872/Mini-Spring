package com.interview.minispring.aop;

import java.lang.reflect.Method;

public interface MethodBeforeAdvice {
    void before(Method method, Object[] args, Object target) throws Throwable;
}
