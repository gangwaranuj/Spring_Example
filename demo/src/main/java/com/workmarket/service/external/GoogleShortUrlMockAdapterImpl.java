package com.workmarket.service.external;

import org.apache.commons.lang3.RandomStringUtils;

public class GoogleShortUrlMockAdapterImpl implements ShortUrlAdapter {
	public static final String MOCK_GOOGLE_URL_FORMAT = "http://goo.gl/fakeg/%s";

	@Override
	public String getShortUrl(String urlUnused) {
		return String.format(MOCK_GOOGLE_URL_FORMAT, RandomStringUtils.randomAlphanumeric(7));
	}
}
