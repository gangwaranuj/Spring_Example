package com.workmarket.service.business.account;

import com.workmarket.dao.account.AccountingSummaryDAO;
import com.workmarket.dao.account.AccountingSummaryDetailDAO;
import com.workmarket.dao.account.FiscalYearDAO;
import com.workmarket.domains.model.account.AccountingCreditMemoSummaryDetail;
import com.workmarket.domains.model.account.AccountingEndOfYearTaxSummary;
import com.workmarket.domains.model.account.AccountingPricingServiceTypeSummary;
import com.workmarket.domains.model.account.AccountingSummary;
import com.workmarket.domains.model.account.AccountingSummaryDetail;
import com.workmarket.domains.model.account.CreditDebitRegisterTransactionsSummary;
import com.workmarket.domains.model.account.FastFundsReceivableSummaryDetail;
import com.workmarket.domains.model.account.FiscalYear;
import com.workmarket.domains.model.account.OfflinePaymentSummary;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.service.business.accountregister.CreditMemoRegisterTransaction;
import com.workmarket.service.business.accountregister.CreditMemoType;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Author: rocio
 */
@Service
public class JournalEntrySummaryServiceImpl implements JournalEntrySummaryService {

	@Autowired public AccountingSummaryDAO accountingSummaryDAO;
	@Autowired public AccountingSummaryDetailDAO accountingSummaryDetailDAO;
	@Autowired private FiscalYearDAO fiscalYearDAO;

	private final Calendar EPOCH = DateUtilities.getCalendarFromMillis(0L);
	private static final BigDecimal TRANSACTIONAL_VENDOR_OF_RECORD_SOFTWARE_FEE_PERCENTAGE = BigDecimal.valueOf(0.8);

	@Override
	public AccountingSummary findSummary(long id) {
		return accountingSummaryDAO.get(id);
	}

	@Override
	public List<AccountingSummaryDetail> findMoneyOutSubscriptionSWFeesDetail(long accountSummaryId) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}
		return accountingSummaryDetailDAO.getMoneyOutSubscriptionSoftwareFeesDetail(accountingSummary.getPreviousRequestDate(), accountingSummary.getRequestDate(), true);
	}

	@Override
	public List<AccountingSummaryDetail> findMoneyOutSubscriptionVORFeesDetail(long accountSummaryId) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}
		return accountingSummaryDetailDAO.getMoneyOutSubscriptionVORFeesDetail(accountingSummary.getPreviousRequestDate(), accountingSummary.getRequestDate());
	}

	@Override
	public List<AccountingEndOfYearTaxSummary> getEndOfYearTaxReport(Calendar start, Calendar end) {
		return accountingSummaryDetailDAO.getEndOfYearTaxReport(start, end);
	}

	@Override
	public List<AccountingSummaryDetail> findMoneyOutSubscriptionNVORSoftwareFeesDetail(long accountSummaryId) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}
		return accountingSummaryDetailDAO.getMoneyOutSubscriptionSoftwareFeesDetail(accountingSummary.getPreviousRequestDate(), accountingSummary.getRequestDate(), false);
	}

	@Override
	public List<AccountingSummary> findAllSummaries() {
		return accountingSummaryDAO.findAllAccountingSummaries();
	}

	@Override
	public Calendar findOrCreateStartFiscalYearForDate(Calendar date) {
		// this is the first day of the fiscal year
		// January 1st,
		Integer currentYear = date.get(Calendar.YEAR);
		return findOrCreateStartFiscalYear(currentYear);
	}

	@Override
	public Calendar findOrCreateStartFiscalYear(Integer year) {
		Calendar startFiscalYear = accountingSummaryDAO.findStartFiscalYearByYear(year);
		if (startFiscalYear == null) {
			Calendar lastAccountSummary = accountingSummaryDAO.findDateOfLastSummary();
			fiscalYearDAO.saveOrUpdate(new FiscalYear(lastAccountSummary, year));
			startFiscalYear = lastAccountSummary;
		}
		return startFiscalYear;
	}

	@Override
	public Calendar findDateOfLastSummary() {
		return accountingSummaryDAO.findDateOfLastSummary();
	}

	@Override
	public Calendar findPreviousRequestDateOfSummary(long summaryId) {
		return accountingSummaryDAO.findPreviousRequestDateOfSummary(summaryId);
	}

	@Override
	public Calendar findRequestDateOfSummary(long summaryId) {
		return accountingSummaryDAO.findRequestDateOfSummary(summaryId);
	}

	@Override
	public AccountingSummary saveSummary(AccountingSummary summary) {
		accountingSummaryDAO.saveOrUpdate(summary);
		return summary;
	}

	@Override
	public void calculateMoneyIn(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		summary.setMoneyInFastFunds(accountingSummaryDAO.calculateMoneyInFastFunds(start, end).abs());
		summary.setMoneyInChecks(accountingSummaryDAO.calculateMoneyInChecks(start, end).abs());
		summary.setMoneyInAch(accountingSummaryDAO.calculateMoneyInAch(start, end).abs());
		summary.setMoneyInWire(accountingSummaryDAO.calculateMoneyInWire(start, end).abs());
		summary.setMoneyInCreditCard(accountingSummaryDAO.calculateMoneyInCreditCard(start, end).abs());
		summary.setMoneyInSubscriptionCreditMemo(accountingSummaryDAO.calculateCreditMemoTotalByType(
			CreditMemoRegisterTransaction.CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS, start, end, true, true, false, false).abs());
		summary.setMoneyInProfServicesCreditMemo(accountingSummaryDAO.calculateCreditMemoTotalByType(
			CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal(), start, end, true, true).abs());

		//Historical
		summary.setMoneyInFastFundsHistorical(accountingSummaryDAO.calculateMoneyInFastFunds(startFiscalYear, end).abs());
		summary.setMoneyInChecksHistorical(accountingSummaryDAO.calculateMoneyInChecks(startFiscalYear, end).abs());
		summary.setMoneyInAchHistorical(accountingSummaryDAO.calculateMoneyInAch(startFiscalYear, end).abs());
		summary.setMoneyInWireHistorical(accountingSummaryDAO.calculateMoneyInWire(startFiscalYear, end).abs());
		summary.setMoneyInCreditCardHistorical(accountingSummaryDAO.calculateMoneyInCreditCard(startFiscalYear, end).abs());
		summary.setMoneyInSubscriptionCreditMemoHistorical(accountingSummaryDAO.calculateCreditMemoTotalByType(
			CreditMemoRegisterTransaction.CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS, startFiscalYear, end, true, true, false, false).abs());
		summary.setMoneyInProfServicesCreditMemoHistorical(accountingSummaryDAO.calculateCreditMemoTotalByType(
			CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal(), startFiscalYear, end, true, true).abs());
	}

	@Override
	public void calculateMoneyOut(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		summary.setMoneyOutWithdrawals(accountingSummaryDAO.calculateMoneyOutWithdrawals(start, end, true).abs());
		summary.setMoneyOutNonUSAWithdrawals(accountingSummaryDAO.calculateMoneyOutWithdrawals(start, end, false).abs());

		summary.setMoneyOutFees(accountingSummaryDAO.calculateMoneyOutFees(start, end).abs());
		summary.setMoneyOutCreditCardFees(accountingSummaryDAO.calculateMoneyOutCreditCardFees(start, end).abs());
		summary.setMoneyOutFastFundsReceivablePayments(accountingSummaryDAO.calculateTotalMoneyOutFastFundsReceivablePayments(start, end).abs());
		summary.setMoneyOutFastFundsFee(accountingSummaryDAO.calculateTotalMoneyOutFastFundsFees(start, end).abs());
		summary.setMoneyOutBackgroundChecks(accountingSummaryDAO.calculateMoneyOutBackgroundChecks(start, end).abs());
		summary.setMoneyOutDrugTests(accountingSummaryDAO.calculateMoneyOutDrugTests(start, end).abs());
		summary.setMoneyOutAchVerifications(accountingSummaryDAO.calculateMoneyOutAchVerifications(start, end).abs());
		summary.setMoneyOutDebitTransactions(accountingSummaryDAO.calculateTotalMoneyOutDebitTransactions(start, end).abs());
		summary.setMoneyOutCreditTransactions(accountingSummaryDAO.calculateTotalMoneyOutCreditTransactions(start, end).abs());

		//Historical
		summary.setMoneyOutWithdrawalsHistorical(accountingSummaryDAO.calculateMoneyOutWithdrawals(startFiscalYear, end, true).abs());
		summary.setMoneyOutNonUSAWithdrawalsHistorical(accountingSummaryDAO.calculateMoneyOutWithdrawals(startFiscalYear, end, false).abs());

		summary.setMoneyOutFeesHistorical(accountingSummaryDAO.calculateMoneyOutFees(startFiscalYear, end).abs());
		summary.setMoneyOutCreditCardFeesHistorical(accountingSummaryDAO.calculateMoneyOutCreditCardFees(startFiscalYear, end).abs());
		summary.setMoneyOutFastFundsReceivablePaymentsHistorical(accountingSummaryDAO.calculateTotalMoneyOutFastFundsReceivablePayments(startFiscalYear, end).abs());
		summary.setMoneyOutFastFundsFeeHistorical(accountingSummaryDAO.calculateTotalMoneyOutFastFundsFees(startFiscalYear, end).abs());
		summary.setMoneyOutBackgroundChecksHistorical(accountingSummaryDAO.calculateMoneyOutBackgroundChecks(startFiscalYear, end).abs());
		summary.setMoneyOutDrugTestsHistorical(accountingSummaryDAO.calculateMoneyOutDrugTests(startFiscalYear, end).abs());
		summary.setMoneyOutAchVerificationsHistorical(accountingSummaryDAO.calculateMoneyOutAchVerifications(startFiscalYear, end).abs());
		summary.setMoneyOutDebitTransactionsHistorical(accountingSummaryDAO.calculateTotalMoneyOutDebitTransactions(startFiscalYear, end).abs());
		summary.setMoneyOutCreditTransactionsHistorical(accountingSummaryDAO.calculateTotalMoneyOutCreditTransactions(startFiscalYear, end).abs());
	}

	@Override
	public void calculateTotalMoneyOnSystem(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		summary.setTotalMoneyOnSystem(accountingSummaryDAO.calculateTotalMoneyOnSystem(start, end));
		summary.setTotalMoneyOnSystemHistorical(accountingSummaryDAO.calculateTotalMoneyOnSystem(startFiscalYear, end));
	}

	@Override
	public void calculateTotalMoneyOnSystem(AccountingSummary summary) {
		Assert.notNull(summary);
		Assert.notNull(summary.getPreviousRequestDate());

		BigDecimal totalMoneyOnSystemDailyAmount = summary.calculateTotalMoneyOnSystem();
		summary.setTotalMoneyOnSystem(totalMoneyOnSystemDailyAmount);

		//Get the previous day
		AccountingSummary previousAccountingSummary = accountingSummaryDAO.findBy("requestDate", summary.getPreviousRequestDate());
		BigDecimal totalMoneyOnSystemDailyYTDPreviousDay = BigDecimal.ZERO;
		if (previousAccountingSummary != null) {
			totalMoneyOnSystemDailyYTDPreviousDay = previousAccountingSummary.getTotalMoneyOnSystemHistorical();
		}
		BigDecimal totalMoneyOnSystemYTDToday = totalMoneyOnSystemDailyYTDPreviousDay.add(totalMoneyOnSystemDailyAmount);
		summary.setTotalMoneyOnSystemHistorical(totalMoneyOnSystemYTDToday);
	}

	@Override
	public void calculatePayPalWithdrawals(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		summary.setMoneyOutPayPalWithdrawal(accountingSummaryDAO.calculateMoneyOutPayPalWithdrawal(start, end).abs());
		summary.setMoneyOutPayPalWithdrawalHistorical(accountingSummaryDAO.calculateMoneyOutPayPalWithdrawal(startFiscalYear, end).abs());
	}

	@Override
	public void calculateGCCWithdrawals(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		summary.setMoneyOutGCCWithdrawal(accountingSummaryDAO.calculateMoneyOutGCCWithdrawal(start, end).abs());
		summary.setMoneyOutGCCWithdrawalHistorical(accountingSummaryDAO.calculateMoneyOutGCCWithdrawal(startFiscalYear, end).abs());
	}

	@Override
	public void calculateMoneyOutPayPalFees(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		summary.setMoneyOutPayPalFees(accountingSummaryDAO.calculateMoneyOutPayPalFees(start, end).abs());
		summary.setMoneyOutPayPalFeesHistorical(accountingSummaryDAO.calculateMoneyOutPayPalFees(startFiscalYear, end).abs());
	}

	@Override
	public void calculateMoneyOutWMToPayPalFees(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		summary.setMoneyOutWMToPayPalFees(accountingSummaryDAO.calculateMoneyOutWorkMarketFeesPaidToPayPal(start, end).abs());
		summary.setMoneyOutWMToPayPalFeesHistorical(accountingSummaryDAO.calculateMoneyOutWorkMarketFeesPaidToPayPal(startFiscalYear, end).abs());
	}

	@Override
	public void calculateTotalInApStatus(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		summary.setTotalInApStatus(accountingSummaryDAO.calculateTotalInAPStatus(start, end).abs());
	}

	@Override
	public void calculateReceivablesFees(AccountingPricingServiceTypeSummary accountingSummary, Calendar start, Calendar end) {
		//Subscription invoices receivables (without add ons)
		BigDecimal feesIssuedToday = accountingSummaryDAO.calculateSubscriptionSoftwareAndVORFeesByIssueDate(start, end).abs();
		BigDecimal softwareFeesPaidToday = accountingSummaryDAO.calculateMoneyOutSubscriptionSoftwareFees(start, end).abs();
		BigDecimal vorFeesPaidToday = accountingSummaryDAO.calculateMoneyOutSubscriptionVORFees(start, end).abs();
		accountingSummary.setSubscriptionFeeReceivables(feesIssuedToday.subtract(softwareFeesPaidToday).subtract(vorFeesPaidToday));

		//Professional Services receivables
		feesIssuedToday = accountingSummaryDAO.calculateProfessionalServiceFeesByIssueDate(start, end).abs();
		BigDecimal feesPaidToday = accountingSummaryDAO.calculateMoneyOutProfessionalServicesFees(start, end).abs();
		accountingSummary.setProfessionalServiceFeeReceivables(feesIssuedToday.subtract(feesPaidToday));

		// Fast Funds Receivables
		BigDecimal fastFundReceivablesIssuedToday = accountingSummaryDAO.calculateFastFundsReceivablesByIssueDate(start, end).abs();
		BigDecimal fastFundReceivablesPaidToday = accountingSummaryDAO.calculateTotalMoneyOutFastFundsReceivablePayments(start, end).abs();
		accountingSummary.setFastFundsFeeReceivables(fastFundReceivablesIssuedToday.subtract(fastFundReceivablesPaidToday));

		// Credit Memos - unpaid invoices
		accountingSummary.setSubscriptionCreditMemoReceivables(accountingSummaryDAO.calculateCreditMemoTotalByType(
			CreditMemoRegisterTransaction.CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS, start, end, true, false, false, false).negate());
		accountingSummary.setProfServicesCreditMemoReceivables(accountingSummaryDAO.calculateCreditMemoTotalByType(
			CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal(), start, end, true, false).negate());

		//Historical
		accountingSummary.setSubscriptionFeeReceivablesHistorical(accountingSummaryDAO.calculatePendingPaymentSubscriptionSoftwareAndVORFees(EPOCH, end).abs());
		accountingSummary.setProfessionalServiceFeeReceivablesHistorical(accountingSummaryDAO.calculatePendingPaymentProfessionalServiceFees(EPOCH, end).abs());
		accountingSummary.setFastFundsFeeReceivablesHistorical(accountingSummaryDAO.calculatePendingPaymentFastFundReceivables(EPOCH, end).abs());
		accountingSummary.setSubscriptionCreditMemoReceivablesHistorical(accountingSummaryDAO.calculateCreditMemoTotalByType(
			CreditMemoRegisterTransaction.CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS, EPOCH, end, true, false, false, false).negate());
		accountingSummary.setProfServicesCreditMemoReceivablesHistorical(accountingSummaryDAO.calculateCreditMemoTotalByType(
			CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal(), EPOCH, end, true, false).negate());
	}

	@Override
	public void calculateAdHocServiceFeesReceivables(AccountingSummary accountingSummary, Calendar start, Calendar end) {
		//Ad-hoc service fees receivables
		BigDecimal feesIssuedToday = accountingSummaryDAO.calculateAdHocServiceFeesByIssueDate(start, end).abs();
		BigDecimal feesPaidToday = accountingSummaryDAO.calculateMoneyOutAdHocServiceFees(start, end).abs();
		accountingSummary.setAdHocServiceFeeReceivables(feesIssuedToday.subtract(feesPaidToday));

		//Historical
		accountingSummary.setAdHocServiceFeeReceivablesHistorical(accountingSummaryDAO.calculatePendingPaymentAdHocServiceFees(EPOCH, end).abs());
	}

	@Override
	public void calculateTotalCompletedAssignments(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		summary.setTotalCompletedAssignments(accountingSummaryDAO.calculateTotalCompletedAssignments(start, end).abs());
		summary.setTotalCompletedAssignmentsHistorical(accountingSummaryDAO.calculateTotalCompletedAssignments(startFiscalYear, end).abs());
	}

	@Override
	public void calculateTotalEarnedForAssignments(AccountingSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		summary.setTotalEarnedForAssignments(accountingSummaryDAO.calculateTotalEarnedForAssignments(start, end).abs());
		summary.setTotalEarnedForAssignmentsHistorical(accountingSummaryDAO.calculateTotalEarnedForAssignments(startFiscalYear, end).abs());
	}

	@Override
	public void calculateAdHocServiceFeesRevenue(AccountingSummary accountingSummary, Calendar start, Calendar end, Calendar startFiscalYear) {
		//Ad-hoc service fees receivables
		Map<String, Double> revenue = accountingSummaryDAO.calculateRevenueAdHocServiceFees(start, end);
		accountingSummary.setRevenueDepositReturnFee(BigDecimal.valueOf(MapUtils.getDouble(revenue, RegisterTransactionType.SERVICE_FEE_DEPOSIT_RETURN, 0.00)).abs());
		accountingSummary.setRevenueWithdrawalReturnFee(BigDecimal.valueOf(MapUtils.getDouble(revenue, RegisterTransactionType.SERVICE_FEE_WITHDRAWAL_RETURN, 0.00)).abs());
		accountingSummary.setRevenueLatePaymentFee(BigDecimal.valueOf(MapUtils.getDouble(revenue, RegisterTransactionType.SERVICE_FEE_LATE_PAYMENT, 0.00)).abs());
		accountingSummary.setRevenueMiscellaneousFee(BigDecimal.valueOf(MapUtils.getDouble(revenue, RegisterTransactionType.SERVICE_FEE_MISCELLANEOUS, 0.00)).abs());

		//Historical
		Map<String, Double> revenueHistorical = accountingSummaryDAO.calculateRevenueAdHocServiceFees(startFiscalYear, end);
		accountingSummary.setRevenueDepositReturnFeeHistorical(BigDecimal.valueOf(MapUtils.getDouble(revenueHistorical, RegisterTransactionType.SERVICE_FEE_DEPOSIT_RETURN, 0.00)).abs());
		accountingSummary.setRevenueWithdrawalReturnFeeHistorical(BigDecimal.valueOf(MapUtils.getDouble(revenueHistorical, RegisterTransactionType.SERVICE_FEE_WITHDRAWAL_RETURN, 0.00)).abs());
		accountingSummary.setRevenueLatePaymentFeeHistorical(BigDecimal.valueOf(MapUtils.getDouble(revenueHistorical, RegisterTransactionType.SERVICE_FEE_LATE_PAYMENT, 0.00)).abs());
		accountingSummary.setRevenueMiscellaneousFeeHistorical(BigDecimal.valueOf(MapUtils.getDouble(revenueHistorical, RegisterTransactionType.SERVICE_FEE_MISCELLANEOUS, 0.00)).abs());
	}

	@Override
	public void calculateMoneyOutAdhocInvoicesFees(AccountingSummary accountingSummary, Calendar start, Calendar end, Calendar startFiscalYear) {
		Map<String, Double> moneyOut = accountingSummaryDAO.calculateMoneyOutAdHocServiceFeesMap(start, end);
		accountingSummary.setMoneyOutDepositReturnFee(BigDecimal.valueOf(MapUtils.getDouble(moneyOut, RegisterTransactionType.SERVICE_FEE_DEPOSIT_RETURN, 0.00)).abs());
		accountingSummary.setMoneyOutWithdrawalReturnFee(BigDecimal.valueOf(MapUtils.getDouble(moneyOut, RegisterTransactionType.SERVICE_FEE_WITHDRAWAL_RETURN, 0.00)).abs());
		accountingSummary.setMoneyOutLatePaymentFee(BigDecimal.valueOf(MapUtils.getDouble(moneyOut, RegisterTransactionType.SERVICE_FEE_LATE_PAYMENT, 0.00)).abs());
		accountingSummary.setMoneyOutMiscellaneousFee(BigDecimal.valueOf(MapUtils.getDouble(moneyOut, RegisterTransactionType.SERVICE_FEE_MISCELLANEOUS, 0.00)).abs());

		Map<String, Double> moneyOutHistorical = accountingSummaryDAO.calculateMoneyOutAdHocServiceFeesMap(startFiscalYear, end);
		accountingSummary.setMoneyOutDepositReturnFeeHistorical(BigDecimal.valueOf(MapUtils.getDouble(moneyOutHistorical, RegisterTransactionType.SERVICE_FEE_DEPOSIT_RETURN, 0.00)).abs());
		accountingSummary.setMoneyOutWithdrawalReturnFeeHistorical(BigDecimal.valueOf(MapUtils.getDouble(moneyOutHistorical, RegisterTransactionType.SERVICE_FEE_WITHDRAWAL_RETURN, 0.00)).abs());
		accountingSummary.setMoneyOutLatePaymentFeeHistorical(BigDecimal.valueOf(MapUtils.getDouble(moneyOutHistorical, RegisterTransactionType.SERVICE_FEE_LATE_PAYMENT, 0.00)).abs());
		accountingSummary.setMoneyOutMiscellaneousFeeHistorical(BigDecimal.valueOf(MapUtils.getDouble(moneyOutHistorical, RegisterTransactionType.SERVICE_FEE_MISCELLANEOUS, 0.00)).abs());
	}

	@Override
	public void calculateAccountingPricingServiceTypeSummaryThroughputValues(AccountingPricingServiceTypeSummary accountingSummary, Calendar start, Calendar end, Calendar startFiscalYear) {
		//Throughput
		accountingSummary.setThroughputTransactionalNonVor(accountingSummaryDAO.calculateThroughputByServiceTypeAndPricingType(start, end,
				AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES, AccountPricingType.TRANSACTIONAL_PRICING_TYPE).abs());
		accountingSummary.setThroughputTransactionalVor(accountingSummaryDAO.calculateThroughputByServiceTypeAndPricingType(start, end,
				AccountServiceType.VOR_SERVICE_TYPES, AccountPricingType.TRANSACTIONAL_PRICING_TYPE).abs());

		accountingSummary.setThroughputSubscriptionVor(accountingSummaryDAO.calculateThroughputByServiceTypeAndPricingType(start, end,
				AccountServiceType.VOR_SERVICE_TYPES, AccountPricingType.SUBSCRIPTION_PRICING_TYPE).abs());
		accountingSummary.setThroughputSubscriptionNonVor(accountingSummaryDAO.calculateThroughputByServiceTypeAndPricingType(start, end,
				AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES, AccountPricingType.SUBSCRIPTION_PRICING_TYPE).abs());

		//Throughput historical
		accountingSummary.setThroughputTransactionalNonVorHistorical(accountingSummaryDAO.calculateThroughputByServiceTypeAndPricingType(startFiscalYear, end,
				AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES, AccountPricingType.TRANSACTIONAL_PRICING_TYPE).abs());
		accountingSummary.setThroughputTransactionalVorHistorical(accountingSummaryDAO.calculateThroughputByServiceTypeAndPricingType(startFiscalYear, end,
				AccountServiceType.VOR_SERVICE_TYPES, AccountPricingType.TRANSACTIONAL_PRICING_TYPE).abs());

		accountingSummary.setThroughputSubscriptionVorHistorical(accountingSummaryDAO.calculateThroughputByServiceTypeAndPricingType(startFiscalYear, end,
				AccountServiceType.VOR_SERVICE_TYPES, AccountPricingType.SUBSCRIPTION_PRICING_TYPE).abs());
		accountingSummary.setThroughputSubscriptionNonVorHistorical(accountingSummaryDAO.calculateThroughputByServiceTypeAndPricingType(startFiscalYear, end,
				AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES, AccountPricingType.SUBSCRIPTION_PRICING_TYPE).abs());
	}

	@Override
	public void calculateAccountingPricingServiceTypeSummaryMoneyOutValues(AccountingPricingServiceTypeSummary accountingSummary, Calendar start, Calendar end, Calendar startFiscalYear) {
		//Money Out Transactional Pricing
		BigDecimal totalMoneyOutTransactionalVorFee = accountingSummaryDAO.calculateMoneyOutTransactionalFeesByServiceType(start, end,
				AccountServiceType.VOR_SERVICE_TYPES).abs();

		BigDecimal moneyOutTransactionalVorSoftwareFee = BigDecimal.ZERO;
		BigDecimal moneyOutTransactionalVorVorFee = BigDecimal.ZERO;
		if (totalMoneyOutTransactionalVorFee != null && totalMoneyOutTransactionalVorFee.compareTo(BigDecimal.ZERO) > 0) {
			moneyOutTransactionalVorSoftwareFee = totalMoneyOutTransactionalVorFee.multiply(TRANSACTIONAL_VENDOR_OF_RECORD_SOFTWARE_FEE_PERCENTAGE);
			moneyOutTransactionalVorSoftwareFee = moneyOutTransactionalVorSoftwareFee.setScale(2, RoundingMode.HALF_UP);
			moneyOutTransactionalVorVorFee = totalMoneyOutTransactionalVorFee.subtract(moneyOutTransactionalVorSoftwareFee);
		}

		accountingSummary.setMoneyOutTransactionalVorSoftwareFee(moneyOutTransactionalVorSoftwareFee);
		accountingSummary.setMoneyOutTransactionalVorVorFee(moneyOutTransactionalVorVorFee);

		accountingSummary.setMoneyOutTransactionalNonVorSoftwareFee(accountingSummaryDAO.calculateMoneyOutTransactionalFeesByServiceType(start, end,
				AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES).abs());

		//Money Out Transactional Pricing Historical
		totalMoneyOutTransactionalVorFee = accountingSummaryDAO.calculateMoneyOutTransactionalFeesByServiceType(startFiscalYear, end,
				AccountServiceType.VOR_SERVICE_TYPES).abs();

		BigDecimal moneyOutTransactionalVorSoftwareFeeHistorical = BigDecimal.ZERO;
		BigDecimal moneyOutTransactionalVorVorFeeHistorical = BigDecimal.ZERO;
		if (totalMoneyOutTransactionalVorFee != null && totalMoneyOutTransactionalVorFee.compareTo(BigDecimal.ZERO) > 0) {
			moneyOutTransactionalVorSoftwareFeeHistorical = totalMoneyOutTransactionalVorFee.multiply(TRANSACTIONAL_VENDOR_OF_RECORD_SOFTWARE_FEE_PERCENTAGE);
			moneyOutTransactionalVorVorFeeHistorical = totalMoneyOutTransactionalVorFee.subtract(moneyOutTransactionalVorSoftwareFeeHistorical);
		}

		accountingSummary.setMoneyOutTransactionalVorSoftwareFeeHistorical(moneyOutTransactionalVorSoftwareFeeHistorical);
		accountingSummary.setMoneyOutTransactionalVorVorFeeHistorical(moneyOutTransactionalVorVorFeeHistorical);

		accountingSummary.setMoneyOutTransactionalNonVorSoftwareFeeHistorical(accountingSummaryDAO.calculateMoneyOutTransactionalFeesByServiceType(startFiscalYear, end,
				AccountServiceType.NON_VOR_AND_TAX_SERVICE_TYPES).abs());

		//Money Out Subscription Pricing
		accountingSummary.setMoneyOutSubscriptionVorSoftwareFee(accountingSummaryDAO.calculateMoneyOutSubscriptionSoftwareFees(start, end, true).abs());
		accountingSummary.setMoneyOutSubscriptionVorVorFee(accountingSummaryDAO.calculateMoneyOutSubscriptionVORFees(start, end).abs());
		accountingSummary.setMoneyOutSubscriptionNonVorSoftwareFee(accountingSummaryDAO.calculateMoneyOutSubscriptionSoftwareFees(start, end, false).abs());

		//Money Out Subscription Pricing Historical
		accountingSummary.setMoneyOutSubscriptionVorSoftwareFeeHistorical(accountingSummaryDAO.calculateMoneyOutSubscriptionSoftwareFees(startFiscalYear, end, true).abs());
		accountingSummary.setMoneyOutSubscriptionVorVorFeeHistorical(accountingSummaryDAO.calculateMoneyOutSubscriptionVORFees(startFiscalYear, end).abs());
		accountingSummary.setMoneyOutSubscriptionNonVorSoftwareFeeHistorical(accountingSummaryDAO.calculateMoneyOutSubscriptionSoftwareFees(startFiscalYear, end, false).abs());

		//Professional Services
		accountingSummary.setMoneyOutProfessionalServiceFee(accountingSummaryDAO.calculateMoneyOutProfessionalServicesFees(start, end).abs());
		accountingSummary.setMoneyOutProfessionalServiceFeeHistorical(accountingSummaryDAO.calculateMoneyOutProfessionalServicesFees(startFiscalYear, end).abs());
	}

	@Override
	public void calculateAccountingPricingServiceTypeSummaryRevenueValues(AccountingPricingServiceTypeSummary accountingSummary, Calendar start, Calendar end, Calendar startFiscalYear) {
		accountingSummary.setRevenueSubscriptionVorSoftwareFee(accountingSummaryDAO.calculateSubscriptionRevenueSoftwareFees(start, end, true).abs());
		accountingSummary.setRevenueSubscriptionVorVorFee(accountingSummaryDAO.calculateSubscriptionRevenueVORFees(start, end).abs());
		accountingSummary.setRevenueSubscriptionNonVorSoftwareFee(accountingSummaryDAO.calculateSubscriptionRevenueSoftwareFees(start, end, false).abs());
		accountingSummary.setRevenueProfessionalServiceFee(accountingSummaryDAO.calculateRevenueProfessionalServicesFees(start, end).abs());

		//Historical
		accountingSummary.setRevenueSubscriptionVorSoftwareFeeHistorical(accountingSummaryDAO.calculateSubscriptionRevenueSoftwareFees(startFiscalYear, end, true).abs());
		accountingSummary.setRevenueSubscriptionVorVorFeeHistorical(accountingSummaryDAO.calculateSubscriptionRevenueVORFees(startFiscalYear, end).abs());
		accountingSummary.setRevenueSubscriptionNonVorSoftwareFeeHistorical(accountingSummaryDAO.calculateSubscriptionRevenueSoftwareFees(startFiscalYear, end, false).abs());
		accountingSummary.setRevenueProfessionalServiceFeeHistorical(accountingSummaryDAO.calculateRevenueProfessionalServicesFees(startFiscalYear, end).abs());

		// Credit
		accountingSummary.setCreditSubscriptionVorSoftwareFee(
			accountingSummaryDAO.calculateCreditMemoTotalByType(
				CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, start, end, false, false, true,
				true).negate());
		accountingSummary.setCreditSubscriptionVorVorFee(
				accountingSummaryDAO.calculateCreditMemoTotalByType(
					CreditMemoType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT_CREDIT.ordinal(),
					start, end, false, false).negate());
		accountingSummary.setCreditSubscriptionNonVorSoftwareFee(
				accountingSummaryDAO.calculateCreditMemoTotalByType(
					CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, start, end, false, false, true,
					false).negate());
		accountingSummary.setCreditProfessionalServiceFee(
				accountingSummaryDAO.calculateCreditMemoTotalByType(
					CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal(), start, end,
					false, false).negate());

		// Credit - Historical
		accountingSummary.setCreditSubscriptionVorSoftwareFeeHistorical(
			accountingSummaryDAO.calculateCreditMemoTotalByType(
				CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, startFiscalYear, end, false, false,
				true, true).negate());
		accountingSummary.setCreditSubscriptionVorVorFeeHistorical(
			accountingSummaryDAO.calculateCreditMemoTotalByType(
				CreditMemoType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT_CREDIT.ordinal(),
				startFiscalYear, end, false, false).negate());
		accountingSummary.setCreditSubscriptionNonVorSoftwareFeeHistorical(
			accountingSummaryDAO.calculateCreditMemoTotalByType(
				CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, startFiscalYear, end, false, false,
				true, false).negate());
		accountingSummary.setCreditProfessionalServiceFeeHistorical(
			accountingSummaryDAO.calculateCreditMemoTotalByType(
				CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal(), startFiscalYear,
				end, false, false).negate());
	}

	@Override
	public void calculateOfflinePayments(OfflinePaymentSummary accountingSummary, Calendar start, Calendar end, Calendar startFiscalYear) {
		accountingSummary.setOfflineTransVor(accountingSummaryDAO.calculateOfflinePayments(start, end, false, true));
		accountingSummary.setOfflineTransVorHistorical(accountingSummaryDAO.calculateOfflinePayments(startFiscalYear, end, false, true));

		accountingSummary.setOfflineTransNvor(accountingSummaryDAO.calculateOfflinePayments(start, end, false, false));
		accountingSummary.setOfflineTransNvorHistorical(accountingSummaryDAO.calculateOfflinePayments(startFiscalYear, end, false, false));

		accountingSummary.setOfflineSubsVor(accountingSummaryDAO.calculateOfflinePayments(start, end, true, true));
		accountingSummary.setOfflineSubsVorHistorical(accountingSummaryDAO.calculateOfflinePayments(startFiscalYear, end, true, true));

		accountingSummary.setOfflineSubsNvor(accountingSummaryDAO.calculateOfflinePayments(start, end, true, false));
		accountingSummary.setOfflineSubsNvorHistorical(accountingSummaryDAO.calculateOfflinePayments(startFiscalYear, end, true, false));

	}

	@Override
	public List<AccountingSummaryDetail> getOfflinePaymentDetails(long accountSummaryId, boolean subscription, boolean vor, boolean isYTD) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}

		Calendar startDate = accountingSummary.getPreviousRequestDate();

		if (isYTD) {
			startDate = findOrCreateStartFiscalYearForDate(accountingSummary.getRequestDate());
		}
		return accountingSummaryDetailDAO.getOfflinePaymentsDetail(startDate, accountingSummary.getRequestDate(), subscription, vor);

	}

		@Override
	public void calculateAccountingPricingServiceTypeSummaryDeferredRevenueValues(AccountingPricingServiceTypeSummary accountingSummary, Calendar start, Calendar end) {
		BigDecimal defRevenueSubscriptionVorSoftwareFee = accountingSummaryDAO.calculateSubscriptionDeferredRevenueSoftwareFees(start, end, true).abs();
		BigDecimal defRevenueSubscriptionVorVorFee = accountingSummaryDAO.calculateSubscriptionDeferredRevenueVORFees(start, end).abs();
		BigDecimal defRevenueSubscriptionNonVorSoftwareFee = accountingSummaryDAO.calculateSubscriptionDeferredRevenueSoftwareFees(start, end, false).abs();
		BigDecimal defRevenueProfessionalServiceFee = accountingSummaryDAO.calculateDeferredRevenueProfessionalServicesFees(start, end).abs();

		//Subtract Non Immediate Revenue Recognized Today
		BigDecimal nonImmediateSubscriptionRevenueVorSoftwareFee = accountingSummaryDAO.calculateNonImmediateSubscriptionRevenueSoftwareFees(start, end, true);
		BigDecimal nonImmediateSubscriptionRevenueVorVorFee = accountingSummaryDAO.calculateNonImmediateSubscriptionRevenueVORFees(start, end);
		BigDecimal nonImmediateSubscriptionRevenueNonVorSoftwareFee = accountingSummaryDAO.calculateNonImmediateSubscriptionRevenueSoftwareFees(start, end, false);
		BigDecimal nonImmediateProfessionalServiceFees = accountingSummaryDAO.calculateNonImmediateRevenueProfessionalServicesFees(start, end);

		if (nonImmediateSubscriptionRevenueVorSoftwareFee != null) {
			defRevenueSubscriptionVorSoftwareFee = defRevenueSubscriptionVorSoftwareFee.subtract(nonImmediateSubscriptionRevenueVorSoftwareFee);
		}

		if (nonImmediateSubscriptionRevenueVorVorFee != null) {
			defRevenueSubscriptionVorVorFee = defRevenueSubscriptionVorVorFee.subtract(nonImmediateSubscriptionRevenueVorVorFee);
		}

		if (nonImmediateSubscriptionRevenueNonVorSoftwareFee != null) {
			defRevenueSubscriptionNonVorSoftwareFee = defRevenueSubscriptionNonVorSoftwareFee.subtract(nonImmediateSubscriptionRevenueNonVorSoftwareFee);
		}

		if (nonImmediateProfessionalServiceFees != null) {
			defRevenueProfessionalServiceFee = defRevenueProfessionalServiceFee.subtract(nonImmediateProfessionalServiceFees);
		}

		accountingSummary.setDefRevenueSubscriptionVorSoftwareFee(defRevenueSubscriptionVorSoftwareFee);
		accountingSummary.setDefRevenueSubscriptionVorVorFee(defRevenueSubscriptionVorVorFee);
		accountingSummary.setDefRevenueSubscriptionNonVorSoftwareFee(defRevenueSubscriptionNonVorSoftwareFee);
		accountingSummary.setDefRevenueProfessionalServiceFee(defRevenueProfessionalServiceFee);

		//Historical
		BigDecimal defRevenueSubscriptionVorSoftwareFeeHistorical = accountingSummaryDAO.calculateSubscriptionDeferredRevenueSoftwareFees(EPOCH, end, true).abs();
		BigDecimal defRevenueSubscriptionVorVorFeeHistorical = accountingSummaryDAO.calculateSubscriptionDeferredRevenueVORFees(EPOCH, end).abs();
		BigDecimal defRevenueSubscriptionNonVorSoftwareFeeHistorical = accountingSummaryDAO.calculateSubscriptionDeferredRevenueSoftwareFees(EPOCH, end, false).abs();
		BigDecimal defRevenueProfessionalServiceFeeHistorical = accountingSummaryDAO.calculateDeferredRevenueProfessionalServicesFees(EPOCH, end).abs();

		accountingSummary.setDefRevenueSubscriptionVorSoftwareFeeHistorical(defRevenueSubscriptionVorSoftwareFeeHistorical);
		accountingSummary.setDefRevenueSubscriptionVorVorFeeHistorical(defRevenueSubscriptionVorVorFeeHistorical);
		accountingSummary.setDefRevenueSubscriptionNonVorSoftwareFeeHistorical(defRevenueSubscriptionNonVorSoftwareFeeHistorical);
		accountingSummary.setDefRevenueProfessionalServiceFeeHistorical(defRevenueProfessionalServiceFeeHistorical);
	}

	@Override
	public void calculateCreditTransactionsSummary(CreditDebitRegisterTransactionsSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		Map<String, Double> credits = accountingSummaryDAO.calculateMoneyOutCreditTransactions(start, end);
		Map<String, Double> creditsHistorical = accountingSummaryDAO.calculateMoneyOutCreditTransactions(startFiscalYear, end);


		summary.setCreditAchWithdrawableReturn(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_ACH_WITHDRAWABLE_RETURN, 0.00)).abs());
		summary.setCreditAdvance(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_ADVANCE, 0.00)).abs());
		summary.setCreditAssignmentPaymentReversal(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_ASSIGNMENT_PAYMENT_REVERSAL, 0.00)).abs());
		summary.setCreditBackgroundCheckRefund(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_BACKGROUND_CHECK_REFUND, 0.00)).abs());
		summary.setCreditDrugTestRefund(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_DRUG_TEST_REFUND, 0.00)).abs());
		summary.setCreditFeeRefundNvor(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_FEE_REFUND_NVOR, 0.00)).abs());
		summary.setCreditGeneralRefund(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_GENERAL_REFUND, 0.00)).abs());
		summary.setCreditMarketingPayment(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_MARKETING_PAYMENT, 0.00)).abs());
		summary.setCreditMiscellaneous(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_MISCELLANEOUS, 0.00)).abs());
		summary.setCreditAdjustment(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_ADJUSTMENT, 0.00)).abs());
		summary.setCreditReclassToAvailableToWithdrawal(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_RECLASS_TO_AVAILABLE_TO_WITHDRAWAL, 0.00)).abs());
		summary.setCreditFastFunds(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_FAST_FUNDS, 0.00)).abs());
		summary.setCreditFastFundsFeeRefund(BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_FAST_FUNDS_FEE_REFUND, 0.00)).abs());

		BigDecimal totalFeeRefundVor = BigDecimal.valueOf(MapUtils.getDouble(credits, RegisterTransactionType.CREDIT_FEE_REFUND_VOR, 0.00)).abs();
		BigDecimal feeRefundVor = BigDecimal.ZERO;
		BigDecimal feeRefundVorSoftware = BigDecimal.ZERO;
		if (totalFeeRefundVor != null && totalFeeRefundVor.compareTo(BigDecimal.ZERO) > 0) {
			feeRefundVorSoftware = totalFeeRefundVor.multiply(TRANSACTIONAL_VENDOR_OF_RECORD_SOFTWARE_FEE_PERCENTAGE);
			feeRefundVor = totalFeeRefundVor.subtract(feeRefundVorSoftware);
		}

		summary.setCreditFeeRefundVor(feeRefundVor);
		summary.setCreditFeeRefundVorSoftware(feeRefundVorSoftware);

		//Historical
		summary.setCreditAchWithdrawableReturnHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_ACH_WITHDRAWABLE_RETURN, 0.00)).abs());
		summary.setCreditAdvanceHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_ADVANCE, 0.00)).abs());
		summary.setCreditAssignmentPaymentReversalHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_ASSIGNMENT_PAYMENT_REVERSAL, 0.00)).abs());
		summary.setCreditBackgroundCheckRefundHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_BACKGROUND_CHECK_REFUND, 0.00)).abs());
		summary.setCreditDrugTestRefundHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_DRUG_TEST_REFUND, 0.00)).abs());
		summary.setCreditFeeRefundNvorHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_FEE_REFUND_NVOR, 0.00)).abs());
		summary.setCreditGeneralRefundHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_GENERAL_REFUND, 0.00)).abs());
		summary.setCreditMarketingPaymentHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_MARKETING_PAYMENT, 0.00)).abs());
		summary.setCreditMiscellaneousHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_MISCELLANEOUS, 0.00)).abs());
		summary.setCreditAdjustmentHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_ADJUSTMENT, 0.00)).abs());
		summary.setCreditReclassToAvailableToWithdrawalHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_RECLASS_TO_AVAILABLE_TO_WITHDRAWAL, 0.00)).abs());
		summary.setCreditFastFundsHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_FAST_FUNDS, 0.00)).abs());
		summary.setCreditFastFundsFeeRefundHistorical(BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_FAST_FUNDS_FEE_REFUND, 0.00)).abs());

		BigDecimal totalFeeRefundVorHistorical = BigDecimal.valueOf(MapUtils.getDouble(creditsHistorical, RegisterTransactionType.CREDIT_FEE_REFUND_VOR, 0.00)).abs();
		BigDecimal feeRefundVorHistorical = BigDecimal.ZERO;
		BigDecimal feeRefundVorSoftwareHistorical = BigDecimal.ZERO;
		if (totalFeeRefundVorHistorical != null && totalFeeRefundVorHistorical.compareTo(BigDecimal.ZERO) > 0) {
			feeRefundVorSoftwareHistorical = totalFeeRefundVorHistorical.multiply(TRANSACTIONAL_VENDOR_OF_RECORD_SOFTWARE_FEE_PERCENTAGE);
			feeRefundVorHistorical = totalFeeRefundVorHistorical.subtract(feeRefundVorSoftwareHistorical);
		}

		summary.setCreditFeeRefundVorHistorical(feeRefundVorHistorical);
		summary.setCreditFeeRefundVorSoftwareHistorical(feeRefundVorSoftwareHistorical);
	}

	@Override
	public void calculateDebitTransactionsSummary(CreditDebitRegisterTransactionsSummary summary, Calendar start, Calendar end, Calendar startFiscalYear) {
		Map<String, Double> debits = accountingSummaryDAO.calculateMoneyOutDebitTransactions(start, end);
		Map<String, Double> debitsHistorical = accountingSummaryDAO.calculateMoneyOutDebitTransactions(startFiscalYear, end);

		summary.setDebitAchDepositReturn(BigDecimal.valueOf(MapUtils.getDouble(debits, RegisterTransactionType.DEBIT_ACH_DEPOSIT_RETURN, 0.00)).abs());
		summary.setDebitAdvanceRepayment(BigDecimal.valueOf(MapUtils.getDouble(debits, RegisterTransactionType.DEBIT_ADVANCE_REPAYMENT, 0.00)).abs());
		summary.setDebitAssignmentPaymentReversal(BigDecimal.valueOf(MapUtils.getDouble(debits, RegisterTransactionType.DEBIT_ASSIGNMENT_PAYMENT_REVERSAL, 0.00)).abs());
		summary.setDebitCreditCardChargeback(BigDecimal.valueOf(MapUtils.getDouble(debits, RegisterTransactionType.DEBIT_CREDIT_CARD_CHARGEBACK, 0.00)).abs());
		summary.setDebitCreditCardRefund(BigDecimal.valueOf(MapUtils.getDouble(debits, RegisterTransactionType.DEBIT_CREDIT_CARD_REFUND, 0.00)).abs());
		summary.setDebitMiscellaneous(BigDecimal.valueOf(MapUtils.getDouble(debits, RegisterTransactionType.DEBIT_MISCELLANEOUS, 0.00)).abs());
		summary.setDebitAdjustment(BigDecimal.valueOf(MapUtils.getDouble(debits, RegisterTransactionType.DEBIT_ADJUSTMENT, 0.00)).abs());
		summary.setDebitReclassFromAvailableToSpend(BigDecimal.valueOf(MapUtils.getDouble(debits, RegisterTransactionType.DEBIT_RECLASS_FROM_AVAILABLE_TO_SPEND, 0.00)).abs());
		summary.setDebitFastFunds(BigDecimal.valueOf(MapUtils.getDouble(debits, RegisterTransactionType.DEBIT_FAST_FUNDS, 0.00)).abs());

		summary.setDebitAchDepositReturnHistorical(BigDecimal.valueOf(MapUtils.getDouble(debitsHistorical, RegisterTransactionType.DEBIT_ACH_DEPOSIT_RETURN, 0.00)).abs());
		summary.setDebitAdvanceRepaymentHistorical(BigDecimal.valueOf(MapUtils.getDouble(debitsHistorical, RegisterTransactionType.DEBIT_ADVANCE_REPAYMENT, 0.00)).abs());
		summary.setDebitAssignmentPaymentReversalHistorical(BigDecimal.valueOf(MapUtils.getDouble(debitsHistorical, RegisterTransactionType.DEBIT_ASSIGNMENT_PAYMENT_REVERSAL, 0.00)).abs());
		summary.setDebitCreditCardChargebackHistorical(BigDecimal.valueOf(MapUtils.getDouble(debitsHistorical, RegisterTransactionType.DEBIT_CREDIT_CARD_CHARGEBACK, 0.00)).abs());
		summary.setDebitCreditCardRefundHistorical(BigDecimal.valueOf(MapUtils.getDouble(debitsHistorical, RegisterTransactionType.DEBIT_CREDIT_CARD_REFUND, 0.00)).abs());
		summary.setDebitMiscellaneousHistorical(BigDecimal.valueOf(MapUtils.getDouble(debitsHistorical, RegisterTransactionType.DEBIT_MISCELLANEOUS, 0.00)).abs());
		summary.setDebitAdjustmentHistorical(BigDecimal.valueOf(MapUtils.getDouble(debitsHistorical, RegisterTransactionType.DEBIT_ADJUSTMENT, 0.00)).abs());
		summary.setDebitReclassFromAvailableToSpendHistorical(BigDecimal.valueOf(MapUtils.getDouble(debitsHistorical, RegisterTransactionType.DEBIT_RECLASS_FROM_AVAILABLE_TO_SPEND, 0.00)).abs());
		summary.setDebitFastFundsHistorical(BigDecimal.valueOf(MapUtils.getDouble(debitsHistorical, RegisterTransactionType.DEBIT_FAST_FUNDS, 0.00)).abs());
	}

	@Override
	public List<AccountingSummaryDetail> getAccItemRevSubVorSw(Long accountSummaryId) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}
		return accountingSummaryDetailDAO.getAccItemRevSubVorSw(accountingSummary.getPreviousRequestDate(), accountingSummary.getRequestDate());
	}

	@Override
	public List<AccountingSummaryDetail> getAccItemRevSubVorSwYTD(Long accountSummaryId) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}
		Calendar startFiscalYear = findOrCreateStartFiscalYearForDate(accountingSummary.getRequestDate());
		return accountingSummaryDetailDAO.getAccItemRevSubVorSw(startFiscalYear, accountingSummary.getRequestDate());
	}

	@Override
	public List<FastFundsReceivableSummaryDetail> getFastFundsReceivableSummaryDetails(Long accountSummaryId, boolean isYTD) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}

		Calendar startDate = accountingSummary.getPreviousRequestDate();

		if (isYTD) {
			startDate = findOrCreateStartFiscalYearForDate(accountingSummary.getRequestDate());
		}

		return accountingSummaryDetailDAO.getFastFundsReceivableSummaryDetails(startDate, accountingSummary.getRequestDate());
	}

	@Override
	public List<AccountingSummaryDetail> getAccItemRevSubVorVor(Long accountSummaryId) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}
		return accountingSummaryDetailDAO.getAccItemRevSubVorVor(accountingSummary.getPreviousRequestDate(), accountingSummary.getRequestDate());
	}

	@Override
	public List<AccountingSummaryDetail> getAccItemRevSubVorVorYTD(Long accountSummaryId) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}
		Calendar startFiscalYear = findOrCreateStartFiscalYearForDate(accountingSummary.getRequestDate());
		return accountingSummaryDetailDAO.getAccItemRevSubVorVor(startFiscalYear, accountingSummary.getRequestDate());
	}

	@Override
	public List<AccountingSummaryDetail> getAccItemRevSubNVor(Long accountSummaryId) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}
		return accountingSummaryDetailDAO.getAccItemRevSubNVor(accountingSummary.getPreviousRequestDate(), accountingSummary.getRequestDate());
	}

	@Override
	public List<AccountingSummaryDetail> getAccItemRevSubNVorYTD(Long accountSummaryId) {
		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}
		Calendar startFiscalYear = findOrCreateStartFiscalYearForDate(accountingSummary.getRequestDate());
		return accountingSummaryDetailDAO.getAccItemRevSubNVor(startFiscalYear, accountingSummary.getRequestDate());
	}

	@Override
	public List<AccountingCreditMemoSummaryDetail> getCreditMemoTransactionDetails(
		Long accountSummaryId, boolean ytd, Integer creditMemoTypeId, boolean filterByPaid, boolean paid) {

		return getCreditMemoTransactionDetails(accountSummaryId, ytd, Collections.singletonList(creditMemoTypeId),
			filterByPaid, paid, false, false);
	}

	@Override
	public List<AccountingCreditMemoSummaryDetail> getCreditMemoTransactionDetails(
		Long accountSummaryId, boolean ytd, List<Integer> creditMemoTypeIds, boolean filterByPaid, boolean paid,
		boolean filterByVOR, boolean isVOR) {

		AccountingSummary accountingSummary = findSummary(accountSummaryId);
		if (accountingSummary == null) {
			return Collections.emptyList();
		}
		Calendar startDate = ytd ? findOrCreateStartFiscalYearForDate(accountingSummary.getRequestDate()) : accountingSummary.getPreviousRequestDate();

		return getCreditMemoTransactions(startDate, accountingSummary.getRequestDate(), creditMemoTypeIds,
			filterByPaid, paid, filterByVOR, isVOR);
	}

	@Override
	public List<AccountingCreditMemoSummaryDetail> getCreditMemoTransactions(
		Calendar startDate, Calendar endDate, List<Integer> creditMemoTypeIds,
		boolean filterByPaid, boolean paid, boolean filterByVOR, boolean isVOR) {

		return accountingSummaryDetailDAO.getCreditMemoTransactions(startDate, endDate, creditMemoTypeIds,
			filterByPaid, paid, filterByVOR, isVOR);
	}

}
