package com.workmarket.common.template;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkDeclinedNotificationTemplate extends AbstractWorkNotificationTemplate {

	/**
	 *
	 */
	private static final long serialVersionUID = 8213768771026318023L;
	private User resource;

	public WorkDeclinedNotificationTemplate(Long toId, Work work, User resource) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORK_DECLINED), ReplyToType.TRANSACTIONAL, work);
		this.resource = resource;
	}

	public User getResource() {
		return resource;
	}
}
