package com.workmarket.web.validators;

import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.groups.model.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserGroupEditPermissionValidator implements Validator {

	@Autowired private SecurityContextFacade securityContextFacade;

	@Override
	public boolean supports(Class<?> clazz) {
		return UserGroup.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserGroup group = (UserGroup) target;
		if ( ! canEditGroup(group)) {
			errors.reject("groups.manage.exception");
		}
	}

	private boolean canEditGroup(UserGroup group) {
		return group != null && group.getCompany() != null && securityContextFacade.getCurrentUser().getCompanyId().equals(group.getCompany().getId());
	}
}
