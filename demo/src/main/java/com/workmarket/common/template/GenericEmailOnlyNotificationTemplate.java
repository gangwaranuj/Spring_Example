package com.workmarket.common.template;

import org.springframework.util.Assert;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.common.template.sms.SMSTemplate;

public class GenericEmailOnlyNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 4511623497931210678L;
	private String message;

	public GenericEmailOnlyNotificationTemplate(Long fromId, Long toId, String emailSubject, String message, NotificationType notificationType) {
		Assert.notNull(fromId);
		Assert.notNull(toId);
		Assert.hasText(emailSubject);
		Assert.notNull(message);

		setFromId(fromId);
		setToId(toId);
		setEmailSubject(emailSubject);
		setNotificationType(notificationType);

		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public SMSTemplate getSMSTemplate() {
		return null;
	}

}
