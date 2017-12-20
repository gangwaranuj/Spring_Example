package com.workmarket.web.validators;

import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.WorkConcern;
import com.workmarket.domains.work.model.Work;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

@Component("workConcernValidator")
public class WorkConcernValidator extends ConcernValidator {
	@Override
	public void validateEntityId(Concern concern, Errors errors) {
		WorkConcern workConcern = (WorkConcern)concern;
		Work work = workConcern.getWork();

		if ((work == null) || !StringUtils.hasText(work.getWorkNumber())) {
			errors.reject("NotEmpty", "Work");
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return (WorkConcern.class == clazz);
	}
}
