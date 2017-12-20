package com.workmarket.domains.authentication.providers;

import com.codahale.metrics.MetricRegistry;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLAuthenticationProvider;

public class SSOAuthenticationProvider extends SAMLAuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(SSOAuthenticationProvider.class);

	@Autowired
	protected MessageSource messages;
	@Autowired
	private MetricRegistry registry;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		registry.meter("auth.sso").mark();
		try {
			return super.authenticate(authentication);
		}
		catch (Exception e) {
			logger.error("unable to process authentication request", e);

			if (e.getMessage() == null) {
				throw new AuthenticationServiceException("Unable to process your request", e);
			}
			else if (e.getMessage().contains("Error validating SAML message")) {
				throw new AuthenticationServiceException(messages.getMessage("auth.mbo.invalidSaml", null, null), e);
			}
			else if (ExceptionUtils.getFullStackTrace(e).contains("buyers_not_allowed")) {
				throw new AuthenticationServiceException( messages.getMessage("auth.mbo.buyersNotAllowed", null, null) );
			}
			else if (ExceptionUtils.getFullStackTrace(e).contains("not_in_salesforce")) {
				throw new AuthenticationServiceException(messages.getMessage("auth.mbo.notInSalesforce", null, null), e);
			}
			else if (ExceptionUtils.getFullStackTrace(e).contains("missing_address")) {
				throw new AuthenticationServiceException(messages.getMessage("auth.mbo.missingAddress", null, null), e);
			}
			else {
				throw e;
			}
		}
	}
}
