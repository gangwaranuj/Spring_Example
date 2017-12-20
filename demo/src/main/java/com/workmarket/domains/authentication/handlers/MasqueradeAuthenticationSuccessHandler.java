package com.workmarket.domains.authentication.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.workmarket.api.v2.worker.security.LoginTracker;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.workmarket.utility.StringUtilities;

public class MasqueradeAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
	
	private String defaultTargetUrl;

	public String getDefaultTargetUrl() {
		return defaultTargetUrl;
	}

	public void setDefaultTargetUrl(String defaultTargetUrl) {
		this.defaultTargetUrl = defaultTargetUrl;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		new LoginTracker().logDetailsOfSigninSuccess(request, "Masquerade");

		if (BooleanUtils.toBoolean(request.getParameter("returnTo")) && StringUtilities.isNotEmpty(request.getHeader("referer"))) {
			response.sendRedirect(request.getHeader("referer"));
		} else{
			response.sendRedirect(defaultTargetUrl);
		}
	}

}
