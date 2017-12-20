package com.workmarket.domains.work.service.route;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.domains.work.service.resource.WorkResourceAddOptions;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.exception.work.WorkNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface WorkRoutingService {


	/**
	 * Adds the users to the work.
	 * Includes a few variations on the method signature for convenience...
	 *
	 * @param work
	 * @param userIds a collection of user ids
	 * @param options
	 * @return
	 * @throws com.workmarket.service.exception.work.WorkNotFoundException if the work isn't found
	 */
	WorkRoutingResponseSummary addToWorkResources(Work work, Set<Long> userIds, WorkResourceAddOptions options) throws WorkNotFoundException;

	WorkRoutingResponseSummary addToWorkResources(Work work, Set<Long> userIds, WorkResourceAddOptions options, boolean assignToFirstToAccept) throws WorkNotFoundException;

	WorkRoutingResponseSummary addToWorkResources(Long workId, Set<Long> userIds, boolean assignToFirstToAccept) throws WorkNotFoundException;

	WorkRoutingResponseSummary addToWorkResourcesAsDispatcher(String workNumber, Set<String> userNumbers, Long dispatcherId, boolean assignToFirstToAccept) throws WorkNotFoundException;

	WorkRoutingResponseSummary addToWorkResources(String workNumber, Set<String> userNumbers) throws WorkNotFoundException;

	WorkRoutingResponseSummary addToWorkResources(String workNumber, Set<String> userNumbers, boolean assignToFirstToAccept) throws WorkNotFoundException;

	WorkRoutingResponseSummary addToWorkResources(long workId, Set<Long> userIds, WorkResourceAddOptions options, boolean assignToFirstToAccept) throws WorkNotFoundException;

	WorkRoutingResponseSummary addToWorkResources(long workId, long userId) throws WorkNotFoundException;

	WorkRoutingResponseSummary addToWorkResources(long workId, long userId, boolean assignToFirstToAccept) throws WorkNotFoundException;

	WorkRoutingResponseSummary addToWorkResources(long workId, List<PeopleSearchResult> selectedUsers, WorkResourceAddOptions options, boolean assignToFirstAccept) throws WorkNotFoundException;

	/**
	 * This takes in a list of non-lane 1,2,3 users and returns the users that are part of lane 4 for that company being passed in
	 *
	 * @param potentialLane4Users
	 * @param company
	 * @return a list of users that are part of the company's lane 4
	 */
	Set<User> getEligibleLane3Users(WorkRoutingResponseSummary response, Collection<Long> potentialLane4Users, Company company);

	/**
	 * After assignments are routed, this method will send the emails, mark the assignment as SENT and do other post-routing operations.
	 *
	 * @param workId
	 * @param invitedWorkResources
	 */

	void openBundle(long workId, Collection<Long> invitedWorkResources);

	void openWork(long workId);

	WorkAuthorizationResponse openWork(String workNumber);

	void openWork(Work work);

}
