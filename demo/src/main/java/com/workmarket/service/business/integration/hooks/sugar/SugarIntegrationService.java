package com.workmarket.service.business.integration.hooks.sugar;

/**
 * User: iloveopt
 * Date: 6/4/14
 */

public interface SugarIntegrationService {
	void createLead(long companyId);
	String getAccountOwner(String companyId);
}
