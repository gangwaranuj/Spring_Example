package com.workmarket.service.search.work;

import com.workmarket.search.gen.WorkMessages.FindWorkResponse;
import com.workmarket.search.gen.WorkMessages.FindWorkRequest;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.dto.WorkBundleDTO;

import java.util.Collection;

public interface WorkSearchService {

	void reindexAllWorkByCompanyAsynchronous(long companyId);

	void reindexWorkAsynchronous(Long workId);

	void reindexWorkAsynchronous(Collection<Long> workIds);

	WorkSearchResponse searchAllWorkByUserId(Long userId, WorkSearchRequest request);

	WorkSearchResponse searchAllWorkByCompanyId(Long companyId, WorkSearchRequest request);

	FindWorkResponse findWork(FindWorkRequest request);

	void workBundleUpdateSearchIndex(WorkBundleDTO workBundleDTO);

	void optimize();
}
