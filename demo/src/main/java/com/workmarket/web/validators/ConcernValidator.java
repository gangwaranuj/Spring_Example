package com.workmarket.web.validators;

import com.workmarket.domains.model.note.concern.Concern;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public abstract class ConcernValidator implements Validator {

	public abstract void validateEntityId(Concern concern, Errors errors);

	@Override
	public void validate(Object target, Errors errors) {
		Concern concern = (Concern)target;
		validateEntityId(concern, errors);
		if (target != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "NotEmpty", "Note");
		}
	}
}
