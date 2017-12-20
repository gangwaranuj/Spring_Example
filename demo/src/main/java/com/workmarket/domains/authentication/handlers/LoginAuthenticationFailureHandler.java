package com.workmarket.domains.authentication.handlers;

import com.workmarket.api.v2.worker.security.LoginTracker;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.service.exception.authentication.EmailNotConfirmedException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.WebUtilities;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.workmarket.configuration.Constants.GOOGLE_RECAPTCHA_RESPONSE_PARAMEMTER_NAME;
import static com.workmarket.utility.StringUtilities.urlEncode;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Custom login in failure handler.
 */
public class LoginAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler implements AuthenticationFailureHandler {
	private static final String SPRING_SECURITY_LAST_EXCEPTION ="SPRING_SECURITY_LAST_EXCEPTION";
	private static final String SPRING_SECURITY_LAST_USERNAME ="SPRING_SECURITY_LAST_USERNAME";
	private static final String GOOGLE_RECAPTCHA_ENABLED = "googleRecaptchaEnabled";

	@Autowired UserService userService;
	@Autowired AuthenticationService authenticationService;

	@Override public void onAuthenticationFailure(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException ae) throws IOException, ServletException {
		boolean isAjaxRequest = WebUtilities.isAjax(request);
		new LoginTracker().logDetailsOfSigninFailure(request, "Desktop");
		
		request.setAttribute(SPRING_SECURITY_LAST_EXCEPTION, ae);

		if (isAjaxRequest) {
			response.setStatus(SC_UNAUTHORIZED);
		} else if (ae instanceof EmailNotConfirmedException) {
			String userNumber = ((EmailNotConfirmedException) ae).getUserNumber();
			response.sendRedirect(String.format("/error/not_confirmed?un=%s", userNumber));
		} else {
			String username = StringEscapeUtils.unescapeHtml((String)request.getSession().getAttribute(SPRING_SECURITY_LAST_USERNAME));
			User user = userService.findUserByEmail(username);

			// Read the recaptcha value sent from the front-end.
			String gRecaptchaUserResponse = request.getParameter(GOOGLE_RECAPTCHA_RESPONSE_PARAMEMTER_NAME);

			/*
				if the recaptcha is visible on the front-end then the gRecaptchaUserResponse will not be null
			 	if the recaptcha is not null but its value is empty then the user skipped the recaptcha hence we will need to
			 	dislpay it on the front-end again.
			*/
			boolean recaptchaEnabled = gRecaptchaUserResponse != null && isEmpty(gRecaptchaUserResponse);

			request.setAttribute(GOOGLE_RECAPTCHA_ENABLED, recaptchaEnabled || authenticationService.isRecaptchaEnabledOnUser(user));
			response.setStatus(SC_UNAUTHORIZED);
			String location = String.format("/login?error&login=%s", urlEncode(username));
			request.getRequestDispatcher(location).forward(request, response);
		}
	}
}
