package com.workmarket.common.template;

import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;
import org.apache.commons.lang3.StringEscapeUtils;

public class AbstractAssessmentAttemptNotificationTemplate extends NotificationTemplate {
	private static final long serialVersionUID = 2407700696313386367L;
	
	private Attempt attempt;
	private String assessmentName;

	public AbstractAssessmentAttemptNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, Attempt attempt) {
		super(fromId, toId, notificationType, ReplyToType.TRANSACTIONAL_FROM_USER);
		this.attempt = attempt;
		this.assessmentName = StringEscapeUtils.unescapeHtml4(attempt.getAssessment().getName());
	}

	public Attempt getAttempt() {
		return attempt;
	}

	public String getAssessmentName() {
		return assessmentName;
	}
}