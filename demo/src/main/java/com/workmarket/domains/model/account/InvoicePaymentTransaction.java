package com.workmarket.domains.model.account;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "invoicePaymentTransaction")
@Table(name = "invoice_payment_transaction")
@AuditChanges
public class InvoicePaymentTransaction extends RegisterTransaction {

	private static final long serialVersionUID = -1519173533063916999L;
	private AbstractInvoice invoice;

	@ManyToOne
	@JoinColumn(name = "invoice_id", referencedColumnName = "id", nullable = false, updatable = false)
	public AbstractInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(AbstractInvoice invoice) {
		this.invoice = invoice;
	}
}
