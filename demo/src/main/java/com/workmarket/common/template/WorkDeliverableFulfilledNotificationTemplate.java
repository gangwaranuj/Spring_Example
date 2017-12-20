package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

/**
 * Created by rahul on 4/14/14
 */
public class WorkDeliverableFulfilledNotificationTemplate extends AbstractWorkNotificationTemplate {
	private static final long serialVersionUID = -1034214818262483265L;

	public WorkDeliverableFulfilledNotificationTemplate(Long toId, Work work) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORK_DELIVERABLE_REQUIREMENTS_FULFILLED), ReplyToType.TRANSACTIONAL, work);
	}
}
