package com.workmarket.service.business;

import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserDashboardFilterServiceImpl implements UserDashboardFilterService {

	@Autowired private RedisAdapter redisAdapter;
	private final long TWO_WEEKS_IN_SECONDS = TimeUnit.DAYS.toSeconds(14);

	@Override
	public Map<String, String> get(Long userId) {
		return redisAdapter.getMap(getKey(userId));
	}

	@Override
	public void set(Long userId, Map<String, String> map) {
		redisAdapter.set(getKey(userId), map, TWO_WEEKS_IN_SECONDS);
	}

	String getKey(Long userId) {
		return RedisFilters.dashboardFilterKeyFor(userId);
	}
}
