package com.interview.minispring.aop.framework;

import java.lang.reflect.Method;

public interface MethodInvocation {
    Method getMethod();

    Object[] getArguments();

    Object getTarget();

    Object proceed() throws Throwable;
}
