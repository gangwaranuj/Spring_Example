package com.workmarket.redis;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisAdapterImpl implements RedisAdapter {
	private static final Log logger = LogFactory.getLog(RedisAdapterImpl.class);
	private RedisTemplate redisTemplate;

	@Autowired
	public RedisAdapterImpl(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public Optional<Object> get(String key) {
		Assert.hasText(key);
		try {
			return Optional.fromNullable(redisTemplate.opsForValue().get(key));
		} catch (Exception e) {
			logger.error("[redis] Error on fetching from Redis: ", e);
		}
		return Optional.absent();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getMap(String key) {
		try {
			return redisTemplate.opsForHash().entries(key);
		} catch (Exception e) {
			logger.error("[redis] Error on fetching from Redis: ", e);
		}
		return Maps.newHashMap();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getList(String key) {
		try {
			return redisTemplate.opsForList().range(key, 0, -1);
		} catch (Exception e) {
			logger.error("[redis] Error on fetching from Redis: ", e);
		}
		return Lists.newArrayList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<String> rightPopAndLeftPush (String key, String key2) {
		try {
			return Optional.fromNullable((String)redisTemplate.opsForList().rightPopAndLeftPush(key, key2));
		} catch (Exception e) {
			logger.error("[redis] Error on fetching from Redis: ", e);
		}
		return Optional.absent();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Long addToList(String key, String value) {
		try {
			return redisTemplate.opsForList().leftPush(key, value);
		} catch (Exception e) {
			logger.error("[redis] Error on fetching from Redis: ", e);
			return 0L;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Long addToList(String key, String value, long expiryInSeconds) {
		Long size = addToList(key, value);

		Date date = new Date();
		date.setTime(date.getTime() + expiryInSeconds * 1000L);
		redisTemplate.expireAt(key, date);

		return size;
	}


	@Override
	@SuppressWarnings("unchecked")
	public Long addAllToList(String key, List<String> values) {
		Long size = 0L;
		for (int i = values.size()-1; i >= 0 ; i--) {
			size = addToList(key, values.get(i));
		}
		return size;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Long addAllToList(String key, List<String> values , long expiryInSeconds) {
		Long size = 0L;
		for (int i = values.size()-1; i >= 0 ; i--) {
			size = addToList(key, values.get(i));
		}

		Date date = new Date();
		date.setTime(date.getTime() + expiryInSeconds * 1000L);
		redisTemplate.expireAt(key, date);

		return size;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object> getMultiple(List<String> keys) {
		try {
			return redisTemplate.opsForValue().multiGet(keys);
		} catch (Exception e) {
			logger.error("[redis] Error on fetching from Redis: ", e);
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<Object> getKeys(Object pattern) {
		try {
			return (Set<Object>) redisTemplate.keys(pattern);
		} catch (Exception e) {
			logger.error("[redis] Error on fetching from Redis: ", e);
		}
		return Collections.EMPTY_SET;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<Object> get(String key, String hashField) {
		Assert.hasText(key);
		Assert.hasText(hashField);
		try {
			return Optional.fromNullable(redisTemplate.opsForHash().get(key, hashField));
		} catch (Exception e) {
			logger.error("[redis] Error on fetching from Redis: ", e);
		}
		return Optional.absent();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(String key, String value) {
		Assert.hasText(key);
		setValue(key, value);
	}

	@Override
	public void set(String key, Object value) {
		Assert.hasText(key);
		setValue(key, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(String key, String value, long expiryInSeconds) {
		set(key, value);

		Date date = new Date();
		date.setTime(date.getTime() + expiryInSeconds * 1000L);
		try {
			redisTemplate.expireAt(key, date);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(String key, Object value, long expiryInSeconds) {
		Assert.hasText(key);
		setValue(key, value);

		Date date = new Date();
		date.setTime(date.getTime() + expiryInSeconds * 1000L);
		try {
			redisTemplate.expireAt(key, date);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(String key, String hashField, String value) {
		Assert.hasText(key);
		Assert.hasText(hashField);
		try {
			redisTemplate.opsForHash().put(key, hashField, value);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(String key, String hashField, String value, long expiryInSeconds) {
		set(key, hashField, value);
		Date date = new Date();
		date.setTime(date.getTime() + expiryInSeconds * 1000L);
		redisTemplate.expireAt(key, date);
	}

	@SuppressWarnings("unchecked")
	public void setAll(String key, Map<String, String> fieldValueMap, long expiryInSeconds) {
		setAll(key, fieldValueMap);
		Date date = new Date();
		date.setTime(date.getTime() + expiryInSeconds * 1000L);

		try {
			redisTemplate.expireAt(key, date);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setAll(String key, Map<String, String> fieldValueMap) {
		Assert.hasText(key);
		Assert.notNull(fieldValueMap);
		try {
			redisTemplate.opsForHash().putAll(key, fieldValueMap);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getAllForHash(String key) {
		Assert.hasText(key);
		try {
			return redisTemplate.opsForHash().entries(key);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
		return Collections.emptyMap();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(String key, Map<String, String> map, long expiryInSeconds) {
		set(key, map);
		Date date = new Date();
		date.setTime(date.getTime() + expiryInSeconds * 1000L);
		redisTemplate.expireAt(key, date);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(String key, Map<String, String> map) {
		try {
			redisTemplate.delete(key);
			redisTemplate.opsForHash().putAll(key, map);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
	}

	@Override
	public long increment(final String key, final int amount, final long expiryInSeconds) {
		try {
			// Execute the increment and expiration in a redis transaction
			return (long) redisTemplate.execute(new RedisCallback() {
				@Override
				public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
					redisConnection.multi();
					redisConnection.incrBy(key.getBytes(), amount);
					redisConnection.expire(key.getBytes(), expiryInSeconds);
					List<Object> results = redisConnection.exec();
					return results.get(0);
				}
			});
		} catch (Exception e) {
			logger.error("[redis] Error saving to Redis: ", e);
			return -1;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void decrement(String key) {
		try {
			redisTemplate.opsForValue().increment(key, -1);
		} catch (Exception e) {
			logger.error("[redis] Error decrementing in Redis: ", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public long increment(String key, int amount) {
		try {
			return redisTemplate.opsForValue().increment(key, amount);
		} catch (Exception e) {
			logger.error("[redis] Error saving to Redis: ", e);
			return -1;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<String> getSet(String key) {
		Assert.hasText(key);

		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		Set<String> results = Sets.newHashSet();
		try {
			results = setOperations.members(key);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
		return results;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Long addToSet(String key, String value) {
		Assert.hasText(key);
		Assert.notNull(value);

		SetOperations setOperations = redisTemplate.opsForSet();
		Long numberOfValuesAddedToSet = 0L;
		try {
			numberOfValuesAddedToSet = setOperations.add(key, value);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
		return numberOfValuesAddedToSet;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Long addToSet(String key, Set<String> values) {
		Assert.hasText(key);
		Assert.notNull(values);
		Assert.isTrue(!values.contains(null));

		SetOperations setOperations = redisTemplate.opsForSet();
		Long numberOfValuesAddedToSet = 0L;
		try {
			for (String id : values) {
				numberOfValuesAddedToSet += setOperations.add(key, id);
			}
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
		return numberOfValuesAddedToSet;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Long addToSet(String key, Set<String> values, long expiryInSeconds) {
		Long size = addToSet(key, values);
		redisTemplate.expire(key, expiryInSeconds, TimeUnit.SECONDS);
		return size;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean setIfAbsent(String key, String value) {
		try {
			return redisTemplate.opsForValue().setIfAbsent(key, value);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
		return false;
	}

	@Override
	public boolean setIfAbsent(final String key, final String value, final long expiryInSeconds) {
		try {
			return (boolean) redisTemplate.execute(new RedisCallback() {
				@Override
				public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
					redisConnection.multi();
					redisConnection.setNX(key.getBytes(), value.getBytes());
					redisConnection.expire(key.getBytes(), expiryInSeconds);
					List<Object> results = redisConnection.exec();
					return results.get(0);
				}
			});
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}

		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void delete(String key) {
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			logger.error("[redis] Error on deleting from Redis: ", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Long removeFromSet(String key, List<String> values) {
		Assert.hasText(key);
		Assert.notNull(values);
		Assert.isTrue(!values.contains(null));

		SetOperations setOperations = redisTemplate.opsForSet();
		Long numberOfRemovedValues = 0L;
		try {
			for (String value : values) {
				numberOfRemovedValues += setOperations.remove(key, value);
			}
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
		return numberOfRemovedValues;
	}

	@SuppressWarnings("unchecked")
	private void setValue(String key, Object value) {
		try {
			redisTemplate.opsForValue().set(key, value);
		} catch (Exception e) {
			logger.error("[redis] Error on saving to Redis: ", e);
		}
	}
}
