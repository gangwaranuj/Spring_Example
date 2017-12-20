package com.workmarket.common.template.email;

import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class SubscriptionInvoiceEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -9207492864151344489L;
	private SubscriptionInvoice invoice;

	public SubscriptionInvoiceEmailTemplate(String toEmail, SubscriptionInvoice invoice) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toEmail);
		setReplyToType(ReplyToType.TRANSACTIONAL);
		this.invoice = invoice;
	}

	public SubscriptionInvoice getInvoice() {
		return invoice;
	}
}
