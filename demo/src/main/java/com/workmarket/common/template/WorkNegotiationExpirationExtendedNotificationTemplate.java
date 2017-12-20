package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;

public class WorkNegotiationExpirationExtendedNotificationTemplate extends AbstractWorkNegotiationNotificationTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1271984872497348088L;

	public WorkNegotiationExpirationExtendedNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation) {
		super(toId, work, negotiation, new NotificationType(NotificationType.WORK_NEGOTIATION));
	}
}