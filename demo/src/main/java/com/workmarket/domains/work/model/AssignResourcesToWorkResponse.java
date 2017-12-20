package com.workmarket.domains.work.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class AssignResourcesToWorkResponse {

	private final Map<AssignResourceResponseType, Collection<User>> resourceResponses;
	private final Map<Long, AssignResourceResponse> resourceResponsePair;

	public AssignResourcesToWorkResponse() {
		this.resourceResponses = Maps.newEnumMap(AssignResourceResponseType.class);
		for (AssignResourceResponseType responseType : AssignResourceResponseType.values()) {
			Set<User> mapVal = Sets.newHashSet();
			resourceResponses.put(responseType, mapVal);
		}
		resourceResponsePair = Maps.newHashMap();
	}

	public void addResourceResponse(AssignResourceResponseType type, User user) {
		resourceResponses.get(type).add(user);
		resourceResponsePair.put(user.getId(), new AssignResourceResponse(user, type));
	}

	public Collection<User> getResourceResponse(AssignResourceResponseType type) {
		return resourceResponses.get(type);
	}

	public AssignResourceResponse getResourceResponseByUserId(Long userId) {
		return resourceResponsePair.get(userId);
	}

	public static class AssignResourceResponse extends MutablePair<User, AssignResourceResponseType> {

		public AssignResourceResponse(User user, AssignResourceResponseType type) {
			super(user, type);
		}
	}
}
