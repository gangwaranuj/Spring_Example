package com.workmarket.common.template.email;

import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class InvoiceEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = 7100498762509899608L;
	private DateFormat dateFormat = new SimpleDateFormat(Constants.EMAIL_INVOICE_DATE_FORMAT);
	private Invoice invoice;
	private AccountStatementDetailRow invoiceDetail;

	public InvoiceEmailTemplate(String toEmail, Invoice invoice, AccountStatementDetailRow invoiceDetail) {

		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toEmail);
		dateFormat.setTimeZone(Constants.EST_TIME_ZONE);
		this.invoice = invoice;
		this.invoiceDetail = invoiceDetail;

		setReplyToType(ReplyToType.INVOICE);
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public AccountStatementDetailRow getInvoiceDetail() {
		return invoiceDetail;
	}

}
