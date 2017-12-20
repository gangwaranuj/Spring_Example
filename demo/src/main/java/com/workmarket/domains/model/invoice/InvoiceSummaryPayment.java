package com.workmarket.domains.model.invoice;

import java.util.Calendar;

public class InvoiceSummaryPayment {

	private Long invoiceId;
	private Calendar paymentDate;
	private Long paidBy;

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Calendar getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Calendar paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Long getPaidBy() {
		return paidBy;
	}

	public void setPaidBy(Long paidBy) {
		this.paidBy = paidBy;
	}

}
