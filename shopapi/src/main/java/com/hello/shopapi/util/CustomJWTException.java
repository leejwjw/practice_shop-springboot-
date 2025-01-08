package com.hello.shopapi.util;

public class CustomJWTException extends RuntimeException {
    public CustomJWTException(String msg) {
        super(msg);
    }
}
