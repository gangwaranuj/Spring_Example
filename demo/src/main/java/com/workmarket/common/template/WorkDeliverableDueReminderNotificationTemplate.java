package com.workmarket.common.template;

import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkDeliverableDueReminderNotificationTemplate extends AbstractWorkNotificationTemplate {
	private static final long serialVersionUID = -1034214818261483265L;

	private int numberOfRequiredDeliverables = 0;
	public static final int hoursRemaining = 12;

	public WorkDeliverableDueReminderNotificationTemplate(Long toId, Work work) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.RESOURCE_WORK_DELIVERABLE_REMINDER), ReplyToType.TRANSACTIONAL, work);
		for (DeliverableRequirement deliverableRequirement : work.getDeliverableRequirementGroup().getDeliverableRequirements()) {
			numberOfRequiredDeliverables += deliverableRequirement.getNumberOfFiles();
		}
	}

	public int getNumberOfRequiredDeliverables() {
		return numberOfRequiredDeliverables;
	}

	public static int getHoursRemaining() {
		return hoursRemaining;
	}
}
