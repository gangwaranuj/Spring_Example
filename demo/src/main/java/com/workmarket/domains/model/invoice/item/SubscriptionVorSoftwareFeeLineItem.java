package com.workmarket.domains.model.invoice.item;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.service.business.accountregister.CreditMemoType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Author: rocio
 */
@Entity(name = "subscriptionVorSoftwareFeeLineItem")
@DiscriminatorValue(InvoiceLineItem.SUBSCRIPTION_VOR_SOFTWARE_FEE_INVOICE_LINE_ITEM)
@AuditChanges
public class SubscriptionVorSoftwareFeeLineItem extends CreditMemoIssuableInvoiceLineItem {

	private static final long serialVersionUID = 1L;

	public SubscriptionVorSoftwareFeeLineItem() {
		super();
	}

	@Override
	@Transient
	public String getType() {
		return SUBSCRIPTION_VOR_SOFTWARE_FEE_INVOICE_LINE_ITEM;
	}

	@Override
	@Transient
	public CreditMemoType getCreditMemoType() {
		return CreditMemoType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT_CREDIT;
	}

	@Override
	@Transient
	public boolean isAdHocVORSoftwareFee() {
		return true;
	}

	@Override
	@Transient
	public InvoiceLineItemType getInvoiceLineItemType() {
		return InvoiceLineItemType.SUBSCRIPTION_SOFTWARE_FEE_VOR;
	}
}
