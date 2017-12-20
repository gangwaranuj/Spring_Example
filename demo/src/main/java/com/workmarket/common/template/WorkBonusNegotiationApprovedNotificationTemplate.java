package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;

public class WorkBonusNegotiationApprovedNotificationTemplate extends AbstractWorkBonusNegotiationNotificationTemplate{
	public WorkBonusNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_BONUS_DECISION));
	}
}
