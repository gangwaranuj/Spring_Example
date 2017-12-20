package com.workmarket.common.cache;

import com.google.common.base.Optional;

public interface SerializedObjectCache {

	String put(String key, Object value, final long expiryInSeconds);

	Optional<String> get(String key);
}
