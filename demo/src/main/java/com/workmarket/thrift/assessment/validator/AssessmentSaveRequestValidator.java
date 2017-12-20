package com.workmarket.thrift.assessment.validator;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.AssessmentOptions;
import com.workmarket.thrift.assessment.AssessmentSaveRequest;
import com.workmarket.thrift.assessment.AssessmentType;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.ValidationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssessmentSaveRequestValidator {
	public void validate(AssessmentSaveRequest request) throws ValidationException {
		List<ConstraintViolation> errors = Lists.newLinkedList();
		validateAssessment(request, errors);
		validateConfiguration(request, errors);

		if (errors.size() > 0) {
			throw new ValidationException("Unable to save assessment", errors);
		}
	}

	private void validateAssessment(AssessmentSaveRequest request, List<ConstraintViolation> errors) {
		Assessment requestAssessment = request.getAssessment();
		AssessmentOptions requestConfig = requestAssessment.getConfiguration();

		if (!requestAssessment.isSetName() || StringUtils.isEmpty(requestAssessment.getName())) {
			errors.add(new ConstraintViolation().setProperty("name").setError(MessageKeys.NOT_NULL));
		}

		if (!requestAssessment.isSetIndustry() || !requestAssessment.getIndustry().isSetId()) {
			errors.add(new ConstraintViolation().setProperty("industry").setError(MessageKeys.NOT_NULL));
		}

		if (requestConfig.getPassingScore() < 0 || requestConfig.getPassingScore() > 100) {
			errors.add(new ConstraintViolation().setError(MessageKeys.Assessment.INCORRECT_PASSING_SCORE));
		}

		if (requestAssessment.getApproximateDurationMinutes() < 0) {
			errors.add(new ConstraintViolation().setError(MessageKeys.Assessment.APPROXIMATE_DURATION_MINUTES_INCORRECT_VALUES_RANGE));
		}

		if (requestConfig.getDurationMinutes() < 0) {
			errors.add(new ConstraintViolation().setError(MessageKeys.Assessment.CONFIGURATION_DURATION_MINUTES_INCORRECT_VALUES_RANGE));
		}

		if (requestAssessment.getApproximateDurationMinutes() > requestConfig.getDurationMinutes()) {
			errors.add(new ConstraintViolation().setError(MessageKeys.Assessment.INCORRECT_DURATION_APPROXIMATE_MINUTES_RANGE));
		}

		if (requestConfig.getRetakesAllowed() < 0) {
			errors.add(new ConstraintViolation().setError(MessageKeys.Assessment.INCORRECT_RETAKES_ALLOWED));
		}

		if (requestAssessment.getType().equals(AssessmentType.GRADED)) {
			if (requestConfig.getNotifications() != null && requestConfig.getNotifications().size() > 2 && requestConfig.getNotifications().get(2).getDays() < 0) {
				errors.add(new ConstraintViolation().setError(MessageKeys.Assessment.INCORRECT_EMAIL_NOTIFICATIONS_DAYS));
			}
		}
	}

	private void validateConfiguration(AssessmentSaveRequest request, List<ConstraintViolation> errors) {
		// TODO At least one recipient if notifications...
	}
}
