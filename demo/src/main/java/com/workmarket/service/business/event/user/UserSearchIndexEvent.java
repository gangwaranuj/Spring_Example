package com.workmarket.service.business.event.user;

import com.google.common.collect.Sets;
import com.workmarket.service.business.event.search.IndexerEvent;

import java.util.List;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class UserSearchIndexEvent extends IndexerEvent {

	private static final long serialVersionUID = 1347564773896215115L;
	private Set<Long> userIds;
	private String directedTowards;

	public UserSearchIndexEvent() {}

	public UserSearchIndexEvent(Long userId) {
		if (userId != null) {
			this.setUserIds(Sets.newHashSet(userId));
		}
	}

	public UserSearchIndexEvent(List<Long> userIds) {
		if (isNotEmpty(userIds)) {
			userIds.remove(null);
		}
		this.setUserIds(Sets.newHashSet(userIds));
	}

	public Set<Long> getUserIds() {
		return userIds;
	}

	public UserSearchIndexEvent setUserIds(Set<Long> userIds) {
		this.userIds = userIds;
		return this;
	}

	public String getDirectedTowards() {
		return directedTowards;
	}

	public UserSearchIndexEvent setDirectedTowards(String directedTowards) {
		this.directedTowards = directedTowards;
		return this;
	}

	@Override
	public String toString() {
		return "UserSearchIndexEvent{" +
				"userIds=" + userIds +
				"directedTowards=" + directedTowards +
				'}';
	}
}
