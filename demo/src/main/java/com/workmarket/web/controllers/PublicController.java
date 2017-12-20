package com.workmarket.web.controllers;

import com.codahale.metrics.MetricRegistry;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.IndustryPagination;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.dto.PublicPageProfileDTO;
import com.workmarket.service.business.*;
import com.workmarket.service.business.dto.InvitationUserRegistrationDTO;
import com.workmarket.service.exception.authentication.EmailNotConfirmedException;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.EncryptionService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.GoogleCalendarService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.business.PublicInfoService;
import com.workmarket.service.web.AssignmentStatusImageService;
import com.workmarket.service.web.ImageAsset;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.WebUtilities;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.forms.ForgotUserPasswordForm;
import com.workmarket.web.forms.RegisterUserForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.FeedRequestParamsValidator;
import com.workmarket.web.validators.PasswordValidator;
import com.workmarket.web.validators.UserEmailValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class PublicController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PublicController.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private RegistrationService registrationService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private UserService userService;
	@Autowired private WorkService workService;
	@Autowired private PasswordValidator passwordValidator;
	@Autowired private UserEmailValidator userEmailValidator;
	@Autowired private WorkFollowService workFollowService;
	@Autowired private LinkedInService linkedInService;
	@Autowired private LinkedInOAuthService linkedInOAuthService;
	@Autowired private SocialService socialService;
	@Autowired private GoogleCalendarService googleCalendarService;
	@Autowired private PublicInfoService publicInfoService;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private AssignmentStatusImageService assignmentStatusImageService;
	@Autowired private EncryptionService encryptionService;
	@Autowired private SalesforceLeadService salesforceLeadService;
	@Autowired private AdmissionService admissionService;
	@Autowired private MetricRegistry registry;
	@Autowired private FeatureEntitlementService featureEntitlementService;

	private MetricRegistryFacade facade;

	private static final int DISPLAYED_PROFILES = 27;

	private static final String INDUSTRY_ONE = "Retail_Merchandising";
	private static final String INDUSTRY_TWO = "IT_Services";
	private static final String INDUSTRY_THREE = "Field_Marketing";
	private static final String WM_LOGIN_MESSAGE = "WM_LOGIN_MESSAGE";
	private static final String SPRING_SECURITY_LAST_EXCEPTION = "SPRING_SECURITY_LAST_EXCEPTION";

	private static final List<String> INDUSTRIES = new ImmutableList.Builder<String>()
		.add(INDUSTRY_ONE)
		.add(INDUSTRY_TWO)
		.add(INDUSTRY_THREE)
		.build();

	private static final ImmutableMap<String, Integer> WARP_CAMPAIGN_MAPPINGS = initWarpCampaignMappings();

	@PostConstruct
	public void init() {
		facade = new WMMetricRegistryFacade(registry, "publiccontroller");
	}

	private void populateIndex(Model model) {
		IndustryPagination pagination = new IndustryPagination();
		pagination.setReturnAllRows();
		pagination = invariantDataService.findAllIndustries(pagination);
		List<Industry> industries = pagination.getResults();

		model.addAttribute("industryList", industries);
	}

	private void populateCallingCodes(Model model) {
		List<CallingCode> callingCodes = invariantDataService.findAllActiveCallingCodes();
		model.addAttribute("callingCodesList", callingCodes);
	}

	private void populateBuyersSignupModel(Model model, RegisterUserForm form) {
		populateIndex(model);
		populateCallingCodes(model);
		form.setRegistrationType(Constants.REGISTER_RESOURCE_MANAGE_LABOR);
		model.addAttribute("newUser", form);
		model.addAttribute("defaultIndustryId", Industry.GENERAL.getId());
	}

	private void setClientReferer(HttpServletRequest request) {
		request.getSession().setAttribute("warpRequisitionId", getClientRefererFromHeaders(request));
	}

	private Integer getClientReferer(HttpServletRequest request) {
		Integer clientId = (Integer) request.getSession().getAttribute("warpRequisitionId");
		//if not in session, check one more time in headers
		return clientId == null ? getClientRefererFromHeaders(request) : clientId;
	}

	private Integer getClientRefererFromHeaders(HttpServletRequest request) {
		String initialReferer = "";

		for(Cookie c : request.getCookies()) {
			if(c.getName().endsWith("_mixpanel")) {
				initialReferer = c.getValue();
			}
		}

		String referer = StringUtils.defaultIfEmpty(request.getHeader("Referer"), StringUtils.EMPTY).toLowerCase();
		for (String key : WARP_CAMPAIGN_MAPPINGS.keySet()) {
			if(referer.contains(key) || initialReferer.contains(key)) {
				return WARP_CAMPAIGN_MAPPINGS.get(key);
			}
		}

		return null;
	}

	private static ImmutableMap<String, Integer> initWarpCampaignMappings() {
		return new ImmutableMap.Builder()
				.put("nsba", 41)
				.put("devry", 42)
				.put("bww1", 43)
				.put("bww2", 44)
				.put("bww3", 45)
				.put("bww4", 46)
				.put("bww5", 47)
				.put("bww6", 48)
				.put("bww7", 49)
				.put("bww8", 50)
				.put("bww9", 51)
				.put("bww10", 52)
				.build();
	}

	@RequestMapping(
		value = {"", "/"},
		method = GET)
	public String index(
		HttpServletRequest request,
		SitePreference site) {

		if (isAuthenticated()) {
			return "forward:/home";
		}

		return "redirect:/login";
	}

	@RequestMapping(value = "/login")
	public String login(Model model, HttpServletRequest request, SitePreference site) {
		facade.meter("login").mark();
		if (isAuthenticated()) {
			return "redirect:/home";
		}

		final boolean hasLocaleFeature = featureEntitlementService.hasFeatureToggle(Constants.WORKMARKET_SYSTEM_USER_ID, "locale");

		model.addAttribute("hasLocaleFeature", hasLocaleFeature);
		model.addAttribute("forgotUserPassword", new ForgotUserPasswordForm());
		model.addAttribute("hideHeaderLogin", true);

		return "web/pages/public/login";
	}

	@RequestMapping(
		value = "/calendar/callback",
		method = GET)
	public String googleCalendarCallback(HttpServletRequest request) throws Throwable {
		String authCode = request.getParameter("code");
		googleCalendarService.authorizeWMFromAuthCode(authCode, getCurrentUser().getId());
		return "web/pages/public/calendarcb";
	}

	// We only get here if social login was successful,
	// but no local email was found
	@RequestMapping(
		value = {"/login/social_no_link", "/social/login/social_no_link"},
		method = GET)
	public String socialNoLink(HttpServletRequest request) {
		facade.meter("social_no_link").mark();
		HttpSession session = request.getSession();
		if (StringUtils.isNotEmpty((String) session.getAttribute("signup"))) {
			return socialService.processSocialSignup(request);
		} else {
			return socialService.processSocialNoLink(request);
		}
	}

	// Mobile Signup Decision Point
	@RequestMapping(
		value = "/signup",
		method = GET)
	public String mobileSignup(
		Model model,
		@ModelAttribute("signupForm") RegisterUserForm form,
		HttpServletRequest request,
		SitePreference site) {
		facade.meter("signup").mark();
		model.addAttribute("newUser", form);
		setClientReferer(request);

		return (isMobile(request, site)) ? "redirect:/findwork" : "redirect:/signup/creatework";
	}

	// Buyer Signup
	@RequestMapping(
		value = "/signup/creatework",
		method = GET)
	public String buyerSignup(
		Model model,
		@ModelAttribute("signupForm") RegisterUserForm form,
		HttpServletRequest request,
		SitePreference site
	) {
		facade.meter("signupcreatework").mark();

		populateBuyersSignupModel(model, form);

		return (isMobile(request, site)) ? "mobile/pages/v2/public/signupgeneral" : "web/pages/public/signupgeneral";
	}

	@RequestMapping(
		value = {"/signup/creatework", "/signup-submit/creatework"},
		method = POST)
	public String doBuyerSignup(
		Model model,
		@Valid @ModelAttribute("signupForm") RegisterUserForm form,
		BindingResult bindingResult,
		HttpServletRequest request,
		SitePreference site,
		RedirectAttributes redirectAttributes) throws Exception {
		facade.meter("signupcreatework-submit").mark();

		String redirectTo = (isMobile(request, site)) ? "mobile/pages/v2/public/signupgeneral" : "web/pages/public/signupgeneral";
		String redirectToSuccess = "redirect:/thankyou";
		MessageBundle messages = messageHelper.newBundle(model);
		boolean isAjaxRequest = WebUtilities.isAjax(request);
		bindingResult.recordSuppressedField("addressTyper");
		bindingResult.pushNestedPath("password");
		passwordValidator.validate(form.getPassword(), form.getUserEmail(), bindingResult);
		bindingResult.popNestedPath();
		bindingResult.pushNestedPath("userEmail");
		userEmailValidator.validate(form.getUserEmail(), bindingResult);
		bindingResult.popNestedPath();

		if (isAjaxRequest) {
			redirectToSuccess = redirectTo;
		}

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			populateBuyersSignupModel(model, form);
			if (isAjaxRequest) {
				model.addAttribute("messages", messages);
				model.addAttribute("successful", false);
			}

			return redirectTo;
		}

		InvitationUserRegistrationDTO dto = form.toInvitationUserRegistrationDTO();
		model.addAttribute("defaultIndustryId", Industry.GENERAL.getId());

		try {
			authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
			if (StringUtils.length(dto.getPostalCode()) > Constants.POSTAL_CODE_MAX_LENGTH) {
				messageHelper.setErrors(messages, bindingResult);
				populateBuyersSignupModel(model, form);
				if (isAjaxRequest) {
					model.addAttribute("messages", messages);
					model.addAttribute("successful", false);
				}

				return redirectTo;
			}

			User newUser = registrationService.registerUserSimple(dto, true);
			linkedInService.attemptToLinkUser(request.getSession().getId(), newUser.getId());
			// remove requestToken from session in case they come right back in
			request.getSession().removeAttribute("requestToken");
			String userNumber = newUser.getUserNumber();
			redirectAttributes.addFlashAttribute("userNumber", userNumber);
			redirectAttributes.addFlashAttribute("thankyouType", "client");
			if (isAjaxRequest) {
				messages.addSuccess(userNumber);
				model.addAttribute("messages", messages);
				model.addAttribute("successful", true);
			}

			String authToken = salesforceLeadService.authenticateToken();
			salesforceLeadService.generateBuyerLead(authToken, form);

			admissionService.saveAdmissionForCompanyIdAndVenue(newUser.getCompany().getId(), Venue.TRANSACTIONAL);
			return redirectToSuccess;

		} catch (Exception e) {
			messageHelper.addError(messages, e.getMessage());
			model.addAttribute("messages", messages);
			populateBuyersSignupModel(model, form);
			if (isAjaxRequest) {
				model.addAttribute("successful", false);
			}

			return redirectTo;
		}
	}

	// Worker Signup
	@RequestMapping(
		value = "/findwork",
		method = GET)
	public String findWork(
		Model model,
		HttpServletRequest request,
		@ModelAttribute("signupForm") RegisterUserForm form,
		SitePreference site) {
		facade.meter("findwork").mark();

		model.addAttribute("newUser", form);
		model.addAttribute("defaultIndustryId", Industry.NONE.getId());
		setClientReferer(request);

		return (isMobile(request, site)) ? "mobile/pages/v2/public/signupgeneral" : "web/pages/public/findwork";
	}

	@RequestMapping(
		value = "/findwork",
		method = POST)
	public String doFindWorkSignup(
		Model model,
		@Valid @ModelAttribute("signupForm") RegisterUserForm form,
		BindingResult bindingResult,
		HttpServletRequest request,
		SitePreference site,
		RedirectAttributes redirectAttributes) throws Exception {
		facade.meter("findwork-signup").mark();

		String redirectTo = (isMobile(request, site)) ? "mobile/pages/v2/public/signupgeneral" : "web/pages/public/findwork";
		MessageBundle messages = messageHelper.newBundle(model);
		bindingResult.recordSuppressedField("addressTyper");
		bindingResult.pushNestedPath("password");
		passwordValidator.validate(form.getPassword(), form.getUserEmail(), bindingResult);
		bindingResult.popNestedPath();
		model.addAttribute("defaultIndustryId", Industry.NONE.getId());

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			model.addAttribute("newUser", form);

			return redirectTo;
		}

		Integer warpRequisitionId = getClientReferer(request);

		InvitationUserRegistrationDTO dto = form.toInvitationUserRegistrationDTO();
		if (StringUtils.isNotEmpty(form.getCampaignId())) {
			dto.setCampaignId(encryptionService.decryptId(form.getCampaignId()));
		}

		if(warpRequisitionId != null) {
			dto.setWarpRequisitionId(warpRequisitionId);
		}

		try {
			authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

			User newUser = registrationService.registerWorker(dto);
			linkedInService.attemptToLinkUser(request.getSession().getId(), newUser.getId());
			// remove requestToken from session in case they come right back in
			request.getSession().removeAttribute("requestToken");
			redirectAttributes.addFlashAttribute("userNumber", newUser.getUserNumber());
			redirectAttributes.addFlashAttribute("thankyouType", "worker");

			return "redirect:/thankyou";

		} catch (Exception e) {
			messageHelper.addError(messages, e.getMessage());
			model.addAttribute("messages", messages);
			model.addAttribute("newUser", form);

			return redirectTo;
		}
	}

	@RequestMapping(
		value = "/thankyou",
		method = GET)
	public String thankyou(
		@ModelAttribute("userNumber") String userNumber,
		@ModelAttribute("thankyouType") String thankyouType,
		Model model,
		HttpServletRequest request,
		SitePreference site) {

		model.addAttribute("userNumber", userNumber);
		model.addAttribute("thankyouType", thankyouType);
		model.addAttribute("feedValidationConstants", FeedRequestParamsValidator.VALIDATION_CONSTANTS);

		return (isMobile(request, site)) ? "mobile/pages/v2/public/thankyou" : "web/pages/public/thankyou";
	}

	// Signup via Social Networks
	@RequestMapping(
		value = {"/login/linkedin", "/signup/linkedin", "/social/login/linkedin", "/social/signup/linkedin"},
		method = GET)
	public String signupLinkedIn(HttpServletRequest request) throws UnsupportedEncodingException, MalformedURLException {
		String oauthCallback = "";
		String nextStep = "";
		facade.meter("signup-linkedin").mark();

		if (request.getRequestURI().contains("login")) {
			oauthCallback = "/social/login/linkedin_finish";
			nextStep = "/social/login/linkedin_step2";
		} else if (request.getRequestURI().contains("social/signup")) {
			oauthCallback = "/find-work/sign-up";
			nextStep = "/social/signup/save_linkedin";
		} else if (request.getRequestURI().contains("signup")) {
			oauthCallback = "/findwork";
			nextStep = "/signup/save_linkedin";
		}
		URL url = RequestUtils.absoluteURL(request, nextStep + "?oauth_callback=" + oauthCallback);
		LinkedInRequestToken requestToken;
		requestToken = linkedInOAuthService.getOAuthRequestToken(url.toExternalForm());
		request.getSession().setAttribute("requestToken", requestToken);

		return "redirect:" + requestToken.getAuthorizationUrl();
	}

	@RequestMapping(
		value = "/social/login/linkedin_step2",
		method = GET)
	public String linkedInLoginStep2(
		@RequestParam(value = "oauth_verifier", required = false) String oauthVerifier,
		@RequestParam(value = "oauth_problem", required = false) String oauthProblem,
		@RequestParam(value = "oauth_callback", required = false) String oauthCallback,
		@RequestParam(value = "oauth_token", required = false) String oauthToken,
		HttpServletRequest request) {
		HttpSession session = request.getSession();
		facade.meter("signup-linkedin-step2").mark();

		linkedInService.authorize(
			(LinkedInRequestToken) session.getAttribute("requestToken"),
			session.getId(),
			oauthVerifier
		);

		return "redirect:/social/login/linkedin_finish?oauth_token=" + oauthToken;
	}

	@RequestMapping(
		value = {"/signup/save_linkedin",
			"/social/signup/save_linkedin"},
		method = GET)
	public String saveLinkedIn(
		@RequestParam(value = "oauth_verifier", required = false) String oauthVerifier,
		@RequestParam(value = "oauth_problem", required = false) String oauthProblem,
		@RequestParam(value = "oauth_callback", required = false) String oauthCallback,
		HttpServletRequest request) throws UnsupportedEncodingException, LinkedInServiceImpl.LinkedInImportFailed {
		LinkedInPerson linkedInPerson;
		String qStr = "";
		facade.meter("signup-linkedin-save").mark();

		if (oauthProblem != null) {
			return "redirect:" + oauthCallback;
		}

		HttpSession session = request.getSession();

		boolean authorized = linkedInService.authorize(
			(LinkedInRequestToken) session.getAttribute("requestToken"),
			session.getId(),
			oauthVerifier
		);

		if (authorized) {
			linkedInPerson = linkedInService.importPersonDataForAnonymous(request.getSession().getId());
			if (linkedInPerson != null) {
				qStr = "?" +
					"firstName=" + StringUtilities.urlEncode(linkedInPerson.getFirstName()) +
					"&lastName=" + StringUtilities.urlEncode(linkedInPerson.getLastName()) +
					"&pictureUrl=" + StringUtilities.urlEncode(linkedInPerson.getPictureUrl()) +
					"&userEmail=" + StringUtilities.urlEncode(linkedInPerson.getEmailAddress()) +
					"&workPhone=" + StringUtilities.urlEncode(linkedInPerson.getMobileOrOtherPhoneNumber()) +
					"&postalCode=" + StringUtilities.urlEncode(linkedInPerson.getPostalCode());
			}
		}
		if (request.getRequestURI().contains("social/signup")) {
			return "redirect:/find-work/sign-up" + qStr;
		}

		return "redirect:/findwork" + qStr;
	}

	// These two are in order to know that this was triggered from the signup
	// page since signin and signup go to the same place with Spring Social
	@RequestMapping(
		value = {"/signup/facebook", "/social/signup/facebook"},
		method = POST)
	public String facebookSignup(HttpServletRequest request) {
		request.getSession().setAttribute("signup", "true");
		// /signin/facebook hits the Spring Social OAuth flow
		facade.meter("signup-facebook").mark();
		return "forward:/signin/facebook";
	}

	@RequestMapping(
		value = {"/login/facebook", "/social/login/facebook"},
		method = POST)
	public String facebookLogin(HttpServletRequest request) {
		request.getSession().removeAttribute("signup");
		// /signin/facebook hits the Spring Social OAuth flow
		facade.meter("login-facebook").mark();

		return "forward:/signin/facebook";
	}

	@RequestMapping(
		value = {"/signup/google", "/social/signup/google"},
		method = POST)
	public String googleSignup(HttpServletRequest request) {
		request.getSession().setAttribute("signup", "true");
		facade.meter("signup-google").mark();

		return "forward:/signin/google";
	}

	@RequestMapping(
		value = {"/login/google", "/social/login/google"},
		method = POST)
	public String googleLogin(HttpServletRequest request) {
		request.getSession().removeAttribute("signup");
		facade.meter("login-google").mark();
		return "forward:/signin/google";
	}

	// TODO API - To be removed and replaced by /v2/resend_confirmation_email endpoint
	@RequestMapping(
		value = "/resend_confirmation_email/{userNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	String resendConfirmationEmail(
		@PathVariable("userNumber") String userNumber,
		@ModelAttribute("bundle") MessageBundle messages,
		HttpServletRequest request) {
		Long uid = userService.findUserId(userNumber);
		boolean isAjaxRequest = WebUtilities.isAjax(request);
		facade.meter("resend-conf-email").mark();

		try {
			registrationService.sendRemindConfirmationEmail(uid);
			messageHelper.addSuccess(messages, "user.confirmation.resend.success");
			if (isAjaxRequest) {
				return messages.getSuccess().get(0);
			}
		} catch (Exception e) {
			messageHelper.addError(messages, "user.confirmation.resend.failure");
			if (isAjaxRequest) {
				return messages.getErrors().get(0);
			}
		}

		return "web/pages/error/not_confirmed";
	}

	@RequestMapping(value = "/passwordreset")
	public String passwordReset(HttpServletRequest request, SitePreference site) {
		facade.meter("password-reset").mark();

		return (isMobile(request, site)) ? "mobile/pages/v2/public/passwordreset" : "web/pages/public/passwordreset";
	}

	// Display assignment status image
	@RequestMapping(value = "/assignment_status/{workNumber}/{encryptedUserId}", method = GET)
	public void getAssignmentStatusImage(
		HttpServletResponse response,
		@PathVariable String workNumber,
		@PathVariable String encryptedUserId
	) {

		User user = userService.findUserByEncryptedId(encryptedUserId);
		ImageAsset imageAsset = assignmentStatusImageService.getImageAsset(user, workNumber);

		try {
			OutputStream outputStream = response.getOutputStream();
			outputStream.write(imageAsset.getData(), 0, imageAsset.size());
			outputStream.close();
		} catch (IOException e) {
			LOGGER.error("Could not write file", e);
		}

		response.setContentType(MimeType.IMAGE_PNG.toString());
		response.addHeader("Cache-Control", "no-cache, must-revalidate");
		response.addHeader("Expires", "Sat, 26 Jul 1997 05:00:00 GMT");
	}

	@RequestMapping(
		value = "/unfollow/{encryptedWorkFollowId}",
		method = GET)
	public String unfollow(@PathVariable String encryptedWorkFollowId, Model model) {
		WorkFollow workFollow = workFollowService.findWorkFollowByEncryptedId(encryptedWorkFollowId);

		if (workFollow == null) {
			return "redirect:/home";
		}

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		workFollow.setDeleted(true);
		workFollowService.saveOrUpdateWorkFollow(workFollow);

		Work work = workService.findWork(workFollow.getWork().getId());

		model.addAttribute("assignmentTitle", work.getTitle());
		model.addAttribute("assignmentId", work.getWorkNumber());

		return "web/pages/public/unfollow";
	}

	@RequestMapping(
		value = "/public-freelancers",
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	@ResponseBody
	public String publicFreelancers() {
		facade.meter("public-freelancers").mark();
		Multimap<String, PublicPageProfileDTO> results = publicInfoService.getPublicProfiles(INDUSTRIES, DISPLAYED_PROFILES);
		return jsonSerializationService.toJson(results.asMap());
	}

	// Worker Signup
	@RequestMapping(
		value = "/signup/worker",
		method = GET)
	public String workerSignup(Model model, @ModelAttribute("signupForm") RegisterUserForm form, SitePreference site, HttpServletRequest request) {
		facade.meter("signup-worker").mark();
		model.addAttribute("newUser", form);
		model.addAttribute("defaultIndustryId", Industry.NONE.getId());
		setClientReferer(request);
		return "web/pages/public/findwork";
	}

	@RequestMapping(
		value = {"/signup/worker", "/signup-submit/worker"},
		method = POST)
	public String doWorkerSignup(
		Model model,
		@Valid @ModelAttribute("signupForm") RegisterUserForm form,
		BindingResult bindingResult,
		HttpServletRequest request,
		SitePreference site,
		RedirectAttributes redirectAttributes) throws Exception {
		facade.meter("signup-worker-submit").mark();

		final String redirectTo = "web/pages/public/findwork";
		String redirectToSuccess = "redirect:/thankyou";
		boolean isAjaxRequest = WebUtilities.isAjax(request);

		MessageBundle messages = messageHelper.newBundle(model);
		bindingResult.recordSuppressedField("addressTyper");
		bindingResult.pushNestedPath("password");
		passwordValidator.validate(form.getPassword(), form.getUserEmail(), bindingResult);
		bindingResult.popNestedPath();

		bindingResult.pushNestedPath("userEmail");
		userEmailValidator.validate(form.getUserEmail(), bindingResult);
		bindingResult.popNestedPath();

		if (isAjaxRequest) {
			redirectToSuccess = redirectTo;
		}

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			if (isAjaxRequest) {
				model.addAttribute("messages", messages);
				model.addAttribute("successful", false);
			}
			model.addAttribute("newUser", form);

			return redirectTo;
		}

		InvitationUserRegistrationDTO dto = form.toInvitationUserRegistrationDTO();
		if (StringUtils.isNotEmpty(form.getCampaignId())) {
			dto.setCampaignId(encryptionService.decryptId(form.getCampaignId()));
		}

		Integer warpRequisitionId = getClientReferer(request);
		if(warpRequisitionId != null) {
			dto.setWarpRequisitionId(warpRequisitionId);
		}

		try {
			authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

			User newUser = registrationService.registerWorker(dto);
			linkedInService.attemptToLinkUser(request.getSession().getId(), newUser.getId());
			// remove requestToken from session in case they come right back in
			request.getSession().removeAttribute("requestToken");
			String userNumber = newUser.getUserNumber();
			redirectAttributes.addFlashAttribute("userNumber", userNumber);
			redirectAttributes.addFlashAttribute("thankyouType", "worker");
			if (isAjaxRequest) {
				messageHelper.addSuccess(messages, userNumber);
				model.addAttribute("messages", messages);
				model.addAttribute("successful", true);
			}
			return redirectToSuccess;

		} catch (Exception e) {
			messageHelper.addError(messages, e.getMessage());
			model.addAttribute("messages", messages);
			model.addAttribute("newUser", form);
			if (isAjaxRequest) {
				model.addAttribute("successful", false);
			}
			return redirectTo;
		}

	}

	@RequestMapping(value = "/indeed/xml", method = GET, produces = APPLICATION_XML_VALUE)
	@ResponseBody
	public String returnXml() {
		// TODO: Wald/Marketing have asked to freeze the content of this feed (NOT disable) until further notice. Uncomment when ready.
		// Optional<Object> xml = redisAdapter.get(RedisCacheFilters.INDEED_XML_KEY);
		// if (xml.isPresent()) {
		//	return xml.get().toString();
		// } else {
		return "";
		// }
	}

	@RequestMapping(
		value = "/login-status",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Boolean> getLoggedInStatus() {

		return ImmutableMap.of("isAuthenticated", isAuthenticated());
	}

	@RequestMapping(
		value = "/industry-list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Long> getIndustryList() {
		IndustryPagination pagination = new IndustryPagination();
		pagination.setReturnAllRows();
		pagination = invariantDataService.findAllIndustries(pagination);
		List<Industry> industries = pagination.getResults();
		Map<String, Long> industryList = new HashMap<>();
		for (Industry industry : industries) {
			industryList.put(industry.getName(), industry.getId());
		}
		return industryList;
	}

	@RequestMapping(
		value = "/login-error",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, String> getSpringSecurityLastException(HttpServletRequest request) {
		facade.meter("login-error").mark();
		Map<String, String> authErrInfo = new HashMap<>();
		HttpSession session = request.getSession(false);
		if (session == null) {
			return authErrInfo;
		}

		AuthenticationException ae = (AuthenticationException) session.getAttribute(SPRING_SECURITY_LAST_EXCEPTION);
		if (ae == null) {
			return authErrInfo;
		}

		authErrInfo.put("error", ae.getMessage());
		if (ae instanceof EmailNotConfirmedException) {
			authErrInfo.put("unconfirmed", ((EmailNotConfirmedException) ae).getUserNumber());
		}

		return authErrInfo;
	}

	@RequestMapping(
		value = "/login-message",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, String> getLoginMessage(HttpServletRequest request) {
		Map<String, String> msgInfo = new HashMap<>();
		HttpSession session = request.getSession(false);
		if (session == null) {
			return msgInfo;
		}

		MessageBundle messages = (MessageBundle) session.getAttribute(WM_LOGIN_MESSAGE);
		if (messages == null) {
			return msgInfo;
		}

		session.removeAttribute(WM_LOGIN_MESSAGE);
		msgInfo.put("message", messages.getAllMessages().get(0));

		return msgInfo;
	}
}
