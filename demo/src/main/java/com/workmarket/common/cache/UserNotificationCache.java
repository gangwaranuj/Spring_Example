package com.workmarket.common.cache;

import com.google.common.base.Optional;
import com.workmarket.domains.model.notification.UserNotification;
import com.workmarket.dto.UnreadNotificationsDTO;

import java.util.List;

public interface UserNotificationCache {

	String putNotifications(long userId, List<UserNotification> notifications);

	void clearNotifications(long userId);

	void putUnreadNotificationInfo(long userId, UnreadNotificationsDTO unreadNotificationsDTO);

	Optional<String> getNewUserNotificationJson(long userId);

	Optional<UnreadNotificationsDTO> getUnreadNotificationsInfoByUser(long userId);

	void clearUnreadNotificationInfo(long userId);

	Long getTimeOutInSeconds();
}
