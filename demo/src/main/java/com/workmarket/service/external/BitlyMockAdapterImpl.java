package com.workmarket.service.external;

import org.apache.commons.lang3.RandomStringUtils;

public class BitlyMockAdapterImpl implements ShortUrlAdapter {

	public static final String MOCK_BITLY_URL_FORMAT = "http://bit.ly/fake/%s";

	@Override
	public String getShortUrl(String urlUnused) {
		return String.format(MOCK_BITLY_URL_FORMAT, RandomStringUtils.randomAlphanumeric(7));
	}
}
