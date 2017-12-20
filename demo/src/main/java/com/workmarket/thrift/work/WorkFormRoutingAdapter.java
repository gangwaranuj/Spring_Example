package com.workmarket.thrift.work;

import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.web.forms.work.WorkFormRouting;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorkFormRoutingAdapter {

	private static int ZERO_MINUTE_DELAY = 0;
	private static boolean ASSIGN_TO_FIRST_TO_ACCEPT = true;
	private static boolean NEED_TO_APPLY = false;

	private WorkFormRouting workFormRouting;

	public WorkFormRoutingAdapter(WorkFormRouting workFormRouting) {
		this.workFormRouting = workFormRouting;
	}

	public List<RoutingStrategy> asRoutingStrategiesForUsers() {
		List<RoutingStrategy> routingStrategies = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(workFormRouting.getAssignToFirstToAcceptUserNumbers())) {
			RoutingStrategy routingStrategy =
				getRoutingStrategyForUsers(workFormRouting.getAssignToFirstToAcceptUserNumbers(), ASSIGN_TO_FIRST_TO_ACCEPT);
			routingStrategies.add(routingStrategy);
		}

		if (CollectionUtils.isNotEmpty(workFormRouting.getNeedToApplyUserNumbers())) {
			RoutingStrategy routingStrategy =
				getRoutingStrategyForUsers(workFormRouting.getNeedToApplyUserNumbers(), NEED_TO_APPLY);
			routingStrategies.add(routingStrategy);
		}
		return routingStrategies;
	}

	public List<RoutingStrategy> asRoutingStrategiesForGroups(long userId) {
		List<RoutingStrategy> routingStrategies = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(workFormRouting.getAssignToFirstToAcceptGroupIds())) {
			PeopleSearchRequest filter = getPeopleSearchRequest(workFormRouting.getAssignToFirstToAcceptGroupIds(), userId);
			RoutingStrategy routingStrategy = getRoutingStrategyForGroups(filter, ASSIGN_TO_FIRST_TO_ACCEPT);
			routingStrategies.add(routingStrategy);
		}

		if (CollectionUtils.isNotEmpty(workFormRouting.getNeedToApplyGroupIds())) {
			PeopleSearchRequest filter = getPeopleSearchRequest(workFormRouting.getNeedToApplyGroupIds(), userId);
			RoutingStrategy routingStrategy = getRoutingStrategyForGroups(filter, NEED_TO_APPLY);
			routingStrategies.add(routingStrategy);
		}

		return routingStrategies;
	}

	public List<RoutingStrategy> asRoutingStrategiesForVendors() {
		List<RoutingStrategy> routingStrategies = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(workFormRouting.getAssignToFirstToAcceptVendorCompanyNumbers())) {
			RoutingStrategy routingStrategy =
				getRoutingStrategyForVendors(workFormRouting.getAssignToFirstToAcceptVendorCompanyNumbers(), ASSIGN_TO_FIRST_TO_ACCEPT);
			routingStrategies.add(routingStrategy);
		}

		if (CollectionUtils.isNotEmpty(workFormRouting.getNeedToApplyVendorCompanyNumbers())) {
			RoutingStrategy routingStrategy =
				getRoutingStrategyForVendors(workFormRouting.getNeedToApplyVendorCompanyNumbers(), NEED_TO_APPLY);
			routingStrategies.add(routingStrategy);
		}
		return routingStrategies;
	}

	private RoutingStrategy getRoutingStrategyForUsers(Set<String> userNumbers, boolean assignToFirstToAccept) {
		return new RoutingStrategy()
			.setRoutingUserNumbers(userNumbers)
			.setDelayMinutes(ZERO_MINUTE_DELAY)
			.setAssignToFirstToAccept(assignToFirstToAccept);
	}

	private RoutingStrategy getRoutingStrategyForVendors(Set<String> companyNumbers, boolean assignToFirstToAccept) {
		return new RoutingStrategy()
			.setVendorCompanyNumbers(companyNumbers)
			.setDelayMinutes(ZERO_MINUTE_DELAY)
			.setAssignToFirstToAccept(assignToFirstToAccept);
	}

	private RoutingStrategy getRoutingStrategyForGroups(PeopleSearchRequest filter, boolean assignToFirstToAccept) {
		return new RoutingStrategy()
			.setFilter(filter)
			.setDelayMinutes(ZERO_MINUTE_DELAY)
			.setAssignToFirstToAccept(assignToFirstToAccept);
	}

	private PeopleSearchRequest getPeopleSearchRequest(Set<Long> groupIds, long userId) {
		PeopleSearchRequest filter = new PeopleSearchRequest().setUserId(userId);

		if (CollectionUtils.isNotEmpty(groupIds)) {
			filter.setGroupFilter(groupIds);
		}

		return filter;
	}


}
