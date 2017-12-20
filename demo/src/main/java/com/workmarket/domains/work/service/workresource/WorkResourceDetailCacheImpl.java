package com.workmarket.domains.work.service.workresource;

import com.google.common.base.Optional;
import com.google.gson.reflect.TypeToken;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.JsonSerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class WorkResourceDetailCacheImpl implements WorkResourceDetailCache {

	private static final long EXPIRATION_TIME_IN_SECONDS = 21600; // 6 Hrs

	@Autowired private RedisAdapter redisAdapter;
	@Autowired private JsonSerializationService jsonSerializationService;

	@Override
	public Optional<WorkResourceDetailPagination> get(long workId, WorkResourceDetailPagination pagination) {
		if (pagination == null) {
			return Optional.absent();
		}
		String redisKey = RedisFilters.getWorkResourcesDetailDataKey(workId, pagination);
		Optional<Object> result = redisAdapter.get(redisKey);

		if (result.isPresent()) {
			WorkResourceDetailPagination detailPagination = jsonSerializationService.fromJson(
				result.get().toString(),
				new TypeToken<WorkResourceDetailPagination>() {}.getType()
			);
			return Optional.fromNullable(detailPagination);
		}

		return Optional.absent();
	}

	@Override
	public void set(long workId, WorkResourceDetailPagination pagination) {
		if (pagination != null && isNotEmpty(pagination.getResults())) {
			String redisKey = RedisFilters.getWorkResourcesDetailDataKey(workId, pagination);
			redisAdapter.set(redisKey, jsonSerializationService.toJson(pagination), EXPIRATION_TIME_IN_SECONDS);
		} else {
			evict(workId);
		}

	}

	@Override
	public void evict(long workId) {
		for (Object key : redisAdapter.getKeys(RedisFilters.getWorkResourcesDetailKeyPattern(workId))) {
			redisAdapter.delete((String) key);
		}
	}
}
