package com.interview.minispring.aop.support;

import com.interview.minispring.aop.Advisor;

public class DefaultAdvisor implements Advisor {
    private Pointcut pointcut;
    private Object advice;

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    public void setAdvice(Object advice) {
        this.advice = advice;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Object getAdvice() {
        return advice;
    }
}
