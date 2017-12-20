package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoicePagination;
import com.workmarket.domains.model.invoice.InvoiceSummary;

import java.util.Calendar;
import java.util.List;

public interface InvoiceDAO extends AbstractInvoiceDAO<Invoice> {

	InvoicePagination findAllByCompanyId(long companyId, InvoicePagination pagination);

	List<Integer> findAutoPayInvoices(java.util.Date dueDate, String invoiceStatusType);

	InvoiceSummary findInvoiceSummaryByInvoiceBundledId(long childInvoiceId);

	List<Long> findAllNonFastFundedAndDueInvoiceIdsToUser(Calendar dueDateFrom, long userId);

	Calendar findFastFundedOnDateForWorkResource(long activeWorkResourceId);

	boolean isCreditMemoIssuable(long invoiceId);
}
