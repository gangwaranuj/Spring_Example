package com.workmarket.domains.model.authnz;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class AuthorizedInetAddressTest {
	@Test
	public void testIsInRange() throws Exception {
		AuthorizedInetAddress ip = new AuthorizedInetAddress();
		ip.setInetAddress("192.168.1.1");

		Assert.assertTrue(ip.isInRange("192.168.1.1"));
		Assert.assertFalse(ip.isInRange("192.168.1.2"));
		Assert.assertFalse(ip.isInRange("192.168.1.100"));
		Assert.assertFalse(ip.isInRange("123.123.123.123"));
	}

	@Test
	public void testIsInRangeMask() throws Exception {
		AuthorizedInetAddress ip = new AuthorizedInetAddress();
		ip.setInetAddress("192.168.1.1/255.255.255.0");

		Assert.assertTrue(ip.isInRange("192.168.1.1"));
		Assert.assertTrue(ip.isInRange("192.168.1.2"));
		Assert.assertTrue(ip.isInRange("192.168.1.100"));
		Assert.assertFalse(ip.isInRange("192.168.2.100"));
		Assert.assertFalse(ip.isInRange("123.123.123.123"));
	}

	@Test
	public void testIsInRangeCidr() throws Exception {
		AuthorizedInetAddress ip = new AuthorizedInetAddress();
		ip.setInetAddress("192.168.1.1/24");

		Assert.assertTrue(ip.isInRange("192.168.1.1"));
		Assert.assertTrue(ip.isInRange("192.168.1.2"));
		Assert.assertTrue(ip.isInRange("192.168.1.100"));
		Assert.assertFalse(ip.isInRange("192.168.2.100"));
		Assert.assertFalse(ip.isInRange("123.123.123.123"));
	}
}
