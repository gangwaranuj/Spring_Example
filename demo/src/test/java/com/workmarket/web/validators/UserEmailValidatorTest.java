package com.workmarket.web.validators;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.BlacklistedDomain;
import com.workmarket.service.infra.business.InvariantDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

/**
 * Created by nick on 4/18/14 7:47 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class UserEmailValidatorTest extends BaseValidatorTest {

	@Mock private InvariantDataService invariantDataService;
	@InjectMocks private UserEmailValidator validator;

	@Before
	public void setup() {
		when(invariantDataService.getBlacklistedDomains())
				.thenReturn(Lists.newArrayList(new BlacklistedDomain("barfville.edu")));
	}

	@Test
	public void validate_null_fail() {
		assertTrue(validate(null).hasErrors());
	}

	@Test
	public void validate_empty_fail() {
		assertTrue(validate(EMPTY_TOKEN).hasErrors());
	}

	@Test
	public void validate_invalidEmail1_fail() {
		assertTrue(validate("barf@slayertech.").hasErrors());
	}

	@Test
	public void validate_invalidEmail2_fail() {
		assertTrue(validate("barfslayertech.com").hasErrors());
	}

	@Test
	public void validate_blacklistedEmail_fail() {
		assertTrue(validate("barf@barfville.edu").hasErrors());
	}

	@Test
	public void validate_blacklistedEmailWithWhitespace_fail() {
		assertTrue(validate("    barf@barfville.edu    ").hasErrors());
	}

	@Test
	public void validate_regularEmail_pass() {
		assertFalse(validate("delightful@slayertech.com").hasErrors());
	}

	@Test
	public void validate_regularEmailWithSpaces_pass() {
		assertFalse(validate("    delightful@slayertech.com  ").hasErrors());
	}

	protected Validator getValidator() {
		return validator;
	}

}
