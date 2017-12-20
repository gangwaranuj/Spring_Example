package com.workmarket.domains.model.pricing;

import java.math.BigDecimal;

import javax.persistence.Transient;

public class FlatPricePricingStrategy extends PricingStrategy {

	public FlatPricePricingStrategy() {
		super(PricingStrategyType.FLAT.getDescription(), (long) (PricingStrategyType.FLAT.ordinal() + 1));
	}

	public FlatPricePricingStrategy(FullPricingStrategy pricingStrategy) {
		super(pricingStrategy, PricingStrategyType.FLAT.getDescription(), (long) (PricingStrategyType.FLAT.ordinal() + 1));
	}

	public BigDecimal getFlatPrice() {
		return pricingStrategy.getFlatPrice();
	}

	public void setFlatPrice(BigDecimal flatPrice) {
		pricingStrategy.setFlatPrice(flatPrice);
	}

	public void setFlatPrice(Double flatPrice) {
		pricingStrategy.setFlatPrice(BigDecimal.valueOf(flatPrice));
	}

	public BigDecimal getMaxFlatPrice() {
		return pricingStrategy.getMaxFlatPrice();
	}

	public Double getMaxFlatPriceAsDouble() {
		return pricingStrategy.getMaxFlatPrice().doubleValue();
	}

	public void setMaxFlatPrice(BigDecimal maxFlatPrice) {
		pricingStrategy.setMaxFlatPrice(maxFlatPrice);
	}

	public void setMaxFlatPrice(Double maxFlatPrice) {
		pricingStrategy.setMaxFlatPrice(BigDecimal.valueOf(maxFlatPrice));
	}
	
	@Transient
	public Boolean isNull() {
		return getFlatPrice() == null;
	}
}
