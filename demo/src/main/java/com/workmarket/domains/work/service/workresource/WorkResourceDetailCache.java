package com.workmarket.domains.work.service.workresource;

import com.google.common.base.Optional;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;

public interface WorkResourceDetailCache {

	Optional<WorkResourceDetailPagination> get(long workId, WorkResourceDetailPagination pagination);

	void set(long workId, WorkResourceDetailPagination pagination);

	void evict(long workId);
}
