package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;

public class WorkExpenseNegotiationApprovedNotificationTemplate extends AbstractWorkExpenseNegotiationNotificationTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3572728079652533941L;

	public WorkExpenseNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_EXPENSE_DECISION));
	}
}