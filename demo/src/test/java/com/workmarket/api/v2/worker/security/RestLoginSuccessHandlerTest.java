package com.workmarket.api.v2.worker.security;

import com.workmarket.api.ExpectApiV3Support;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.onboarding.model.OnboardCompleteValidator;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.CSRFTokenService;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.TestingAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RestLoginSuccessHandlerTest extends ExpectApiV3Support {

	public static final String EXPECTED_CSRF_TOKEN = "ct1298sijq98sfk-12-0idsfm";
	public static final String EXPECTED_EMAIL = "jeb@skeezics.com";
	MockHttpServletRequest request;
	MockHttpServletResponse response;
	Authentication authStub;  // not used by handler right now, exists here simply to satisfy parameter needs
	@Mock AuthenticationService authenticationService;
	@Mock CSRFTokenService csrfTokenService;
	@Mock OnboardCompleteValidator onboardCompleteValidator;
	@Mock ProfileService profileService;

	@InjectMocks
	RestLoginSuccessHandler handler = new RestLoginSuccessHandler();

	User user;
	Profile profile;

	private static final String AUTH_PAYLOAD =
		"{\"meta\":{\"code\":200},\"results\":[{\"firstName\":\"Jebediah\",\"lastName\":\"Alhambra\"," +
			"\"email\":\"jeb@skeezics.com\",\"userNumber\":\"12345678\",\"phoneNumber\":\"5555257018\"," +
			"\"userId\":\"87654321\"},{\"CSRFToken\":\"ct1298sijq98sfk-12-0idsfm\"}]}";


	@Before
	public void setup() {
		user = new User();
		user.setFirstName("Jebediah");
		user.setLastName("Alhambra");
		user.setEmail(EXPECTED_EMAIL);
		user.setUserNumber("12345678");
		user.setId(87654321L);

		when(authenticationService.getEmailConfirmed(any(User.class))).thenReturn(Boolean.TRUE);
		when(authenticationService.getCurrentUser()).thenReturn(user);

		when(csrfTokenService.getTokenFromSession((HttpServletRequest) anyObject(), eq(false)))
				.thenReturn(EXPECTED_CSRF_TOKEN);

		profile = new Profile();
		profile.setWorkPhone("5555257018");
		user.setProfile(profile);

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		authStub = new TestingAuthenticationToken(null, null);
	}

	@Test
	public void login_succeeds_profileIncomplete() {

		Profile profile = new Profile();
		Company company = new Company();
		when(profileService.findProfile(any(Long.class))).thenReturn(profile);
		when(profileService.findCompany(any(Long.class))).thenReturn(company);

		when(onboardCompleteValidator.validateMobile(eq(profile), eq(company))).thenReturn(false);

		try {
			handler.onAuthenticationSuccess(request, response, authStub);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		assertEquals("application/json;charset=UTF-8", response.getContentType());
		assertEquals("no-cache", response.getHeader("Cache-Control"));

		try {
			ApiV2Response apiResponse = expectApiV2Response(response.getContentAsString());
			expectApiV3ResponseMetaSupport(apiResponse.getMeta());
			expectStatusCode(HttpStatus.SC_OK, apiResponse.getMeta());
			assertEquals("Expect two objects in response list", 2, apiResponse.getResults().size());

			Map userMap = (Map)apiResponse.getResults().get(0);
			Map tokenMap = (Map)apiResponse.getResults().get(1);
			
			assertEquals("Expect user firstName to match", user.getFirstName(), userMap.get("firstName"));
			assertEquals("Expect user lastName to match", user.getLastName(), userMap.get("lastName"));
			assertEquals("Expect user email to match", user.getEmail(), userMap.get("email"));
			assertEquals("Expect user userNumber to match", user.getUserNumber(), userMap.get("userNumber"));
			assertEquals("Expect user phoneNumber to match", user.getProfile().getWorkPhone(), userMap.get("phoneNumber"));
			assertEquals("Expect user id to match", user.getId().toString(), userMap.get("userId"));
			assertEquals("Expect token CSRFToken to match", EXPECTED_CSRF_TOKEN, tokenMap.get("CSRFToken"));
			assertEquals("Expect user emailConfirmed to match", authenticationService.getEmailConfirmed(user), userMap.get("emailConfirmed"));
			assertEquals("Expect user onboardingComplete to match", false, userMap.get("onboardingComplete"));
		} catch (Exception e) {
			fail("Exception getting response content");
		}
	}

	@Test
	public void login_succeeds_profileComplete() {
		Profile profile = new Profile();
		Company company = new Company();
		when(profileService.findProfile(any(Long.class))).thenReturn(profile);
		when(profileService.findCompany(any(Long.class))).thenReturn(company);

		when(onboardCompleteValidator.validateMobile(eq(profile), eq(company))).thenReturn(true);
		try {
			handler.onAuthenticationSuccess(request, response, authStub);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		assertEquals("application/json;charset=UTF-8", response.getContentType());
		assertEquals("no-cache", response.getHeader("Cache-Control"));

		try {
			ApiV2Response apiResponse = expectApiV2Response(response.getContentAsString());
			expectApiV3ResponseMetaSupport(apiResponse.getMeta());
			expectStatusCode(HttpStatus.SC_OK, apiResponse.getMeta());
			assertEquals("Expect two objects in response list", 2, apiResponse.getResults().size());

			Map userMap = (Map)apiResponse.getResults().get(0);
			Map tokenMap = (Map)apiResponse.getResults().get(1);

			assertEquals("Expect user firstName to match", user.getFirstName(), userMap.get("firstName"));
			assertEquals("Expect user lastName to match", user.getLastName(), userMap.get("lastName"));
			assertEquals("Expect user email to match", user.getEmail(), userMap.get("email"));
			assertEquals("Expect user userNumber to match", user.getUserNumber(), userMap.get("userNumber"));
			assertEquals("Expect user phoneNumber to match", user.getProfile().getWorkPhone(), userMap.get("phoneNumber"));
			assertEquals("Expect user id to match", user.getId().toString(), userMap.get("userId"));
			assertEquals("Expect token CSRFToken to match", EXPECTED_CSRF_TOKEN, tokenMap.get("CSRFToken"));
			assertEquals("Expect user emailConfirmed to match", authenticationService.getEmailConfirmed(user), userMap.get("emailConfirmed"));
			assertEquals("Expect user onboardingComplete to match", true, userMap.get("onboardingComplete"));
		} catch (Exception e) {
			fail("Exception getting response content");
		}
	}
}
