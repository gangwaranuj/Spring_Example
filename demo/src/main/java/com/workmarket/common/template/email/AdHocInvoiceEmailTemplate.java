package com.workmarket.common.template.email;

import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class AdHocInvoiceEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -9207492864151344489L;
	private AdHocInvoice invoice;

	public AdHocInvoiceEmailTemplate(String toEmail, AdHocInvoice invoice) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toEmail);
		setReplyToType(ReplyToType.TRANSACTIONAL);
		this.invoice = invoice;
	}

	public AdHocInvoice getInvoice() {
		return invoice;
	}
}
