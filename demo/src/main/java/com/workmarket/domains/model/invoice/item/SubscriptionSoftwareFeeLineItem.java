package com.workmarket.domains.model.invoice.item;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.service.business.accountregister.CreditMemoType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Author: rocio
 */
@Entity(name = "subscriptionSoftwareFeeLineItem")
@DiscriminatorValue(InvoiceLineItem.SUBSCRIPTION_SOFTWARE_FEE_INVOICE_LINE_ITEM)
@AuditChanges
public class SubscriptionSoftwareFeeLineItem extends CreditMemoIssuableInvoiceLineItem {

	private static final long serialVersionUID = 1L;

	public SubscriptionSoftwareFeeLineItem() {
		super();
	}

	public SubscriptionSoftwareFeeLineItem(AbstractServiceInvoice invoice) {
		super(invoice);
	}

	@Override
	@Transient
	public String getType() {
		return SUBSCRIPTION_SOFTWARE_FEE_INVOICE_LINE_ITEM;
	}

	@Override
	@Transient
	public CreditMemoType getCreditMemoType() {
		return CreditMemoType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT_CREDIT;
	}

	@Override
	@Transient
	public InvoiceLineItemType getInvoiceLineItemType() {
		return InvoiceLineItemType.SUBSCRIPTION_SOFTWARE_FEE;
	}
}
