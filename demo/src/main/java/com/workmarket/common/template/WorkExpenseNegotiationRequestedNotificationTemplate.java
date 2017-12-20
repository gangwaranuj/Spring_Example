package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;

public class WorkExpenseNegotiationRequestedNotificationTemplate extends AbstractWorkExpenseNegotiationNotificationTemplate {

	private static final long serialVersionUID = 8977242293120199706L;

	public WorkExpenseNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkExpenseNegotiation negotiation, String noteCreatorFullName) {
		super(toId, work, negotiation, new NotificationType(NotificationType.WORK_EXPENSE_REQUESTED), noteCreatorFullName);
	}
}