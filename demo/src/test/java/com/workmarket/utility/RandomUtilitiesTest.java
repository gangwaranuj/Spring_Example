package com.workmarket.utility;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(BlockJUnit4ClassRunner.class)
public class RandomUtilitiesTest {
	@Test
	public void test_generateLoremIpsum() {
		Assert.isTrue(RandomUtilities.generateLoremIpsum(100).length() == 100);
	}
}
