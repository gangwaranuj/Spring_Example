package com.workmarket.common.template;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class WelcomeEmployeeWorkerNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = -3022683791449333631L;
	private String companyName;

	public WelcomeEmployeeWorkerNotificationTemplate(Long toId, String companyName) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.MISC), ReplyToType.TRANSACTIONAL);
		this.companyName = companyName;
	}

	public String getCompanyName() {
		return companyName;
	}
}
