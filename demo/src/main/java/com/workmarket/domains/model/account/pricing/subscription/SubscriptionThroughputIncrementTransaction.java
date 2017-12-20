package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.domains.model.account.TransactionStatus;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Author: rocio
 */
@Entity(name = "subscriptionThroughputIncrementTransaction")
@Table(name = "subscription_throughput_increment_transaction")
@AuditChanges
public class SubscriptionThroughputIncrementTransaction extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private SubscriptionPaymentTier subscriptionPaymentTier;
	private WorkResourceTransaction triggeredByRegisterTransaction;
	private TransactionStatus transactionStatus = new TransactionStatus(TransactionStatus.SUBMITTED);
	private boolean softwareIncrement = true;
	private boolean vorIncrement = false;

	public SubscriptionThroughputIncrementTransaction() {
	}

	public SubscriptionThroughputIncrementTransaction(SubscriptionPaymentTier subscriptionPaymentTier, WorkResourceTransaction triggeredByRegisterTransaction) {
		this.setSubscriptionPaymentTier(subscriptionPaymentTier);
		this.setTriggeredByRegisterTransaction(triggeredByRegisterTransaction);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_payment_tier_id", referencedColumnName = "id", nullable = false)
	public SubscriptionPaymentTier getSubscriptionPaymentTier() {
		return subscriptionPaymentTier;
	}

	public void setSubscriptionPaymentTier(SubscriptionPaymentTier subscriptionPaymentTier) {
		this.subscriptionPaymentTier = subscriptionPaymentTier;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "triggered_by_register_transaction_id", referencedColumnName = "id", nullable = false)
	public WorkResourceTransaction getTriggeredByRegisterTransaction() {
		return triggeredByRegisterTransaction;
	}

	public void setTriggeredByRegisterTransaction(WorkResourceTransaction triggeredByRegisterTransaction) {
		this.triggeredByRegisterTransaction = triggeredByRegisterTransaction;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_status", referencedColumnName = "code")
	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	@Column(name = "software_increment", nullable = false)
	public boolean isSoftwareIncrement() {
		return softwareIncrement;
	}

	public void setSoftwareIncrement(boolean softwareIncrement) {
		this.softwareIncrement = softwareIncrement;
	}

	@Column(name = "vor_increment", nullable = false)
	public boolean isVorIncrement() {
		return vorIncrement;
	}

	public void setVorIncrement(boolean vorIncrement) {
		this.vorIncrement = vorIncrement;
	}

	@Override
	public String toString() {
		return "SubscriptionThroughputIncrementTransaction{" +
				", subscriptionPaymentTier=" + subscriptionPaymentTier.getId() +
				", triggeredByRegisterTransaction=" + triggeredByRegisterTransaction.getId() +
				", subscriptionPaymentTierStatusType=" + transactionStatus.getCode() +
				'}';
	}
}
