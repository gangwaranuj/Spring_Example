package com.workmarket.web.validators;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.feedback.FeedbackConcern;
import com.workmarket.service.business.dto.FeedbackDTO;
import com.workmarket.web.forms.work.WorkAssetForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.Validator;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class FeedbackValidatorTest extends BaseValidatorTest {

	private FeedbackDTO feedback;
	private WorkAssetForm workAssetForm;
	private FeedbackConcern concern;

	FeedbackValidator validator = new FeedbackValidator();

	@Before
	public void setUp() {
		concern = mock(FeedbackConcern.class);
		when(concern.getCode()).thenReturn(ANY_STRING);
		when(concern.getDescription()).thenReturn(ANY_STRING);

		feedback = mock(FeedbackDTO.class);
		when(feedback.getDescription()).thenReturn(ANY_STRING);
		when(feedback.getTitle()).thenReturn(ANY_STRING);
		when(feedback.getConcern()).thenReturn(concern);
		when(feedback.getType()).thenReturn(ANY_STRING);

		workAssetForm = mock(WorkAssetForm.class);
	}

	@Test
	public void validate_feedback_pass() {
		assertFalse(validate(feedback).hasErrors());
	}

	@Test
	public void validate_nullTitle_fail() {
		when(feedback.getTitle()).thenReturn(null);
		assertTrue(hasErrorCode(validate(feedback), "feedback.title.blank"));
	}

	@Test
	public void validate_emptyTitle_fail() {
		when(feedback.getTitle()).thenReturn(EMPTY_TOKEN);
		assertTrue(hasErrorCode(validate(feedback), "feedback.title.blank"));
	}

	@Test
	public void validate_whiteSpaceTitle_fail() {
		when(feedback.getTitle()).thenReturn(WHITE_SPACE_TOKEN);
		assertTrue(hasErrorCode(validate(feedback), "feedback.title.blank"));
	}

	@Test
	public void validate_lengthTitle_fail() {
		when(feedback.getTitle()).thenReturn(generateMaxExceededString(FeedbackValidator.TITLE_MAX_LENGTH));
		assertTrue(hasErrorCode(validate(feedback), "feedback.title.length"));
	}

	@Test
	public void validate_lengthTitle_pass() {
		when(feedback.getTitle()).thenReturn(generateMaxExceededString(FeedbackValidator.TITLE_MAX_LENGTH - 1));
		assertFalse(validate(feedback).hasErrors());
	}

	@Test
	public void validate_lengthDescription_fail() {
		when(feedback.getDescription()).thenReturn(generateMaxExceededString(Constants.TEXT_MAX_LENGTH));
		assertTrue(hasErrorCode(validate(feedback), "feedback.description.length"));
	}

	@Test
	public void validate_lengthDescription_pass() {
		when(feedback.getDescription()).thenReturn(generateMaxExceededString(Constants.TEXT_MAX_LENGTH - 1));
		assertFalse(validate(feedback).hasErrors());
	}

	@Test
	public void validate_nullDescription_fail() {
		when(feedback.getDescription()).thenReturn(null);
		assertTrue(hasErrorCode(validate(feedback), "feedback.description.blank"));
	}

	@Test
	public void validate_emptyDescription_fail() {
		when(feedback.getDescription()).thenReturn(EMPTY_TOKEN);
		assertTrue(hasErrorCode(validate(feedback), "feedback.description.blank"));
	}

	@Test
	public void validate_whiteSpaceDescription_fail() {
		when(feedback.getDescription()).thenReturn(WHITE_SPACE_TOKEN);
		assertTrue(hasErrorCode(validate(feedback), "feedback.description.blank"));
	}

	@Test
	public void validate_nullConcern_fail() {
		when(feedback.getConcern()).thenReturn(null);
		assertTrue(hasErrorCode(validate(feedback), "feedback.concern.blank"));
	}

	@Test
	public void validate_nullCodeConcern_fail() {
		when(concern.getDescription()).thenReturn(null);
		when(concern.getCode()).thenReturn(null);
		when(feedback.getConcern()).thenReturn(concern);
		assertTrue(hasErrorCode(validate(feedback), "feedback.concern.blank"));
	}

	@Test
	public void validate_emptyCodeConcern_fail() {
		when(concern.getDescription()).thenReturn(EMPTY_TOKEN);
		when(concern.getCode()).thenReturn(EMPTY_TOKEN);
		when(feedback.getConcern()).thenReturn(concern);
		assertTrue(hasErrorCode(validate(feedback), "feedback.concern.blank"));
	}

	@Test
	public void validate_whiteSpaceCodeConcern_fail() {
		when(concern.getDescription()).thenReturn(WHITE_SPACE_TOKEN);
		when(concern.getCode()).thenReturn(WHITE_SPACE_TOKEN);
		when(feedback.getConcern()).thenReturn(concern);
		assertTrue(hasErrorCode(validate(feedback), "feedback.concern.blank"));
	}

	@Test
	public void validate_nullType_fail() {
		when(feedback.getType()).thenReturn(null);
		assertTrue(hasErrorCode(validate(feedback), "feedback.type.blank"));
	}

	@Test
	public void validate_emptyType_fail() {
		when(feedback.getType()).thenReturn(EMPTY_TOKEN);
		assertTrue(hasErrorCode(validate(feedback), "feedback.type.blank"));
	}

	@Test
	public void validate_whiteSpaceType_fail() {
		when(feedback.getType()).thenReturn(WHITE_SPACE_TOKEN);
		assertTrue(hasErrorCode(validate(feedback), "feedback.type.blank"));
	}

	@Test
	public void validate_assetLength_pass() {
		List<WorkAssetForm> list = Lists.newArrayList();
		when(workAssetForm.getDescription()).thenReturn(generateMaxExceededString(Constants.ASSET_DESCRIPTION_TEXT_LENGTH - 1));
		list.add(workAssetForm);
		when(feedback.getAttachments()).thenReturn(list);
		assertFalse(validate(feedback).hasErrors());
	}

	@Test
	public void validate_assetLength_fail() {
		List<WorkAssetForm> list = Lists.newArrayList();
		when(workAssetForm.getDescription()).thenReturn(generateMaxExceededString(Constants.ASSET_DESCRIPTION_TEXT_LENGTH));
		list.add(workAssetForm);
		when(feedback.getAttachments()).thenReturn(list);
		assertTrue(hasErrorCode(validate(feedback), "feedback.asset.length"));
	}

	@Test
	public void validate_ValidSupports_success() {
		assertTrue(validator.supports(FeedbackDTO.class));
	}

	@Test
	public void validate_InvalidSupports_fail() {
		assertFalse(validator.supports(List.class));
		assertFalse(validator.supports(Integer.class));
		assertFalse(validator.supports(String.class));
	}

	protected Validator getValidator() {
		return validator;
	}
}
