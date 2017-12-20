package com.workmarket.dao.integration.webhook;


import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.model.integration.webhook.GenericWebHookClient;
import com.workmarket.domains.model.integration.webhook.SalesforceWebHookClient;

public interface WebHookClientDAO extends DAOInterface<AbstractWebHookClient> {

	public Optional<AbstractWebHookClient> findWebHookClientById(Long id);
	public Optional<SalesforceWebHookClient> findSalesforceWebHookClientByCompany(Long companyId);
	public Optional<GenericWebHookClient> findGenericWebHookClientByCompany(Long companyId);
}
