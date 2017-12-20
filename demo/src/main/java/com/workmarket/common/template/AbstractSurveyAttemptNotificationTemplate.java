package com.workmarket.common.template;

import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class AbstractSurveyAttemptNotificationTemplate extends NotificationTemplate{
	private static final long serialVersionUID = 1L;
	
	private Attempt attempt;

	public AbstractSurveyAttemptNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, Attempt attempt) {
		super(fromId, toId, notificationType, ReplyToType.TRANSACTIONAL_FROM_USER);
		this.attempt = attempt;
	}

	public Attempt getAttempt() {
		return attempt;
	}
}