package com.workmarket.domains.model.account;

import java.math.BigDecimal;
import java.util.Calendar;

public class WeekReportDetail {

	private BigDecimal totalAmount = BigDecimal.ZERO;
	private BigDecimal average = BigDecimal.ZERO;
	private boolean initialWeek = false;
	private boolean trendingUp = false;
	private Calendar weekStartDate;

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getAverage() {
		return average;
	}

	public void setAverage(BigDecimal average) {
		this.average = average;
	}

	public void setInitialWeek(boolean initialWeek) {
		this.initialWeek = initialWeek;
	}

	public boolean isInitialWeek() {
		return initialWeek;
	}

	public void setTrendingUp(boolean trendingUp) {
		this.trendingUp = trendingUp;
	}

	public boolean isTrendingUp() {
		return trendingUp;
	}

	public Calendar getWeekStartDate() {
		return weekStartDate;
	}

	public void setWeekStartDate(Calendar weekStartDate) {
		this.weekStartDate = weekStartDate;
	}

	@Override
	public String toString() {
		return "WeekReportDetail{" +
				"totalAmount=" + totalAmount +
				", average=" + average +
				", initialWeek=" + initialWeek +
				", trendingUp=" + trendingUp +
				", weekStartDate=" + weekStartDate +
				'}';
	}
}
