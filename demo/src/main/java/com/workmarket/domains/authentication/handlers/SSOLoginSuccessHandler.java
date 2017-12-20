package com.workmarket.domains.authentication.handlers;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.Status;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthTrialCommon;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.NameID;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Make it so that auth service can know about SSO authenticated sessions.
 */
public class SSOLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(SSOLoginSuccessHandler.class);

	@Autowired AuthTrialCommon trialCommon;
	@Autowired AuthenticationClient authClient;
	@Autowired UserService userService;
	@Autowired CompanyService companyService;
	@Autowired MetricRegistry registry;

	private WMMetricRegistryFacade facade;

	@PostConstruct
	public void init() {
		this.facade = new WMMetricRegistryFacade(registry, "sso-login");
	}

	@Override
	public void onAuthenticationSuccess(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Authentication authentication) throws ServletException, IOException {

		final boolean result = safeNotify(request, authentication);
		if (result) {
			facade.meter("success").mark();
		} else {
			facade.meter("failure").mark();
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}

	private boolean safeNotify(final HttpServletRequest request, final Authentication authentication)
			throws ServletException, IOException {
		// So at worst case, we don't notify authentication service on login.  Not ideal, but better than
		// throwing up.  Eventually SSO login will be handled by the auth service, but until then....
		final HttpSession session = request.getSession();
		if (session == null) {
			logger.error("session was null!");
			return false;
		}
		if (authentication == null) {
			logger.error("authentication was null!");
			return false;
		}
		final Object untypedCreds = authentication.getCredentials();
		if (!(untypedCreds instanceof SAMLCredential)) {
			logger.error("credentials were not SAML but {}", untypedCreds.getClass());
			return false;
		}
		final SAMLCredential credentials = (SAMLCredential) untypedCreds;
		logger.debug("safeNotify(): ");
		final NameID nameID = credentials.getNameID();
		if (nameID == null) {
			logger.error("name in credentials was null!");
			return false;
		}

		final Attribute emailAttr = credentials.getAttribute("email");
		if (emailAttr == null) {
			logger.error("No email attribute in SAML credentials!");
			return false;
		}

		final List<XMLObject> attrValues = emailAttr.getAttributeValues();
		if (attrValues.size() != 1) {
			logger.error("SSO attribute 'email' has wrong number of values ({}) expected 1", attrValues.size());
			return false;
		}

		final XMLObject attrValue = attrValues.get(0);
		if (!(attrValue instanceof XSString)) {
			logger.error("Expected email attribute value to be of type XSSString (or a descendant), got {}",
					attrValue.getClass());
			return false;
		}
		final String email = ((XSString) attrValue).getValue();

		final User user = userService.findUserByEmail(email);
		if (user == null) {
			logger.error("user with email {} does not exist in db", email);
			return false;
		}
		final List<Long> ids = companyService.findCompanyIdsForUsers(ImmutableList.of(user.getId()));
		if (ids.size() != 1) { // it really should be!!!
			logger.error("Could not get a company id for user uuid {} id {}", user.getUuid(), user.getId());
			return false;
		}
		notifyAuthServiceOfSsoLogin(session, user, ids.get(0));
		return true;
	}

	private void notifyAuthServiceOfSsoLogin(final HttpSession session, final User user, final Long id) {
		final Company byId = companyService.findById(id);
		final String companyUuid = byId.getUuid();
		final String userUuid = user.getUuid();

		logger.info("SSO Login USER {} CO id {}", userUuid, companyUuid);
		final Status status;
		try {
			status = authClient.ssoLoginFromMonolith(userUuid, companyUuid, session.getId(),
				trialCommon.getApiContext()).toBlocking().singleOrDefault(null);
		} catch (final RuntimeException e) {
			logger.error("Error telling auth service that user {} logged in", userUuid, e);
			return;
		}
		if (status == null) {
			logger.error("SSO login notify status NULL");
		} else {
			logger.info("SSO login notify status {} message {}", status.getSuccess(), status.getMessage());
		}
	}
}
