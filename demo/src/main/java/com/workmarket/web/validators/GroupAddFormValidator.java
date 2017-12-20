package com.workmarket.web.validators;

import com.workmarket.web.forms.groups.manage.GroupAddEditForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GroupAddFormValidator implements Validator {

	@Autowired private GroupAddEditFormUserGroupExistsValidator groupAddEditFormUserGroupExistsValidator;

	@Override
	public boolean supports(Class<?> clazz) {
		return GroupAddEditForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		groupAddEditFormUserGroupExistsValidator.validate(target, errors);
	}
}
