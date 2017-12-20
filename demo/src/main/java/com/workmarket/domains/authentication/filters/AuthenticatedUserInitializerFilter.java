package com.workmarket.domains.authentication.filters;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.service.business.LinkedInService;
import com.workmarket.service.infra.business.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * TODO Audit that this is the correct way to do this.
 * Still kind of experimental. Spring security filter to explicitly set the current
 * and masquerade user via the {@link AuthenticationService}.
 */
public class AuthenticatedUserInitializerFilter extends GenericFilterBean {
	
	@Autowired private AuthenticationService authn;
	@Autowired private LinkedInService linkedInService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;

		ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);

		if (request instanceof SecurityContextHolderAwareRequestWrapper) {
			SecurityContextHolderAwareRequestWrapper securityRequest = (SecurityContextHolderAwareRequestWrapper)request;
			Authentication token = (Authentication)securityRequest.getUserPrincipal();

			if (token != null) {
				Long switchUserId = getSwitchUserId(token);

				if (token.getPrincipal() instanceof ExtendedUserDetails) {
					ExtendedUserDetails user = (ExtendedUserDetails)token.getPrincipal();
					String linkedInId =
						(String)request.getSession().getAttribute("linkedInId");
					String socialId =
						(String)request.getSession().getAttribute("socialId");

					if (linkedInId != null) {
						request.getSession().removeAttribute("linkedInId");
						linkedInService.attemptToLinkUserById(linkedInId, user.getId());
					}
					else if (socialId != null) {
						ProviderSignInUtils.handlePostSignUp(
							String.valueOf(user.getId()), requestAttributes
						);
					}

					if (switchUserId != null) {
						authn.startMasquerade(switchUserId, user.getId());
					} else {
						authn.setCurrentUser(user.getId());
					}
				}
			}
		}

		chain.doFilter(req, res);
	}
	
	private Long getSwitchUserId(Authentication token) {
		SwitchUserGrantedAuthority switchUserAuthority = getSwitchUserAuthority(token);
		if (switchUserAuthority == null) return null;
		if (!(switchUserAuthority.getSource() instanceof ExtendedUserDetails)) return null;
		ExtendedUserDetails switchUser = (ExtendedUserDetails)switchUserAuthority.getSource();
		return switchUser.getId();
	}

	private SwitchUserGrantedAuthority getSwitchUserAuthority(Authentication token) {
		for (GrantedAuthority authority : token.getAuthorities()) {
			if (authority instanceof SwitchUserGrantedAuthority)
				return (SwitchUserGrantedAuthority)authority;
		}
		return null;
	}
}
