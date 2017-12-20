package com.workmarket.common.template;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;

public class WorkSubStatusAlertNotificationTemplate extends WorkSubStatusNotificationTemplate {

	private static final long serialVersionUID = 7365229153263212554L;

	public WorkSubStatusAlertNotificationTemplate(Long toId, WorkSubStatusTypeAssociation association, Work work, WorkResource workResource, Long activeWorkerId) {
		super(toId, association, work, workResource, activeWorkerId);

		if (isToActiveWorker()) {
			setNotificationType(new NotificationType(NotificationType.RESOURCE_WORK_SUBSTATUS_ALERT));
		} else {
			setNotificationType(new NotificationType(NotificationType.WORK_SUBSTATUS_ALERT));
		}
	}
}