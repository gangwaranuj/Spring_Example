package com.workmarket.web.validators;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.workmarket.integration.webhook.WebHookDispatchField;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.integration.WebHookDTO;
import com.workmarket.service.business.dto.integration.WebHookHeaderDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookIntegrationService;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

@Component("webHookValidator")
public class WebHookValidator implements Validator {
	@Autowired WorkService workService;
	@Autowired WebHookIntegrationService webHookIntegrationService;
	@Autowired private CustomFieldService customFieldService;

	@Override
	public boolean supports(Class<?> clazz) {
		return WebHookDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		WebHookDTO webHookDTO = (WebHookDTO) o;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "url", "NotNull", CollectionUtilities.newArray("URL"));

		for (WebHookHeaderDTO webHookHeaderDTO : webHookDTO.getHeaders()) {
			if (StringUtils.isBlank(webHookHeaderDTO.getValue())) {
				errors.rejectValue("headers", "mmw.integration.webhook.empty_header");
				break;
			}
		}

		List<String> eventVariables = getEventVariables(webHookDTO.getIntegrationEventTypeCode());

		if (eventVariables == null) {
			eventVariables = Collections.emptyList();
		}

		String body = webHookDTO.getBody();
		Matcher matcher = getVariablePatternMatcher(body);

		Optional<AbstractWebHookClient> abstractWebHookClient = webHookIntegrationService.findWebHookClientById(webHookDTO.getWebHookClientId());

		if (!abstractWebHookClient.isPresent()) {
			errors.reject("mmw.integration.webhook.invalid_client");
		}

		List<String> customFields = Lists.newArrayList();
		for (WorkCustomFieldGroup workCustomFieldGroup : customFieldService.findWorkCustomFieldGroups(abstractWebHookClient.get().getCompany().getId())) {
			for (WorkCustomField workCustomField : customFieldService.findAllFieldsForCustomFieldGroup(workCustomFieldGroup.getId())) {
				customFields.add(WebHook.CUSTOM_FIELD_PREFIX + workCustomField.getId());
			}
		}

		while (matcher.find()) {
			String variable = body.substring(matcher.start() + 2, matcher.end() - 1);

			if (getWebHookGeneralFields().contains(variable) || eventVariables.contains(variable) || customFields.contains(variable)) {
				continue;
			}

			errors.reject("mmw.integration.webhook.invalid_variable", CollectionUtilities.newArray(variable), "");
		}
	}

	private List<String> getEventVariables(String key) {
		return WebHookDispatchField.WEB_HOOK_EVENT_FIELDS.get(key);
	}

	private Matcher getVariablePatternMatcher(CharSequence charSequence) {
		return WebHook.VARIABLE_PATTERN.matcher(charSequence);
	}

	private List<String> getWebHookGeneralFields() {
		return WebHookDispatchField.WEB_HOOK_GENERAL_FIELDS;
	}

}
