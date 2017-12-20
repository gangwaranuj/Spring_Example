package com.workmarket.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import com.codahale.metrics.Meter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.workmarket.api.internal.service.ApiService;
import com.workmarket.api.internal.service.RateLimitConfigService;
import com.workmarket.api.internal.service.RateLimiterService;
import com.workmarket.api.v1.ApiHelper;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages.RedeemOAuthTokenRequest;
import com.workmarket.auth.gen.Messages.RedeemOAuthTokenResponse;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.jwt.Either;
import com.workmarket.common.jwt.JwtValidator;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.util.proto.JacksonProtoModule;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.web.helpers.MessageBundleHelperImpl;

import org.jose4j.jwt.JwtClaims;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Validator;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import edu.emory.mathcs.backport.java.util.Collections;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class BaseApiControllerTest extends ExpectApiV3Support {
  public static final TypeReference<ApiV2Response<ApiBaseError>> apiErrorResponseType = new TypeReference<ApiV2Response<ApiBaseError>>() {
  };
  public static final long DEFAULT_COMPANY_ID = 1978L;
  public static final long DEFAULT_USER_ID = 11358L;

  protected ObjectMapper jackson = new ObjectMapper()
      .registerModule(new GuavaModule())
      .registerModule(new JacksonProtoModule());

  protected MockMvc mockMvc;
  @Mock private BearerTokenExtractor tokenExtractor;
  @Mock private Authentication auth;
  @Mock protected ApiService apiService;
  @Mock private UserDAO userDAO;
  @Mock protected MessageBundleHelperImpl messageHelper;
  @Mock protected ApiHelper apiHelper;
  @Mock protected AuthenticationService authenticationService;
  @Mock private RateLimiterService rateLimiterService;
  @Mock protected ExtendedUserDetailsService extendedUserDetailsService;
  @Mock private RateLimitConfigService rateLimitConfigService;
  @Mock protected UserService userService;
  @Mock protected WebRequestContextProvider webRequestContextProvider;
  @Mock private WebRequestContext webRequestContext;
  @Mock private WMMetricRegistryFacade wmMetricRegistryFacade;
  @Mock private Meter meter;
  @Mock private JwtValidator jwtValidator;
  @Mock private AuthenticationClient authenticationClient;

  @InjectMocks protected ApiBaseInterceptor apiBaseInterceptor = new ApiBaseInterceptor();
  @InjectMocks protected ApiBaseHttpMessageConverter apiBaseHttpMessageConverter = new ApiBaseHttpMessageConverter();

  protected User user;
  private static final String[] EXCLUDED_PATHS = {"/api/v1/authorization"};

  public void setup(ApiBaseController controller) throws Exception {
    apiBaseHttpMessageConverter.getObjectMapper().registerModule(new JacksonProtoModule());
    apiBaseInterceptor.setExcludedPaths(EXCLUDED_PATHS);
    MockitoAnnotations.initMocks(this);
    mockMvc = getMockMvc(controller);
    when(tokenExtractor.extract(any(HttpServletRequest.class))).thenReturn(auth);
    when(auth.getPrincipal()).thenReturn("fakeToken");
    user = getUser();
    doReturn(user).when(userDAO).getUser(eq(user.getId()));
    when(authenticationService.getCurrentUser()).thenReturn(user);
		when(authenticationClient.redeemOAuthToken((RedeemOAuthTokenRequest) anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(RedeemOAuthTokenResponse.newBuilder().setOk(true).setJwt("valid").build()));
//    when(authenticationService.redeemOAuthToken("fakeToken", null)).thenReturn("valid");
    final JwtClaims claims = mock(JwtClaims.class);
    when(claims.getSubject()).thenReturn("useruuid");
    final Optional<JwtClaims> jwtClaimsOptional = Optional.of(claims);
    final Either<Collection<String>, Optional<JwtClaims>> value = Either.right(jwtClaimsOptional);
    when(jwtValidator.call(any(String.class))).thenReturn(value);
    when(userService.findUserIdByUuid("useruuid")).thenReturn(3L);

    when(userService.getUser(anyLong())).thenReturn(user);
    when(userService.findUserByEmail(anyString())).thenReturn(null);
    when(extendedUserDetailsService.loadUser(any(User.class))).thenReturn(getExtendedUserDetails());
    when(messageHelper.getMessage(anyString())).thenReturn("Default Mock message helper message");
    when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
    when(webRequestContextProvider.getWebRequestContext(any(String.class), any(String.class))).thenCallRealMethod();
    when(rateLimitConfigService.findByCompanyId(anyLong())).thenReturn(Collections.emptyList());
    when(rateLimiterService.increment(anyString(),
      anyInt(),
      anyLong(),
      anyLong())).thenReturn(user.getCompany().getId());
    when(wmMetricRegistryFacade.meter(anyString())).thenReturn(meter);
  }

  /**
   * Simulate being logged out by removing the mocked authentication token
   */
  public void logout() {
    when(tokenExtractor.extract(any(HttpServletRequest.class))).thenReturn(null);
    SecurityContextHolder.getContext().setAuthentication(null);
  }

  protected MockMvc getMockMvc(Object controller) {
    // TODO API - Paths
    String[] paths = {"/api/**", "/worker/v2/**", "/employer/v2/**", "/v1/**", "/v2/**", "/v3/**"};
    return standaloneSetup(controller).addMappedInterceptors(paths, apiBaseInterceptor)
      .addFilter(new Filter() {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {

        }

        @Override
        public void doFilter(ServletRequest servletRequest,
                             ServletResponse servletResponse,
                             FilterChain filterChain) throws IOException, ServletException {
          webRequestContextProvider.getWebRequestContext();
          filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {

        }
      }, "/*")
      .setMessageConverters(apiBaseHttpMessageConverter)
      .setHandlerExceptionResolvers(createExceptionResolver(controller))
      .setValidator(getValidator())
      .build();
  }

  protected Validator getValidator() {
    return null;
  }

  protected User getUser() {

    User user = new User();

    user.setId(DEFAULT_USER_ID);
    user.setUuid("fakeUser-" + DEFAULT_USER_ID);
    user.setCompany(newCompany());
    return user;
  }

  protected ExtendedUserDetails getExtendedUserDetails() {

    ExtendedUserDetails extendedUserDetails = new ExtendedUserDetails("username",
      "password",
      ImmutableList.of(new SimpleGrantedAuthority("ACL_ADMIN")));

    extendedUserDetails.setId(DEFAULT_USER_ID);
    extendedUserDetails.setCompanyId(DEFAULT_COMPANY_ID);

    return extendedUserDetails;
  }

  protected ExceptionHandlerExceptionResolver createExceptionResolver(final Object controller) {

    ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {

      protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod,
                                                                        Exception exception) {

        Method method = new ExceptionHandlerMethodResolver(ApiBaseController.class).resolveMethod(exception);
        return new ServletInvocableHandlerMethod(controller, method);
      }
    };

    exceptionResolver.getMessageConverters().add(apiBaseHttpMessageConverter);
    exceptionResolver.afterPropertiesSet();

    return exceptionResolver;
  }

  public Company newCompany() {
    Company c = new Company();
    c.setId(1978L);
    c.setName("companyName" + RandomUtilities.generateNumericString(10));
    c.setOperatingAsIndividualFlag(true);
    c.setCustomerType(Company.TEST_CUSTOMER_TYPE);
    return c;
  }

}

// http://stackoverflow.com/questions/14308341/how-to-login-a-user-with-spring-3-2-new-mvc-testing
// https://github.com/spring-projects/spring-test-mvc/blob/master/src/test/java/org/springframework/test/web/server/samples/context/SpringSecurityTests.java#L41
// https://spring.io/blog/2014/05/23/preview-spring-security-test-web-security
// http://fstyle.de/hp_fstyle/wordpress/2013/03/04/spring-test-spring-security-how-to-mock-authentication/
// http://blog.czeczotka.com/2015/01/20/spring-mvc-integration-test-with-rest-assured-and-mockmvc/
