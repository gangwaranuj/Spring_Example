package com.workmarket.service.orgstructure;

import com.workmarket.business.OrgStructClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class OrgStructureConfiguration {

	/**
	 * Wire OrgStructClient.
	 *
	 * @return OrgStructClient.
	 */
	@Bean(name = "OrgStructClient")
	protected OrgStructClient getOrgStructClient() throws IOException {
		return new OrgStructClient();
	}
}
