package com.workmarket.common.template.pdf;

import com.workmarket.domains.model.invoice.AdHocInvoice;

public class AdHocInvoicePDFTemplate extends InvoicePDFTemplate {

	private static final long serialVersionUID = 6902159133339828174L;
	private AdHocInvoice invoice;

	public AdHocInvoicePDFTemplate(AdHocInvoice invoice) {
		super();
		this.invoice = invoice;
		if (invoice != null) {
			setOutputFileName("Invoice_" + invoice.getInvoiceNumber());
		}
	}

	public AdHocInvoice getInvoice() {
		return invoice;
	}

	public String getCompanyFormattedAddress() {
		return super.getCompanyFormattedAddress(invoice);
	}

}
