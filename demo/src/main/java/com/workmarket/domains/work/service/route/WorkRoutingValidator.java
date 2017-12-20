package com.workmarket.domains.work.service.route;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.search.response.user.PeopleSearchResult;

import java.util.List;
import java.util.Set;

/**
 * Author: rocio
 */
public interface WorkRoutingValidator {

	WorkAuthorizationResponse validateSearchResult(PeopleSearchResult searchResult, List<Long> resourcesAlreadyOnWork, Work work);

	WorkAuthorizationResponse validateUser(User user, Work work, LaneType laneType);

	WorkAuthorizationResponse validateProjectBudget(Work work);

	Set<WorkAuthorizationResponse> validateWorkForRouting(Work work);

}
