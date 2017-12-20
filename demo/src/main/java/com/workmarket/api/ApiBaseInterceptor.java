package com.workmarket.api;

import com.google.api.client.util.Sets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.api.exceptions.ApiException;
import com.workmarket.api.exceptions.ApiRateLimitException;
import com.workmarket.api.exceptions.IncrementException;
import com.workmarket.api.internal.model.RateLimitConfig;
import com.workmarket.api.internal.service.RateLimitConfigService;
import com.workmarket.api.internal.service.RateLimiterService;
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
import com.workmarket.domains.model.User;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.WebUtilities;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException403;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rx.Observable;

/**
 * Created by joshlevine on 12/27/16.
 */
public class ApiBaseInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ApiBaseInterceptor.class);
	public static final long DEFAULT_LIMIT_WINDOW = 60 * 60 * 24;
	public static final long DEFAULT_RATE_LIMIT = 1000000;
	public static final String DEFAULT_RATE_LIMIT_NAME = "defaultRateLimit";
	public static final String RATE_LIMIT_KEY_PREFIX = "rateLimit.";
	public static final String PARAM_FIELDS = "fields";

	private static final Pattern VERSION_EXTRACT_PATTERN = Pattern.compile("/v(\\d?)/");
	private static final Pattern API_EXTRACT_PATTERN = Pattern.compile("(employer|worker|internal)");
	private static final Pattern ID_EXTRACT_PATTERN = Pattern.compile("/(\\d+|[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})");
	private final String ID_REPLACEMENT = "/_id_";
	private static final String[] API_V_1_PATH_PREFIXES = {"/api/v1", "/v1"};

	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserService userService;
	@Autowired private ExtendedUserDetailsService extendedUserDetailsService;
	@Autowired private BearerTokenExtractor tokenExtractor;
	@Autowired private RateLimiterService rateLimiterService;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private JwtValidator jwtValidator;
	@Autowired private RateLimitConfigService rateLimitConfigService;
	@Autowired private CompanyService companyService;
	@Autowired private AuthenticationClient authClient;

	private WMMetricRegistryFacade wmMetricRegistryFacade;

	@PostConstruct
	protected void init() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "api");
	}

	private String[] excludedPaths;
	private boolean requireSSL;

	public void setExcludedPaths(String[] excludedPaths) {
		this.excludedPaths = excludedPaths;
	}

	public void setRequireSSL(boolean requireSSL) {
		this.requireSSL = requireSSL;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// TODO API - do we need this debug boolean?
		final boolean debug = logger.isDebugEnabled();

		// Check HTTP Protocol. In production, this can only be HTTPS.
		if (requireSSL && !request.isSecure()) {
			throw new HttpException403("HTTPS Protocol is required to access Work Market API Services.");
		}
		authenticateRequest(request, debug);
		if (authenticationService.getCurrentUserCompanyId() != null) {
			detectProjections(request, response);
			rateLimitRequest(request, authenticationService.getCurrentUserCompanyId());
		} else {
			// TODO API - rate limit un-authenticated requests ?
		}

		return true;
	}

	private void detectProjections(HttpServletRequest request, HttpServletResponse response) {
		final Set<String> fieldsToInclude = Sets.newHashSet();
		final String[] fieldParameterValues = request.getParameterValues(PARAM_FIELDS);
		if (fieldParameterValues != null) {
			for (String fieldToInclude : fieldParameterValues) {
				if (!fieldToInclude.contains(",")) {
					fieldsToInclude.add(fieldToInclude);
				} else {
					final List<String> splitFieldsToInclude = Splitter.on(',').splitToList(fieldToInclude);
					final List<String> trimmedFieldsToInclude = Lists.transform(splitFieldsToInclude, new Function<String, String>() {
						@Nullable
						@Override
						public String apply(@Nullable final String input) {
							return input != null ? input.trim() : "";
						}
					});
					fieldsToInclude.addAll(ImmutableSet.copyOf(trimmedFieldsToInclude));
				}
			}
		}

		final String fieldsHeader = request.getHeader(ApiBaseHttpMessageConverter.HEADER_X_WM_FIELD_PROJECTION);
		if (!StringUtils.isEmpty(fieldsHeader)) {
			final List<String> fields = Splitter.on(',').splitToList(fieldsHeader);
			fieldsToInclude.addAll(fields);
		}

		for (final String field : fieldsToInclude) {
			response.addHeader(ApiBaseHttpMessageConverter.HEADER_X_WM_FIELD_PROJECTION, field.trim());
		}
	}

	@Override
	public void postHandle(HttpServletRequest request,
												 HttpServletResponse response,
												 Object handler,
												 ModelAndView modelAndView) throws Exception {
		logger.debug("API - POST HANDLE");
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
															HttpServletResponse response,
															Object handler,
															Exception ex) throws Exception {
		logger.debug("API - AFTER COMPLETION");
		final Matcher versionMatcher = VERSION_EXTRACT_PATTERN.matcher(request.getRequestURI());
		final Matcher apiMatcher = API_EXTRACT_PATTERN.matcher(request.getRequestURI());
		final Matcher idMatcher = ID_EXTRACT_PATTERN.matcher(request.getRequestURI());

		String version = "none";
		String api = "none";
		String cleanedUri = request.getRequestURI();

		if (versionMatcher.find()) {
			version = versionMatcher.group(1);
		}

		if (apiMatcher.find()) {
			api = apiMatcher.group(1);
		}

		if (idMatcher.find()) {
			cleanedUri = idMatcher.replaceAll(ID_REPLACEMENT);
		}

		final int status = response.getStatus();
		final String metricPath = StringUtils.collectionToDelimitedString(Arrays.asList(
			version,
			api,
			request.getMethod(),
			cleanedUri.replaceAll("\\.", "_"),
			status), ".");

		wmMetricRegistryFacade.meter(metricPath).mark();
	}

	/**
	 * Evaluates the company rate limit for this request
	 *
	 * @param request   Current HttpServletRequest
	 * @param companyId The company to check the rate limit for
	 * @throws ApiRateLimitException
	 */
	private void rateLimitRequest(HttpServletRequest request, Long companyId) throws ApiRateLimitException {
		final List<RateLimitConfig> configs = Lists.newArrayList(rateLimitConfigService.findByCompanyId(companyId));

		configs.add(getDefaultRateLimitConfig());

		for (final RateLimitConfig config : configs) {
			final String key = RATE_LIMIT_KEY_PREFIX + config.getName() + "." + companyId;

			try {
				rateLimiterService.increment(key, 1, config.getLimit(), config.getLimitWindowInSeconds());
			} catch (IncrementException e) {
				throw new ApiException("Unable to increment redis rate limit for key[" + key + "]");
			}
		}
	}

	private RateLimitConfig getDefaultRateLimitConfig() {
		final RateLimitConfig defaultRateLimitConfig = new RateLimitConfig();
		defaultRateLimitConfig.setName(DEFAULT_RATE_LIMIT_NAME);
		defaultRateLimitConfig.setLimit(DEFAULT_RATE_LIMIT);
		defaultRateLimitConfig.setLimitWindowInSeconds(DEFAULT_LIMIT_WINDOW);
		return defaultRateLimitConfig;
	}

	/**
	 * Authenticate the current HttpServletRequest for API Access.
	 * This method tries two strategies to authenticate the request:
	 * <p>
	 * 1) Look for excluded endpoints
	 * 2) Look for a JWT in request
	 * 3) Try to fetch JWT from auth token in request
	 *
	 * @param request
	 * @param debug
	 * @throws HttpException401 When the request can not be authenticated by any strategy.
	 */
	protected void authenticateRequest(HttpServletRequest request, boolean debug) {
		// First attempts to auth the request by JWT, and if not, tries to exchange an access token for a jwt via the
		// auth service.
		String userUuid = null;
		String stringAccessToken = "unknown";

		final boolean isExcludedPath = WebUtilities.isRequestURIPrefixedByAny(request, excludedPaths);
		final Authentication passThroughAuthentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isPassThroughAuthentication = false;

		final Authentication tokenAuthentication = tokenExtractor.extract(request);
		// Get JWT by access token
		if (tokenAuthentication != null && !StringUtils.isEmpty(tokenAuthentication.getPrincipal().toString())) {
			final RequestContext requestContext = webRequestContextProvider.getRequestContext();
			final String jwt = redeemOAuthToken(tokenAuthentication.getPrincipal().toString(), requestContext);
			final WebRequestContext webRequestContext = webRequestContextProvider.getWebRequestContext();
			webRequestContext.setJwt(jwt);
			userUuid = getUserUuidFromJwt();
			stringAccessToken = tokenAuthentication.getPrincipal().toString();
		}
		else if (passThroughAuthentication != null
			&& passThroughAuthentication.isAuthenticated()
			&& passThroughAuthentication.getPrincipal() instanceof ExtendedUserDetails) {
			if (isExcludedPath) {
				logger.debug("request is excluded from authentication, however \"pass through\" principal was provided",
					stringAccessToken);
			}

			ExtendedUserDetails extendedUserDetails = (ExtendedUserDetails) passThroughAuthentication.getPrincipal();
			logger.warn("Pass through authorization used for URL=\"{}\" by companyId={} userId={}",
				request.getRequestURI(),
				extendedUserDetails.getCompanyId(),
				extendedUserDetails.getId());

			// If the user is the API User, reject this
			User user = userService.getUser(extendedUserDetails.getId());
			if(user.isApiEnabled()) {
				throw new HttpException401("Not authorized to perform this request - please provide your API access token");
			}

			try {
				userUuid = user.getUuid();
				isPassThroughAuthentication = true;
			}
			catch (Exception e) {
				if (!isExcludedPath) {
					Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
					if(authentication != null) {
						authentication.setAuthenticated(false);
					}
					throw e;
				}
			}
		}
		else {
			userUuid = getUserUuidFromJwt();
			if (userUuid != null) {
				stringAccessToken = "JWT";
			}
		}

		if (userUuid == null) {
			if (isExcludedPath) {
				userUuid = userService.findUserUuidById(Constants.WORKMARKET_SYSTEM_USER_ID);
				stringAccessToken = "anonymous";
			} else {
				throw new HttpException401("Not authorized to perform this request.");
			}
		}

		// JWT Authentication
		final Long userId = userService.findUserIdByUuid(userUuid);
		final User user = userService.getUser(userId);
		final UserDetails extendedUserDetails = extendedUserDetailsService.loadUser(user);
		authenticationService.setCurrentUser(userId);

		if (user.getId() != Constants.WORKMARKET_SYSTEM_USER_ID) {
			if(!isPassThroughAuthentication) {
				SecurityContextHolder.getContext().setAuthentication(new ApiAuthentication(extendedUserDetails));
			}
			// TODO API - Remove this check once we are checking roles/permissions for V1 at the service layer
			if (!isExcludedPath && isApiV1Request(request)) {
				final ExtendedUserDetails principal = (ExtendedUserDetails) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
				if (!principal.hasAnyRoles("ACL_ADMIN")) {
					throw new HttpException403("Your token is not authorized to perform this request");
				}
			}
		}

		if (debug) {
			if (user.getId() == Constants.WORKMARKET_SYSTEM_USER_ID) {
				logger.debug("API allowing unauthenticated access to {}", request.getRequestURI());
			} else {
				logger.debug("API successfully authenticated for {}: access_token=[{}], and userId={}",
					request.getRequestURI(), stringAccessToken, userId);
			}
		}

		logger.info("API " + request.getMethod() + "[" + request.getRequestURI() + "] from companyId={}",
				user.getCompany().getId());
	}

	private String getUserUuidFromJwt() {
		final WebRequestContext webRequestContext = webRequestContextProvider.getWebRequestContext();
		final String jwtToken = webRequestContext.getJwt();
		final Either<Collection<String>, Optional<JwtClaims>> either = jwtValidator.call(jwtToken);
		if (either.isLeft()) {
			logger.info("attempted JWT auth, failed validation: {}" + Joiner.on(" ,").join(either.getLeft()));
			throw new HttpException401("Not authorized to perform this request.");
		}
		final Optional<JwtClaims> jwtClaimsOptional = either.get();
		if (!jwtClaimsOptional.isPresent()) {
			return null;
		}
		final JwtClaims claims = jwtClaimsOptional.get();
		try {
			webRequestContext.setJwtClaims(claims);
			final String subject = claims.getSubject();
			logger.debug("JWT auth successful for user: {}", subject);
			return subject;
		}
		catch (final MalformedClaimException e) {
			logger.info("attempted JWT auth, claims were malformed: {}", e.getMessage());
			throw new HttpException401("Not authorized to perform this request.");
		}
	}

	private boolean isApiV1Request(HttpServletRequest request) {
		return WebUtilities.isRequestURIPrefixedByAny(request, API_V_1_PATH_PREFIXES);
	}

	private String redeemOAuthToken(final String token, final RequestContext context) {
		final RedeemOAuthTokenRequest redeemOAuthTokenRequest = RedeemOAuthTokenRequest.newBuilder()
			.setAuthToken(token)
			.build();
		try {
			Observable<RedeemOAuthTokenResponse> redeemOAuthTokenResponseObservable = authClient.redeemOAuthToken
					(redeemOAuthTokenRequest, context);
			return redeemOAuthTokenResponseObservable.toBlocking().single().getJwt();
		} catch (final RuntimeException e) {
			throw new RuntimeException("Failed to redeem auth token for jwt", e);
		}
	}
}
