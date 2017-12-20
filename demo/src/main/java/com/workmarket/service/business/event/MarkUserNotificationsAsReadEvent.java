package com.workmarket.service.business.event;

import com.workmarket.dto.UnreadNotificationsDTO;

public class MarkUserNotificationsAsReadEvent extends Event {

	private static final long serialVersionUID = -2540655518651412616L;
	private final long userId;
	private final UnreadNotificationsDTO unreadNotificationsDTO;

	public MarkUserNotificationsAsReadEvent(final long userId, final UnreadNotificationsDTO unreadNotificationsDTO) {
		this.userId = userId;
		this.unreadNotificationsDTO = unreadNotificationsDTO;
	}

	public long getUserId() {
		return userId;
	}

	public UnreadNotificationsDTO getUnreadNotificationsDTO() {
		return unreadNotificationsDTO;
	}
}
