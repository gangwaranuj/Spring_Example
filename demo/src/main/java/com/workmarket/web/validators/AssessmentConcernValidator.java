package com.workmarket.web.validators;

import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.note.concern.AssessmentConcern;
import com.workmarket.domains.model.note.concern.Concern;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

@Component("assessmentConcernValidator")
public class AssessmentConcernValidator extends ConcernValidator {

	@Override
	public void validateEntityId(Concern concern, Errors errors) {
		Assert.notNull(concern);
		Assert.notNull(errors);

		AssessmentConcern assessmentConcern = (AssessmentConcern)concern;
		AbstractAssessment assessment = assessmentConcern.getAssessment();

		if ((assessment == null) || (assessment.getId() == null)) {
			errors.rejectValue("assessment", "NotEmpty");
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return (AssessmentConcern.class == clazz);
	}
}
