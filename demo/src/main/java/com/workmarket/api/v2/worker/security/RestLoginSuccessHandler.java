package com.workmarket.api.v2.worker.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.onboarding.model.OnboardCompleteValidator;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.CSRFTokenService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for successful login drops the MVC forwarding mechanisms typically set up by spring security, and just
 * send back a 200 OK response with expected payload. Payload includes some basic user data as a convenience
 */
public class RestLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private static final Log logger = LogFactory.getLog(RestAuthenticationEntryPoint.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private CSRFTokenService csrfTokenService;
	@Autowired private ProfileService profileService;
	@Autowired private OnboardCompleteValidator onboardCompleteValidator;

	private class AuthenticationUserPayload {
		private String firstName;
		private String lastName;
		private String email;
		private String userNumber;
		private String phoneNumber;
		private String userId;
		private Boolean emailConfirmed;
		private Boolean onboardingComplete;

		public String getUserNumber() { return userNumber; }
		public void setUserNumber(String userNumber) { this.userNumber = userNumber; }

		public String getUserId() { return userId; }
		public void setUserId(String userId) { this.userId = userId; }

		public String getFirstName() { return firstName; }
		public void setFirstName(String firstName) { this.firstName = firstName; }

		public String getLastName() { return lastName; }
		public void setLastName(String lastName) { this.lastName = lastName; }

		public String getEmail() { return email; }
		public void setEmail(String email) {this.email = email;}

		public String getPhoneNumber() { return phoneNumber; }
		public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

		public Boolean isEmailConfirmed() { return emailConfirmed; }

		public void setEmailConfirmed(Boolean emailConfirmed) { this.emailConfirmed = emailConfirmed; }
		
		public Boolean getOnboardingComplete() {
			return onboardingComplete;
		}

		public void setOnboardingComplete(Boolean onboardingComplete) {
			this.onboardingComplete = onboardingComplete;
		}
	}


	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		clearAuthenticationAttributes(request);

		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setStatus(HttpServletResponse.SC_OK);

		ApiJSONPayloadMap metadataBuilder = new ApiJSONPayloadMap();
		metadataBuilder.put("code", HttpServletResponse.SC_OK);


		ApiV2Response responsePayload =
				new ApiV2Response(metadataBuilder,
													ImmutableList.of(marshallUserToJsonPayload(), buildCSRFTokenMap(request)),
													null);
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		PrintWriter writer = response.getWriter();
		writer.write(jsonMapper.writeValueAsString(responsePayload));
		writer.flush();
		writer.close();

		new LoginTracker().logDetailsOfSigninSuccess(request, "API");
	}

	private AuthenticationUserPayload marshallUserToJsonPayload() {
		User user = authenticationService.getCurrentUser();

		AuthenticationUserPayload userPayload = new AuthenticationUserPayload();
		userPayload.setUserId(String.valueOf(user.getId()));
		userPayload.setUserNumber(user.getUserNumber());
		userPayload.setFirstName(user.getFirstName());
		userPayload.setLastName(user.getLastName());
		userPayload.setEmail(user.getEmail());
		userPayload.setPhoneNumber(user.getProfile().getMobilePhone() == null ? user.getProfile().getWorkPhone() :
			user.getProfile().getMobilePhone());
		userPayload.setEmailConfirmed(BooleanUtils.toBoolean(authenticationService.getEmailConfirmed(user)));

		Profile profile = profileService.findProfile(user.getId());
		Company company = profileService.findCompany(user.getId());

		if (profile != null && company != null) {
			userPayload.setOnboardingComplete(onboardCompleteValidator.validateMobile(profile, company));
		}
		else {
			userPayload.setOnboardingComplete(false);
		}

		return userPayload;
	}

	private Map buildCSRFTokenMap(HttpServletRequest request) {
			Map tokenMap = new HashMap();
			tokenMap.put("CSRFToken", csrfTokenService.getTokenFromSession(request, false));

			return tokenMap;
	}

}
