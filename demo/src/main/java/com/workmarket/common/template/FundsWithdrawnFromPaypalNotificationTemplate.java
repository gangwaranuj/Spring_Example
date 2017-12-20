package com.workmarket.common.template;

import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class FundsWithdrawnFromPaypalNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 7712947857950272832L;
	private RegisterTransaction transaction;

	public FundsWithdrawnFromPaypalNotificationTemplate(Long toId, RegisterTransaction transaction) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.MONEY_WITHDRAWN), ReplyToType.TRANSACTIONAL);
		this.transaction = transaction;
	}

	public RegisterTransaction getTransaction() {
		return transaction;
	}
}
