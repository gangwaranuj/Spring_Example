package com.workmarket.common.template;

import java.math.BigDecimal;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class LockedCompanyAccountNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 606544552479546606L;

	private BigDecimal        pastDuePayables;

	public LockedCompanyAccountNotificationTemplate(Long toId, BigDecimal pastDuePayables) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORKMARKET_MESSAGE), ReplyToType.TRANSACTIONAL);
		setCcEmail(Constants.EMAIL_CLIENT_SERVICES);
		this.pastDuePayables = pastDuePayables;
	}

	public BigDecimal getPastDuePayables() {
		return pastDuePayables;
	}

	public void setPastDuePayables(BigDecimal pastDuePayables) {
		this.pastDuePayables = pastDuePayables;
	}
}
