package com.workmarket.domains.model.invoice.item;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.service.business.accountregister.CreditMemoType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Author: rocio
 */
@Entity(name = "subscriptionDiscountLineItem")
@DiscriminatorValue(InvoiceLineItem.SUBSCRIPTION_DISCOUNT_INVOICE_LINE_ITEM)
@AuditChanges
public class SubscriptionDiscountLineItem extends CreditMemoIssuableInvoiceLineItem {

	private static final long serialVersionUID = 1L;

	public SubscriptionDiscountLineItem() {
		super();
	}

	@Override
	@Transient
	public String getType() {
		return SUBSCRIPTION_DISCOUNT_INVOICE_LINE_ITEM;
	}

	@Override
	@Transient
	public CreditMemoType getCreditMemoType() {
		return CreditMemoType.SUBSCRIPTION_DISCOUNT_CREDIT;
	}

	@Override
	@Transient
	public InvoiceLineItemType getInvoiceLineItemType() {
		return InvoiceLineItemType.SUBSCRIPTION_DISCOUNT;
	}
}
