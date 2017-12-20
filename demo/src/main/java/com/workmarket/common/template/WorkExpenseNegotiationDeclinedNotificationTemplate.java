package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;

public class WorkExpenseNegotiationDeclinedNotificationTemplate extends AbstractWorkExpenseNegotiationNotificationTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7666436022603558159L;

	public WorkExpenseNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation, String noteCreatorFullName) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_EXPENSE_DECISION), noteCreatorFullName);
	}
}