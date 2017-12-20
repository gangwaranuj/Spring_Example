package com.workmarket.domains.work.service.follow;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.dto.WorkFollowDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class WorkFollowCacheImpl implements WorkFollowCache {

	private static final long A_DAY_IN_SECONDS = TimeUnit.DAYS.toSeconds(1);

	@Autowired private RedisAdapter redisAdapter;
	@Autowired private JsonSerializationService jsonSerializationService;

	public Optional<List<WorkFollowDTO>> get(long workId) {
		final String key = RedisFilters.followersKey(workId);
		final Optional<Object> result = redisAdapter.get(key);
		if (result.isPresent()) {
			List<WorkFollowDTO> followers = jsonSerializationService.fromJson(
				result.get().toString(),
				new TypeToken<List<WorkFollowDTO>>() {}.getType()
			);
			return Optional.fromNullable(followers);
		}

		return Optional.absent();
	}

	@Override
	public void set(long workId, List<WorkFollowDTO> followers) {
		final String
			followersKey = RedisFilters.followersKey(workId),
			followersValue = jsonSerializationService.toJson(followers);
		redisAdapter.set(followersKey, followersValue, A_DAY_IN_SECONDS);
	}

	@Override
	public void evict(long workId) {
		redisAdapter.delete(RedisFilters.followersKey(workId));
	}
}
