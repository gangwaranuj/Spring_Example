package com.workmarket.domains.authentication.web;

import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nick on 4/29/14 12:00 AM
 *
 * To get around a Spring Security bug: https://jira.spring.io/browse/SEC-2578
 */
public class DoNothingHttpFirewall implements HttpFirewall {

	public FirewalledRequest getFirewalledRequest(HttpServletRequest request) throws RequestRejectedException {
		return new MyFirewalledRequest(request);
	}

	public HttpServletResponse getFirewalledResponse(HttpServletResponse response) {
		return response;
	}

	private static class MyFirewalledRequest extends FirewalledRequest {
		MyFirewalledRequest(HttpServletRequest r) {
			super(r);
		}
		public void reset() {}
	}
}
