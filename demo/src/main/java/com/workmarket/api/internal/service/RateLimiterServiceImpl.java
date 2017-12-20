package com.workmarket.api.internal.service;

import com.google.common.base.Optional;
import com.workmarket.api.exceptions.IncrementException;
import com.workmarket.api.exceptions.ApiRateLimitException;
import com.workmarket.redis.RedisAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {
	@Autowired RedisAdapter redisAdapter;

    @Override
    public long increment(String key, int amount, long limit, long expiryInSeconds) throws IncrementException, ApiRateLimitException {
        Optional<Object> result = redisAdapter.get(key);
        long value;

        if (result.isPresent()) {
            long currentCount = Long.parseLong((String)result.get());
            if (currentCount + amount > limit) {
                throw new ApiRateLimitException("Rate limit reached", limit, expiryInSeconds);
            } else {
                value = redisAdapter.increment(key, amount);
            }
        } else {
            value = redisAdapter.increment(key, amount, expiryInSeconds);
        }

        if (value == -1) { // error was encountered in redis
            throw new IncrementException("There was an error processing the requested transaction.");
        }

        return value;
    }
}
