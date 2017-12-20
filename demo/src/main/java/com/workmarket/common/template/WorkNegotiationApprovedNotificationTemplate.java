package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkApplyNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;

public class WorkNegotiationApprovedNotificationTemplate extends AbstractWorkNegotiationNotificationTemplate {

	private static final long serialVersionUID = -5692579862475672263L;

	public WorkNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_NEGOTIATION_DECISION));
	}

	public boolean isWorkApplyNegotiation() {
		return negotiation instanceof WorkApplyNegotiation;
	}
}