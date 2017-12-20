package com.workmarket.web.validators;

import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.PasswordStrengthResponse;
import com.workmarket.auth.gen.Messages.PasswordStrengthViolation;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.User;
import com.workmarket.service.infra.business.AuthTrialCommon;
import com.workmarket.service.infra.business.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component("passwordValidator")
public class PasswordValidator {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private AuthTrialCommon trialCommon;
	@Autowired private AuthenticationClient authClient;

	protected boolean validatePasswordMatch(String password, String confirmPassword) {
		return StringUtils.hasText(confirmPassword) && confirmPassword.equals(password);
	}

	/**
	 * @param currentPassword
	 * @return True if {@code currentPassword} matches against the user's current password
	 */
	protected boolean validateCurrentPassword(String currentPassword) {
		if (!StringUtils.hasText(currentPassword)) {
			return false;
		}
		ExtendedUserDetails currentUser = securityContextFacade.getCurrentUser();
		User updatedUser = authenticationService.auth(currentUser.getEmail(), currentPassword);
		return (updatedUser != null);
	}

	public void validate(String password, String username, Errors errors) {
		if (StringUtils.hasText(password)) {
			final RequestContext apiContext = trialCommon.getApiContext();
			final String actualUsername = isBlank(username) ? "ignored" : username;
			final PasswordStrengthResponse result = authClient
				.checkPasswordStrength(password, actualUsername, apiContext)
				.toBlocking().singleOrDefault(null);

			if (!result.getOk()) {
				if (result.getViolation() == PasswordStrengthViolation.LENGTH_TOO_SHORT) {
					errors.rejectValue(null, "mysettings.password.tooShort");
				} else {
					errors.rejectValue(null, "mysettings.password.predictable");
				}
			}
		} else {
			errors.rejectValue(null, "NotNull", null, "invalid");
		}
	}

	public PasswordValidator setAuthTrialCommon(final AuthTrialCommon trialCommon) {
		this.trialCommon = trialCommon;
		return this;
	}

	public PasswordValidator setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
		return this;
	}

	public PasswordValidator setSecurityContextFacade(SecurityContextFacade securityContextFacade) {
		this.securityContextFacade = securityContextFacade;
		return this;
	}

	public PasswordValidator setAuthClient(AuthenticationClient authClient) {
		this.authClient = authClient;
		return this;
	}
}
