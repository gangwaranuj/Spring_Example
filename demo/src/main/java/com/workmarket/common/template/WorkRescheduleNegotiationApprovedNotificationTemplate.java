package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;

public class WorkRescheduleNegotiationApprovedNotificationTemplate extends AbstractWorkRescheduleNegotiationNotificationTemplate {

	private static final long serialVersionUID = 3178904128920409954L;

	public WorkRescheduleNegotiationApprovedNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation, Long activeWorkerId) {
		super(toId, work, negotiation, new NotificationType(NotificationType.WORK_RESCHEDULE_DECISION), activeWorkerId);

		if (isToActiveWorker()) {
			setNotificationType(new NotificationType(NotificationType.RESOURCE_WORK_RESCHEDULE_DECISION));
		}
	}
}