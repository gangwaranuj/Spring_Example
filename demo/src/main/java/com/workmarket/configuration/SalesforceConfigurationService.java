package com.workmarket.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SalesforceConfigurationService {

	@Value("${salesforce.leads.id}")
	private String SALESFORCE_ID;

	@Value("${salesforce.leads.key}")
	private String SALESFORCE_KEY;

	@Value("${salesforce.leads.auth.url}")
	private String SALESFORCE_AUTH_URL;

	@Value("${salesforce.leads.generate.url}")
	private String SALESFORCE_LEADS_URL;

	@Value("${salesforce.leads.username}")
	private String SALESFORCE_USERNAME;

	@Value("${salesforce.leads.password}")
	private String SALESFORCE_PASSWORD;

	public String getSALESFORCE_ID() {
		return SALESFORCE_ID;
	}

	public String getSALESFORCE_KEY() {
		return SALESFORCE_KEY;
	}

	public String getSALESFORCE_AUTH_URL() {
		return SALESFORCE_AUTH_URL;
	}

	public String getSALESFORCE_LEADS_URL() {
		return SALESFORCE_LEADS_URL;
	}

	public String getSALESFORCE_USERNAME() {
		return SALESFORCE_USERNAME;
	}

	public String getSALESFORCE_PASSWORD() {
		return SALESFORCE_PASSWORD;
	}
}
