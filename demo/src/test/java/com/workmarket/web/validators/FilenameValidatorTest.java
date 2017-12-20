package com.workmarket.web.validators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by nick on 7/3/13 2:51 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class FilenameValidatorTest extends BaseValidatorTest {

	private FilenameValidator validator = new FilenameValidator();

	@Test
	public void validate_NullFilename_fail() {
		assertTrue(hasErrorCode(validate(null), "certifications.add.attachment.blank"));
	}

	@Test
	public void validate_EmptyFilename_fail() {
		assertTrue(hasErrorCode(validate(EMPTY_TOKEN), "certifications.add.attachment.blank"));
	}

	@Test
	public void validate_WhiteSpaceFilename_fail() {
		assertTrue(hasErrorCode(validate(WHITE_SPACE_TOKEN), "certifications.add.attachment.blank"));
	}

	@Test
	public void validate_InvalidCharInFilename_fail() {
		assertTrue(hasErrorCode(validate("schmoopsy,pants.png"), "filename.invalid_chars"));
	}

	@Test
	public void validate_TooLongFilename_fail() {
		assertTrue(
			hasErrorCode(
				validate(generateMaxExceededString(FilenameValidator.MAX_S3_FILENAME_LENGTH)),
				"filename.length"
			)
		);
	}

	@Test
	public void validate_ValidFilename_success() {
		assertFalse(validate("schmoopsy.pants.png").hasErrors());
	}

	@Test
	public void validate_ValidFilenameWithPunctuation_success() {
		assertFalse(validate("schmoopsy.\'()[]pant{s}.png").hasErrors());
	}

	@Test
	public void validate_ValidSupports_success() {
		assertTrue(validator.supports(String.class));
	}

	@Test
	public void validate_InvalidSupports_fail() {
		assertFalse(validator.supports(ArrayList.class));
		assertFalse(validator.supports(Integer.class));
		assertFalse(validator.supports(Set.class));
	}

	protected Validator getValidator() {
		return validator;
	}
}
