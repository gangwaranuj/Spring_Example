package com.workmarket.service.business.dto.account.pricing.subscription;

import java.math.BigDecimal;
import java.util.Calendar;

public class SubscriptionInvoiceTransactionDTO {

	private Calendar transactionDate;
	private BigDecimal subscriptionPeriodAmount = BigDecimal.ZERO;
	private BigDecimal subscriptionVORAmount = BigDecimal.ZERO;
	private BigDecimal subscriptionAddOnsAmount = BigDecimal.ZERO;

	public BigDecimal getSubscriptionAddOnsAmount() {
		return subscriptionAddOnsAmount;
	}

	public void setSubscriptionAddOnsAmount(BigDecimal subscriptionAddOnsAmount) {
		this.subscriptionAddOnsAmount = subscriptionAddOnsAmount;
	}

	public BigDecimal getSubscriptionPeriodAmount() {
		return subscriptionPeriodAmount;
	}

	public void setSubscriptionPeriodAmount(BigDecimal subscriptionPeriodAmount) {
		this.subscriptionPeriodAmount = subscriptionPeriodAmount;
	}

	public BigDecimal getSubscriptionVORAmount() {
		return subscriptionVORAmount;
	}

	public void setSubscriptionVORAmount(BigDecimal subscriptionVORAmount) {
		this.subscriptionVORAmount = subscriptionVORAmount;
	}

	public Calendar getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Calendar transactionDate) {
		this.transactionDate = transactionDate;
	}
}