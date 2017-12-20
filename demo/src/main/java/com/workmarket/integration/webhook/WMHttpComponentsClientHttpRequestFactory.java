package com.workmarket.integration.webhook;

import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

/**
 * This is a workaround to implement a PATCH request
 * This class should be removed when Spring is upgraded to 3.2
 */
public class WMHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {
	@Override
	protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {

		if (httpMethod == HttpMethod.TRACE)
			return new HttpPatch(uri);

		return super.createHttpUriRequest(httpMethod, uri);
	}
}
