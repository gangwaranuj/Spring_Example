package com.workmarket.domains.authentication.filters;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PublicWorkRequestFilter extends OncePerRequestFilter {
	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	private RequestCache requestCache = new HttpSessionRequestCache();
	private RequestMatcher requestMatcher = new AntPathRequestMatcher("/work/**");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if(requestMatcher.matches(request) && trustResolver.isAnonymous(authentication)) {
			requestCache.saveRequest(request, response);
		}
		filterChain.doFilter(request, response);
	}
}
