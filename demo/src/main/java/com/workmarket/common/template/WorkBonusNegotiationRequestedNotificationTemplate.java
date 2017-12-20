package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;

/**
 * Created by nick on 2/12/13 6:16 PM
 */
public class WorkBonusNegotiationRequestedNotificationTemplate extends AbstractWorkBonusNegotiationNotificationTemplate {
	public WorkBonusNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkBonusNegotiation negotiation, String noteCreatorFullName) {
		super(toId, work, negotiation, new NotificationType(NotificationType.WORK_BONUS_REQUESTED), noteCreatorFullName);
	}
}
