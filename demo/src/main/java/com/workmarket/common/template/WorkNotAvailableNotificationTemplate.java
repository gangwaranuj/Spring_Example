package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkNotAvailableNotificationTemplate extends AbstractWorkNotificationTemplate {
	private static final long serialVersionUID = 1;

	public WorkNotAvailableNotificationTemplate(Long toId, Work work) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.RESOURCE_WORK_NOT_AVAILABLE), ReplyToType.TRANSACTIONAL, work);
	}
}
