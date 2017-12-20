package com.workmarket.utility;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * User: iloveopt
 * Date: 5/7/14
 */

@RunWith(BlockJUnit4ClassRunner.class)
public class AnalyticsUtilitiesTest {

	@Test
	public void negative_scoreCardValueDisplay_dash() {
		Assert.assertEquals("-", AnalyticsUtilities.scoreCardValueDisplay(-1.00));
	}

	@Test
	public void isZero_scoreCardValueDisplay_dash() {
		Assert.assertEquals("-", AnalyticsUtilities.scoreCardValueDisplay(0.00));
	}

	@Test
	public void positive_scoreCardValueDisplay_oneDigit() {
		Assert.assertEquals("95.0", AnalyticsUtilities.scoreCardValueDisplay(0.95));
		Assert.assertEquals("95.1", AnalyticsUtilities.scoreCardValueDisplay(0.951));
		Assert.assertEquals("95.1", AnalyticsUtilities.scoreCardValueDisplay(0.9511));
	}

	@Test
	public void one_scoreCardValueDisplay_noDigit() {
		Assert.assertEquals("100", AnalyticsUtilities.scoreCardValueDisplay(1.0));
	}
}
