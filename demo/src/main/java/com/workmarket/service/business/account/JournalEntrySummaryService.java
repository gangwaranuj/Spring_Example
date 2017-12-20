package com.workmarket.service.business.account;

import com.workmarket.domains.model.account.AccountingCreditMemoSummaryDetail;
import com.workmarket.domains.model.account.AccountingEndOfYearTaxSummary;
import com.workmarket.domains.model.account.AccountingPricingServiceTypeSummary;
import com.workmarket.domains.model.account.AccountingSummary;
import com.workmarket.domains.model.account.AccountingSummaryDetail;
import com.workmarket.domains.model.account.CreditDebitRegisterTransactionsSummary;
import com.workmarket.domains.model.account.FastFundsReceivableSummaryDetail;
import com.workmarket.domains.model.account.OfflinePaymentSummary;

import java.util.Calendar;
import java.util.List;

/**
 * Author: rocio
 */
public interface JournalEntrySummaryService {

	AccountingSummary findSummary(long id);

	List<AccountingSummaryDetail> findMoneyOutSubscriptionSWFeesDetail(long accountSummaryId);

	List<AccountingSummaryDetail> findMoneyOutSubscriptionVORFeesDetail(long accountSummaryId);

	List<AccountingSummaryDetail> findMoneyOutSubscriptionNVORSoftwareFeesDetail(long accountSummaryId);

	List<AccountingSummary> findAllSummaries();

	Calendar findOrCreateStartFiscalYearForDate(Calendar date);

	Calendar findOrCreateStartFiscalYear(Integer year);

	Calendar findDateOfLastSummary();

	Calendar findPreviousRequestDateOfSummary(long summaryId);

	Calendar findRequestDateOfSummary(long summaryId);

	AccountingSummary saveSummary(AccountingSummary summary);

	void calculateMoneyIn(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateMoneyOut(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	@Deprecated
	void calculateTotalMoneyOnSystem(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateTotalMoneyOnSystem(AccountingSummary summary);

	void calculatePayPalWithdrawals(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateGCCWithdrawals(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateMoneyOutPayPalFees(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateMoneyOutWMToPayPalFees(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateTotalInApStatus(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateMoneyOutAdhocInvoicesFees(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateAdHocServiceFeesRevenue(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateAdHocServiceFeesReceivables(AccountingSummary summary, Calendar start, Calendar end);

	void calculateTotalCompletedAssignments(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateTotalEarnedForAssignments(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateReceivablesFees(AccountingPricingServiceTypeSummary summary, Calendar start, Calendar end);

	void calculateAccountingPricingServiceTypeSummaryThroughputValues(AccountingPricingServiceTypeSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateAccountingPricingServiceTypeSummaryMoneyOutValues(AccountingPricingServiceTypeSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateAccountingPricingServiceTypeSummaryRevenueValues(AccountingPricingServiceTypeSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateOfflinePayments(OfflinePaymentSummary accountingSummary, Calendar start, Calendar end, Calendar startFiscalYear);

	List<AccountingSummaryDetail> getOfflinePaymentDetails(long accountSummaryId, boolean subscription, boolean vor, boolean isYTD);

	void calculateAccountingPricingServiceTypeSummaryDeferredRevenueValues(AccountingPricingServiceTypeSummary summary, Calendar start, Calendar end);

	void calculateCreditTransactionsSummary(CreditDebitRegisterTransactionsSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	void calculateDebitTransactionsSummary(CreditDebitRegisterTransactionsSummary summary, Calendar start, Calendar end, Calendar startFiscalYear);

	List<AccountingSummaryDetail> getAccItemRevSubVorSw(Long accountSummaryId);

	List<AccountingSummaryDetail> getAccItemRevSubVorSwYTD(Long accountSummaryId);

	List<FastFundsReceivableSummaryDetail> getFastFundsReceivableSummaryDetails(Long accountSummaryId, boolean isYTD);

	List<AccountingSummaryDetail> getAccItemRevSubVorVor(Long accountSummaryId);

	List<AccountingSummaryDetail> getAccItemRevSubVorVorYTD(Long accountSummaryId);

	List<AccountingSummaryDetail> getAccItemRevSubNVor(Long accountSummaryId);

	List<AccountingSummaryDetail> getAccItemRevSubNVorYTD(Long accountSummaryId);

	List<AccountingEndOfYearTaxSummary> getEndOfYearTaxReport(Calendar start, Calendar end);

	List<AccountingCreditMemoSummaryDetail> getCreditMemoTransactionDetails(
		Long accountSummaryId, boolean ytd, Integer creditMemoTypeId, boolean filterByPaid, boolean paid);

	List<AccountingCreditMemoSummaryDetail> getCreditMemoTransactionDetails(
		Long accountSummaryId, boolean ytd, List<Integer> creditMemoTypeIds, boolean filterByPaid, boolean paid,
		boolean filterByVOR, boolean isVOR);

	List<AccountingCreditMemoSummaryDetail> getCreditMemoTransactions(Calendar startDate, Calendar endDate,
																	  List<Integer> creditMemoTypeIds, boolean filterByPaid, boolean paid, boolean filterByVOR, boolean isVOR);
}
