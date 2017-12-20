package com.workmarket.integration.webhook;

import com.google.common.base.Optional;

import com.workmarket.configuration.Constants;
import com.workmarket.dao.integration.webhook.WebHookClientDAO;
import com.workmarket.dao.integration.webhook.WebHookDAO;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.model.integration.webhook.GenericWebHookClient;
import com.workmarket.domains.model.integration.webhook.SalesforceWebHookClient;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.model.integration.webhook.WebHookHeader;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.integration.GenericWebHookClientDTO;
import com.workmarket.service.business.dto.integration.SalesforceWebHookClientDTO;
import com.workmarket.service.business.dto.integration.WebHookDTO;
import com.workmarket.service.business.dto.integration.WebHookHeaderDTO;
import com.workmarket.service.business.integration.hooks.webhook.SalesforceWebHookIntegrationService;
import com.workmarket.service.business.integration.hooks.webhook.WebHookHTTPPoolingFactory;
import com.workmarket.service.business.integration.hooks.webhook.WebHookIntegrationService;
import com.workmarket.test.IntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@Transactional
public class WebHookIntegrationServiceIT extends BaseServiceIT {
	@Autowired WebHookIntegrationService webHookIntegrationService;
	@Autowired SalesforceWebHookIntegrationService salesforceWebHookIntegrationService;
	@Autowired WebHookHTTPPoolingFactory webHookHTTPPoolingFactory;

	@Autowired WebHookClientDAO webHookClientDAO;
	@Autowired WebHookDAO webHookDAO;

	static Long COMPANY_ID = Constants.WM_COMPANY_ID;

	private WebHook createSalesforceWebhook() {
		SalesforceWebHookClientDTO salesforceWebHookClientDTO = new SalesforceWebHookClientDTO();
		salesforceWebHookClientDTO.setDateFormat(AbstractWebHookClient.DateFormat.UNIX);

		SalesforceWebHookClient salesforceWebHookClient = salesforceWebHookIntegrationService.saveSalesforceSettings(salesforceWebHookClientDTO, COMPANY_ID);

		WebHookDTO webHookDTO = new WebHookDTO();
		webHookDTO.setBody("rock that body");
		webHookDTO.setIntegrationEventType(IntegrationEventType.newInstance("workNoteAdd"));
		webHookDTO.setWebHookClientId(salesforceWebHookClient.getId());
		webHookDTO.setEnabled(true);
		webHookDTO.setCallOrder(0);
		webHookDTO.setMethodType(WebHook.MethodType.POST);
		webHookDTO.setContentType(WebHook.ContentType.JSON);

		try {
			webHookDTO.setUrl(new URL("http://www.workmarket.com"));
		} catch (MalformedURLException e) {
			Assert.fail("this should not happen");
		}

		List<WebHookHeaderDTO> webHookHeaderDTOList = new ArrayList<WebHookHeaderDTO>();
		WebHookHeaderDTO webHookHeaderDTO = new WebHookHeaderDTO();
		webHookHeaderDTO.setName("hello");
		webHookHeaderDTO.setValue("world");
		webHookHeaderDTOList.add(webHookHeaderDTO);
		webHookDTO.setHeaders(webHookHeaderDTOList);

		WebHook webHook = webHookIntegrationService.saveWebHook(webHookDTO);
		assertNotNull("Failed to create webhook", webHook);

		return webHook;
	}

	private WebHook createGenericWebhook() {
		GenericWebHookClientDTO genericWebHookClientDTO = new GenericWebHookClientDTO();
		genericWebHookClientDTO.setDateFormat(AbstractWebHookClient.DateFormat.UNIX);

		GenericWebHookClient genericWebHookClient = webHookIntegrationService.saveSettings(genericWebHookClientDTO,
				COMPANY_ID);

		WebHookDTO webHookDTO = new WebHookDTO();
		webHookDTO.setBody("rock that body");
		webHookDTO.setIntegrationEventType(IntegrationEventType.newInstance("workNoteAdd"));
		webHookDTO.setWebHookClientId(genericWebHookClient.getId());
		webHookDTO.setEnabled(true);
		webHookDTO.setCallOrder(0);
		webHookDTO.setMethodType(WebHook.MethodType.POST);
		webHookDTO.setContentType(WebHook.ContentType.JSON);
		webHookDTO.setSuppressApiEvents(true);

		try {
			webHookDTO.setUrl(new URL("http://www.workmarket.com"));
		} catch (MalformedURLException e) {
			Assert.fail("this should not happen");
		}

		List<WebHookHeaderDTO> webHookHeaderDTOList = new ArrayList<WebHookHeaderDTO>();
		WebHookHeaderDTO webHookHeaderDTO = new WebHookHeaderDTO();
		webHookHeaderDTO.setName("hello");
		webHookHeaderDTO.setValue("world");
		webHookHeaderDTOList.add(webHookHeaderDTO);
		webHookDTO.setHeaders(webHookHeaderDTOList);

		WebHook webHook = webHookIntegrationService.saveWebHook(webHookDTO);
		assertNotNull("Failed to create webhook", webHook);
		assertNotNull(webHook.getUuid());

		return webHook;
	}

	@Test
	public void shouldReturnWebhookClientCompanyId() {
		WebHook webHook = createGenericWebhook();

		final Optional<Long> clientCompanyId = webHookIntegrationService.getWebhookClientCompanyId(webHook.getId());

		if (clientCompanyId.isPresent()) {
			assertEquals(COMPANY_ID, clientCompanyId.get());
		} else {
			fail();
		}
	}

	@Test
	public void testErrorHandling_GivenWorkingWebHook_ExpectEnable() {
		WebHook webHook = createGenericWebhook();

		for (int i = 0; i < 2 * WebHook.MAX_CONSECUTIVE_ERRORS; i++) {
			if (i % 3 == 0) {
				webHookIntegrationService.clearErrors(webHook);
			} else {
				webHookIntegrationService.handleError(webHook);
			}
		}

		Assert.assertTrue("Webhook should still be enabled", webHookIntegrationService.getWebHook(webHook.getId()).get()
				.isEnabled());
	}

	@Test
	public void testErrorHandling_GivenWorkingSalesforceWebHook_ExpectEnable() {
		WebHook webHook = createSalesforceWebhook();

		for (int i = 0; i < 2 * WebHook.MAX_CONSECUTIVE_ERRORS; i++) {
			if (i % 3 == 0) {
				webHookIntegrationService.clearErrors(webHook);
			} else {
				webHookIntegrationService.handleError(webHook);
			}
		}

		Assert.assertTrue("Webhook should still be enabled", webHookIntegrationService.getWebHook(webHook.getId()).get()
				.isEnabled());
	}

	@Test
	public void testDisableAllHooks_GivenMultipleHooks_ExpectDisable() {
		Set<WebHook> webHooks = new HashSet<WebHook>();
		WebHook lastHook = null;

		for (int i = 0; i < 4; i++) {
			lastHook = createGenericWebhook();
			webHooks.add(lastHook);
		}

		webHookIntegrationService.disableAllHooks(lastHook.getWebHookClient().getId(), COMPANY_ID);

		for (WebHook webHook : webHooks) {
			Assert.assertFalse("All webhooks should be disabled", webHookIntegrationService.getWebHook(webHook.getId()).get().isEnabled());
		}
	}

	@Test
	public void testHookPermissions_GivenOwnerCompany_ExpectAllowed() {
		WebHook webHook = createGenericWebhook();

		Assert.assertTrue("Company should be allowed to modify their webhooks", webHookIntegrationService.canModifyWebHook
				(webHook.getId(), COMPANY_ID));
		Assert.assertTrue("Company should be allowed to modify their webhook client settings", webHookIntegrationService
				.canModifyWebHookClient(webHook.getWebHookClient().getId(), COMPANY_ID));
		for (WebHookHeader webHookHeader : webHook.getWebHookHeaders()) {
			Assert.assertTrue("Company should be allowed to modify their webhook headers", webHookIntegrationService
					.canModifyWebHookHeader(webHookHeader.getId(), COMPANY_ID));
		}
	}

	@Test
	public void testHookPermissions_GivenInvalidCompany_ExpectDenied() {
		WebHook webHook = createGenericWebhook();

		Assert.assertFalse("Invalid Company should not be allowed to modify another company's webhooks",
				webHookIntegrationService.canModifyWebHook(webHook.getId(), COMPANY_ID + 1));
		Assert.assertFalse("Invalid Company should not be allowed to modify another company's webhook client settings",
				webHookIntegrationService.canModifyWebHookClient(webHook.getWebHookClient().getId(), COMPANY_ID + 1));
		for (WebHookHeader webHookHeader : webHook.getWebHookHeaders()) {
			Assert.assertFalse("Invalid Company should not be allowed to modify another company's webhook headers",
					webHookIntegrationService.canModifyWebHookHeader(webHookHeader.getId(), COMPANY_ID + 1));
		}
	}

	@Test
	public void testWebHookDelete_GivenWebHook_ExpectDelete() {
		WebHook webHook = createGenericWebhook();

		webHookIntegrationService.deleteWebHook(webHook.getId());

		Assert.assertTrue("Webhook should be deleted", webHookIntegrationService.getWebHook(webHook.getId()).get()
				.getDeleted());
	}

	@Test
	public void testWebHookHeaderDelete_GivenWebHook_ExpectDelete() {
		WebHook webHook = createGenericWebhook();

		for (WebHookHeader webHookHeader : webHook.getWebHookHeaders()) {
			webHookIntegrationService.deleteWebHookHeader(webHookHeader.getId());
		}

		webHook = webHookIntegrationService.getWebHook(webHook.getId()).get();

		for (WebHookHeader webHookHeader : webHook.getWebHookHeaders()) {
			Assert.assertTrue("Webhook header should be deleted", webHookHeader.getDeleted());
		}
	}

	@Test
	public void testWebHookCallOrder_GivenWebHook_ExpectCallOrderChange() {
		WebHook webHook = createGenericWebhook();

		Integer newValue = webHook.getCallOrder() + 1;
		webHookIntegrationService.updateWebHookCallOrder(webHook.getId(), newValue);

		Assert.assertTrue("Webhook order should be changed", webHookIntegrationService.getWebHook(webHook.getId()).get().getCallOrder().equals(newValue));
	}

	@Test
	public void testWebHookCall_createGenericWebhook_suppressFromApiFlagTrue() {
		final WebHook webHook = createGenericWebhook();

		Assert.assertTrue(webHook.isSuppressApiEvents());
	}
}
