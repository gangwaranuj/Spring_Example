package com.workmarket.service.business.event;

import com.workmarket.domains.model.notification.NotificationType;

import java.util.List;

/**
 * An event to signal that the user notification cache needs to be refreshed from the DB
 */
public class RefreshUserNotificationCacheEvent extends Event {

	private static final long serialVersionUID = 7038783778470199685L;
	private final Long userId;
	private final String notificationUuid;

	@Deprecated
	private List<String> unreadNotificationIds;

	@Deprecated
	private NotificationType notificationType = null;

	public RefreshUserNotificationCacheEvent(final Long userId, final String notificationUuid) {
		this.userId = userId;
		this.notificationUuid = notificationUuid;
	}

	public Long getUserId() {
		return userId;
	}

	public String getNotificationUuid() {
		return notificationUuid;
	}
}
