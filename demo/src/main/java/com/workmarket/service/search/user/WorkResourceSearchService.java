package com.workmarket.service.search.user;

import com.workmarket.common.core.RequestContext;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.Worker;
import com.workmarket.service.exception.search.SearchException;

import java.util.List;

public interface WorkResourceSearchService {

	PeopleSearchResponse searchInternalAssignmentWorkers(AssignmentResourceSearchRequest request) throws SearchException;

	PeopleSearchResponse searchAssignmentWorkers(AssignmentResourceSearchRequest request) throws SearchException;

	PeopleSearchResponse searchWorkersForAutoRouting(AssignmentResourceSearchRequest request) throws SearchException;

	List<Worker> searchWorkersForGroupRouting(FindWorkerCriteria findWorkerCriteria, Long offset, Long limit, RequestContext requestContext) throws SearchException;
}
