package com.workmarket.service.business.event;

import java.util.List;

public class UserGroupAssociationUpdateEvent extends Event {

	private static final long serialVersionUID = -8092814949267116389L;

	private Long groupId;
	private List<Long> userIds;

	public UserGroupAssociationUpdateEvent() {}

	public UserGroupAssociationUpdateEvent(Long groupId, final List<Long> userIds) {
		this.groupId = groupId;
		this.userIds = userIds;
	}

	public Long getGroupId() {
		return groupId;
	}

	public List<Long> getUserIds() {
		return userIds;
	}
}
