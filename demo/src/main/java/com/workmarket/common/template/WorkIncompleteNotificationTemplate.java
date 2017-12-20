package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkIncompleteNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = 8661853389750675548L;
	private String message;

	public WorkIncompleteNotificationTemplate(Long toId, Work work, String message) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORK_INCOMPLETE), ReplyToType.TRANSACTIONAL, work);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
