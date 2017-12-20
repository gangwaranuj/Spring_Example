package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class ScreeningStatusNotificationTemplate extends NotificationTemplate {
	/**
	 *
	 */
	private static final long serialVersionUID = -8104319868557981830L;
	private Screening screening;

	public ScreeningStatusNotificationTemplate(Long toId, Screening screening) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.MISC), ReplyToType.TRANSACTIONAL);
		this.screening = screening;
	}

	public Screening getScreening() {
		return screening;
	}
}
