package com.workmarket.service.external;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.apache.commons.lang3.StringUtils.startsWith;

@RunWith(BlockJUnit4ClassRunner.class)
public class BitlyMockAdapterImplTest {

	ShortUrlAdapter adapter = new BitlyMockAdapterImpl();

	@Test
	public void getShortUrl_success() {
		Assert.assertTrue(startsWith(adapter.getShortUrl("http://test"), "http://bit.ly/fake"));
	}
}
