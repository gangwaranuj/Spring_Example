package com.workmarket.api.v2.worker.service;

import com.workmarket.service.search.work.WorkSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service interacts with existing mobile "v1" API monolith controllers to obtain user funds related data and make
 * funds related processing calls. Basically a wrapper for the UGLY part of our V2 implementation. In time, this should
 * give way to service classes that call on microservices for this type of work.
 */
@Service
public class SearchService {

	@Autowired private WorkSearchService workSearchService;

	public void reindexWork(final Long assignmentId) {

		workSearchService.reindexWorkAsynchronous(assignmentId);
	}
}
