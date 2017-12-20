package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;

import java.math.BigDecimal;

public class TaxReportRow {

	private Long companyId;
	private Long activeTaxEntityId;
	private BigDecimal earnings = BigDecimal.ZERO;
	private BigDecimal expenses = BigDecimal.ZERO;
	private BigDecimal workPayments = BigDecimal.ZERO;
	private BigDecimal paymentReversals = BigDecimal.ZERO;
	private BigDecimal marketingPayments = BigDecimal.ZERO;
	private BigDecimal vorEarnings = BigDecimal.ZERO;
	private BigDecimal nonVorEarnings = BigDecimal.ZERO;

	public TaxReportRow() {
	}

	public TaxReportRow(Long companyId) {
		this.companyId = companyId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public TaxReportRow setCompanyId(Long companyId) {
		this.companyId = companyId;
		return this;
	}

	public Long getActiveTaxEntityId() {
		return activeTaxEntityId;
	}

	public TaxReportRow setActiveTaxEntityId(Long activeTaxEntityId) {
		this.activeTaxEntityId = activeTaxEntityId;
		return this;
	}

	public BigDecimal getEarnings() {
		return earnings;
	}

	public TaxReportRow setEarnings(BigDecimal earnings) {
		this.earnings = earnings;
		return this;
	}

	public BigDecimal getExpenses() {
		return expenses;
	}

	public TaxReportRow setExpenses(BigDecimal expenses) {
		this.expenses = expenses;
		return this;
	}

	public BigDecimal getMarketingPayments() {
		return marketingPayments;
	}

	public TaxReportRow setMarketingPayments(BigDecimal marketingPayments) {
		this.marketingPayments = marketingPayments;
		return this;
	}

	public BigDecimal getPaymentReversals() {
		return paymentReversals;
	}

	public TaxReportRow setPaymentReversals(BigDecimal paymentReversals) {
		this.paymentReversals = paymentReversals;
		return this;
	}

	public BigDecimal getWorkPayments() {
		return workPayments;
	}

	public TaxReportRow setWorkPayments(BigDecimal workPayments) {
		this.workPayments = workPayments;
		return this;
	}

	public BigDecimal getNonVorEarnings() {
		return nonVorEarnings;
	}

	public void setNonVorEarnings(BigDecimal nonVorEarnings) {
		this.nonVorEarnings = nonVorEarnings;
	}

	public BigDecimal getVorEarnings() {
		return vorEarnings;
	}

	public void setVorEarnings(BigDecimal vorEarnings) {
		this.vorEarnings = vorEarnings;
	}

	public TaxReportRow setItemizedAmount(BigDecimal amount, String transactionTypeCode, String accountServiceType) {
		if (RegisterTransactionType.RESOURCE_WORK_PAYMENT.equals(transactionTypeCode)) {
			workPayments = workPayments.add(amount);
			if (AccountServiceType.VENDOR_OF_RECORD.equals(accountServiceType)) {
				vorEarnings = vorEarnings.add(amount);
			} else {
				nonVorEarnings = nonVorEarnings.add(amount);
			}
		} else if (RegisterTransactionType.DEBIT_ASSIGNMENT_PAYMENT_REVERSAL.equals(transactionTypeCode)) {
			paymentReversals = amount;
		} else if (RegisterTransactionType.CREDIT_MARKETING_PAYMENT.equals(transactionTypeCode)) {
			marketingPayments = amount;
		}
		return this;
	}

	public BigDecimal getItemizedTotal() {
		return workPayments.add(paymentReversals).add(marketingPayments);
	}

	@Override
	public String toString() {
		return "TaxReportRow{" +
				"companyId=" + companyId +
				", activeTaxEntityId=" + activeTaxEntityId +
				", earnings=" + earnings +
				", expenses=" + expenses +
				'}';
	}
}