package com.interview.minispring.core.env;

public class StandardEnvironment implements Environment {
    @Override
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        return value == null ? defaultValue : value;
    }
}
