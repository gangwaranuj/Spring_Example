package com.workmarket.web.validators;

import org.springframework.validation.Errors;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class FiletypeValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		Map<String, Object> fileDesc = (Map<String, Object>) o;
		String filetype = (String) fileDesc.get("filetype");
		Set<String> validFileTypesForPage = (Set) fileDesc.get("pageSet");

		if (isBlank(filetype)) {
			errors.rejectValue(null, "NotEmpty");
		} else {
			if (!validFileTypesForPage.contains(filetype.toLowerCase())) {
				errors.rejectValue(null, "filetype.invalid_type_for_page", new Object[]{validFileTypesForPage.toString()}, "");
			}
		}

	}
}
