package com.workmarket.web.validators;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.startsWith;

/**
 * Created by nick on 7/28/13 12:08 PM
 */
@Component
public class RedirectValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		String redirectUrl = (String) o;
		if (isBlank(redirectUrl)) {
			errors.rejectValue(null, "NotEmpty");
			return;
		}
		if (!startsWith(redirectUrl, "/")) {
			errors.rejectValue(null, "redirect.invalid");
			return;
		}

		try {
			URI uri = new URI(redirectUrl);
			if (uri.isAbsolute()) {
				errors.rejectValue(null, "redirect.invalid");
			}
		} catch (URISyntaxException e) {
			errors.rejectValue(null, "redirect.invalid");
		}
	}

	public String validateWithDefault(String url, String defaultUrl) {
		MapBindingResult bind = new MapBindingResult(Maps.newHashMap(), "redirect");
		validate(url, bind);
		return (bind.hasErrors()) ? defaultUrl : url;
	}
}
