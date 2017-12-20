package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

/**
 * Created by rahul on 4/14/14
 */
public class WorkDeliverableLateNotificationTemplate extends AbstractWorkNotificationTemplate {
	private static final long serialVersionUID = -1034214818261483255L;

	public WorkDeliverableLateNotificationTemplate(Long toId, Work work) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.RESOURCE_WORK_DELIVERABLE_LATE), ReplyToType.TRANSACTIONAL, work);
	}
}
