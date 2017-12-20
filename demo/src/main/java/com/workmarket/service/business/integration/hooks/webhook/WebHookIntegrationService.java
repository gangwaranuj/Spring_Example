package com.workmarket.service.business.integration.hooks.webhook;

import com.google.common.base.Optional;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.model.integration.webhook.GenericWebHookClient;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.service.business.dto.integration.GenericWebHookClientDTO;
import com.workmarket.service.business.dto.integration.WebHookDTO;

import java.util.List;

public interface WebHookIntegrationService {
	public List<WebHook> findWebHooksForCompany(Long companyId);
	public List<WebHook> findWebHooksForCompanyAndClient(Long companyId, Long webHookClientId);
	public Optional<AbstractWebHookClient> findWebHookClientById(Long webHookClientId);
	public Optional<GenericWebHookClient> findGenericWebHookClientByCompanyId(Long companyId);

	public GenericWebHookClient saveSettings(GenericWebHookClientDTO genericWebHookClientDTO, Long companyId);

	public Optional<WebHook> getWebHook(Long webHookId);
	public Optional<Long> getWebhookClientCompanyId(Long webHookId);

	public WebHook saveWebHook(WebHookDTO webHookDTO);

	public void clearErrors(WebHook webHook);
	public void handleError(WebHook webHook);

	public void disable(Long webHookId);
	public void enable(Long webHookId);

	public void disableAllHooks(Long webHookClientId, Long companyId);

	public boolean canModifyWebHookClient(Long webHookClientId, Long companyId);
	public boolean canModifyWebHook(Long webHookId, Long companyId);
	public boolean canModifyWebHookHeader(Long webHookHeaderId, Long companyId);

	public void deleteWebHook(Long webHookId);
	public void deleteWebHookHeader(Long webHookHeaderId);

	public void updateWebHookCallOrder(Long webHookId, Integer callOrder);
	public void saveOrUpdate(WebHook webHook);
}
