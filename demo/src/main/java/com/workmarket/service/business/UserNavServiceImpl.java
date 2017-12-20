package com.workmarket.service.business;

import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserNavServiceImpl implements UserNavService {

	@Autowired RedisAdapter redisAdapter;
	public static final long TWO_WEEKS_IN_SECONDS = TimeUnit.DAYS.toSeconds(14);

	public Map<String, String> get(Long userId) {
		return redisAdapter.getMap(getKey(userId));
	}

	public void set(Long userId, Map<String, String> map) {
		redisAdapter.set(getKey(userId), map, TWO_WEEKS_IN_SECONDS);
	}

	private String getKey(Long userId) {
		return RedisFilters.navPreferencesKeyFor(userId);
	}
}
