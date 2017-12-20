package com.workmarket.dao.account;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.AccountingSummary;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface AccountingSummaryDAO extends DAOInterface<AccountingSummary>{
	
	List<AccountingSummary> findAllAccountingSummaries();
	
	Calendar findDateOfLastSummary();

	Calendar findPreviousRequestDateOfSummary(long accountSummaryId);

	Calendar findRequestDateOfSummary(long accountSummaryId);

	Calendar findStartFiscalYearByYear(Integer year);

	BigDecimal calculateMoneyInFastFunds(Calendar start, Calendar end);

	BigDecimal calculateMoneyInChecks(Calendar start, Calendar end);
	
	BigDecimal calculateMoneyInAch(Calendar start, Calendar end);
	
	BigDecimal calculateMoneyInWire(Calendar start, Calendar end);
	
	BigDecimal calculateMoneyInCreditCard(Calendar start, Calendar end);

	BigDecimal calculateCreditMemoTotalByType(
		Integer creditMemoTypeId, Calendar start, Calendar end, boolean filterByPaid, boolean paid);

	BigDecimal calculateCreditMemoTotalByType(
		List<Integer> creditMemoTypeIds, Calendar start, Calendar end,
		boolean filterByPaid, boolean paid, boolean filterByVOR, boolean isVOR);

	BigDecimal calculateMoneyOutWithdrawals(Calendar start, Calendar end, boolean usaTransactions);
	
	BigDecimal calculateMoneyOutFees(Calendar start, Calendar end);
	
	BigDecimal calculateMoneyOutCreditCardFees(Calendar start, Calendar end);
	
	BigDecimal calculateMoneyOutBackgroundChecks(Calendar start, Calendar end);
	
	BigDecimal calculateMoneyOutDrugTests(Calendar start, Calendar end);
	
	BigDecimal calculateMoneyOutAchVerifications(Calendar start, Calendar end);
	
	BigDecimal calculateTotalMoneyOutDebitTransactions(Calendar start, Calendar end);
	
	BigDecimal calculateTotalMoneyOutCreditTransactions(Calendar start, Calendar end);

	BigDecimal calculateTotalMoneyOutFastFundsReceivablePayments(Calendar start, Calendar end);

	BigDecimal calculateTotalMoneyOutFastFundsFees(Calendar start, Calendar end);

	BigDecimal calculateTotalMoneyOnSystem(Calendar start, Calendar end);
	
	BigDecimal calculateTotalCompletedAssignments(Calendar start, Calendar end);
	
	BigDecimal calculateTotalEarnedForAssignments(Calendar start, Calendar end);
	
	BigDecimal calculateTotalInAPStatus(Calendar start, Calendar end);

	BigDecimal calculateMoneyOutPayPalWithdrawal(Calendar start, Calendar end);

	BigDecimal calculateMoneyOutGCCWithdrawal(Calendar start,Calendar end);

	BigDecimal calculateMoneyOutPayPalFees(Calendar start, Calendar end);

	BigDecimal calculateMoneyOutWorkMarketFeesPaidToPayPal(Calendar start, Calendar end);
	
	BigDecimal calculateTotalApAvailable();
	
	BigDecimal calculateTotalApExposure();

	Map<String, Double> calculateMoneyOutAdHocServiceFeesMap(Calendar start, Calendar end);

	BigDecimal calculateMoneyOutAdHocServiceFees(Calendar start, Calendar end);

	/**
	 * Subscriptions
	 */

	BigDecimal calculateThroughputByServiceTypeAndPricingType(Calendar start, Calendar end, List<String> serviceTypes, String pricingType);

	//Money out
	BigDecimal calculateMoneyOutTransactionalFeesByServiceType(Calendar start, Calendar end, List<String> serviceTypes);

	BigDecimal calculateMoneyOutSubscriptionSoftwareFees(Calendar start, Calendar end);

	BigDecimal calculateMoneyOutSubscriptionSoftwareFees(Calendar start, Calendar end, boolean companyHasVORServiceType);

	BigDecimal calculateMoneyOutSubscriptionVORFees(Calendar start, Calendar end);

	BigDecimal calculateMoneyOutProfessionalServicesFees(Calendar start, Calendar end);

	//Revenue
	BigDecimal calculateSubscriptionRevenueSoftwareFees(Calendar start, Calendar end, boolean companyHasVORServiceType);

	BigDecimal calculateSubscriptionRevenueVORFees(Calendar start, Calendar end);

	BigDecimal calculateRevenueProfessionalServicesFees(Calendar start, Calendar end);

	Map<String, Double> calculateRevenueAdHocServiceFees(Calendar start, Calendar end);

	//Non Immediate Revenue
	BigDecimal calculateNonImmediateSubscriptionRevenueSoftwareFees(Calendar start, Calendar end, boolean companyHasVORServiceType);

	BigDecimal calculateNonImmediateSubscriptionRevenueVORFees(Calendar start, Calendar end);

	BigDecimal calculateNonImmediateRevenueProfessionalServicesFees(Calendar start, Calendar end);

	//Deferred revenue
	BigDecimal calculateSubscriptionDeferredRevenueSoftwareFees(Calendar start, Calendar end, boolean companyHasVORServiceType);

	BigDecimal calculateSubscriptionDeferredRevenueVORFees(Calendar start, Calendar end);

	BigDecimal calculateDeferredRevenueProfessionalServicesFees(Calendar start, Calendar end);

	//Credits and Debits
	Map<String, Double> calculateMoneyOutCreditTransactions(Calendar start, Calendar end);

	Map<String, Double> calculateMoneyOutDebitTransactions(Calendar start, Calendar end);

	//Receivables
	BigDecimal calculatePendingPaymentSubscriptionSoftwareAndVORFees(Calendar start, Calendar end);

	BigDecimal calculatePendingPaymentProfessionalServiceFees(Calendar start, Calendar end);

	BigDecimal calculatePendingPaymentFastFundReceivables(Calendar start, Calendar end);

	BigDecimal calculatePendingPaymentAdHocServiceFees(Calendar start, Calendar end);

	BigDecimal calculateSubscriptionSoftwareAndVORFeesByIssueDate(Calendar start, Calendar end);

	BigDecimal calculateProfessionalServiceFeesByIssueDate(Calendar start, Calendar end);

	BigDecimal calculateFastFundsReceivablesByIssueDate(Calendar start, Calendar end);

	BigDecimal calculateAdHocServiceFeesByIssueDate(Calendar start, Calendar end);

	BigDecimal calculateOfflinePayments(Calendar start, Calendar end, boolean subscription, boolean vor);
}
