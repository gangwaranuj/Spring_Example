package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class Lane3ApprovalNotificationTemplate extends NotificationTemplate {

	/**
	 *
	 */
	private static final long serialVersionUID = -3938890371372469293L;

	public Lane3ApprovalNotificationTemplate(Long toId) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.PROFILE_APPROVED), ReplyToType.TRANSACTIONAL);
	}

}
