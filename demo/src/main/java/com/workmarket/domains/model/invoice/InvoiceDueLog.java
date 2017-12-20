package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "invoiceDueLog")
@Table(name = "invoice_due_log")
@AuditChanges
public class InvoiceDueLog extends AuditedEntity {

	private static final long serialVersionUID = 7018598094596763762L;

	private Long invoiceId;

	public InvoiceDueLog() {
	}

	public InvoiceDueLog(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Column(name = "invoice_id", nullable = false)
	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}
}
