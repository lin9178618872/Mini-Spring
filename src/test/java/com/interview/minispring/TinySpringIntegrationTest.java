package com.interview.minispring;

import com.interview.minispring.SampleBeans.Counter;
import com.interview.minispring.SampleBeans.GreetingService;
import com.interview.minispring.SampleBeans.PlainCalculator;
import com.interview.minispring.SampleBeans.TraceBeforeAdvice;
import com.interview.minispring.aop.support.DefaultAdvisor;
import com.interview.minispring.aop.support.NameMatchMethodPointcut;
import com.interview.minispring.aop.framework.ProxyFactory;
import com.interview.minispring.context.support.TinyApplicationContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TinySpringIntegrationTest {
    @Test
    void loadsXmlInjectsDependenciesAndAppliesJdkAop() {
        try (TinyApplicationContext context = new TinyApplicationContext("classpath:tiny-spring.xml")) {
            GreetingService service = context.getBean("greetingService", GreetingService.class);

            assertEquals("Hello, ADA", service.greet("Ada"));
            assertTrue(TraceBeforeAdvice.calls.contains("greet"));
        }
    }

    @Test
    void createsPrototypeInstancesIndependently() {
        try (TinyApplicationContext context = new TinyApplicationContext("classpath:tiny-spring.xml")) {
            Counter first = context.getBean("prototypeCounter", Counter.class);
            Counter second = context.getBean("prototypeCounter", Counter.class);

            assertNotEquals(first.getId(), second.getId());
        }
    }

    @Test
    void supportsCglibProxyForClassWithoutInterfaces() {
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("add");
        DefaultAdvisor advisor = new DefaultAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(new TraceBeforeAdvice());

        ProxyFactory factory = new ProxyFactory(new PlainCalculator(), List.of(advisor));
        factory.setProxyTargetClass(true);
        PlainCalculator calculator = (PlainCalculator) factory.getProxy();

        assertEquals(7, calculator.add(3, 4));
        assertTrue(TraceBeforeAdvice.calls.contains("add"));
    }
}
