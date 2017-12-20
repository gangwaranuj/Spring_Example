package com.workmarket.redis.repositories;

import com.google.common.base.Optional;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.service.business.JsonSerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkSearchRequestRepositoryImpl implements WorkSearchRequestRepository {

	@Autowired RedisAdapter redisAdapter;
	@Autowired JsonSerializationService jsonSerializationService;
	private static final long EXPIRATION_TIME_IN_SECONDS = 3600;

	@Override
	public Optional<WorkSearchRequest> get(Long userId) {
		Optional<Object> resultOpt = redisAdapter.get(RedisFilters.workSearchKeyFor(userId));
		if (resultOpt.isPresent()) {
			return Optional.of(jsonSerializationService.fromJson((String) resultOpt.get(), WorkSearchRequest.class));
		}
		return Optional.absent();
	}

	@Override
	public void set(Long userId, WorkSearchRequest request) {
		redisAdapter.set(RedisFilters.workSearchKeyFor(userId), jsonSerializationService.toJson(request), EXPIRATION_TIME_IN_SECONDS);
	}
}
