package com.workmarket.web.validators;

import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.note.concern.AssessmentConcern;
import com.workmarket.domains.model.note.concern.RecruitingCampaignConcern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentConcernValidatorTest extends BaseValidatorTest {

	@InjectMocks AssessmentConcernValidator validator;
	@Mock AbstractAssessment assessment;
	@Mock AssessmentConcern assessmentConcern;

	@Before
	public void setUp() throws Exception {
		when(assessmentConcern.getAssessment()).thenReturn(assessment);
		when(assessmentConcern.getContent()).thenReturn(ANY_STRING);
		when(assessment.getId()).thenReturn(1L);
	}

	@Test
	public void validate_passesValidation() {
		assertFalse(validate(assessmentConcern).hasErrors());
	}

	@Test(expected = IllegalArgumentException.class)
	public void validate_withNullArguments_throwsException() {
		validate(null);
	}

	@Test(expected = ClassCastException.class)
	public void validate_withWrongConcernClass_throwsException() {
		validate(new RecruitingCampaignConcern());
	}

	@Test
	public void validate_withEmptyNote_returnsError() {
		when(assessmentConcern.getContent()).thenReturn(EMPTY_TOKEN);
		Errors errors = validate(assessmentConcern);
		assertTrue(hasFieldInError(errors, "content"));
		assertTrue(hasErrorCode(errors, "NotEmpty"));
	}

	@Test
	public void validate_withWhiteSpaceNote_returnsError() {
		when(assessmentConcern.getContent()).thenReturn(WHITE_SPACE_TOKEN);
		Errors errors = validate(assessmentConcern);
		assertTrue(hasFieldInError(errors, "content"));
		assertTrue(hasErrorCode(errors, "NotEmpty"));
	}

	@Test
	public void validate_withNullNote_returnsError() {
		when(assessmentConcern.getContent()).thenReturn(null);
		Errors errors = validate(assessmentConcern);
		assertTrue(hasFieldInError(errors, "content"));
		assertTrue(hasErrorCode(errors, "NotEmpty"));
	}

	@Test
	public void validate_withNullAssessment_returnsError() {
		when(assessmentConcern.getAssessment()).thenReturn(null);
		Errors errors = validate(assessmentConcern);
		assertTrue(hasFieldInError(errors, "assessment"));
		assertTrue(hasErrorCode(errors, "NotEmpty"));
	}

	@Test
	public void validate_withNullAssessmentId_returnsError() {
		when(assessment.getId()).thenReturn(null);
		Errors errors = validate(assessmentConcern);
		assertTrue(hasFieldInError(errors, "assessment"));
		assertTrue(hasErrorCode(errors, "NotEmpty"));
	}

	protected Validator getValidator() {
		return validator;
	}

}