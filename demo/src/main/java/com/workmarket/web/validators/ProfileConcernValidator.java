package com.workmarket.web.validators;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.ProfileConcern;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

@Component("profileConcernValidator")
public class ProfileConcernValidator extends ConcernValidator {
	@Override
	public void validateEntityId(Concern concern, Errors errors) {
		ProfileConcern profileConcern = (ProfileConcern)concern;
		User user = profileConcern.getUser();

		if ((user == null) || !StringUtils.hasText(user.getUserNumber())) {
			errors.reject("NotEmpty", "Profile");
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return (ProfileConcern.class == clazz);
	}
}
