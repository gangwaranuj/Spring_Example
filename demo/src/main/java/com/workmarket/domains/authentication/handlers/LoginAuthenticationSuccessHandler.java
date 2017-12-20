package com.workmarket.domains.authentication.handlers;

import com.workmarket.api.v2.worker.security.LoginTracker;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by joshlevine on 2/16/17.
 */
public class LoginAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
																			HttpServletResponse response,
																			Authentication authentication) throws ServletException, IOException {
		new LoginTracker().logDetailsOfSigninSuccess(request, "Desktop");
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
