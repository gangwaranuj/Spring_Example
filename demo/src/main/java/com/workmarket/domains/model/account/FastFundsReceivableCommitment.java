package com.workmarket.domains.model.account;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Calendar;

@Entity(name = "fastFundsReceivableCommitment")
@Table(name = "fast_funds_receivable_commitment")
@NamedQueries({
	@NamedQuery(name = "fastFundsReceivableCommitment.findByWorkId", query = "FROM fastFundsReceivableCommitment ffrc " +
		" where ffrc.workId = :workId ")
})
@AuditChanges
public class FastFundsReceivableCommitment extends AuditedEntity {
	private static final long serialVersionUID = 1L;

	private Calendar transactionDate;
	private Calendar effectiveDate;
	private BigDecimal amount;
	private boolean pending;
	private Long workId;

	@Column(name = "transaction_date", nullable = false)
	public Calendar getTransactionDate() {
		return transactionDate;
	}

	public FastFundsReceivableCommitment setTransactionDate(Calendar transactionDate) {
		this.transactionDate = transactionDate;
		return this;
	}

	@Column(name = "effective_date", nullable = false)
	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public FastFundsReceivableCommitment setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
		return this;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return amount;
	}

	public FastFundsReceivableCommitment setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	@Column(name = "is_pending", nullable = false)
	public boolean isPending() {
		return pending;
	}

	public FastFundsReceivableCommitment 	setPending(boolean pending) {
		this.pending = pending;
		return this;
	}

	@Column(name = "work_id")
	public Long getWorkId() {
		return workId;
	}

	public FastFundsReceivableCommitment setWorkId(Long workId) {
		this.workId = workId;
		return this;
	}
}
