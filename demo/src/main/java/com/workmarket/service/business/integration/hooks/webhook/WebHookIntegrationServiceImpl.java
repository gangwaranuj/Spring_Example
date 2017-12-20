package com.workmarket.service.business.integration.hooks.webhook;


import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.dao.integration.webhook.WebHookClientDAO;
import com.workmarket.dao.integration.webhook.WebHookDAO;
import com.workmarket.dao.integration.webhook.WebHookHeaderDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.model.integration.webhook.GenericWebHookClient;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.model.integration.webhook.WebHookHeader;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.dto.integration.GenericWebHookClientDTO;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.integration.WebHookDTO;
import com.workmarket.service.business.dto.integration.WebHookHeaderDTO;
import com.workmarket.service.business.queue.integration.IntegrationEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class WebHookIntegrationServiceImpl implements WebHookIntegrationService {
	private static final Logger logger = LoggerFactory.getLogger(WebHookIntegrationService.class);

	private static final long TWO_WEEKS_IN_SECONDS = TimeUnit.DAYS.toSeconds(14);

	@Autowired WorkService workService;
	@Autowired IntegrationEventService integrationEventService;
	@Autowired RegistrationService registrationService;
	@Autowired RedisAdapter redisAdapter;

	@Autowired CompanyDAO companyDAO;
	@Autowired WorkDAO workDAO;
	@Autowired WebHookDAO webHookDAO;
	@Autowired WebHookClientDAO webHookClientDAO;
	@Autowired WebHookHeaderDAO webHookHeaderDAO;

	@Override
	 public List<WebHook> findWebHooksForCompany(Long companyId) {
		Assert.notNull(companyId);
		return webHookDAO.findAllWebHooksByCompany(companyId);
	}

	@Override
	public List<WebHook> findWebHooksForCompanyAndClient(Long companyId, Long webHookClientId) {
		Assert.notNull(companyId);
		return webHookDAO.findAllWebHooksByCompanyAndClient(companyId, webHookClientId);
	}

	@Override
	public Optional<AbstractWebHookClient> findWebHookClientById(Long webHookClientId) {
		Assert.notNull(webHookClientId);
		return webHookClientDAO.findWebHookClientById(webHookClientId);
	}

	@Override
	public Optional<GenericWebHookClient> findGenericWebHookClientByCompanyId(Long companyId) {
		Assert.notNull(companyId);
		return webHookClientDAO.findGenericWebHookClientByCompany(companyId);
	}

	@Override
	public GenericWebHookClient saveSettings(GenericWebHookClientDTO genericWebHookClientDTO, Long companyId) {
		Assert.notNull(companyId);

		Company company = companyDAO.get(companyId);

		Assert.notNull(company);

		Optional<GenericWebHookClient> genericWebHookClientOptional = webHookClientDAO.findGenericWebHookClientByCompany(companyId);

		GenericWebHookClient genericWebHookClient;

		if (genericWebHookClientOptional.isPresent()) {
			genericWebHookClient = genericWebHookClientOptional.get();
		} else {
			genericWebHookClient = new GenericWebHookClient();
		}

		genericWebHookClient.setDateFormat(genericWebHookClientDTO.getDateFormat());
		genericWebHookClient.setSuppressApiEvents(genericWebHookClientDTO.isSuppressApiEvents());
		genericWebHookClient.setCompany(company);
		webHookClientDAO.saveOrUpdate(genericWebHookClient);
		return genericWebHookClient;
	}

	@Override
	public Optional<WebHook> getWebHook(Long webHookId) {
		Assert.notNull(webHookId);
		return Optional.fromNullable(webHookDAO.get(webHookId));
	}

	@Override
	public Optional<Long> getWebhookClientCompanyId(Long webHookId) {
		Assert.notNull(webHookId);
		return Optional.fromNullable(webHookDAO.findWebHookClientCompanyId(webHookId));
	}

	@Override
	public WebHook saveWebHook(WebHookDTO webHookDTO) {
		WebHook webHook;

		if (webHookDTO.getId() == null) {
			webHook = new WebHook();
		} else {
			webHook = webHookDAO.get(webHookDTO.getId());
			Assert.notNull(webHook);
		}

		webHook.setContentType(webHookDTO.getContentType());
		webHook.setBody(webHookDTO.getBody());
		webHook.setIntegrationEventType(webHookDTO.getIntegrationEventType());
		webHook.setUrl(webHookDTO.getUrl());
		webHook.setMethodType(webHookDTO.getMethodType());
		webHook.setCallOrder(webHookDTO.getCallOrder());

		if (webHookDTO.isSuppressApiEvents() != null) {
			webHook.setSuppressApiEvents(webHookDTO.isSuppressApiEvents());
		}

		Optional<AbstractWebHookClient> abstractWebHookClientOptional = webHookClientDAO.findWebHookClientById(webHookDTO.getWebHookClientId());

		Assert.isTrue(abstractWebHookClientOptional.isPresent(), "Can't save webhook because webhook client id " + webHookDTO.getWebHookClientId() + " does not exist");

		webHook.setWebHookClient(abstractWebHookClientOptional.get());

		webHookDAO.saveOrUpdate(webHook);

		Set<WebHookHeader> webHookHeaders = Sets.newHashSet();

		for (WebHookHeaderDTO webHookHeaderDTO : webHookDTO.getHeaders()) {
			WebHookHeader webHookHeader;

			if (webHookHeaderDTO.getId() == null) {
				webHookHeader = new WebHookHeader();
			} else {
				webHookHeader = webHookHeaderDAO.get(webHookHeaderDTO.getId());
				Assert.notNull(webHookHeader, "Can't save Webhook header id " + webHookHeaderDTO.getId() + " - does not exist");
			}

			webHookHeader.setName(webHookHeaderDTO.getName());
			webHookHeader.setValue(webHookHeaderDTO.getValue());
			webHookHeader.setWebHook(webHook);

			webHookHeaderDAO.saveOrUpdate(webHookHeader);

			webHookHeaders.add(webHookHeader);
		}

		webHook.setWebHookHeaders(webHookHeaders);

		return webHook;
	}

	@Override
	public void clearErrors(WebHook webHook) {
		Assert.notNull(webHook);

		redisAdapter.set(RedisFilters.webHookErrorsFor(webHook.getId()), "0", TWO_WEEKS_IN_SECONDS);
	}

	@Override
	public void handleError(WebHook webHook) {
		Assert.notNull(webHook);

		String key = RedisFilters.webHookErrorsFor(webHook.getId());
		Optional<Object> errorCount = redisAdapter.get(key);

		if (!errorCount.isPresent()) {
			redisAdapter.set(RedisFilters.webHookErrorsFor(webHook.getId()), "1", TWO_WEEKS_IN_SECONDS);
			return;
		}

		Integer count;

		try {
			count = Integer.valueOf(String.valueOf(errorCount.get()));
		} catch (NumberFormatException e) {
			redisAdapter.set(RedisFilters.webHookErrorsFor(webHook.getId()), "1", TWO_WEEKS_IN_SECONDS);
			return;
		}

		if (count >= WebHook.MAX_CONSECUTIVE_ERRORS) {
			logger.warn("Max consecutive error limit reached for webhook ID " + webHook.getId() + ". Disabling.");
		} else {
			if (count == (WebHook.MAX_CONSECUTIVE_ERRORS - 10)) {
				logger.warn("Nearing max consecutive error limit for webhook ID " + webHook.getId() + ".  Consecutive error count:" + count + ".");
			}
			redisAdapter.set(RedisFilters.webHookErrorsFor(webHook.getId()), String.valueOf(count + 1), TWO_WEEKS_IN_SECONDS);
		}
	}

	@Override
	public void disable(Long webHookId) {
		Assert.notNull(webHookId);

		WebHook webHook = webHookDAO.get(webHookId);
		Assert.notNull(webHook);

		webHook.setEnabled(false);
	}

	@Override
	public void enable(Long webHookId) {
		Assert.notNull(webHookId);

		WebHook webHook = webHookDAO.get(webHookId);
		Assert.notNull(webHook);

		webHook.setEnabled(true);

		clearErrors(webHook);
	}

	@Override
	public void disableAllHooks(Long webHookClientId, Long companyId) {
		Assert.notNull(webHookClientId);
		Assert.notNull(companyId);

		List<WebHook> webHooks = findWebHooksForCompanyAndClient(companyId, webHookClientId);

		for (WebHook webHook : webHooks) {
			webHook.setEnabled(false);
		}
	}

	@Override
	public boolean canModifyWebHookClient(Long webHookClientId, Long companyId) {
		Assert.notNull(webHookClientId);
		Assert.notNull(companyId);

		AbstractWebHookClient abstractWebHookClient = webHookClientDAO.get(webHookClientId);

		if (abstractWebHookClient == null) {
			logger.debug("Attempted to modify webhook client id " + webHookClientId + " but it does not exist");
			return false;
		}

		if (!companyId.equals(abstractWebHookClient.getCompany().getId())) {
			logger.debug("Attempted to modify webhook client id " + webHookClientId + " but " + companyId + " is not authorized, expected " + abstractWebHookClient.getCompany().getId());
			return false;
		}

		return true;
	}

	@Override
	public boolean canModifyWebHook(Long webHookId, Long companyId) {
		Assert.notNull(webHookId);
		Assert.notNull(companyId);

		WebHook webHook = webHookDAO.get(webHookId);

		if (webHook == null) {
			logger.debug("Attempted to modify webhook id " + webHookId + " but it does not exist");
			return false;
		}

		if (!companyId.equals(webHook.getWebHookClient().getCompany().getId())) {
			logger.debug("Attempted to modify webhook id " + webHookId + " but " + companyId + " is not authorized, expected " + webHook.getWebHookClient().getCompany().getId());
			return false;
		}

		return true;
	}

	@Override
	public boolean canModifyWebHookHeader(Long webHookHeaderId, Long companyId) {
		Assert.notNull(webHookHeaderId);
		Assert.notNull(companyId);

		WebHookHeader webHookHeader = webHookHeaderDAO.get(webHookHeaderId);

		if (webHookHeader == null) {
			logger.debug("Attempted to modify webhook header id " + webHookHeaderId + " but it does not exist");
			return false;
		}

		if (!companyId.equals(webHookHeader.getWebHook().getWebHookClient().getCompany().getId())) {
			logger.debug("Attempted to modify webhook header id " + webHookHeaderId + " but " + companyId + " is not authorized, expected " + webHookHeader.getWebHook().getWebHookClient().getCompany().getId());
			return false;
		}

		return true;
	}

	@Override
	public void deleteWebHook(Long webHookId) {
		Assert.notNull(webHookId);

		WebHook webHook = webHookDAO.get(webHookId);
		Assert.notNull(webHook);

		webHook.setDeleted(true);
	}

	@Override
	public void deleteWebHookHeader(Long webHookHeaderId) {
		Assert.notNull(webHookHeaderId);

		WebHookHeader webHookHeader = webHookHeaderDAO.get(webHookHeaderId);
		Assert.notNull(webHookHeader);

		webHookHeader.setDeleted(true);
	}

	@Override
	public void updateWebHookCallOrder(Long webHookId, Integer callOrder) {
		Assert.notNull(webHookId);
		Assert.notNull(callOrder);

		WebHook webHook = webHookDAO.get(webHookId);
		Assert.notNull(webHook);

		webHook.setCallOrder(callOrder);
	}

	@Override
	public void saveOrUpdate(WebHook webHook) {
		webHookDAO.saveOrUpdate(webHook);
	}

}
