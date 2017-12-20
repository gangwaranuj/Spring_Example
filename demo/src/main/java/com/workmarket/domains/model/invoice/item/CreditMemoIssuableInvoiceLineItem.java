package com.workmarket.domains.model.invoice.item;


import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.service.business.accountregister.CreditMemoType;

public abstract class CreditMemoIssuableInvoiceLineItem extends InvoiceLineItem {

	public CreditMemoIssuableInvoiceLineItem() {
		super();
	}

	public CreditMemoIssuableInvoiceLineItem(AbstractServiceInvoice invoice) {
		super(invoice);
	}

	public abstract CreditMemoType getCreditMemoType();

	public boolean isAdHocVORSoftwareFee() {
		return false;
	}

	public abstract InvoiceLineItemType getInvoiceLineItemType();
}
