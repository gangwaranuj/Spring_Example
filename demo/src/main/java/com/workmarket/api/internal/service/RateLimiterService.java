package com.workmarket.api.internal.service;

import com.workmarket.api.exceptions.IncrementException;
import com.workmarket.api.exceptions.ApiRateLimitException;

public interface RateLimiterService {
    /**
     * Increment a rate limit counter. The increment <code>amount</code> is applied iff
     * adding the increment amount does not exceed the limit.
     *
     * @param key
     * @param amount
     * @param limit
     * @param expiryInSeconds
     * @return Return the new value after incrementation. If incrementation was unsuccesful, the old value
     * is returned without the increment amount; -1 if rate limit reached.
     */
    long increment(String key, int amount, long limit, long expiryInSeconds) throws IncrementException,
																																										ApiRateLimitException;
}
