package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;

public class WorkNegotiationDeclinedNotificationTemplate extends AbstractWorkNegotiationNotificationTemplate {

	private static final long serialVersionUID = 7205418823339305775L;

	public WorkNegotiationDeclinedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation, String noteCreatorFullName) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_NEGOTIATION_DECISION), noteCreatorFullName);
	}
}