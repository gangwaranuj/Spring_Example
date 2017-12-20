package com.workmarket.domains.authentication.saml.metadata;

import org.springframework.security.saml.context.SAMLMessageContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Extending SAMLMessageContext so we can set an extra field with the IDP authentication redirect URL. This value is
 * returned from the SSO GET SAML Context call when we specify an IDP.
 * We need this when sending authentication redirects.
 */
public class WMSAMLMessageContext extends SAMLMessageContext {

	private String redirectURL;
	private HttpServletRequest request;

	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
}
