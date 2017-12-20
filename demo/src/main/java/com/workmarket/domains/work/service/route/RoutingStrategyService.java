package com.workmarket.domains.work.service.route;

import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.AutoRoutingStrategy;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeGroupVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeGroupsAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeWorkAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeWorkVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.PeopleSearchRoutingStrategy;
import com.workmarket.domains.work.model.route.PolymathAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.PolymathVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.UserRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorSearchRoutingStrategy;
import com.workmarket.search.request.user.PeopleSearchRequest;

import java.util.List;
import java.util.Set;

public interface RoutingStrategyService {

	/**
	 * Add a new routing strategy to an assignment.
	 * A routing strategy is a search filter with a delay time that defines a list of users to route the assignment
	 * to after a prescribed amount of time from the time the strategy is initialized. An assignment can have multiple
	 * routing strategies to allow for a timed delivery of the assignment details to various resource pools.
	 *
	 * @param workId
	 * @param routingStrategyGroupId
	 * @param searchRequest
	 * @param delayMinutes
	 * @param assignToFirstToAccept
	 * @return Routing strategy
	 * @throws Exception
	 */
	AbstractRoutingStrategy addPeopleSearchRequestRoutingStrategy(long workId, long routingStrategyGroupId, PeopleSearchRequest searchRequest, int delayMinutes, boolean assignToFirstToAccept);

	AbstractRoutingStrategy addPeopleSearchRequestRoutingStrategy(long workId, PeopleSearchRequest searchRequest, int delayMinutes, boolean assignToFirstToAccept);

	AbstractRoutingStrategy addGroupIdsRoutingStrategy(long workId, Set<Long> groupIds, int delayMinutes, boolean assignToFirstToAccept);

	AbstractRoutingStrategy addGroupIdsRoutingStrategy(long workId, long routingStrategyGroupId, Set<Long> groupIds, int delayMinutes, boolean assignToFirstToAccept);

	UserRoutingStrategy addUserIdsRoutingStrategy(long workId, Set<Long> userIds, int delayInMinutes, boolean assignToFirstToAcccept);

	UserRoutingStrategy addUserNumbersRoutingStrategy(long workId, Set<String> userNumbers, int delayInMinutes, boolean assignToFirstToAcccept);

	UserRoutingStrategy addUserNumbersRoutingStrategy(long workId, long routingStrategyGroupId, Set<String> userNumbers, int delayInMinutes, boolean assignToFirstToAcccept);

	AutoRoutingStrategy addAutoRoutingStrategy(long workId, boolean assignToFirstToAccept);

	LikeGroupsAutoRoutingStrategy addLikeGroupsAutoRoutingStrategy(long workId);

	LikeWorkAutoRoutingStrategy addLikeWorkAutoRoutingStrategy(long workId);

	PolymathAutoRoutingStrategy addPolymathAutoRoutingStrategy(long workId);

	LikeGroupVendorRoutingStrategy addLikeGroupVendorRoutingStrategy(long workId);

	LikeWorkVendorRoutingStrategy addLikeWorkVendorRoutingStrategy(long workId);

	PolymathVendorRoutingStrategy addPolymathVendorRoutingStrategy(long workId);

	PeopleSearchRoutingStrategy addPeopleSearchRoutingStrategy(long workId, Set<String> userNumbers, Long dispatcherId, boolean assignToFirstToAccept);
	PeopleSearchRoutingStrategy addPeopleSearchRoutingStrategy(String workNumber, Set<String> userNumbers, Long dispatcherId, boolean assignToFirstToAccept);

	VendorSearchRoutingStrategy addVendorSearchRoutingStrategy(long workId, Set<String> companyNumbers, Long dispatcherId, boolean assignToFirstToAccept);

	VendorRoutingStrategy addVendorRoutingStrategyByCompanyNumbers(long workId, long routingStrategyGroupId, Set<String> companyNumbers, int delayInMinutes, boolean assignToFirstToAccept);

	void scheduleExecuteRoutingStrategy(long workId, AbstractRoutingStrategy routingStrategy);

	void scheduleExecuteRoutingStrategyGroup(long workId, long routingStrategyGroupId, int delayMinutes);

	/**
	 * Send the assignment to the resources defined by the routing strategy.
	 *
	 * @param routingStrategyId
	 * @throws Exception
	 */
	void executeRoutingStrategy(long routingStrategyId);

	List<Long> findScheduledRoutingStrategies();

	List<Long> findScheduledRoutingStrategiesWithoutGroup();

	List<Long> findScheduledRoutingStrategiesByGroupId(long routingStrategyGroupId);

	List<UserRoutingStrategy> findUserRoutingStrategiesByGroup(long routingGroupId);

	List<GroupRoutingStrategy> findGroupRoutingStrategiesByGroup(long routingGroupId);

	List<VendorRoutingStrategy> findVendorRoutingStrategiesByGroup(long routingGroupId);

	List<AbstractRoutingStrategy> findAllRoutingStrategiesByWork(long workId);

	void saveOrUpdateRoutingStrategy(AbstractRoutingStrategy routingStrategy);

	<T extends AbstractRoutingStrategy> T findRoutingStrategy(long id);
}
