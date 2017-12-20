package com.workmarket.domains.model.pricing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PricingStrategyPriceCalculatorTest {

	private long workId = 123L;
	private BigDecimal hoursWorked = BigDecimal.valueOf(10);
	private BigDecimal unitsProcessed = BigDecimal.valueOf(15);

	PricingStrategyPriceCalculator toTest = new PricingStrategyPriceCalculator(workId, hoursWorked, unitsProcessed);

	@Test
	public void flatPricePricingStrategyCalculate() {
		FlatPricePricingStrategy strategy = mock(FlatPricePricingStrategy.class);
		BigDecimal flatPrice = BigDecimal.valueOf(10);
		when(strategy.getFlatPrice()).thenReturn(flatPrice);

		BigDecimal calculatedPrice = toTest.calculate(strategy);

		assertEquals(calculatedPrice, flatPrice);
	}

	@Test
	public void perHourPricingStrategyCalculate() {
		PerHourPricingStrategy strategy = mock(PerHourPricingStrategy.class);
		BigDecimal perHourPrice = BigDecimal.valueOf(10);
		when(strategy.getPerHourPrice()).thenReturn(perHourPrice);
		BigDecimal expectedPrice = hoursWorked.multiply(perHourPrice);

		BigDecimal calculatedPrice = toTest.calculate(strategy);

		assertEquals(calculatedPrice, expectedPrice);
	}

	@Test
	public void perUnitPricingStrategyCalculate_threeDecimals_oneUnit_roundPriceDown() {
		assertEquals(BigDecimal.valueOf(10.44), calculatePerUnitPrice(1, 10.444));
	}

	@Test
	public void perUnitPricingStrategyCalculate_threeDecimals_oneUnit_roundPriceUp() {
		assertEquals(BigDecimal.valueOf(10.45), calculatePerUnitPrice(1, 10.445));
	}

	@Test
	public void perUnitPricingStrategyCalculate_threeDecimals_twoUnits_roundAfterCalculation() {
		assertEquals(BigDecimal.valueOf(20.89), calculatePerUnitPrice(2, 10.444));
	}

	@Test
	public void perUnitPricingStrategyCalculate_threeDecimals_twoUnits_roundPriceUp() {
		assertEquals(BigDecimal.valueOf(20.89), calculatePerUnitPrice(2, 10.445));
	}

	@Test
	public void blendedPerHourPricingStrategy_baseHoursWorked_Calculate() {
		BlendedPerHourPricingStrategy strategy = mock(BlendedPerHourPricingStrategy.class);
		BigDecimal initialPerHourPrice = BigDecimal.valueOf(10);
		BigDecimal initialNumberOfHours = hoursWorked;
		BigDecimal expectedPrice = hoursWorked.multiply(initialPerHourPrice);
		when(strategy.getInitialPerHourPrice()).thenReturn(initialPerHourPrice);
		when(strategy.getInitialNumberOfHours()).thenReturn(initialNumberOfHours);

		BigDecimal calculatedPrice = toTest.calculate(strategy);

		assertEquals(calculatedPrice, expectedPrice);
	}

	@Test
	public void blendedPerHourPricingStrategy_additionalHoursWorked_Calculate() {
		BlendedPerHourPricingStrategy strategy = mock(BlendedPerHourPricingStrategy.class);
		BigDecimal initialPerHourPrice = BigDecimal.valueOf(10);
		BigDecimal additionalHoursWorked = BigDecimal.valueOf(5);
		BigDecimal additionalPerHourPrice = BigDecimal.valueOf(5);
		BigDecimal initialNumberOfHours = hoursWorked.subtract(additionalHoursWorked);
		BigDecimal basePrice = initialNumberOfHours.multiply(initialPerHourPrice);
		BigDecimal additionalPrice = additionalHoursWorked.multiply(additionalPerHourPrice);
		BigDecimal expectedPrice = basePrice.add(additionalPrice);
		when(strategy.getInitialPerHourPrice()).thenReturn(initialPerHourPrice);
		when(strategy.getInitialNumberOfHours()).thenReturn(initialNumberOfHours);
		when(strategy.getAdditionalPerHourPrice()).thenReturn(additionalPerHourPrice);

		BigDecimal calculatedPrice = toTest.calculate(strategy);

		assertEquals(calculatedPrice, expectedPrice);
	}

	private BigDecimal calculatePerUnitPrice(int units, double perUnitPrice) {

		PricingStrategyPriceCalculator toTest = new PricingStrategyPriceCalculator(workId, hoursWorked, BigDecimal.valueOf(units));

		PerUnitPricingStrategy strategy = mock(PerUnitPricingStrategy.class);
		when(strategy.getPerUnitPrice()).thenReturn(BigDecimal.valueOf(perUnitPrice));

		return toTest.calculate(strategy);
	}
}
