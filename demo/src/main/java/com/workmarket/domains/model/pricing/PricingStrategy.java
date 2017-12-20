package com.workmarket.domains.model.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PricingStrategy implements Cloneable {

	FullPricingStrategy pricingStrategy;
	private Long id;
	private String name;

	public PricingStrategy(String name, Long id) {
		pricingStrategy = new FullPricingStrategy();
		this.name = name;
		this.id = id;
	}

	public PricingStrategy(FullPricingStrategy pricingStrategy, String name, Long id) {
		this.pricingStrategy = pricingStrategy;
		this.name = name;
		this.id = id;
	}

	public PricingStrategy(FullPricingStrategy pricingStrategy, PricingStrategyType type) {
		this.pricingStrategy = pricingStrategy;
		this.name = type.name();
		this.id = PricingStrategyType.getId(type);
	}

	public String getName() {
		return name;
	}

	public Long getId() {
		return id;
	}

	public FullPricingStrategy getFullPricingStrategy() {
		return pricingStrategy;
	}

	public Boolean isNull() {
		return false;
	}

	public BigDecimal calculatePrice(long workId, BigDecimal hoursWorked, BigDecimal unitsProcessed) {
		PricingStrategyPriceCalculator pricingStrategyPriceCalculator = new PricingStrategyPriceCalculator(workId, hoursWorked, unitsProcessed);
		return pricingStrategyPriceCalculator.calculate(this);
	}

	//TODO: change this toString to toPrettyString().  Right now it's used as a display value in some of the 
	//		templates, so it's not a simple find/replace.
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		switch (this.pricingStrategy.getPricingStrategyType()) {
		case FLAT:
			str.append("Flat fee of $" + this.pricingStrategy.getFlatPrice().setScale(2, RoundingMode.HALF_UP));
			break;
		case PER_HOUR:
			str.append("Hourly rate of $" + this.pricingStrategy.getPerHourPrice().setScale(2, RoundingMode.HALF_UP));
			str.append(" for a maximum of " + this.pricingStrategy.getMaxNumberOfHours() + " hours");
			break;
		case PER_UNIT:
			str.append("Unit rate of $" + getUnitPriceDecimalFormat().format(this.pricingStrategy.getPerUnitPrice()));
			str.append(" for a maximum of " + this.pricingStrategy.getMaxNumberOfUnits() + " units");
			break;
		case BLENDED_PER_HOUR:
			str.append("Initial hourly rate of $" + this.pricingStrategy.getInitialPerHourPrice().setScale(2, RoundingMode.HALF_UP));
			str.append(" for " + this.pricingStrategy.getInitialNumberOfHours());
			str.append(", then $" + this.pricingStrategy.getAdditionalPerHourPrice().setScale(2, RoundingMode.HALF_UP));
			str.append(" for each additional hour, up to " + this.pricingStrategy.getMaxBlendedNumberOfHours() + " hours");
			break;
		case BLENDED_PER_UNIT:
			str.append("Initial unit rate of $" + getUnitPriceDecimalFormat().format(this.pricingStrategy.getInitialPerUnitPrice()));
			str.append(" for " + this.pricingStrategy.getInitialNumberOfUnits());
			str.append(", then $" + this.pricingStrategy.getAdditionalPerUnitPrice().setScale(2, RoundingMode.HALF_UP));
			str.append(" for each additional unit, up to " + this.pricingStrategy.getMaxBlendedNumberOfUnits() + " units");
			break;
		case INTERNAL:
			str.append("Internal - Employee assignment. Payment directly via employer for work performed as a W2 employee.");
		}
		
		if (this.pricingStrategy.getAdditionalExpenses() != null && this.pricingStrategy.getAdditionalExpenses().compareTo(BigDecimal.ZERO) > 0) {
			str.append(", and additional expenses of $" + this.pricingStrategy.getAdditionalExpenses().setScale(2, RoundingMode.HALF_UP));
		}
		if (this.pricingStrategy.getBonus() != null && this.pricingStrategy.getBonus().compareTo(BigDecimal.ZERO) > 0) {
			str.append(", and a bonus of $" + this.pricingStrategy.getBonus().setScale(2, RoundingMode.HALF_UP));
		}

		return str.toString();
	}

	private DecimalFormat getUnitPriceDecimalFormat() {
		DecimalFormat unitPriceFormat = new DecimalFormat();
		unitPriceFormat.setMinimumFractionDigits(2);
		unitPriceFormat.setMaximumFractionDigits(3);
		unitPriceFormat.setRoundingMode(RoundingMode.HALF_UP);
		return unitPriceFormat;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pricingStrategy == null) ? 0 : pricingStrategy.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PricingStrategy other = (PricingStrategy) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pricingStrategy == null) {
			if (other.pricingStrategy != null)
				return false;
		} else if (!pricingStrategy.equals(other.pricingStrategy))
			return false;
		return true;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
