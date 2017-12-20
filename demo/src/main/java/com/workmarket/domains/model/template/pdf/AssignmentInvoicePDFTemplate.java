package com.workmarket.domains.model.template.pdf;

import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.utility.DateUtilities;

/**
 * Created by nick on 4/17/13 5:29 PM
 */
public class AssignmentInvoicePDFTemplate extends PDFTemplate {

	private AccountStatementDetailRow invoice;

	public AssignmentInvoicePDFTemplate(AccountStatementDetailRow invoice) {
		super();
		setOutputFileName("Invoice_" + invoice.getInvoiceNumber());
		this.invoice = invoice;
	}

	public AccountStatementDetailRow getInvoice() {
		return invoice;
	}

	public String getInvoiceCreatedDate() {
		return DateUtilities.format("MM/dd/YY", invoice.getInvoiceCreatedDate());
	}

	public String getInvoiceDueDate() {
		// invoice time zone comes from work, use work time zone to show correct date on invoice email
		return DateUtilities.format("MM/dd/YY", invoice.getInvoiceDueDate(), invoice.getTimeZoneId());
	}

	public String getInvoiceVoidDate() {
		return DateUtilities.format("MM/dd/YY", invoice.getInvoiceVoidDate());
	}

	public String getInvoicePaymentDate() {
		return DateUtilities.format("MM/dd/YY", invoice.getInvoicePaymentDate());
	}

	public String getWorkCloseDate() {
		return DateUtilities.format("MM/dd/YY", invoice.getWorkCloseDate());
	}
}
