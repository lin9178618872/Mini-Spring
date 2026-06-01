Mini Spring

This is an independently structured lightweight Spring-inspired framework for demo use.

It implements the same feature goals described in the resume bullet:

- Lightweight IoC container with dependency injection.
- Bean lifecycle management: aware callbacks, init callbacks, destroy callbacks.
- Reflection-driven component creation and property injection.
- Singleton caching, prototype scope, lazy initialization.
- XML configuration with value/ref injection and `${...}` placeholders.
- Application context refresh flow and synchronous application events.
- AOP with JDK dynamic proxies and CGLIB subclass proxies.
- Advice chain with before and after-returning advice.

## Quick Start

```java
try (TinyApplicationContext context = new TinyApplicationContext("classpath:tiny-spring.xml")) {
    GreetingService service = context.getBean(GreetingService.class);
    service.greet("Ada");
}
```

## XML Example

```xml
<beans properties="classpath:app.properties">
    <bean id="repository" class="example.MemoryUserRepository"/>
    <bean id="service" class="example.DefaultGreetingService" init-method="start" destroy-method="stop">
        <property name="repository" ref="repository"/>
        <property name="prefix" value="${greeting.prefix}"/>
    </bean>
</beans>
```

## Design Notes

The code is intentionally not a file-by-file rewrite of another project. It uses a different package layout and a smaller set of focused abstractions:

- `aop/aspectj`: AspectJ-style pointcut extension point.
- `aop/framework`: proxy factory, invocation chain, auto proxy creator.
- `aop/support`: reusable pointcut/advisor implementations.
- `beans/factory`: BeanFactory API and lifecycle aware callbacks.
- `beans/factory/config`: Bean metadata, post processors, scopes.
- `beans/factory/support`: default BeanFactory implementation and singleton cache.
- `beans/factory/xml`: XML BeanDefinition reader.
- `context/event`: refresh/close events and synchronous event publisher.
- `context/support`: XML application context refresh orchestration.
- `core/convert`: conversion service abstractions.
- `core/env`: environment abstraction.
- `core/io`: resource loading.
- `web/context/request`: request and session scope examples.

## Project Structure

```text
mini-spring
|-- aop
|   |-- aspectj
|   |-- framework
|   `-- support
|-- beans
|   `-- factory
|       |-- config
|       |-- support
|       `-- xml
|-- context
|   |-- event
|   `-- support
|-- core
|   |-- convert
|   |   |-- converter
|   |   `-- support
|   |-- env
|   `-- io
|-- util
`-- web
    `-- context
        `-- request
```

## Build

```bash
mvn test
```

If CGLIB is used on modern JDKs, Maven Surefire opens `java.lang` for test execution in `pom.xml`.
