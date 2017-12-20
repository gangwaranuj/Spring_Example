package com.workmarket.web.validators;

import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.PasswordStrengthResponse;
import com.workmarket.auth.gen.Messages.PasswordStrengthViolation;
import com.workmarket.common.core.RequestContext;
import com.workmarket.service.infra.business.AuthTrialCommon;
import com.workmarket.web.forms.user.PasswordResetForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import rx.Observable;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class PasswordResetValidatorTest {

	private PasswordResetValidator validator;
	private AuthTrialCommon authTrialCommon;
	private AuthenticationClient mockAuthClient;
	private RequestContext context;

	@Before
	public void init() throws IOException {
		final PasswordValidator passwordValidator = new PasswordValidator();
		validator = new PasswordResetValidator(passwordValidator);
		authTrialCommon = mock(AuthTrialCommon.class);
		mockAuthClient = mock(AuthenticationClient.class);
		context = mock(RequestContext.class);
		when(authTrialCommon.getApiContext()).thenReturn(context);
		passwordValidator.setAuthTrialCommon(authTrialCommon)
				.setAuthClient(mockAuthClient);

	}

	@Test
	public void valid() {
		setSuccessfulResponse();

		final String newPassword = "Va1karyan";
		final PasswordResetForm form = new PasswordResetForm();
		form.setPasswordNew(newPassword);
		form.setPasswordNewConfirm(newPassword);
		assertFalse(validate(form).hasErrors());
	}

	private BindingResult validate(final PasswordResetForm form) {
		BindingResult binding = new BeanPropertyBindingResult(form, "password");
		validator.validate(form, "username", binding);
		return binding;
	}

	@Test
	public void invalidNewPassword() {
		when(mockAuthClient.checkPasswordStrength("Tali", "username", context)).thenReturn(
				Observable.just(PasswordStrengthResponse.newBuilder()
						.setViolation(PasswordStrengthViolation.LENGTH_TOO_SHORT)
						.setOk(false).build()));
		String newPassword = "Tali";
		PasswordResetForm form = new PasswordResetForm();
		form.setPasswordNew(newPassword);
		form.setPasswordNewConfirm(newPassword);
		assertTrue(validate(form).hasErrors());
	}

	@Test
	public void invalidConfirmationDoesNotMatch() {
		setSuccessfulResponse();
		final String newPassword = "Va1karyan";
		final String confirmationPassword = "Mord1n";
		final PasswordResetForm form = new PasswordResetForm();
		form.setPasswordNew(newPassword);
		form.setPasswordNewConfirm(confirmationPassword);
		assertTrue(validate(form).hasErrors());
	}


	private void setSuccessfulResponse() {
		when(mockAuthClient.checkPasswordStrength("Va1karyan", "username", context)).thenReturn(
				Observable.just(PasswordStrengthResponse.newBuilder()
						.setOk(true).build()));
	}
}
