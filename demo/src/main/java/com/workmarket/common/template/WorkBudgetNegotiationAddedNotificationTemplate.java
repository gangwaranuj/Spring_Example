package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;

public class WorkBudgetNegotiationAddedNotificationTemplate extends AbstractWorkBudgetNegotiationNotificationTemplate {
	public WorkBudgetNegotiationAddedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_BUDGET_ADDED));
	}
}
