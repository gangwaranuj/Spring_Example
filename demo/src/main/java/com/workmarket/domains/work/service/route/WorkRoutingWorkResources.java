package com.workmarket.domains.work.service.route;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WorkRoutingWorkResources<E> {

	private Collection<E> potentialWorkResources;
	private Collection<?> resourcesAlreadyOnWork;

	public WorkRoutingWorkResources(Collection<E> potentialWorkResources, Collection<?> resourcesAlreadyOnWork) {
		this.potentialWorkResources = potentialWorkResources;
		this.resourcesAlreadyOnWork = resourcesAlreadyOnWork;
	}

	public boolean exceedsMaxResourceLimit() {
		int numPotentialWorkResources = potentialWorkResources.size();
		// ignore limit if inviting just one worker (APP-9231)
		if (numPotentialWorkResources == 1) {
			return false;
		}
		int numResourcesAlreadyOnWork = resourcesAlreadyOnWork.size();
		int numTotalWorkResources = numPotentialWorkResources + numResourcesAlreadyOnWork;
		return numTotalWorkResources > Constants.MAX_RESOURCES_PER_ASSIGNMENT;
	}

	public List<E> getExcess() {
		if (!exceedsMaxResourceLimit()) {
			return Collections.EMPTY_LIST;
		}
		List<E> allResources = ImmutableList.copyOf(potentialWorkResources);
		int fromIndex = Math.max(Constants.MAX_RESOURCES_PER_ASSIGNMENT - resourcesAlreadyOnWork.size(), 0);
		return ImmutableList.copyOf(allResources.subList(fromIndex, allResources.size()));
	}
}
