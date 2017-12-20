package com.workmarket.vault.http.server;

import com.workmarket.service.infra.http.server.MasterSlaveLoadBalanceStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class VaultLoadBalanceStrategy extends MasterSlaveLoadBalanceStrategy {

	@Value("${vault.service.http.hosts}")
	private String vaultHosts;

	@PostConstruct
	private void construct() {
		super.setFailureNumberThreshold(1);
		loadHosts(vaultHosts);
	}
}
