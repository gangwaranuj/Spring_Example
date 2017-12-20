package com.workmarket.common.template;

import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class CreditCardReceiptNotificationTemplate extends NotificationTemplate {
	private static final long serialVersionUID = -1952047028210956570L;

	private CreditCardTransaction creditCardTransaction;

	public CreditCardReceiptNotificationTemplate(Long toId, CreditCardTransaction creditCardTransaction) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId,
			new NotificationType(NotificationType.MONEY_CREDIT_CARD_RECEIPT), ReplyToType.TRANSACTIONAL);
		this.creditCardTransaction = creditCardTransaction;
	}

	public CreditCardTransaction getCreditCardTransaction() {
		return creditCardTransaction;
	}
}
