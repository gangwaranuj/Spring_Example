package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WelcomeNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 5312157696400992499L;

	public WelcomeNotificationTemplate(Long toId) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.MISC), ReplyToType.TRANSACTIONAL);
	}

}
