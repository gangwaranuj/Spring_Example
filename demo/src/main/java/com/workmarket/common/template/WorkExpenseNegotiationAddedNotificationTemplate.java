package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;

public class WorkExpenseNegotiationAddedNotificationTemplate extends AbstractWorkExpenseNegotiationNotificationTemplate {
	public WorkExpenseNegotiationAddedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_EXPENSE_ADDED));
	}
}
