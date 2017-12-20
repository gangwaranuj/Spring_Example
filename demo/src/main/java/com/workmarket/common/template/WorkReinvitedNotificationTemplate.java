package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkReinvitedNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -4223253465737704573L;

	public WorkReinvitedNotificationTemplate(Long toId, Work work, double distanceInMilesToWork) {
		super(work.getBuyer().getId(), toId, new NotificationType(NotificationType.RESOURCE_WORK_INVITED), ReplyToType.TRANSACTIONAL_FROM_USER, work, distanceInMilesToWork);
	}
}