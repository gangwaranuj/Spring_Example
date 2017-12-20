package com.workmarket.domains.model.pricing;

import com.workmarket.service.exception.account.InvalidPricingException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PricingStrategyPriceCalculator {

	long workId;
	BigDecimal hoursWorked;
	BigDecimal unitsProcessed;

	public PricingStrategyPriceCalculator(long workId, BigDecimal hoursWorked, BigDecimal unitsProcessed) {
		this.workId = workId;
		this.hoursWorked = hoursWorked;
		this.unitsProcessed = unitsProcessed;
	}

	public BigDecimal calculate(PricingStrategy strategy) {
		if (strategy instanceof FlatPricePricingStrategy) {
			return this.calculate((FlatPricePricingStrategy) strategy);
		} else if (strategy instanceof PerHourPricingStrategy) {
			return this.calculate((PerHourPricingStrategy) strategy);
		} else if (strategy instanceof PerUnitPricingStrategy) {
			return this.calculate((PerUnitPricingStrategy) strategy);
		} else if (strategy instanceof BlendedPerHourPricingStrategy) {
			return this.calculate((BlendedPerHourPricingStrategy) strategy);
		} else {
			return BigDecimal.ZERO;
		}
	}

	public BigDecimal calculate(FlatPricePricingStrategy strategy) {
		if (strategy.getFlatPrice() == null) {
			throw new InvalidPricingException("Work " + workId + " has null flat price");
		}

		return strategy.getFlatPrice();
	}

	public BigDecimal calculate(PerHourPricingStrategy strategy) {
		if (hoursWorked == null) {
			throw new InvalidPricingException("WorkResource for " + workId + " had null hours worked");
		}

		if (strategy.getPerHourPrice() == null) {
			throw new InvalidPricingException("Work " + workId + " has null per hour price");
		}

		return hoursWorked.multiply(strategy.getPerHourPrice());
	}

	public BigDecimal calculate(PerUnitPricingStrategy strategy) {
		if (unitsProcessed == null) {
			throw new InvalidPricingException("WorkResource for " + workId + " had null units processed");
		}

		if (strategy.getPerUnitPrice() == null) {
			throw new InvalidPricingException("Work " + workId + " has null per unit price");
		}

		// We support 3 decimal places for unit price so we round to 2 decimal places here to get accurate total price
		return unitsProcessed.multiply(strategy.getPerUnitPrice()).setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal calculate(BlendedPerHourPricingStrategy strategy) {
		if (hoursWorked == null) {
			throw new InvalidPricingException("WorkResource for " + workId + " had null hours worked");
		}

		if (strategy.getInitialPerHourPrice() == null) {
			throw new InvalidPricingException("Work " + workId + " has null initial per hour price");
		}

		if (strategy.getInitialNumberOfHours() == null) {
			throw new InvalidPricingException("Work " + workId + " has null initial number of hours");
		}

		if (hoursWorked.compareTo(strategy.getInitialNumberOfHours()) > 0) {
			if (strategy.getAdditionalPerHourPrice() == null) {
				throw new InvalidPricingException("WorkResource " + workId + " has null additional per hour price");
			}

			BigDecimal cost = strategy.getInitialNumberOfHours().multiply(strategy.getInitialPerHourPrice());
			BigDecimal additionalHours = hoursWorked.subtract(strategy.getInitialNumberOfHours());
			BigDecimal additionalCost = additionalHours.multiply(strategy.getAdditionalPerHourPrice());
			return cost.add(additionalCost);
		} else {
			return hoursWorked.multiply(strategy.getInitialPerHourPrice());
		}
	}
}
