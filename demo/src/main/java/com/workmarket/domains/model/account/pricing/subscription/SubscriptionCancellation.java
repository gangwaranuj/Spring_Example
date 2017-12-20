package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.domains.model.ApprovableVerifiableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Author: rocio
 */
@Entity(name = "subscriptionCancellation")
@Table(name = "subscription_cancellation")
@AuditChanges
public class SubscriptionCancellation extends ApprovableVerifiableEntity {

	private static final long serialVersionUID = 1L;

	private SubscriptionConfiguration subscriptionConfiguration;
	private Calendar effectiveDate;
	private Calendar approvedOn;
	private User approvedBy;
	private BigDecimal cancellationFee = BigDecimal.ZERO;
	private SubscriptionInvoice cancellationInvoice;

	public SubscriptionCancellation() {
	}

	public SubscriptionCancellation(SubscriptionConfiguration subscriptionConfiguration) {
		this.subscriptionConfiguration = subscriptionConfiguration;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_configuration_id", referencedColumnName = "id", updatable = false)
	public SubscriptionConfiguration getSubscriptionConfiguration() {
		return subscriptionConfiguration;
	}

	public void setSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration) {
		this.subscriptionConfiguration = subscriptionConfiguration;
	}

	@Column(name = "effective_date", nullable = false)
	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "approved_by")
	public User getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	@Column(name = "approved_on")
	public Calendar getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Calendar approvedOn) {
		this.approvedOn = approvedOn;
	}

	@Column(name = "cancellation_fee", nullable = false)
	public BigDecimal getCancellationFee() {
		return cancellationFee;
	}

	public void setCancellationFee(BigDecimal cancellationFee) {
		this.cancellationFee = cancellationFee;
	}

	@ManyToOne
	@JoinColumn(name = "cancellation_invoice_id", referencedColumnName = "id")
	public SubscriptionInvoice getCancellationInvoice() {
		return cancellationInvoice;
	}

	public void setCancellationInvoice(SubscriptionInvoice cancellationInvoice) {
		this.cancellationInvoice = cancellationInvoice;
	}

	@Transient
	public boolean hasCancellationFee() {
		if (cancellationFee == null) return false;
		return cancellationFee.compareTo(BigDecimal.ZERO) > 0;
	}
}
