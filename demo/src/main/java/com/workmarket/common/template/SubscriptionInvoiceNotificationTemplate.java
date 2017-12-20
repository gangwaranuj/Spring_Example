package com.workmarket.common.template;

import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class SubscriptionInvoiceNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 1L;
	private SubscriptionInvoice invoice;

	public SubscriptionInvoiceNotificationTemplate(Long toId, SubscriptionInvoice invoice) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.SUBSCRIPTION_REMINDER), ReplyToType.TRANSACTIONAL);
		this.invoice = invoice;
	}

	public SubscriptionInvoice getInvoice() {
		return invoice;
	}
}
