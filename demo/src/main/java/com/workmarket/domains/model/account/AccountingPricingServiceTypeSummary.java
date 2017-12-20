package com.workmarket.domains.model.account;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Author: rocio
 */
@Embeddable
public class AccountingPricingServiceTypeSummary {

	private static final long serialVersionUID = 1L;

	private BigDecimal throughputTransactionalVor = BigDecimal.ZERO;
	private BigDecimal throughputTransactionalNonVor = BigDecimal.ZERO;
	private BigDecimal throughputSubscriptionVor = BigDecimal.ZERO;
	private BigDecimal throughputSubscriptionNonVor = BigDecimal.ZERO;
	private BigDecimal throughputTransactionalVorHistorical = BigDecimal.ZERO;
	private BigDecimal throughputTransactionalNonVorHistorical = BigDecimal.ZERO;
	private BigDecimal throughputSubscriptionVorHistorical = BigDecimal.ZERO;
	private BigDecimal throughputSubscriptionNonVorHistorical = BigDecimal.ZERO;

	private BigDecimal revenueSubscriptionVorSoftwareFee = BigDecimal.ZERO;
	private BigDecimal revenueSubscriptionVorVorFee = BigDecimal.ZERO;
	private BigDecimal revenueSubscriptionNonVorSoftwareFee = BigDecimal.ZERO;
	private BigDecimal revenueProfessionalServiceFee = BigDecimal.ZERO;
	private BigDecimal revenueSubscriptionVorSoftwareFeeHistorical = BigDecimal.ZERO;
	private BigDecimal revenueSubscriptionVorVorFeeHistorical = BigDecimal.ZERO;
	private BigDecimal revenueSubscriptionNonVorSoftwareFeeHistorical = BigDecimal.ZERO;
	private BigDecimal revenueProfessionalServiceFeeHistorical = BigDecimal.ZERO;

	private BigDecimal paidSubscriptionInvoiceCredit = BigDecimal.ZERO;
	private BigDecimal creditSubscriptionVorSoftwareFee = BigDecimal.ZERO;
	private BigDecimal creditSubscriptionVorVorFee = BigDecimal.ZERO;
	private BigDecimal creditSubscriptionNonVorSoftwareFee = BigDecimal.ZERO;
	private BigDecimal creditProfessionalServiceFee = BigDecimal.ZERO;

	private BigDecimal paidSubscriptionInvoiceCreditHistorical = BigDecimal.ZERO;
	private BigDecimal creditSubscriptionVorSoftwareFeeHistorical = BigDecimal.ZERO;
	private BigDecimal creditSubscriptionVorVorFeeHistorical = BigDecimal.ZERO;
	private BigDecimal creditSubscriptionNonVorSoftwareFeeHistorical = BigDecimal.ZERO;
	private BigDecimal creditProfessionalServiceFeeHistorical = BigDecimal.ZERO;


	private BigDecimal defRevenueSubscriptionVorSoftwareFee = BigDecimal.ZERO;
	private BigDecimal defRevenueSubscriptionVorVorFee = BigDecimal.ZERO;
	private BigDecimal defRevenueSubscriptionNonVorSoftwareFee = BigDecimal.ZERO;
	private BigDecimal defRevenueSubscriptionVorSoftwareFeeHistorical = BigDecimal.ZERO;
	private BigDecimal defRevenueSubscriptionVorVorFeeHistorical = BigDecimal.ZERO;
	private BigDecimal defRevenueSubscriptionNonVorSoftwareFeeHistorical = BigDecimal.ZERO;
	private BigDecimal defRevenueProfessionalServiceFee = BigDecimal.ZERO;
	private BigDecimal defRevenueProfessionalServiceFeeHistorical = BigDecimal.ZERO;

	private BigDecimal subscriptionFeeReceivables = BigDecimal.ZERO;
	private BigDecimal subscriptionFeeReceivablesHistorical = BigDecimal.ZERO;
	private BigDecimal professionalServiceFeeReceivables = BigDecimal.ZERO;
	private BigDecimal professionalServiceFeeReceivablesHistorical = BigDecimal.ZERO;
	private BigDecimal fastFundsFeeReceivables = BigDecimal.ZERO;
	private BigDecimal fastFundsFeeReceivablesHistorical = BigDecimal.ZERO;
	private BigDecimal subscriptionCreditMemoReceivables = BigDecimal.ZERO;
	private BigDecimal subscriptionCreditMemoReceivablesHistorical = BigDecimal.ZERO;
	private BigDecimal profServicesCreditMemoReceivables = BigDecimal.ZERO;
	private BigDecimal profServicesCreditMemoReceivablesHistorical = BigDecimal.ZERO;

	private BigDecimal moneyOutTransactionalVorSoftwareFee = BigDecimal.ZERO;
	private BigDecimal moneyOutTransactionalVorVorFee = BigDecimal.ZERO;
	private BigDecimal moneyOutTransactionalNonVorSoftwareFee = BigDecimal.ZERO;
	private BigDecimal moneyOutTransactionalVorSoftwareFeeHistorical = BigDecimal.ZERO;
	private BigDecimal moneyOutTransactionalVorVorFeeHistorical = BigDecimal.ZERO;
	private BigDecimal moneyOutTransactionalNonVorSoftwareFeeHistorical = BigDecimal.ZERO;

	private BigDecimal moneyOutSubscriptionVorSoftwareFee = BigDecimal.ZERO;
	private BigDecimal moneyOutSubscriptionVorVorFee = BigDecimal.ZERO;
	private BigDecimal moneyOutSubscriptionNonVorSoftwareFee = BigDecimal.ZERO;
	private BigDecimal moneyOutSubscriptionVorSoftwareFeeHistorical = BigDecimal.ZERO;
	private BigDecimal moneyOutSubscriptionVorVorFeeHistorical = BigDecimal.ZERO;
	private BigDecimal moneyOutSubscriptionNonVorSoftwareFeeHistorical = BigDecimal.ZERO;

	private BigDecimal moneyOutProfessionalServiceFee = BigDecimal.ZERO;
	private BigDecimal moneyOutProfessionalServiceFeeHistorical = BigDecimal.ZERO;


	@Column(name = "throughput_transactional_vor")
	public BigDecimal getThroughputTransactionalVor() {
		return throughputTransactionalVor;
	}

	public void setThroughputTransactionalVor(BigDecimal throughputTransactionalVor) {
		this.throughputTransactionalVor = throughputTransactionalVor;
	}

	@Column(name = "throughput_transactional_non_vor")
	public BigDecimal getThroughputTransactionalNonVor() {
		return throughputTransactionalNonVor;
	}

	public void setThroughputTransactionalNonVor(BigDecimal throughputTransactionalNonVor) {
		this.throughputTransactionalNonVor = throughputTransactionalNonVor;
	}

	@Column(name = "throughput_subscription_vor")
	public BigDecimal getThroughputSubscriptionVor() {
		return throughputSubscriptionVor;
	}

	public void setThroughputSubscriptionVor(BigDecimal throughputSubscriptionVor) {
		this.throughputSubscriptionVor = throughputSubscriptionVor;
	}

	@Column(name = "throughput_subscription_non_vor")
	public BigDecimal getThroughputSubscriptionNonVor() {
		return throughputSubscriptionNonVor;
	}

	public void setThroughputSubscriptionNonVor(BigDecimal throughputSubscriptionNonVor) {
		this.throughputSubscriptionNonVor = throughputSubscriptionNonVor;
	}

	@Column(name = "throughput_transactional_vor_historical")
	public BigDecimal getThroughputTransactionalVorHistorical() {
		return throughputTransactionalVorHistorical;
	}

	public void setThroughputTransactionalVorHistorical(BigDecimal throughputTransactionalVorHistorical) {
		this.throughputTransactionalVorHistorical = throughputTransactionalVorHistorical;
	}

	@Column(name = "throughput_transactional_non_vor_historical")
	public BigDecimal getThroughputTransactionalNonVorHistorical() {
		return throughputTransactionalNonVorHistorical;
	}

	public void setThroughputTransactionalNonVorHistorical(BigDecimal throughputTransactionalNonVorHistorical) {
		this.throughputTransactionalNonVorHistorical = throughputTransactionalNonVorHistorical;
	}

	@Column(name = "throughput_subscription_vor_historical")
	public BigDecimal getThroughputSubscriptionVorHistorical() {
		return throughputSubscriptionVorHistorical;
	}

	public void setThroughputSubscriptionVorHistorical(BigDecimal throughputSubscriptionVorHistorical) {
		this.throughputSubscriptionVorHistorical = throughputSubscriptionVorHistorical;
	}

	@Column(name = "throughput_subscription_non_vor_historical")
	public BigDecimal getThroughputSubscriptionNonVorHistorical() {
		return throughputSubscriptionNonVorHistorical;
	}

	public void setThroughputSubscriptionNonVorHistorical(BigDecimal throughputSubscriptionNonVorHistorical) {
		this.throughputSubscriptionNonVorHistorical = throughputSubscriptionNonVorHistorical;
	}

	@Column(name = "revenue_subscription_vor_software_fee")
	public BigDecimal getRevenueSubscriptionVorSoftwareFee() {
		return revenueSubscriptionVorSoftwareFee;
	}

	public void setRevenueSubscriptionVorSoftwareFee(BigDecimal revenueSubscriptionVorSoftwareFee) {
		this.revenueSubscriptionVorSoftwareFee = revenueSubscriptionVorSoftwareFee;
	}

	@Column(name = "revenue_subscription_vor_vor_fee")
	public BigDecimal getRevenueSubscriptionVorVorFee() {
		return revenueSubscriptionVorVorFee;
	}

	public void setRevenueSubscriptionVorVorFee(BigDecimal revenueSubscriptionVorVorFee) {
		this.revenueSubscriptionVorVorFee = revenueSubscriptionVorVorFee;
	}

	@Column(name = "revenue_subscription_non_vor_software_fee")
	public BigDecimal getRevenueSubscriptionNonVorSoftwareFee() {
		return revenueSubscriptionNonVorSoftwareFee;
	}

	public void setRevenueSubscriptionNonVorSoftwareFee(BigDecimal revenueSubscriptionNonVorSoftwareFee) {
		this.revenueSubscriptionNonVorSoftwareFee = revenueSubscriptionNonVorSoftwareFee;
	}

	@Column(name = "revenue_subscription_vor_software_fee_historical")
	public BigDecimal getRevenueSubscriptionVorSoftwareFeeHistorical() {
		return revenueSubscriptionVorSoftwareFeeHistorical;
	}

	public void setRevenueSubscriptionVorSoftwareFeeHistorical(BigDecimal revenueSubscriptionVorSoftwareFeeHistorical) {
		this.revenueSubscriptionVorSoftwareFeeHistorical = revenueSubscriptionVorSoftwareFeeHistorical;
	}

	@Column(name = "revenue_subscription_vor_vor_fee_historical")
	public BigDecimal getRevenueSubscriptionVorVorFeeHistorical() {
		return revenueSubscriptionVorVorFeeHistorical;
	}

	public void setRevenueSubscriptionVorVorFeeHistorical(BigDecimal revenueSubscriptionVorVorFeeHistorical) {
		this.revenueSubscriptionVorVorFeeHistorical = revenueSubscriptionVorVorFeeHistorical;
	}

	@Column(name = "revenue_subscription_non_vor_software_fee_historical")
	public BigDecimal getRevenueSubscriptionNonVorSoftwareFeeHistorical() {
		return revenueSubscriptionNonVorSoftwareFeeHistorical;
	}

	public void setRevenueSubscriptionNonVorSoftwareFeeHistorical(BigDecimal revenueSubscriptionNonVorSoftwareFeeHistorical) {
		this.revenueSubscriptionNonVorSoftwareFeeHistorical = revenueSubscriptionNonVorSoftwareFeeHistorical;
	}

	@Column(name = "paid_subscription_invoice_credit")
	public BigDecimal getPaidSubscriptionInvoiceCredit() {
		return paidSubscriptionInvoiceCredit;
	}

	public void setPaidSubscriptionInvoiceCredit(BigDecimal paidSubscriptionInvoiceCredit) {
		this.paidSubscriptionInvoiceCredit = paidSubscriptionInvoiceCredit;
	}

	@Column(name = "paid_subscription_invoice_credit_historical")
	public BigDecimal getPaidSubscriptionInvoiceCreditHistorical() {
		return paidSubscriptionInvoiceCreditHistorical;
	}

	public void setPaidSubscriptionInvoiceCreditHistorical(BigDecimal paidSubscriptionInvoiceCreditHistorical) {
		this.paidSubscriptionInvoiceCreditHistorical = paidSubscriptionInvoiceCreditHistorical;
	}

	@Column(name = "credit_subscription_vor_software_fee")
	public BigDecimal getCreditSubscriptionVorSoftwareFee() {
		return creditSubscriptionVorSoftwareFee;
	}

	public void setCreditSubscriptionVorSoftwareFee(BigDecimal creditSubscriptionVorSoftwareFee) {
		this.creditSubscriptionVorSoftwareFee = creditSubscriptionVorSoftwareFee;
	}

	@Column(name = "credit_subscription_vor_vor_fee")
	public BigDecimal getCreditSubscriptionVorVorFee() {
		return creditSubscriptionVorVorFee;
	}

	public void setCreditSubscriptionVorVorFee(BigDecimal creditSubscriptionVorVorFee) {
		this.creditSubscriptionVorVorFee = creditSubscriptionVorVorFee;
	}

	@Column(name = "credit_subscription_non_vor_software_fee")
	public BigDecimal getCreditSubscriptionNonVorSoftwareFee() {
		return creditSubscriptionNonVorSoftwareFee;
	}

	public void setCreditSubscriptionNonVorSoftwareFee(BigDecimal creditSubscriptionNonVorSoftwareFee) {
		this.creditSubscriptionNonVorSoftwareFee = creditSubscriptionNonVorSoftwareFee;
	}

	@Column(name = "credit_subscription_vor_software_fee_historical")
	public BigDecimal getCreditSubscriptionVorSoftwareFeeHistorical() {
		return creditSubscriptionVorSoftwareFeeHistorical;
	}

	public void setCreditSubscriptionVorSoftwareFeeHistorical(BigDecimal creditSubscriptionVorSoftwareFeeHistorical) {
		this.creditSubscriptionVorSoftwareFeeHistorical = creditSubscriptionVorSoftwareFeeHistorical;
	}

	@Column(name = "credit_subscription_vor_vor_fee_historical")
	public BigDecimal getCreditSubscriptionVorVorFeeHistorical() {
		return creditSubscriptionVorVorFeeHistorical;
	}

	public void setCreditSubscriptionVorVorFeeHistorical(BigDecimal creditSubscriptionVorVorFeeHistorical) {
		this.creditSubscriptionVorVorFeeHistorical = creditSubscriptionVorVorFeeHistorical;
	}

	@Column(name = "credit_subscription_non_vor_software_fee_historical")
	public BigDecimal getCreditSubscriptionNonVorSoftwareFeeHistorical() {
		return creditSubscriptionNonVorSoftwareFeeHistorical;
	}

	public void setCreditSubscriptionNonVorSoftwareFeeHistorical(BigDecimal creditSubscriptionNonVorSoftwareFeeHistorical) {
		this.creditSubscriptionNonVorSoftwareFeeHistorical = creditSubscriptionNonVorSoftwareFeeHistorical;
	}

	@Column(name = "def_revenue_subscription_vor_software_fee")
	public BigDecimal getDefRevenueSubscriptionVorSoftwareFee() {
		return defRevenueSubscriptionVorSoftwareFee;
	}

	public void setDefRevenueSubscriptionVorSoftwareFee(BigDecimal defRevenueSubscriptionVorSoftwareFee) {
		this.defRevenueSubscriptionVorSoftwareFee = defRevenueSubscriptionVorSoftwareFee;
	}

	@Column(name = "def_revenue_subscription_vor_vor_fee")
	public BigDecimal getDefRevenueSubscriptionVorVorFee() {
		return defRevenueSubscriptionVorVorFee;
	}

	public void setDefRevenueSubscriptionVorVorFee(BigDecimal defRevenueSubscriptionVorVorFee) {
		this.defRevenueSubscriptionVorVorFee = defRevenueSubscriptionVorVorFee;
	}

	@Column(name = "def_revenue_subscription_non_vor_software_fee")
	public BigDecimal getDefRevenueSubscriptionNonVorSoftwareFee() {
		return defRevenueSubscriptionNonVorSoftwareFee;
	}

	public void setDefRevenueSubscriptionNonVorSoftwareFee(BigDecimal defRevenueSubscriptionNonVorSoftwareFee) {
		this.defRevenueSubscriptionNonVorSoftwareFee = defRevenueSubscriptionNonVorSoftwareFee;
	}

	@Column(name = "def_revenue_subscription_vor_software_fee_historical")
	public BigDecimal getDefRevenueSubscriptionVorSoftwareFeeHistorical() {
		return defRevenueSubscriptionVorSoftwareFeeHistorical;
	}

	public void setDefRevenueSubscriptionVorSoftwareFeeHistorical(BigDecimal defRevenueSubscriptionVorSoftwareFeeHistorical) {
		this.defRevenueSubscriptionVorSoftwareFeeHistorical = defRevenueSubscriptionVorSoftwareFeeHistorical;
	}

	@Column(name = "def_revenue_subscription_vor_vor_fee_historical")
	public BigDecimal getDefRevenueSubscriptionVorVorFeeHistorical() {
		return defRevenueSubscriptionVorVorFeeHistorical;
	}

	public void setDefRevenueSubscriptionVorVorFeeHistorical(BigDecimal defRevenueSubscriptionVorVorFeeHistorical) {
		this.defRevenueSubscriptionVorVorFeeHistorical = defRevenueSubscriptionVorVorFeeHistorical;
	}

	@Column(name = "def_revenue_subscription_non_vor_software_fee_historical")
	public BigDecimal getDefRevenueSubscriptionNonVorSoftwareFeeHistorical() {
		return defRevenueSubscriptionNonVorSoftwareFeeHistorical;
	}

	public void setDefRevenueSubscriptionNonVorSoftwareFeeHistorical(BigDecimal defRevenueSubscriptionNonVorSoftwareFeeHistorical) {
		this.defRevenueSubscriptionNonVorSoftwareFeeHistorical = defRevenueSubscriptionNonVorSoftwareFeeHistorical;
	}

	@Column(name = "subscription_fee_receivables")
	public BigDecimal getSubscriptionFeeReceivables() {
		return subscriptionFeeReceivables;
	}

	public void setSubscriptionFeeReceivables(BigDecimal subscriptionFeeReceivables) {
		this.subscriptionFeeReceivables = subscriptionFeeReceivables;
	}

	@Column(name = "subscription_fee_receivables_historical")
	public BigDecimal getSubscriptionFeeReceivablesHistorical() {
		return subscriptionFeeReceivablesHistorical;
	}

	public void setSubscriptionFeeReceivablesHistorical(BigDecimal subscriptionFeeReceivablesHistorical) {
		this.subscriptionFeeReceivablesHistorical = subscriptionFeeReceivablesHistorical;
	}

	@Column(name = "money_out_transactional_vor_software_fee")
	public BigDecimal getMoneyOutTransactionalVorSoftwareFee() {
		return moneyOutTransactionalVorSoftwareFee;
	}

	public void setMoneyOutTransactionalVorSoftwareFee(BigDecimal moneyOutTransactionalVorSoftwareFee) {
		this.moneyOutTransactionalVorSoftwareFee = moneyOutTransactionalVorSoftwareFee;
	}

	@Column(name = "money_out_transactional_vor_vor_fee")
	public BigDecimal getMoneyOutTransactionalVorVorFee() {
		return moneyOutTransactionalVorVorFee;
	}

	public void setMoneyOutTransactionalVorVorFee(BigDecimal moneyOutTransactionalVorVorFee) {
		this.moneyOutTransactionalVorVorFee = moneyOutTransactionalVorVorFee;
	}

	@Column(name = "money_out_transactional_non_vor_software_fee")
	public BigDecimal getMoneyOutTransactionalNonVorSoftwareFee() {
		return moneyOutTransactionalNonVorSoftwareFee;
	}

	public void setMoneyOutTransactionalNonVorSoftwareFee(BigDecimal moneyOutTransactionalNonVorSoftwareFee) {
		this.moneyOutTransactionalNonVorSoftwareFee = moneyOutTransactionalNonVorSoftwareFee;
	}

	@Column(name = "money_out_transactional_vor_software_fee_historical")
	public BigDecimal getMoneyOutTransactionalVorSoftwareFeeHistorical() {
		return moneyOutTransactionalVorSoftwareFeeHistorical;
	}

	public void setMoneyOutTransactionalVorSoftwareFeeHistorical(BigDecimal moneyOutTransactionalVorSoftwareFeeHistorical) {
		this.moneyOutTransactionalVorSoftwareFeeHistorical = moneyOutTransactionalVorSoftwareFeeHistorical;
	}

	@Column(name = "money_out_transactional_vor_vor_fee_historical")
	public BigDecimal getMoneyOutTransactionalVorVorFeeHistorical() {
		return moneyOutTransactionalVorVorFeeHistorical;
	}

	public void setMoneyOutTransactionalVorVorFeeHistorical(BigDecimal moneyOutTransactionalVorVorFeeHistorical) {
		this.moneyOutTransactionalVorVorFeeHistorical = moneyOutTransactionalVorVorFeeHistorical;
	}

	@Column(name = "money_out_transactional_non_vor_software_fee_historical")
	public BigDecimal getMoneyOutTransactionalNonVorSoftwareFeeHistorical() {
		return moneyOutTransactionalNonVorSoftwareFeeHistorical;
	}

	public void setMoneyOutTransactionalNonVorSoftwareFeeHistorical(BigDecimal moneyOutTransactionalNonVorSoftwareFeeHistorical) {
		this.moneyOutTransactionalNonVorSoftwareFeeHistorical = moneyOutTransactionalNonVorSoftwareFeeHistorical;
	}

	@Column(name = "money_out_subscription_vor_software_fee")
	public BigDecimal getMoneyOutSubscriptionVorSoftwareFee() {
		return moneyOutSubscriptionVorSoftwareFee;
	}

	public void setMoneyOutSubscriptionVorSoftwareFee(BigDecimal moneyOutSubscriptionVorSoftwareFee) {
		this.moneyOutSubscriptionVorSoftwareFee = moneyOutSubscriptionVorSoftwareFee;
	}

	@Column(name = "money_out_subscription_vor_vor_fee")
	public BigDecimal getMoneyOutSubscriptionVorVorFee() {
		return moneyOutSubscriptionVorVorFee;
	}

	public void setMoneyOutSubscriptionVorVorFee(BigDecimal moneyOutSubscriptionVorVorFee) {
		this.moneyOutSubscriptionVorVorFee = moneyOutSubscriptionVorVorFee;
	}

	@Column(name = "money_out_subscription_non_vor_software_fee")
	public BigDecimal getMoneyOutSubscriptionNonVorSoftwareFee() {
		return moneyOutSubscriptionNonVorSoftwareFee;
	}

	public void setMoneyOutSubscriptionNonVorSoftwareFee(BigDecimal moneyOutSubscriptionNonVorSoftwareFee) {
		this.moneyOutSubscriptionNonVorSoftwareFee = moneyOutSubscriptionNonVorSoftwareFee;
	}

	@Column(name = "money_out_subscription_vor_software_fee_historical")
	public BigDecimal getMoneyOutSubscriptionVorSoftwareFeeHistorical() {
		return moneyOutSubscriptionVorSoftwareFeeHistorical;
	}

	public void setMoneyOutSubscriptionVorSoftwareFeeHistorical(BigDecimal moneyOutSubscriptionVorSoftwareFeeHistorical) {
		this.moneyOutSubscriptionVorSoftwareFeeHistorical = moneyOutSubscriptionVorSoftwareFeeHistorical;
	}

	@Column(name = "money_out_subscription_vor_vor_fee_historical")
	public BigDecimal getMoneyOutSubscriptionVorVorFeeHistorical() {
		return moneyOutSubscriptionVorVorFeeHistorical;
	}

	public void setMoneyOutSubscriptionVorVorFeeHistorical(BigDecimal moneyOutSubscriptionVorVorFeeHistorical) {
		this.moneyOutSubscriptionVorVorFeeHistorical = moneyOutSubscriptionVorVorFeeHistorical;
	}

	@Column(name = "money_out_subscription_non_vor_software_fee_historical")
	public BigDecimal getMoneyOutSubscriptionNonVorSoftwareFeeHistorical() {
		return moneyOutSubscriptionNonVorSoftwareFeeHistorical;
	}

	public void setMoneyOutSubscriptionNonVorSoftwareFeeHistorical(BigDecimal moneyOutSubscriptionNonVorSoftwareFeeHistorical) {
		this.moneyOutSubscriptionNonVorSoftwareFeeHistorical = moneyOutSubscriptionNonVorSoftwareFeeHistorical;
	}

	@Column(name = "professional_service_fee_receivables")
	public BigDecimal getProfessionalServiceFeeReceivables() {
		return professionalServiceFeeReceivables;
	}

	public void setProfessionalServiceFeeReceivables(BigDecimal professionalServiceFeeReceivables) {
		this.professionalServiceFeeReceivables = professionalServiceFeeReceivables;
	}

	@Column(name = "professional_service_fee_receivables_historical")
	public BigDecimal getProfessionalServiceFeeReceivablesHistorical() {
		return professionalServiceFeeReceivablesHistorical;
	}

	public void setProfessionalServiceFeeReceivablesHistorical(BigDecimal professionalServiceFeeReceivablesHistorical) {
		this.professionalServiceFeeReceivablesHistorical = professionalServiceFeeReceivablesHistorical;
	}

	@Column(name = "fast_funds_fee_receivables")
	public BigDecimal getFastFundsFeeReceivables() {
		return fastFundsFeeReceivables;
	}

	public void setFastFundsFeeReceivables(BigDecimal fastFundsFeeReceivables) {
		this.fastFundsFeeReceivables = fastFundsFeeReceivables;
	}

	@Column(name = "fast_funds_fee_receivables_historical")
	public BigDecimal getFastFundsFeeReceivablesHistorical() {
		return fastFundsFeeReceivablesHistorical;
	}

	public void setFastFundsFeeReceivablesHistorical(BigDecimal fastFundsFeeReceivablesHistorical) {
		this.fastFundsFeeReceivablesHistorical = fastFundsFeeReceivablesHistorical;
	}

	@Column(name = "subscription_credit_memo_receivables")
	public BigDecimal getSubscriptionCreditMemoReceivables() {
		return subscriptionCreditMemoReceivables;
	}

	public void setSubscriptionCreditMemoReceivables(BigDecimal subscriptionCreditMemoReceivables) {
		this.subscriptionCreditMemoReceivables = subscriptionCreditMemoReceivables;
	}

	@Column(name = "subscription_credit_memo_receivables_historical")
	public BigDecimal getSubscriptionCreditMemoReceivablesHistorical() {
		return subscriptionCreditMemoReceivablesHistorical;
	}

	public void setSubscriptionCreditMemoReceivablesHistorical(BigDecimal subscriptionCreditMemoReceivablesHistorical) {
		this.subscriptionCreditMemoReceivablesHistorical = subscriptionCreditMemoReceivablesHistorical;
	}

	@Column(name = "prof_services_credit_memo_receivables")
	public BigDecimal getProfServicesCreditMemoReceivables() {
		return profServicesCreditMemoReceivables;
	}

	public void setProfServicesCreditMemoReceivables(BigDecimal profServicesCreditMemoReceivables) {
		this.profServicesCreditMemoReceivables = profServicesCreditMemoReceivables;
	}

	@Column(name = "prof_services_credit_memo_receivables_historical")
	public BigDecimal getProfServicesCreditMemoReceivablesHistorical() {
		return profServicesCreditMemoReceivablesHistorical;
	}

	public void setProfServicesCreditMemoReceivablesHistorical(BigDecimal profServicesCreditMemoReceivablesHistorical) {
		this.profServicesCreditMemoReceivablesHistorical = profServicesCreditMemoReceivablesHistorical;
	}

	@Column(name = "revenue_professional_service_fee")
	public BigDecimal getRevenueProfessionalServiceFee() {
		return revenueProfessionalServiceFee;
	}

	public void setRevenueProfessionalServiceFee(BigDecimal revenueProfessionalServiceFee) {
		this.revenueProfessionalServiceFee = revenueProfessionalServiceFee;
	}

	@Column(name = "revenue_professional_service_fee_historical")
	public BigDecimal getRevenueProfessionalServiceFeeHistorical() {
		return revenueProfessionalServiceFeeHistorical;
	}

	public void setRevenueProfessionalServiceFeeHistorical(BigDecimal revenueProfessionalServiceFeeHistorical) {
		this.revenueProfessionalServiceFeeHistorical = revenueProfessionalServiceFeeHistorical;
	}

	@Column(name = "credit_professional_service_fee")
	public BigDecimal getCreditProfessionalServiceFee() {
		return creditProfessionalServiceFee;
	}

	public void setCreditProfessionalServiceFee(BigDecimal creditProfessionalServiceFee) {
		this.creditProfessionalServiceFee = creditProfessionalServiceFee;
	}

	@Column(name = "credit_professional_service_fee_historical")
	public BigDecimal getCreditProfessionalServiceFeeHistorical() {
		return creditProfessionalServiceFeeHistorical;
	}

	public void setCreditProfessionalServiceFeeHistorical(BigDecimal creditProfessionalServiceFeeHistorical) {
		this.creditProfessionalServiceFeeHistorical = creditProfessionalServiceFeeHistorical;
	}

	@Column(name = "def_revenue_professional_service_fee")
	public BigDecimal getDefRevenueProfessionalServiceFee() {
		return defRevenueProfessionalServiceFee;
	}

	public void setDefRevenueProfessionalServiceFee(BigDecimal defRevenueProfessionalServiceFee) {
		this.defRevenueProfessionalServiceFee = defRevenueProfessionalServiceFee;
	}

	@Column(name = "def_revenue_professional_service_fee_historical")
	public BigDecimal getDefRevenueProfessionalServiceFeeHistorical() {
		return defRevenueProfessionalServiceFeeHistorical;
	}

	public void setDefRevenueProfessionalServiceFeeHistorical(BigDecimal defRevenueProfessionalServiceFeeHistorical) {
		this.defRevenueProfessionalServiceFeeHistorical = defRevenueProfessionalServiceFeeHistorical;
	}

	@Column(name = "money_out_professional_service_fee")
	public BigDecimal getMoneyOutProfessionalServiceFee() {
		return moneyOutProfessionalServiceFee;
	}

	public void setMoneyOutProfessionalServiceFee(BigDecimal moneyOutProfessionalServiceFee) {
		this.moneyOutProfessionalServiceFee = moneyOutProfessionalServiceFee;
	}

	@Column(name = "money_out_professional_service_fee_historical")
	public BigDecimal getMoneyOutProfessionalServiceFeeHistorical() {
		return moneyOutProfessionalServiceFeeHistorical;
	}

	public void setMoneyOutProfessionalServiceFeeHistorical(BigDecimal moneyOutProfessionalServiceFeeHistorical) {
		this.moneyOutProfessionalServiceFeeHistorical = moneyOutProfessionalServiceFeeHistorical;
	}
}