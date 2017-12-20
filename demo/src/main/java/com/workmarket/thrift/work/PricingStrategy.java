package com.workmarket.thrift.work;

import com.workmarket.domains.model.pricing.PricingStrategyType;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PricingStrategy implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private PricingStrategyType type;
	private double flatPrice;
	private double maxFlatPrice;
	private double perHourPrice;
	private double maxNumberOfHours;
	private double perUnitPrice;
	private double maxNumberOfUnits;
	private double initialPerHourPrice;
	private double initialNumberOfHours;
	private double additionalPerHourPrice;
	private double maxBlendedNumberOfHours;
	private double initialPerUnitPrice;
	private double initialNumberOfUnits;
	private double additionalPerUnitPrice;
	private double maxBlendedNumberOfUnits;
	private double maxSpendLimit;
	private double additionalExpenses;
	private double bonus;
	private double overridePrice;
	private boolean offlinePayment;

	public PricingStrategy() {
	}

	public PricingStrategy(
			long id,
			PricingStrategyType type,
			double flatPrice,
			double maxFlatPrice,
			double perHourPrice,
			double maxNumberOfHours,
			double perUnitPrice,
			double maxNumberOfUnits,
			double initialPerHourPrice,
			double initialNumberOfHours,
			double additionalPerHourPrice,
			double maxBlendedNumberOfHours,
			double initialPerUnitPrice,
			double initialNumberOfUnits,
			double additionalPerUnitPrice,
			double maxBlendedNumberOfUnits,
			double maxSpendLimit,
			double additionalExpenses,
			double bonus,
			double overridePrice) {
		this();
		this.id = id;
		this.type = type;
		this.flatPrice = flatPrice;
		this.maxFlatPrice = maxFlatPrice;
		this.perHourPrice = perHourPrice;
		this.maxNumberOfHours = maxNumberOfHours;
		this.perUnitPrice = perUnitPrice;
		this.maxNumberOfUnits = maxNumberOfUnits;
		this.initialPerHourPrice = initialPerHourPrice;
		this.initialNumberOfHours = initialNumberOfHours;
		this.additionalPerHourPrice = additionalPerHourPrice;
		this.maxBlendedNumberOfHours = maxBlendedNumberOfHours;
		this.initialPerUnitPrice = initialPerUnitPrice;
		this.initialNumberOfUnits = initialNumberOfUnits;
		this.additionalPerUnitPrice = additionalPerUnitPrice;
		this.maxBlendedNumberOfUnits = maxBlendedNumberOfUnits;
		this.maxSpendLimit = maxSpendLimit;
		this.additionalExpenses = additionalExpenses;
		this.bonus = bonus;
		this.overridePrice = overridePrice;
	}

	public long getId() {
		return this.id;
	}

	public PricingStrategy setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public PricingStrategyType getType() {
		return this.type;
	}

	public PricingStrategy setType(PricingStrategyType type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public double getFlatPrice() {
		return this.flatPrice;
	}

	public PricingStrategy setFlatPrice(double flatPrice) {
		this.flatPrice = flatPrice;
		return this;
	}

	public boolean isSetFlatPrice() {
		return (flatPrice > 0D);
	}

	public double getMaxFlatPrice() {
		return this.maxFlatPrice;
	}

	public PricingStrategy setMaxFlatPrice(double maxFlatPrice) {
		this.maxFlatPrice = maxFlatPrice;
		return this;
	}

	public boolean isSetMaxFlatPrice() {
		return (maxFlatPrice > 0D);
	}

	public double getPerHourPrice() {
		return this.perHourPrice;
	}

	public PricingStrategy setPerHourPrice(double perHourPrice) {
		this.perHourPrice = perHourPrice;
		return this;
	}

	public boolean isSetPerHourPrice() {
		return (perHourPrice > 0D);
	}

	public double getMaxNumberOfHours() {
		return this.maxNumberOfHours;
	}

	public PricingStrategy setMaxNumberOfHours(double maxNumberOfHours) {
		this.maxNumberOfHours = maxNumberOfHours;
		return this;
	}

	public boolean isSetMaxNumberOfHours() {
		return (maxNumberOfHours > 0D);
	}

	public double getPerUnitPrice() {
		return this.perUnitPrice;
	}

	public PricingStrategy setPerUnitPrice(double perUnitPrice) {
		this.perUnitPrice = perUnitPrice;
		return this;
	}

	public boolean isSetPerUnitPrice() {
		return (perUnitPrice > 0D);
	}

	public double getMaxNumberOfUnits() {
		return this.maxNumberOfUnits;
	}

	public PricingStrategy setMaxNumberOfUnits(double maxNumberOfUnits) {
		this.maxNumberOfUnits = maxNumberOfUnits;
		return this;
	}

	public boolean isSetMaxNumberOfUnits() {
		return (maxNumberOfUnits > 0D);
	}

	public double getInitialPerHourPrice() {
		return this.initialPerHourPrice;
	}

	public PricingStrategy setInitialPerHourPrice(double initialPerHourPrice) {
		this.initialPerHourPrice = initialPerHourPrice;
		return this;
	}

	public boolean isSetInitialPerHourPrice() {
		return (initialPerHourPrice > 0D);
	}

	public double getInitialNumberOfHours() {
		return this.initialNumberOfHours;
	}

	public PricingStrategy setInitialNumberOfHours(double initialNumberOfHours) {
		this.initialNumberOfHours = initialNumberOfHours;
		return this;
	}

	public boolean isSetInitialNumberOfHours() {
		return (initialNumberOfHours > 0D);
	}

	public double getAdditionalPerHourPrice() {
		return this.additionalPerHourPrice;
	}

	public PricingStrategy setAdditionalPerHourPrice(double additionalPerHourPrice) {
		this.additionalPerHourPrice = additionalPerHourPrice;
		return this;
	}

	public boolean isSetAdditionalPerHourPrice() {
		return (additionalPerHourPrice > 0D);
	}

	public double getMaxBlendedNumberOfHours() {
		return this.maxBlendedNumberOfHours;
	}

	public PricingStrategy setMaxBlendedNumberOfHours(double maxBlendedNumberOfHours) {
		this.maxBlendedNumberOfHours = maxBlendedNumberOfHours;
		return this;
	}

	public boolean isSetMaxBlendedNumberOfHours() {
		return (maxBlendedNumberOfHours > 0D);
	}

	public double getInitialPerUnitPrice() {
		return this.initialPerUnitPrice;
	}

	public PricingStrategy setInitialPerUnitPrice(double initialPerUnitPrice) {
		this.initialPerUnitPrice = initialPerUnitPrice;
		return this;
	}

	public boolean isSetInitialPerUnitPrice() {
		return (this.initialPerUnitPrice > 0D);
	}

	public double getInitialNumberOfUnits() {
		return this.initialNumberOfUnits;
	}

	public PricingStrategy setInitialNumberOfUnits(double initialNumberOfUnits) {
		this.initialNumberOfUnits = initialNumberOfUnits;
		return this;
	}

	public boolean isSetInitialNumberOfUnits() {
		return (initialNumberOfUnits > 0D);
	}

	public double getAdditionalPerUnitPrice() {
		return this.additionalPerUnitPrice;
	}

	public PricingStrategy setAdditionalPerUnitPrice(double additionalPerUnitPrice) {
		this.additionalPerUnitPrice = additionalPerUnitPrice;
		return this;
	}

	public boolean isSetAdditionalPerUnitPrice() {
		return (additionalPerUnitPrice > 0D);
	}

	public double getMaxBlendedNumberOfUnits() {
		return this.maxBlendedNumberOfUnits;
	}

	public PricingStrategy setMaxBlendedNumberOfUnits(double maxBlendedNumberOfUnits) {
		this.maxBlendedNumberOfUnits = maxBlendedNumberOfUnits;
		return this;
	}

	public boolean isSetMaxBlendedNumberOfUnits() {
		return (maxBlendedNumberOfUnits > 0D);
	}

	public double getMaxSpendLimit() {
		return this.maxSpendLimit;
	}

	public PricingStrategy setMaxSpendLimit(double maxSpendLimit) {
		this.maxSpendLimit = maxSpendLimit;
		return this;
	}

	public boolean isSetMaxSpendLimit() {
		return (maxSpendLimit > 0D);
	}

	public double getAdditionalExpenses() {
		return this.additionalExpenses;
	}

	public PricingStrategy setAdditionalExpenses(double additionalExpenses) {
		this.additionalExpenses = additionalExpenses;
		return this;
	}

	public boolean isSetAdditionalExpenses() {
		return (additionalExpenses > 0D);
	}

	public double getBonus() {
		return bonus;
	}

	public PricingStrategy setBonus(double bonus) {
		this.bonus = bonus;
		return this;
	}

	public boolean isSetBonus() {
		return (bonus > 0D);
	}

	public double getOverridePrice() {
		return this.overridePrice;
	}

	public PricingStrategy setOverridePrice(double overridePrice) {
		this.overridePrice = overridePrice;
		return this;
	}

	public boolean isSetOverridePrice() {
		return (overridePrice > 0D);
	}

	// Used to display max unit price in assignment details page
	public double getMaxUnitPrice() {
		// We support 3 decimal places for unit price so we round to 2 decimal places here to get accurate max unit price
		return BigDecimal.valueOf(perUnitPrice).multiply(BigDecimal.valueOf(maxNumberOfUnits)).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	public boolean isOfflinePayment() {
		return offlinePayment;
	}

	public void setOfflinePayment(boolean offlinePayment) {
		this.offlinePayment = offlinePayment;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof PricingStrategy)
			return this.equals((PricingStrategy) that);
		return false;
	}

	private boolean equals(PricingStrategy that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_type = true && this.isSetType();
		boolean that_present_type = true && that.isSetType();
		if (this_present_type || that_present_type) {
			if (!(this_present_type && that_present_type))
				return false;
			if (!this.type.equals(that.type))
				return false;
		}

		boolean this_present_flatPrice = true;
		boolean that_present_flatPrice = true;
		if (this_present_flatPrice || that_present_flatPrice) {
			if (!(this_present_flatPrice && that_present_flatPrice))
				return false;
			if (this.flatPrice != that.flatPrice)
				return false;
		}

		boolean this_present_maxFlatPrice = true;
		boolean that_present_maxFlatPrice = true;
		if (this_present_maxFlatPrice || that_present_maxFlatPrice) {
			if (!(this_present_maxFlatPrice && that_present_maxFlatPrice))
				return false;
			if (this.maxFlatPrice != that.maxFlatPrice)
				return false;
		}

		boolean this_present_perHourPrice = true;
		boolean that_present_perHourPrice = true;
		if (this_present_perHourPrice || that_present_perHourPrice) {
			if (!(this_present_perHourPrice && that_present_perHourPrice))
				return false;
			if (this.perHourPrice != that.perHourPrice)
				return false;
		}

		boolean this_present_maxNumberOfHours = true;
		boolean that_present_maxNumberOfHours = true;
		if (this_present_maxNumberOfHours || that_present_maxNumberOfHours) {
			if (!(this_present_maxNumberOfHours && that_present_maxNumberOfHours))
				return false;
			if (this.maxNumberOfHours != that.maxNumberOfHours)
				return false;
		}

		boolean this_present_perUnitPrice = true;
		boolean that_present_perUnitPrice = true;
		if (this_present_perUnitPrice || that_present_perUnitPrice) {
			if (!(this_present_perUnitPrice && that_present_perUnitPrice))
				return false;
			if (this.perUnitPrice != that.perUnitPrice)
				return false;
		}

		boolean this_present_maxNumberOfUnits = true;
		boolean that_present_maxNumberOfUnits = true;
		if (this_present_maxNumberOfUnits || that_present_maxNumberOfUnits) {
			if (!(this_present_maxNumberOfUnits && that_present_maxNumberOfUnits))
				return false;
			if (this.maxNumberOfUnits != that.maxNumberOfUnits)
				return false;
		}

		boolean this_present_initialPerHourPrice = true;
		boolean that_present_initialPerHourPrice = true;
		if (this_present_initialPerHourPrice || that_present_initialPerHourPrice) {
			if (!(this_present_initialPerHourPrice && that_present_initialPerHourPrice))
				return false;
			if (this.initialPerHourPrice != that.initialPerHourPrice)
				return false;
		}

		boolean this_present_initialNumberOfHours = true;
		boolean that_present_initialNumberOfHours = true;
		if (this_present_initialNumberOfHours || that_present_initialNumberOfHours) {
			if (!(this_present_initialNumberOfHours && that_present_initialNumberOfHours))
				return false;
			if (this.initialNumberOfHours != that.initialNumberOfHours)
				return false;
		}

		boolean this_present_additionalPerHourPrice = true;
		boolean that_present_additionalPerHourPrice = true;
		if (this_present_additionalPerHourPrice || that_present_additionalPerHourPrice) {
			if (!(this_present_additionalPerHourPrice && that_present_additionalPerHourPrice))
				return false;
			if (this.additionalPerHourPrice != that.additionalPerHourPrice)
				return false;
		}

		boolean this_present_maxBlendedNumberOfHours = true;
		boolean that_present_maxBlendedNumberOfHours = true;
		if (this_present_maxBlendedNumberOfHours || that_present_maxBlendedNumberOfHours) {
			if (!(this_present_maxBlendedNumberOfHours && that_present_maxBlendedNumberOfHours))
				return false;
			if (this.maxBlendedNumberOfHours != that.maxBlendedNumberOfHours)
				return false;
		}

		boolean this_present_initialPerUnitPrice = true;
		boolean that_present_initialPerUnitPrice = true;
		if (this_present_initialPerUnitPrice || that_present_initialPerUnitPrice) {
			if (!(this_present_initialPerUnitPrice && that_present_initialPerUnitPrice))
				return false;
			if (this.initialPerUnitPrice != that.initialPerUnitPrice)
				return false;
		}

		boolean this_present_initialNumberOfUnits = true;
		boolean that_present_initialNumberOfUnits = true;
		if (this_present_initialNumberOfUnits || that_present_initialNumberOfUnits) {
			if (!(this_present_initialNumberOfUnits && that_present_initialNumberOfUnits))
				return false;
			if (this.initialNumberOfUnits != that.initialNumberOfUnits)
				return false;
		}

		boolean this_present_additionalPerUnitPrice = true;
		boolean that_present_additionalPerUnitPrice = true;
		if (this_present_additionalPerUnitPrice || that_present_additionalPerUnitPrice) {
			if (!(this_present_additionalPerUnitPrice && that_present_additionalPerUnitPrice))
				return false;
			if (this.additionalPerUnitPrice != that.additionalPerUnitPrice)
				return false;
		}

		boolean this_present_maxBlendedNumberOfUnits = true;
		boolean that_present_maxBlendedNumberOfUnits = true;
		if (this_present_maxBlendedNumberOfUnits || that_present_maxBlendedNumberOfUnits) {
			if (!(this_present_maxBlendedNumberOfUnits && that_present_maxBlendedNumberOfUnits))
				return false;
			if (this.maxBlendedNumberOfUnits != that.maxBlendedNumberOfUnits)
				return false;
		}

		boolean this_present_maxSpendLimit = true;
		boolean that_present_maxSpendLimit = true;
		if (this_present_maxSpendLimit || that_present_maxSpendLimit) {
			if (!(this_present_maxSpendLimit && that_present_maxSpendLimit))
				return false;
			if (this.maxSpendLimit != that.maxSpendLimit)
				return false;
		}

		boolean this_present_additionalExpenses = true;
		boolean that_present_additionalExpenses = true;
		if (this_present_additionalExpenses || that_present_additionalExpenses) {
			if (!(this_present_additionalExpenses && that_present_additionalExpenses))
				return false;
			if (this.additionalExpenses != that.additionalExpenses)
				return false;
		}

		boolean this_present_bonus = true;
		boolean that_present_bonus = true;
		if (this_present_bonus || that_present_bonus) {
			if (!(this_present_bonus && that_present_bonus))
				return false;
			if (this.bonus != that.bonus)
				return false;
		}

		boolean this_present_overridePrice = true;
		boolean that_present_overridePrice = true;
		if (this_present_overridePrice || that_present_overridePrice) {
			if (!(this_present_overridePrice && that_present_overridePrice))
				return false;
			if (this.overridePrice != that.overridePrice)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_type = true && (isSetType());
		builder.append(present_type);
		if (present_type)
			builder.append(type);

		boolean present_flatPrice = true;
		builder.append(present_flatPrice);
		if (present_flatPrice)
			builder.append(flatPrice);

		boolean present_maxFlatPrice = true;
		builder.append(present_maxFlatPrice);
		if (present_maxFlatPrice)
			builder.append(maxFlatPrice);

		boolean present_perHourPrice = true;
		builder.append(present_perHourPrice);
		if (present_perHourPrice)
			builder.append(perHourPrice);

		boolean present_maxNumberOfHours = true;
		builder.append(present_maxNumberOfHours);
		if (present_maxNumberOfHours)
			builder.append(maxNumberOfHours);

		boolean present_perUnitPrice = true;
		builder.append(present_perUnitPrice);
		if (present_perUnitPrice)
			builder.append(perUnitPrice);

		boolean present_maxNumberOfUnits = true;
		builder.append(present_maxNumberOfUnits);
		if (present_maxNumberOfUnits)
			builder.append(maxNumberOfUnits);

		boolean present_initialPerHourPrice = true;
		builder.append(present_initialPerHourPrice);
		if (present_initialPerHourPrice)
			builder.append(initialPerHourPrice);

		boolean present_initialNumberOfHours = true;
		builder.append(present_initialNumberOfHours);
		if (present_initialNumberOfHours)
			builder.append(initialNumberOfHours);

		boolean present_additionalPerHourPrice = true;
		builder.append(present_additionalPerHourPrice);
		if (present_additionalPerHourPrice)
			builder.append(additionalPerHourPrice);

		boolean present_maxBlendedNumberOfHours = true;
		builder.append(present_maxBlendedNumberOfHours);
		if (present_maxBlendedNumberOfHours)
			builder.append(maxBlendedNumberOfHours);

		boolean present_initialPerUnitPrice = true;
		builder.append(present_initialPerUnitPrice);
		if (present_initialPerUnitPrice)
			builder.append(initialPerUnitPrice);

		boolean present_initialNumberOfUnits = true;
		builder.append(present_initialNumberOfUnits);
		if (present_initialNumberOfUnits)
			builder.append(initialNumberOfUnits);

		boolean present_additionalPerUnitPrice = true;
		builder.append(present_additionalPerUnitPrice);
		if (present_additionalPerUnitPrice)
			builder.append(additionalPerUnitPrice);

		boolean present_maxBlendedNumberOfUnits = true;
		builder.append(present_maxBlendedNumberOfUnits);
		if (present_maxBlendedNumberOfUnits)
			builder.append(maxBlendedNumberOfUnits);

		boolean present_maxSpendLimit = true;
		builder.append(present_maxSpendLimit);
		if (present_maxSpendLimit)
			builder.append(maxSpendLimit);

		boolean present_additionalExpenses = true;
		builder.append(present_additionalExpenses);
		if (present_additionalExpenses)
			builder.append(additionalExpenses);

		boolean present_bonus = true;
		builder.append(present_bonus);
		if (present_bonus)
			builder.append(bonus);

		boolean present_overridePrice = true;
		builder.append(present_overridePrice);
		if (present_overridePrice)
			builder.append(overridePrice);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PricingStrategy(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("type:");
		if (this.type == null) {
			sb.append("null");
		} else {
			sb.append(this.type);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("flatPrice:");
		sb.append(this.flatPrice);
		first = false;
		if (!first) sb.append(", ");
		sb.append("maxFlatPrice:");
		sb.append(this.maxFlatPrice);
		first = false;
		if (!first) sb.append(", ");
		sb.append("perHourPrice:");
		sb.append(this.perHourPrice);
		first = false;
		if (!first) sb.append(", ");
		sb.append("maxNumberOfHours:");
		sb.append(this.maxNumberOfHours);
		first = false;
		if (!first) sb.append(", ");
		sb.append("perUnitPrice:");
		sb.append(this.perUnitPrice);
		first = false;
		if (!first) sb.append(", ");
		sb.append("maxNumberOfUnits:");
		sb.append(this.maxNumberOfUnits);
		first = false;
		if (!first) sb.append(", ");
		sb.append("initialPerHourPrice:");
		sb.append(this.initialPerHourPrice);
		first = false;
		if (!first) sb.append(", ");
		sb.append("initialNumberOfHours:");
		sb.append(this.initialNumberOfHours);
		first = false;
		if (!first) sb.append(", ");
		sb.append("additionalPerHourPrice:");
		sb.append(this.additionalPerHourPrice);
		first = false;
		if (!first) sb.append(", ");
		sb.append("maxBlendedNumberOfHours:");
		sb.append(this.maxBlendedNumberOfHours);
		first = false;
		if (!first) sb.append(", ");
		sb.append("initialPerUnitPrice:");
		sb.append(this.initialPerUnitPrice);
		first = false;
		if (!first) sb.append(", ");
		sb.append("initialNumberOfUnits:");
		sb.append(this.initialNumberOfUnits);
		first = false;
		if (!first) sb.append(", ");
		sb.append("additionalPerUnitPrice:");
		sb.append(this.additionalPerUnitPrice);
		first = false;
		if (!first) sb.append(", ");
		sb.append("maxBlendedNumberOfUnits:");
		sb.append(this.maxBlendedNumberOfUnits);
		first = false;
		if (!first) sb.append(", ");
		sb.append("maxSpendLimit:");
		sb.append(this.maxSpendLimit);
		first = false;
		if (!first) sb.append(", ");
		sb.append("additionalExpenses:");
		sb.append(this.additionalExpenses);
		first = false;
		if (!first) sb.append(", ");
		sb.append("bonus:");
		sb.append(this.bonus);
		first = false;
		if (!first) sb.append(", ");
		sb.append("overridePrice:");
		sb.append(this.overridePrice);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}