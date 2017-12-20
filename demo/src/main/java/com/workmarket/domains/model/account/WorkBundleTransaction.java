package com.workmarket.domains.model.account;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Author: rocio
 */
@Entity(name = "workBundleTransaction")
@Table(name = "work_bundle_transaction")
@AuditChanges
public class WorkBundleTransaction extends WorkResourceTransaction {

	private static final long serialVersionUID = -8493805209264118227L;
	private BigDecimal remainingAuthorizedAmount;

	@Column(name = "remaining_work_bundle_authorized_amount", nullable = false)
	public BigDecimal getRemainingAuthorizedAmount() {
		return remainingAuthorizedAmount;
	}

	public void setRemainingAuthorizedAmount(BigDecimal remainingAuthorizedAmount) {
		this.remainingAuthorizedAmount = remainingAuthorizedAmount;
	}

	@Override public String toString() {
		return "WorkBundleTransaction{" +
				"remainingAuthorizedAmount=" + remainingAuthorizedAmount +
				", workBundle=" + (getWork() != null ? getWork().getId() : "") +
				'}';
	}
}
