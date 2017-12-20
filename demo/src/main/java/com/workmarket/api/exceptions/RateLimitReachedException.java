package com.workmarket.api.exceptions;

public class RateLimitReachedException extends Exception {
    public RateLimitReachedException(String s) {
        super(s);
    }
}
