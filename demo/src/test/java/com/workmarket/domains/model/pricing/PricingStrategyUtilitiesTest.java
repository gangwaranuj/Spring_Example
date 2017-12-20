package com.workmarket.domains.model.pricing;

import com.workmarket.thrift.work.PricingStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;


/**
 * Created by nick on 2012-11-04 4:01 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class PricingStrategyUtilitiesTest {

	@Test
	public void testCompareThriftPricingStrategyToValue() throws Exception {
		PricingStrategy tps = new PricingStrategy();
		tps.setType(PricingStrategyType.FLAT);
		tps.setFlatPrice(5);

		assertEquals(0, PricingStrategyUtilities.compareThriftPricingStrategyToValue(tps, 5D));
		assertEquals(1, PricingStrategyUtilities.compareThriftPricingStrategyToValue(tps, 3D));
		assertEquals(-1, PricingStrategyUtilities.compareThriftPricingStrategyToValue(tps, 8D));

		tps = new PricingStrategy();
		tps.setType(PricingStrategyType.BLENDED_PER_HOUR);
		tps.setInitialNumberOfHours(4);
		tps.setInitialPerHourPrice(2d);
		tps.setMaxBlendedNumberOfHours(5d);
		tps.setAdditionalPerHourPrice(3d);

		assertEquals(0, PricingStrategyUtilities.compareThriftPricingStrategyToValue(tps, 5D));
		assertEquals(1, PricingStrategyUtilities.compareThriftPricingStrategyToValue(tps, 3D));
		assertEquals(-1, PricingStrategyUtilities.compareThriftPricingStrategyToValue(tps, 8D));
	}

	@Test
	public void testCopyHigherValues() throws Exception {
		FullPricingStrategy destps = new FullPricingStrategy();
		destps.setPricingStrategyType(PricingStrategyType.FLAT);
		destps.setFlatPrice(BigDecimal.valueOf(5D));

		FullPricingStrategy ps = new FullPricingStrategy();
		ps.setPricingStrategyType(PricingStrategyType.FLAT);
		ps.setFlatPrice(BigDecimal.valueOf(6D));

		PricingStrategyUtilities.copyHigherPriceValues(ps, destps);
		assertEquals(BigDecimal.valueOf(6D), destps.getFlatPrice());

		destps = new FullPricingStrategy();
		destps.setPricingStrategyType(PricingStrategyType.PER_HOUR);
		destps.setPerHourPrice(BigDecimal.valueOf(5D));
		destps.setMaxNumberOfHours(BigDecimal.valueOf(4D));
		ps = new FullPricingStrategy();
		ps.setPricingStrategyType(PricingStrategyType.PER_HOUR);
		ps.setPerHourPrice(BigDecimal.valueOf(6D));
		ps.setMaxNumberOfHours(BigDecimal.valueOf(3D));

		PricingStrategyUtilities.copyHigherPriceValues(ps, destps);
		assertEquals(BigDecimal.valueOf(6D), destps.getPerHourPrice());
		assertEquals(BigDecimal.valueOf(4D), destps.getMaxNumberOfHours());

		destps = new FullPricingStrategy();
		ps.setPricingStrategyType(PricingStrategyType.PER_UNIT);
		destps.setPerUnitPrice(BigDecimal.valueOf(5D));
		destps.setMaxNumberOfUnits(BigDecimal.valueOf(4D));

		ps = new FullPricingStrategy();
		ps.setPricingStrategyType(PricingStrategyType.PER_UNIT);
		ps.setPerUnitPrice(BigDecimal.valueOf(5D));
		ps.setMaxNumberOfUnits(BigDecimal.valueOf(3D));

		PricingStrategyUtilities.copyHigherPriceValues(ps, destps);
		assertEquals(BigDecimal.valueOf(5D), destps.getPerUnitPrice());
		assertEquals(BigDecimal.valueOf(4D), destps.getMaxNumberOfUnits());

		destps = new FullPricingStrategy();
		destps.setPricingStrategyType(PricingStrategyType.BLENDED_PER_HOUR);
		destps.setInitialNumberOfHours(BigDecimal.valueOf(5D));
		destps.setInitialPerHourPrice(BigDecimal.valueOf(6D));
		destps.setAdditionalPerHourPrice(BigDecimal.valueOf(5D));
		destps.setMaxBlendedNumberOfHours(BigDecimal.valueOf(4D));

		ps = new FullPricingStrategy();
		ps.setPricingStrategyType(PricingStrategyType.BLENDED_PER_HOUR);
		ps.setInitialNumberOfHours(BigDecimal.valueOf(5D));
		ps.setInitialPerHourPrice(BigDecimal.valueOf(3D));
		ps.setAdditionalPerHourPrice(BigDecimal.valueOf(5d));
		ps.setMaxBlendedNumberOfHours(BigDecimal.valueOf(3d));

		PricingStrategyUtilities.copyHigherPriceValues(ps, destps);
		assertEquals(BigDecimal.valueOf(5D), destps.getInitialNumberOfHours());
		assertEquals(BigDecimal.valueOf(6D), destps.getInitialPerHourPrice());
		assertEquals(BigDecimal.valueOf(5D), destps.getAdditionalPerHourPrice());
		assertEquals(BigDecimal.valueOf(4D), destps.getMaxBlendedNumberOfHours());
	}

	@Test
	public void testSubtract() throws Exception {
		FullPricingStrategy flat1 = new FullPricingStrategy();
		flat1.setPricingStrategyType(PricingStrategyType.FLAT);
		flat1.setFlatPrice(BigDecimal.valueOf(5D));
		flat1.setBonus(BigDecimal.valueOf(5D));

		FullPricingStrategy flat2 = new FullPricingStrategy();
		flat2.setPricingStrategyType(PricingStrategyType.FLAT);
		flat2.setFlatPrice(BigDecimal.valueOf(2D));
		flat2.setAdditionalExpenses(BigDecimal.valueOf(1D));

		com.workmarket.domains.model.pricing.PricingStrategy first = new com.workmarket.domains.model.pricing.PricingStrategy(
				flat1, "first", 1L);
		com.workmarket.domains.model.pricing.PricingStrategy second = new com.workmarket.domains.model.pricing.PricingStrategy(
				flat2, "second", 2L);

		// base test
		com.workmarket.domains.model.pricing.PricingStrategy result = PricingStrategyUtilities.subtract(first, second);
		assertEquals(BigDecimal.valueOf(3D), result.getFullPricingStrategy().getFlatPrice());
		assertEquals(BigDecimal.valueOf(-1D), result.getFullPricingStrategy().getAdditionalExpenses());

		// null test
		flat2.setFlatPrice(null);
		result = PricingStrategyUtilities.subtract(first, second);
		assertEquals(BigDecimal.valueOf(3D), result.getFullPricingStrategy().getFlatPrice());

		FullPricingStrategy blended1 = new FullPricingStrategy();
		blended1.setPricingStrategyType(PricingStrategyType.BLENDED_PER_HOUR);
		blended1.setInitialNumberOfHours(BigDecimal.valueOf(10D));
		blended1.setInitialPerHourPrice(BigDecimal.valueOf(2D));
		blended1.setAdditionalPerHourPrice(BigDecimal.valueOf(5D));
		blended1.setMaxBlendedNumberOfHours(BigDecimal.valueOf(7D));

		FullPricingStrategy blended2 = new FullPricingStrategy();
		blended2.setPricingStrategyType(PricingStrategyType.BLENDED_PER_HOUR);
		blended2.setInitialNumberOfHours(BigDecimal.valueOf(9D));
		blended2.setInitialPerHourPrice(BigDecimal.valueOf(1D));
		blended2.setAdditionalPerHourPrice(BigDecimal.valueOf(4D));
		blended2.setMaxBlendedNumberOfHours(BigDecimal.valueOf(6D));
		first = new com.workmarket.domains.model.pricing.PricingStrategy(blended1, "first", 1L);
		second = new com.workmarket.domains.model.pricing.PricingStrategy(blended2, "second", 2L);

		result = PricingStrategyUtilities.subtract(first, second);
		assertEquals(BigDecimal.valueOf(1D), result.getFullPricingStrategy().getInitialNumberOfHours());
		assertEquals(BigDecimal.valueOf(1D), result.getFullPricingStrategy().getInitialPerHourPrice());
		assertEquals(BigDecimal.valueOf(1D), result.getFullPricingStrategy().getAdditionalPerHourPrice());
		assertEquals(BigDecimal.valueOf(1D), result.getFullPricingStrategy().getMaxBlendedNumberOfHours());
	}

	@Test
	public void testClonePricingStrategy() throws Exception {

		FullPricingStrategy blended = new FullPricingStrategy();
		blended.setPricingStrategyType(PricingStrategyType.BLENDED_PER_HOUR);
		blended.setInitialNumberOfHours(BigDecimal.valueOf(10D));
		blended.setInitialPerHourPrice(BigDecimal.valueOf(2D));
		blended.setAdditionalPerHourPrice(BigDecimal.valueOf(5D));
		blended.setMaxBlendedNumberOfHours(BigDecimal.valueOf(7D));
		blended.setAdditionalExpenses(BigDecimal.valueOf(2D));
		blended.setBonus(BigDecimal.valueOf(1D));

		com.workmarket.domains.model.pricing.PricingStrategy pricing = new com.workmarket.domains.model.pricing.PricingStrategy(
				blended, PricingStrategyType.BLENDED_PER_HOUR.name(), PricingStrategyType.getId(PricingStrategyType.BLENDED_PER_HOUR));
		com.workmarket.domains.model.pricing.PricingStrategy cloned = PricingStrategyUtilities.clonePricingStrategy(pricing);
		FullPricingStrategy clonedPricing = cloned.getFullPricingStrategy();

		assertTrue(cloned.getName().equals(pricing.getName()));
		assertTrue(cloned.getId().equals(pricing.getId()));
		assertTrue(clonedPricing.getInitialNumberOfHours().equals(blended.getInitialNumberOfHours()));
		assertTrue(clonedPricing.getInitialPerHourPrice().equals(blended.getInitialPerHourPrice()));
		assertTrue(clonedPricing.getAdditionalPerHourPrice().equals(blended.getAdditionalPerHourPrice()));
		assertTrue(clonedPricing.getMaxBlendedNumberOfHours().equals(blended.getMaxBlendedNumberOfHours()));
		assertTrue(clonedPricing.getAdditionalExpenses().equals(blended.getAdditionalExpenses()));
		assertTrue(clonedPricing.getBonus().equals(blended.getBonus()));

		assertNull(PricingStrategyUtilities.clonePricingStrategy(null));
	}

	@Test
	public void testCopyThrift() {
		// Null pricingStrategy Test
		PricingStrategy thriftPricingStrategy = null;
		com.workmarket.domains.model.pricing.PricingStrategy  pricingStrategy = PricingStrategyUtilities.copyThrift(thriftPricingStrategy);

		assertNull(pricingStrategy);

		// Null pricingStrategy type
		thriftPricingStrategy = new PricingStrategy();
		thriftPricingStrategy.setFlatPrice(5);

		pricingStrategy = PricingStrategyUtilities.copyThrift(thriftPricingStrategy);
		assertNull(pricingStrategy);

		// Flat Price Test
		thriftPricingStrategy = new PricingStrategy();
		thriftPricingStrategy.setType(PricingStrategyType.FLAT);
		thriftPricingStrategy.setFlatPrice(5);

		pricingStrategy = PricingStrategyUtilities.copyThrift(thriftPricingStrategy);
		assertThat(pricingStrategy, instanceOf(FlatPricePricingStrategy.class));

		FullPricingStrategy fullPricingStrategy = pricingStrategy.getFullPricingStrategy();
		assertEquals(new BigDecimal(thriftPricingStrategy.getFlatPrice()), fullPricingStrategy.getFlatPrice());

		// Per Hour Test
		thriftPricingStrategy = new PricingStrategy();
		thriftPricingStrategy.setType(PricingStrategyType.PER_HOUR);
		thriftPricingStrategy.setPerHourPrice(5);
		thriftPricingStrategy.setMaxNumberOfHours(2);

		pricingStrategy = PricingStrategyUtilities.copyThrift(thriftPricingStrategy);
		assertThat(pricingStrategy, instanceOf(PerHourPricingStrategy.class));

		fullPricingStrategy = pricingStrategy.getFullPricingStrategy();
		assertEquals(new BigDecimal(thriftPricingStrategy.getPerHourPrice()), fullPricingStrategy.getPerHourPrice());
		assertEquals(new BigDecimal(thriftPricingStrategy.getMaxNumberOfHours()), fullPricingStrategy.getMaxNumberOfHours());

		// Per Unit Test
		thriftPricingStrategy = new PricingStrategy();
		thriftPricingStrategy.setType(PricingStrategyType.PER_UNIT);
		thriftPricingStrategy.setMaxNumberOfUnits(5);

		pricingStrategy = PricingStrategyUtilities.copyThrift(thriftPricingStrategy);
		assertThat(pricingStrategy, instanceOf(PerUnitPricingStrategy.class));

		fullPricingStrategy = pricingStrategy.getFullPricingStrategy();
		assertEquals(new BigDecimal(thriftPricingStrategy.getMaxNumberOfUnits()), fullPricingStrategy.getMaxNumberOfUnits());

		// Blended Per Hour
		thriftPricingStrategy = new PricingStrategy();
		thriftPricingStrategy.setType(PricingStrategyType.BLENDED_PER_HOUR);
		thriftPricingStrategy.setInitialPerHourPrice(5);
		thriftPricingStrategy.setInitialNumberOfHours(3);
		thriftPricingStrategy.setAdditionalPerHourPrice(6);
		thriftPricingStrategy.setMaxBlendedNumberOfHours(4);

		pricingStrategy = PricingStrategyUtilities.copyThrift(thriftPricingStrategy);
		assertThat(pricingStrategy, instanceOf(BlendedPerHourPricingStrategy.class));

		fullPricingStrategy = pricingStrategy.getFullPricingStrategy();
		assertEquals(new BigDecimal(thriftPricingStrategy.getInitialPerHourPrice()), fullPricingStrategy.getInitialPerHourPrice());
		assertEquals(new BigDecimal(thriftPricingStrategy.getInitialNumberOfHours()), fullPricingStrategy.getInitialNumberOfHours());
		assertEquals(new BigDecimal(thriftPricingStrategy.getAdditionalPerHourPrice()), fullPricingStrategy.getAdditionalPerHourPrice());
		assertEquals(new BigDecimal(thriftPricingStrategy.getMaxBlendedNumberOfHours()), fullPricingStrategy.getMaxBlendedNumberOfHours());

		// Blended Per Unit Test
		thriftPricingStrategy = new PricingStrategy();
		thriftPricingStrategy.setType(PricingStrategyType.BLENDED_PER_UNIT);

		pricingStrategy = PricingStrategyUtilities.copyThrift(thriftPricingStrategy);
		Assert.assertNull(pricingStrategy);

		// None Type Test
		thriftPricingStrategy = new PricingStrategy();
		thriftPricingStrategy.setType(PricingStrategyType.NONE);

		pricingStrategy = PricingStrategyUtilities.copyThrift(thriftPricingStrategy);
		Assert.assertNull(pricingStrategy);

		// Internal Test
		thriftPricingStrategy = new PricingStrategy();
		thriftPricingStrategy.setType(PricingStrategyType.INTERNAL);

		pricingStrategy = PricingStrategyUtilities.copyThrift(thriftPricingStrategy);
		assertThat(pricingStrategy, instanceOf(InternalPricingStrategy.class));
	}
}
