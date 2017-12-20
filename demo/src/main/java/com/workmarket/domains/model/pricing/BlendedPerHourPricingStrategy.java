package com.workmarket.domains.model.pricing;

import java.math.BigDecimal;

import javax.persistence.Transient;

public class BlendedPerHourPricingStrategy extends PricingStrategy {

	public BlendedPerHourPricingStrategy() {
		super(PricingStrategyType.BLENDED_PER_HOUR.getDescription(), (long) (PricingStrategyType.BLENDED_PER_HOUR.ordinal() + 1));
	}

	public BlendedPerHourPricingStrategy(FullPricingStrategy pricingStrategy) {
		super(pricingStrategy, PricingStrategyType.BLENDED_PER_HOUR.getDescription(), (long) (PricingStrategyType.BLENDED_PER_HOUR.ordinal() + 1));
	}

	public BigDecimal getInitialPerHourPrice() {
		return pricingStrategy.getInitialPerHourPrice();
	}

	public void setInitialPerHourPrice(BigDecimal initialPerHourPrice) {
		pricingStrategy.setInitialPerHourPrice(initialPerHourPrice);
	}

	public void setInitialPerHourPrice(Double initialPerHourPrice) {
		pricingStrategy.setInitialPerHourPrice(BigDecimal.valueOf(initialPerHourPrice));
	}

	public BigDecimal getInitialNumberOfHours() {
		return pricingStrategy.getInitialNumberOfHours();
	}

	public void setInitialNumberOfHours(BigDecimal initialNumberOfHours) {
		pricingStrategy.setInitialNumberOfHours(initialNumberOfHours);
	}

	public void setInitialNumberOfHours(Double initialNumberOfHours) {
		pricingStrategy.setInitialNumberOfHours(BigDecimal.valueOf(initialNumberOfHours));
	}

	public BigDecimal getAdditionalPerHourPrice() {
		return pricingStrategy.getAdditionalPerHourPrice();
	}

	public void setAdditionalPerHourPrice(BigDecimal additionalPerHourPrice) {
		pricingStrategy.setAdditionalPerHourPrice(additionalPerHourPrice);
	}

	public void setAdditionalPerHourPrice(Double additionalPerHourPrice) {
		pricingStrategy.setAdditionalPerHourPrice(BigDecimal.valueOf(additionalPerHourPrice));
	}

	public BigDecimal getMaxBlendedNumberOfHours() {
		return pricingStrategy.getMaxBlendedNumberOfHours();
	}

	public void setMaxBlendedNumberOfHours(BigDecimal maxBlendedNumberOfHours) {
		pricingStrategy.setMaxBlendedNumberOfHours(maxBlendedNumberOfHours);
	}

	public void setMaxBlendedNumberOfHours(Double maxBlendedNumberOfHours) {
		pricingStrategy.setMaxBlendedNumberOfHours(BigDecimal.valueOf(maxBlendedNumberOfHours));
	}
	
	@Transient
	public Boolean isNull() {
		return getInitialPerHourPrice() == null || getInitialNumberOfHours() == null || getAdditionalPerHourPrice() == null || getMaxBlendedNumberOfHours() == null;
	}
}
