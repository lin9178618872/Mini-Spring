package com.interview.minispring.aop;

import com.interview.minispring.aop.support.Pointcut;

public interface Advisor {
    Pointcut getPointcut();

    Object getAdvice();
}
