package com.workmarket.common.template;

import com.workmarket.domains.model.assessment.WorkScopedAttempt;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;

public class WorkSurveyCompletedNotificationTemplate extends AbstractWorkNotificationTemplate {
	private static final long serialVersionUID = 3426044132190718377L;

	private WorkScopedAttempt attempt;
	
	public WorkSurveyCompletedNotificationTemplate(Long fromId, Long toId, WorkScopedAttempt attempt, Work work) {
		super(fromId, toId, new NotificationType(NotificationType.WORK_SURVEY_COMPLETED), ReplyToType.TRANSACTIONAL, work);
		this.attempt = attempt;
	}

	public WorkScopedAttempt getAttempt() {
		return attempt;
	}
}