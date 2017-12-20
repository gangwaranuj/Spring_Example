package com.workmarket.api.internal.service;

import com.google.common.base.Optional;
import com.workmarket.api.exceptions.IncrementException;
import com.workmarket.api.exceptions.ApiRateLimitException;
import com.workmarket.redis.RedisAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RateLimiterServiceImplTest {
	@Mock RedisAdapter redisAdapter;
	@InjectMocks RateLimiterServiceImpl service;

	@Mock Optional<Object> result;

	@Before
	public void setup() {
		when(redisAdapter.get(anyString())).thenReturn(result);
		when(result.isPresent()).thenReturn(true);
	}

	@Test(expected = ApiRateLimitException.class)
	public void shouldThrowRateLimitReachedExceptionIfLimitReached() throws IncrementException, ApiRateLimitException {
		when(result.get()).thenReturn("100");
		service.increment("key", 3, 1, 10);
	}

	@Test(expected = ApiRateLimitException.class)
	public void shouldReturnRateLimitReachedIfRateLimitExactlyReached() throws IncrementException, ApiRateLimitException {
		when(result.get()).thenReturn("100");
		service.increment("key", 1, 100, 10);
	}

	@Test
	public void shouldIncrementIfRateLimitNotReached() throws IncrementException, ApiRateLimitException {
		when(result.get()).thenReturn("10");
		service.increment("key", 1, 100, 10);
		verify(redisAdapter).increment("key", 1);
	}

	@Test
	public void shouldIncrementAndExpireIfCountNotPresent() throws IncrementException, ApiRateLimitException {
		when(result.isPresent()).thenReturn(false);
		service.increment("key", 1, 100, 88);
		verify(redisAdapter).increment("key", 1, 88);
	}

	@Test(expected = IncrementException.class)
	public void shouldThrowExceptionIfRedisIsDown() throws IncrementException, ApiRateLimitException {
		when(result.isPresent()).thenReturn(false);
		when(redisAdapter.increment(anyString(), anyInt(), anyLong())).thenReturn(-1L);
		service.increment("key", 1, 100, 88);
	}
}