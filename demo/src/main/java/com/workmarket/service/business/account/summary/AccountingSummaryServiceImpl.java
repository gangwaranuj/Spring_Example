package com.workmarket.service.business.account.summary;

import com.workmarket.domains.model.account.AccountingPricingServiceTypeSummary;
import com.workmarket.domains.model.account.AccountingSummary;
import com.workmarket.domains.model.account.CreditDebitRegisterTransactionsSummary;
import com.workmarket.domains.model.account.OfflinePaymentSummary;
import com.workmarket.service.business.account.JournalEntrySummaryService;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class AccountingSummaryServiceImpl implements AccountingSummaryService {

	private static final Log logger = LogFactory.getLog(AccountingSummaryServiceImpl.class);
	@Autowired private JournalEntrySummaryService journalEntrySummaryService;

	@Override
	public AccountingSummary createNewSummary() {
		AccountingSummary summary = new AccountingSummary();
		Calendar end = Calendar.getInstance();
		Calendar start = journalEntrySummaryService.findDateOfLastSummary();
		Calendar startFiscalYear = journalEntrySummaryService.findOrCreateStartFiscalYearForDate(Calendar.getInstance());

		logger.debug("Start historical: " + DateUtilities.format("MM/dd/yyyy hh:mm:ss", startFiscalYear));
		logger.debug("End historical: " + DateUtilities.format("MM/dd/yyyy  hh:mm:ss", end));

		if (start == null) {
			start = startFiscalYear;
		}

		summary.setRequestDate(end);
		summary.setPreviousRequestDate(start);

		/**
		 * IMPORTANT:
		 * Total Money on System, Receivables
		 * and Subscription Deferred Revenue don't reset every year. Use EPOCH.
		 */

		//Money In
		journalEntrySummaryService.calculateMoneyIn(summary, start, end, startFiscalYear);
		//Money Out
		journalEntrySummaryService.calculateMoneyOut(summary, start, end, startFiscalYear);

		//Completed and Earned
		journalEntrySummaryService.calculateTotalCompletedAssignments(summary, start, end, startFiscalYear);
		journalEntrySummaryService.calculateTotalEarnedForAssignments(summary, start, end, startFiscalYear);

		//PayPal withdrawals
		journalEntrySummaryService.calculatePayPalWithdrawals(summary, start, end, startFiscalYear);

		//GCC Withdrawals
		journalEntrySummaryService.calculateGCCWithdrawals(summary,start,end,startFiscalYear);


		//PayPal fee charged to the users
		journalEntrySummaryService.calculateMoneyOutPayPalFees(summary, start, end, startFiscalYear);

		//Fees paid to PayPal by WM
		journalEntrySummaryService.calculateMoneyOutWMToPayPalFees(summary, start, end, startFiscalYear);

		journalEntrySummaryService.calculateTotalInApStatus(summary, start, end, startFiscalYear);

		//Ad-hoc invoices money out
		journalEntrySummaryService.calculateMoneyOutAdhocInvoicesFees(summary, start, end, startFiscalYear);

		//Ad-hoc invoices revenue
		journalEntrySummaryService.calculateAdHocServiceFeesRevenue(summary, start, end, startFiscalYear);

		//Receivables
		journalEntrySummaryService.calculateAdHocServiceFeesReceivables(summary, start, end);

		AccountingPricingServiceTypeSummary accountingSummary = new AccountingPricingServiceTypeSummary();
		journalEntrySummaryService.calculateReceivablesFees(accountingSummary, start, end);

		//Throughput
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryThroughputValues(accountingSummary, start, end, startFiscalYear);
		//Money Out
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryMoneyOutValues(accountingSummary, start, end, startFiscalYear);
		//Revenue
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryRevenueValues(accountingSummary, start, end, startFiscalYear);
		//Deferred Revenue
		journalEntrySummaryService.calculateAccountingPricingServiceTypeSummaryDeferredRevenueValues(accountingSummary, start, end);
		summary.setAccountingPricingServiceTypeSummary(accountingSummary);

		CreditDebitRegisterTransactionsSummary creditDebitRegisterTransactionsSummary = new CreditDebitRegisterTransactionsSummary();
		journalEntrySummaryService.calculateCreditTransactionsSummary(creditDebitRegisterTransactionsSummary, start, end, startFiscalYear);
		journalEntrySummaryService.calculateDebitTransactionsSummary(creditDebitRegisterTransactionsSummary, start, end, startFiscalYear);
		summary.setCreditDebitRegisterTransactionsSummary(creditDebitRegisterTransactionsSummary);

		//Total money on system
		journalEntrySummaryService.calculateTotalMoneyOnSystem(summary);

		OfflinePaymentSummary offlinePaymentSummary = new OfflinePaymentSummary();
		journalEntrySummaryService.calculateOfflinePayments(offlinePaymentSummary, start, end, startFiscalYear);
		summary.setOfflinePaymentSummary(offlinePaymentSummary);

		journalEntrySummaryService.saveSummary(summary);
		return summary;
	}
}
