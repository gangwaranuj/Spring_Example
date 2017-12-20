package com.workmarket.service.infra.dto;

import com.workmarket.service.business.dto.NotificationDTO;

public class UserNotificationDTO extends NotificationDTO {

	private static final long serialVersionUID = -4028182664745431782L;
	private boolean sticky = false;
	private String uuid;

	public boolean isSticky() {
		return sticky;
	}

	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}
}
