package com.workmarket.data.report.work;

import java.math.BigDecimal;

public class PaymentTotals {
	private BigDecimal payables = BigDecimal.ZERO;
	private BigDecimal receivables = BigDecimal.ZERO;
	
	public BigDecimal getPayables() {
		return payables;
	}
	public void setPayables(BigDecimal payables) {
		this.payables = payables;
	}
	public BigDecimal getReceivables() {
		return receivables;
	}
	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}
}
