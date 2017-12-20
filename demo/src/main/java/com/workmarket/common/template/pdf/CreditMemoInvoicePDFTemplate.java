package com.workmarket.common.template.pdf;

import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.CreditMemo;

import java.math.BigDecimal;

public class CreditMemoInvoicePDFTemplate extends InvoicePDFTemplate {

	private static final long serialVersionUID = 6902159133339828175L;
	private CreditMemo invoice;
	private AbstractInvoice originalInvoice;

	public CreditMemoInvoicePDFTemplate(CreditMemo invoice) {
		super();
		this.invoice = invoice;
		if (invoice != null) {
			setOutputFileName("Invoice_" + invoice.getInvoiceNumber());
			this.originalInvoice = invoice.getCreditMemoAudit().getServiceInvoice();
		}
	}

	public CreditMemo getInvoice() {
		return invoice;
	}

	public AbstractInvoice getOriginalInvoice(){return this.originalInvoice;}

	public BigDecimal getTotal(){
		BigDecimal total = BigDecimal.ZERO;
		if(this.invoice.getBalance().subtract(this.originalInvoice.getBalance()).equals(BigDecimal.ZERO))
			return total;
		else
			return this.originalInvoice.getBalance().subtract(this.invoice.getBalance());
	}

	public String getCompanyFormattedAddress() {
		return super.getCompanyFormattedAddress(invoice);
	}

}
