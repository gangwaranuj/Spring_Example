package com.workmarket.api.v1.support;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class ApiTestRequestInterceptor implements ClientHttpRequestInterceptor {
	static final Logger logger = LoggerFactory.getLogger(ApiTestRequestInterceptor.class);

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		final ClientHttpResponse response = execution.execute(request, body);
		boolean debug = logger.isDebugEnabled();

		if (debug) {
			String content = IOUtils.toString(response.getBody());
			logger.debug("{} {}\n{}", new Object[] {request.getMethod(), request.getURI(), content });
			return new WrappedClientHttpResponse(response.getStatusCode(), response.getHeaders(), response.getStatusText(), content);
		}
		else {
			return response;
		}
	}
}
