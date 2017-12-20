package com.workmarket.web.validators;

import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.PasswordStrengthResponse;
import com.workmarket.auth.gen.Messages.PasswordStrengthViolation;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.authentication.services.SecurityContextFacadeImpl;
import com.workmarket.domains.model.User;
import com.workmarket.service.infra.business.AuthTrialCommon;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.web.forms.PasswordChangeForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import rx.Observable;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class PasswordChangeValidatorTest {

	private PasswordChangeValidator validator;
	private ExtendedUserDetails userDetails = new ExtendedUserDetails("Commander", "Sh3pard", Arrays.asList(new GrantedAuthority[0]));
	private AuthTrialCommon authTrialCommon = mock(AuthTrialCommon.class);
	private AuthenticationClient mockAuthClient;
	private RequestContext context;

	@Before
	public void init() throws IOException {
		final AuthenticationService authenticationService = mock(AuthenticationService.class);
		final SecurityContextFacade securityContextFacade = mock(SecurityContextFacadeImpl.class);

		when(securityContextFacade.getCurrentUser()).thenReturn(userDetails);

		when(authenticationService.auth(anyString(), anyString())).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				String password = (String) invocationOnMock.getArguments()[1];
				User user = null;
				if (password.equals(userDetails.getPassword())) {
					user = new User();
				}
				return user;
			}
		});

		context = mock(RequestContext.class);
		authTrialCommon = mock(AuthTrialCommon.class);
		mockAuthClient = mock(AuthenticationClient.class);
		when(authTrialCommon.getApiContext()).thenReturn(context);
		validator = new PasswordChangeValidator(new PasswordValidator()
				.setAuthenticationService(authenticationService)
				.setSecurityContextFacade(securityContextFacade)
				.setAuthTrialCommon(authTrialCommon)
				.setAuthClient(mockAuthClient));
	}

	private BindingResult validate(final PasswordChangeForm form) {
		BindingResult binding = new BeanPropertyBindingResult(form, "password");
		validator.validate(form, "username", binding);
		return binding;
	}

	@Test
	public void valid() {
		setSuccessfulResponse();
		String newPassword = "Va1karyan";
		PasswordChangeForm form = new PasswordChangeForm();
		form.setNewPassword(newPassword);
		form.setConfirmNewPassword(newPassword);
		form.setCurrentPassword(userDetails.getPassword());
		final BindingResult validate = validate(form);
		assertFalse(validate.hasErrors());
	}

	@Test
	public void invalidCurrentPasswordDoesNotMatch() {
		setSuccessfulResponse();
		String newPassword = "Va1karyan";
		PasswordChangeForm form = new PasswordChangeForm();
		form.setNewPassword(newPassword);
		form.setConfirmNewPassword(newPassword);
		form.setCurrentPassword("Grunt");
		assertTrue(validate(form).hasErrors());
	}

	private void setSuccessfulResponse() {
		when(mockAuthClient.checkPasswordStrength("Va1karyan", "username", context)).thenReturn(
				Observable.just(PasswordStrengthResponse.newBuilder()
						.setOk(true).build()));
	}
	@Test
	public void invalidConfirmationDoesNotMatch() {
		setSuccessfulResponse();
		String newPassword = "Va1karyan";
		String confirmationPassword = "Mord1n";
		PasswordChangeForm form = new PasswordChangeForm();
		form.setNewPassword(newPassword);
		form.setConfirmNewPassword(confirmationPassword);
		form.setCurrentPassword("Grunt");
		assertTrue(validate(form).hasErrors());
	}

	@Test
	public void invalidEmptyCurrentPassword() {
		setSuccessfulResponse();
		final String newPassword = "Va1karyan";
		PasswordChangeForm form = new PasswordChangeForm();
		form.setNewPassword(newPassword);
		form.setConfirmNewPassword(newPassword);
		assertTrue(validate(form).hasErrors());
	}

	@Test
	public void invalidNewPasswordIsInvalid() {
		when(mockAuthClient.checkPasswordStrength("Tali", "username", context)).thenReturn(
				Observable.just(PasswordStrengthResponse.newBuilder()
						.setViolation(PasswordStrengthViolation.LENGTH_TOO_SHORT)
						.setOk(false).build()));
		String newPassword = "Tali";
		PasswordChangeForm form = new PasswordChangeForm();
		form.setNewPassword(newPassword);
		form.setConfirmNewPassword(newPassword);
		form.setCurrentPassword(userDetails.getPassword());
		assertTrue(validate(form).hasErrors());
	}
}
