package com.workmarket.web.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.ApiAuthentication;
import com.workmarket.api.ApiBaseFilter;
import com.workmarket.api.exceptions.ApiException;
import com.workmarket.api.internal.RequestToken;
import com.workmarket.api.internal.service.ApiService;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages;
import com.workmarket.auth.gen.Messages.CredentialValidationResponse;
import com.workmarket.auth.gen.Messages.MigrateMonolithOAuthTokenRequest;
import com.workmarket.auth.gen.Messages.Status;
import com.workmarket.common.core.RequestContext;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.UserService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.web.filter.RequestIdFilter;

import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

public class ControllerIT extends BaseServiceIT {
	@Autowired protected WebApplicationContext webApplicationContext;
	@Autowired protected ExtendedUserDetailsService extendedUserDetailsService;
	@Autowired protected WebRequestContextProvider webRequestContextProvider;
	@Autowired protected UserService userService;
	@Autowired private AuthenticationClient authClient;

	protected MockMvc mockMvc;

	protected User user;
	protected ExtendedUserDetails details;

	@Autowired protected ApiService apiService;

	protected String accessToken;

	@Before
	public void setUpControllerMappings() {
		final AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();
		final RequestIdFilter requestIdFilter = autowireCapableBeanFactory.createBean(RequestIdFilter.class);
		final ApiBaseFilter apiBaseFilter = autowireCapableBeanFactory.createBean(ApiBaseFilter.class);

		// TODO API - Paths
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
						.addFilter(requestIdFilter, "/*")
						.addFilter(apiBaseFilter, "/v1/*")
						.addFilter(apiBaseFilter, "/v2/*")
						.addFilter(apiBaseFilter, "/api/v1/*")
						.addFilter(apiBaseFilter, "/worker/v2/*")
						.addFilter(apiBaseFilter, "/employer/v2/*")
						.build();
	}

	protected void login() throws Exception {
		login(newFirstEmployeeWithCashBalance());
	}

	protected void login(final User user) throws Exception {
	  this.user = user;
		// Make this user the API user so it's their access token
		final User apiUser = userService.getUser(user.getId());
		final String apiUserEmail = String.format(
				Constants.DEFAULT_API_USER_EMAIL_FORMAT_USING_COMPANY_UUID, user.getCompany() .getUuid());
		apiUser.setEmail(apiUserEmail);
		user.setEmail(apiUserEmail);
		changeUserName(apiUser, apiUserEmail);
		userService.saveOrUpdateUser(apiUser);

		setupRequestContextWithJWTForUser(user);
		final RequestToken requestToken = generateApiRequestTokenForCompany(user.getCompany().getId());
		accessToken = generateAccessToken(requestToken, user.getUuid(), user.getCompany().getUuid());
		details = (ExtendedUserDetails) extendedUserDetailsService.loadUserByUsername(user.getEmail());
		SecurityContextHolder.getContext().setAuthentication(new ApiAuthentication(details));
		logger.debug("Access token: " + accessToken + ", details: " + details.getUsername());

	}

	private void changeUserName(final User apiUser, final String apiUserEmail) {
		final Status single = authClient.changeUsername(apiUser.getUuid(), apiUserEmail, webRequestContextProvider
				.getRequestContext()).toBlocking().single();
		if (!single.getSuccess()) {
			throw new RuntimeException(single.getMessage());
		}
	}

	private void setupRequestContextWithJWTForUser(final User user) {
		authenticationService.setCurrentUser(user);
		final CredentialValidationResponse getJwtCredential = authClient
				.validateUserPasswordButNotIp(user.getEmail(), generatedPassword, webRequestContextProvider
						.getRequestContext()).toBlocking().single();

		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		requestContext.setJwt(getJwtCredential.getJwt());
		webRequestContextProvider.setRequestContext(requestContext);
	}

	protected MockHttpServletRequestBuilder doGet(String endpoint) {
		return MockMvcRequestBuilders.get(endpoint)
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON);
	}

	protected MockHttpServletRequestBuilder doPost(String endpoint) {
		return MockMvcRequestBuilders.post(endpoint)
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON);
	}

	protected MockHttpServletRequestBuilder doPut(String endpoint) {
		return MockMvcRequestBuilders.put(endpoint)
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON);
	}

	protected MockHttpServletRequestBuilder doDelete(String endpoint) {
		return MockMvcRequestBuilders.delete(endpoint)
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON);
	}

	protected RequestToken generateApiRequestTokenForCompany(final long companyId) throws ApiException {
		return apiService.createRequestToken(companyId);
	}

	protected String generateAccessToken(final RequestToken token, final String userUuid, final String companyUuid)
			throws ApiException {
		final String accessToken = apiService.getAccessToken(token);

		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		requestContext.setJwt(null);
		final Messages.Status status = authClient.migrateMonolithOAuthToken(
			MigrateMonolithOAuthTokenRequest.newBuilder()
				.setUserUuid(userUuid)
				.setCompanyUuid(companyUuid)
				.setToken(accessToken).build(), requestContext).toBlocking().single();

		logger.debug("Migrated token[" + accessToken + "] - Status: " + status);
		return accessToken;
	}

	protected void loginAsDefaultFirstNewEmployee() throws Exception {
		// Create a new user for API
		user = newFirstEmployee();
		// Make this user the API user so it's their access token
		final User apiUser = userService.getUser(user.getId());
		final Company firstUserCompany = companyService.findById(user.getCompany().getId());
		apiUser.setCompany(firstUserCompany);

		userService.saveOrUpdateUser(apiUser);
		setupRequestContextWithJWTForUser(user);

		authenticationService.setCurrentUser(apiUser);
		final RequestToken requestToken = generateApiRequestTokenForCompany(apiUser.getCompany().getId());
		accessToken = generateAccessToken(requestToken, apiUser.getUuid(), apiUser.getCompany().getUuid());
		details = (ExtendedUserDetails) extendedUserDetailsService.loadUserByUsername(apiUser.getEmail());
	}

	protected void loginAsFirstNewEmployeeWithTermsAndCustomSpendLimitAndCash(
			final String spendingLimit,
			final String cash) throws Exception {

		// Move the current user away from the api email address
		final User firstApiUser = userService.getUser(user.getId());
		final Company firstUserCompany = companyService.findById(user.getCompany().getId());
		final String firstUserCompanyUuid = firstUserCompany.getUuid();
		firstApiUser.setEmail(
				String.format("not" + Constants.DEFAULT_API_USER_EMAIL_FORMAT_USING_COMPANY_UUID, firstUserCompanyUuid));
		userService.saveOrUpdateUser(firstApiUser);

		// Create a new user for API
		user = newFirstEmployeeWithCustomSpendingLimitAndCashBalance(spendingLimit, cash);
		// Make this user the API user so it's their access token
		final User apiUser = userService.getUser(user.getId());
		final Company apiUserCompany = companyService.findById(user.getCompany().getId());
		final String apiUserCompanyUuid = apiUserCompany.getUuid();
		apiUser.setEmail(String.format(Constants.DEFAULT_API_USER_EMAIL_FORMAT_USING_COMPANY_UUID, apiUserCompanyUuid));
		userService.saveOrUpdateUser(apiUser);
		setupRequestContextWithJWTForUser(user);
		changeUserName(apiUser, apiUser.getEmail());

		authenticationService.setCurrentUser(apiUser);
		final RequestToken requestToken = generateApiRequestTokenForCompany(apiUser.getCompany().getId());
		accessToken = generateAccessToken(requestToken, apiUser.getUuid(), apiUserCompanyUuid);
		details = (ExtendedUserDetails) extendedUserDetailsService.loadUserByUsername(apiUser.getEmail());
	}

	protected <T> List<T> getResults(final MvcResult mvcResult, final TypeReference<ApiV2Response<T>> type)
			throws IOException {
		return expectApiV2Response(mvcResult, type).getResults();
	}

	protected <T> T getFirstResult(final MvcResult mvcResult, final TypeReference<ApiV2Response<T>> type)
			throws IOException {
		try {
			return getResults(mvcResult, type).get(0);
		}
		catch (final IndexOutOfBoundsException e) {
			Assert.assertTrue(
				"Expected content to resolve to type " + type + ": " + mvcResult.getResponse().getContentAsString(), false);
		}
		return null;
	}
}
