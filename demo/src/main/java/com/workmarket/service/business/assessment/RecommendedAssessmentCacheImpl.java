package com.workmarket.service.business.assessment;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.JsonSerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RecommendedAssessmentCacheImpl implements RecommendedAssessmentCache {

	private static final long A_DAY_IN_SECONDS = TimeUnit.DAYS.toSeconds(1);

	@Autowired private RedisAdapter redisAdapter;
	@Autowired private JsonSerializationService jsonSerializationService;

	@Override
	public Optional<List<Long>> get(long userId) {
		String key = RedisFilters.recommendedAssessmentKeyFor(userId);
		Optional<Object> result = redisAdapter.get(key);
		if (result.isPresent()) {
			List<Long> ids = jsonSerializationService.fromJson(
				result.get().toString(),
				new TypeToken<List<Long>>() {}.getType()
			);
			return Optional.fromNullable(ids);
		}
		return Optional.absent();
	}

	@Override
	public void set(long userId, ManagedAssessmentPagination pagination) {
		redisAdapter.set(
			RedisFilters.recommendedAssessmentKeyFor(userId),
			jsonSerializationService.toJson(pagination.getResultIds()),
			A_DAY_IN_SECONDS
		);
	}
}
