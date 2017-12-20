package com.workmarket.service.business.event;

import java.util.Set;

public class InviteToGroupFromCartEvent extends Event{

	private Long groupId;
	private Set<String> inviteeUserNumbers;
	private Long invitedByUserId;

	public InviteToGroupFromCartEvent(Long groupId, Set<String> inviteeUserNumbers, Long inviterId) {
		this.groupId = groupId;
		this.inviteeUserNumbers = inviteeUserNumbers;
		invitedByUserId = inviterId;
	}

	public InviteToGroupFromCartEvent() {
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Set<String> getInviteeUserNumbers() {
		return inviteeUserNumbers;
	}

	public void setInviteeUserNumbers(Set<String> inviteeUserNumbers) {
		this.inviteeUserNumbers = inviteeUserNumbers;
	}

	public Long getInvitedByUserId() {
		return invitedByUserId;
	}
}
