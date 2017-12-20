package com.workmarket.domains.model.cache;

import java.math.BigDecimal;

public class MoneyAggregateSummary {
	private BigDecimal total;
	private BigDecimal inProgress;
	private BigDecimal available;
	private BigDecimal earnedPending;
	private BigDecimal earnedInProgress;
	private BigDecimal earnedAvailable;
	
	private BigDecimal due7Days;
	
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	public BigDecimal getInProgress() {
		return inProgress;
	}
	public void setInProgress(BigDecimal inProgress) {
		this.inProgress = inProgress;
	}
	
	public BigDecimal getAvailable() {
		return available;
	}
	public void setAvailable(BigDecimal available) {
		this.available = available;
	}
	
	public BigDecimal getEarnedPending() {
		return earnedPending;
	}
	public void setEarnedPending(BigDecimal earnedPending) {
		this.earnedPending = earnedPending;
	}
	
	public BigDecimal getEarnedAvailable() {
		return earnedAvailable;
	}
	public void setEarnedAvailable(BigDecimal earnedAvailable) {
		this.earnedAvailable = earnedAvailable;
	}
	public BigDecimal getEarnedInProgress() {
		return earnedInProgress;
	}
	public void setEarnedInProgress(BigDecimal earnedInProgress) {
		this.earnedInProgress = earnedInProgress;
	}
	
	public BigDecimal getDue7Days() {
		return due7Days;
	}
	public void setDue7Days(BigDecimal due7Days) {
		this.due7Days = due7Days;
	}
}