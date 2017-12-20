package com.workmarket.web.validators;

import com.workmarket.web.forms.user.PasswordSetupForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class PasswordSetupValidatorTest {

	private static final String PASSWORD = "Va1karyan";
	private PasswordSetupValidator validator;
	private PasswordValidator passwordValidator = mock(PasswordValidator.class);

	@Before
	public void init() throws IOException {
		validator = new PasswordSetupValidator(passwordValidator);
	}

	@Test
	public void invalidTermsAgree() {
		final PasswordSetupForm form = new PasswordSetupForm();
		form.setPasswordNew(PASSWORD);
		form.setPasswordNewConfirm(PASSWORD);
		form.setTermsAgree(false);
		when(passwordValidator.validatePasswordMatch(anyString(), anyString())).thenReturn(true);

		assertTrue(validate(form).hasErrors());
	}

	private BindingResult validate(final PasswordSetupForm form) {
		BindingResult binding = new BeanPropertyBindingResult(form, "password");
		validator.validate(form, "username", binding);
		return binding;
	}
}
