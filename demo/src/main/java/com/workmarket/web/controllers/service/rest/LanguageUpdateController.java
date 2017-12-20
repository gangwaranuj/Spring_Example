
package com.workmarket.web.controllers.service.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.workmarket.service.business.AddressService;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.locale.LocaleService;
import com.workmarket.web.controllers.BaseController;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/service/language")
public class LanguageUpdateController extends BaseController {

	@Autowired private AddressService addressService;
	@Autowired private LocaleService localeService;
	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private MessageBundleHelper messageHelper;

	@RequestMapping(value="/update", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	final AjaxResponseBuilder update(@RequestBody final String body) throws Exception {

		final AjaxResponseBuilder result = new AjaxResponseBuilder();
		final MessageBundle bundle = messageHelper.newBundle();

		final boolean hasLocaleFeature = featureEntitlementService.hasFeatureToggle(getCurrentUser().getId(), "locale");

		if (!hasLocaleFeature) {
			result.setSuccessful(false);
			bundle.addError("Locale service not enabled");
			result.setMessages(bundle.getAllMessages());
			return result;
		}

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode node = mapper.readTree(body);
		final boolean hasLocale = node.hasNonNull("locale");

		if (!hasLocale) {
			bundle.addError("No locale specified in the request");
			result.setMessages(bundle.getAllMessages());
			result.setSuccessful(false);
		} else {
			final String locale = node.get("locale").asText();
			final String validLocal = localeService.getValidLocaleCode(locale);

			localeService.setPreferredLocale(getCurrentUser().getUuid(), validLocal);

			bundle.addSuccess("Successfully updated user preferred locale to " + validLocal);
			result.setMessages(bundle.getAllMessages());
			result.setSuccessful(true);
		}

		return result;
	}
}
