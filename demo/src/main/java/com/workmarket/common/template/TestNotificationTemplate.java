package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.common.template.sms.SMSTemplate;
import com.workmarket.service.infra.communication.ReplyToType;

public class TestNotificationTemplate extends NotificationTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8713113574368818633L;

	public TestNotificationTemplate(Long fromId, Long toId, NotificationType notificationType) {
		super(fromId, toId, notificationType, ReplyToType.INVITATION);
	}

	public SMSTemplate getSMSTemplate() {
		return null;
	}
}
