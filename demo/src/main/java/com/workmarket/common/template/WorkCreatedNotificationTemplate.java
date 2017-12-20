package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkCreatedNotificationTemplate extends AbstractWorkNotificationTemplate {
	/**
	 *
	 */
	private static final long serialVersionUID = 5834788089248344214L;

	public WorkCreatedNotificationTemplate(Long toId, Work work) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORK_CREATED), ReplyToType.TRANSACTIONAL, work);
	}
}
