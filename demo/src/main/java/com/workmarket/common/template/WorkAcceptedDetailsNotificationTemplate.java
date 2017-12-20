package com.workmarket.common.template;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkAcceptedDetailsNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -6369589271512936050L;

	private User resource;

	public WorkAcceptedDetailsNotificationTemplate(Long toId, Work work, User resource) {
		super(resource.getId(), toId, new NotificationType(NotificationType.RESOURCE_WORK_ACCEPTED_DETAILS), ReplyToType.TRANSACTIONAL, work);
		this.resource = resource;
	}

	public User getResource() {
		return resource;
	}
}
