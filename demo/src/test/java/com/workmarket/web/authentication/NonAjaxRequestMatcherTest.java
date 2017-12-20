package com.workmarket.web.authentication;

import com.workmarket.domains.authentication.web.NonAjaxRequestMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by nick on 10/9/13 12:02 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class NonAjaxRequestMatcherTest {

	private HttpServletRequest request;
	private NonAjaxRequestMatcher matcher = new NonAjaxRequestMatcher();

	@Before
	public void setup() {
		request = mock(HttpServletRequest.class);
	}

	@Test
	public void matches_AjaxHeader_NoMatch() throws Exception {
		when(request.getHeader("X-Requested-With")).thenReturn("XmlHttpRequest");
		assertFalse(matcher.matches(request));
	}

	@Test
	public void matches_LowercaseAjaxHeaderValue_NoMatch() throws Exception {
		when(request.getHeader("X-Requested-With")).thenReturn("xmlhttprequest");
		assertFalse(matcher.matches(request));
	}

	@Test
	public void matches_NoAjaxHeader_Match() throws Exception {
		when(request.getHeader("X-Requested-With")).thenReturn("");
		assertTrue(matcher.matches(request));
	}
}
