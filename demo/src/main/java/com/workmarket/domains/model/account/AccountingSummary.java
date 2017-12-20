package com.workmarket.domains.model.account;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import static com.workmarket.utility.NumberUtilities.nullSafeAbs;

@Entity(name = "accounting_summary")
@Table(name = "accounting_summary")
@NamedQueries({
		@NamedQuery(name = "accounting_summary.findAll", query = "FROM accounting_summary order by id desc")
})
@AuditChanges
public class AccountingSummary extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	//Money In Historical
	private BigDecimal moneyInFastFundsHistorical;
	private BigDecimal moneyInChecksHistorical;
	private BigDecimal moneyInAchHistorical;
	private BigDecimal moneyInWireHistorical;
	private BigDecimal moneyInCreditCardHistorical;
	private BigDecimal moneyInSubscriptionCreditMemoHistorical;
	private BigDecimal moneyInProfServicesCreditMemoHistorical;

	//Money Out Historical
	private BigDecimal moneyOutWithdrawalsHistorical;
	private BigDecimal moneyOutNonUSAWithdrawalsHistorical;
	private BigDecimal moneyOutFeesHistorical;
	private BigDecimal moneyOutCreditCardFeesHistorical;
	private BigDecimal moneyOutFastFundsReceivablePaymentsHistorical;
	private BigDecimal moneyOutFastFundsFeeHistorical;
	private BigDecimal moneyOutBackgroundChecksHistorical;
	private BigDecimal moneyOutDrugTestsHistorical;
	private BigDecimal moneyOutAchVerificationsHistorical;
	private BigDecimal moneyOutDebitTransactionsHistorical;
	private BigDecimal moneyOutCreditTransactionsHistorical;
	private BigDecimal moneyOutPayPalWithdrawalHistorical;
	private BigDecimal moneyOutPayPalFeesHistorical;
	private BigDecimal moneyOutWMToPayPalFeesHistorical;
	//Ad-hoc invoices fees
	private BigDecimal moneyOutDepositReturnFeeHistorical;
	private BigDecimal moneyOutWithdrawalReturnFeeHistorical;
	private BigDecimal moneyOutLatePaymentFeeHistorical;
	private BigDecimal moneyOutMiscellaneousFeeHistorical;

	//Totals Historical
	private BigDecimal totalMoneyOnSystemHistorical;
	private BigDecimal totalCompletedAssignmentsHistorical;
	private BigDecimal totalEarnedForAssignmentsHistorical;

	//Money In Daily
	private BigDecimal moneyInFastFunds;
	private BigDecimal moneyInChecks;
	private BigDecimal moneyInAch;
	private BigDecimal moneyInWire;
	private BigDecimal moneyInCreditCard;
	private BigDecimal moneyInSubscriptionCreditMemo;
	private BigDecimal moneyInProfServicesCreditMemo;

	//Money Out Daily
	private BigDecimal moneyOutWithdrawals;
	private BigDecimal moneyOutNonUSAWithdrawals;
	private BigDecimal moneyOutFees;
	private BigDecimal moneyOutCreditCardFees;
	private BigDecimal moneyOutFastFundsReceivablePayments;
	private BigDecimal moneyOutFastFundsFee;
	private BigDecimal moneyOutBackgroundChecks;
	private BigDecimal moneyOutDrugTests;
	private BigDecimal moneyOutAchVerifications;
	private BigDecimal moneyOutDebitTransactions;
	private BigDecimal moneyOutCreditTransactions;
	private BigDecimal moneyOutPayPalWithdrawal;
	private BigDecimal moneyOutGCCWithdrawal;
	private BigDecimal moneyOutGCCWithdrawalHistorical;
	private BigDecimal moneyOutPayPalFees;
	private BigDecimal moneyOutWMToPayPalFees;
	//Ad-hoc invoices fees
	private BigDecimal moneyOutDepositReturnFee;
	private BigDecimal moneyOutWithdrawalReturnFee;
	private BigDecimal moneyOutLatePaymentFee;
	private BigDecimal moneyOutMiscellaneousFee;

	//Revenue Ad-hoc invoices Daily
	private BigDecimal revenueDepositReturnFee;
	private BigDecimal revenueWithdrawalReturnFee;
	private BigDecimal revenueLatePaymentFee;
	private BigDecimal revenueMiscellaneousFee;

	//Revenue Ad-hoc invoices Historical
	private BigDecimal revenueDepositReturnFeeHistorical;
	private BigDecimal revenueWithdrawalReturnFeeHistorical;
	private BigDecimal revenueLatePaymentFeeHistorical;
	private BigDecimal revenueMiscellaneousFeeHistorical;

	//Receivables Ad-hoc service fees
	private BigDecimal adHocServiceFeeReceivables;
	private BigDecimal adHocServiceFeeReceivablesHistorical;

	//Totals Daily
	private BigDecimal totalMoneyOnSystem;
	private BigDecimal totalCompletedAssignments;
	private BigDecimal totalEarnedForAssignments;
	private BigDecimal totalInApStatus;

	private Calendar requestDate;
	private Calendar previousRequestDate;

	private AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummary;
	private CreditDebitRegisterTransactionsSummary creditDebitRegisterTransactionsSummary;
	private OfflinePaymentSummary offlinePaymentSummary;

	@Column(name = "money_in_fast_funds")
	public BigDecimal getMoneyInFastFunds() {
		return moneyInFastFunds;
	}

	@Column(name = "money_in_checks")
	public BigDecimal getMoneyInChecks() {
		return moneyInChecks;
	}

	@Column(name = "money_in_ach")
	public BigDecimal getMoneyInAch() {
		return moneyInAch;
	}

	@Column(name = "money_in_wire")
	public BigDecimal getMoneyInWire() {
		return moneyInWire;
	}

	@Column(name = "money_in_credit_card")
	public BigDecimal getMoneyInCreditCard() {
		return moneyInCreditCard;
	}

	@Column(name = "money_in_prof_services_credit_memo")
	public BigDecimal getMoneyInProfServicesCreditMemo() {
		return moneyInProfServicesCreditMemo;
	}

	@Column(name = "money_in_subscription_credit_memo")
	public BigDecimal getMoneyInSubscriptionCreditMemo() {
		return moneyInSubscriptionCreditMemo;
	}

	@Column(name = "money_out_withdrawals")
	public BigDecimal getMoneyOutWithdrawals() {
		return moneyOutWithdrawals;
	}

	@Column(name = "money_out_fees")
	public BigDecimal getMoneyOutFees() {
		return moneyOutFees;
	}

	@Column(name = "money_out_credit_card_fees")
	public BigDecimal getMoneyOutCreditCardFees() {
		return moneyOutCreditCardFees;
	}

	@Column(name = "money_out_fast_funds_receivable_payments")
	public BigDecimal getMoneyOutFastFundsReceivablePayments() {
		return moneyOutFastFundsReceivablePayments;
	}

	@Column(name = "money_out_fast_funds_fee")
	public BigDecimal getMoneyOutFastFundsFee() {
		return moneyOutFastFundsFee;
	}

	@Column(name = "money_out_background_checks")
	public BigDecimal getMoneyOutBackgroundChecks() {
		return moneyOutBackgroundChecks;
	}

	@Column(name = "money_out_drug_tests")
	public BigDecimal getMoneyOutDrugTests() {
		return moneyOutDrugTests;
	}

	@Column(name = "money_out_ach_verifications")
	public BigDecimal getMoneyOutAchVerifications() {
		return moneyOutAchVerifications;
	}

	@Column(name = "money_out_debit")
	public BigDecimal getMoneyOutDebitTransactions() {
		return moneyOutDebitTransactions;
	}

	@Column(name = "money_out_credit")
	public BigDecimal getMoneyOutCreditTransactions() {
		return moneyOutCreditTransactions;
	}

	@Column(name = "total_money_on_system")
	public BigDecimal getTotalMoneyOnSystem() {
		return totalMoneyOnSystem;
	}

	@Column(name = "total_completed_assignments")
	public BigDecimal getTotalCompletedAssignments() {
		return totalCompletedAssignments;
	}

	@Column(name = "total_earned_for_assignments")
	public BigDecimal getTotalEarnedForAssignments() {
		return totalEarnedForAssignments;
	}

	@Column(name = "request_date")
	public Calendar getRequestDate() {
		return requestDate;
	}

	@Column(name = "previous_request_date")
	public Calendar getPreviousRequestDate() {
		return previousRequestDate;
	}


	public void setMoneyInFastFunds(BigDecimal moneyInFastFunds) {
		this.moneyInFastFunds = moneyInFastFunds;
	}

	public void setMoneyInChecks(BigDecimal moneyInChecks) {
		this.moneyInChecks = moneyInChecks;
	}

	public void setMoneyInAch(BigDecimal moneyInAch) {
		this.moneyInAch = moneyInAch;
	}

	public void setMoneyInWire(BigDecimal moneyInWire) {
		this.moneyInWire = moneyInWire;
	}

	public void setMoneyInCreditCard(BigDecimal moneyInCreditCard) {
		this.moneyInCreditCard = moneyInCreditCard;
	}

	public void setMoneyInSubscriptionCreditMemo(BigDecimal moneyInCreditMemo) {
		this.moneyInSubscriptionCreditMemo = moneyInCreditMemo;
	}

	public void setMoneyInProfServicesCreditMemo(BigDecimal moneyInCreditMemo) {
		this.moneyInProfServicesCreditMemo = moneyInCreditMemo;
	}

	public void setMoneyOutWithdrawals(BigDecimal moneyOutWithdrawals) {
		this.moneyOutWithdrawals = moneyOutWithdrawals;
	}

	public void setMoneyOutFees(BigDecimal moneyOutFees) {
		this.moneyOutFees = moneyOutFees;
	}

	public void setMoneyOutCreditCardFees(BigDecimal moneyOutCreditCardFees) {
		this.moneyOutCreditCardFees = moneyOutCreditCardFees;
	}

	public void setMoneyOutFastFundsReceivablePayments(BigDecimal moneyOutFastFundsReceivablePayments) {
		this.moneyOutFastFundsReceivablePayments = moneyOutFastFundsReceivablePayments;
	}

	public void setMoneyOutFastFundsFee(BigDecimal moneyOutFastFundsFee) {
		this.moneyOutFastFundsFee = moneyOutFastFundsFee;
	}

	public void setMoneyOutBackgroundChecks(BigDecimal moneyOutBackgroundChecks) {
		this.moneyOutBackgroundChecks = moneyOutBackgroundChecks;
	}

	public void setMoneyOutDrugTests(BigDecimal moneyOutDrugTests) {
		this.moneyOutDrugTests = moneyOutDrugTests;
	}

	public void setMoneyOutAchVerifications(BigDecimal moneyOutAchVerifications) {
		this.moneyOutAchVerifications = moneyOutAchVerifications;
	}

	public void setMoneyOutDebitTransactions(BigDecimal moneyOutDebitTransactions) {
		this.moneyOutDebitTransactions = moneyOutDebitTransactions;
	}

	public void setMoneyOutCreditTransactions(BigDecimal moneyOutCreditTransactions) {
		this.moneyOutCreditTransactions = moneyOutCreditTransactions;
	}

	public void setTotalMoneyOnSystem(BigDecimal totalMoneyOnSystem) {
		this.totalMoneyOnSystem = totalMoneyOnSystem;
	}

	public void setTotalCompletedAssignments(BigDecimal totalCompletedAssignments) {
		this.totalCompletedAssignments = totalCompletedAssignments;
	}

	public void setTotalEarnedForAssignments(BigDecimal totalEarnedForAssignments) {
		this.totalEarnedForAssignments = totalEarnedForAssignments;
	}

	public void setRequestDate(Calendar requestDate) {
		this.requestDate = requestDate;
	}

	public void setPreviousRequestDate(Calendar previousRequestDate) {
		this.previousRequestDate = previousRequestDate;
	}

	@Column(name = "total_in_ap_status")
	public BigDecimal getTotalInApStatus() {
		return totalInApStatus;
	}

	public void setTotalInApStatus(BigDecimal totalInApStatus) {
		this.totalInApStatus = totalInApStatus;
	}

	@Column(name = "money_in_fast_funds_historical")
	public BigDecimal getMoneyInFastFundsHistorical() {
		return moneyInFastFundsHistorical;
	}

	@Column(name = "money_in_checks_historical")
	public BigDecimal getMoneyInChecksHistorical() {
		return moneyInChecksHistorical;
	}

	@Column(name = "money_in_ach_historical")
	public BigDecimal getMoneyInAchHistorical() {
		return moneyInAchHistorical;
	}

	@Column(name = "money_in_wire_historical")
	public BigDecimal getMoneyInWireHistorical() {
		return moneyInWireHistorical;
	}

	@Column(name = "money_in_credit_card_historical")
	public BigDecimal getMoneyInCreditCardHistorical() {
		return moneyInCreditCardHistorical;
	}

	@Column(name = "money_in_subscription_credit_memo_historical")
	public BigDecimal getMoneyInSubscriptionCreditMemoHistorical() {
		return moneyInSubscriptionCreditMemoHistorical;
	}

	@Column(name = "money_in_prof_services_credit_memo_historical")
	public BigDecimal getMoneyInProfServicesCreditMemoHistorical() {
		return moneyInProfServicesCreditMemoHistorical;
	}

	@Column(name = "money_out_withdrawals_historical")
	public BigDecimal getMoneyOutWithdrawalsHistorical() {
		return moneyOutWithdrawalsHistorical;
	}

	@Column(name = "money_out_fees_historical")
	public BigDecimal getMoneyOutFeesHistorical() {
		return moneyOutFeesHistorical;
	}

	@Column(name = "money_out_credit_card_fees_historical")
	public BigDecimal getMoneyOutCreditCardFeesHistorical() {
		return moneyOutCreditCardFeesHistorical;
	}

	@Column(name = "money_out_fast_funds_receivable_payments_historical")
	public BigDecimal getMoneyOutFastFundsReceivablePaymentsHistorical() {
		return moneyOutFastFundsReceivablePaymentsHistorical;
	}

	@Column(name = "money_out_fast_funds_fees_historical")
	public BigDecimal getMoneyOutFastFundsFeeHistorical() {
		return moneyOutFastFundsFeeHistorical;
	}

	@Column(name = "money_out_background_checks_historical")
	public BigDecimal getMoneyOutBackgroundChecksHistorical() {
		return moneyOutBackgroundChecksHistorical;
	}

	@Column(name = "money_out_drug_tests_historical")
	public BigDecimal getMoneyOutDrugTestsHistorical() {
		return moneyOutDrugTestsHistorical;
	}

	@Column(name = "money_out_ach_verifications_historical")
	public BigDecimal getMoneyOutAchVerificationsHistorical() {
		return moneyOutAchVerificationsHistorical;
	}

	@Column(name = "money_out_debit_historical")
	public BigDecimal getMoneyOutDebitTransactionsHistorical() {
		return moneyOutDebitTransactionsHistorical;
	}

	@Column(name = "money_out_credit_historical")
	public BigDecimal getMoneyOutCreditTransactionsHistorical() {
		return moneyOutCreditTransactionsHistorical;
	}

	@Column(name = "total_money_on_system_historical")
	public BigDecimal getTotalMoneyOnSystemHistorical() {
		return totalMoneyOnSystemHistorical;
	}

	@Column(name = "total_completed_assignments_historical")
	public BigDecimal getTotalCompletedAssignmentsHistorical() {
		return totalCompletedAssignmentsHistorical;
	}

	@Column(name = "total_earned_for_assignments_historical")
	public BigDecimal getTotalEarnedForAssignmentsHistorical() {
		return totalEarnedForAssignmentsHistorical;
	}

	public void setMoneyInFastFundsHistorical(BigDecimal moneyInFastFundsHistorical) {
		this.moneyInFastFundsHistorical = moneyInFastFundsHistorical;
	}

	public void setMoneyInChecksHistorical(BigDecimal moneyInChecksHistorical) {
		this.moneyInChecksHistorical = moneyInChecksHistorical;
	}

	public void setMoneyInAchHistorical(BigDecimal moneyInAchHistorical) {
		this.moneyInAchHistorical = moneyInAchHistorical;
	}

	public void setMoneyInWireHistorical(BigDecimal moneyInWireHistorical) {
		this.moneyInWireHistorical = moneyInWireHistorical;
	}

	public void setMoneyInCreditCardHistorical(BigDecimal moneyInCreditCardHistorical) {
		this.moneyInCreditCardHistorical = moneyInCreditCardHistorical;
	}

	public void setMoneyInSubscriptionCreditMemoHistorical(BigDecimal moneyInSubscriptionCreditMemoHistorical) {
		this.moneyInSubscriptionCreditMemoHistorical = moneyInSubscriptionCreditMemoHistorical;
	}

	public void setMoneyInProfServicesCreditMemoHistorical(BigDecimal moneyInProfServicesCreditMemoHistorical) {
		this.moneyInProfServicesCreditMemoHistorical = moneyInProfServicesCreditMemoHistorical;
	}

	public void setMoneyOutWithdrawalsHistorical(BigDecimal moneyOutWithdrawalsHistorical) {
		this.moneyOutWithdrawalsHistorical = moneyOutWithdrawalsHistorical;
	}

	public void setMoneyOutFeesHistorical(BigDecimal moneyOutFeesHistorical) {
		this.moneyOutFeesHistorical = moneyOutFeesHistorical;
	}

	public void setMoneyOutCreditCardFeesHistorical(BigDecimal moneyOutCreditCardFeesHistorical) {
		this.moneyOutCreditCardFeesHistorical = moneyOutCreditCardFeesHistorical;
	}

	public void setMoneyOutFastFundsReceivablePaymentsHistorical(BigDecimal moneyOutFastFundsReceivablePaymentsHistorical) {
		this.moneyOutFastFundsReceivablePaymentsHistorical = moneyOutFastFundsReceivablePaymentsHistorical;
	}

	public void setMoneyOutFastFundsFeeHistorical(BigDecimal moneyOutFastFundsFeeHistorical) {
		this.moneyOutFastFundsFeeHistorical = moneyOutFastFundsFeeHistorical;
	}

	public void setMoneyOutBackgroundChecksHistorical(BigDecimal moneyOutBackgroundChecksHistorical) {
		this.moneyOutBackgroundChecksHistorical = moneyOutBackgroundChecksHistorical;
	}

	public void setMoneyOutDrugTestsHistorical(BigDecimal moneyOutDrugTestsHistorical) {
		this.moneyOutDrugTestsHistorical = moneyOutDrugTestsHistorical;
	}

	public void setMoneyOutAchVerificationsHistorical(BigDecimal moneyOutAchVerificationsHistorical) {
		this.moneyOutAchVerificationsHistorical = moneyOutAchVerificationsHistorical;
	}

	public void setMoneyOutDebitTransactionsHistorical(BigDecimal moneyOutDebitTransactionsHistorical) {
		this.moneyOutDebitTransactionsHistorical = moneyOutDebitTransactionsHistorical;
	}

	public void setMoneyOutCreditTransactionsHistorical(BigDecimal moneyOutCreditTransactionsHistorical) {
		this.moneyOutCreditTransactionsHistorical = moneyOutCreditTransactionsHistorical;
	}

	public void setTotalMoneyOnSystemHistorical(BigDecimal totalMoneyOnSystemHistorical) {
		this.totalMoneyOnSystemHistorical = totalMoneyOnSystemHistorical;
	}

	public void setTotalCompletedAssignmentsHistorical(BigDecimal totalCompletedAssignmentsHistorical) {
		this.totalCompletedAssignmentsHistorical = totalCompletedAssignmentsHistorical;
	}

	public void setTotalEarnedForAssignmentsHistorical(BigDecimal totalEarnedForAssignmentsHistorical) {
		this.totalEarnedForAssignmentsHistorical = totalEarnedForAssignmentsHistorical;
	}

	@Column(name = "money_out_paypal_fees")
	public BigDecimal getMoneyOutPayPalFees() {
		return moneyOutPayPalFees;
	}

	public void setMoneyOutPayPalFees(BigDecimal moneyOutPayPalFees) {
		this.moneyOutPayPalFees = moneyOutPayPalFees;
	}

	@Column(name = "money_out_paypal_fees_historical")
	public BigDecimal getMoneyOutPayPalFeesHistorical() {
		return moneyOutPayPalFeesHistorical;
	}

	public void setMoneyOutPayPalFeesHistorical(BigDecimal moneyOutPayPalFeesHistorical) {
		this.moneyOutPayPalFeesHistorical = moneyOutPayPalFeesHistorical;
	}

	@Column(name = "money_out_paypal_withdrawal")
	public BigDecimal getMoneyOutPayPalWithdrawal() {
		return moneyOutPayPalWithdrawal;
	}

	public void setMoneyOutPayPalWithdrawal(BigDecimal moneyOutPayPalWithdrawal) {
		this.moneyOutPayPalWithdrawal = moneyOutPayPalWithdrawal;
	}

	@Column(name = "money_out_gcc_withdrawal")
	public BigDecimal getMoneyOutGCCWithdrawal() {
		return moneyOutGCCWithdrawal;
	}

	public void setMoneyOutGCCWithdrawal(BigDecimal moneyOutGCCWithdrawal) {
		this.moneyOutGCCWithdrawal = moneyOutGCCWithdrawal;
	}


	@Column(name = "money_out_gcc_withdrawal_historical")
	public BigDecimal getMoneyOutGCCWithdrawalHistorical() {
		return moneyOutGCCWithdrawalHistorical;
	}

	public void setMoneyOutGCCWithdrawalHistorical(BigDecimal moneyOutGCCWithdrawalHistorical) {
		this.moneyOutGCCWithdrawalHistorical = moneyOutGCCWithdrawalHistorical;
	}

	@Column(name = "money_out_paypal_wd_historical")
	public BigDecimal getMoneyOutPayPalWithdrawalHistorical() {
		return moneyOutPayPalWithdrawalHistorical;
	}

	public void setMoneyOutPayPalWithdrawalHistorical(BigDecimal moneyOutPayPalWithdrawalHistorical) {
		this.moneyOutPayPalWithdrawalHistorical = moneyOutPayPalWithdrawalHistorical;
	}

	@Column(name = "money_out_wm_to_paypal_fees")
	public BigDecimal getMoneyOutWMToPayPalFees() {
		return moneyOutWMToPayPalFees;
	}

	public void setMoneyOutWMToPayPalFees(BigDecimal moneyOutWMToPayPalFees) {
		this.moneyOutWMToPayPalFees = moneyOutWMToPayPalFees;
	}

	@Column(name = "money_out_wm_to_paypal_fees_historical")
	public BigDecimal getMoneyOutWMToPayPalFeesHistorical() {
		return moneyOutWMToPayPalFeesHistorical;
	}

	public void setMoneyOutWMToPayPalFeesHistorical(BigDecimal moneyOutWMToPayPalFeesHistorical) {
		this.moneyOutWMToPayPalFeesHistorical = moneyOutWMToPayPalFeesHistorical;
	}

	@Column(name = "money_out_deposit_return_fee")
	public BigDecimal getMoneyOutDepositReturnFee() {
		return moneyOutDepositReturnFee;
	}

	public void setMoneyOutDepositReturnFee(BigDecimal moneyOutDepositReturnFee) {
		this.moneyOutDepositReturnFee = moneyOutDepositReturnFee;
	}

	@Column(name = "money_out_deposit_return_fee_historical")
	public BigDecimal getMoneyOutDepositReturnFeeHistorical() {
		return moneyOutDepositReturnFeeHistorical;
	}

	public void setMoneyOutDepositReturnFeeHistorical(BigDecimal moneyOutDepositReturnFeeHistorical) {
		this.moneyOutDepositReturnFeeHistorical = moneyOutDepositReturnFeeHistorical;
	}

	@Column(name = "money_out_late_payment_fee")
	public BigDecimal getMoneyOutLatePaymentFee() {
		return moneyOutLatePaymentFee;
	}

	public void setMoneyOutLatePaymentFee(BigDecimal moneyOutLatePaymentFee) {
		this.moneyOutLatePaymentFee = moneyOutLatePaymentFee;
	}

	@Column(name = "money_out_late_payment_fee_historical")
	public BigDecimal getMoneyOutLatePaymentFeeHistorical() {
		return moneyOutLatePaymentFeeHistorical;
	}

	public void setMoneyOutLatePaymentFeeHistorical(BigDecimal moneyOutLatePaymentFeeHistorical) {
		this.moneyOutLatePaymentFeeHistorical = moneyOutLatePaymentFeeHistorical;
	}

	@Column(name = "money_out_miscellaneous_fee")
	public BigDecimal getMoneyOutMiscellaneousFee() {
		return moneyOutMiscellaneousFee;
	}

	public void setMoneyOutMiscellaneousFee(BigDecimal moneyOutMiscellaneousFee) {
		this.moneyOutMiscellaneousFee = moneyOutMiscellaneousFee;
	}

	@Column(name = "money_out_miscellaneous_fee_historical")
	public BigDecimal getMoneyOutMiscellaneousFeeHistorical() {
		return moneyOutMiscellaneousFeeHistorical;
	}

	public void setMoneyOutMiscellaneousFeeHistorical(BigDecimal moneyOutMiscellaneousFeeHistorical) {
		this.moneyOutMiscellaneousFeeHistorical = moneyOutMiscellaneousFeeHistorical;
	}

	@Column(name = "money_out_withdrawal_return_fee")
	public BigDecimal getMoneyOutWithdrawalReturnFee() {
		return moneyOutWithdrawalReturnFee;
	}

	public void setMoneyOutWithdrawalReturnFee(BigDecimal moneyOutWithdrawalReturnFee) {
		this.moneyOutWithdrawalReturnFee = moneyOutWithdrawalReturnFee;
	}

	@Column(name = "money_out_withdrawal_return_fee_historical")
	public BigDecimal getMoneyOutWithdrawalReturnFeeHistorical() {
		return moneyOutWithdrawalReturnFeeHistorical;
	}

	public void setMoneyOutWithdrawalReturnFeeHistorical(BigDecimal moneyOutWithdrawalReturnFeeHistorical) {
		this.moneyOutWithdrawalReturnFeeHistorical = moneyOutWithdrawalReturnFeeHistorical;
	}

	@Column(name = "revenue_deposit_return_fee")
	public BigDecimal getRevenueDepositReturnFee() {
		return revenueDepositReturnFee;
	}

	public void setRevenueDepositReturnFee(BigDecimal revenueDepositReturnFee) {
		this.revenueDepositReturnFee = revenueDepositReturnFee;
	}

	@Column(name = "revenue_deposit_return_fee_historical")
	public BigDecimal getRevenueDepositReturnFeeHistorical() {
		return revenueDepositReturnFeeHistorical;
	}

	public void setRevenueDepositReturnFeeHistorical(BigDecimal revenueDepositReturnFeeHistorical) {
		this.revenueDepositReturnFeeHistorical = revenueDepositReturnFeeHistorical;
	}

	@Column(name = "revenue_late_payment_fee")
	public BigDecimal getRevenueLatePaymentFee() {
		return revenueLatePaymentFee;
	}

	public void setRevenueLatePaymentFee(BigDecimal revenueLatePaymentFee) {
		this.revenueLatePaymentFee = revenueLatePaymentFee;
	}

	@Column(name = "revenue_late_payment_fee_historical")
	public BigDecimal getRevenueLatePaymentFeeHistorical() {
		return revenueLatePaymentFeeHistorical;
	}

	public void setRevenueLatePaymentFeeHistorical(BigDecimal revenueLatePaymentFeeHistorical) {
		this.revenueLatePaymentFeeHistorical = revenueLatePaymentFeeHistorical;
	}

	@Column(name = "revenue_miscellaneous_fee")
	public BigDecimal getRevenueMiscellaneousFee() {
		return revenueMiscellaneousFee;
	}

	public void setRevenueMiscellaneousFee(BigDecimal revenueMiscellaneousFee) {
		this.revenueMiscellaneousFee = revenueMiscellaneousFee;
	}

	@Column(name = "revenue_miscellaneous_fee_historical")
	public BigDecimal getRevenueMiscellaneousFeeHistorical() {
		return revenueMiscellaneousFeeHistorical;
	}

	public void setRevenueMiscellaneousFeeHistorical(BigDecimal revenueMiscellaneousFeeHistorical) {
		this.revenueMiscellaneousFeeHistorical = revenueMiscellaneousFeeHistorical;
	}

	@Column(name = "revenue_withdrawal_return_fee")
	public BigDecimal getRevenueWithdrawalReturnFee() {
		return revenueWithdrawalReturnFee;
	}

	public void setRevenueWithdrawalReturnFee(BigDecimal revenueWithdrawalReturnFee) {
		this.revenueWithdrawalReturnFee = revenueWithdrawalReturnFee;
	}

	@Column(name = "revenue_withdrawal_return_fee_historical")
	public BigDecimal getRevenueWithdrawalReturnFeeHistorical() {
		return revenueWithdrawalReturnFeeHistorical;
	}

	public void setRevenueWithdrawalReturnFeeHistorical(BigDecimal revenueWithdrawalReturnFeeHistorical) {
		this.revenueWithdrawalReturnFeeHistorical = revenueWithdrawalReturnFeeHistorical;
	}

	@Column(name = "ad_hoc_service_fee_receivables")
	public BigDecimal getAdHocServiceFeeReceivables() {
		return adHocServiceFeeReceivables;
	}

	public void setAdHocServiceFeeReceivables(BigDecimal adHocServiceFeeReceivables) {
		this.adHocServiceFeeReceivables = adHocServiceFeeReceivables;
	}

	@Column(name = "ad_hoc_service_fee_receivables_historical")
	public BigDecimal getAdHocServiceFeeReceivablesHistorical() {
		return adHocServiceFeeReceivablesHistorical;
	}

	public void setAdHocServiceFeeReceivablesHistorical(BigDecimal adHocServiceFeeReceivablesHistorical) {
		this.adHocServiceFeeReceivablesHistorical = adHocServiceFeeReceivablesHistorical;
	}

	@Column(name = "money_out_non_usa_withdrawals")
	public BigDecimal getMoneyOutNonUSAWithdrawals() {
		return moneyOutNonUSAWithdrawals;
	}

	public void setMoneyOutNonUSAWithdrawals(BigDecimal moneyOutNonUSAWithdrawals) {
		this.moneyOutNonUSAWithdrawals = moneyOutNonUSAWithdrawals;
	}

	@Column(name = "money_out_non_usa_withdrawals_historical")
	public BigDecimal getMoneyOutNonUSAWithdrawalsHistorical() {
		return moneyOutNonUSAWithdrawalsHistorical;
	}

	public void setMoneyOutNonUSAWithdrawalsHistorical(BigDecimal moneyOutNonUSAWithdrawalsHistorical) {
		this.moneyOutNonUSAWithdrawalsHistorical = moneyOutNonUSAWithdrawalsHistorical;
	}

	@Embedded
	public AccountingPricingServiceTypeSummary getAccountingPricingServiceTypeSummary() {
		return accountingPricingServiceTypeSummary;
	}

	public void setAccountingPricingServiceTypeSummary(AccountingPricingServiceTypeSummary accountingPricingServiceTypeSummary) {
		this.accountingPricingServiceTypeSummary = accountingPricingServiceTypeSummary;
	}

	@Embedded
	public CreditDebitRegisterTransactionsSummary getCreditDebitRegisterTransactionsSummary() {
		return creditDebitRegisterTransactionsSummary;
	}

	public void setCreditDebitRegisterTransactionsSummary(CreditDebitRegisterTransactionsSummary creditDebitRegisterTransactionsSummary) {
		this.creditDebitRegisterTransactionsSummary = creditDebitRegisterTransactionsSummary;
	}

	@Embedded
	public OfflinePaymentSummary getOfflinePaymentSummary() {
		return offlinePaymentSummary;
	}

	public void setOfflinePaymentSummary(OfflinePaymentSummary offlinePaymentSummary) {
		this.offlinePaymentSummary = offlinePaymentSummary;
	}

	@Transient
	public BigDecimal getOfflineTransactionVOR() {
		return this.offlinePaymentSummary.getOfflineTransVor();
	}

	@Transient
	public BigDecimal getOfflineTransactionVORHistorical() {
		return this.offlinePaymentSummary.getOfflineTransVorHistorical();
	}

	@Transient
	public BigDecimal getOfflineTransactionNVOR() {
		return this.offlinePaymentSummary.getOfflineTransNvor();
	}

	@Transient
	public BigDecimal getOfflineTransactionNVORHistorical() {
		return this.offlinePaymentSummary.getOfflineTransNvorHistorical();
	}

	@Transient
	public BigDecimal getOfflineSubscriptionVOR() {
		return this.offlinePaymentSummary.getOfflineSubsVor();
	}

	@Transient
	public BigDecimal getOfflineSubscriptionVORHistorical() {
		return this.offlinePaymentSummary.getOfflineSubsVorHistorical();
	}

	@Transient
	public BigDecimal getOfflineSubscriptionNVOR() {
		return this.offlinePaymentSummary.getOfflineSubsNvor();
	}

	@Transient
	public BigDecimal getOfflineSubscriptionNVORHistorical() {
		return this.offlinePaymentSummary.getOfflineSubsNvorHistorical();
	}

	@Transient
	public BigDecimal getDefRevenueSubscriptionNonVorSoftwareFee() {
		return accountingPricingServiceTypeSummary.getDefRevenueSubscriptionNonVorSoftwareFee();
	}

	@Transient
	public BigDecimal getDefRevenueSubscriptionNonVorSoftwareFeeHistorical() {
		return accountingPricingServiceTypeSummary.getDefRevenueSubscriptionNonVorSoftwareFeeHistorical();
	}

	@Transient
	public BigDecimal getDefRevenueSubscriptionVorSoftwareFee() {
		return accountingPricingServiceTypeSummary.getDefRevenueSubscriptionVorSoftwareFee();
	}

	@Transient
	public BigDecimal getDefRevenueSubscriptionVorSoftwareFeeHistorical() {
		return accountingPricingServiceTypeSummary.getDefRevenueSubscriptionVorSoftwareFeeHistorical();
	}

	@Transient
	public BigDecimal getDefRevenueSubscriptionVorVorFee() {
		return accountingPricingServiceTypeSummary.getDefRevenueSubscriptionVorVorFee();
	}

	@Transient
	public BigDecimal getDefRevenueSubscriptionVorVorFeeHistorical() {
		return accountingPricingServiceTypeSummary.getDefRevenueSubscriptionVorVorFeeHistorical();
	}

	@Transient
	public BigDecimal getMoneyOutSubscriptionNonVorSoftwareFee() {
		return accountingPricingServiceTypeSummary.getMoneyOutSubscriptionNonVorSoftwareFee();
	}

	@Transient
	public BigDecimal getMoneyOutSubscriptionNonVorSoftwareFeeHistorical() {
		return accountingPricingServiceTypeSummary.getMoneyOutSubscriptionNonVorSoftwareFeeHistorical();
	}

	@Transient
	public BigDecimal getMoneyOutSubscriptionVorSoftwareFee() {
		return accountingPricingServiceTypeSummary.getMoneyOutSubscriptionVorSoftwareFee();
	}

	@Transient
	public BigDecimal getMoneyOutSubscriptionVorSoftwareFeeHistorical() {
		return accountingPricingServiceTypeSummary.getMoneyOutSubscriptionVorSoftwareFeeHistorical();
	}

	@Transient
	public BigDecimal getMoneyOutSubscriptionVorVorFee() {
		return accountingPricingServiceTypeSummary.getMoneyOutSubscriptionVorVorFee();
	}

	@Transient
	public BigDecimal getMoneyOutSubscriptionVorVorFeeHistorical() {
		return accountingPricingServiceTypeSummary.getMoneyOutSubscriptionVorVorFeeHistorical();
	}

	@Transient
	public BigDecimal getMoneyOutTransactionalNonVorSoftwareFee() {
		return accountingPricingServiceTypeSummary.getMoneyOutTransactionalNonVorSoftwareFee();
	}

	@Transient
	public BigDecimal getMoneyOutTransactionalNonVorSoftwareFeeHistorical() {
		return accountingPricingServiceTypeSummary.getMoneyOutTransactionalNonVorSoftwareFeeHistorical();
	}

	@Transient
	public BigDecimal getMoneyOutTransactionalVorSoftwareFee() {
		return accountingPricingServiceTypeSummary.getMoneyOutTransactionalVorSoftwareFee();
	}

	@Transient
	public BigDecimal getMoneyOutTransactionalVorSoftwareFeeHistorical() {
		return accountingPricingServiceTypeSummary.getMoneyOutTransactionalVorSoftwareFeeHistorical();
	}

	@Transient
	public BigDecimal getMoneyOutTransactionalVorVorFee() {
		return accountingPricingServiceTypeSummary.getMoneyOutTransactionalVorVorFee();
	}

	@Transient
	public BigDecimal getMoneyOutTransactionalVorVorFeeHistorical() {
		return accountingPricingServiceTypeSummary.getMoneyOutTransactionalVorVorFeeHistorical();
	}

	@Transient
	public BigDecimal getRevenueSubscriptionNonVorSoftwareFee() {
		return accountingPricingServiceTypeSummary.getRevenueSubscriptionNonVorSoftwareFee();
	}

	@Transient
	public BigDecimal getRevenueSubscriptionNonVorSoftwareFeeHistorical() {
		return accountingPricingServiceTypeSummary.getRevenueSubscriptionNonVorSoftwareFeeHistorical();
	}

	@Transient
	public BigDecimal getRevenueSubscriptionVorSoftwareFee() {
		return accountingPricingServiceTypeSummary.getRevenueSubscriptionVorSoftwareFee();
	}

	@Transient
	public BigDecimal getRevenueSubscriptionVorSoftwareFeeHistorical() {
		return accountingPricingServiceTypeSummary.getRevenueSubscriptionVorSoftwareFeeHistorical();
	}

	@Transient
	public BigDecimal getRevenueSubscriptionVorVorFee() {
		return accountingPricingServiceTypeSummary.getRevenueSubscriptionVorVorFee();
	}

	@Transient
	public BigDecimal getRevenueSubscriptionVorVorFeeHistorical() {
		return accountingPricingServiceTypeSummary.getRevenueSubscriptionVorVorFeeHistorical();
	}

	@Transient
	public BigDecimal getPaidSubscriptionInvoiceCredit() {
		return accountingPricingServiceTypeSummary.getPaidSubscriptionInvoiceCredit();
	}

	@Transient
	public BigDecimal getPaidSubscriptionInvoiceCreditHistorical() {
		return accountingPricingServiceTypeSummary.getPaidSubscriptionInvoiceCreditHistorical();
	}

	@Transient
	public BigDecimal getCreditSubscriptionNonVorSoftwareFee() {
		return accountingPricingServiceTypeSummary.getCreditSubscriptionNonVorSoftwareFee();
	}

	@Transient
	public BigDecimal getCreditSubscriptionNonVorSoftwareFeeHistorical() {
		return accountingPricingServiceTypeSummary.getCreditSubscriptionNonVorSoftwareFeeHistorical();
	}

	@Transient
	public BigDecimal getCreditSubscriptionVorSoftwareFee() {
		return accountingPricingServiceTypeSummary.getCreditSubscriptionVorSoftwareFee();
	}

	@Transient
	public BigDecimal getCreditSubscriptionVorSoftwareFeeHistorical() {
		return accountingPricingServiceTypeSummary.getCreditSubscriptionVorSoftwareFeeHistorical();
	}

	@Transient
	public BigDecimal getCreditSubscriptionVorVorFee() {
		return accountingPricingServiceTypeSummary.getCreditSubscriptionVorVorFee();
	}

	@Transient
	public BigDecimal getCreditSubscriptionVorVorFeeHistorical() {
		return accountingPricingServiceTypeSummary.getCreditSubscriptionVorVorFeeHistorical();
	}

	@Transient
	public BigDecimal getSubscriptionFeeReceivables() {
		return accountingPricingServiceTypeSummary.getSubscriptionFeeReceivables();
	}

	@Transient
	public BigDecimal getSubscriptionFeeReceivablesHistorical() {
		return accountingPricingServiceTypeSummary.getSubscriptionFeeReceivablesHistorical();
	}

	@Transient
	public BigDecimal getThroughputSubscriptionNonVor() {
		return accountingPricingServiceTypeSummary.getThroughputSubscriptionNonVor();
	}

	@Transient
	public BigDecimal getThroughputSubscriptionNonVorHistorical() {
		return accountingPricingServiceTypeSummary.getThroughputSubscriptionNonVorHistorical();
	}

	@Transient
	public BigDecimal getThroughputSubscriptionVor() {
		return accountingPricingServiceTypeSummary.getThroughputSubscriptionVor();
	}

	@Transient
	public BigDecimal getThroughputSubscriptionVorHistorical() {
		return accountingPricingServiceTypeSummary.getThroughputSubscriptionVorHistorical();
	}

	@Transient
	public BigDecimal getThroughputTransactionalNonVor() {
		return accountingPricingServiceTypeSummary.getThroughputTransactionalNonVor();
	}

	@Transient
	public BigDecimal getThroughputTransactionalNonVorHistorical() {
		return accountingPricingServiceTypeSummary.getThroughputTransactionalNonVorHistorical();
	}

	@Transient
	public BigDecimal getThroughputTransactionalVor() {
		return accountingPricingServiceTypeSummary.getThroughputTransactionalVor();
	}

	@Transient
	public BigDecimal getThroughputTransactionalVorHistorical() {
		return accountingPricingServiceTypeSummary.getThroughputTransactionalVorHistorical();
	}



	@Transient
	public BigDecimal getCreditAchWithdrawableReturn() {
		return creditDebitRegisterTransactionsSummary.getCreditAchWithdrawableReturn();
	}

	@Transient
	public BigDecimal getCreditAchWithdrawableReturnHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditAchWithdrawableReturnHistorical();
	}

	@Transient
	public BigDecimal getCreditAdvance() {
		return creditDebitRegisterTransactionsSummary.getCreditAdvance();
	}

	@Transient
	public BigDecimal getCreditAdvanceHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditAdvanceHistorical();
	}

	@Transient
	public BigDecimal getCreditAssignmentPaymentReversal() {
		return creditDebitRegisterTransactionsSummary.getCreditAssignmentPaymentReversal();
	}

	@Transient
	public BigDecimal getCreditAssignmentPaymentReversalHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditAssignmentPaymentReversalHistorical();
	}

	@Transient
	public BigDecimal getCreditBackgroundCheckRefund() {
		return creditDebitRegisterTransactionsSummary.getCreditBackgroundCheckRefund();
	}

	@Transient
	public BigDecimal getCreditBackgroundCheckRefundHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditBackgroundCheckRefundHistorical();
	}

	@Transient
	public BigDecimal getCreditDrugTestRefund() {
		return creditDebitRegisterTransactionsSummary.getCreditDrugTestRefund();
	}

	@Transient
	public BigDecimal getCreditDrugTestRefundHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditDrugTestRefundHistorical();
	}

	@Transient
	public BigDecimal getCreditFeeRefundNvor() {
		return creditDebitRegisterTransactionsSummary.getCreditFeeRefundNvor();
	}

	@Transient
	public BigDecimal getCreditFeeRefundNvorHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditFeeRefundNvorHistorical();
	}

	@Transient
	public BigDecimal getCreditFeeRefundVor() {
		return creditDebitRegisterTransactionsSummary.getCreditFeeRefundVor();
	}

	@Transient
	public BigDecimal getCreditFeeRefundVorHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditFeeRefundVorHistorical();
	}

	@Transient
	public BigDecimal getCreditGeneralRefund() {
		return creditDebitRegisterTransactionsSummary.getCreditGeneralRefund();
	}

	@Transient
	public BigDecimal getCreditGeneralRefundHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditGeneralRefundHistorical();
	}

	@Transient
	public BigDecimal getCreditMarketingPayment() {
		return creditDebitRegisterTransactionsSummary.getCreditMarketingPayment();
	}

	@Transient
	public BigDecimal getCreditMarketingPaymentHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditMarketingPaymentHistorical();
	}

	@Transient
	public BigDecimal getCreditMiscellaneous() {
		return creditDebitRegisterTransactionsSummary.getCreditMiscellaneous();
	}

	@Transient
	public BigDecimal getCreditMiscellaneousHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditMiscellaneousHistorical();
	}

	@Transient
	public BigDecimal getCreditAdjustment() {
		return creditDebitRegisterTransactionsSummary.getCreditAdjustment();
	}

	@Transient
	public BigDecimal getCreditAdjustmentHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditAdjustmentHistorical();
	}

	@Transient
	public BigDecimal getCreditReclassToAvailableToWithdrawal() {
		return creditDebitRegisterTransactionsSummary.getCreditReclassToAvailableToWithdrawal();
	}

	@Transient
	public BigDecimal getCreditReclassToAvailableToWithdrawalHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditReclassToAvailableToWithdrawalHistorical();
	}

	@Transient
	public BigDecimal getCreditFastFunds() {
		return creditDebitRegisterTransactionsSummary.getCreditFastFunds();
	}

	@Transient
	public BigDecimal getCreditFastFundsHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditFastFundsHistorical();
	}

	@Transient
	public BigDecimal getCreditFastFundsFeeRefund() {
		return creditDebitRegisterTransactionsSummary.getCreditFastFundsFeeRefund();
	}

	@Transient
	public BigDecimal getCreditFastFundsFeeRefundHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditFastFundsFeeRefundHistorical();
	}

	@Transient
	public BigDecimal getDebitAchDepositReturn() {
		return creditDebitRegisterTransactionsSummary.getDebitAchDepositReturn();
	}

	@Transient
	public BigDecimal getDebitAchDepositReturnHistorical() {
		return creditDebitRegisterTransactionsSummary.getDebitAchDepositReturnHistorical();
	}

	@Transient
	public BigDecimal getDebitAdvanceRepayment() {
		return creditDebitRegisterTransactionsSummary.getDebitAdvanceRepayment();
	}

	@Transient
	public BigDecimal getDebitAdvanceRepaymentHistorical() {
		return creditDebitRegisterTransactionsSummary.getDebitAdvanceRepaymentHistorical();
	}

	@Transient
	public BigDecimal getDebitAssignmentPaymentReversal() {
		return creditDebitRegisterTransactionsSummary.getDebitAssignmentPaymentReversal();
	}

	@Transient
	public BigDecimal getDebitAssignmentPaymentReversalHistorical() {
		return creditDebitRegisterTransactionsSummary.getDebitAssignmentPaymentReversalHistorical();
	}

	@Transient
	public BigDecimal getDebitCreditCardChargeback() {
		return creditDebitRegisterTransactionsSummary.getDebitCreditCardChargeback();
	}

	@Transient
	public BigDecimal getDebitCreditCardChargebackHistorical() {
		return creditDebitRegisterTransactionsSummary.getDebitCreditCardChargebackHistorical();
	}

	@Transient
	public BigDecimal getDebitCreditCardRefund() {
		return creditDebitRegisterTransactionsSummary.getDebitCreditCardRefund();
	}

	@Transient
	public BigDecimal getDebitCreditCardRefundHistorical() {
		return creditDebitRegisterTransactionsSummary.getDebitCreditCardRefundHistorical();
	}

	@Transient
	public BigDecimal getDebitMiscellaneous() {
		return creditDebitRegisterTransactionsSummary.getDebitMiscellaneous();
	}

	@Transient
	public BigDecimal getDebitMiscellaneousHistorical() {
		return creditDebitRegisterTransactionsSummary.getDebitMiscellaneousHistorical();
	}

	@Transient
	public BigDecimal getDebitAdjustment() {
		return creditDebitRegisterTransactionsSummary.getDebitAdjustment();
	}

	@Transient
	public BigDecimal getDebitAdjustmentHistorical() {
		return creditDebitRegisterTransactionsSummary.getDebitAdjustmentHistorical();
	}

	@Transient
	public BigDecimal getDebitReclassFromAvailableToSpend() {
		return creditDebitRegisterTransactionsSummary.getDebitReclassFromAvailableToSpend();
	}

	@Transient
	public BigDecimal getDebitReclassFromAvailableToSpendHistorical() {
		return creditDebitRegisterTransactionsSummary.getDebitReclassFromAvailableToSpendHistorical();
	}

	@Transient
	public BigDecimal getDebitFastFunds() {
		return creditDebitRegisterTransactionsSummary.getDebitFastFunds();
	}

	@Transient
	public BigDecimal getDebitFastFundsHistorical() {
		return creditDebitRegisterTransactionsSummary.getDebitFastFundsHistorical();
	}

	@Transient
	public BigDecimal getCreditFeeRefundVorSoftware() {
		return creditDebitRegisterTransactionsSummary.getCreditFeeRefundVorSoftware();
	}

	@Transient
	public BigDecimal getCreditFeeRefundVorSoftwareHistorical() {
		return creditDebitRegisterTransactionsSummary.getCreditFeeRefundVorSoftwareHistorical();
	}

	@Transient
	public BigDecimal getProfessionalServiceFeeReceivables() {
		return accountingPricingServiceTypeSummary.getProfessionalServiceFeeReceivables();
	}

	@Transient
	public BigDecimal getProfessionalServiceFeeReceivablesHistorical() {
		return accountingPricingServiceTypeSummary.getProfessionalServiceFeeReceivablesHistorical();
	}

	@Transient
	public BigDecimal getFastFundsFeeReceivables() {
		return accountingPricingServiceTypeSummary.getFastFundsFeeReceivables();
	}

	@Transient
	public BigDecimal getFastFundsFeeReceivablesHistorical() {
		return accountingPricingServiceTypeSummary.getFastFundsFeeReceivablesHistorical();
	}

	@Transient
	public BigDecimal getSubscriptionCreditMemoReceivables() {
		return accountingPricingServiceTypeSummary.getSubscriptionCreditMemoReceivables();
	}

	@Transient
	public BigDecimal getSubscriptionCreditMemoReceivablesHistorical() {
		return accountingPricingServiceTypeSummary.getSubscriptionCreditMemoReceivablesHistorical();
	}

	@Transient
	public BigDecimal getProfServicesCreditMemoReceivables() {
		return accountingPricingServiceTypeSummary.getProfServicesCreditMemoReceivables();
	}

	@Transient
	public BigDecimal getProfServicesCreditMemoReceivablesHistorical() {
		return accountingPricingServiceTypeSummary.getProfServicesCreditMemoReceivablesHistorical();
	}

	@Transient
	public BigDecimal getRevenueProfessionalServiceFee() {
		return accountingPricingServiceTypeSummary.getRevenueProfessionalServiceFee();
	}

	@Transient
	public BigDecimal getRevenueProfessionalServiceFeeHistorical() {
		return accountingPricingServiceTypeSummary.getRevenueProfessionalServiceFeeHistorical();
	}

	@Transient
	public BigDecimal getCreditProfessionalServiceFee() {
		return accountingPricingServiceTypeSummary.getCreditProfessionalServiceFee();
	}

	@Transient
	public BigDecimal getCreditProfessionalServiceFeeHistorical() {
		return accountingPricingServiceTypeSummary.getCreditProfessionalServiceFeeHistorical();
	}

	@Transient
	public BigDecimal getDefRevenueProfessionalServiceFee() {
		return accountingPricingServiceTypeSummary.getDefRevenueProfessionalServiceFee();
	}

	@Transient
	public BigDecimal getDefRevenueProfessionalServiceFeeHistorical() {
		return accountingPricingServiceTypeSummary.getDefRevenueProfessionalServiceFeeHistorical();
	}

	@Transient
	public BigDecimal getMoneyOutProfessionalServiceFee() {
		return accountingPricingServiceTypeSummary.getMoneyOutProfessionalServiceFee();
	}

	@Transient
	public BigDecimal getMoneyOutProfessionalServiceFeeHistorical() {
		return accountingPricingServiceTypeSummary.getMoneyOutProfessionalServiceFeeHistorical();
	}

	@Transient
	public BigDecimal calculateTotalMoneyOnSystem() {
		//+ MONEY IN - MONEY OUT - CASH OUT DEBITS + CASH OUT CREDITS
		BigDecimal totalMoneyOnSystemDailyAmount = BigDecimal.ZERO;

		//ADD MONEY IN
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.add(nullSafeAbs(moneyInFastFunds));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.add(nullSafeAbs(moneyInAch));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.add(nullSafeAbs(moneyInChecks));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.add(nullSafeAbs(moneyInCreditCard));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.add(nullSafeAbs(moneyInWire));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.add(nullSafeAbs(moneyInSubscriptionCreditMemo));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.add(nullSafeAbs(moneyInProfServicesCreditMemo));

		//SUBTRACT MONEY OUT
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutWithdrawals));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutNonUSAWithdrawals));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutPayPalWithdrawal));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutGCCWithdrawal));

		if (accountingPricingServiceTypeSummary != null) {
			totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(getMoneyOutTransactionalVorSoftwareFee()));
			totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(getMoneyOutTransactionalVorVorFee()));
			totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(getMoneyOutTransactionalNonVorSoftwareFee()));

			totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(getMoneyOutSubscriptionVorSoftwareFee()));
			totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(getMoneyOutSubscriptionVorVorFee()));
			totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(getMoneyOutSubscriptionNonVorSoftwareFee()));

			totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(getMoneyOutProfessionalServiceFee()));
		}

		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutPayPalFees));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutCreditCardFees));

		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutFastFundsReceivablePayments));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutFastFundsFee));

		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutBackgroundChecks));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutDrugTests));

		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutDepositReturnFee));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutWithdrawalReturnFee));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutLatePaymentFee));
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutMiscellaneousFee));

		//SUBTRACT CASH OUT DEBITS
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.subtract(nullSafeAbs(moneyOutDebitTransactions));

		//ADD CASH OUT CREDITS
		totalMoneyOnSystemDailyAmount = totalMoneyOnSystemDailyAmount.add(nullSafeAbs(moneyOutCreditTransactions));
		return totalMoneyOnSystemDailyAmount.setScale(2, RoundingMode.FLOOR);
	}
}
