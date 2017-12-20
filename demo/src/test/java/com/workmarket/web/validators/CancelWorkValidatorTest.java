package com.workmarket.web.validators;

import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.service.business.dto.CancelWorkDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by nick on 3/29/13 5:41 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class CancelWorkValidatorTest extends BaseValidatorTest {

	@InjectMocks CancelWorkValidator validator;
	@Mock CancelWorkDTO dto;

	@Before
	public void setUp() {
		when(dto.getNote()).thenReturn(ANY_STRING);
		when(dto.getCancellationReasonTypeCode()).thenReturn(CancellationReasonType.END_USER_CANCELLED);
		when(dto.getPrice()).thenReturn(ANY_DOUBLE);
	}

	@Test
	public void validate_pass() {
		assertFalse(validate(dto).hasErrors());
	}

	@Test
	public void validate_nullNote_error() {
		when(dto.getNote()).thenReturn(null);
        Errors errors = validate(dto);
		assertTrue(hasErrorCode(errors, "NotEmpty"));
		assertTrue(hasFieldInError(errors, "note"));
	}

	@Test
	public void validate_emptyNote_error() {
		when(dto.getNote()).thenReturn(EMPTY_TOKEN);
		Errors errors = validate(dto);
		assertTrue(hasErrorCode(errors, "NotEmpty"));
		assertTrue(hasFieldInError(errors, "note"));
	}

	@Test
	public void validate_whiteSpaceNote_error() {
		when(dto.getNote()).thenReturn(WHITE_SPACE_TOKEN);
		Errors errors = validate(dto);
		assertTrue(hasErrorCode(errors, "NotEmpty"));
		assertTrue(hasFieldInError(errors, "note"));
	}

	@Test
	public void validate_nullPrice_error() {
		when(dto.getPrice()).thenReturn(null);
		Errors errors = validate(dto);
		assertTrue(hasErrorCode(errors, "price_positive"));
		assertTrue(hasFieldInError(errors, "price"));
	}

	@Test
	public void validate_negativePrice_error() {
		when(dto.getPrice()).thenReturn(NEGATIVE_DOUBLE);
		Errors errors = validate(dto);
		assertTrue(hasErrorCode(errors, "price_positive"));
		assertTrue(hasFieldInError(errors, "price"));
	}

	@Test
	public void validate_invalidReason_error() {
		when(dto.getCancellationReasonTypeCode()).thenReturn(ANY_STRING);
		Errors errors = validate(dto);
		assertTrue(hasErrorCode(errors, "invalid_reason"));
		assertTrue(hasFieldInError(errors, "cancellationReasonTypeCode"));
	}

	protected Validator getValidator() {
		return validator;
	}
}
