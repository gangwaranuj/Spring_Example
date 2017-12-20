package com.workmarket.service.business.wrapper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import org.apache.commons.collections.MapUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class WorkRoutingResponseSummary extends MessageResponse {

	private final Map<WorkAuthorizationResponse, Set<String>> response = Maps.newLinkedHashMap();
	private final Map<LaneType, Set<User>> companyUserLaneAssociationMap = Maps.newLinkedHashMap();

	public WorkRoutingResponseSummary() {
		super();
	}

	public Map<WorkAuthorizationResponse, Set<String>> getResponse() {
		return response;
	}

	public Map<LaneType, Set<User>> getCompanyUserLaneAssociationMap() {
		return companyUserLaneAssociationMap;
	}

	public void addToCompanyUserLaneAssociationMap(Map<LaneType, Set<User>> companyUserLaneAssociationMap) {
		if (MapUtils.isNotEmpty(companyUserLaneAssociationMap)) {
			this.companyUserLaneAssociationMap.putAll(companyUserLaneAssociationMap);
		}
	}

	public WorkRoutingResponseSummary addToWorkAuthorizationResponse(WorkAuthorizationResponse workAuthorizationResponse, String routedToId) {
		if (workAuthorizationResponse != null) {
			Set<String> routedToIds = (Set<String>)MapUtils.getObject(response, workAuthorizationResponse, Sets.newLinkedHashSet());
			routedToIds.add(routedToId);
			response.put(workAuthorizationResponse, routedToIds);
		}
		return this;
	}

	public WorkRoutingResponseSummary addToWorkAuthorizationResponse(WorkAuthorizationResponse workAuthorizationResponse, Collection<String> routedToIds) {
		if (workAuthorizationResponse != null) {
			Set<String> routedToIdsSet = (Set<String>)MapUtils.getObject(response, workAuthorizationResponse, Sets.newLinkedHashSet());
			routedToIdsSet.addAll(routedToIds);
			response.put(workAuthorizationResponse, routedToIdsSet);
		}
		return this;
	}

	@Override
	public boolean isSuccessful() {
		return response.containsKey(WorkAuthorizationResponse.SUCCEEDED);
	}

	@Override public String toString() {
		return "WorkRoutingResponseSummary{" +
				"response=" + response +
				'}';
	}
}
