package com.workmarket.domains.authentication.web;

import org.springframework.security.web.util.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by nick on 10/8/13 1:42 PM
 */
@Component
public class NonAjaxRequestMatcher implements RequestMatcher {
	@Override
	public boolean matches(HttpServletRequest request) {
		return !"XmlHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
	}
}
