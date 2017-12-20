package com.workmarket.service.business.dto.invoice;

import com.workmarket.domains.model.invoice.SubscriptionInvoiceType;

/**
 * Author: rocio
 */
public class SubscriptionInvoiceDTO extends InvoiceDTO {

	private SubscriptionInvoiceType subscriptionInvoiceType;

	public SubscriptionInvoiceType getSubscriptionInvoiceType() {
		return subscriptionInvoiceType;
	}

	public void setSubscriptionInvoiceType(SubscriptionInvoiceType subscriptionInvoiceType) {
		this.subscriptionInvoiceType = subscriptionInvoiceType;
	}
}