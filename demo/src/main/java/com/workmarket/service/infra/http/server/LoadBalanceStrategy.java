package com.workmarket.service.infra.http.server;

public interface LoadBalanceStrategy {
	String getNextHost();
	int getNumHosts();
	void setFailureNumberThreshold(int t);
	void incrementFailure(String host);
	void incrementFailure(Host host);
}
