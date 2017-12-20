package com.workmarket.web.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This code was adapted from http://www.javacodegeeks.com/2012/07/anti-cross-site-scripting-xss-filter.html
 */
public class XSSFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		// TODO - API - This is super hacky - this should come from config or read the @RequestMapping annotations in com.workmarket.api.*
		String path = ((HttpServletRequest) request).getRequestURI();
		if (path.startsWith("/v2/") ||
						path.startsWith("/api/v1/") ||
						path.startsWith("/worker/v2/") ||
						path.startsWith("/employer/v2/")) {
			chain.doFilter(request, response);
		} else {
			chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
		}
	}
}

