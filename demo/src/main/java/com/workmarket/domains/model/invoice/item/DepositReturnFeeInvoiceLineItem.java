package com.workmarket.domains.model.invoice.item;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "depositReturnFeeInvoiceLineItem")
@DiscriminatorValue(InvoiceLineItem.DEPOSIT_RETURN_FEE)
@AuditChanges
public class DepositReturnFeeInvoiceLineItem extends InvoiceLineItem {

	private static final long serialVersionUID = 1L;

	public DepositReturnFeeInvoiceLineItem() {
		super();
	}

	@Override
	@Transient
	public String getType() {
		return DEPOSIT_RETURN_FEE;
	}
}
