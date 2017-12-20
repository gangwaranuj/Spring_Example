package com.workmarket.domains.work.model.route;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;

@Embeddable
public class RoutingStrategySummary implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer sent = 0;
	private Integer failed = 0;
	private Integer failedInsufficientFunds = 0;
	private Integer failedInsufficientBudget = 0;
	private Integer failedInsufficientSpendLimit = 0;
	private Integer failedPaymentTermsCreditLimit = 0;
	private Integer failedIllegalState = 0;

	@Column(name = "summary_sent", nullable = false)
	public Integer getSent() {
		return sent;
	}

	public void setSent(Integer sent) {
		this.sent = sent;
	}

	@Column(name = "summary_failed", nullable = false)
	public Integer getFailed() {
		return failed;
	}

	public void setFailed(Integer failed) {
		this.failed = failed;
	}

	@Column(name = "summary_failed_funds", nullable = false)
	public Integer getFailedInsufficientFunds() {
		return failedInsufficientFunds;
	}

	public void setFailedInsufficientFunds(Integer failedInsufficientFunds) {
		this.failedInsufficientFunds = failedInsufficientFunds;
	}

	@Column(name = "summary_failed_budget", nullable = false)
	public Integer getFailedInsufficientBudget() {
		return failedInsufficientBudget;
	}

	public void setFailedInsufficientBudget(Integer failedInsufficientBudget) {
		this.failedInsufficientBudget = failedInsufficientBudget;
	}


	@Column(name = "summary_failed_spend_limit", nullable = false)
	public Integer getFailedInsufficientSpendLimit() {
		return failedInsufficientSpendLimit;
	}

	public void setFailedInsufficientSpendLimit(Integer failedInsufficientSpendLimit) {
		this.failedInsufficientSpendLimit = failedInsufficientSpendLimit;
	}

	@Column(name = "summary_failed_credit", nullable = false)
	public Integer getFailedPaymentTermsCreditLimit() {
		return failedPaymentTermsCreditLimit;
	}

	public void setFailedPaymentTermsCreditLimit(Integer failedPaymentTermsCreditLimit) {
		this.failedPaymentTermsCreditLimit = failedPaymentTermsCreditLimit;
	}

	@Column(name = "summary_failed_validation", nullable = false)
	public Integer getFailedIllegalState() {
		return failedIllegalState;
	}

	public void setFailedIllegalState(Integer failedIllegalState) {
		this.failedIllegalState = failedIllegalState;
	}

	@Transient
	public boolean hasErrors() {
		return getFailed() > 0 ||
				getFailedInsufficientFunds() > 0 ||
				getFailedInsufficientSpendLimit() > 0 ||
				getFailedPaymentTermsCreditLimit() > 0 ||
				getFailedIllegalState() > 0;
	}
}