package com.interview.minispring.aop.support;

import java.lang.reflect.Method;

public class NameMatchMethodPointcut implements Pointcut {
    private String mappedName = "*";

    public void setMappedName(String mappedName) {
        this.mappedName = mappedName;
    }

    @Override
    public boolean matches(Class<?> targetClass, Method method) {
        if ("*".equals(mappedName)) {
            return true;
        }
        if (mappedName.endsWith("*")) {
            return method.getName().startsWith(mappedName.substring(0, mappedName.length() - 1));
        }
        return method.getName().equals(mappedName);
    }
}
