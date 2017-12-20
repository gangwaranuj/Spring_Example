package com.workmarket.domains.model.invoice.item;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "latePaymentFeeInvoiceLineItem")
@DiscriminatorValue(InvoiceLineItem.LATE_PAYMENT_FEE)
@AuditChanges
public class LatePaymentFeeInvoiceLineItem extends InvoiceLineItem {

	private static final long serialVersionUID = 1L;

	public LatePaymentFeeInvoiceLineItem() {
		super();
	}

	@Override
	@Transient
	public String getType() {
		return LATE_PAYMENT_FEE;
	}
}
