package com.workmarket.api.exceptions;

public class ApiRateLimitException extends Exception {
    private final long limit;
    private final long expiryInSeconds;

    public ApiRateLimitException(String message, long limit, long expiryInSeconds) {
        super(message);
        this.limit = limit;
        this.expiryInSeconds = expiryInSeconds;
    }

    public long getLimit() {
        return limit;
    }

    public long getExpiryInSeconds() {
        return expiryInSeconds;
    }
}
