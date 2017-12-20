package com.workmarket.web.validators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.Validator;

import static org.junit.Assert.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class RedirectValidatorTest extends BaseValidatorTest {

	RedirectValidator validator = new RedirectValidator();

	@Test
	public void validate_NullURL_fail() {
		assertTrue(hasErrorCode(validate(null), "NotEmpty"));
	}

	@Test
	public void validate_EmptyURL_fail() {
		assertTrue(hasErrorCode(validate(EMPTY_TOKEN), "NotEmpty"));
	}

	@Test
	public void validate_WhiteSpaceURL_fail() {
		assertTrue(hasErrorCode(validate(WHITE_SPACE_TOKEN), "NotEmpty"));
	}

	@Test
	public void validate_AbsoluteHttpURL_fail() {
		assertTrue(hasErrorCode(validate("http://www.zombo.com"), "redirect.invalid"));
	}

	@Test
	public void validate_AbsoluteHttpsURL_fail() {
		assertTrue(hasErrorCode(validate("https://www.zombo.com"), "redirect.invalid"));
	}

	@Test
	public void validate_AbsoluteNoProtocolURL_fail() {
		assertTrue(hasErrorCode(validate("www.zombo.com"), "redirect.invalid"));
	}

	@Test
	public void validate_AbsoluteNoProtocolNoSubdomainURL_fail() {
		assertTrue(hasErrorCode(validate("zombo.com"), "redirect.invalid"));
	}

	@Test
	public void validate_AbsoluteHttpNoSubdomainURL_fail() {
		assertTrue(hasErrorCode(validate("http://zombo.com"), "redirect.invalid"));
	}

	@Test
	public void validate_AbsoluteHttpsNoSubdomainURL_fail() {
		assertTrue(hasErrorCode(validate("https://zombo.com"), "redirect.invalid"));
	}

	@Test
	public void validate_AbsoluteHttpsFullPathURL_fail() {
		assertTrue(hasErrorCode(validate("https://www.zombo.com/zombocom/index.php"), "redirect.invalid"));
	}

	@Test
	public void validate_Injection_fail() {
		assertTrue(hasErrorCode(validate("/mmw/;</script>;"), "redirect.invalid"));
	}

	@Test
	public void validate_RelativeURL_success() {
		assertFalse(validate("/mmw").hasErrors());
	}

	@Test
	public void validate_RelativeURLMultiSlash_success() {
		assertFalse(validate("//mmw").hasErrors());
	}

	@Test
	public void validateWithDefault_ValidURL_success() {
		String url = "/mmw";
		assertEquals(url, validator.validateWithDefault(url, "/some/other/url"));
	}

	@Test
	public void validateWithDefault_InvalidURL_defaultURL() {
		String url = "/mmw";
		assertEquals(url, validator.validateWithDefault("http://zombo.com", url));
	}

	protected Validator getValidator() {
		return validator;
	}
}
