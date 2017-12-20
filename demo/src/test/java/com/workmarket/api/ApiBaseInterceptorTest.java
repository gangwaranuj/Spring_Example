package com.workmarket.api;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;

import com.codahale.metrics.Meter;
import com.workmarket.api.exceptions.ApiRateLimitException;
import com.workmarket.api.internal.model.RateLimitConfig;
import com.workmarket.api.internal.service.RateLimitConfigService;
import com.workmarket.api.internal.service.RateLimiterService;
import com.workmarket.api.v2.worker.controllers.ProfileController;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.RedeemOAuthTokenRequest;
import com.workmarket.auth.gen.Messages.RedeemOAuthTokenResponse;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.jwt.Either;
import com.workmarket.common.jwt.JwtValidator;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException403;

import org.jose4j.jwt.JwtClaims;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by joshlevine on 1/26/17.
 */
@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ApiBaseInterceptorTest {

	@Mock private AuthenticationService authenticationService;
	@Mock private UserService userService;
	@Mock private ExtendedUserDetailsService extendedUserDetailsService;
	@Mock private BearerTokenExtractor tokenExtractor;
	@Mock private RateLimiterService rateLimiterService;
	@Mock private RateLimitConfigService rateLimitConfigService;
	@Mock private ProfileController controller;
	@Mock private MockHttpServletRequest request;
	@Mock private MockHttpServletResponse response;
	@Mock private ExtendedUserDetails userDetails;
	@Mock private WMMetricRegistryFacade wmMetricRegistryFacade;
	@Mock private Meter meter;
	@Mock private JwtValidator jwtValidator;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@Mock private AuthenticationClient authenticationClient;

	@InjectMocks private ApiBaseInterceptor interceptor = new ApiBaseInterceptor();

	private static final String VALID_JWT = "Valid";
	private static final String INVALID_JWT = "notValid";
	private static final String REQUEST_CONTEXT_PATH = "testContext";
	private static final String REQUEST_URI = "/testUri";
	private static final long COMPANY_ID = 6L;
	private static final String TOKEN = "userDetails";
	private static final String ACL_ADMIN = "ACL_ADMIN";
	private static final String USERUUID = "useruuid";

	@Before
	public void setup() {
		String[] excludedPaths = new String[0];
		interceptor.setRequireSSL(true);
		interceptor.setExcludedPaths(excludedPaths);
		SecurityContextHolder.getContext().setAuthentication(null);
		when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
		when(webRequestContextProvider.getWebRequestContext(any(String.class), any(String.class))).thenCallRealMethod();
		doCallRealMethod().when(webRequestContextProvider).setWebRequestContext(any(WebRequestContext.class));

		when(wmMetricRegistryFacade.meter(any(String.class))).thenReturn(meter);
		when(jwtValidator.call((String) anyObject()))
			.thenReturn(Either.<Collection<String>, Optional<JwtClaims>>right(Optional.<JwtClaims>absent()));
		final Authentication auth = new ApiAuthentication(userDetails);
		when(tokenExtractor.extract(request)).thenReturn(auth);
	}

	@After
	public void resetJwtInRequestContext() {
		webRequestContextProvider.getWebRequestContext().setJwt("");
	}

	@Test
	public void testRequestMustBeSecure() throws Exception {
		when(request.isSecure()).thenReturn(false);
		try {
			interceptor.preHandle(request, response, controller);
			fail("Expected preHandle to throw exception on non-https requests");
		} catch (Exception e) {
			assertTrue("Expected HttpException403 but got " + e.getClass().getName(), e instanceof HttpException403);
		}
	}

	@Test
	public void testRequestCanBeInsecure() throws Exception {
		interceptor.setRequireSSL(false);
		when(request.isSecure()).thenReturn(false);
		when(authenticationClient.redeemOAuthToken((RedeemOAuthTokenRequest) anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(RedeemOAuthTokenResponse.newBuilder().setOk(false).build()));

		try {
			interceptor.preHandle(request, response, controller);
			fail("Expected preHandle to throw an unauthorized exception when secure requests are not required");
		} catch (Exception e) {
			assertTrue("Expected HttpException401 but got " + e.getClass().getName(), e instanceof HttpException401);
		}
	}

	@Test
	public void testExcludedUrl() throws Exception {
		final User user = new User();
		user.setId(Constants.WORKMARKET_SYSTEM_USER_ID);
		final String[] excludedPaths = {REQUEST_URI};
		interceptor.setExcludedPaths(excludedPaths);

		when(request.getMethod()).thenReturn("GET");
		when(request.isSecure()).thenReturn(true);
		when(request.getRequestURI()).thenReturn(REQUEST_CONTEXT_PATH + REQUEST_URI);
		when(request.getContextPath()).thenReturn(REQUEST_CONTEXT_PATH);
		when(request.getHeader(eq("user-agent"))).thenReturn("user-agent");

		user.setUuid("test");
		user.setCompany(new Company());
		when(userService.findUserIdByUuid("test")).thenReturn(Constants.WORKMARKET_SYSTEM_USER_ID);
		when(userService.getUser(Constants.WORKMARKET_SYSTEM_USER_ID)).thenReturn(user);
		when(userService.findUserUuidById(Constants.WORKMARKET_SYSTEM_USER_ID)).thenReturn("test");
		when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(userDetails);
		when(authenticationClient.redeemOAuthToken((RedeemOAuthTokenRequest) anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(RedeemOAuthTokenResponse.newBuilder().setOk(false).build()));

		try {
			interceptor.preHandle(request, response, controller);
			verify(userService, times(1)).getUser(Constants.WORKMARKET_SYSTEM_USER_ID);
			verify(extendedUserDetailsService, times(1)).loadUser(any(User.class));
			verify(authenticationService, times(1)).setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
			assertNull(SecurityContextHolder.getContext().getAuthentication());
		} catch (Exception e) {
			fail("Expected preHandle to allow this request based on excluded paths but got " + e.getClass().getName() + " instead");
		}
	}

	@Test
	public void testValidTokenAuthentication() throws Exception {
		final User user = new User();
		user.setId(3L);
		user.setCompany(new Company());
		when(userService.getUser(anyLong())).thenReturn(user);
		expectValidAuthenticationToken(REQUEST_CONTEXT_PATH, REQUEST_URI);
	}

	@Test
	public void testInvalidTokenAuthentication() throws Exception {

		final Authentication auth = new ApiAuthentication(userDetails);

		when(request.isSecure()).thenReturn(true);
		when(request.getRequestURI()).thenReturn(REQUEST_CONTEXT_PATH + REQUEST_URI);
		when(request.getContextPath()).thenReturn(REQUEST_CONTEXT_PATH);
		when(tokenExtractor.extract(request)).thenReturn(auth);
		when(authenticationClient.redeemOAuthToken((RedeemOAuthTokenRequest) anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(RedeemOAuthTokenResponse.newBuilder().setOk(false).build()));

		expectInvalidAuthentication();

		verify(tokenExtractor, times(1)).extract(request);
	}

	@Test
	public void testInvalidV1TokenAuthentication() throws Exception {
		final String requestUri = "/api/v1/testUti";
		final JwtClaims claims = mock(JwtClaims.class);
		final Optional<JwtClaims> jwtClaimsOptional = Optional.of(claims);
		final Either<Collection<String>, Optional<JwtClaims>> value = Either.right(jwtClaimsOptional);
		final Authentication auth = new ApiAuthentication(userDetails);
		final User user = new User();
		user.setId(3L);

		when(request.getMethod()).thenReturn("GET");
		when(request.isSecure()).thenReturn(true);
		when(request.getRequestURI()).thenReturn(REQUEST_CONTEXT_PATH + requestUri);
		when(request.getContextPath()).thenReturn(REQUEST_CONTEXT_PATH);
		when(userDetails.hasAnyRoles(ACL_ADMIN)).thenReturn(false);

		when(tokenExtractor.extract(request)).thenReturn(auth);
		when(authenticationClient.redeemOAuthToken((RedeemOAuthTokenRequest) anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(RedeemOAuthTokenResponse.newBuilder().setJwt(VALID_JWT).setOk(true).build()));
		when(claims.getSubject()).thenReturn(USERUUID);
		when(userService.findUserIdByUuid(USERUUID)).thenReturn(3L);
		when(jwtValidator.call(eq(VALID_JWT))).thenReturn(value);

		when(userService.getUser(3L)).thenReturn(user);
		when(extendedUserDetailsService.loadUser(user)).thenReturn(userDetails);
		when(authenticationService.getCurrentUser()).thenReturn(user);

		try {
			interceptor.preHandle(request, response, controller);
			fail("Expected API V1 token without ROLE_WM_ADMIN to be forbidden");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Expected HttpException403 but got " + e.getClass().getName(), e instanceof HttpException403);

			assertEquals("Expected SecurityContextHolder to have the provided authentication",
				SecurityContextHolder.getContext().getAuthentication().getPrincipal(), userDetails);
			assertTrue("Expected SecurityContextHolder to be authenticated",
				SecurityContextHolder.getContext().getAuthentication().isAuthenticated());

			verifyCalls(1, 1, 1, 1);
			verify(userDetails, times(1)).hasAnyRoles(ACL_ADMIN);

		}
	}

	@Test
	public void testValidV1TokenAuthentication() throws Exception {

		when(request.isSecure()).thenReturn(true);
		when(request.getRequestURI()).thenReturn(REQUEST_CONTEXT_PATH + REQUEST_URI);
		when(request.getContextPath()).thenReturn(REQUEST_CONTEXT_PATH);
		when(userDetails.hasAnyRoles("ACL_ADMIN")).thenReturn(true);

		expectValidAuthenticationToken("testContext", "/api/v1/thing");

		verifyCalls(1,1, 1, 1);

	}

	private void verifyCalls(final int numberOfTimesTokenExtractorCalled,
													 final int numberOfTimesAuthenticationServiceCalled,
													 final int numberOfTimesUserServiceCalled,
													 final int numberOfTimesJwtValidatorCalled) {

		verify(tokenExtractor, times(numberOfTimesTokenExtractorCalled)).extract(any(HttpServletRequest.class));
		verify(authenticationClient, times(numberOfTimesAuthenticationServiceCalled))
				.redeemOAuthToken((RedeemOAuthTokenRequest) anyObject(), (RequestContext) anyObject());
		verify(userService, times(numberOfTimesUserServiceCalled)).getUser(3L);
		verify(jwtValidator, times(numberOfTimesJwtValidatorCalled)).call(any(String.class));
	}


	@Test
	public void testInvalidJWTTokenAuthentication() throws Exception {

		final WebRequestContext webRequestContext = new WebRequestContext();
		final Optional<JwtClaims> jwtClaimsOptional = Optional.absent();

		webRequestContext.setJwt(INVALID_JWT);
		webRequestContextProvider.setWebRequestContext(webRequestContext);
		Either<Collection<String>, Optional<JwtClaims>> value = Either.right(jwtClaimsOptional);
		when(jwtValidator.call(eq(INVALID_JWT))).thenReturn(value);
		when(request.isSecure()).thenReturn(true);
		when(request.getRequestURI()).thenReturn(REQUEST_CONTEXT_PATH + REQUEST_URI);
		when(request.getContextPath()).thenReturn(REQUEST_CONTEXT_PATH);
		when(tokenExtractor.extract(request)).thenReturn(null);

		expectInvalidAuthentication();

		verifyCalls(1,0, 0, 1);

	}

	@Test
	public void testValidJWTTokenAuthentication() throws Exception {

		final String requestUri = "/api/v1/testUri";
		final WebRequestContext webRequestContext = new WebRequestContext();
		final JwtClaims claims = mock(JwtClaims.class);
		webRequestContext.setJwt(VALID_JWT);
		final Optional<JwtClaims> jwtClaimsOptional = Optional.of(claims);
		final Either<Collection<String>, Optional<JwtClaims>> value = Either.right(jwtClaimsOptional);
		final User user = new User();
		user.setId(3L);
		final Authentication auth = new ApiAuthentication(userDetails);
		when(tokenExtractor.extract(request)).thenReturn(null);

		webRequestContextProvider.setWebRequestContext(webRequestContext);

		when(claims.getSubject()).thenReturn(USERUUID);
		when(userService.findUserIdByUuid(USERUUID)).thenReturn(3L);

		when(jwtValidator.call(eq(VALID_JWT))).thenReturn(value);
		when(userService.getUser(3L)).thenReturn(user);
		when(userDetails.getCompanyId()).thenReturn(2L);
		when(userDetails.hasAnyRoles("ACL_ADMIN")).thenReturn(true);
		when(userDetails.getId()).thenReturn(3L);
		when(extendedUserDetailsService.loadUser(user)).thenReturn(userDetails);
		when(authenticationService.getCurrentUser()).thenReturn(user);

		expectValidAuthenticationToken(REQUEST_CONTEXT_PATH, requestUri);

		verifyCalls(1, 0, 1, 1);

	}
	@Test
	public void testValidPassThroughAuthentication() throws Exception {
		String requestContextPath = "testContext";
		String requestUri = "/testUri";

		when(request.getMethod()).thenReturn("GET");
		when(request.isSecure()).thenReturn(true);
		when(request.getRequestURI()).thenReturn(requestContextPath + requestUri);
		when(request.getContextPath()).thenReturn(requestContextPath);
		when(request.getHeader(eq("user-agent"))).thenReturn("user-agent");

		when(userDetails.getId()).thenReturn(3L);

		Authentication auth = new ApiAuthentication(userDetails);
		auth.setAuthenticated(true);
		when(tokenExtractor.extract(request)).thenReturn(null);

		SecurityContextHolder.getContext().setAuthentication(auth);

		when(userDetails.getId()).thenReturn(3L);
		when(userDetails.getCompanyId()).thenReturn(2L);

		expectValidAuthentication();
	}


	@Test
	public void testInvalidPassThroughAuthentication() throws Exception {

		when(request.isSecure()).thenReturn(true);
		when(request.getRequestURI()).thenReturn(REQUEST_CONTEXT_PATH + REQUEST_URI);
		when(request.getContextPath()).thenReturn(REQUEST_CONTEXT_PATH);

		Authentication auth = new ApiAuthentication(userDetails);
		SecurityContextHolder.getContext().setAuthentication(auth);
		auth.setAuthenticated(false);
		when(tokenExtractor.extract(request)).thenReturn(null);

		expectInvalidAuthentication();
		verifyCalls(1,0,0,1);

	}

	private void expectValidAuthentication() throws Exception {

		final JwtClaims claims = mock(JwtClaims.class);
		final Optional<JwtClaims> jwtClaimsOptional = Optional.of(claims);
		final Either<Collection<String>, Optional<JwtClaims>> value = Either.right(jwtClaimsOptional);
		final User user = new User();
		user.setId(3L);
		user.setCompany(new Company());
		user.setUuid(USERUUID);

		when(authenticationClient.redeemOAuthToken((RedeemOAuthTokenRequest) anyObject(), (RequestContext) eq(null)))
				.thenReturn(Observable.just(RedeemOAuthTokenResponse.newBuilder().setJwt(VALID_JWT).setOk(true).build()));

		when(claims.getSubject()).thenReturn(USERUUID);
		when(userService.findUserIdByUuid(USERUUID)).thenReturn(3L);
		when(jwtValidator.call(eq(VALID_JWT))).thenReturn(value);
		when(userService.getUser(3L)).thenReturn(user);
		when(userDetails.getCompanyId()).thenReturn(2L);
		when(userDetails.hasAnyRoles("ACL_ADMIN")).thenReturn(true);
		when(userDetails.getId()).thenReturn(3L);
		when(extendedUserDetailsService.loadUser(user)).thenReturn(userDetails);
		when(authenticationService.getCurrentUser()).thenReturn(user);

		interceptor.preHandle(request, response, controller);
		assertEquals("Expected SecurityContextHolder to have the provided authentication",
			SecurityContextHolder.getContext().getAuthentication().getPrincipal(), userDetails);
		assertTrue("Expected SecurityContextHolder to be authenticated",
			SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
		verify(authenticationService, times(1)).setCurrentUser(3L);
	}

	private void expectInvalidAuthentication() throws Exception {
		try {
			interceptor.preHandle(request, response, controller);
			fail("Expected preHandle to throw an unauthorized exception when secure requests are not required");
		} catch (Exception e) {
			if (!(e instanceof HttpException401)) {
				throw e;
			}
			assertTrue("Expected HttpException401 but got " + e.getClass().getName(), e instanceof HttpException401);

			// expect either null authentication, or unauthenticated authentication
			if (SecurityContextHolder.getContext().getAuthentication() != null) {
				assertEquals("Expected SecurityContextHolder to have authentication",
					SecurityContextHolder.getContext().getAuthentication().getPrincipal(), userDetails);

				assertFalse("Expected SecurityContextHolder to be unauthenticated",
					SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
			}
		}
	}

	@Test
	public void testRequestIsIncrementingRateLimit() throws Exception {
		when(authenticationService.getCurrentUserCompanyId()).thenReturn(COMPANY_ID);
		final List<RateLimitConfig> rateLimitConfigurations = Lists.newArrayList();
		final RateLimitConfig rateLimitConfig1 = new RateLimitConfig();

		rateLimitConfig1.setName("rateLimit1");
		rateLimitConfig1.setLimit(ApiBaseInterceptor.DEFAULT_RATE_LIMIT);
		rateLimitConfig1.setLimitWindowInSeconds(ApiBaseInterceptor.DEFAULT_LIMIT_WINDOW);

		final RateLimitConfig rateLimitConfig2 = new RateLimitConfig();
		rateLimitConfig2.setName("rateLimit2");
		rateLimitConfig2.setLimit(1);
		rateLimitConfig2.setLimitWindowInSeconds(2);

		rateLimitConfigurations.add(rateLimitConfig1);
		rateLimitConfigurations.add(rateLimitConfig2);

		when(rateLimitConfigService.findByCompanyId(eq(COMPANY_ID))).thenReturn(rateLimitConfigurations);

		expectValidAuthenticationToken("testContextPath", "/test/uri");
		verify(rateLimiterService, times(1)).increment(
			ApiBaseInterceptor.RATE_LIMIT_KEY_PREFIX + ApiBaseInterceptor.DEFAULT_RATE_LIMIT_NAME  + "." + COMPANY_ID,
			1,
			ApiBaseInterceptor.DEFAULT_RATE_LIMIT,
			ApiBaseInterceptor.DEFAULT_LIMIT_WINDOW);
		verify(rateLimiterService, times(1)).increment(
			ApiBaseInterceptor.RATE_LIMIT_KEY_PREFIX + rateLimitConfig1.getName()  + "." + COMPANY_ID,
			1,
			ApiBaseInterceptor.DEFAULT_RATE_LIMIT,
			ApiBaseInterceptor.DEFAULT_LIMIT_WINDOW);
		verify(rateLimiterService, times(1)).increment(
			ApiBaseInterceptor.RATE_LIMIT_KEY_PREFIX + rateLimitConfig2.getName()  + "." + COMPANY_ID,
			1,
			1,
			2);
		verifyCalls(1,1,1,1);

	}

	@Test
	public void testRequestUsesDefaultRateLimit() throws Exception {
		when(authenticationService.getCurrentUserCompanyId()).thenReturn(COMPANY_ID);
		when(rateLimitConfigService.findByCompanyId(eq(COMPANY_ID))).thenReturn(Lists.<RateLimitConfig>newArrayList());

		expectValidAuthenticationToken("testContextPath", "/test/uri");
		verify(rateLimiterService, times(1)).increment(
			ApiBaseInterceptor.RATE_LIMIT_KEY_PREFIX + ApiBaseInterceptor.DEFAULT_RATE_LIMIT_NAME  + "." + COMPANY_ID,
			1,
			ApiBaseInterceptor.DEFAULT_RATE_LIMIT,
			ApiBaseInterceptor.DEFAULT_LIMIT_WINDOW);
		verifyCalls(1,1,1,1);
		verify(authenticationService, times(2)).getCurrentUserCompanyId();
	}

	@Test
	public void testRateLimitResultsInException() throws Exception {
		when(request.getMethod()).thenReturn("GET");
		when(request.isSecure()).thenReturn(true);
		when(request.getRequestURI()).thenReturn("testContextPath" + "/test/uri");
		when(request.getContextPath()).thenReturn("testContextPath");

		when(authenticationService.getCurrentUserCompanyId()).thenReturn(COMPANY_ID);
		when(authenticationClient.redeemOAuthToken((RedeemOAuthTokenRequest) anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(RedeemOAuthTokenResponse.newBuilder().setOk(true).setJwt(VALID_JWT).build()));

		final JwtClaims claims = mock(JwtClaims.class);
		when(claims.getSubject()).thenReturn(USERUUID);
		final Optional<JwtClaims> jwtClaimsOptional = Optional.of(claims);
		final Either<Collection<String>, Optional<JwtClaims>> value = Either.right(jwtClaimsOptional);
		when(userService.findUserIdByUuid(USERUUID)).thenReturn(3L);
		when(jwtValidator.call(eq(VALID_JWT))).thenReturn(value);

		when(rateLimitConfigService.findByCompanyId(eq(COMPANY_ID))).thenReturn(Lists.<RateLimitConfig>newArrayList());
		final String rateLimitKey = ApiBaseInterceptor.RATE_LIMIT_KEY_PREFIX + ApiBaseInterceptor.DEFAULT_RATE_LIMIT_NAME + "." + COMPANY_ID;
		when(rateLimiterService.increment(
			eq(rateLimitKey),
			eq(1),
			eq(ApiBaseInterceptor.DEFAULT_RATE_LIMIT),
			eq(ApiBaseInterceptor.DEFAULT_LIMIT_WINDOW)
		)).thenThrow(new ApiRateLimitException("Test Rate Limit Reached", 1, 1));


		Authentication auth = new ApiAuthentication(userDetails);
		when(tokenExtractor.extract(request)).thenReturn(auth);
		final User user = new User();
		user.setId(3L);
		user.setCompany(new Company());
		when(userService.getUser(3L)).thenReturn(user);
		when(extendedUserDetailsService.loadUser(user)).thenReturn(userDetails);
		when(authenticationService.getCurrentUser()).thenReturn(user);

		try {
			interceptor.preHandle(request, response, controller);
			fail("Expected preHandle to throw an ApiRateLimitException exception when requests are rate limited");
		} catch (Exception e) {
			assertTrue("Expected ApiRateLimitException but got " + e.getClass().getName(), e instanceof ApiRateLimitException);
			// expect either null authentication, or unauthenticated authentication
			if (SecurityContextHolder.getContext().getAuthentication() != null) {
				assertEquals("Expected SecurityContextHolder to have authentication",
					SecurityContextHolder.getContext().getAuthentication().getPrincipal(), userDetails);

				assertTrue("Expected SecurityContextHolder to be authenticated",
					SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
			}
		}

		verifyCalls(1,1,1,1);
		verify(authenticationService, times(2)).getCurrentUserCompanyId();
		verify(userService, times(1)).findUserIdByUuid(USERUUID);
	}

	@Test
	public void testProperMetricsGenerated_1() {
		final String requestUri = "/api/worker/v2/assignments/123123123/deliverables/987";

		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn(requestUri);
		when(response.getStatus()).thenReturn(200);

		final String expectedMeter = "2.worker.GET./api/worker/v2/assignments/_id_/deliverables/_id_.200";

		expectProperMetricGenerated(expectedMeter);
	}


	@Test
	public void testProperMetricsGenerated_2() {
		final String requestUri = "/api/v2/test";

		when(request.getMethod()).thenReturn("POST");
		when(request.getRequestURI()).thenReturn(requestUri);
		when(response.getStatus()).thenReturn(201);

		final String expectedMeter = "2.none.POST./api/v2/test.201";

		expectProperMetricGenerated(expectedMeter);
	}

	@Test
	public void testProperMetricsGenerated_3() {
		final String requestUri = "/api/search/t.e.s.t";

		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn(requestUri);
		when(response.getStatus()).thenReturn(200);

		final String expectedMeter = "none.none.GET./api/search/t_e_s_t.200";

		expectProperMetricGenerated(expectedMeter);
	}

	@Test
	public void testProperMetricsGenerated_4() {
		final String requestUri = "/worker/v2/assignments/4828692223/deliverables/a43b0112-258e-4d1f-a116-4a47f47402a7/removeUuid";

		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn(requestUri);
		when(response.getStatus()).thenReturn(500);

		final String expectedMeter = "2.worker.GET./worker/v2/assignments/_id_/deliverables/_id_/removeUuid.500";

		expectProperMetricGenerated(expectedMeter);
	}

	private void expectProperMetricGenerated(final String expectedMeter) {
		try {
			interceptor.afterCompletion(request, response, null, null);
			verify(wmMetricRegistryFacade).meter(expectedMeter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void expectValidAuthenticationToken(final String requestContextPath, final String requestUri) throws Exception {

		when(request.getMethod()).thenReturn("GET");
		when(request.isSecure()).thenReturn(true);
		when(request.getRequestURI()).thenReturn(requestContextPath + requestUri);
		when(request.getContextPath()).thenReturn(requestContextPath);

		expectValidAuthentication();
	}
}
