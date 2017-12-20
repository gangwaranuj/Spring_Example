package com.workmarket.service.infra.http;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;

import java.util.HashMap;
import java.util.Map;

public abstract class HttpRequestCommand {
	private String url = "";
	private String body = "";
	private String queryString = "";
	private Map<String, String> headers = new HashMap<>();
	private Long connectionTimeout = 10000L;
	private Long socketTimeout = 60000L;

	public String getUrl() {
		return url;
	}

	public HttpRequestCommand setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getBody() {
		return body;
	}

	public HttpRequestCommand setBody(String body) {
		this.body = body;
		return this;
	}

	public String getQueryString() {
		return queryString;
	}

	public HttpRequestCommand setQueryString(String queryString) {
		this.queryString = queryString;
		return this;
	}

	public HttpRequestCommand addParameter(String key, String value) {
		if (StringUtils.isEmpty(queryString)) {
			queryString = key + "=" + value;
		} else {
			queryString += "&" + key + "=" + value;
		}

		return this;
	}

	public String getPath() {
		if (url.endsWith("?")) {
			return url + queryString;
		} else if (queryString.isEmpty()){
			return url;
		} else {
			return url + "?" + queryString;
		}
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public HttpRequestCommand setHeaders(Map<String, String> headers) {
		if (headers != null) {
			this.headers = headers;
		}
		return this;
	}

	public Long getConnectionTimeout() {
		return connectionTimeout;
	}

	public HttpRequestCommand setConnectionTimeout(Long connectionTimeout) {
		if (connectionTimeout >= 0) {
			this.connectionTimeout = connectionTimeout;
		}
		return this;
	}

	public Long getSocketTimeout() {
		return socketTimeout;
	}

	public HttpRequestCommand setSocketTimeout(Long socketTimeout) {
		if (socketTimeout >= 0) {
			this.socketTimeout = socketTimeout;
		}
		return this;
	}

	public HttpResponse<JsonNode> execute() throws UnirestException {
		return execute(null);
	}

	abstract public HttpResponse<JsonNode> execute(HttpClient httpClient) throws UnirestException;
}
