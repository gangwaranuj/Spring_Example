package com.workmarket.service.business.event;

public class UserGroupValidationEvent extends Event {
	private static final long serialVersionUID = -3989143364234720596L;
	private final long userGroupId;

	public UserGroupValidationEvent(final long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public long getUserGroupId() {
		return userGroupId;
	}
}
