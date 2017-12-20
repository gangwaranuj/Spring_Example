package com.workmarket.api.v1.support;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WrappedClientHttpResponse implements ClientHttpResponse {
	private HttpStatus httpStatus;
	private HttpHeaders httpHeaders;
	private String statusText;
	private InputStream in;

	public WrappedClientHttpResponse(HttpStatus httpStatus, HttpHeaders httpHeaders, String statusText, String content) {
		this.httpStatus = httpStatus;
		this.httpHeaders = httpHeaders;
		this.statusText = statusText;
		in = new ByteArrayInputStream(content.getBytes());
	}

	@Override
	public HttpStatus getStatusCode() throws IOException {
		return httpStatus;
	}

	@Override
	public int getRawStatusCode() throws IOException {
		return httpStatus.value();
	}

	@Override
	public String getStatusText() throws IOException {
		return statusText;
	}

	@Override
	public void close() {
		if (in != null) {
			try {
				in.close();
			} catch (IOException ex) {
				throw new RuntimeException("error closing input stream!", ex);
			} finally {
				in = null;
			}
		}
	}

	@Override
	public InputStream getBody() throws IOException {
		return in;
	}

	@Override
	public HttpHeaders getHeaders() {
		return httpHeaders;
	}
}
