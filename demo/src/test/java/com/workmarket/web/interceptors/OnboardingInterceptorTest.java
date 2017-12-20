package com.workmarket.web.interceptors;

import com.google.common.collect.ImmutableMap;
import com.workmarket.api.ApiAuthentication;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.business.ProfileService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Map;

import static com.workmarket.web.interceptors.OnboardingInterceptor.EXCLUDED_PATHS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ianha on 9/11/14
 */
@RunWith(MockitoJUnitRunner.class)
public class OnboardingInterceptorTest {
	@Mock FeatureEvaluator featureEvaluator;
	@Mock SecurityContextFacade securityContext;
	@Mock ProfileService profileService;
	@Mock HttpServletResponse response;
	@Mock HttpServletRequest request;
	@Mock ExtendedUserDetails user;
	@InjectMocks OnboardingInterceptor onboardingInterceptor;

	@Before
	public void setUp() throws Exception {
		when(user.getPersonaPreference()).thenReturn(null);
		when(user.getAuthorities()).thenReturn(Collections.<GrantedAuthority>emptyList());
		SecurityContextHolder.getContext().setAuthentication(new ApiAuthentication(user));
		when(securityContext.getCurrentUser()).thenReturn(user);
	}

	@Test
	public void itShouldNotDirectToOnboardingIfYouDoNotHaveAPersona() throws Exception {
		when(request.getRequestURI()).thenReturn("/someUrl");
		when(request.getContextPath()).thenReturn("");
		onboardingInterceptor.preHandle(request, response, null);
		verify(response, never()).sendRedirect("/onboarding");
		verify(response, never()).sendRedirect("/");
	}

	@Test
	public void itShouldNotDirectToOnboardingIfThePathsAreExcluded() throws Exception {
		when(request.getRequestURI()).thenReturn(EXCLUDED_PATHS[0]);
		when(request.getContextPath()).thenReturn(EXCLUDED_PATHS[0]);
		assertTrue("Expect that onboardingInterceptor doesn't care about excluded paths", onboardingInterceptor.preHandle(request, response, null));
		verify(response, never()).sendRedirect("/onboarding");
		verify(response, never()).sendRedirect("/");
	}

	@Test
	public void itShouldRedirectToOnboarding() throws Exception {
		when(request.getRequestURI()).thenReturn("/somePage");
		when(request.getContextPath()).thenReturn("");
		ImmutableMap<String, Object> propsMap = ImmutableMap.of("onboardCompleted", (Object)"false");
		when(profileService.getProjectionMapByUserNumber(any(String.class), any(String.class))).thenReturn(propsMap);
		PersonaPreference personaPreference = new PersonaPreference();
		personaPreference.setSeller(true);
		when(user.getPersonaPreference()).thenReturn(personaPreference);
		assertFalse("Expect that onboardingInterceptor redirects to onboarding", onboardingInterceptor.preHandle(request, response, null));
		verify(response, times(1)).sendRedirect("/onboarding");
	}

	@Test
	public void itShouldPassThroughToOnboarding_ajaxOrJsp() throws Exception {
		when(request.getRequestURI()).thenReturn("/onboarding.jsp");
		when(request.getContextPath()).thenReturn("");
		ImmutableMap<String, Object> propsMap = ImmutableMap.of("onboardCompleted", (Object)"false");
		when(profileService.getProjectionMapByUserNumber(any(String.class), any(String.class))).thenReturn(propsMap);
		PersonaPreference personaPreference = new PersonaPreference();
		personaPreference.setSeller(true);
		when(user.getPersonaPreference()).thenReturn(personaPreference);
		assertTrue("Expect that onboardingInterceptor redirects to home", onboardingInterceptor.preHandle(request, response, null));
		verify(response, never()).sendRedirect("/onboarding");
		verify(response, never()).sendRedirect("/");
	}

	@Test
	public void itShouldPassThroughToOnboarding() throws Exception {
		when(request.getRequestURI()).thenReturn("/onboarding");
		when(request.getContextPath()).thenReturn("");
		ImmutableMap<String, Object> propsMap = ImmutableMap.of("onboardCompleted", (Object)"false");
		when(profileService.getProjectionMapByUserNumber(any(String.class), any(String.class))).thenReturn(propsMap);
		PersonaPreference personaPreference = new PersonaPreference();
		personaPreference.setSeller(true);
		when(user.getPersonaPreference()).thenReturn(personaPreference);
		assertTrue("Expect that onboardingInterceptor redirects to home", onboardingInterceptor.preHandle(request, response, null));
		verify(response, never()).sendRedirect("/onboarding");
		verify(response, never()).sendRedirect("/");
	}

	@Test
	public void itShouldRedirectToHome() throws Exception {
		when(request.getRequestURI()).thenReturn("/onboarding");
		when(request.getContextPath()).thenReturn("");
		ImmutableMap<String, Object> propsMap = ImmutableMap.of("onboardCompleted", (Object)"true");
		when(profileService.getProjectionMapByUserNumber(any(String.class), any(String.class))).thenReturn(propsMap);
		PersonaPreference personaPreference = new PersonaPreference();
		personaPreference.setSeller(true);
		when(user.getPersonaPreference()).thenReturn(personaPreference);
		assertFalse("Expect that onboardingInterceptor redirects to home", onboardingInterceptor.preHandle(request, response, null));
		verify(response, times(1)).sendRedirect("/");
	}
}
