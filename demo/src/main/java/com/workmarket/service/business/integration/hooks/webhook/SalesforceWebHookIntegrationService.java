package com.workmarket.service.business.integration.hooks.webhook;

import com.google.common.base.Optional;
import com.workmarket.domains.model.integration.webhook.SalesforceWebHookClient;
import com.workmarket.service.business.dto.integration.SalesforceAccessTokenDTO;
import com.workmarket.service.business.dto.integration.SalesforceRefreshTokenDTO;
import com.workmarket.service.business.dto.integration.SalesforceWebHookClientDTO;

public interface SalesforceWebHookIntegrationService {
	public SalesforceWebHookClient saveSalesforceRefreshToken(String code, Long companyId, Boolean isSandbox);
	public SalesforceWebHookClient saveSalesforceSettings(SalesforceWebHookClientDTO salesforceWebHookClientDTO, Long companyId);

	public Optional<SalesforceWebHookClient> findSalesforceSettings(Long companyId);

	public Optional<SalesforceAccessTokenDTO> getSalesforceAccessToken(SalesforceWebHookClient salesforceWebHookClient);
	public Optional<SalesforceRefreshTokenDTO> getSalesforceRefreshToken(String code, Boolean isSandbox);
	public String getSalesforceConsumerKey();
	public String getSalesforceCallbackUrl();
}
