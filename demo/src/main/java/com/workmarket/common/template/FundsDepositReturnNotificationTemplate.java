package com.workmarket.common.template;

import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class FundsDepositReturnNotificationTemplate extends NotificationTemplate {
	private static final long serialVersionUID = 4724355431525970302L;

	private RegisterTransaction transaction;

	public FundsDepositReturnNotificationTemplate(Long toId, RegisterTransaction transaction) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.MONEY_DEPOSITED), ReplyToType.TRANSACTIONAL);
		this.transaction = transaction;
	}

	public RegisterTransaction getTransaction() {
		return transaction;
	}

}
