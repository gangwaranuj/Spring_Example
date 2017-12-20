package com.workmarket.domains.model.user;

import java.math.BigDecimal;

public class CompanyUserStats {
	private BigDecimal sentCount = BigDecimal.ZERO;
	private BigDecimal sentValue = BigDecimal.ZERO;
	private BigDecimal approvedCount = BigDecimal.ZERO;
	private BigDecimal approvedValue = BigDecimal.ZERO;

	public BigDecimal getSentCount() {
		return sentCount;
	}
	public CompanyUserStats setSentCount(BigDecimal sentCount) {
		this.sentCount = sentCount;
		return this;
	}

	public BigDecimal getSentValue() {
		return sentValue;
	}
	public CompanyUserStats setSentValue(BigDecimal sentValue) {
		this.sentValue = sentValue;
		return this;
	}

	public BigDecimal getApprovedCount() {
		return approvedCount;
	}
	public CompanyUserStats setApprovedCount(BigDecimal approvedCount) {
		this.approvedCount = approvedCount;
		return this;
	}

	public BigDecimal getApprovedValue() {
		return approvedValue;
	}
	public CompanyUserStats setApprovedValue(BigDecimal approvedValue) {
		this.approvedValue = approvedValue;
		return this;
	}
}
