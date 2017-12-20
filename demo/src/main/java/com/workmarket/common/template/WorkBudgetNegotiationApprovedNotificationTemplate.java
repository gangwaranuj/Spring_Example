package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;

/**
 * Created by nick on 2012-11-06 9:35 AM
 */
public class WorkBudgetNegotiationApprovedNotificationTemplate extends AbstractWorkBudgetNegotiationNotificationTemplate {


	public WorkBudgetNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_BUDGET_DECISION));
	}

}