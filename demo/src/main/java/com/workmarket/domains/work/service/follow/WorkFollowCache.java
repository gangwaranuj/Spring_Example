package com.workmarket.domains.work.service.follow;

import com.google.common.base.Optional;
import com.workmarket.service.business.dto.WorkFollowDTO;

import java.util.List;

public interface WorkFollowCache {
	Optional<List<WorkFollowDTO>> get(long workId);

	void set(long workId, List<WorkFollowDTO> followers);

	void evict(long workId);
}
