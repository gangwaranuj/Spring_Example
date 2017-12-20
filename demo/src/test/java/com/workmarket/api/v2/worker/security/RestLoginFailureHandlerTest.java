package com.workmarket.api.v2.worker.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.ExpectApiV3Support;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.ApiEmailNotConfirmedDTO;
import com.workmarket.service.exception.authentication.EmailNotConfirmedException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class RestLoginFailureHandlerTest extends ExpectApiV3Support {

	RestLoginFailureHandler handler;
	MockHttpServletRequest request;
	MockHttpServletResponse response;

	private static final String RESPONSE_PAYLOAD = "{\"meta\":{\"message\":\"Email has not been confirmed\",\"code\":401},\"results\":[]}";
	private static final TypeReference<ApiV2Response<ApiEmailNotConfirmedDTO>> apiV2ResponseTypeEmailNotConfirmedDto =
			new TypeReference<ApiV2Response<ApiEmailNotConfirmedDTO>>() { };


	@Before
	public void setup() {
		handler = new RestLoginFailureHandler();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	public void login_fails() {
		AuthenticationException exception = new BadCredentialsException("Email has not been confirmed");

		try {
			handler.onAuthenticationFailure(request, response, exception);
		} catch (Exception e) {
			fail("Exception processing failure event");
		}

		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
		assertEquals("application/json;charset=UTF-8", response.getContentType());
		assertEquals("no-cache", response.getHeader("Cache-Control"));

		try {
			ApiV2Response v2Response = expectApiV2Response(response.getContentAsString());
			expectApiV3ResponseMetaSupport(v2Response.getMeta());
			expectStatusCode(HttpStatus.UNAUTHORIZED.value(), v2Response.getMeta());
		}	catch (Exception e) {
			fail("Exception getting response content");
		}
	}

	@Test
	public void shouldReturnUserNumberOnUncofirmedEmailException() {
		final String userNumber = "user-number";
		AuthenticationException exception =
				new EmailNotConfirmedException("Email has not been confirmed").setUserNumber(userNumber);

		try {
			handler.onAuthenticationFailure(request, response, exception);
		} catch (Exception e) {
			fail("Exception processing failure event");
		}

		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
		assertEquals("application/json;charset=UTF-8", response.getContentType());
		assertEquals("no-cache", response.getHeader("Cache-Control"));
		assertTrue(response.isCommitted());

		try {
			final ApiV2Response<ApiEmailNotConfirmedDTO> v2Response =
					expectApiV2Response(response.getContentAsString(), apiV2ResponseTypeEmailNotConfirmedDto);
			expectApiV3ResponseMetaSupport(v2Response.getMeta());
			expectStatusCode(HttpStatus.UNAUTHORIZED.value(), v2Response.getMeta());
			assertEquals(1, v2Response.getResults().size());
			assertEquals(userNumber, v2Response.getResults().get(0).getUserNumber());
		}	catch (Exception e) {
			fail("Exception getting response content");
		}

	}
}
