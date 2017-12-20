package com.workmarket.web.validators;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.web.forms.groups.manage.GroupAddEditForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GroupEditFormValidator implements Validator {

	@Autowired private GroupAddEditFormUserGroupExistsValidator groupAddEditFormUserGroupExistsValidator;
	@Autowired private UserGroupService groupService;

	@Override
	public boolean supports(Class<?> clazz) {
		return GroupAddEditForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		GroupAddEditForm form = (GroupAddEditForm) target;
		UserGroup existingGroup = groupService.findGroupById(form.getId());
		if (groupNameChanged(form, existingGroup)) {
			groupAddEditFormUserGroupExistsValidator.validate(form, errors);
		}
	}

	private boolean groupNameChanged(GroupAddEditForm form, UserGroup userGroup) {
		return ! StringUtils.equals(form.getName(), userGroup.getName());
	}
}
