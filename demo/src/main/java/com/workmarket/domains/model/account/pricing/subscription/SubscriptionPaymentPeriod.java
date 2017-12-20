package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.domains.model.account.pricing.PaymentPeriod;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Author: rocio
 */
@Entity(name = "subscriptionPaymentPeriod")
@AuditChanges
@DiscriminatorValue("SPP")
public class SubscriptionPaymentPeriod extends PaymentPeriod {

	private static final long serialVersionUID = 1L;

	private SubscriptionInvoice subscriptionInvoice;
	private SubscriptionConfiguration subscriptionConfiguration;

	private Long subscriptionFeeConfigurationId;
	private Long subscriptionPaymentTierSWId;
	private Long subscriptionPaymentTierVORId;
	private SubscriptionPeriodType subscriptionPeriodType = new SubscriptionPeriodType(SubscriptionPeriodType.AUTO);

	public SubscriptionPaymentPeriod() {
		super();
	}

	public SubscriptionPaymentPeriod(SubscriptionConfiguration subscriptionConfiguration) {
		super();
		this.subscriptionConfiguration = subscriptionConfiguration;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_invoice_id", referencedColumnName = "id")
	public SubscriptionInvoice getSubscriptionInvoice() {
		return subscriptionInvoice;
	}

	public void setSubscriptionInvoice(SubscriptionInvoice subscriptionInvoice) {
		this.subscriptionInvoice = subscriptionInvoice;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "subscription_configuration_id", referencedColumnName = "id", updatable = false)
	public SubscriptionConfiguration getSubscriptionConfiguration() {
		return subscriptionConfiguration;
	}

	public void setSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration) {
		this.subscriptionConfiguration = subscriptionConfiguration;
	}

	@Column(name = "subscription_fee_configuration_id")
	public Long getSubscriptionFeeConfigurationId() {
		return subscriptionFeeConfigurationId;
	}

	public void setSubscriptionFeeConfigurationId(Long subscriptionFeeConfigurationId) {
		this.subscriptionFeeConfigurationId = subscriptionFeeConfigurationId;
	}

	@Column(name = "subscription_payment_tier_sw_id")
	public Long getSubscriptionPaymentTierSWId() {
		return subscriptionPaymentTierSWId;
	}

	public void setSubscriptionPaymentTierSWId(Long subscriptionPaymentTierSWId) {
		this.subscriptionPaymentTierSWId = subscriptionPaymentTierSWId;
	}

	@Column(name = "subscription_payment_tier_vor_id")
	public Long getSubscriptionPaymentTierVORId() {
		return subscriptionPaymentTierVORId;
	}

	public void setSubscriptionPaymentTierVORId(Long subscriptionPaymentTierVORId) {
		this.subscriptionPaymentTierVORId = subscriptionPaymentTierVORId;
	}

	@ManyToOne(fetch= FetchType.LAZY, optional = false)
	@JoinColumn(name="subscription_period_type", referencedColumnName="code")
	public SubscriptionPeriodType getSubscriptionPeriodType() {
		return subscriptionPeriodType;
	}

	public void setSubscriptionPeriodType(SubscriptionPeriodType subscriptionPeriodType) {
		this.subscriptionPeriodType = subscriptionPeriodType;
	}

	@Transient
	public boolean hasSubscriptionInvoice() {
		return subscriptionInvoice != null;
	}
}
