package com.workmarket.web.interceptors;

import com.workmarket.utility.WebUtilities;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;

public abstract class ExcludableInterceptor extends HandlerInterceptorAdapter {
	String[] excludedPaths;

	public boolean isExcluded(HttpServletRequest request) {
		return WebUtilities.isRequestURIPrefixedByAny(request, excludedPaths);
	}

	public void setExcludedPaths(String[] excludedPaths) {
		this.excludedPaths = excludedPaths;
	}
}