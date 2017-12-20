package com.workmarket.api.internal.service;

import com.workmarket.api.exceptions.ApiException;
import com.workmarket.api.internal.RequestToken;
import com.workmarket.api.internal.model.AccessKey;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.ApiKeyAccessTokenResponse;
import com.workmarket.auth.gen.Messages.CreateLegacyApiKeyResponse;
import com.workmarket.auth.gen.Messages.FindLegacyApiKeysResponse;
import com.workmarket.auth.gen.Messages.LegacyTokenAndSecret;
import com.workmarket.auth.gen.Messages.Status;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.web.exceptions.HttpException401;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by joshlevine on 2/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiServiceTest {
	@Mock private RegistrationService registrationService;
	@Mock private AuthenticationClient authClient;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@Mock private RequestContext context;
	@InjectMocks private ApiService service;

	@Before
	public void setup() {
		when(webRequestContextProvider.getRequestContext()).thenReturn(context);
	}

	@Test
	public void testCreateRequestToken() throws ApiException {
		final long companyId = 13L;
		final User user = new User();
		final AccessKey accessKey = new AccessKey();
		accessKey.setSecret("SECRET");
		accessKey.setToken("TOKEN");
		when(webRequestContextProvider.getRequestContext()).thenReturn(context);
		when(registrationService.registerNewApiUserForCompany(eq(companyId))).thenReturn(user);

		when(authClient.createLegacyApiKey(eq(user.getUuid()), (RequestContext) anyObject()))
				.thenReturn(Observable.just(CreateLegacyApiKeyResponse.newBuilder()
						.setSecret("SECRET")
						.setToken("TOKEN")
						.setStatus(Status.newBuilder()
						.setSuccess(true)).build()));
		final RequestToken result = service.createRequestToken(companyId);
		assertEquals("Expected token to match", result.getToken(), accessKey.getToken());
		assertEquals("Expected secret to match", result.getSecret(), accessKey.getSecret());
	}

	@Test
	public void testGetRequestTokens() throws ApiException {
		final AccessKey accessKey1 = new AccessKey();
		accessKey1.setSecret("secret1");
		accessKey1.setToken("token1");

		final AccessKey accessKey2 = new AccessKey();
		accessKey2.setSecret("secret2");
		accessKey2.setToken("token2");

		when(authClient.findLegacyApiKeys(context))
				.thenReturn(Observable.just(FindLegacyApiKeysResponse.newBuilder()
						.addToken(LegacyTokenAndSecret.newBuilder()
								.setToken("token1")
								.setSecret("secret1"))
						.addToken(LegacyTokenAndSecret.newBuilder()
								.setToken("token2")
								.setSecret("secret2"))
						.setStatus(Status.newBuilder().setSuccess(true))
						.build()));
		final List<RequestToken> result = service.getRequestTokens();

		assertEquals("Expected two tokens", 2, result.size());
		assertEquals("Expected token1 to match", result.get(0).getSecret(), accessKey1.getSecret());
		assertEquals("Expected secret1 to match", result.get(0).getSecret(), accessKey1.getSecret());
		assertEquals("Expected token2 to match", result.get(1).getSecret(), accessKey2.getSecret());
		assertEquals("Expected secret2 to match", result.get(1).getSecret(), accessKey2.getSecret());
	}

	@Test(expected = HttpException401.class)
	public void testGetAccessToken_exception() throws ApiException {
		final RequestToken requestToken = new RequestToken("token", "secret");

		when(authClient.getAccessTokenFromApiKey(eq("token"), eq("secret"), (RequestContext) anyObject())).thenReturn(
				Observable.just(
						ApiKeyAccessTokenResponse.newBuilder()
								.setStatus(Status.newBuilder().setSuccess(false).setMessage("FAIL").build())
								.build()));
		service.getAccessToken(requestToken);
	}

	@Test
	public void testGetAccessToken() throws ApiException {
		final RequestToken requestToken = new RequestToken("token", "secret");

		final AccessKey accessKey = new AccessKey();
		accessKey.setToken(requestToken.getToken());
		accessKey.setSecret(requestToken.getSecret());

		when(authClient.getAccessTokenFromApiKey("token", "secret", context))
				.thenReturn(Observable.just(ApiKeyAccessTokenResponse.newBuilder()
						.setToken("OAUTHTOKEN!")
						.setStatus(Status.newBuilder().setSuccess(true))
						.build()));

		final String accessToken = service.getAccessToken(requestToken);

		assertEquals("OAUTHTOKEN!", accessToken);
	}
}
