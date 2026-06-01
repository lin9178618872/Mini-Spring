package com.interview.minispring.core.env;

public interface Environment {
    String getProperty(String key);

    String getProperty(String key, String defaultValue);
}
