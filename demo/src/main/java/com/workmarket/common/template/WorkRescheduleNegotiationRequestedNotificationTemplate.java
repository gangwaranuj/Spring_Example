package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;

public class WorkRescheduleNegotiationRequestedNotificationTemplate extends AbstractWorkRescheduleNegotiationNotificationTemplate {

	private static final long serialVersionUID = -8851459890983728452L;

	public WorkRescheduleNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation, Long activeWorkerId) {
		super(toId, work, negotiation, new NotificationType(NotificationType.WORK_RESCHEDULE_REQUESTED), activeWorkerId);

		if (isToActiveWorker()) {
			setNotificationType(new NotificationType(NotificationType.RESOURCE_WORK_RESCHEDULE_REQUESTED));
		}
	}

	public WorkRescheduleNegotiationRequestedNotificationTemplate(Long toId, Work work, WorkRescheduleNegotiation negotiation, String noteCreatorFullName, Long activeWorkerId) {
		this(toId, work, negotiation, activeWorkerId);
		this.noteCreatorFullName = noteCreatorFullName;
	}
}