package com.workmarket.web.validators;

import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.web.forms.feed.FeedRequestParams;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.PostConstruct;

/**
 * Created by alejandrosilva on 3/6/15.
 */
@Component
public class FeedRequestParamsValidator implements Validator {

	public static String VALIDATION_CONSTANTS;
	public static final String POSTAL_CODE = "postalCode";

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private JsonSerializationService jsonService;

	@PostConstruct
	public void init() {
		VALIDATION_CONSTANTS = jsonService.toJson(ImmutableMap.<String, Object>of(
			"postalCodeMax", PostalCode.POSTAL_CODE_MAX,
			"postalCodeMin", PostalCode.POSTAL_CODE_MIN,
			"postalCodeErrors", ImmutableMap.of(
				"max", messageHelper.getMessage("work.feed.postal_code.max"),
				"min", messageHelper.getMessage("work.feed.postal_code.min")
			)
		));
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return FeedRequestParams.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		FeedRequestParams feedRequestParams = (FeedRequestParams) o;

		if (feedRequestParams.getPostalCodeToggle()) {
			String postalCode = feedRequestParams.getPostalCode();
			if (StringUtils.hasText(postalCode)) {
				int postalCodeLength = postalCode.length();
				if (postalCodeLength > PostalCode.POSTAL_CODE_MAX) {
					errors.rejectValue(POSTAL_CODE, "Max", messageHelper.getMessage("work.feed.postal_code.max"));
				} else if (postalCodeLength < PostalCode.POSTAL_CODE_MIN) {
					errors.rejectValue(POSTAL_CODE, "Min", messageHelper.getMessage("work.feed.postal_code.min"));
				} else if (invariantDataService.findOrSavePostalCode(postalCode) == null) {
					errors.rejectValue(POSTAL_CODE, "Invalid", messageHelper.getMessage("work.feed.postal_code.invalid"));
				}
			}
		}
	}
}
