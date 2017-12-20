package com.workmarket.domains.authentication.saml.metadata;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.providers.SSOAuthenticationProvider;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.common.SAMLException;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.impl.AttributeImpl;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.validation.ValidationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLAuthenticationToken;
import org.springframework.security.saml.SAMLConstants;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.log.SAMLLogger;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SSOAuthenticationProviderTest {

	@Mock MetricRegistry registry;
	@Mock WMWebSSOProfileConsumerImpl webSSOProfileConsumer;
	@Mock SAMLUserDetailsService userDetails;
	@Mock SAMLLogger samlLogger;
	@InjectMocks SSOAuthenticationProvider SSOAuthenticationProvider;

	private SAMLMessageContext samlMessageContext;

	@Before
	public void init() throws SecurityException, SAMLException, ValidationException {

		// mock SAMLCredential
		SAMLCredential samlCredential = mock(SAMLCredential.class);

		NameID nameId = mock(NameID.class);
		when(samlCredential.getNameID()).thenReturn(nameId);

		AuthnStatement authnStatement = mock(AuthnStatement.class);
		Assertion assertion = mock(Assertion.class);
		when(assertion.getAuthnStatements()).thenReturn(Lists.newArrayList(authnStatement));
		when(samlCredential.getAuthenticationAssertion()).thenReturn(assertion);

		when(webSSOProfileConsumer.processAuthenticationResponse(any(SAMLMessageContext.class))).thenReturn(samlCredential);
		Meter meter = mock(Meter.class);
		when(registry.meter(anyString())).thenReturn(meter);

		ExtendedUserDetails extendedUserDetails = mock(ExtendedUserDetails.class);
		when(userDetails.loadUserBySAML(any(SAMLCredential.class))).thenReturn(extendedUserDetails);

		samlMessageContext = mock(SAMLMessageContext.class);
		when(samlMessageContext.getCommunicationProfileId()).thenReturn(SAMLConstants.SAML2_WEBSSO_PROFILE_URI);
	}

	@Test
	public void testAuthenticate() {
		SAMLAuthenticationToken authentication = new SAMLAuthenticationToken(samlMessageContext);
		Authentication authenticationResult = SSOAuthenticationProvider.authenticate(authentication);
		assertNotNull(authenticationResult.getDetails());
	}
}
