package com.workmarket.utility;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.Assert;

/**
 * User: micah
 * Date: 2/28/13
 * Time: 12:07 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class SpamSlayerTest {
	@Test
	public void test_SpamSlayer() {
		String email = "micah@workmarket.com";
		String encoded = SpamSlayer.slay(email);
		Assert.assertEquals(
			encoded,
			"&#109;&#105;&#099;&#097;&#104;&#064;&#119;&#111;&#114;&#107;&#109;&#097;&#114;&#107;&#101;&#116;&#046;&#099;&#111;&#109;"
		);
	}

	@Test
	public void test_SpamSlayerWithNull_ReturnsNull() {
		String str = null;
		String encoded = SpamSlayer.slay(str);
		Assert.assertNull(encoded);
	}
}
