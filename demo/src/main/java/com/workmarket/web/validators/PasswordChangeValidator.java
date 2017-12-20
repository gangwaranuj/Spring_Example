package com.workmarket.web.validators;

import com.workmarket.web.forms.PasswordChangeForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import java.io.IOException;

@Component("passwordChangeValidator")
public class PasswordChangeValidator {

	private final PasswordValidator	passwordValidator;

	@Autowired
	public PasswordChangeValidator(PasswordValidator passwordValidator) throws IOException {
		if (passwordValidator == null) {
			throw new IllegalArgumentException("The supplied [Validator] is required and must not be null.");
		}
		this.passwordValidator = passwordValidator;
	}

	public void validate(final PasswordChangeForm form, final String email, final Errors errors) {
		if (StringUtils.hasText(form.getCurrentPassword())) {
			if (!passwordValidator.validateCurrentPassword(form.getCurrentPassword())) {
				errors.rejectValue("currentPassword", "mysettings.password.currentPassword.invalid");
			}
		} else {
			errors.rejectValue("currentPassword", "NotEmpty");
		}

		if (!passwordValidator.validatePasswordMatch(form.getNewPassword(), form.getConfirmNewPassword())) {
			errors.rejectValue("confirmNewPassword", "FieldMatch", new Object[] { "The password confirmation", "the new password" }, "Invalid");
		}

		try {
			errors.pushNestedPath("newPassword");
			passwordValidator.validate(form.getNewPassword(), email, errors);
		} finally {
			errors.popNestedPath();
		}
	}
}
