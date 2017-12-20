package com.workmarket.service.infra.http;

import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.service.infra.http.server.Host;
import com.workmarket.service.infra.http.server.LoadBalanceStrategy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpRequestUtilCommand {

	private String uri = "";
	private String host = "";
	private String body = "";
	private String queryString = "";
	private HttpMethod httpMethod = null;
	private ResponseErrorHandler responseErrorHandler = null;
	private HttpHeaders headers = new HttpHeaders();
	private Long connectionTimeout = 10000L;
	private Long socketTimeout = 60000L;
	private LoadBalanceStrategy loadBalanceStrategy = null;

	private static final Logger logger = LoggerFactory.getLogger(HttpRequestCommand.class);

	public String getUri() {
		return uri;
	}

	public String getHost() {
		return host;
	}

	public String getRequestBody() {
		return body;
	}

	public String getQueryString() {
		return queryString;
	}

	public HttpRequestUtilCommand setQueryString(String queryString) {
		this.queryString = queryString;
		return this;
	}

	public HttpRequestUtilCommand addParameter(String key, String value) {
		if (StringUtils.isEmpty(queryString)) {
			queryString = key + "=" + value;
		} else {
			queryString += "&" + key + "=" + value;
		}

		return this;
	}

	public String getPath() {
		if (uri.endsWith("?")) {
			return uri + queryString;
		} else if (queryString.isEmpty()) {
			return uri;
		} else {
			return uri + "?" + queryString;
		}
	}

	public Long getConnectionTimeout() {
		return connectionTimeout;
	}

	public Long getSocketTimeout() {
		return socketTimeout;
	}

	public HttpRequestUtilCommand toHost(String host) {
		if (host != null) {
			this.host = host;
		}
		return this;
	}

	public HttpRequestUtilCommand andUri(String uri) {
		if (uri != null) {
			this.uri = uri;
		}
		return this;
	}

	public HttpRequestUtilCommand withQueryString(String queryString) {
		this.queryString = queryString;
		return this;
	}

	public HttpRequestUtilCommand by(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
		return this;
	}

	public HttpRequestUtilCommand usingHeaders(Map<String, String> headers) {
		this.headers.setAll(headers);
		return this;
	}

	public HttpRequestUtilCommand withConnectionTimeout(Long connectionTimeout) {
		if (connectionTimeout >= 0) {
			this.connectionTimeout = connectionTimeout;
		}
		return this;
	}

	public HttpRequestUtilCommand withSocketTimeout(Long socketTimeout) {
		if (socketTimeout >= 0) {
			this.socketTimeout = socketTimeout;
		}
		return this;
	}

	public HttpRequestUtilCommand withErrorHandler(ResponseErrorHandler responseErrorHandler) {
		this.responseErrorHandler = responseErrorHandler;
		return this;
	}

	public HttpRequestUtilCommand body(String body) {
		this.body = body;
		return this;
	}

	public HttpRequestUtilCommand usingLoadBalancerStrategy(LoadBalanceStrategy loadBalanceStrategy) {
		this.loadBalanceStrategy = loadBalanceStrategy;
		return this;
	}

	public <T> T callAndReturn(Class<T> responseType) throws ServiceUnavailableException {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(responseErrorHandler == null ? new DefaultErrorHandler() : responseErrorHandler);
		if (!isLoadBalancerStrategySet()) {
			loadBalanceStrategy = new DefaultLoadBalancerStrategy();
		}
		return callAndReturnHelper(loadBalanceStrategy.getNumHosts(), restTemplate, responseType);
	}

	private <T> T callAndReturnHelper(
			int remaining,
			RestTemplate restTemplate,
			Class<T> responseType) throws ServiceUnavailableException {
		while (remaining > 0) {
			try {
				return restTemplate
						.exchange(loadBalanceStrategy.getNextHost() + getPath(),
							httpMethod,
							new HttpEntity<>(getRequestBody(), headers), responseType)
						.getBody();
			} catch (Exception e) {
				remaining--;
				logger.error(
						String.format("Error fetching host. Will attempt %s more times before failing. Request %s",
								remaining,
								this.toString()),
						e);
				callAndReturnHelper(remaining, restTemplate, responseType);
			}
		}

		throw new ServiceUnavailableException("Could not successfully make calls given load balance strategy.");
	}

	public boolean isLoadBalancerStrategySet() {
		return loadBalanceStrategy != null;
	}

	private class DefaultErrorHandler implements ResponseErrorHandler {

		@Override
		public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
			return clientHttpResponse.getStatusCode().value() % 200 != 0;
		}

		@Override
		public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
			logger.warn(String.format("Call out failed. Code [%s], Body [%s]",
					clientHttpResponse.getStatusCode(),
					IOUtils.toString(clientHttpResponse.getBody(), String.valueOf(Charset.defaultCharset()))));
		}
	}

	private class DefaultLoadBalancerStrategy implements LoadBalanceStrategy {
		@Override
		public String getNextHost() {
			return host;
		}

		@Override
		public int getNumHosts() {
			return 1;
		}

		@Override
		public void setFailureNumberThreshold(int t) {
		}

		@Override
		public void incrementFailure(String host) {
		}

		@Override
		public void incrementFailure(Host host) {
		}
	}

	@Override
	public String toString() {
		return "HttpRequestUtilCommand {" +
				"uri='" + uri + '\'' +
				", httpMethod=" + httpMethod +
				", queryString='" + queryString + '\'' +
				", host='" + host + '\'' +
				'}';
	}
}
