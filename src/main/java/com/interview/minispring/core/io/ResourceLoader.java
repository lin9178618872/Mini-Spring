package com.interview.minispring.core.io;

import com.interview.minispring.core.TinySpringException;

import java.io.FileInputStream;
import java.io.InputStream;

public class ResourceLoader {
    public InputStream open(String location) {
        try {
            if (location.startsWith("classpath:")) {
                String path = location.substring("classpath:".length());
                InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                if (stream == null) {
                    throw new TinySpringException("Classpath resource not found: " + path);
                }
                return stream;
            }
            return new FileInputStream(location);
        } catch (Exception ex) {
            throw new TinySpringException("Could not open resource: " + location, ex);
        }
    }
}
