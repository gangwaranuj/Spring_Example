package com.workmarket.web.interceptors;

import com.workmarket.service.business.ProfileService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.utility.WebUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;

public class OnboardingInterceptor extends ExcludableInterceptor {

	public static final String[] EXCLUDED_PATHS = {"/v1", "/v2", "/v3", "/api/v1", "/worker/v2", "/employer/v2"};
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private SecurityContextFacade securityContext;
	@Autowired private ProfileService profileService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		setExcludedPaths(EXCLUDED_PATHS);
		String[] onboardingPaths = {"/onboarding"};

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ExtendedUserDetails user = securityContext.getCurrentUser();

		if (auth != null && user != null) {
			Map<String, Object> props = profileService.getProjectionMapByUserNumber(user.getUserNumber(), "onboardCompleted"); // needs to go into memory
			boolean isOnboardingComplete = props.containsKey("onboardCompleted") && "true".equals(props.get("onboardCompleted").toString());
			boolean shouldGoToOnboarding = (user.getPersonaPreference() != null && user.getPersonaPreference().isSeller()) && !isOnboardingComplete;

			// IF the request is excluded, move on
			if (isExcluded(request)) {
				return true;
			}

			// If we are going to an onboarding path
			if(WebUtilities.isRequestURIPrefixedByAny(request, onboardingPaths)) {
				// And this isn't ajax, and we don't need to go to onboarding
				if (WebUtilities.isPageRequest(request) && !shouldGoToOnboarding) {
					// Redirect to homepage
					String referer = request.getHeader("Referer");
					response.sendRedirect("/");

					if (referer != null) {
						URI refererUri = new URI(referer);
						String workMarketHostName = InetAddress.getLocalHost().getHostName(); // workmarket.com or localhost

						if (refererUri.getHost().equals(workMarketHostName) && !refererUri.getPath().equals("/login")) {
							response.sendRedirect(refererUri.getPath());
						}
					}

					return false;
				}
			}
			// We are not going to an onboarding path
			else {
				// But if we should
				if (shouldGoToOnboarding) {
					// Redirect to onboarding
					response.sendRedirect("/onboarding");
					return false;
				}
			}
		}

		return true;
	}
}
