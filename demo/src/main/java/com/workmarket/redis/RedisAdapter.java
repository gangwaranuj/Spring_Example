package com.workmarket.redis;

import com.google.common.base.Optional;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisAdapter {

	Optional<Object> get(String key);

	Map<String, String> getMap(String key);

	@SuppressWarnings("unchecked")
	Long addAllToList(String key, List<String> values);

	Long addAllToList(String key, List<String> values , long expiryInSeconds);

	List<Object> getMultiple(List<String> keys);

	Set<Object> getKeys(Object pattern);

	Optional<Object> get(String key, String hashField);

	void set(String key, String value);

	void set(String key, Object value);

	void set(String key, String value, long expiryInSeconds);

	void set(String key, Object value, long expiryInSeconds);

	void set(String key, String hash, String value);

	void set(String key, String hash, String value, long expiryInSeconds);

	void set(String key, Map<String, String> map);

	void set(String key, Map<String, String> map, long expiryInSeconds);

	void setAll(String key, Map<String, String> fieldValueMap);

	void setAll(String key, Map<String, String> fieldValueMap, long expiryInSeconds);

	Set<String> getSet(String key);

	Long addToSet(String key, String value);

	Long addToSet(String key, Set<String> values);

	Long addToSet(String key, Set<String> values, long expiryInSeconds);

	Long removeFromSet(String key, List<String> values);

	boolean setIfAbsent(String key, String value);

	boolean setIfAbsent(String key, String value, long expiryInSeconds);

	void delete(String key);

	Map<Object, Object> getAllForHash(String key);

	List<String> getList(String key);

	Optional<String> rightPopAndLeftPush (String key, String key2);

	Long addToList(String key, String value);

	Long addToList(String uploadKey, String value, long expiryInSeconds);

	/**
	 *
	 * @param key
	 * @param amount
	 * @return return value of counter after increment; -1 if error encountered
	 */
	long increment(String key, int amount);

	/**
	 *
	 * @param key
	 * @param amount
	 * @param expiryInSeconds
	 * @return return value of counter after increment; -1 if error encountered
	 */
	long increment(String key, int amount, long expiryInSeconds);

	void decrement(String s);
}
