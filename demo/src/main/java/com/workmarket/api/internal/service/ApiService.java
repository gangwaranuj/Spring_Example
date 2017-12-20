package com.workmarket.api.internal.service;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.exceptions.ApiException;
import com.workmarket.api.internal.RequestToken;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.ApiKeyAccessTokenResponse;
import com.workmarket.auth.gen.Messages.CreateLegacyApiKeyResponse;
import com.workmarket.auth.gen.Messages.FindLegacyApiKeysResponse;
import com.workmarket.auth.gen.Messages.LegacyTokenAndSecret;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.web.exceptions.HttpException401;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiService {
	private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
	@Autowired private RegistrationService registrationService;
	@Autowired private AuthenticationClient authenticationClient;
	@Autowired private WebRequestContextProvider contextProvider;

	public RequestToken createRequestToken(final long companyId) throws ApiException {
		final User apiUser = registrationService.registerNewApiUserForCompany(companyId);
		if (apiUser == null) {
			return null;
		}
		final CreateLegacyApiKeyResponse resp = authenticationClient.createLegacyApiKey(
				apiUser.getUuid(), contextProvider.getRequestContext()).toBlocking().single();
		if (!resp.getStatus().getSuccess()) {
			logger.info("Not authorized creating api key: {}", resp.getStatus().getMessage());
			throw new HttpException401("Not Authorized");
		}
		return new RequestToken().setToken(resp.getToken()).setSecret(resp.getSecret());
	}

	public List<RequestToken> getRequestTokens() throws ApiException {
		final FindLegacyApiKeysResponse resp = authenticationClient.findLegacyApiKeys(contextProvider.getRequestContext())
				.toBlocking().single();
		if (!resp.getStatus().getSuccess()) {
			return ImmutableList.of();
		}

		final ImmutableList.Builder<RequestToken> tokens = ImmutableList.builder();
		for (final LegacyTokenAndSecret key : resp.getTokenList()) {
			tokens.add(new RequestToken().setToken(key.getToken()).setSecret(key.getSecret()));
		}
		return tokens.build();
	}

	public String getAccessToken(final RequestToken token) throws ApiException {
		final ApiKeyAccessTokenResponse resp = authenticationClient.getAccessTokenFromApiKey(
				token.getToken(), token.getSecret(), contextProvider.getRequestContext())
				.toBlocking().single();

		if (!resp.getStatus().getSuccess()) {
			logger.info("Not authorized getting token from api key: {}", resp.getStatus().getMessage());
			throw new HttpException401("Not Authorized");
		}

		return resp.getToken();
	}
}
