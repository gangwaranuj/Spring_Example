package com.workmarket.web.validators;

import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.utility.EmailUtilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

@Component("userFormControllerValidator")
public class UserFormControllerValidator implements Validator {
	@Autowired private UserService userService;

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(User.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User submittedUser = (User) target;
		User existingUser = userService.findUserByEmail(submittedUser.getEmail());

		if (StringUtils.isEmpty(submittedUser.getEmail())) {
			errors.rejectValue("email", "user.validation.emailRequired");
		} else if (!EmailUtilities.isValidEmailAddress(submittedUser.getEmail())) {
			errors.rejectValue("email", "user.validation.emailInvalid");
		} else {
			if (existingUser != null && !existingUser.getId().equals(submittedUser.getId())) {
				errors.rejectValue("email", "user.validation.emailExists");
			}
		}

		if (submittedUser.getSpendLimit() == null || (submittedUser.getSpendLimit().compareTo(BigDecimal.ZERO) < 0)) {
			errors.rejectValue("spendLimit", "user.validation.spendLimit");
		}

		Profile submittedUserProfile = submittedUser.getProfile();
		String workPhoneNumber = submittedUserProfile.getWorkPhone();
		if (StringUtils.isBlank(workPhoneNumber)) {
			errors.rejectValue("profile.workPhone", "user.validation.workPhoneRequired");
		} else if (!StringUtils.isNumericSpace(workPhoneNumber.replaceAll("[-\\(\\)]", StringUtils.EMPTY))) {
			errors.rejectValue("profile.workPhone", "user.validation.workPhoneInvalid");
		}

		ValidationUtils.rejectIfEmpty(errors, "firstName", "user.validation.firstNameRequired");
		ValidationUtils.rejectIfEmpty(errors, "lastName", "user.validation.lastNameRequired");
	}
}
