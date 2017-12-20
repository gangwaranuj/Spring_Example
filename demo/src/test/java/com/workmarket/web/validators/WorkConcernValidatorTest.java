package com.workmarket.web.validators;

import com.workmarket.domains.model.note.concern.RecruitingCampaignConcern;
import com.workmarket.domains.model.note.concern.WorkConcern;
import com.workmarket.domains.work.model.Work;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkConcernValidatorTest extends BaseValidatorTest {

	@InjectMocks WorkConcernValidator validator;
	@Mock WorkConcern workConcern;
	@Mock Work work;

	@Before
	public void setUp() throws Exception {
		when(workConcern.getWork()).thenReturn(work);
		when(workConcern.getContent()).thenReturn(ANY_STRING);
		when(work.getWorkNumber()).thenReturn(ANY_STRING);
	}

	@Test
	public void validate_pass() {
		assertFalse(validate(workConcern).hasErrors());
	}

	@Test(expected = ClassCastException.class)
	public void validate_withWrongConcernClass_throwsException() {
		validate(new RecruitingCampaignConcern());
	}

	@Test
	public void validate_withEmptyNote_returnsError() {
		when(workConcern.getContent()).thenReturn(EMPTY_TOKEN);
		Errors errors = validate(workConcern);
		hasFieldInError(errors, "Note");
		hasErrorCode(errors, "NotEmpty");
	}

	@Test
	public void validate_withWhiteSpaceNote_returnsError() {
		when(workConcern.getContent()).thenReturn(WHITE_SPACE_TOKEN);
		Errors errors = validate(workConcern);
		hasFieldInError(errors, "Note");
		hasErrorCode(errors, "NotEmpty");
	}

	@Test
	public void validate_withNullNote_returnsError() {
		when(workConcern.getContent()).thenReturn(null);
		Errors errors = validate(workConcern);
		hasFieldInError(errors, "Note");
		hasErrorCode(errors, "NotEmpty");
	}

	@Test
	public void validate_withNullWork_returnsError() {
		when(workConcern.getWork()).thenReturn(null);
		Errors errors = validate(workConcern);
		hasFieldInError(errors, "Work");
		hasErrorCode(errors, "NotEmpty");
	}

	@Test
	public void validate_withNullWorkNumber_returnsError() {
		when(work.getWorkNumber()).thenReturn(null);
		Errors errors = validate(workConcern);
		hasFieldInError(errors, "Work");
		hasErrorCode(errors, "NotEmpty");
	}

	@Test
	public void validate_emptyWorkNumber_returnsError() {
		when(work.getWorkNumber()).thenReturn(EMPTY_TOKEN);
		Errors errors = validate(workConcern);
		hasFieldInError(errors, "Work");
		hasErrorCode(errors, "NotEmpty");
	}

	@Test
	public void validate_whiteSpaceWorkNumber_returnsError() {
		when(work.getWorkNumber()).thenReturn(WHITE_SPACE_TOKEN);
		Errors errors = validate(workConcern);
		hasFieldInError(errors, "Work");
		hasErrorCode(errors, "NotEmpty");
	}

	protected Validator getValidator() {
		return validator;
	}

}