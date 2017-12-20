package com.workmarket.domains.model.reporting.subscriptions;

import java.math.BigDecimal;

/**
 * Author: rocio
 */
public class SubscriptionAggregate {

	private int totalCompanies;
	private int totalVorCompanies;
	private BigDecimal sumOfTerms;
	private BigDecimal sumOfMonthlyRecurringRevenue;
	private BigDecimal sumOfAnnualRecurringRevenue;

	public BigDecimal getSumOfAnnualRecurringRevenue() {
		return sumOfAnnualRecurringRevenue;
	}

	public void setSumOfAnnualRecurringRevenue(BigDecimal sumOfAnnualRecurringRevenue) {
		this.sumOfAnnualRecurringRevenue = sumOfAnnualRecurringRevenue;
	}

	public BigDecimal getSumOfMonthlyRecurringRevenue() {
		return sumOfMonthlyRecurringRevenue;
	}

	public void setSumOfMonthlyRecurringRevenue(BigDecimal sumOfMonthlyRecurringRevenue) {
		this.sumOfMonthlyRecurringRevenue = sumOfMonthlyRecurringRevenue;
	}

	public BigDecimal getSumOfTerms() {
		return sumOfTerms;
	}

	public void setSumOfTerms(BigDecimal sumOfTerms) {
		this.sumOfTerms = sumOfTerms;
	}

	public int getTotalCompanies() {
		return totalCompanies;
	}

	public void setTotalCompanies(int totalCompanies) {
		this.totalCompanies = totalCompanies;
	}

	public int getTotalVorCompanies() {
		return totalVorCompanies;
	}

	public void setTotalVorCompanies(int totalVorCompanies) {
		this.totalVorCompanies = totalVorCompanies;
	}
}
