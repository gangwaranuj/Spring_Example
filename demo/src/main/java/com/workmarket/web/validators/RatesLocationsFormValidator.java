package com.workmarket.web.validators;

import com.workmarket.web.forms.RatesLocationsForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("ratesLocationsFormValidator")
public class RatesLocationsFormValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return RatesLocationsForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RatesLocationsForm form = (RatesLocationsForm)target;

		if (form.getSelectedCurrentLocationTypes().isEmpty()) {
			errors.reject("profile.rates_locations.required");
		}
	}
}
