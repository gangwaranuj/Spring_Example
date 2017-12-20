package com.workmarket.service.business.event.group;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.workmarket.service.business.event.search.IndexerEvent;

import java.util.Collection;
import java.util.Set;

public class GroupUpdateSearchIndexEvent extends IndexerEvent {

	private static final long serialVersionUID = -6199733883270735764L;
	private Set<Long> groupIds;

	public GroupUpdateSearchIndexEvent() {}

	public GroupUpdateSearchIndexEvent(Collection<Long> groupIds) {
		this.groupIds = Sets.newHashSet(groupIds);
	}

	public Set<Long> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Set<Long> groupIds) {
		this.groupIds = groupIds;
	}

	public GroupUpdateSearchIndexEvent setGroupId(final Long groupId) {
		this.groupIds = ImmutableSet.of(groupId);
		return this;
	}

	@Override public String toString() {
		return "GroupUpdateSearchIndexEvent{" +
				"groupIds=" + groupIds +
				'}';
	}
}
