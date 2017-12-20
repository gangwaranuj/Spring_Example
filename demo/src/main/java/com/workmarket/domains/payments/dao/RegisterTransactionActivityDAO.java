package com.workmarket.domains.payments.dao;

import java.math.BigDecimal;
import java.util.Calendar;
import com.workmarket.domains.model.account.AccountTransactionReportRowPagination;
import com.workmarket.domains.model.account.RegisterTransactionActivityPagination;


public interface RegisterTransactionActivityDAO {

	RegisterTransactionActivityPagination getLedgerForCompany(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination);

	RegisterTransactionActivityPagination getOfflineLedgerForCompany(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination);

	RegisterTransactionActivityPagination getPendingTransactions(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination);

	RegisterTransactionActivityPagination getAccountRegisterTransactionReport(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination);

	AccountTransactionReportRowPagination findFundingTransactionsByTransactionDate(Calendar fromDate, Calendar toDate, AccountTransactionReportRowPagination pagination);

	BigDecimal getBalanceFromLedger(Long companyId, Long accountRegisterId, RegisterTransactionActivityPagination pagination);
}
