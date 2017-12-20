package com.workmarket.domains.authentication.handlers;

import com.workmarket.api.v2.worker.security.LoginTracker;
import com.workmarket.service.exception.authentication.EmailNotConfirmedException;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException401;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This handler allows Spring to redirect masquerading errors appropriately instead of puking up a tomcat 401
 */
public class MasqueradeAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler implements AuthenticationFailureHandler {


	@Override public void onAuthenticationFailure(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		new LoginTracker().logDetailsOfSigninFailure(request, "Masquerade");
		ServletContext ctx = request.getSession().getServletContext();
		if (exception instanceof UsernameNotFoundException) {
		//	request.removeAttribute("user");
		//	request.removeAttribute("user_fullname");
			response.sendRedirect("/admin/usermanagement/masquerade");
			//ctx.getRequestDispatcher(").forward(request, response);
			/*throw new HttpException404()
					.setMessageKey("admin.usermanagement.masquerade.usernotfound")
					.setRedirectUri("redirect:/admin/usermanagement/masquerade"); */
		} else if (exception instanceof EmailNotConfirmedException) {
			saveException(request, exception); // this exposes the exception via SPRING_SECURITY_LAST_EXCEPTION
			throw new HttpException401()
					.setMessageKey("admin.usermanagement.masquerade.usernotfound")
					.setRedirectUri("redirect:/error/not_confirmed");
		} else if (exception instanceof AuthenticationCredentialsNotFoundException) {
			throw new HttpException400()
					.setMessageKey("admin.usermanagement.masquerade.exception")
					.setRedirectUri("redirect:/admin/usermanagement/masquerade");
		} else if (exception instanceof SessionAuthenticationException) {
			throw new HttpException401()
					.setMessageKey("admin.usermanagement.masquerade.internal")
					.setRedirectUri("redirect:/admin/usermanagement/masquerade");
		} else {
			throw new HttpException400()
					.setMessageKey("admin.usermanagement.masquerade.exception")
					.setRedirectUri("redirect:/admin/usermanagement/masquerade");
		}
	}
}
