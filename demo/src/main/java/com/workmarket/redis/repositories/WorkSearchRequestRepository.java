package com.workmarket.redis.repositories;

import com.google.common.base.Optional;
import com.workmarket.search.request.work.WorkSearchRequest;

public interface WorkSearchRequestRepository {
	public Optional<WorkSearchRequest> get(Long userId);
	public void set(Long userId,WorkSearchRequest request);
}
