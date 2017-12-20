package com.workmarket.domains.model.cache;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Summary values for presentation on the Payment Center page.
 * Used for both payables and receivables.
 */
public class PaymentCenterAggregateSummary implements Serializable {
	private BigDecimal pastDue = BigDecimal.ZERO;
	private BigDecimal upcomingDue = BigDecimal.ZERO;
	private BigDecimal paidYtd = BigDecimal.ZERO;
	private BigDecimal totalFastFundableAmount = BigDecimal.ZERO;

	public BigDecimal getPastDue() {
		return pastDue;
	}
	public void setPastDue(BigDecimal pastDue) {
		this.pastDue = pastDue;
	}

	public BigDecimal getUpcomingDue() {
		return upcomingDue;
	}
	public void setUpcomingDue(BigDecimal upcomingDue) {
		this.upcomingDue = upcomingDue;
	}

	public BigDecimal getPaidYtd() {
		return paidYtd;
	}
	public void setPaidYtd(BigDecimal paidYtd) {
		this.paidYtd = paidYtd;
	}

	public BigDecimal getTotalFastFundableAmount() {
		return totalFastFundableAmount;
	}
	public void setTotalFastFundableAmount(BigDecimal totalFastFundableAmount) {
		this.totalFastFundableAmount = totalFastFundableAmount;
	}
}
