package com.workmarket.domains.model.account;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Author: rocio
 */
@Embeddable
public class CreditDebitRegisterTransactionsSummary {

	private static final long serialVersionUID = 1L;

	private BigDecimal debitAchDepositReturn= BigDecimal.ZERO;
	private BigDecimal debitAdvanceRepayment= BigDecimal.ZERO;
	private BigDecimal debitAssignmentPaymentReversal= BigDecimal.ZERO;
	private BigDecimal debitCreditCardChargeback= BigDecimal.ZERO;
	private BigDecimal debitCreditCardRefund= BigDecimal.ZERO;
	private BigDecimal debitReclassFromAvailableToSpend= BigDecimal.ZERO;
	private BigDecimal debitFastFunds = BigDecimal.ZERO;
	private BigDecimal debitMiscellaneous= BigDecimal.ZERO;
	private BigDecimal debitAdjustment= BigDecimal.ZERO;

	private BigDecimal creditAchWithdrawableReturn= BigDecimal.ZERO;
	private BigDecimal creditAdvance= BigDecimal.ZERO;
	private BigDecimal creditAssignmentPaymentReversal = BigDecimal.ZERO;
	private BigDecimal creditBackgroundCheckRefund= BigDecimal.ZERO;
	private BigDecimal creditDrugTestRefund= BigDecimal.ZERO;
	private BigDecimal creditMarketingPayment= BigDecimal.ZERO;
	private BigDecimal creditReclassToAvailableToWithdrawal= BigDecimal.ZERO;
	private BigDecimal creditFastFunds = BigDecimal.ZERO;
	private BigDecimal creditFastFundsFeeRefund = BigDecimal.ZERO;
	private BigDecimal creditMiscellaneous= BigDecimal.ZERO;
	private BigDecimal creditAdjustment= BigDecimal.ZERO;
	private BigDecimal creditFeeRefundVor= BigDecimal.ZERO;
	private BigDecimal creditFeeRefundVorSoftware= BigDecimal.ZERO;
	private BigDecimal creditFeeRefundNvor= BigDecimal.ZERO;
	private BigDecimal creditGeneralRefund= BigDecimal.ZERO;

	private BigDecimal debitAchDepositReturnHistorical= BigDecimal.ZERO;
	private BigDecimal debitAdvanceRepaymentHistorical= BigDecimal.ZERO;
	private BigDecimal debitAssignmentPaymentReversalHistorical= BigDecimal.ZERO;
	private BigDecimal debitCreditCardChargebackHistorical= BigDecimal.ZERO;
	private BigDecimal debitCreditCardRefundHistorical= BigDecimal.ZERO;
	private BigDecimal debitReclassFromAvailableToSpendHistorical= BigDecimal.ZERO;
	private BigDecimal debitFastFundsHistorical = BigDecimal.ZERO;
	private BigDecimal debitMiscellaneousHistorical= BigDecimal.ZERO;
	private BigDecimal debitAdjustmentHistorical= BigDecimal.ZERO;

	private BigDecimal creditAchWithdrawableReturnHistorical= BigDecimal.ZERO;
	private BigDecimal creditAdvanceHistorical= BigDecimal.ZERO;
	private BigDecimal creditAssignmentPaymentReversalHistorical = BigDecimal.ZERO;
	private BigDecimal creditBackgroundCheckRefundHistorical= BigDecimal.ZERO;
	private BigDecimal creditDrugTestRefundHistorical= BigDecimal.ZERO;
	private BigDecimal creditMarketingPaymentHistorical= BigDecimal.ZERO;
	private BigDecimal creditReclassToAvailableToWithdrawalHistorical= BigDecimal.ZERO;
	private BigDecimal creditFastFundsHistorical = BigDecimal.ZERO;
	private BigDecimal creditFastFundsFeeRefundHistorical = BigDecimal.ZERO;
	private BigDecimal creditMiscellaneousHistorical= BigDecimal.ZERO;
	private BigDecimal creditAdjustmentHistorical= BigDecimal.ZERO;
	private BigDecimal creditFeeRefundVorHistorical= BigDecimal.ZERO;
	private BigDecimal creditFeeRefundVorSoftwareHistorical= BigDecimal.ZERO;
	private BigDecimal creditFeeRefundNvorHistorical= BigDecimal.ZERO;
	private BigDecimal creditGeneralRefundHistorical= BigDecimal.ZERO;

	@Column(name = "debit_ach_deposit_return")
	public BigDecimal getDebitAchDepositReturn() {
		return debitAchDepositReturn;
	}

	public void setDebitAchDepositReturn(BigDecimal debitAchDepositReturn) {
		this.debitAchDepositReturn = debitAchDepositReturn;
	}

	@Column(name = "debit_advance_repayment")
	public BigDecimal getDebitAdvanceRepayment() {
		return debitAdvanceRepayment;
	}

	public void setDebitAdvanceRepayment(BigDecimal debitAdvanceRepayment) {
		this.debitAdvanceRepayment = debitAdvanceRepayment;
	}

	@Column(name = "debit_assignment_payment_reversal")
	public BigDecimal getDebitAssignmentPaymentReversal() {
		return debitAssignmentPaymentReversal;
	}

	public void setDebitAssignmentPaymentReversal(BigDecimal debitAssignmentPaymentReversal) {
		this.debitAssignmentPaymentReversal = debitAssignmentPaymentReversal;
	}

	@Column(name = "debit_credit_card_chargeback")
	public BigDecimal getDebitCreditCardChargeback() {
		return debitCreditCardChargeback;
	}

	public void setDebitCreditCardChargeback(BigDecimal debitCreditCardChargeback) {
		this.debitCreditCardChargeback = debitCreditCardChargeback;
	}

	@Column(name = "debit_credit_card_refund")
	public BigDecimal getDebitCreditCardRefund() {
		return debitCreditCardRefund;
	}

	public void setDebitCreditCardRefund(BigDecimal debitCreditCardRefund) {
		this.debitCreditCardRefund = debitCreditCardRefund;
	}

	@Column(name = "debit_reclass_from_available_to_spend")
	public BigDecimal getDebitReclassFromAvailableToSpend() {
		return debitReclassFromAvailableToSpend;
	}

	public void setDebitReclassFromAvailableToSpend(BigDecimal debitReclassFromAvailableToSpend) {
		this.debitReclassFromAvailableToSpend = debitReclassFromAvailableToSpend;
	}

	@Column(name = "debit_fast_funds")
	public BigDecimal getDebitFastFunds() {
		return debitFastFunds;
	}

	public void setDebitFastFunds(BigDecimal debitFastFunds) {
		this.debitFastFunds = debitFastFunds;
	}

	@Column(name = "debit_miscellaneous")
	public BigDecimal getDebitMiscellaneous() {
		return debitMiscellaneous;
	}

	public void setDebitMiscellaneous(BigDecimal debitMiscellaneous) {
		this.debitMiscellaneous = debitMiscellaneous;
	}

	@Column(name = "debit_adjustment")
	public BigDecimal getDebitAdjustment() {
		return debitAdjustment;
	}

	public void setDebitAdjustment(BigDecimal debitAdjustment) {
		this.debitAdjustment = debitAdjustment;
	}

	@Column(name = "credit_ach_withdrawable_return")
	public BigDecimal getCreditAchWithdrawableReturn() {
		return creditAchWithdrawableReturn;
	}

	public void setCreditAchWithdrawableReturn(BigDecimal creditAchWithdrawableReturn) {
		this.creditAchWithdrawableReturn = creditAchWithdrawableReturn;
	}

	@Column(name = "credit_advance")
	public BigDecimal getCreditAdvance() {
		return creditAdvance;
	}

	public void setCreditAdvance(BigDecimal creditAdvance) {
		this.creditAdvance = creditAdvance;
	}

	@Column(name = "credit_assignment_payment_reversal")
	public BigDecimal getCreditAssignmentPaymentReversal() {
		return creditAssignmentPaymentReversal;
	}

	public void setCreditAssignmentPaymentReversal(BigDecimal creditAssignmentPaymentReversal) {
		this.creditAssignmentPaymentReversal = creditAssignmentPaymentReversal;
	}

	@Column(name = "credit_background_check_refund")
	public BigDecimal getCreditBackgroundCheckRefund() {
		return creditBackgroundCheckRefund;
	}

	public void setCreditBackgroundCheckRefund(BigDecimal creditBackgroundCheckRefund) {
		this.creditBackgroundCheckRefund = creditBackgroundCheckRefund;
	}

	@Column(name = "credit_drug_test_refund")
	public BigDecimal getCreditDrugTestRefund() {
		return creditDrugTestRefund;
	}

	public void setCreditDrugTestRefund(BigDecimal creditDrugTestRefund) {
		this.creditDrugTestRefund = creditDrugTestRefund;
	}

	@Column(name = "credit_marketing_payment")
	public BigDecimal getCreditMarketingPayment() {
		return creditMarketingPayment;
	}

	public void setCreditMarketingPayment(BigDecimal creditMarketingPayment) {
		this.creditMarketingPayment = creditMarketingPayment;
	}

	@Column(name = "credit_reclass_to_available_to_withdrawal")
	public BigDecimal getCreditReclassToAvailableToWithdrawal() {
		return creditReclassToAvailableToWithdrawal;
	}

	public void setCreditReclassToAvailableToWithdrawal(BigDecimal creditReclassToAvailableToWithdrawal) {
		this.creditReclassToAvailableToWithdrawal = creditReclassToAvailableToWithdrawal;
	}

	@Column(name = "credit_fast_funds")
	public BigDecimal getCreditFastFunds() {
		return creditFastFunds;
	}

	public void setCreditFastFunds(BigDecimal creditFastFunds) {
		this.creditFastFunds = creditFastFunds;
	}

	@Column(name = "credit_fast_funds_fee_refund")
	public BigDecimal getCreditFastFundsFeeRefund() {
		return creditFastFundsFeeRefund;
	}

	public void setCreditFastFundsFeeRefund(BigDecimal creditFastFundsFeeRefund) {
		this.creditFastFundsFeeRefund = creditFastFundsFeeRefund;
	}

	@Column(name = "credit_miscellaneous")
	public BigDecimal getCreditMiscellaneous() {
		return creditMiscellaneous;
	}

	public void setCreditMiscellaneous(BigDecimal creditMiscellaneous) {
		this.creditMiscellaneous = creditMiscellaneous;
	}

	@Column(name = "credit_adjustment")
	public BigDecimal getCreditAdjustment() {
		return creditAdjustment;
	}

	public void setCreditAdjustment(BigDecimal creditAdjustment) {
		this.creditAdjustment = creditAdjustment;
	}

	@Column(name = "credit_fee_refund_vor")
	public BigDecimal getCreditFeeRefundVor() {
		return creditFeeRefundVor;
	}

	public void setCreditFeeRefundVor(BigDecimal creditFeeRefundVor) {
		this.creditFeeRefundVor = creditFeeRefundVor;
	}

	@Column(name = "credit_fee_refund_nvor")
	public BigDecimal getCreditFeeRefundNvor() {
		return creditFeeRefundNvor;
	}

	public void setCreditFeeRefundNvor(BigDecimal creditFeeRefundNvor) {
		this.creditFeeRefundNvor = creditFeeRefundNvor;
	}

	@Column(name = "credit_general_refund")
	public BigDecimal getCreditGeneralRefund() {
		return creditGeneralRefund;
	}

	public void setCreditGeneralRefund(BigDecimal creditGeneralRefund) {
		this.creditGeneralRefund = creditGeneralRefund;
	}

	@Column(name = "debit_ach_deposit_return_historical")
	public BigDecimal getDebitAchDepositReturnHistorical() {
		return debitAchDepositReturnHistorical;
	}

	public void setDebitAchDepositReturnHistorical(BigDecimal debitAchDepositReturnHistorical) {
		this.debitAchDepositReturnHistorical = debitAchDepositReturnHistorical;
	}

	@Column(name = "debit_advance_repayment_historical")
	public BigDecimal getDebitAdvanceRepaymentHistorical() {
		return debitAdvanceRepaymentHistorical;
	}

	public void setDebitAdvanceRepaymentHistorical(BigDecimal debitAdvanceRepaymentHistorical) {
		this.debitAdvanceRepaymentHistorical = debitAdvanceRepaymentHistorical;
	}

	@Column(name = "debit_assignment_payment_reversal_historical")
	public BigDecimal getDebitAssignmentPaymentReversalHistorical() {
		return debitAssignmentPaymentReversalHistorical;
	}

	public void setDebitAssignmentPaymentReversalHistorical(BigDecimal debitAssignmentPaymentReversalHistorical) {
		this.debitAssignmentPaymentReversalHistorical = debitAssignmentPaymentReversalHistorical;
	}

	@Column(name = "debit_credit_card_chargeback_historical")
	public BigDecimal getDebitCreditCardChargebackHistorical() {
		return debitCreditCardChargebackHistorical;
	}

	public void setDebitCreditCardChargebackHistorical(BigDecimal debitCreditCardChargebackHistorical) {
		this.debitCreditCardChargebackHistorical = debitCreditCardChargebackHistorical;
	}

	@Column(name = "debit_credit_card_refund_historical")
	public BigDecimal getDebitCreditCardRefundHistorical() {
		return debitCreditCardRefundHistorical;
	}

	public void setDebitCreditCardRefundHistorical(BigDecimal debitCreditCardRefundHistorical) {
		this.debitCreditCardRefundHistorical = debitCreditCardRefundHistorical;
	}

	@Column(name = "debit_reclass_from_available_to_spend_historical")
	public BigDecimal getDebitReclassFromAvailableToSpendHistorical() {
		return debitReclassFromAvailableToSpendHistorical;
	}

	public void setDebitReclassFromAvailableToSpendHistorical(BigDecimal debitReclassFromAvailableToSpendHistorical) {
		this.debitReclassFromAvailableToSpendHistorical = debitReclassFromAvailableToSpendHistorical;
	}

	@Column(name = "debit_fast_funds_historical")
	public BigDecimal getDebitFastFundsHistorical() {
		return debitFastFundsHistorical;
	}

	public void setDebitFastFundsHistorical(BigDecimal debitFastFundsHistorical) {
		this.debitFastFundsHistorical = debitFastFundsHistorical;
	}

	@Column(name = "debit_miscellaneous_historical")
	public BigDecimal getDebitMiscellaneousHistorical() {
		return debitMiscellaneousHistorical;
	}

	public void setDebitMiscellaneousHistorical(BigDecimal debitMiscellaneousHistorical) {
		this.debitMiscellaneousHistorical = debitMiscellaneousHistorical;
	}

	@Column(name = "debit_adjustment_historical")
	public BigDecimal getDebitAdjustmentHistorical() {
		return debitAdjustmentHistorical;
	}

	public void setDebitAdjustmentHistorical(BigDecimal debitAdjustmentHistorical) {
		this.debitAdjustmentHistorical = debitAdjustmentHistorical;
	}

	@Column(name = "credit_ach_withdrawable_return_historical")
	public BigDecimal getCreditAchWithdrawableReturnHistorical() {
		return creditAchWithdrawableReturnHistorical;
	}

	public void setCreditAchWithdrawableReturnHistorical(BigDecimal creditAchWithdrawableReturnHistorical) {
		this.creditAchWithdrawableReturnHistorical = creditAchWithdrawableReturnHistorical;
	}

	@Column(name = "credit_advance_historical")
	public BigDecimal getCreditAdvanceHistorical() {
		return creditAdvanceHistorical;
	}

	public void setCreditAdvanceHistorical(BigDecimal creditAdvanceHistorical) {
		this.creditAdvanceHistorical = creditAdvanceHistorical;
	}

	@Column(name = "credit_assignment_payment_reversal_historical")
	public BigDecimal getCreditAssignmentPaymentReversalHistorical() {
		return creditAssignmentPaymentReversalHistorical;
	}

	public void setCreditAssignmentPaymentReversalHistorical(BigDecimal creditAssignmentPaymentReversalHistorical) {
		this.creditAssignmentPaymentReversalHistorical = creditAssignmentPaymentReversalHistorical;
	}

	@Column(name = "credit_background_check_refund_historical")
	public BigDecimal getCreditBackgroundCheckRefundHistorical() {
		return creditBackgroundCheckRefundHistorical;
	}

	public void setCreditBackgroundCheckRefundHistorical(BigDecimal creditBackgroundCheckRefundHistorical) {
		this.creditBackgroundCheckRefundHistorical = creditBackgroundCheckRefundHistorical;
	}

	@Column(name = "credit_drug_test_refund_historical")
	public BigDecimal getCreditDrugTestRefundHistorical() {
		return creditDrugTestRefundHistorical;
	}

	public void setCreditDrugTestRefundHistorical(BigDecimal creditDrugTestRefundHistorical) {
		this.creditDrugTestRefundHistorical = creditDrugTestRefundHistorical;
	}

	@Column(name = "credit_marketing_payment_historical")
	public BigDecimal getCreditMarketingPaymentHistorical() {
		return creditMarketingPaymentHistorical;
	}

	public void setCreditMarketingPaymentHistorical(BigDecimal creditMarketingPaymentHistorical) {
		this.creditMarketingPaymentHistorical = creditMarketingPaymentHistorical;
	}

	@Column(name = "credit_reclass_to_available_to_withdrawal_historical")
	public BigDecimal getCreditReclassToAvailableToWithdrawalHistorical() {
		return creditReclassToAvailableToWithdrawalHistorical;
	}

	public void setCreditReclassToAvailableToWithdrawalHistorical(BigDecimal creditReclassToAvailableToWithdrawalHistorical) {
		this.creditReclassToAvailableToWithdrawalHistorical = creditReclassToAvailableToWithdrawalHistorical;
	}

	@Column(name = "credit_fast_funds_historical")
	public BigDecimal getCreditFastFundsHistorical() {
		return creditFastFundsHistorical;
	}

	public void setCreditFastFundsHistorical(BigDecimal creditFastFundsHistorical) {
		this.creditFastFundsHistorical = creditFastFundsHistorical;
	}

	@Column(name = "credit_fast_funds_fee_refund_historical")
	public BigDecimal getCreditFastFundsFeeRefundHistorical() {
		return creditFastFundsFeeRefundHistorical;
	}

	public void setCreditFastFundsFeeRefundHistorical(BigDecimal creditFastFundsFeeRefundHistorical) {
		this.creditFastFundsFeeRefundHistorical = creditFastFundsFeeRefundHistorical;
	}

	@Column(name = "credit_miscellaneous_historical")
	public BigDecimal getCreditMiscellaneousHistorical() {
		return creditMiscellaneousHistorical;
	}

	public void setCreditMiscellaneousHistorical(BigDecimal creditMiscellaneousHistorical) {
		this.creditMiscellaneousHistorical = creditMiscellaneousHistorical;
	}

	@Column(name = "credit_adjustment_historical")
	public BigDecimal getCreditAdjustmentHistorical() {
		return creditAdjustmentHistorical;
	}

	public void setCreditAdjustmentHistorical(BigDecimal creditAdjustmentHistorical) {
		this.creditAdjustmentHistorical = creditAdjustmentHistorical;
	}

	@Column(name = "credit_fee_refund_vor_historical")
	public BigDecimal getCreditFeeRefundVorHistorical() {
		return creditFeeRefundVorHistorical;
	}

	public void setCreditFeeRefundVorHistorical(BigDecimal creditFeeRefundVorHistorical) {
		this.creditFeeRefundVorHistorical = creditFeeRefundVorHistorical;
	}

	@Column(name = "credit_fee_refund_nvor_historical")
	public BigDecimal getCreditFeeRefundNvorHistorical() {
		return creditFeeRefundNvorHistorical;
	}

	public void setCreditFeeRefundNvorHistorical(BigDecimal creditFeeRefundNvorHistorical) {
		this.creditFeeRefundNvorHistorical = creditFeeRefundNvorHistorical;
	}

	@Column(name = "credit_general_refund_historical")
	public BigDecimal getCreditGeneralRefundHistorical() {
		return creditGeneralRefundHistorical;
	}

	public void setCreditGeneralRefundHistorical(BigDecimal creditGeneralRefundHistorical) {
		this.creditGeneralRefundHistorical = creditGeneralRefundHistorical;
	}

	@Column(name = "credit_fee_refund_vor_software")
	public BigDecimal getCreditFeeRefundVorSoftware() {
		return creditFeeRefundVorSoftware;
	}

	public void setCreditFeeRefundVorSoftware(BigDecimal creditFeeRefundVorSoftware) {
		this.creditFeeRefundVorSoftware = creditFeeRefundVorSoftware;
	}

	@Column(name = "credit_fee_refund_vor_software_historical")
	public BigDecimal getCreditFeeRefundVorSoftwareHistorical() {
		return creditFeeRefundVorSoftwareHistorical;
	}

	public void setCreditFeeRefundVorSoftwareHistorical(BigDecimal creditFeeRefundVorSoftwareHistorical) {
		this.creditFeeRefundVorSoftwareHistorical = creditFeeRefundVorSoftwareHistorical;
	}
}