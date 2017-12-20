package com.workmarket.service.infra.notification;

import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.notification.UserNotificationTemplate;
import com.workmarket.common.template.push.PushTemplate;
import com.workmarket.common.template.sms.SMSTemplate;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.notification.sms.vo.SmsNotifyResponse;
import com.workmarket.notification.vo.EmailNotifyResponse;
import com.workmarket.service.business.wrapper.PushResponseAndDeviceType;

import java.util.List;
import java.util.Map;

public interface NotificationDispatcher {

	/**
	 * Notification templates are representative of all of the possible delivery methods available
	 * for a notification type. Message delivery is conditional on the user's status and preferences.
	 *	 
	 * @param template
	 * @throws Exception
	 */
	void dispatchNotification(NotificationTemplate template)  throws Exception;

	void dispatchNotification(NotificationTemplate notificationTemplate, NotificationType notificationType);

	void dispatchNotifications(List<? extends NotificationTemplate> templates);

	/**
	 * Dispatch email.
	 *
	 * @param template
	 * @throws Exception
	 */
	EmailNotifyResponse dispatchEmail(EmailTemplate template);

	/**
	 * Dispatch an email using the notification service to render and send the email.
	 * @param toUserId                the userId of the user we're sending this to
	 * @param fromUserId
	 * @param emailBodyTemplateKey    the template key for the body
	 * @param emailSubjectTemplateKey the template key for the subject
	 * @param languageCode            the code of the language to send the email in
	 * @param replacements            the replacements for the template
	 * @param notificationCode        the notification code for the database, like "manage.work.invited"
	 */
	EmailNotifyResponse dispatchEmail(
			final long toUserId,
			final long fromUserId,
			final String emailBodyTemplateKey,
			final String emailSubjectTemplateKey,
			final String languageCode,
			final Map<String, Object> replacements, String notificationCode);
	
	/**
	 * Dispatch user notifications.
	 * User notifications are a display of a user's non work-related activity and event stream.
	 * 
	 * @param template
	 * @throws Exception
	 */
	boolean dispatchUserNotification(UserNotificationTemplate template) throws Exception;

	/**
	 * Dispatch if we need to render the template
	 * @param isSticky
	 * @param userNotificationTemplateKey
	 * @param toUserId
	 * @param fromUserId
	 * @param notificationTypeCode
	 * @param replacements
	 * @param languageCode
	 */
	boolean dispatchUserNotification(
			final boolean isSticky,
			final String userNotificationTemplateKey,
			final Long toUserId,
			final Long fromUserId,
			final String notificationTypeCode, Map<String, Object> replacements, String languageCode);

	/**
	 * Dispatch SMS.
	 *
	 * @param smsTemplate
	 * @throws Exception
	 */
	SmsNotifyResponse dispatchSMS(SMSTemplate smsTemplate) throws Exception;

	SmsNotifyResponse dispatchSMS(final Long toUserId, final String smsTemplateKey, final String langaugeCode, final Map<String, Object> replacements, final String notificationTypeCode);

	/**
	 * Dispatch push notification to associated devices.
	 *
	 * @param pushTemplate
	 * @throws Exception
	 */
	List<PushResponseAndDeviceType> dispatchPush(PushTemplate pushTemplate) throws Exception;

	List<PushResponseAndDeviceType> dispatchPush(final Long toUserId, final String pushTemplateKey, final String languageCode, final Map<String, Object> replacements, final String notificationTypeCode);
}
