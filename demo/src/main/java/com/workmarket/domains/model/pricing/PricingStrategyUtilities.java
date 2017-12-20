package com.workmarket.domains.model.pricing;


import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.math.BigDecimal;

/**
 * Created by nick on 2012-11-04 3:10 PM
 * Utilities for PricingStrategy and FullPricingStrategy
 */
public class PricingStrategyUtilities {

	private static final Log logger = LogFactory.getLog(PricingStrategyUtilities.class);

	/**
	 * Compares value to the appropriate "editable" field for each pricing strategy type
	 * Thrift version
	 *
	 * @param ps
	 * @param value
	 * @return
	 */
	public static int compareThriftPricingStrategyToValue(PricingStrategy ps, Double value) {
		Assert.notNull(ps);
		Assert.notNull(value);
		switch (ps.getType()) {
			case FLAT:
				return Double.compare(ps.getFlatPrice(), value);
			case PER_HOUR:
				return Double.compare(ps.getMaxNumberOfHours(), value);
			case PER_UNIT:
				return Double.compare(ps.getMaxNumberOfUnits(), value);
			case BLENDED_PER_HOUR:
				return Double.compare(ps.getMaxBlendedNumberOfHours(), value);
			case BLENDED_PER_UNIT:
				return Double.compare(ps.getMaxBlendedNumberOfUnits(), value);
		}
		return 0;
	}

	/**
	 * Ensures that the higher value in each strategy is copied into dest. Null safe
	 *
	 * @param src
	 * @param dest
	 */
	public static void copyHigherPriceValues(FullPricingStrategy src, FullPricingStrategy dest) {
		BigDecimal val;
		switch (dest.getPricingStrategyType()) {
			case FLAT:
				val = src.getFlatPrice();
				if (val != null && val.compareTo(dest.getFlatPrice()) == 1)
					dest.setFlatPrice(val);
				break;
			case PER_HOUR:
				val = src.getPerHourPrice();
				if (val != null && val.compareTo(dest.getPerHourPrice()) == 1)
					dest.setPerHourPrice(val);
				val = src.getMaxNumberOfHours();
				if (val != null && val.compareTo(dest.getMaxNumberOfHours()) == 1)
					dest.setMaxNumberOfHours(val);
				break;
			case PER_UNIT:
				val = src.getPerUnitPrice();
				if (val != null && val.compareTo(dest.getPerUnitPrice()) == 1)
					dest.setPerHourPrice(val);
				val = src.getMaxNumberOfUnits();
				if (val != null && val.compareTo(dest.getMaxNumberOfUnits()) == 1)
					dest.setMaxNumberOfUnits(val);
				break;
			case BLENDED_PER_HOUR:
				val = src.getInitialPerHourPrice();
				if (val != null && val.compareTo(dest.getInitialPerHourPrice()) == 1)
					dest.setInitialPerHourPrice(val);
				val = src.getInitialNumberOfHours();
				if (val != null && val.compareTo(dest.getInitialNumberOfHours()) == 1)
					dest.setInitialNumberOfHours(val);
				val = src.getAdditionalPerHourPrice();
				if (val != null && val.compareTo(dest.getAdditionalPerHourPrice()) == 1)
					dest.setAdditionalPerHourPrice(val);
				val = src.getMaxBlendedNumberOfHours();
				if (val != null && val.compareTo(dest.getMaxBlendedNumberOfHours()) == 1)
					dest.setMaxBlendedNumberOfHours(val);
				break;
			case BLENDED_PER_UNIT:
				val = src.getInitialPerUnitPrice();
				if (val != null && val.compareTo(dest.getInitialPerUnitPrice()) == 1)
					dest.setInitialPerUnitPrice(val);
				val = src.getInitialNumberOfUnits();
				if (val != null && val.compareTo(dest.getInitialNumberOfUnits()) == 1)
					dest.setInitialNumberOfUnits(val);
				val = src.getAdditionalPerUnitPrice();
				if (val != null && val.compareTo(dest.getAdditionalPerUnitPrice()) == 1)
					dest.setAdditionalPerUnitPrice(val);
				val = src.getMaxBlendedNumberOfUnits();
				if (val != null && val.compareTo(dest.getMaxBlendedNumberOfUnits()) == 1)
					dest.setMaxBlendedNumberOfUnits(val);
				break;
		}
	}


	/**
	 * returns first - second. All metadata taken from first.
	 * @param first
	 * @param second
	 * @return
	 */
	public static com.workmarket.domains.model.pricing.PricingStrategy subtract(
			com.workmarket.domains.model.pricing.PricingStrategy first,
			com.workmarket.domains.model.pricing.PricingStrategy second) {

		Assert.notNull(first);
		Assert.notNull(second);
		FullPricingStrategy ps1 = first.getFullPricingStrategy();
		FullPricingStrategy ps2 = second.getFullPricingStrategy();
		Assert.notNull(ps1);
		Assert.notNull(ps2);
		Assert.isTrue(ps1.getPricingStrategyType().equals(ps2.getPricingStrategyType()));

		com.workmarket.domains.model.pricing.PricingStrategy result;
		try {
			result = (com.workmarket.domains.model.pricing.PricingStrategy) first.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException("Must pass two valid pricing strategies");
		}

		switch (ps1.getPricingStrategyType()) {
			case FLAT:
				result.getFullPricingStrategy().setFlatPrice(
						NumberUtilities.defaultValue(ps1.getFlatPrice())
								.subtract(NumberUtilities.defaultValue(ps2.getFlatPrice())));
				break;
			case PER_HOUR:
				result.getFullPricingStrategy().setPerHourPrice(
						NumberUtilities.defaultValue(ps1.getPerHourPrice())
								.subtract(NumberUtilities.defaultValue(ps2.getPerHourPrice())));
				result.getFullPricingStrategy().setMaxNumberOfHours(
						NumberUtilities.defaultValue(ps1.getMaxNumberOfHours())
								.subtract(NumberUtilities.defaultValue(ps2.getMaxNumberOfHours())));
				break;
			case PER_UNIT:
				result.getFullPricingStrategy().setPerUnitPrice(
						NumberUtilities.defaultValue(ps1.getPerUnitPrice())
								.subtract(NumberUtilities.defaultValue(ps2.getPerUnitPrice())));
				result.getFullPricingStrategy().setMaxNumberOfUnits(
						NumberUtilities.defaultValue(ps1.getMaxNumberOfUnits())
								.subtract(NumberUtilities.defaultValue(ps2.getMaxNumberOfUnits())));
				break;
			case BLENDED_PER_HOUR:
				result.getFullPricingStrategy().setInitialPerHourPrice(
						NumberUtilities.defaultValue(ps1.getInitialPerHourPrice())
								.subtract(NumberUtilities.defaultValue(ps2.getInitialPerHourPrice())));
				result.getFullPricingStrategy().setInitialNumberOfHours(
						NumberUtilities.defaultValue(ps1.getInitialNumberOfHours())
								.subtract(NumberUtilities.defaultValue(ps2.getInitialNumberOfHours())));
				result.getFullPricingStrategy().setAdditionalPerHourPrice(
						NumberUtilities.defaultValue(ps1.getAdditionalPerHourPrice())
								.subtract(NumberUtilities.defaultValue(ps2.getAdditionalPerHourPrice())));
				result.getFullPricingStrategy().setMaxBlendedNumberOfHours(
						NumberUtilities.defaultValue(ps1.getMaxBlendedNumberOfHours())
								.subtract(NumberUtilities.defaultValue(ps2.getMaxBlendedNumberOfHours())));
				break;
			case BLENDED_PER_UNIT:
				result.getFullPricingStrategy().setInitialPerUnitPrice(
						NumberUtilities.defaultValue(ps1.getInitialPerUnitPrice())
								.subtract(NumberUtilities.defaultValue(ps2.getInitialPerUnitPrice())));
				result.getFullPricingStrategy().setInitialNumberOfUnits(
						NumberUtilities.defaultValue(ps1.getInitialNumberOfUnits())
								.subtract(NumberUtilities.defaultValue(ps2.getInitialNumberOfUnits())));
				result.getFullPricingStrategy().setAdditionalPerUnitPrice(
						NumberUtilities.defaultValue(ps1.getAdditionalPerUnitPrice())
								.subtract(NumberUtilities.defaultValue(ps2.getAdditionalPerUnitPrice())));
				result.getFullPricingStrategy().setMaxBlendedNumberOfUnits(
						NumberUtilities.defaultValue(ps1.getMaxBlendedNumberOfUnits())
								.subtract(NumberUtilities.defaultValue(ps2.getMaxBlendedNumberOfUnits())));
				break;
		}

		result.getFullPricingStrategy().setAdditionalExpenses(
				NumberUtilities.defaultValue(ps1.getAdditionalExpenses())
						.subtract(NumberUtilities.defaultValue(ps2.getAdditionalExpenses())));
		result.getFullPricingStrategy().setBonus(
				NumberUtilities.defaultValue(ps1.getBonus())
						.subtract(NumberUtilities.defaultValue(ps2.getBonus())));
		return result;
	}


	public static com.workmarket.domains.model.pricing.PricingStrategy clonePricingStrategy(com.workmarket.domains.model.pricing.PricingStrategy orig) {
		if (orig == null) return null;
		com.workmarket.domains.model.pricing.PricingStrategy dest = null;
		try {
			dest = new com.workmarket.domains.model.pricing.PricingStrategy(
					(FullPricingStrategy) orig.getFullPricingStrategy().clone(), orig.getName(), orig.getId());
		} catch (CloneNotSupportedException e) {
			logger.error(String.format("Error cloning PricingStrategy %s: ", orig.toString()), e);
		}
		return dest;
	}

	public static com.workmarket.domains.model.pricing.PricingStrategy copyThrift(PricingStrategy orig) {
		com.workmarket.domains.model.pricing.PricingStrategy result = null;

		if (orig == null || orig.getType() == null) {
			return result;
		}

		FullPricingStrategy fps = new FullPricingStrategy();

		switch (orig.getType()) {
			case FLAT:
				fps.setFlatPrice(new BigDecimal(orig.getFlatPrice()));
				result = new FlatPricePricingStrategy(fps);
				break;
			case PER_HOUR:
				fps.setPerHourPrice(new BigDecimal(orig.getPerHourPrice()));
				fps.setMaxNumberOfHours(new BigDecimal(orig.getMaxNumberOfHours()));
				result = new PerHourPricingStrategy(fps);
				break;
			case PER_UNIT:
				fps.setPerUnitPrice(new BigDecimal(orig.getPerUnitPrice()));
				fps.setMaxNumberOfUnits(new BigDecimal(orig.getMaxNumberOfUnits()));
				result = new PerUnitPricingStrategy(fps);
				break;
			case BLENDED_PER_HOUR:
				result = new BlendedPerHourPricingStrategy(fps);
				fps.setInitialPerHourPrice(new BigDecimal(orig.getInitialPerHourPrice()));
				fps.setInitialNumberOfHours(new BigDecimal(orig.getInitialNumberOfHours()));
				fps.setAdditionalPerHourPrice(new BigDecimal(orig.getAdditionalPerHourPrice()));
				fps.setMaxBlendedNumberOfHours(new BigDecimal(orig.getMaxBlendedNumberOfHours()));
				break;
			case BLENDED_PER_UNIT:
				// unsupported
				break;
			case NONE:
				break;
			case INTERNAL:
				result = new InternalPricingStrategy(fps);
				break;
		}
	return result;
	}
}
