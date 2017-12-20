package com.workmarket.common.template;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkResourceCancelledNotificationTemplate extends WorkResourceNotificationTemplate {
	private static final long serialVersionUID = 3925291047446518094L;

	public WorkResourceCancelledNotificationTemplate(WorkResource workResource) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, workResource.getWork().getBuyer().getId(), new NotificationType(NotificationType.WORK_RESOURCE_CANCELLED), ReplyToType.TRANSACTIONAL, workResource);
	}

	public WorkResourceCancelledNotificationTemplate(WorkResource workResource, Long toId) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORK_RESOURCE_CANCELLED), ReplyToType.TRANSACTIONAL, workResource);
	}
}
