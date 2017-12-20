package com.workmarket.common.template;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;

public class WorkQuestionAnsweredNotificationTemplate extends WorkQuestionNotificationTemplate {

	private static final long serialVersionUID = -584450120104235404L;
	private String type;

	public WorkQuestionAnsweredNotificationTemplate(Long toId, Work work, WorkQuestionAnswerPair question, String fullName) {
		super(toId, work, question, fullName);

		setNotificationType(new NotificationType(NotificationType.RESOURCE_WORK_QUESTION));
		this.type = work.isWorkBundle() ? Constants.WORK_TYPE_BUNDLE : Constants.WORK_TYPE_ASSIGNMENT;
	}

	public String getType() {
		return type;
	}
}