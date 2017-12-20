package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Entity(name = "subscriptionPaymentTier")
@Table(name = "subscription_payment_tier")
@AuditChanges
public class SubscriptionPaymentTier extends AbstractPaymentTier implements Comparable<SubscriptionPaymentTier> {

	private static final long serialVersionUID = 1L;

	public static enum PaymentTierCategory {
		SOFTWARE,
		VENDOR_OF_RECORD;
	}

	private SubscriptionFeeConfiguration subscriptionFeeConfiguration;
	private SubscriptionPaymentTierStatusType subscriptionPaymentTierSoftwareStatusType = new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.NOT_REACHED);
	private SubscriptionPaymentTierStatusType subscriptionPaymentTierVorStatusType = new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.NOT_REACHED);
	private BigDecimal softwareThroughputReached = BigDecimal.ZERO;
	private BigDecimal vorThroughputReached = BigDecimal.ZERO;

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
	@JoinColumn(name = "subscription_fee_configuration_id", referencedColumnName = "id", updatable = false)
	public SubscriptionFeeConfiguration getSubscriptionFeeConfiguration() {
		return subscriptionFeeConfiguration;
	}

	public void setSubscriptionFeeConfiguration(SubscriptionFeeConfiguration subscriptionFeeConfiguration) {
		this.subscriptionFeeConfiguration = subscriptionFeeConfiguration;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "subscription_payment_tier_sw_status_type_code", referencedColumnName = "code")
	public SubscriptionPaymentTierStatusType getSubscriptionPaymentTierSoftwareStatusType() {
		return subscriptionPaymentTierSoftwareStatusType;
	}

	public void setSubscriptionPaymentTierSoftwareStatusType(SubscriptionPaymentTierStatusType subscriptionPaymentTierSoftwareStatusType) {
		this.subscriptionPaymentTierSoftwareStatusType = subscriptionPaymentTierSoftwareStatusType;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "subscription_payment_tier_vor_status_type_code", referencedColumnName = "code")
	public SubscriptionPaymentTierStatusType getSubscriptionPaymentTierVorStatusType() {
		return subscriptionPaymentTierVorStatusType;
	}

	public void setSubscriptionPaymentTierVorStatusType(SubscriptionPaymentTierStatusType subscriptionPaymentTierVorStatusType) {
		this.subscriptionPaymentTierVorStatusType = subscriptionPaymentTierVorStatusType;
	}

	@Column(name = "software_throughput_reached")
	public BigDecimal getSoftwareThroughputReached() {
		return softwareThroughputReached;
	}

	public void setSoftwareThroughputReached(BigDecimal softwareThroughputReached) {
		this.softwareThroughputReached = softwareThroughputReached;
	}

	@Column(name = "vor_throughput_reached")
	public BigDecimal getVorThroughputReached() {
		return vorThroughputReached;
	}

	public void setVorThroughputReached(BigDecimal vorThroughputReached) {
		this.vorThroughputReached = vorThroughputReached;
	}

	@Override
	public int compareTo(SubscriptionPaymentTier subscriptionPaymentTier) {
		if (this.getMinimum() == null) {
			return (subscriptionPaymentTier != null && subscriptionPaymentTier.getMinimum() != null) ? -1 : 0;
		}
		if (subscriptionPaymentTier == null || subscriptionPaymentTier.getMinimum() == null) {
			return 1;
		}
		return this.getMinimum().compareTo(subscriptionPaymentTier.getMinimum());
	}

	@Transient
	public boolean hasReachedSoftwareThroughputNewThreshold(BigDecimal amount) {
		if (softwareThroughputReached == null) return false;
		BigDecimal reachedPercent = calculatePercentReached(amount);
		if (reachedPercent.compareTo(BigDecimal.ZERO) == 0) {
			return false;
		}
		for (Integer threshold : Constants.SUBSCRIPTION_THROUGHPUT_THRESHOLD_PERCENTAGES) {
			//Find the threshold that's next
			if (BigDecimal.valueOf(threshold).compareTo(softwareThroughputReached) > 0) {
				//Check if the new reachedPercent is greater or equal than the next threshold to be reached
				return (reachedPercent.compareTo(BigDecimal.valueOf(threshold)) >= 0);
			}
		}
		return false;
	}

	@Transient
	public boolean hasReachedVorThroughputNewThreshold(BigDecimal amount) {
		if (vorThroughputReached == null) return false;
		BigDecimal reachedPercent = calculatePercentReached(amount);
		if (reachedPercent.compareTo(BigDecimal.ZERO) == 0) return false;
		for (Integer threshold : Constants.SUBSCRIPTION_THROUGHPUT_THRESHOLD_PERCENTAGES) {
			//Find the threshold that's next
			if (BigDecimal.valueOf(threshold).compareTo(vorThroughputReached) > 0) {
				//Check if the new reachedPercent is greater or equal than the next threshold to be reached
				return (reachedPercent.compareTo(BigDecimal.valueOf(threshold)) >= 0);
			}
		}
		return false;
	}

	@Transient
	public void resetToActive() {
		this.setSubscriptionPaymentTierSoftwareStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.ACTIVE));
		this.setSubscriptionPaymentTierVorStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.ACTIVE));
	}

	@Transient
	public void resetToNotReached() {
		this.setSubscriptionPaymentTierSoftwareStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.NOT_REACHED));
		this.setSubscriptionPaymentTierVorStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.NOT_REACHED));
	}
}
