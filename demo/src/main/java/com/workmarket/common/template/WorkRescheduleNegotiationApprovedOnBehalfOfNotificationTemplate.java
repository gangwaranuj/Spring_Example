package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;

public class WorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate extends AbstractWorkRescheduleNegotiationNotificationTemplate {

	private static final long serialVersionUID = 3178904128920409954L;

	private String negotiationRequestedByFullName;

	public WorkRescheduleNegotiationApprovedOnBehalfOfNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation, String negotiationRequestedByFullName) {
		super(toId, work, negotiation, new NotificationType(NotificationType.RESOURCE_WORK_RESCHEDULE_DECISION));
		this.negotiationRequestedByFullName = negotiationRequestedByFullName;
	}

	public String getNegotiationRequestedByFullName() {
		return negotiationRequestedByFullName;
	}
}