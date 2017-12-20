package com.workmarket.dao.integration.webhook;


import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.model.integration.webhook.WebHook;

import java.util.List;

public interface WebHookDAO extends DAOInterface<WebHook> {

	List<WebHook> findAllWebHooksByCompany(Long companyId);

	List<WebHook> findAllWebHooksByCompanyAndClient(Long companyId, Long webHookClientId);

	List<WebHook> findAllEnabledWebHooksByCompany(Long companyId);

	List<WebHook> findAllEnabledWebHooksByCompanyAndType(Long companyId, IntegrationEventType type);

	List<WebHook> findAllEnabledWebHooksByCompanyAndType(Long companyId, String typeCode);

	Long findWebHookClientCompanyId(Long webHookId);
}
