package com.workmarket.web.validators;

import com.workmarket.domains.groups.model.UserGroup;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("companyUserGroupValidator")
public class CompanyUserGroupValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return UserGroup.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserGroup group = (UserGroup)target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty", "Name");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "NotEmpty", "Description");

		if (group.getCompany() == null) {
			errors.rejectValue("company", "NotEmpty", "Company");
		}

		if (group.getOpenMembership() && (group.getIndustry() == null)) {
			errors.rejectValue("industry", "NotEmpty", "Industry");
		}
	}
}
