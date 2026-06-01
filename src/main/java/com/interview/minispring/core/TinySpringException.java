package com.interview.minispring.core;

public class TinySpringException extends RuntimeException {
    public TinySpringException(String message) {
        super(message);
    }

    public TinySpringException(String message, Throwable cause) {
        super(message, cause);
    }
}
