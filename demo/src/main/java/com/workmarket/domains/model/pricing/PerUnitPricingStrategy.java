package com.workmarket.domains.model.pricing;

import java.math.BigDecimal;

import javax.persistence.Transient;

public class PerUnitPricingStrategy extends PricingStrategy {

	public PerUnitPricingStrategy() {
		super(PricingStrategyType.PER_UNIT.getDescription(), (long) (PricingStrategyType.PER_UNIT.ordinal() + 1));
	}

	public PerUnitPricingStrategy(FullPricingStrategy pricingStrategy) {
		super(pricingStrategy, PricingStrategyType.PER_UNIT.getDescription(), (long) (PricingStrategyType.PER_UNIT.ordinal() + 1));
	}

	public BigDecimal getPerUnitPrice() {
		return pricingStrategy.getPerUnitPrice();
	}

	public void setPerUnitPrice(BigDecimal perUnitPrice) {
		pricingStrategy.setPerUnitPrice(perUnitPrice);
	}

	public void setPerUnitPrice(Double perUnitPrice) {
		pricingStrategy.setPerUnitPrice(BigDecimal.valueOf(perUnitPrice));
	}

	public BigDecimal getMaxNumberOfUnits() {
		return pricingStrategy.getMaxNumberOfUnits();
	}

	public void setMaxNumberOfUnits(BigDecimal maxNumberOfUnits) {
		pricingStrategy.setMaxNumberOfUnits(maxNumberOfUnits);
	}

	public void setMaxNumberOfUnits(Double maxNumberOfUnits) {
		pricingStrategy.setMaxNumberOfUnits(BigDecimal.valueOf(maxNumberOfUnits));
	}
	
	@Transient
	public Boolean isNull() {
		return getPerUnitPrice() == null || getMaxNumberOfUnits() == null;
	}
}
