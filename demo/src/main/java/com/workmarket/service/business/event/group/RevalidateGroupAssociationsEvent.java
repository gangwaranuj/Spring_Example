package com.workmarket.service.business.event.group;

import com.workmarket.service.business.event.Event;

import java.util.Map;

public class RevalidateGroupAssociationsEvent extends Event {

	private static final long serialVersionUID = -886599615149671956L;

	private Long memberUserId;
	private Long groupId;
	private Map<String, Object> modificationType;

	public RevalidateGroupAssociationsEvent() {
	}

	public RevalidateGroupAssociationsEvent(Long memberUserId, Long groupId, Map<String, Object> modificationType) {
		this.memberUserId = memberUserId;
		this.groupId = groupId;
		this.modificationType = modificationType;
	}

	public Long getMemberUserId() {
		return memberUserId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public Map<String, Object> getModificationType() {
		return modificationType;
	}
}
