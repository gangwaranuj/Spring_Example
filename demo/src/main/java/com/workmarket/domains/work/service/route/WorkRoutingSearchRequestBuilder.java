package com.workmarket.domains.work.service.route;

import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;

/**
 * Author: rocio
 */
public interface WorkRoutingSearchRequestBuilder {

	AssignmentResourceSearchRequest build(AbstractRoutingStrategy routingStrategy);

	PeopleSearchRequest buildAddressSearchFilter(AbstractRoutingStrategy routingStrategy, PeopleSearchRequest searchRequest);

	FindWorkerCriteria buildFindWorkerCriteriaForGroupRouting(AbstractRoutingStrategy routingStrategy);
}
