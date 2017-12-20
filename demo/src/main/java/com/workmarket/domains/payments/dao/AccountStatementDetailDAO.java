package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.model.account.AccountTransactionReportRowPagination;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.data.report.work.AccountStatementFilters;

import java.math.BigDecimal;
import java.util.Calendar;

public interface AccountStatementDetailDAO {
	
	AccountStatementDetailPagination findInvoices(Long userId, Long companyId, AccountStatementDetailPagination pagination, AccountStatementFilters accountStatementFilters, boolean isAdminOrController);
	AccountStatementDetailPagination findInvoicesWithBundledInvoices(Long userId, Long companyId, AccountStatementDetailPagination pagination, AccountStatementFilters accountStatementFilters, boolean isAdminOrController);
	AccountStatementDetailPagination findBundledInvoices(Long userId, Long companyId, AccountStatementDetailPagination pagination, AccountStatementFilters accountStatementFilters, boolean isAdminOrController);

	BigDecimal sumTotalPastDue(Long userId, Long companyId, boolean isAdminOrController, boolean isPayables);

	BigDecimal sumTotalUpcomingDue(Long userId, Long companyId, boolean isAdminOrController, boolean isPayables);

	BigDecimal sumTotalUpcomingDueIn24Hours(Long userId, Long companyId, boolean isAdminOrController, boolean isPayables);

	BigDecimal sumTotalPaid(Long userId, Long companyId, boolean isAdminOrController, boolean isPayables, DateFilter dateFilter);

	AccountStatementDetailRow findAccountStatementDetailByInvoiceId(Long invoiceId, Long userId, Long companyId, boolean isAdminOrController);

	AccountTransactionReportRowPagination getPaymentPendingInvoicesRunningTotalsByCompany(Calendar fromDueDate, Calendar toDueDate, AccountTransactionReportRowPagination pagination);
}
