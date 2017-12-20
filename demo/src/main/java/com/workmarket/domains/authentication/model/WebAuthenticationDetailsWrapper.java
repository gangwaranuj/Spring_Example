package com.workmarket.domains.authentication.model;

import com.workmarket.utility.InetAddressUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

import static com.workmarket.configuration.Constants.GOOGLE_RECAPTCHA_RESPONSE_PARAMEMTER_NAME;

/**
 * Subclass of {@link WebAuthenticationDetails} that uses a custom implementation
 * for setting the value of the <code>remoteAddress</code> property.
 */
public class WebAuthenticationDetailsWrapper extends WebAuthenticationDetails {

	private final String aRemoteAddress;
	private final String recaptchaUserResponse;
	private final boolean recaptchaExcluded;

	public WebAuthenticationDetailsWrapper(HttpServletRequest httpRequest) {
		super(httpRequest);
		this.aRemoteAddress = InetAddressUtilities.getAddressFromRequest(httpRequest);
		this.recaptchaUserResponse = httpRequest.getParameter(GOOGLE_RECAPTCHA_RESPONSE_PARAMEMTER_NAME);
		this.recaptchaExcluded = (Boolean) httpRequest.getSession().getAttribute("recaptchaExcluded");
	}

	@Override
	public String getRemoteAddress() {
		return StringUtils.defaultIfEmpty(aRemoteAddress, super.getRemoteAddress());
	}

	public String getRecaptchaUserResponse() {
		return recaptchaUserResponse;
	}

	public boolean isRecaptchaExcluded() {
		return recaptchaExcluded;
	}
}
