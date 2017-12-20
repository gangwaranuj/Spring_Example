package com.workmarket.domains.model.pricing;

import javax.persistence.Transient;

public class InternalPricingStrategy extends PricingStrategy {
	public InternalPricingStrategy() {
		super(PricingStrategyType.INTERNAL.getDescription(), (long)(PricingStrategyType.INTERNAL.ordinal() + 1));
	}

	public InternalPricingStrategy(FullPricingStrategy pricingStrategy) {
		super(pricingStrategy, PricingStrategyType.INTERNAL.getDescription(), (long)(PricingStrategyType.INTERNAL.ordinal() + 1));
	}
	
	@Transient
	public Boolean isNull() {
		return false;
	}
}