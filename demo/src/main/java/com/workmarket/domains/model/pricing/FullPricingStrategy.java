package com.workmarket.domains.model.pricing;

import com.workmarket.configuration.Constants;
import com.workmarket.utility.BeanUtilities;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Access(AccessType.FIELD)
public class FullPricingStrategy implements Serializable, Cloneable {
	private static final long serialVersionUID = -8710079965857458451L;

	@NotNull
	@Column(name = "pricing_strategy_type", nullable = false, length = Constants.TYPE_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private PricingStrategyType pricingStrategyType = PricingStrategyType.FLAT;

	// flat price pricing strategy
	@Column(name = "flat_price", nullable = true)
	private BigDecimal flatPrice = BigDecimal.valueOf(0);
	@Column(name = "max_flat_price", nullable = true)
	private BigDecimal maxFlatPrice;

	// per hour pricing strategy
	@Column(name = "per_hour_price", nullable = true)
	private BigDecimal perHourPrice = BigDecimal.valueOf(0);
	@Column(name = "max_number_of_hours", nullable = true)
	private BigDecimal maxNumberOfHours;

	// per unit pricing strategy
	@Column(name = "per_unit_price", nullable = true)
	private BigDecimal perUnitPrice = BigDecimal.valueOf(0);
	@Column(name = "max_number_of_units", nullable = true)
	private BigDecimal maxNumberOfUnits;

	// blended per hour pricing strategy
	@Column(name = "initial_per_hour_price", nullable = true)
	private BigDecimal initialPerHourPrice = BigDecimal.valueOf(0);
	@Column(name = "initial_number_of_hours", nullable = true)
	private BigDecimal initialNumberOfHours;
	@Column(name = "additional_per_hour_price", nullable = true)
	private BigDecimal additionalPerHourPrice = BigDecimal.valueOf(0);
	@Column(name = "max_blended_number_of_hours", nullable = true)
	private BigDecimal maxBlendedNumberOfHours;

	// blended per unit pricing strategy
	@Column(name = "initial_per_unit_price", nullable = true)
	private BigDecimal initialPerUnitPrice = BigDecimal.valueOf(0);
	@Column(name = "initial_number_of_units", nullable = true)
	private BigDecimal initialNumberOfUnits;
	@Column(name = "additional_per_unit_price", nullable = true)
	private BigDecimal additionalPerUnitPrice = BigDecimal.valueOf(0);
	@Column(name = "max_blended_number_of_units", nullable = true)
	private BigDecimal maxBlendedNumberOfUnits;

	@Column(name = "sales_tax_collected_flag", nullable = false)
	private Boolean salesTaxCollectedFlag = Boolean.FALSE;

	@Column(name = "sales_tax_rate", nullable = true)
	private BigDecimal salesTaxRate;

	@Column(name = "additional_expenses", nullable = true)
	private BigDecimal additionalExpenses = BigDecimal.ZERO;

	@Column(name = "bonus", nullable = true)
	private BigDecimal bonus = BigDecimal.ZERO;

	@Column(name = "override_price", nullable = true)
	private BigDecimal overridePrice;

	public PricingStrategyType getPricingStrategyType() {
		return pricingStrategyType;
	}

	public void setPricingStrategyType(PricingStrategyType pricingStrategyType) {
		this.pricingStrategyType = pricingStrategyType;
	}

	public BigDecimal getFlatPrice() {
		return flatPrice;
	}

	public void setFlatPrice(BigDecimal flatPrice) {
		this.flatPrice = flatPrice;
	}

	public BigDecimal getMaxFlatPrice() {
		return maxFlatPrice;
	}

	public void setMaxFlatPrice(BigDecimal maxFlatPrice) {
		this.maxFlatPrice = maxFlatPrice;
	}

	public BigDecimal getPerHourPrice() {
		return perHourPrice;
	}

	public void setPerHourPrice(BigDecimal perHourPrice) {
		this.perHourPrice = perHourPrice;
	}

	public BigDecimal getMaxNumberOfHours() {
		return maxNumberOfHours;
	}

	public void setMaxNumberOfHours(BigDecimal maxNumberOfHours) {
		this.maxNumberOfHours = maxNumberOfHours;
	}

	public BigDecimal getPerUnitPrice() {
		return perUnitPrice;
	}

	public void setPerUnitPrice(BigDecimal perUnitPrice) {
		this.perUnitPrice = perUnitPrice;
	}

	public BigDecimal getMaxNumberOfUnits() {
		return maxNumberOfUnits;
	}

	public void setMaxNumberOfUnits(BigDecimal maxNumberOfUnits) {
		this.maxNumberOfUnits = maxNumberOfUnits;
	}

	public BigDecimal getInitialPerHourPrice() {
		return initialPerHourPrice;
	}

	public void setInitialPerHourPrice(BigDecimal initialPerHourPrice) {
		this.initialPerHourPrice = initialPerHourPrice;
	}

	public BigDecimal getInitialNumberOfHours() {
		return initialNumberOfHours;
	}

	public void setInitialNumberOfHours(BigDecimal initialNumberOfHours) {
		this.initialNumberOfHours = initialNumberOfHours;
	}

	public BigDecimal getAdditionalPerHourPrice() {
		return additionalPerHourPrice;
	}

	public void setAdditionalPerHourPrice(BigDecimal additionalPerHourPrice) {
		this.additionalPerHourPrice = additionalPerHourPrice;
	}

	public BigDecimal getMaxBlendedNumberOfHours() {
		return maxBlendedNumberOfHours;
	}

	public void setMaxBlendedNumberOfHours(BigDecimal maxBlendedNumberOfHours) {
		this.maxBlendedNumberOfHours = maxBlendedNumberOfHours;
	}

	public BigDecimal getInitialPerUnitPrice() {
		return initialPerUnitPrice;
	}

	public void setInitialPerUnitPrice(BigDecimal initialPerUnitPrice) {
		this.initialPerUnitPrice = initialPerUnitPrice;
	}

	public BigDecimal getInitialNumberOfUnits() {
		return initialNumberOfUnits;
	}

	public void setInitialNumberOfUnits(BigDecimal initialNumberOfUnits) {
		this.initialNumberOfUnits = initialNumberOfUnits;
	}

	public BigDecimal getAdditionalPerUnitPrice() {
		return additionalPerUnitPrice;
	}

	public void setAdditionalPerUnitPrice(BigDecimal additionalPerUnitPrice) {
		this.additionalPerUnitPrice = additionalPerUnitPrice;
	}

	public BigDecimal getMaxBlendedNumberOfUnits() {
		return maxBlendedNumberOfUnits;
	}

	public void setMaxBlendedNumberOfUnits(BigDecimal maxBlendedNumberOfUnits) {
		this.maxBlendedNumberOfUnits = maxBlendedNumberOfUnits;
	}

	public void reset() {
		BeanUtilities.copyProperties(this, new FullPricingStrategy());
		pricingStrategyType = PricingStrategyType.FLAT;
	}

	public BigDecimal getSalesTaxRate() {
		return salesTaxRate;
	}

	public void setSalesTaxRate(BigDecimal salesTaxRate) {
		this.salesTaxRate = salesTaxRate;
	}

	public Boolean getSalesTaxCollectedFlag() {
		return salesTaxCollectedFlag;
	}

	public void setSalesTaxCollectedFlag(Boolean salesTaxCollectedFlag) {
		this.salesTaxCollectedFlag = salesTaxCollectedFlag;
	}

	public BigDecimal getAdditionalExpenses() {
		return additionalExpenses;
	}

	public void setAdditionalExpenses(BigDecimal additionalExpenses) {
		this.additionalExpenses = additionalExpenses;
	}

	public BigDecimal getBonus() {
		return bonus;
	}

	public void setBonus(BigDecimal bonus) {
		this.bonus = bonus;
	}

	public String toString() {
		PricingStrategy strategy = new PricingStrategy(this, null, null);
		return strategy.toString();
	}

	@Transient
	public PricingStrategy getPricingStrategy() {
		switch (getPricingStrategyType()) {
			case FLAT:
				return new FlatPricePricingStrategy(this);
			case PER_HOUR:
				return new PerHourPricingStrategy(this);
			case PER_UNIT:
				return new PerUnitPricingStrategy(this);
			case BLENDED_PER_HOUR:
				return new BlendedPerHourPricingStrategy(this);
			case INTERNAL:
				return new InternalPricingStrategy(this);
		}
		Assert.isTrue(false, "Unable to process pricing strategy " + getPricingStrategyType());
		return null;
	}

	@Transient
	public void setPricingStrategy(PricingStrategy pricingStrategy) {
		// TODO Verify that commenting this out is indeed safe and not preserving old values.
		// reset();

		if (pricingStrategy != null) {
			BeanUtilities.copyProperties(this, pricingStrategy.getFullPricingStrategy(), new String[]{"pricingStrategy"});
		}

		if (pricingStrategy instanceof FlatPricePricingStrategy)
			this.setPricingStrategyType(PricingStrategyType.FLAT);
		if (pricingStrategy instanceof PerHourPricingStrategy)
			this.setPricingStrategyType(PricingStrategyType.PER_HOUR);
		if (pricingStrategy instanceof PerUnitPricingStrategy)
			this.setPricingStrategyType(PricingStrategyType.PER_UNIT);
		if (pricingStrategy instanceof BlendedPerHourPricingStrategy)
			this.setPricingStrategyType(PricingStrategyType.BLENDED_PER_HOUR);
		if (pricingStrategy instanceof InternalPricingStrategy)
			this.setPricingStrategyType(PricingStrategyType.INTERNAL);
	}

	public BigDecimal getOverridePrice() {
		return overridePrice;
	}

	public void setOverridePrice(BigDecimal overridePrice) {
		this.overridePrice = overridePrice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((additionalExpenses == null) ? 0 : additionalExpenses.hashCode());
		result = prime * result + ((bonus == null) ? 0 : bonus.hashCode());
		result = prime * result + ((additionalPerHourPrice == null) ? 0 : additionalPerHourPrice.hashCode());
		result = prime * result + ((additionalPerUnitPrice == null) ? 0 : additionalPerUnitPrice.hashCode());
		result = prime * result + ((flatPrice == null) ? 0 : flatPrice.hashCode());
		result = prime * result + ((initialNumberOfHours == null) ? 0 : initialNumberOfHours.hashCode());
		result = prime * result + ((initialNumberOfUnits == null) ? 0 : initialNumberOfUnits.hashCode());
		result = prime * result + ((initialPerHourPrice == null) ? 0 : initialPerHourPrice.hashCode());
		result = prime * result + ((initialPerUnitPrice == null) ? 0 : initialPerUnitPrice.hashCode());
		result = prime * result + ((maxBlendedNumberOfHours == null) ? 0 : maxBlendedNumberOfHours.hashCode());
		result = prime * result + ((maxBlendedNumberOfUnits == null) ? 0 : maxBlendedNumberOfUnits.hashCode());
		result = prime * result + ((maxFlatPrice == null) ? 0 : maxFlatPrice.hashCode());
		result = prime * result + ((maxNumberOfHours == null) ? 0 : maxNumberOfHours.hashCode());
		result = prime * result + ((maxNumberOfUnits == null) ? 0 : maxNumberOfUnits.hashCode());
		result = prime * result + ((overridePrice == null) ? 0 : overridePrice.hashCode());
		result = prime * result + ((perHourPrice == null) ? 0 : perHourPrice.hashCode());
		result = prime * result + ((perUnitPrice == null) ? 0 : perUnitPrice.hashCode());
		result = prime * result + ((pricingStrategyType == null) ? 0 : pricingStrategyType.hashCode());
		result = prime * result + ((salesTaxCollectedFlag == null) ? 0 : salesTaxCollectedFlag.hashCode());
		result = prime * result + ((salesTaxRate == null) ? 0 : salesTaxRate.hashCode());
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
		FullPricingStrategy other = (FullPricingStrategy) obj;
		if (additionalExpenses == null) {
			if (other.additionalExpenses != null)
				return false;
		} else if (other.additionalExpenses != null && !additionalExpenses.setScale(2, RoundingMode.HALF_UP).equals(other.additionalExpenses.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (bonus == null) {
			if (other.bonus != null)
				return false;
		} else if (other.bonus != null && !bonus.setScale(2, RoundingMode.HALF_UP).equals(other.bonus.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (additionalPerHourPrice == null) {
			if (other.additionalPerHourPrice != null)
				return false;
		} else if (other.additionalPerHourPrice != null && !additionalPerHourPrice.setScale(2, RoundingMode.HALF_UP).equals(other.additionalPerHourPrice.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (additionalPerUnitPrice == null) {
			if (other.additionalPerUnitPrice != null)
				return false;
		} else if (other.additionalPerUnitPrice != null && !additionalPerUnitPrice.setScale(2, RoundingMode.HALF_UP).equals(other.additionalPerUnitPrice.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (flatPrice == null) {
			if (other.flatPrice != null)
				return false;
		} else if (other.flatPrice != null && !flatPrice.setScale(2, RoundingMode.HALF_UP).equals(other.flatPrice.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (initialNumberOfHours == null) {
			if (other.initialNumberOfHours != null)
				return false;
		} else if (other.initialNumberOfHours != null && !initialNumberOfHours.setScale(2, RoundingMode.HALF_UP).equals(other.initialNumberOfHours.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (initialNumberOfUnits == null) {
			if (other.initialNumberOfUnits != null)
				return false;
		} else if (other.initialNumberOfUnits != null && !initialNumberOfUnits.setScale(2, RoundingMode.HALF_UP).equals(other.initialNumberOfUnits.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (initialPerHourPrice == null) {
			if (other.initialPerHourPrice != null)
				return false;
		} else if (other.initialPerHourPrice != null && !initialPerHourPrice.setScale(2, RoundingMode.HALF_UP).equals(other.initialPerHourPrice.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (initialPerUnitPrice == null) {
			if (other.initialPerUnitPrice != null)
				return false;
		} else if (other.initialPerUnitPrice != null && !initialPerUnitPrice.setScale(2, RoundingMode.HALF_UP).equals(other.initialPerUnitPrice.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (maxBlendedNumberOfHours == null) {
			if (other.maxBlendedNumberOfHours != null)
				return false;
		} else if (other.maxBlendedNumberOfHours != null && !maxBlendedNumberOfHours.setScale(2, RoundingMode.HALF_UP).equals(other.maxBlendedNumberOfHours.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (maxBlendedNumberOfUnits == null) {
			if (other.maxBlendedNumberOfUnits != null)
				return false;
		} else if (other.maxBlendedNumberOfUnits != null && !maxBlendedNumberOfUnits.setScale(2, RoundingMode.HALF_UP).equals(other.maxBlendedNumberOfUnits.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (maxFlatPrice == null) {
			if (other.maxFlatPrice != null)
				return false;
		} else if (other.maxFlatPrice != null && !maxFlatPrice.setScale(2, RoundingMode.HALF_UP).equals(other.maxFlatPrice.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (maxNumberOfHours == null) {
			if (other.maxNumberOfHours != null)
				return false;
		} else if (other.maxNumberOfHours != null && !maxNumberOfHours.setScale(2, RoundingMode.HALF_UP).equals(other.maxNumberOfHours.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (maxNumberOfUnits == null) {
			if (other.maxNumberOfUnits != null)
				return false;
		} else if (other.maxNumberOfUnits != null && !maxNumberOfUnits.setScale(2, RoundingMode.HALF_UP).equals(other.maxNumberOfUnits.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (overridePrice == null) {
			if (other.overridePrice != null)
				return false;
		} else if (other.overridePrice != null && !overridePrice.setScale(2, RoundingMode.HALF_UP).equals(other.overridePrice.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (perHourPrice == null) {
			if (other.perHourPrice != null)
				return false;
		} else if (other.perHourPrice != null && !perHourPrice.setScale(2, RoundingMode.HALF_UP).equals(other.perHourPrice.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (perUnitPrice == null) {
			if (other.perUnitPrice != null)
				return false;
		} else if (other.perUnitPrice != null && !perUnitPrice.setScale(2, RoundingMode.HALF_UP).equals(other.perUnitPrice.setScale(2, RoundingMode.HALF_UP)))
			return false;
		if (pricingStrategyType != other.pricingStrategyType)
			return false;
		if (salesTaxCollectedFlag == null) {
			if (other.salesTaxCollectedFlag != null)
				return false;
		} else if (!salesTaxCollectedFlag.equals(other.salesTaxCollectedFlag))
			return false;
		if (salesTaxRate == null) {
			if (other.salesTaxRate != null)
				return false;
		} else if (other.salesTaxRate != null && !salesTaxRate.setScale(2, RoundingMode.HALF_UP).equals(other.salesTaxRate.setScale(2, RoundingMode.HALF_UP)))
			return false;
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

