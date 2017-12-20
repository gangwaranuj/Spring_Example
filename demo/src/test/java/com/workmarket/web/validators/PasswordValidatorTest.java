package com.workmarket.web.validators;

import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.PasswordStrengthResponse;
import com.workmarket.auth.gen.Messages.PasswordStrengthViolation;
import com.workmarket.common.core.RequestContext;
import com.workmarket.service.infra.business.AuthTrialCommon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import rx.Observable;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class PasswordValidatorTest {

	private List<String> simplePasswords;
	private PasswordValidator validator;
	private AuthTrialCommon authTrialCommon;
	private AuthenticationClient mockAuthClient;
	private RequestContext context;

	@Before
	public void init() throws IOException {
		validator = new PasswordValidator();
		authTrialCommon = mock(AuthTrialCommon.class);
		mockAuthClient = mock(AuthenticationClient.class);
		context = mock(RequestContext.class);
		when(authTrialCommon.getApiContext()).thenReturn(context);
		validator.setAuthTrialCommon(authTrialCommon).setAuthClient(mockAuthClient);
	}

	@Test
	public void validPassword() {
		when(mockAuthClient.checkPasswordStrength("*1A8bcdx", "someusername", context)).thenReturn(
				Observable.just(PasswordStrengthResponse.newBuilder()
						.setOk(true).build()));
		assertFalse(validate("*1A8bcdx").hasErrors());
	}

	protected Errors validate(final String password) {
		BindingResult binding = new DataBinder(password).getBindingResult();
		validator.validate(password, "someusername", binding);
		return binding;
	}

	@Test
	public void invalidShortPassword() {
		when(mockAuthClient.checkPasswordStrength("123", "someusername", context)).thenReturn(
				Observable.just(PasswordStrengthResponse.newBuilder()
						.setViolation(PasswordStrengthViolation.LENGTH_TOO_SHORT)
						.setOk(false).build()));
		assertTrue(validate("123").hasErrors());
	}

	@Test
	public void invalidEmptyPassword() {
		when(mockAuthClient.checkPasswordStrength("", "someusername", context)).thenReturn(
				Observable.just(PasswordStrengthResponse.newBuilder()
						.setViolation(PasswordStrengthViolation.LENGTH_TOO_SHORT)
						.setOk(false).build()));
		assertTrue(validate("").hasErrors());
	}

	@Test
	public void simplePassword() {
		when(mockAuthClient.checkPasswordStrength("adirondacks", "someusername", context)).thenReturn(
				Observable.just(PasswordStrengthResponse.newBuilder()
						.setViolation(PasswordStrengthViolation.DICTIONARY_WORD)
						.setOk(false).build()));
		assertTrue(validate("adirondacks").hasErrors());
	}


	@Test
	public void sameAscurrent() {
		when(mockAuthClient.checkPasswordStrength("adirondacks", "someusername", context)).thenReturn(
				Observable.just(PasswordStrengthResponse.newBuilder()
						.setViolation(PasswordStrengthViolation.SAME_PASSWORD_AS_OLD)
						.setOk(false).build()));
		assertTrue(validate("adirondacks").hasErrors());
	}

	protected PasswordValidator getValidator() {
		return validator;
	}

}
