package com.workmarket.domains.model.pricing;

import javax.persistence.Transient;

public class NonePricingStrategy extends PricingStrategy {
	public NonePricingStrategy() {
		super(PricingStrategyType.NONE.getDescription(), (long)(PricingStrategyType.NONE.ordinal() + 1));
	}

	public NonePricingStrategy(FullPricingStrategy pricingStrategy) {
		super(pricingStrategy, PricingStrategyType.NONE.getDescription(), (long)(PricingStrategyType.NONE.ordinal() + 1));
	}
	
	@Transient
	public Boolean isNull() {
		return false;
	}
}