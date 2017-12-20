package com.workmarket.dao.notification;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserNotificationPreference;

import java.util.List;

public interface UserNotificationPreferenceDAO extends DAOInterface<UserNotificationPreference> {

	public UserNotificationPreference findByUserAndNotificationType(Long userId, String notificationTypeCode);

	/* only use is in AuthenticationService.finalAllUsersByCompanySubscribedToNotification, by extension,
	 * it's used in a number of places. */
	public List<User> findUsersByCompanyAndNotificationType(Long companyId, String notificationTypeCode);

	/* only use is in AuthenticationService to unsubscribe all SMS */
	public List<UserNotificationPreference> findByUser(Long userId);

	/* only use is in UserServiceImpl.findUserNotificationPreferencesWithDefault */
	public List<NotificationType> findByUserWithDefault(Long userId);

}