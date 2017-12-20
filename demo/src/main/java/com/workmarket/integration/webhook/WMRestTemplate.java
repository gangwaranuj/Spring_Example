package com.workmarket.integration.webhook;

import org.springframework.web.client.RestTemplate;

/**
 * This is a workaround to implement a PATCH request
 * This class should be removed when Spring is upgraded to 3.2
 */
public class WMRestTemplate extends RestTemplate {
	private final int TIMEOUT_IN_MILLIS = 60000; // 1 minute

	public WMRestTemplate() {
		super();
		final WMHttpComponentsClientHttpRequestFactory factory = new WMHttpComponentsClientHttpRequestFactory();
		factory.setConnectionRequestTimeout(TIMEOUT_IN_MILLIS); // timeout to the pool
		factory.setConnectTimeout(TIMEOUT_IN_MILLIS); // timeout to establish connection
		factory.setReadTimeout(TIMEOUT_IN_MILLIS); // socket timeout (time between packets)
		setRequestFactory(factory);
	}
}
