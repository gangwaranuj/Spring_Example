package com.workmarket.service.search;

import com.google.common.collect.Maps;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SearchPreferencesServiceImpl implements SearchPreferencesService {

	@Autowired private RedisAdapter redisAdapter;
	private final long TWO_WEEKS_IN_SECONDS = TimeUnit.DAYS.toSeconds(14);

	@Override
	public Map<String, String> get(Long userId) {
		return redisAdapter.getMap(getKey(userId));
	}

	@Override
	public void set(Long userId, Map<String, String> map) {
		redisAdapter.set(getKey(userId), Maps.newHashMap(map), TWO_WEEKS_IN_SECONDS);
	}

	private String getKey(Long userId) {
		return RedisFilters.searchPreferencesKeysFor(userId);
	}
}

