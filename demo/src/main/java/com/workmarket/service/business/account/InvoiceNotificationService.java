package com.workmarket.service.business.account;

import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.wrapper.InvoiceResponse;

/**
 * Created by nick on 4/16/13 5:42 PM
 *
 * TODO move all PDF generation/emailing stuff from BillingService to here
 */
public interface InvoiceNotificationService {

	public InvoiceResponse sendInvoicePdfToAutoInvoiceEnabledUsersForWork(Work work, AccountStatementDetailRow invoiceDetail);

	public InvoiceResponse sendInvoicePdfToSubscribedUsersForWork(Work work, AccountStatementDetailRow invoiceDetail);

	public InvoiceResponse sendInvoiceToUsers(Work work, AccountStatementDetailRow invoiceDetail);

}
