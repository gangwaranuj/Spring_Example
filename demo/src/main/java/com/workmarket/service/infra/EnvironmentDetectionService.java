package com.workmarket.service.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by rahul on 2013-11-03 1:37 PM
 */
@Service
public class EnvironmentDetectionService {

	@Value("${sandbox}")
	private String SANDBOX;

	public boolean isDev() {
		return SANDBOX.equals("dev");
	}

	public boolean isWestProd() {
		return SANDBOX.equals("westprod");
	}

	public boolean isProd() {
		return SANDBOX.equals("prod");
	}

	public boolean isLocal() {
		return SANDBOX.equals("local");
	}

	public String getSandbox() {
		return SANDBOX;
	}

	public boolean isLocalOrDevEnvironment() {
		return (isLocal() || isDev() || isWestProd());
	}
}
