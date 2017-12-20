package com.workmarket.service.business.dto.integration;

import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.util.Map;

public class ParsedWebHookDTO {
	String body;
	URI uri;
	Map<String, String> headers;
	Map<String, String> variables; // variableName -> value

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}

	@Override
	public String toString() {
		// Limit the body size as sometimes we embed assets which can be very big. The value 10000 was
		// chosen looking at the max size in production (7721) and rounding up for room.
		return String.format("URI[%s] HEADERS[%s] BODY[%s]",
				uri, headers, StringUtils.isNotBlank(body) ? body.substring(0, Math.min(10000, body.length())) : "");
	}
}
