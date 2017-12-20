package com.workmarket.service.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by nick on 7/12/13 2:12 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CSRFTokenServiceImplTest {

	private CSRFTokenServiceImpl csrfTokenService = new CSRFTokenServiceImpl();
	private MockHttpSession session;
	private MockHttpServletRequest request;
	private String validToken = csrfTokenService.generateToken();

	@Before
	public void initRequests() {
		session = new MockHttpSession();
		request = new MockHttpServletRequest();
		Authentication authentication = mock(Authentication.class);
		request.setSession(session);
		request.setUserPrincipal(authentication);
	}

	@Test
	public void acceptsTokenIn_ValidToken_success() {
		session.setAttribute(CSRFTokenService.TOKEN_ATTRIBUTE_NAME, validToken);
		request.setParameter(CSRFTokenService.TOKEN_PARAMETER_NAME, validToken);

		assertTrue(csrfTokenService.acceptsTokenIn(request));
	}

	@Test
	public void acceptsTokenIn_ValidHeaderToken_success() {
		session.setAttribute(CSRFTokenService.TOKEN_ATTRIBUTE_NAME, validToken);
		request.addHeader(CSRFTokenService.TOKEN_HEADER_NAME, validToken);

		assertTrue(csrfTokenService.acceptsTokenIn(request));
	}

	@Test
	public void acceptsTokenIn_MaliciousToken_fail() {
		session.setAttribute(CSRFTokenService.TOKEN_ATTRIBUTE_NAME, validToken);
		request.setParameter(CSRFTokenService.TOKEN_PARAMETER_NAME, "I EAT YOUR FACE");

		assertFalse(csrfTokenService.acceptsTokenIn(request));
	}

	@Test
	public void acceptsTokenIn_MaliciousHeaderToken_fail() {
		session.setAttribute(CSRFTokenService.TOKEN_ATTRIBUTE_NAME, validToken);
		request.addHeader(CSRFTokenService.TOKEN_HEADER_NAME, "I EAT YOUR FACE");

		assertFalse(csrfTokenService.acceptsTokenIn(request));
	}

	@Test
	public void acceptsTokenIn_NullSessionToken_FailAndSetSessionToken() {
		session.setAttribute(CSRFTokenService.TOKEN_ATTRIBUTE_NAME, (String) null);
		request.setParameter(CSRFTokenService.TOKEN_PARAMETER_NAME, validToken);

		assertFalse(csrfTokenService.acceptsTokenIn(request));
		assertTrue(isNotBlank((CharSequence) session.getAttribute(CSRFTokenService.TOKEN_ATTRIBUTE_NAME)));
	}
}
