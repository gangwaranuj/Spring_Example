package com.workmarket.utility;

import com.google.common.base.Optional;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WebUtilitiesTest {

	@Test
	public void isPathExcluded() {
		MockHttpServletRequest request;
		String[] excludedPaths = { "/download/", "/media/", "/favicon.ico" };

		request = new MockHttpServletRequest();
		request.setContextPath("/myapplication");
		request.setRequestURI("/myapplication/media/images/logo.gif");
		assertTrue(WebUtilities.isRequestURIPrefixedByAny(request, excludedPaths));
	}

	@Test
	public void isPathNotExcluded() {
		MockHttpServletRequest request;
		String[] excludedPaths = { "/download/", "/media/", "/favicon.ico" };

		request = new MockHttpServletRequest();
		request.setContextPath("/myapplication");
		request.setRequestURI("/myapplication/home");
		assertFalse(WebUtilities.isRequestURIPrefixedByAny(request, excludedPaths));
	}

	@Test
	public void testFormEncode_GivenMap_ExpectValidResult() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("foo", "bar");
		parameters.put("spaces", "look at these spaces");
		parameters.put("characters", ":/'");

		Optional<String> encodedParameters = WebUtilities.formEncodeMap(parameters);

		if (!encodedParameters.isPresent())
			fail("Expected a result given a valid map");

		assertTrue("Missing foo parameter in result", encodedParameters.get().contains("foo=bar"));
		assertTrue("Missing spaces parameter in result", encodedParameters.get().contains("spaces=look+at+these+spaces"));
		assertTrue("Missing characters parameter in result", encodedParameters.get().contains("characters=%3A%2F%27"));
	}

	@Test
	public void isAjax_Happy() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("X-Requested-With", "XmLHtTpReQuEsT");
		assertTrue(WebUtilities.isAjax(request));
	}

	@Test
	public void isAjax_False() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("X-Requested-With", "Julio Iglesias");
		assertFalse(WebUtilities.isAjax(request));
	}

	@Test
	public void isPageRequest_Happy() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertTrue(WebUtilities.isPageRequest(request));
	}

	@Test
	public void isPageRequest_Ajax_False() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("X-Requested-With", "XmlHttpRequest");
		assertFalse(WebUtilities.isPageRequest(request));
	}

	@Test
	public void isPageRequest_Jsp_False() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("test.jsp");
		assertFalse(WebUtilities.isPageRequest(request));
	}
}
