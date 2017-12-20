package com.workmarket.web.validators;

import com.workmarket.domains.model.ManageMyWorkMarket;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("mmwValidator")
public class MmwValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return ManageMyWorkMarket.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Assert.notNull(target);
		Assert.notNull(errors);

		ManageMyWorkMarket mmw = (ManageMyWorkMarket) target;

		if (mmw.getAutocloseEnabledFlag() && !errors.hasFieldErrors("autocloseDelayInHours")) {
			if ((mmw.getAutocloseDelayInHours() == null) || (mmw.getAutocloseDelayInHours() < 1)) {
				errors.rejectValue("autocloseDelayInHours", "mmw.manage.autocloseDelayInHours.required");
			}
		}
	}
}
