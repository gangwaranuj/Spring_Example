package com.workmarket.common.template;

import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.notification.NotificationType;

public class AssessmentGradePendingNotificationTemplate extends AbstractAssessmentAttemptNotificationTemplate {
	private static final long serialVersionUID = 2407700696313386367L;

	public AssessmentGradePendingNotificationTemplate(Long fromId, Long toId, Attempt attempt) {
		super(fromId, toId, new NotificationType(NotificationType.ASSESSMENT_ATTEMPT_GRADE_PENDING), attempt);
	}
}