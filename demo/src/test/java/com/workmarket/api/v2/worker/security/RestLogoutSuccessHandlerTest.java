package com.workmarket.api.v2.worker.security;

import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RestLogoutSuccessHandlerTest extends BaseApiControllerTest {

	RestLogoutSuccessHandler handler;
	MockHttpServletRequest request;
	MockHttpServletResponse response;
	Authentication authStub;

	@Before
	public void setup() {
		handler = new RestLogoutSuccessHandler();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		authStub = new TestingAuthenticationToken(null, null);
	}

	@Test
	public void logout_succeeds() {

		try {
			handler.onLogoutSuccess(request, response, authStub);
		} catch (Exception e) {
			fail("Exception processing successful logout request: " + e);
		}

		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		assertEquals("application/json;charset=UTF-8", response.getContentType());
		assertEquals("no-cache", response.getHeader("Cache-Control"));

		try {
			ApiV2Response apiResponse = expectApiV2Response(response.getContentAsString());
			expectStatusCode(HttpStatus.SC_OK, apiResponse.getMeta());
		} catch (Exception e) {
			fail("Exception getting response content:" + e);
		}
	}
}
