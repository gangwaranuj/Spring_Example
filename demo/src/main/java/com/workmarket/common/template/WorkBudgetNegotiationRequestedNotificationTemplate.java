package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;

/**
 * Created by nick on 2012-11-06 9:23 AM
 */
public class WorkBudgetNegotiationRequestedNotificationTemplate extends AbstractWorkBudgetNegotiationNotificationTemplate {

	private static final long serialVersionUID = 5062981502230183736L;

	public Boolean isBuyerInitiated = Boolean.FALSE;

	public WorkBudgetNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkBudgetNegotiation negotiation, String noteCreatorFullName) {
		super(toId, work, negotiation, new NotificationType(NotificationType.WORK_BUDGET_REQUESTED), noteCreatorFullName);
		isBuyerInitiated = negotiation.getRequestedBy().getId().equals(work.getBuyer().getId());
	}
}
