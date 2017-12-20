package com.workmarket.service.business.event;

import java.util.List;

public class InviteToGroupEvent extends Event {

	private static final long serialVersionUID = -8092814949967116389L;
	private Long groupId;
	private List<Long> inviteeUserIds;
	private Long invitedByUserId;

	public InviteToGroupEvent(List<Long> inviteeUserIds, Long groupId, Long invitedByUserId) {
		this.groupId = groupId;
		this.inviteeUserIds = inviteeUserIds;
		this.invitedByUserId = invitedByUserId;
	}

	public InviteToGroupEvent() {}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public List<Long> getInviteeUserIds() {
		return inviteeUserIds;
	}

	public void setInviteeUserIds(List<Long> inviteeUserIds) {
		this.inviteeUserIds = inviteeUserIds;
	}

	public Long getInvitedByUserId() {
		return invitedByUserId;
	}

	public void setInvitedByUserId(Long invitedByUserId) {
		this.invitedByUserId = invitedByUserId;
	}
}
