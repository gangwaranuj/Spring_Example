package com.workmarket.service.business.dto.account.pricing.subscription;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.utility.BeanUtilities;

import javax.annotation.Nullable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/** Author: rocio */
public class SubscriptionPaymentTierDTO {

	private BigDecimal minimum              = BigDecimal.ZERO;
	private BigDecimal maximum              = SubscriptionPaymentTier.MAXIMUM;
	private BigDecimal paymentAmount        = BigDecimal.ZERO;
	private BigDecimal vendorOfRecordAmount = BigDecimal.ZERO;

	public SubscriptionPaymentTierDTO() {}

	public SubscriptionPaymentTierDTO(SubscriptionPaymentTier paymentTier) {
		BeanUtilities.copyProperties(this, paymentTier);
	}

	public BigDecimal getMaximum() {
		return maximum;
	}

	public void setMaximum(BigDecimal maximum) {
		this.maximum = maximum;
	}

	public BigDecimal getMinimum() {
		return minimum;
	}

	public void setMinimum(BigDecimal minimum) {
		this.minimum = minimum;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public BigDecimal getVendorOfRecordAmount() {
		return vendorOfRecordAmount;
	}

	public void setVendorOfRecordAmount(BigDecimal vendorOfRecordAmount) {
		this.vendorOfRecordAmount = vendorOfRecordAmount;
	}

	public static List<SubscriptionPaymentTierDTO> transform(Collection<SubscriptionPaymentTier> paymentTiers) {
		return Lists.transform(Lists.newArrayList(paymentTiers), new Function<SubscriptionPaymentTier, SubscriptionPaymentTierDTO>() {
			@Override
			public SubscriptionPaymentTierDTO apply(@Nullable SubscriptionPaymentTier paymentTier) {
				return (paymentTier != null) ? new SubscriptionPaymentTierDTO(paymentTier) : null;
			}
		});
	}
}
