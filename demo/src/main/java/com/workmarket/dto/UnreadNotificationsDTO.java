package com.workmarket.dto;

import java.io.Serializable;

public class UnreadNotificationsDTO implements Serializable {

	private static final long serialVersionUID = -1862109906620282988L;
	private String startUuid;
	private String endUuid;
	private long unreadCount;

	public UnreadNotificationsDTO(final String startUuid, final String endUuid, final long unreadCount) {
		this.startUuid = startUuid;
		this.endUuid = endUuid;
		this.unreadCount = unreadCount;
	}

	public UnreadNotificationsDTO(final String startUuid, final String endUuid) {
		this.startUuid = startUuid;
		this.endUuid = endUuid;
	}

	public String getStartUuid() {
		return startUuid;
	}

	public String getEndUuid() {
		return endUuid;
	}

	public long getUnreadCount() {
		return unreadCount;
	}
}
