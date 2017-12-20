package com.workmarket.domains.model.invoice.item;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "withdrawalReturnFeeInvoiceLineItem")
@DiscriminatorValue(InvoiceLineItem.WITHDRAWAL_RETURN_FEE)
@AuditChanges
public class WithdrawalReturnFeeInvoiceLineItem extends InvoiceLineItem {

	private static final long serialVersionUID = 1L;

	public WithdrawalReturnFeeInvoiceLineItem() {
		super();
	}

	@Override
	@Transient
	public String getType() {
		return WITHDRAWAL_RETURN_FEE;
	}
}
