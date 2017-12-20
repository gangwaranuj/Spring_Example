package com.workmarket.common.cache;

import com.google.common.base.Optional;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.JsonSerializationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SerializedJsonObjectCacheTest {

	@Mock RedisAdapter redisAdapter;
	@Mock JsonSerializationService jsonSerializationService;
	@InjectMocks SerializedJsonObjectCache cache;

	String validKey = "fk", missingKey = "mk";
	Object cachedValue = new Object();
	private static final String SERIALIZED_JSON = "cerealized valyew";
	private static final long EXPIRY = 1;

	@Before
	public void setUp() throws Exception {
		when(jsonSerializationService.toJson(cachedValue)).thenReturn(SERIALIZED_JSON);
		when(redisAdapter.get(validKey)).thenReturn(Optional.<Object>of("foundValue"));
		when(redisAdapter.get(missingKey)).thenReturn(Optional.absent());
		when(redisAdapter.get(null)).thenReturn(Optional.absent());
	}

	@Test
	public void get_NullKey_ResultAbsent() throws Exception {
		assertFalse(cache.get(null).isPresent());
	}

	@Test
	public void get_KeyIsPresent_ResultPresent() throws Exception {
		assertTrue(cache.get(validKey).isPresent());
	}

	@Test
	public void get_KeyNotPresent_ResultAbsent() throws Exception {
		assertFalse(cache.get(missingKey).isPresent());
	}

	@Test
	public void put_ValidKey_ResultSerialized() throws Exception {
		assertTrue(SERIALIZED_JSON.equals(cache.put(validKey, cachedValue, EXPIRY)));
	}

	@Test
	public void put_ValidKey_SavedToRedis() throws Exception {
		cache.put(validKey, cachedValue, EXPIRY);
		verify(redisAdapter, times(1)).set(validKey, SERIALIZED_JSON, EXPIRY);
	}

}
