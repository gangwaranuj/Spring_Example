package com.workmarket.web.validators;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.workmarket.web.forms.user.PasswordResetForm;

@Component("passwordResetValidator")
public class PasswordResetValidator {

	private final PasswordValidator	passwordValidator;

	@Autowired
	public PasswordResetValidator(PasswordValidator passwordValidator) throws IOException {
		if (passwordValidator == null) {
			throw new IllegalArgumentException("The supplied [Validator] is required and must not be null.");
		}
		this.passwordValidator = passwordValidator;
	}

	public void validate(final PasswordResetForm form, final String email, final Errors errors) {
		if (!passwordValidator.validatePasswordMatch(form.getPasswordNew(), form.getPasswordNewConfirm())) {
			errors.rejectValue("passwordNewConfirm", "FieldMatch", new Object[] { "Your password", "your confirmation password" }, "Invalid");
		}

		try {
			errors.pushNestedPath("passwordNew");
			passwordValidator.validate(form.getPasswordNew(), email, errors);
		} finally {
			errors.popNestedPath();
		}
	}
}
