package com.workmarket.domains.model.pricing;

import java.math.BigDecimal;

import javax.persistence.Transient;

public class PerHourPricingStrategy extends PricingStrategy {

	public PerHourPricingStrategy() {
		super(PricingStrategyType.PER_HOUR.getDescription(), (long) (PricingStrategyType.PER_HOUR.ordinal() + 1));
	}

	public PerHourPricingStrategy(FullPricingStrategy pricingStrategy) {
		super(pricingStrategy, PricingStrategyType.PER_HOUR.getDescription(), (long) (PricingStrategyType.PER_HOUR.ordinal() + 1));
	}

	public BigDecimal getPerHourPrice() {
		return pricingStrategy.getPerHourPrice();
	}

	public void setPerHourPrice(BigDecimal perHourPrice) {
		pricingStrategy.setPerHourPrice(perHourPrice);
	}

	public void setPerHourPrice(Double perHourPrice) {
		pricingStrategy.setPerHourPrice(BigDecimal.valueOf(perHourPrice));
	}

	public BigDecimal getMaxNumberOfHours() {
		return pricingStrategy.getMaxNumberOfHours();
	}

	public void setMaxNumberOfHours(BigDecimal maxNumberOfHours) {
		pricingStrategy.setMaxNumberOfHours(maxNumberOfHours);
	}

	public void setMaxNumberOfHours(Double maxNumberOfHours) {
		pricingStrategy.setMaxNumberOfHours(BigDecimal.valueOf(maxNumberOfHours));
	}
	
	@Transient
	public Boolean isNull() {
		return getPerHourPrice() == null || getMaxNumberOfHours() == null;
	}
}
