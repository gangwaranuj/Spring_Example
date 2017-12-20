package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;

public class WorkBonusNegotiationDeclinedNotificationTemplate extends AbstractWorkBonusNegotiationNotificationTemplate {
	public WorkBonusNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation, String noteCreatorFullName) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_BONUS_DECISION), noteCreatorFullName);
	}
}
