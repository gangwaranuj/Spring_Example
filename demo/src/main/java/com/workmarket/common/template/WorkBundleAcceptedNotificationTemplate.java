package com.workmarket.common.template;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkBundleAcceptedNotificationTemplate extends AbstractWorkNotificationTemplate {

	/**
	 *
	 */
	private static final long serialVersionUID = 1073325313063452032L;
	private User resource;
	private WorkNegotiation negotiation;

	public WorkBundleAcceptedNotificationTemplate(Long toId, Work work, User resource) {
		super(resource.getId(), toId, new NotificationType(NotificationType.WORK_ACCEPTED), ReplyToType.TRANSACTIONAL_FROM_USER, work);
		this.resource = resource;
	}

	public User getResource() {
		return resource;
	}
}
