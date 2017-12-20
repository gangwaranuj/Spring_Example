package com.workmarket.web.validators;

import com.workmarket.domains.model.note.concern.RecruitingCampaignConcern;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
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
public class RecruitingCampaignConcernValidatorTest extends BaseValidatorTest {

	@InjectMocks RecruitingCampaignConcernValidator validator;
	@Mock RecruitingCampaignConcern concern;
	@Mock RecruitingCampaign recruitingCampaign;

	@Before
	public void setUp() {
		when(recruitingCampaign.getId()).thenReturn(ANY_LONG);
		when(concern.getCampaign()).thenReturn(recruitingCampaign);
		when(concern.getContent()).thenReturn(ANY_STRING);
	}

	@Test
	public void itShouldNotReturnErrorOnNonEmptyCampaign() {
		assertFalse(validate(concern).hasErrors());
	}

	@Test
	public void itShouldReturnErrorForNullConcern() {
		Errors errors = validate(null);
		assertTrue(hasErrorCode(errors, "NotEmpty"));
		assertTrue(hasErrorMessage(errors, "Campaign Concern"));
	}

	@Test
	public void itShouldReturnErrorForNullRecruitingCampaign() {
		when(concern.getCampaign()).thenReturn(null);
		Errors errors = validate(concern);
		assertTrue(hasErrorCode(errors, "NotEmpty"));
		assertTrue(hasFieldInError(errors, "campaign"));
	}

	@Test
	public void itShouldReturnErrorForRecruitingCampaignWithNoId() {
		when(recruitingCampaign.getId()).thenReturn(null);
		Errors errors = validate(concern);
		assertTrue(hasErrorCode(errors, "NotEmpty"));
		assertTrue(hasFieldInError(errors, "campaign"));
	}

	protected Validator getValidator() {
		return validator;
	}
}