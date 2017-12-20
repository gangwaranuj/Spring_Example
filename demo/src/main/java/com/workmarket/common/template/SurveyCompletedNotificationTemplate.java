package com.workmarket.common.template;

import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.notification.NotificationType;

public class SurveyCompletedNotificationTemplate extends AbstractSurveyAttemptNotificationTemplate{
	private static final long serialVersionUID = 1L;

	public SurveyCompletedNotificationTemplate(Long fromId, Long toId,
			Attempt attempt) {
		super(fromId, toId, new NotificationType(NotificationType.SURVEY_ATTEMPT_COMPLETED), attempt);
	}

}