package com.workmarket.common.template;

import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class AdHocInvoiceNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 5606753615038573019L;

	private AdHocInvoice invoice;

	public AdHocInvoiceNotificationTemplate(Long toId, AdHocInvoice invoice) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.SUBSCRIPTION_REMINDER), ReplyToType.TRANSACTIONAL);
		this.invoice = invoice;
	}

	public AdHocInvoice getInvoice() {
		return invoice;
	}

}
