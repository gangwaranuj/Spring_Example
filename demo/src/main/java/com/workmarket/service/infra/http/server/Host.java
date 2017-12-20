package com.workmarket.service.infra.http.server;

import java.util.concurrent.atomic.AtomicInteger;

public class Host {

	private String host;
	private AtomicInteger failures = new AtomicInteger(0);
	private AtomicInteger success = new AtomicInteger(0);

	public Host(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public AtomicInteger getFailures() {
		return failures;
	}

	public void setFailures(AtomicInteger failures) {
		this.failures = failures;
	}

	public AtomicInteger getSuccess() {
		return success;
	}

	public void setSuccess(AtomicInteger success) {
		this.success = success;
	}
}
