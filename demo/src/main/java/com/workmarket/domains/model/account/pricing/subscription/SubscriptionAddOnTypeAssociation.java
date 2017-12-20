package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.domains.model.ActiveEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Author: rocio
 */
@Entity(name = "subscriptionAddOnTypeAssociation")
@Table(name = "subscription_add_on_type_association")
@AuditChanges
public class SubscriptionAddOnTypeAssociation extends ActiveEntity {

	private static final long serialVersionUID = 1L;

	private SubscriptionConfiguration subscriptionConfiguration;
	private SubscriptionAddOnType subscriptionAddOnType;
	private BigDecimal costPerPeriod = BigDecimal.ZERO;
	private Calendar effectiveDate;
	private Calendar removedOn;

	public SubscriptionAddOnTypeAssociation() {
	}

	public SubscriptionAddOnTypeAssociation(SubscriptionAddOnType subscriptionAddOnType, SubscriptionConfiguration subscriptionConfiguration) {
		this.subscriptionAddOnType = subscriptionAddOnType;
		this.subscriptionConfiguration = subscriptionConfiguration;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
	@JoinColumn(name = "subscription_add_on_type_code", updatable = false)
	public SubscriptionAddOnType getSubscriptionAddOnType() {
		return subscriptionAddOnType;
	}

	public void setSubscriptionAddOnType(SubscriptionAddOnType subscriptionAddOnType) {
		this.subscriptionAddOnType = subscriptionAddOnType;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "subscription_configuration_id", updatable = false)
	public SubscriptionConfiguration getSubscriptionConfiguration() {
		return subscriptionConfiguration;
	}

	public void setSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration) {
		this.subscriptionConfiguration = subscriptionConfiguration;
	}

	@Column(name = "cost_per_period", nullable = false)
	public BigDecimal getCostPerPeriod() {
		return costPerPeriod;
	}

	public void setCostPerPeriod(BigDecimal costPerPeriod) {
		this.costPerPeriod = costPerPeriod;
	}

	@Column(name = "effective_date", nullable = false)
	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Column(name = "removed_on")
	public Calendar getRemovedOn() {
		return removedOn;
	}

	public void setRemovedOn(Calendar removedOn) {
		this.removedOn = removedOn;
	}
}

