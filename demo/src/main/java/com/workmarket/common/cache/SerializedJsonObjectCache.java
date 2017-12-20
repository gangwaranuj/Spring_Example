package com.workmarket.common.cache;

import com.google.common.base.Optional;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.JsonSerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SerializedJsonObjectCache implements SerializedObjectCache {

	@Autowired RedisAdapter redisAdapter;
	@Autowired JsonSerializationService jsonSerializationService;

	@Override
	public String put(String key, Object value, final long expiryInSeconds) {
		String serializedValue = jsonSerializationService.toJson(value);
		redisAdapter.set(key, serializedValue, expiryInSeconds);
		return serializedValue;
	}

	@Override
	public Optional<String> get(String key) {
		Optional<Object> result = redisAdapter.get(key);
		return (result.isPresent()) ?
			Optional.of((String) result.get()) :
			Optional.<String>absent();
	}
}
