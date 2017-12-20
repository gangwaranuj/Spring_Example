package com.workmarket.domains.model.account;

import java.math.BigDecimal;
import java.util.Calendar;

public class AccountingCreditMemoSummaryDetail {

	private BigDecimal amount;
	private String registerTransactionTypeCode;
	private String invoiceType;
	private String invoiceNumber;
	private String effectiveName;
	private Calendar invoicedOn;
	private String subscriptionInvoiceTypeCode;
	private String reason;
	private String note;
	private String creditMemoType;
	private BigDecimal originalTransactionAmount;
	private Calendar originalInvoiceRevenueEffectiveDate;
	private String originalInvoiceRegisterTransactionTypeCode;
	private String originalInvoiceType;
	private String originalInvoiceNumber;
	private String originalInvoiceEffectiveName;
	private Calendar originalInvoiceDueDate;
	private Calendar originalInvoicePaymentDate;
	private Calendar originalInvoicedOn;
	private String originalInvoiceSubscriptionInvoiceTypeCode;

	public BigDecimal getAmount() {
		return amount;
	}

	public AccountingCreditMemoSummaryDetail setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public String getRegisterTransactionTypeCode() {
		return registerTransactionTypeCode;
	}

	public AccountingCreditMemoSummaryDetail setRegisterTransactionTypeCode(String registerTransactionTypeCode) {
		this.registerTransactionTypeCode = registerTransactionTypeCode;
		return this;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public AccountingCreditMemoSummaryDetail setInvoiceType(String type) {
		this.invoiceType = type;
		return this;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public AccountingCreditMemoSummaryDetail setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
		return this;
	}

	public String getEffectiveName() {
		return effectiveName;
	}

	public AccountingCreditMemoSummaryDetail setEffectiveName(String effectiveName) {
		this.effectiveName = effectiveName;
		return this;
	}

	public Calendar getInvoicedOn() {
		return invoicedOn;
	}

	public AccountingCreditMemoSummaryDetail setInvoicedOn(Calendar invoicedOn) {
		this.invoicedOn = invoicedOn;
		return this;
	}

	public String getSubscriptionInvoiceTypeCode() {
		return subscriptionInvoiceTypeCode;
	}

	public AccountingCreditMemoSummaryDetail setSubscriptionInvoiceTypeCode(String subscriptionInvoiceTypeCode) {
		this.subscriptionInvoiceTypeCode = subscriptionInvoiceTypeCode;
		return this;
	}

	public String getReason() {
		return reason;
	}

	public AccountingCreditMemoSummaryDetail setReason(String reason) {
		this.reason = reason;
		return this;
	}

	public String getNote() {
		return note;
	}

	public AccountingCreditMemoSummaryDetail setNote(String note) {
		this.note = note;
		return this;
	}

	public String getCreditMemoType() {
		return creditMemoType;
	}

	public AccountingCreditMemoSummaryDetail setCreditMemoType(String creditMemoType) {
		this.creditMemoType = creditMemoType;
		return this;
	}

	public BigDecimal getOriginalTransactionAmount() {
		return originalTransactionAmount;
	}

	public AccountingCreditMemoSummaryDetail setOriginalTransactionAmount(BigDecimal originalTransactionAmount) {
		this.originalTransactionAmount = originalTransactionAmount;
		return this;
	}

	public Calendar getOriginalInvoiceRevenueEffectiveDate() {
		return originalInvoiceRevenueEffectiveDate;
	}

	public AccountingCreditMemoSummaryDetail setOriginalInvoiceRevenueEffectiveDate(Calendar originalInvoiceRevenueEffectiveDate) {
		this.originalInvoiceRevenueEffectiveDate = originalInvoiceRevenueEffectiveDate;
		return this;
	}

	public String getOriginalInvoiceRegisterTransactionTypeCode() {
		return originalInvoiceRegisterTransactionTypeCode;
	}

	public AccountingCreditMemoSummaryDetail setOriginalInvoiceRegisterTransactionTypeCode(String originalInvoiceRegisterTransactionTypeCode) {
		this.originalInvoiceRegisterTransactionTypeCode = originalInvoiceRegisterTransactionTypeCode;
		return this;
	}

	public String getOriginalInvoiceType() {
		return originalInvoiceType;
	}

	public AccountingCreditMemoSummaryDetail setOriginalInvoiceType(String invoiceType) {
		this.originalInvoiceType = invoiceType;
		return this;
	}

	public String getOriginalInvoiceNumber() {
		return originalInvoiceNumber;
	}

	public AccountingCreditMemoSummaryDetail setOriginalInvoiceNumber(String originalInvoiceNumber) {
		this.originalInvoiceNumber = originalInvoiceNumber;
		return this;
	}

	public String getOriginalInvoiceEffectiveName() {
		return originalInvoiceEffectiveName;
	}

	public AccountingCreditMemoSummaryDetail setOriginalInvoiceEffectiveName(String originalInvoiceEffectiveName) {
		this.originalInvoiceEffectiveName = originalInvoiceEffectiveName;
		return this;
	}

	public Calendar getOriginalInvoiceDueDate() {
		return originalInvoiceDueDate;
	}

	public AccountingCreditMemoSummaryDetail setOriginalInvoiceDueDate(Calendar originalInvoiceDueDate) {
		this.originalInvoiceDueDate = originalInvoiceDueDate;
		return this;
	}

	public Calendar getOriginalInvoicePaymentDate() {
		return originalInvoicePaymentDate;
	}

	public AccountingCreditMemoSummaryDetail setOriginalInvoicePaymentDate(Calendar originalInvoicePaymentDate) {
		this.originalInvoicePaymentDate = originalInvoicePaymentDate;
		return this;
	}

	public String getOriginalInvoiceSubscriptionInvoiceTypeCode() {
		return originalInvoiceSubscriptionInvoiceTypeCode;
	}

	public AccountingCreditMemoSummaryDetail setOriginalInvoiceSubscriptionInvoiceTypeCode(String originalInvoiceSubscriptionInvoiceTypeCode) {
		this.originalInvoiceSubscriptionInvoiceTypeCode = originalInvoiceSubscriptionInvoiceTypeCode;
		return this;
	}

	public Calendar getOriginalInvoicedOn() {
		return originalInvoicedOn;
	}

	public AccountingCreditMemoSummaryDetail setOriginalInvoicedOn(Calendar originalInvoicedOn) {
		this.originalInvoicedOn = originalInvoicedOn;
		return this;
	}
}
