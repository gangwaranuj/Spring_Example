package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;

public class WorkNegotiationRequestedNotificationTemplate extends AbstractWorkNegotiationNotificationTemplate {

	private static final long serialVersionUID = -5951242408974997324L;

	public WorkNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation, String noteCreatorFullName) {
		super(toId, work, negotiation, new NotificationType(NotificationType.WORK_NEGOTIATION), noteCreatorFullName);
	}
}