package com.workmarket.api.v1;

/**
 * Created by joshlevine on 1/23/17.
 */

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.internal.AccessToken;
import com.workmarket.api.internal.ApiAuthorizationResponse;
import com.workmarket.api.internal.RequestToken;
import com.workmarket.api.v1.model.ApiAuthorizationDTO;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.models.MessageBundle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by joshlevine on 1/20/17.
 */
@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class AuthorizationControllerTest extends BaseApiControllerTest {

	public static final String ENDPOINT_API_V1_AUTHORIZATION = "/api/v1/authorization/request";

	private static final TypeReference<ApiV1Response<ApiAuthorizationDTO>> authorizationType = new TypeReference<ApiV1Response<ApiAuthorizationDTO>>() {};
	private static final TypeReference<ApiV1Response<Map>> authorizationFailureType = new TypeReference<ApiV1Response<Map>>() {};

	@InjectMocks private AuthorizationController controller = new AuthorizationController();

	@Before
	public void setup() throws Exception {
		super.setup(controller);

		when(messageHelper.newBundle()).thenReturn(MessageBundle.newInstance());
	}

	@Test
	public void testRequestAuthorized() throws Exception {
		logout();

		final String token = "token";
		final String secret = "secret";

		final AccessToken accessToken = new AccessToken(token, secret);
		// FIXME:this test is wrong the token here should be different than the one we use to get the token
		when(apiService.getAccessToken(any(RequestToken.class))).thenReturn(token);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post(ENDPOINT_API_V1_AUTHORIZATION)
			.header("accept", MediaType.APPLICATION_JSON)
			.param("token", token)
			.param("secret", secret)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		ApiV1Response<ApiAuthorizationDTO> apiResponse = expectApiV1Response(result, authorizationType);
		assertEquals("Expect access token to match", accessToken.getToken(), apiResponse.getResponse().getAccessToken());
	}

	@Test
	public void testRequestNotAuthorized() throws Exception {
		logout();

		final String token = "token";
		final String secret = "secret";

		final ArgumentCaptor<RequestToken> requestTokenArgumentCaptor = ArgumentCaptor.forClass(RequestToken.class);

		when(apiService.getAccessToken(requestTokenArgumentCaptor.capture())).thenThrow(new HttpException401("Forbidden"));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post(ENDPOINT_API_V1_AUTHORIZATION)
			.header("accept", MediaType.APPLICATION_JSON)
			.param("token", token)
			.param("secret", secret)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized()).andReturn();

		ApiV1Response<Map> apiResponse = expectApiV1Response(result, authorizationFailureType);
		expectStatusCode(HttpStatus.UNAUTHORIZED.value(), apiResponse.getMeta());
	}
}
