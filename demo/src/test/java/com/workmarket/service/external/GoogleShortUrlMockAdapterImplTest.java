package com.workmarket.service.external;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.apache.commons.lang3.StringUtils.startsWith;

@RunWith(BlockJUnit4ClassRunner.class)
public class GoogleShortUrlMockAdapterImplTest {

	ShortUrlAdapter adapter = new GoogleShortUrlMockAdapterImpl();

	@Test
	public void getShortUrl_success() {
		Assert.assertTrue(startsWith(adapter.getShortUrl("http://test"), "http://goo.gl/fakeg"));
	}
}
