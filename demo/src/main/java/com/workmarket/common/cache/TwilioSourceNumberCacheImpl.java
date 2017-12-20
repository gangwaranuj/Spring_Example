package com.workmarket.common.cache;

import com.google.common.base.Optional;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwilioSourceNumberCacheImpl implements TwilioSourceNumberCache {

	@Autowired private RedisAdapter redisAdapter;

	private static final long FIVE_DAYS_IN_SECONDS = 432000l;

	@Override
	public Optional<String> getSourceNumber() {
		String key = RedisFilters.twilioSourceNumbersKey();
		return redisAdapter.rightPopAndLeftPush(key, key);
	}

	@Override
	public void putSourceNumbers(List<String> sourceNumbers) {
		redisAdapter.addAllToList(RedisFilters.twilioSourceNumbersKey(), sourceNumbers, FIVE_DAYS_IN_SECONDS);
	}
}
