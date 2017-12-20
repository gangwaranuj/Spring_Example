package com.workmarket.domains.model.tax;

import java.math.BigDecimal;

/**
 * User: iloveopt
 * Date: 1/15/14
 */
public class EarningDetailReportRow {

	private String buyerCompanyName;
	private BigDecimal earnings = BigDecimal.ZERO;
	private BigDecimal expenses = BigDecimal.ZERO;


	public BigDecimal getExpenses() {
		return expenses;
	}

	public void setExpenses(BigDecimal expenses) {
		this.expenses = expenses;
	}

	public String getBuyerCompanyName() {
		return buyerCompanyName;
	}

	public void setBuyerCompanyName(String buyerCompanyName) {
		this.buyerCompanyName = buyerCompanyName;
	}

	public BigDecimal getEarnings() {
		return earnings;
	}

	public void setEarnings(BigDecimal earnings) {
		this.earnings = earnings;
	}

}
