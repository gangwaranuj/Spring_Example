package com.workmarket.api.v2.worker.security;


import com.workmarket.api.ExpectApiV3Support;
import com.workmarket.api.v2.ApiV2Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class RestAuthenticationEntryPointTest extends ExpectApiV3Support {

	RestAuthenticationEntryPoint entryPoint;
	MockHttpServletRequest request;
	MockHttpServletResponse response;

	private static final String RESPONSE_PAYLOAD =
		"{\"meta\":{\"message\":\"Full authentication is required to access this resource\",\"code\":401},\"results\":[]}";

	@Before
	public void setup() {
		entryPoint = new RestAuthenticationEntryPoint();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	public void captureRequest_notAuthenticated_returns_401() {
		AuthenticationException exception = new InsufficientAuthenticationException("Full authentication is required to access this resource");

		try {
			entryPoint.commence(request, response, exception);
		} catch (Exception e) {
			fail ("Error processing request in entry point");
		}

		assertEquals("application/json;charset=UTF-8", response.getContentType());
		assertEquals("no-cache", response.getHeader("Cache-Control"));
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());


		try {
			ApiV2Response v2Response = expectApiV2Response(response.getContentAsString());
			expectApiV3ResponseMetaSupport(v2Response.getMeta());
			expectStatusCode(HttpStatus.UNAUTHORIZED.value(), v2Response.getMeta());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception getting response content");
		}
	}

}
