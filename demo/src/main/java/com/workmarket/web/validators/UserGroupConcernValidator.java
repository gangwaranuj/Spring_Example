package com.workmarket.web.validators;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.UserGroupConcern;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component("userGroupConcern")
public class UserGroupConcernValidator extends ConcernValidator {
	@Override
	public void validateEntityId(Concern concern, Errors errors) {
		UserGroupConcern userGroupConcern = (UserGroupConcern)concern;
		UserGroup group = userGroupConcern.getGroup();

		if ((group == null) || (group.getId() == null)) {
			errors.reject("NotEmpty", "Group");
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return (UserGroupConcern.class == clazz);
	}
}
