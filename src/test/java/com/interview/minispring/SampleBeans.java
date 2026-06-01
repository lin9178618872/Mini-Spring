package com.interview.minispring;

import com.interview.minispring.aop.MethodBeforeAdvice;
import com.interview.minispring.beans.factory.DisposableBean;
import com.interview.minispring.beans.factory.InitializingBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SampleBeans {
    public interface GreetingService {
        String greet(String name);
    }

    public interface UserRepository {
        String findDisplayName(String name);
    }

    public static class MemoryUserRepository implements UserRepository {
        @Override
        public String findDisplayName(String name) {
            return name.toUpperCase();
        }
    }

    public static class DefaultGreetingService implements GreetingService, InitializingBean, DisposableBean {
        private UserRepository repository;
        private String prefix;
        private boolean started;
        private boolean stopped;

        public void setRepository(UserRepository repository) {
            this.repository = repository;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public boolean isStarted() {
            return started;
        }

        public boolean isStopped() {
            return stopped;
        }

        public void start() {
            started = true;
        }

        public void stop() {
            stopped = true;
        }

        @Override
        public void afterPropertiesSet() {
            if (repository == null || prefix == null) {
                throw new IllegalStateException("service is not fully wired");
            }
        }

        @Override
        public void destroy() {
            stopped = true;
        }

        @Override
        public String greet(String name) {
            return prefix + ", " + repository.findDisplayName(name);
        }
    }

    public static class Counter {
        private static int sequence;
        private final int id = ++sequence;

        public int getId() {
            return id;
        }
    }

    public static class PlainCalculator {
        public int add(int left, int right) {
            return left + right;
        }
    }

    public static class TraceBeforeAdvice implements MethodBeforeAdvice {
        static final List<String> calls = new ArrayList<>();

        @Override
        public void before(Method method, Object[] args, Object target) {
            calls.add(method.getName());
        }
    }
}
