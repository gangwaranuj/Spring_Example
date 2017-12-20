package com.workmarket.web.controllers.mmw;

import com.google.common.base.Optional;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.model.integration.webhook.GenericWebHookClient;
import com.workmarket.domains.model.integration.webhook.SalesforceWebHookClient;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.integration.webhook.WebHookDispatchField;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.dto.integration.GenericWebHookClientDTO;
import com.workmarket.service.business.dto.integration.SalesforceRefreshTokenDTO;
import com.workmarket.service.business.dto.integration.SalesforceWebHookClientDTO;
import com.workmarket.service.business.dto.integration.WebHookDTO;
import com.workmarket.service.business.integration.hooks.autotask.AutotaskIntegrationService;
import com.workmarket.service.business.integration.hooks.webhook.SalesforceWebHookIntegrationService;
import com.workmarket.service.business.integration.hooks.webhook.WebHookIntegrationService;
import com.workmarket.service.business.queue.integration.IntegrationEventService;
import com.workmarket.service.exception.integration.AutotaskAuthenticationException;
import com.workmarket.service.exception.integration.AutotaskCustomFieldsException;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.forms.mmw.AutotaskUserForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.WebHookValidator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/mmw/integration")
public class MmwIntegrationController extends BaseController {

	private static final Log logger = LogFactory.getLog(MmwIntegrationController.class);
	private static final String PASSWORD_NOT_CHANGED = "******";

	@Autowired AutotaskIntegrationService autotaskIntegrationService;
	@Autowired WebHookIntegrationService webHookIntegrationService;
	@Autowired SalesforceWebHookIntegrationService salesforceWebHookIntegrationService;
	@Autowired RegistrationService registrationService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired IntegrationEventService integrationEventService;
	@Autowired WorkService workService;
	@Autowired private CustomFieldService customFieldService;
	@Autowired WebHookValidator webHookValidator;
	@Autowired FeatureEntitlementService featureEntitlementService;

	@RequestMapping(method = RequestMethod.GET)
	public String showIntegrations() {
		return "web/pages/mmw/integration/index";
	}

	@VelvetRope(
		venue = Venue.SALESFORCE_WEBHOOKS,
		redirectPath = "/settings",
		message = "You do not have access to this feature.")
	@RequestMapping(
		value = "/salesforce",
		method = GET)
	public String salesforce(Model model) {

		Optional<SalesforceWebHookClient> salesforceWebHookClientOptional = salesforceWebHookIntegrationService.findSalesforceSettings(getCurrentUser().getCompanyId());
		SalesforceWebHookClientDTO salesforceWebHookClientDTO = new SalesforceWebHookClientDTO();
		SalesforceWebHookClient salesforceWebHookClient;
		boolean isSalesforceAuthenticated = false;

		if (salesforceWebHookClientOptional.isPresent()) {
			salesforceWebHookClient = salesforceWebHookClientOptional.get();
			salesforceWebHookClientDTO = SalesforceWebHookClientDTO.newDTO(salesforceWebHookClient);

			model.addAttribute("webhookSettingsId", salesforceWebHookClient.getId());
			isSalesforceAuthenticated = salesforceWebHookClient.getRefreshToken() != null;
		}

		model.addAttribute("isSalesforceAuthenticated", isSalesforceAuthenticated);
		model.addAttribute("salesforceIntegrationSettingsDTO", salesforceWebHookClientDTO);
		model.addAttribute("salesforceClientId", salesforceWebHookIntegrationService.getSalesforceConsumerKey());

		try {
			model.addAttribute("salesforceCallbackUrl", URLEncoder.encode(salesforceWebHookIntegrationService.getSalesforceCallbackUrl(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Could not URL encode " + salesforceWebHookIntegrationService.getSalesforceCallbackUrl());
		}

		model.addAttribute("dateFormats", AbstractWebHookClient.DateFormat.values());

		if (isSalesforceAuthenticated) {
			// populate dropdowns
			model.addAttribute("events", integrationEventService.findIntegrationEventTypes());
			model.addAttribute("methods", WebHook.MethodType.values());
			model.addAttribute("contentTypes", WebHook.ContentType.values());

			// populate variable list
			model.addAttribute("eventFieldsMap", WebHookDispatchField.WEB_HOOK_EVENT_FIELDS);
			model.addAttribute("generalFieldsMap", WebHookDispatchField.WEB_HOOK_GENERAL_FIELDS);

			Map<String, Map<String, String>> customFieldsGroups = new HashMap<String, Map<String, String>>();

			for (WorkCustomFieldGroup workCustomFieldGroup : customFieldService.findWorkCustomFieldGroups(getCurrentUser().getCompanyId())) {
				Map<String, String> customFields = new HashMap<String, String>();

				for (WorkCustomField workCustomField : customFieldService.findAllFieldsForCustomFieldGroup(workCustomFieldGroup.getId())) {
					customFields.put(WebHook.CUSTOM_FIELD_PREFIX + workCustomField.getId(), workCustomField.getName());
				}

				customFieldsGroups.put(workCustomFieldGroup.getName(), customFields);
			}

			model.addAttribute("customFieldsGroups", customFieldsGroups);
		}

		return "web/pages/mmw/integration/salesforce/index";
	}

	@VelvetRope(
		venue = Venue.SALESFORCE_WEBHOOKS,
		redirectPath = "/settings",
		message = "You do not have access to this feature.")
	@RequestMapping(
		value = "/salesforce_callback",
		method = GET)
	public String salesforceCallback(
		@RequestParam(required = false) String code,
		@RequestParam(required = false) String state,
		@RequestParam(required = false) String error,
		@RequestParam(value = "error_description", required = false) String errorDescription,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (code == null) {
			messageHelper.addError(bundle, "mmw.integration.salesforce.callback.error");
			logger.debug("User id " + getCurrentUser().getId() + " could not get code from Salesforce [error=" + error
					+ ", error_description=" + errorDescription + "]");
			return "redirect:/mmw/integration/salesforce";
		}

		Boolean isSandbox = "sandbox".equals(state);

		Optional<SalesforceRefreshTokenDTO> salesforceRefreshTokenDTOOptional = salesforceWebHookIntegrationService.getSalesforceRefreshToken(code, isSandbox);

		if (!salesforceRefreshTokenDTOOptional.isPresent()) {
			messageHelper.addError(bundle, "mmw.integration.salesforce.callback.error");
			logger.debug("User id " + getCurrentUser().getId() + " request for refresh token failed");
			return "redirect:/mmw/integration/salesforce";
		}

		SalesforceRefreshTokenDTO salesforceRefreshTokenDTO = salesforceRefreshTokenDTOOptional.get();

		if (salesforceRefreshTokenDTO.getAccess_token() == null) {
			messageHelper.addError(bundle, "mmw.integration.salesforce.callback.error");
			logger.debug("User id " + getCurrentUser().getId() + " could not get refresh token from Salesforce [error="
					+ error + ", error_description=" + errorDescription + "]");
			return "redirect:/mmw/integration/salesforce";
		}

		salesforceWebHookIntegrationService.saveSalesforceRefreshToken(salesforceRefreshTokenDTO.getRefresh_token(),
				getCurrentUser().getCompanyId(), isSandbox);

		return "redirect:/mmw/integration/salesforce";
	}

	@RequestMapping(
		value = "/salesforce_forget_authentication/{webHookClientId}",
		method = GET)
	public String salesforceForgetAuthentication(@PathVariable("webHookClientId") Long webHookClientId) {
		if (!hasFeature("salesforce")) {
			throw new HttpException401();
		}

		if (!webHookIntegrationService.canModifyWebHookClient(webHookClientId, getCurrentUser().getCompanyId())) {
			throw new HttpException401();
		}

		// null refresh token
		salesforceWebHookIntegrationService.saveSalesforceRefreshToken(null, getCurrentUser().getCompanyId(), false);

		// disable all hooks
		webHookIntegrationService.disableAllHooks(webHookClientId, getCurrentUser().getCompanyId());

		return "redirect:/mmw/integration/salesforce";
	}


	@VelvetRope(
		venue = Venue.SALESFORCE_WEBHOOKS,
		redirectPath = "/settings",
		message = "You do not have access to this feature.")
	@RequestMapping(
		value = "/salesforce",
		method = POST)
	public String saveSalesforceIntegration(
		@Valid @ModelAttribute("salesforceIntegrationSettingsDTO") SalesforceWebHookClientDTO salesforceWebHookClientDTO,
		BindingResult bind,
		RedirectAttributes flash) {

		if (!hasFeature("salesforce")) {
			throw new HttpException401();
		}

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mmw/integration/salesforce";
		}

		salesforceWebHookIntegrationService.saveSalesforceSettings(salesforceWebHookClientDTO, getCurrentUser()
				.getCompanyId());
		messageHelper.addSuccess(bundle, "mmw.integration.webhook.settings_saved");

		return "redirect:/mmw/integration/salesforce";
	}

	@RequestMapping(
		value = "/save_web_hook",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder saveWebHook(@RequestBody WebHookDTO webHookDTO) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		MessageBundle bundle = messageHelper.newBundle();
		BindingResult bind = new BeanPropertyBindingResult(webHookDTO, "webHook");
		webHookValidator.validate(webHookDTO, bind);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return response.setSuccessful(false).setMessages(bundle.getErrors());
		}

		if (webHookDTO.getId() != null) {
			// can they update this webhook?
			if (!webHookIntegrationService.canModifyWebHook(webHookDTO.getId(), getCurrentUser().getCompanyId())) {
				throw new HttpException401();
			}
		} else {
			// can they insert a webhook for these settings?
			if (!webHookIntegrationService.canModifyWebHookClient(webHookDTO.getWebHookClientId(), getCurrentUser().getCompanyId())) {
				throw new HttpException401();
			}
		}

		WebHook webHook = webHookIntegrationService.saveWebHook(webHookDTO);
		response.addData("web_hook", WebHookDTO.newDTO(webHook));

		messageHelper.addMessage(response, "mmw.integration.webhook.saved");
		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/load_web_hooks/{webHookClientId}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody List<WebHookDTO> loadWebHooks(@PathVariable("webHookClientId") Long webHookClientId) {
		if (!webHookIntegrationService.canModifyWebHookClient(webHookClientId, getCurrentUser().getCompanyId()))
			throw new HttpException401();

		List<WebHook> webHooks = webHookIntegrationService.findWebHooksForCompanyAndClient(getCurrentUser().getCompanyId(), webHookClientId);
		List<WebHookDTO> webHookDTOs = new ArrayList<WebHookDTO>();

		for (WebHook webHook : webHooks) {
			webHookDTOs.add(WebHookDTO.newDTO(webHook));
		}

		return webHookDTOs;
	}

	@RequestMapping(
		value = "/delete_web_hook/{webHookId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteWebHook(@PathVariable("webHookId") Long webHookId) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (!webHookIntegrationService.canModifyWebHook(webHookId, getCurrentUser().getCompanyId())) {
			throw new HttpException401();
		}

		webHookIntegrationService.deleteWebHook(webHookId);

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/enable_web_hook/{webHookId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder enableWebHook(@PathVariable("webHookId") Long webHookId) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (!webHookIntegrationService.canModifyWebHook(webHookId, getCurrentUser().getCompanyId())) {
			throw new HttpException401();
		}

		webHookIntegrationService.enable(webHookId);

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/disable_web_hook/{webHookId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder disableWebHook(@PathVariable("webHookId") Long webHookId) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (!webHookIntegrationService.canModifyWebHook(webHookId, getCurrentUser().getCompanyId())) {
			throw new HttpException401();
		}

		webHookIntegrationService.disable(webHookId);

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/delete_web_hook_header/{webHookHeaderId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteWebHookHeader(@PathVariable("webHookHeaderId") Long webHookHeaderId) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (!webHookIntegrationService.canModifyWebHookHeader(webHookHeaderId, getCurrentUser().getCompanyId())) {
			throw new HttpException401();
		}

		webHookIntegrationService.deleteWebHookHeader(webHookHeaderId);

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/update_web_hook_call_order/{webHookHeaderId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder updateWebHookOrder(@PathVariable("webHookHeaderId") Long webHookHeaderId, @RequestParam("callOrder") Integer callOrder) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (!webHookIntegrationService.canModifyWebHook(webHookHeaderId, getCurrentUser().getCompanyId())) {
			throw new HttpException401();
		}

		webHookIntegrationService.updateWebHookCallOrder(webHookHeaderId, callOrder);

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/autotask",
		method = GET)
	public String showAutotaskSettings(Model model) {


		model.addAttribute("autotaskUserForm", autotaskIntegrationService.populateAutotaskUserForm(getCurrentUser().getCompanyId(), getCurrentUser().getId()));

		return "web/pages/mmw/integration/autotask/index";
	}

	@RequestMapping(
		value = "/autotask",
		method = POST)
	public String saveAutotaskSettings(
		@ModelAttribute("autotaskUserForm") AutotaskUserForm form,
		BindingResult bind,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			form.getAutotaskUser().setPassword("");
			return "redirect:/mmw/integration/autotask";
		}

		Optional<User> apiUserOpt = registrationService.getApiUserByUserId(getCurrentUser().getId());
		if (!apiUserOpt.isPresent()) {
			return "redirect:/mmw/integration/autotask";
		}

		Long userId = apiUserOpt.get().getId();
		form.getAutotaskUser().setUserId(userId);

		Optional<AutotaskUser> existingUserOpt = autotaskIntegrationService.findAutotaskUserByUserId(userId);

		if (!existingUserOpt.isPresent() || !form.getAutotaskUser().hasZoneUrl()) {
			// for new users, get Zone URL
			Optional<String> zoneUrlOpt = autotaskIntegrationService.findZoneUrl(form.getAutotaskUser().getUserName());
			if (zoneUrlOpt.isPresent()) {
				form.getAutotaskUser().setZoneUrl(zoneUrlOpt.get());
			} else {
				messageHelper.addError(bundle, "mmw.integration.autotask.zone_url_not_found");
				form.getAutotaskUser().setPassword("");
				return "redirect:/mmw/integration/autotask";
			}
		}

		if (form.getAutotaskUser().isUpdatePassword()) {
			String password = form.getAutotaskUser().getPassword();
			if(StringUtils.isBlank(password) || password.equals(PASSWORD_NOT_CHANGED)) {
				messageHelper.addError(bundle, "mmw.integration.autotask.password_required");
				form.getAutotaskUser().setPassword("");
				return "redirect:/mmw/integration/autotask";
			}
			// validate credentials
			if (!autotaskIntegrationService.validateCredentials(form.getAutotaskUser().getUserName(), form.getAutotaskUser().getPassword(), form.getAutotaskUser().getZoneUrl())) {
				messageHelper.addError(bundle, "mmw.integration.autotask.invalid_credentials");
				form.getAutotaskUser().setPassword("");
				return "redirect:/mmw/integration/autotask";
			}
		}

		try {
			autotaskIntegrationService.saveOrUpdateAutotaskUser(form.getAutotaskUser(), form.isNotesEnabled(), form.isNotesInternal(), form.isAttachmentsInternal(), form.getPreferenceMap());
			messageHelper.addSuccess(bundle, "mmw.integration.autotask.success");
		} catch (AutotaskAuthenticationException e) {
			messageHelper.addError(bundle, "mmw.integration.autotask.invalid_credentials");
			logger.warn(String.format("Invalid AutoTask auth attempt by user %s, API user %d, userName %s",
				getCurrentUser().getId(), userId, form.getAutotaskUser().getUserName()));
		} catch (AutotaskCustomFieldsException ex) {
			messageHelper.addError(bundle, "mmw.integration.autotask.empty_fields");
			logger.warn(String.format("AutoTask custom fields empty attempt by user %s, API user %d, userName %s",
				getCurrentUser().getId(), userId, form.getAutotaskUser().getUserName()));
		}

		return "redirect:/mmw/integration/autotask";
	}

	@VelvetRope(
		venue = Venue.WEBHOOKS,
		redirectPath = "/settings",
		message = "You do not have access to this feature.")
	@RequestMapping(
		value = "/webhooks",
		method = GET)
	public String webhooks(Model model) {

		Optional<GenericWebHookClient> genericWebHookClientOptional = webHookIntegrationService.findGenericWebHookClientByCompanyId(getCurrentUser().getCompanyId());

		GenericWebHookClientDTO genericWebHookClientDTO = new GenericWebHookClientDTO();
		GenericWebHookClient genericWebHookClient;

		if (genericWebHookClientOptional.isPresent()) {
			genericWebHookClient = genericWebHookClientOptional.get();
			genericWebHookClientDTO = GenericWebHookClientDTO.newDTO(genericWebHookClient);

			model.addAttribute("webhookSettingsId", genericWebHookClient.getId());
		}
		model.addAttribute("webHookIntegrationSettingsDTO", genericWebHookClientDTO);

		model.addAttribute("dateFormats", AbstractWebHookClient.DateFormat.values());

		// populate dropdowns
		model.addAttribute("events", integrationEventService.findIntegrationEventTypes());
		model.addAttribute("methods", WebHook.MethodType.values());
		model.addAttribute("contentTypes", WebHook.ContentType.values());

		// populate variable list
		model.addAttribute("eventFieldsMap", WebHookDispatchField.WEB_HOOK_EVENT_FIELDS);
		model.addAttribute("generalFieldsMap", WebHookDispatchField.WEB_HOOK_GENERAL_FIELDS);

		Map<String, Map<String, String>> customFieldsGroups = new HashMap<String, Map<String, String>>();

		for (WorkCustomFieldGroup workCustomFieldGroup : customFieldService.findWorkCustomFieldGroups(getCurrentUser().getCompanyId())) {
			Map<String, String> customFields = new HashMap<String, String>();

			for (WorkCustomField workCustomField : customFieldService.findAllFieldsForCustomFieldGroup(workCustomFieldGroup.getId())) {
				customFields.put(WebHook.CUSTOM_FIELD_PREFIX + workCustomField.getId(), workCustomField.getName());
			}

			customFieldsGroups.put(workCustomFieldGroup.getName(), customFields);
		}

		model.addAttribute("customFieldsGroups", customFieldsGroups);
		model.addAttribute("suppressApiEventsFeatureFlag",
				featureEntitlementService.hasFeatureToggle(getCurrentUser().getId(), "webhook.suppressapievents"));

		return "web/pages/mmw/integration/webhooks/index";
	}

	@VelvetRope(
		venue = Venue.WEBHOOKS,
		redirectPath = "/settings",
		message = "You do not have access to this feature.")
	@RequestMapping(
		value = "/webhooks",
		method = POST)
	public String saveGenericIntegration(
		@Valid @ModelAttribute("genericIntegrationSettingsDTO") GenericWebHookClientDTO genericWebHookClientDTO,
		BindingResult bind,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mmw/integration/webhooks";
		}

		webHookIntegrationService.saveSettings(genericWebHookClientDTO, getCurrentUser().getCompanyId());
		messageHelper.addSuccess(bundle, "mmw.integration.webhook.settings_saved");

		return "redirect:/mmw/integration/webhooks";
	}
}
