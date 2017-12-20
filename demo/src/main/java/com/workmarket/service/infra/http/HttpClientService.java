package com.workmarket.service.infra.http;

import org.apache.http.impl.client.CloseableHttpClient;

import javax.naming.ConfigurationException;

public interface HttpClientService {
	/**
	 *
	 * @return null on failure
	 */
	CloseableHttpClient getClient() throws ConfigurationException;
}
