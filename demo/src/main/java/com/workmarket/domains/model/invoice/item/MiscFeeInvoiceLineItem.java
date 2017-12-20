package com.workmarket.domains.model.invoice.item;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.service.business.accountregister.CreditMemoType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "miscFeeInvoiceLineItem")
@DiscriminatorValue(InvoiceLineItem.MISC_FEE)
@AuditChanges
public class MiscFeeInvoiceLineItem extends CreditMemoIssuableInvoiceLineItem {

	private static final long serialVersionUID = 1L;

	public MiscFeeInvoiceLineItem() {
		super();
	}

	@Override
	@Transient
	public CreditMemoType getCreditMemoType() {
		return CreditMemoType.MISC_CREDIT;
	}

	@Override
	@Transient
	public InvoiceLineItemType getInvoiceLineItemType() {
		return InvoiceLineItemType.MISC_FEE;
	}

	@Override
	@Transient
	public String getType() {
		return MISC_FEE;
	}
}
