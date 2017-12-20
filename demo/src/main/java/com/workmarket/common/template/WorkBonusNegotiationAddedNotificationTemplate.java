package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;

/**
 * Created by nick on 2/12/13 6:08 PM
 */
public class WorkBonusNegotiationAddedNotificationTemplate extends AbstractWorkBonusNegotiationNotificationTemplate {
	public WorkBonusNegotiationAddedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_BONUS_ADDED));
	}
}
