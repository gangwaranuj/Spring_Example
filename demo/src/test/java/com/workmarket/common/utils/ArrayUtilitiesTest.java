package com.workmarket.common.utils;

import com.workmarket.splitter.FeatureDomain;
import com.workmarket.splitter.WorkmarketComponent;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Created by nick on 9/7/12 2:41 PM
 */
@WorkmarketComponent(FeatureDomain.UTILS)
@RunWith(BlockJUnit4ClassRunner.class)
public class ArrayUtilitiesTest {

	@Test
	public void testConvertToLongArrays() throws Exception {
		Assert.assertTrue(ArrayUtils.isEquals(
				new Long[]{1L, 2L, 3L, 4L, 5L},
				ArrayUtilities.convertToLongArrays(new Integer[]{1, 2, 3, 4, 5})));
		try {
			ArrayUtilities.convertToLongArrays(new Integer[]{1, 2, 3, null, 5});
			Assert.fail("Should throw exception on null entry");
			ArrayUtilities.convertToLongArrays(null);
			Assert.fail("Should throw exception on null array");
		} catch (Exception e) {
		}
	}

	@Test
	public void testUnique() throws Exception {
		Assert.assertTrue(ArrayUtils.isEquals(
				new String[]{"1", "2", "3", "4", "5"},
				ArrayUtilities.unique(new String[]{"1", "1", "2", "2", "3", "3", "4", "5", "5"})));
		Assert.assertTrue(ArrayUtils.isEquals(
				new String[]{"a", "b", "C", "D", "e"},
				ArrayUtilities.unique(new String[]{"a", "A", "b", "B", "C", "D", "d", "e", "e"})));
		try {
			ArrayUtilities.unique(null);
			Assert.fail("No nulls allowed");
		} catch (Exception e) {
		}
	}

	@Test
	public void testSort() throws Exception {
		Assert.assertTrue(ArrayUtils.isEquals(
				new String[]{"1", "2", "3", "4", "5"},
				ArrayUtilities.sort(new String[]{"3", "2", "4", "1", "5"})));
		Assert.assertTrue(ArrayUtils.isEquals(
				new String[]{"Chai", "Grass", "chai", "grass"},
				ArrayUtilities.sort(new String[]{"Grass", "grass", "Chai", "chai"})));
		try {
			ArrayUtilities.sort(null);
			Assert.fail("No nulls allowed");
		} catch (Exception e) {
		}
	}

	@Test
	public void testJoin() throws Exception {
		Assert.assertEquals(
				"Blinky, Pinky, Inky, Clyde",
				ArrayUtilities.join(new String[] {"Blinky", "Pinky", "Inky", "Clyde"}, ", "));
		Assert.assertEquals(
				"Pac-Man",
				ArrayUtilities.join(new String[] {"Pac-Man"}, ", "));
		try {
			ArrayUtilities.join(null, ", ");
			ArrayUtilities.join(new String[] {"Junior"}, null);
			Assert.fail("No nulls allowed");
		} catch (Exception e) {
		}
	}
}
