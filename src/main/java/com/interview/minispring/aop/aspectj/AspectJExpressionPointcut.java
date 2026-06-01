package com.interview.minispring.aop.aspectj;

import com.interview.minispring.aop.support.Pointcut;

import java.lang.reflect.Method;

public class AspectJExpressionPointcut implements Pointcut {
    private String expression = "*";

    public void setExpression(String expression) {
        this.expression = expression == null || expression.isBlank() ? "*" : expression;
    }

    @Override
    public boolean matches(Class<?> targetClass, Method method) {
        if ("*".equals(expression)) {
            return true;
        }
        return targetClass.getName().contains(expression) || method.getName().contains(expression);
    }
}
