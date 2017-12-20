package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class WelcomeExclusiveWorkerNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 6633487994959740107L;
	private String companyName;
	private Boolean hasOfflinePayment;

	public WelcomeExclusiveWorkerNotificationTemplate(Long toId, String companyName, Boolean hasOfflinePayment) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.MISC), ReplyToType.TRANSACTIONAL);
		this.companyName = companyName;
		this.hasOfflinePayment = hasOfflinePayment;
	}

	public Boolean getHasOfflinePayment() {
		return hasOfflinePayment;
	}

	public String getCompanyName() {
		return companyName;
	}

}
