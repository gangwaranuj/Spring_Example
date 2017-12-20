package com.workmarket.web.validators;

import com.workmarket.web.forms.user.PasswordSetupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.io.IOException;

@Component("passwordSetupValidator")
public class PasswordSetupValidator {

	private final PasswordValidator passwordValidator;

	@Autowired
	public PasswordSetupValidator(PasswordValidator passwordValidator) throws IOException {
		if (passwordValidator == null) {
			throw new IllegalArgumentException("The supplied [Validator] is required and must not be null.");
		}
		this.passwordValidator = passwordValidator;
	}

	public void validate(final PasswordSetupForm form, final String email, final Errors errors) {
		if (!form.isTermsAgree()) {
			errors.rejectValue("termsAgree", "NotEmpty");
		}

		if (!passwordValidator.validatePasswordMatch(form.getPasswordNew(), form.getPasswordNewConfirm())) {
			errors.rejectValue("passwordNewConfirm", "FieldMatch", new Object[]{"Your password", "your confirmation password"}, "Invalid");
		}

		try {
			errors.pushNestedPath("passwordNew");
			passwordValidator.validate(form.getPasswordNew(), email, errors);
		} finally {
			errors.popNestedPath();
		}
	}
}
