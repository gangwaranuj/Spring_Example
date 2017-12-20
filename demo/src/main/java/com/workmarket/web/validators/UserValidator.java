package com.workmarket.web.validators;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("userValidator")
public class UserValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User user = (User)target;

		if (user.getId() == null) {
			errors.rejectValue("id", "");
		}

		//cannot use ValidationUtils because User is not bound to errors in controller
		if (StringUtils.isEmpty(user.getFirstName())) {
			errors.reject("user.validation.firstNameRequired");
		} else if (StringUtils.length(user.getFirstName()) > Constants.FIRST_NAME_MAX_LENGTH) {
			errors.reject("user.validation.firstNameMaxLength");
		}

		if (StringUtils.isEmpty(user.getLastName())) {
			errors.reject("user.validation.lastNameRequired");
		} else if (StringUtils.length(user.getLastName()) > Constants.LAST_NAME_MAX_LENGTH) {
			errors.reject("user.validation.lastNameMaxLength");
		}

		if (StringUtils.isEmpty(user.getEmail())) {
			errors.reject("user.validation.emailRequired");
		} else if (StringUtils.length(user.getEmail()) > Constants.EMAIL_MAX_LENGTH) {
			errors.reject("user.validation.emailMaxLength");
		}
	}
}
