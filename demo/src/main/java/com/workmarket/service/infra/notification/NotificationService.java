package com.workmarket.service.infra.notification;

import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.email.EmailTemplate;

import java.util.Calendar;
import java.util.List;
import java.util.Map;


/**
 * This class only schedules the notification to be sent, in other words it sends the template to the queue to be processed.
 */
public interface NotificationService {

	/**
	 * Sends the EmailTemplate to the queue to be processed immediately
	 *
	 * @param template
	 */
	void sendNotification(EmailTemplate template);
	void sendNotification(EmailTemplate template, Calendar scheduleDate);

	/**
	 *  Sends the NotificationTemplate to the queue to be processed immediately
	 *
	 * @param template
	 */
	void sendNotification(NotificationTemplate template);

	/**
	 * Sends a notification directly to the output channels, processes immediately
	 * and without the subsequent JMS message
	 * @param template
	 */
	void sendNotificationsDirectly(List<NotificationTemplate> template);

	/**
	 * Sends a set of notifications
	 * @param template The messages we are sending
	 */
	void sendNotifications(List<NotificationTemplate> template);


	/**
	 * Dispatch notification template at a scheduled date/time.
	 * Notification templates are representative of all of the possible delivery methods available
	 * for a notification type. Message delivery is conditional on the user's status and preferences.
	 * 
	 * @param template
	 * @param scheduleDate
	 * @
	 */
	void sendNotification(NotificationTemplate template, Calendar scheduleDate);
			
	/**
	 * Send verification code to confirm user's SMS phone configuration.
	 * 
	 * @param userId
	 * @
	 */
	void sendMobileVerificationCode(Long userId) ;
    
	/**
	 * Verify whether or not the provided SMS verification code matches that which was sent for authorization.
	 * 
	 * @param userId
	 * @param code
	 * @return Whether the provided verification code is valid
	 */
	boolean verifyMobileVerificationCode(Long userId, String code);

	/**
	 * Send a Work Notifification.
	 * @param template
	 */
	void sendWorkNotifyAsync(NotificationTemplate template, List<Long> userIdsWithSms, List<Long> userIdsWithPush, Calendar scheduleDate);
}