package com.workmarket.domains.authentication.filters;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.workmarket.test.mock.answer.defaults.DefaultExtendedUserDetailsAnswer;
import com.workmarket.test.mock.auth.AuthenticationMock;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;

public class SessionHijackingFilterTest {
	private static final String SESSION_ATTR = "workmarketSessionId";

	SessionHijackingFilter filter;
	MockHttpServletRequest request;
	MockHttpServletResponse response;
	MockHttpSession session;
	FilterChain filterChain;

	@Before
	public void setUp() {
		filter = new SessionHijackingFilter();

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		session = new MockHttpSession();
		filterChain = mock(FilterChain.class);

		request.setSession(session);

		SecurityContextHolder.clearContext();
	}

	/**
	 * Establish our security context - not all tests will use our security context
	 */
	private void setSecurityContext() {
		SecurityContext holder = SecurityContextHolder.createEmptyContext();
		holder.setAuthentication(new AuthenticationMock(new DefaultExtendedUserDetailsAnswer(Arrays.asList(new String[] {"val1", "val2"}))));
		SecurityContextHolder.setContext(holder);
	}

	@Test
	public void filter_withMultiple_workmarketSessionId_cookies_matches() {
		setSecurityContext();
		request.setCookies(new Cookie(SESSION_ATTR, "12345"), new Cookie(SESSION_ATTR, "9876"));
		session.setAttribute(SESSION_ATTR, "9876");

		try {
			filter.doFilter(request, response, filterChain);
			verify(filterChain, times(1)).doFilter(request, response);
		}
		catch (Exception se) {
			fail("Servlet exception");
		}

	}

	@Test
	public void filter_session_nosecuritycontext_passes() {
		request.setCookies(new Cookie(SESSION_ATTR, "1111"));
		session.setAttribute(SESSION_ATTR, "9876");

		try {
			filter.doFilter(request, response, filterChain);
			verify(filterChain, times(1)).doFilter(request, response);
		}
		catch (Exception se) {
			fail("Servlet exception");
		}

	}

	@Test
	public void filter_session_no_workmarketSessionId_in_session_passes() {
		setSecurityContext();
		request.setCookies(new Cookie(SESSION_ATTR, "1111"));

		try {
			filter.doFilter(request, response, filterChain);
			verify(filterChain, times(1)).doFilter(request, response);
			assertNotNull(session.getAttribute(SESSION_ATTR));
		}
		catch (Exception se) {
			fail("Servlet exception");
		}

	}

	@Test
	public void filter_session_nomatch() {
		setSecurityContext();
		request.setCookies(new Cookie(SESSION_ATTR, "1111"));
		session.setAttribute(SESSION_ATTR, "9876");

		try {
			filter.doFilter(request, response, filterChain);
			verifyZeroInteractions(filterChain);

			// verify our security context was cleared
			assertNull(SecurityContextHolder.getContext().getAuthentication());
		}
		catch (Exception se) {
			fail("Servlet exception");
		}

	}

}
