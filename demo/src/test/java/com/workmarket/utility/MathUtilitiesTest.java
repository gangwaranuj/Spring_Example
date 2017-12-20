package com.workmarket.utility;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Author: rocio
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class MathUtilitiesTest {

	private List<BigDecimal> disperseNumbers = Lists.newArrayListWithCapacity(4);
	private List<BigDecimal> weigths = Lists.newArrayListWithCapacity(5);

	@Before
	public void before() throws Exception {
		disperseNumbers.add(BigDecimal.valueOf(100));
		disperseNumbers.add(BigDecimal.valueOf(78));
		disperseNumbers.add(null);
		disperseNumbers.add(BigDecimal.valueOf(4));

		weigths.add(BigDecimal.valueOf(600));
		weigths.add(BigDecimal.valueOf(470));
		weigths.add(BigDecimal.valueOf(170));
		weigths.add(BigDecimal.valueOf(430));
		weigths.add(BigDecimal.valueOf(300));
	}

	@Test
	public void mean_success() throws Exception {
		assertTrue(MathUtilities.mean(disperseNumbers) == 60.666666666666664);
		assertTrue(MathUtilities.mean(weigths) == 394);
	}

	@Test
	public void median_succes() throws Exception {
		assertTrue(MathUtilities.median(disperseNumbers) == 78);
	}

	@Test
	public void variance_success() throws Exception {
		assertTrue(MathUtilities.variance(disperseNumbers) == 1686.2222222222224);
		assertTrue(MathUtilities.variance(weigths) == 21704);
	}

	@Test
	public void stdDeviation_success() throws Exception {
		assertTrue(MathUtilities.stdDeviation(disperseNumbers) == 41.063636251825315);
		assertTrue(MathUtilities.stdDeviation(weigths) == 147.32277488562318);
	}
}
