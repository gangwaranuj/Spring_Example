package com.workmarket.web.validators;

import com.workmarket.domains.model.comment.UserComment;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserCommentValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return UserComment.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserComment userComment = (UserComment)target;

		if (StringUtils.isEmpty(userComment.getComment())) {
			errors.rejectValue("comment", "not_empty", "comment");
		}

		if (userComment.getUser() == null) {
			errors.rejectValue("user", "not_empty", "user_id");
		}
	}
}
