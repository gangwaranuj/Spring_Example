package com.workmarket.api.v2.worker.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.model.ApiPhoneNumberDTO;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.AddressApiDTO;
import com.workmarket.api.v2.model.ApiImageDTO;
import com.workmarket.api.v2.worker.model.ApiCreateWorkerRequestDTO;
import com.workmarket.api.v2.worker.model.ApiProfileDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetCdnUri;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import com.workmarket.service.business.dto.CreateNewWorkerResponse;
import com.workmarket.service.business.dto.validation.CreateNewWorkerDTOValidator;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.locale.LocaleService;
import com.workmarket.service.web.ProfileFacadeService;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.web.converters.AddressFormToAddressDTOConverter;
import com.workmarket.web.facade.ProfileFacade;
import com.workmarket.web.forms.base.AddressForm;
import com.workmarket.web.models.MessageBundle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ProfileControllerTest extends BaseApiControllerTest {
	private static final TypeReference<ApiV2Response<Object>> apiV2ResponseType = new TypeReference<ApiV2Response<Object>>() {
	};
	private static final TypeReference<ApiV2Response<ApiProfileDTO>> profileDtoResponseType = new TypeReference<ApiV2Response<ApiProfileDTO>>() {
	};
	private static final TypeReference<ApiV2Response<CreateNewWorkerResponse>> createWorkerResponseType = new TypeReference<ApiV2Response<CreateNewWorkerResponse>>() {
	};
	public static final String ENDPOINT_V2_WORKER_PROFILE_V2 = "/v2/worker/create-account/";
	public static final String ENDPOINT_V2_WORKER_PROFILE = "/worker/v2/create-account/";
	public static final String INVALID_EMAIL = "not a real email";
	public static final String HTTP_CDN_URI = "http://cdn.uri/";
	private MockHttpServletResponse response;

	@Mock private ProfileFacadeService profileFacadeService;
	@Mock private FeatureEntitlementService featureEntitlementService;
	@Mock private LocaleService localeService;
	@Mock private RegistrationService registrationService;
	@Mock private EventRouter eventRouter;
	@Mock private ProfileService profileService;
	@Mock private SecurityContextFacade securityContextFacade;
	@Mock private AddressFormToAddressDTOConverter addressFormToAddressDTOConverter;
	@Mock private CreateNewWorkerDTOValidator createNewWorkerDTOValidator;
	@Mock private RecruitingService recruitingService;
	@InjectMocks private ProfileController controller = new ProfileController();

	final RecruitingCampaign recruitingCampaign = mock(RecruitingCampaign.class);
	final Company company = mock(Company.class);

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		response = new MockHttpServletResponse();
		
		when(recruitingCampaign.getCompany()).thenReturn(company);
		when(recruitingCampaign.getCreatorId()).thenReturn(DEFAULT_USER_ID);
		when(company.getId()).thenReturn(DEFAULT_COMPANY_ID);
	}

	@Test
	public void profileNotFound() throws Exception {
		when(profileFacadeService.findProfileFacadeByUserNumber(any(String.class))).thenReturn(null);
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get("/worker/v2/profile/abc123")
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden()).andReturn();
		final ApiV2Response response = expectApiV2Response(result, apiV2ResponseType);
		ApiJSONPayloadMap responseMeta = response.getMeta();
		expectApiV3ResponseMetaSupport(responseMeta);
		expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
	}

	@Test
	public void profileNotVisibleToCurrentUser() throws Exception {
		ProfileFacade mockProfileFacade = mock(ProfileFacade.class);
		when(profileFacadeService.findProfileFacadeByUserNumber(any(String.class))).thenReturn(mockProfileFacade);
		when(profileFacadeService.isCurrentUserAuthorizedToSeeProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(false);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get("/worker/v2/profile/abc123")
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden()).andReturn();
		final ApiV2Response response = expectApiV2Response(result, apiV2ResponseType);
		ApiJSONPayloadMap responseMeta = response.getMeta();

		expectApiV3ResponseMetaSupport(responseMeta);
		expectStatusCode(HttpStatus.FORBIDDEN.value(), responseMeta);
	}

	@Test
	public void profileIsVisibleToCurrentUser() throws Exception {
		ProfileFacade profileFacade = new ProfileFacade();
		profileFacade.setAddress(new AddressDTO());

		profileFacade.setUserNumber("abc123");
		profileFacade.setFirstName("johnn");
		profileFacade.setLastName("b goode");
		profileFacade.setEmail("primary@email.com");
		profileFacade.setSecondaryEmail("secondary@email.com");

		when(profileFacadeService.findProfileFacadeByUserNumber(any(String.class)))
			.thenReturn(profileFacade);

		when(userService.findUserByUserNumber(any(String.class)))
			.thenReturn(user);

		when(profileFacadeService.isCurrentUserAuthorizedToSeeProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))
		).thenReturn(true);

		setUpProfileAsset(HTTP_CDN_URI);

		final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.get("/worker/v2/profile/abc123")
			.accept(MediaType.APPLICATION_JSON)
			.param("fields", "userNumber")
			.param("fields", "firstName")
			.param("fields", "lastName")
			.param("fields", "email")
			.param("fields", "secondaryEmail")
			.param("fields", "skills.name")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		final ApiV2Response<ApiProfileDTO> response = expectApiV2Response(result, profileDtoResponseType);
		final List<ApiProfileDTO> results = response.getResults();
		final ApiJSONPayloadMap responseMeta = response.getMeta();

		expectApiV3ResponseMetaSupport(responseMeta);
		expectStatusCode(HttpStatus.OK.value(), responseMeta);

		assertEquals(1, results.size());
		assertEquals("abc123", results.get(0).getUserNumber());
		assertEquals("johnn", results.get(0).getFirstName());
		assertEquals("b goode", results.get(0).getLastName());
		assertEquals("primary@email.com", results.get(0).getEmail());
		assertEquals("secondary@email.com", results.get(0).getSecondaryEmail());
	}

	@Test
	public void signupIsAvailableWithoutHavingToLogIn() throws Exception {
		String[] excludedPaths = {ENDPOINT_V2_WORKER_PROFILE};
		signupIsAvailableWithoutHavingToLogin_specificEndpoint(excludedPaths, ENDPOINT_V2_WORKER_PROFILE);
		String[] excludedPaths_v2 = {ENDPOINT_V2_WORKER_PROFILE_V2};
		signupIsAvailableWithoutHavingToLogin_specificEndpoint(excludedPaths_v2, ENDPOINT_V2_WORKER_PROFILE_V2);
	}

	private void signupIsAvailableWithoutHavingToLogin_specificEndpoint(String[] excludedPaths, String endpoint) throws Exception {
		when(featureEntitlementService.hasFeatureToggle(any(Long.class), any(String.class))).thenReturn(false);
		apiBaseInterceptor.setExcludedPaths(excludedPaths);
		logout();
		ApiCreateWorkerRequestDTO newWorker = makeBasicCreateWorkerRequestDTO()
			.build();

		User user = new User();
		user.setId(1978L);
		user.setEmail("abc@def.ghi");

		when(registrationService.registerNew(any(CreateNewWorkerRequest.class), anyBoolean())).thenReturn(user);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post(endpoint)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(jackson.writeValueAsString(newWorker))).andExpect(status().isOk()).andReturn();

		final ApiV2Response<CreateNewWorkerResponse> response = expectApiV2Response(result, createWorkerResponseType);
		expectStatusCode(HttpStatus.OK.value(), response.getMeta());
		assertEquals("Expect email of new user matches", response.getResults().get(0).getEmail(), user.getEmail());
	}

	@Test
	public void signupWithLocaleFeature() throws Exception {
		when(featureEntitlementService.hasFeatureToggle(any(Long.class), any(String.class))).thenReturn(true);

		final ApiCreateWorkerRequestDTO newWorker = makeBasicCreateWorkerRequestDTO()
			.withLocale("en_US")
			.build();

		final User user = new User();
		user.setId(1978L);
		user.setEmail("abc@def.ghi");

		when(registrationService.registerNew(any(CreateNewWorkerRequest.class), anyBoolean())).thenReturn(user);

		final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post(ENDPOINT_V2_WORKER_PROFILE_V2)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(jackson.writeValueAsString(newWorker))).andExpect(status().isOk()).andReturn();

		final ApiV2Response<CreateNewWorkerResponse> response = expectApiV2Response(result, createWorkerResponseType);
		expectStatusCode(HttpStatus.OK.value(), response.getMeta());
		assertEquals("Expect email of new user matches", response.getResults().get(0).getEmail(), user.getEmail());
		verify(localeService).setPreferredLocale(user.getUuid(), "en_US");
	}

	@Test
	public void signupWithOnboardingNotificationStrategy_noRecruitingCampign_rejected() throws Exception {
		ApiCreateWorkerRequestDTO newWorker = makeBasicCreateWorkerRequestDTO()
			.withOnboardingNotificationStrategy(ApiCreateWorkerRequestDTO.OnboardingNotificationStrategy.PASSWORD_RESET)
			.build();

		final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post(ENDPOINT_V2_WORKER_PROFILE_V2)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(jackson.writeValueAsString(newWorker))).andExpect(status().isBadRequest()).andReturn();

		final ApiV2Response<ApiBaseError> failResponse = expectApiV2Response(result, apiErrorResponseType);

		assertEquals(ProfileController.MESSAGE_MUST_SET_RECRUITING_CAMPAIGN_ID, failResponse.getMeta().get("message"));
	}

	@Test
	public void signupWithOnboardingNotificationStrategy_notSameCompany_rejected() throws Exception {
		final Long RECRUITING_CAMPAIGN_ID = 531L;

		final ApiCreateWorkerRequestDTO newWorker = makeBasicCreateWorkerRequestDTO()
			.withRecruitingCampaignId(RECRUITING_CAMPAIGN_ID)
			.withOnboardingNotificationStrategy(ApiCreateWorkerRequestDTO.OnboardingNotificationStrategy.PASSWORD_RESET)
			.build();
		
		final Long OTHER_COMPANY_ID = 751542L;
		when(company.getId()).thenReturn(OTHER_COMPANY_ID);
		when(recruitingService.findRecruitingCampaign(anyLong())).thenReturn(recruitingCampaign);

		final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post(ENDPOINT_V2_WORKER_PROFILE_V2)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(jackson.writeValueAsString(newWorker))).andExpect(status().isForbidden()).andReturn();

		final ApiV2Response<ApiBaseError> failResponse = expectApiV2Response(result, apiErrorResponseType);
		assertEquals("Recruiting campaign " + RECRUITING_CAMPAIGN_ID + " not found", failResponse.getMeta().get("message"));
	}

	@Test
	public void signupWithOnboardingStrategy_confirmAndResetPwEmailSent() throws Exception {
		final Long RECRUITING_CAMPAIGN_ID = 531L;

		final ApiCreateWorkerRequestDTO newWorker = makeBasicCreateWorkerRequestDTO()
			.withRecruitingCampaignId(RECRUITING_CAMPAIGN_ID)
			.withOnboardingNotificationStrategy(ApiCreateWorkerRequestDTO.OnboardingNotificationStrategy.PASSWORD_RESET)
			.build();

		final User user = new User();
		user.setId(1978L);
		user.setEmail("abc@def.ghi");
		
		when(recruitingService.findRecruitingCampaign(anyLong())).thenReturn(recruitingCampaign);
		when(registrationService.registerNew(any(CreateNewWorkerRequest.class), anyBoolean())).thenReturn(user);

		final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
			.post(ENDPOINT_V2_WORKER_PROFILE_V2)
			.header("accept", MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(jackson.writeValueAsString(newWorker))).andExpect(status().isOk()).andReturn();

		final ApiV2Response<CreateNewWorkerResponse> response = expectApiV2Response(result, createWorkerResponseType);
		expectStatusCode(HttpStatus.OK.value(), response.getMeta());
		assertEquals("Expect email of new user matches", response.getResults().get(0).getEmail(), user.getEmail());
		verify(registrationService).registerNew(any(CreateNewWorkerRequest.class), eq(false));
		verify(registrationService).sendConfirmationWithPasswordResetEmail(eq(recruitingCampaign.getCreatorId()), eq(user.getId()));
	}

	@Test
	public void workerEditProfileInvalid() throws Exception {

		mockMvc = getMockMvc(controller);

		ExtendedUserDetails currentUser = new ExtendedUserDetails("test",
			"test",
			Collections.<GrantedAuthority>emptyList());
		currentUser.setId(123L);
		currentUser.setEmail("jlevine+unittest@workmarket.com");

		ApiProfileDTO.Builder profileForm = new ApiProfileDTO.Builder();
		profileForm.withEmail("jlevine+unittestworker-invalid");
		profileForm.withFirstName("");
		profileForm.withLastName("");
		profileForm.withAddress(new AddressApiDTO.Builder()
			.setAddressLine1("address1")
			.setAddressLine2("address2")
			.setCity("city")
			.setState("state")
			.setCountry("country")
			.setPostalCode("postalCode")
			.setLongitude(new BigDecimal(1.1))
			.setLatitude(new BigDecimal(2.1))
			.build());

		when(messageHelper.setErrors(any(MessageBundle.class), any(BindingResult.class))).thenCallRealMethod();
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);
		when(messageHelper.newBundle()).thenReturn(new MessageBundle());
		when(addressFormToAddressDTOConverter.convert(any(AddressForm.class))).thenCallRealMethod();

		ProfileFacade profileFacade = new ProfileFacade();
		profileFacade.setEmail(currentUser.getEmail());
		profileFacade.setAddress(new AddressDTO());
		when(profileFacadeService.findProfileFacadeByUserNumber(any(String.class))).thenReturn(profileFacade);
		when(profileFacadeService.isCurrentUserAuthorizedToEditProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		when(profileFacadeService.isCurrentUserAuthorizedToSeeProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		Profile profile = new Profile();
		profile.setId(124L);

		when(userService.findUserByUserNumber(eq("some-user-number"))).thenReturn(user);
		when(userService.emailExists(eq(INVALID_EMAIL), eq(123L))).thenReturn(true);
		when(profileService.findCompany(eq(user.getId()))).thenReturn(user.getCompany());
		when(profileService.findProfile(eq(user.getId()))).thenReturn(profile);
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);

		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(edu.emory.mathcs.backport.java.util.Collections.emptySet())
		);

		String profileJson = jackson.writer(filters).writeValueAsString(profileForm.build());

		MvcResult result = mockMvc.perform(post(
			"/worker/v2/profile/some-user-number"
		).contentType(MediaType.APPLICATION_JSON).content(profileJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.meta.code", is(400)))
			.andReturn();

		final ApiV2Response<ApiBaseError> avatarUpdateFailResponse = expectApiV2Response(result, apiErrorResponseType);
		expectApiErrorCode(avatarUpdateFailResponse.getResults(), "user.validation.firstNameRequired");
		expectApiErrorCode(avatarUpdateFailResponse.getResults(), "user.validation.lastNameRequired");
		expectApiErrorCode(avatarUpdateFailResponse.getResults(), "user.validation.emailInvalid");
	}

	@Test
	public void workerEditAvatarValid() throws Exception {

		mockMvc = getMockMvc(controller);

		ExtendedUserDetails currentUser = new ExtendedUserDetails("test",
			"test",
			Collections.<GrantedAuthority>emptyList());
		currentUser.setId(123L);
		currentUser.setEmail("jlevine+unittest@workmarket.com");

		ApiImageDTO.Builder imageBuilder = new ApiImageDTO.Builder()
			.image("ABC123")
			.filename("test.jpg");

		when(messageHelper.setErrors(any(MessageBundle.class), any(BindingResult.class))).thenCallRealMethod();
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);
		when(messageHelper.newBundle()).thenReturn(new MessageBundle());
		when(addressFormToAddressDTOConverter.convert(any(AddressForm.class))).thenCallRealMethod();

		ProfileFacade profileFacade = new ProfileFacade();
		profileFacade.setEmail(currentUser.getEmail());
		profileFacade.setAddress(new AddressDTO());
		when(profileFacadeService.findProfileFacadeByUserNumber(any(String.class))).thenReturn(profileFacade);
		when(profileFacadeService.isCurrentUserAuthorizedToEditProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		when(profileFacadeService.isCurrentUserAuthorizedToSeeProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		Profile profile = new Profile();
		profile.setId(124L);

		when(userService.findUserByUserNumber(eq("some-user-number"))).thenReturn(user);
		when(userService.emailExists(eq(INVALID_EMAIL), eq(123L))).thenReturn(true);
		when(profileService.findCompany(eq(user.getId()))).thenReturn(user.getCompany());
		when(profileService.findProfile(eq(user.getId()))).thenReturn(profile);
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);

		setUpProfileAsset(HTTP_CDN_URI);

		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(edu.emory.mathcs.backport.java.util.Collections.emptySet())
		);

		ApiImageDTO image = imageBuilder.build();
		String imageJson = jackson.writer(filters).writeValueAsString(image);

		MvcResult result = mockMvc.perform(post(
			"/worker/v2/profile/some-user-number/avatar"
		).contentType(MediaType.APPLICATION_JSON).content(imageJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.meta.code", is(200)))
			.andReturn();

		final ApiV2Response<ApiProfileDTO> avatarUpdateResponse = expectApiV2Response(result, profileDtoResponseType);
		assertEquals("Expected image to have the right url", HTTP_CDN_URI + "AB/CD/EF/GH/IJ/ABCDEFGHIJ", avatarUpdateResponse.getResults().get(0).getAvatar().getUrl());
	}

	public void setUpProfileAsset(String cdnuri) {
		Asset asset = new Asset("testAsset", "testAsset description", "abc125", "image/jpg");
		asset.setCdnUri(cdnuri);
		asset.setUUID("ABCDEFGHIJ");
		AssetCdnUri assetCdnUri = new AssetCdnUri();
		assetCdnUri.setCdnUriPrefix(cdnuri);
		asset.setAssetCdnUri(assetCdnUri);
		UserAssetAssociation userAssetAssociation = new UserAssetAssociation(user, asset, new AssetType("assetType"));
		userAssetAssociation.setTransformedLargeAsset(asset);
		when(userService.findUserAvatars(any(Long.class))).thenReturn(userAssetAssociation);
	}

	@Test
	public void workerEditAvatarInvalid() throws Exception {

		mockMvc = getMockMvc(controller);

		ExtendedUserDetails currentUser = new ExtendedUserDetails("test",
			"test",
			Collections.<GrantedAuthority>emptyList());
		currentUser.setId(123L);
		currentUser.setEmail("jlevine+unittest@workmarket.com");

		ApiImageDTO.Builder image = new ApiImageDTO.Builder();

		when(messageHelper.setErrors(any(MessageBundle.class), any(BindingResult.class))).thenCallRealMethod();
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);
		when(messageHelper.newBundle()).thenReturn(new MessageBundle());
		when(addressFormToAddressDTOConverter.convert(any(AddressForm.class))).thenCallRealMethod();

		ProfileFacade profileFacade = new ProfileFacade();
		profileFacade.setEmail(currentUser.getEmail());
		profileFacade.setAddress(new AddressDTO());
		when(profileFacadeService.findProfileFacadeByUserNumber(any(String.class))).thenReturn(profileFacade);
		when(profileFacadeService.isCurrentUserAuthorizedToEditProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		when(profileFacadeService.isCurrentUserAuthorizedToSeeProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		Profile profile = new Profile();
		profile.setId(124L);

		when(userService.findUserByUserNumber(eq("some-user-number"))).thenReturn(user);
		when(userService.emailExists(eq(INVALID_EMAIL), eq(123L))).thenReturn(true);
		when(profileService.findCompany(eq(user.getId()))).thenReturn(user.getCompany());
		when(profileService.findProfile(eq(user.getId()))).thenReturn(profile);
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);

		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(edu.emory.mathcs.backport.java.util.Collections.emptySet())
		);

		String imageJson = jackson.writer(filters).writeValueAsString(image.build());

		MvcResult result = mockMvc.perform(post(
			"/worker/v2/profile/some-user-number/avatar"
		).contentType(MediaType.APPLICATION_JSON).content(imageJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.meta.code", is(400)))
			.andReturn();

		final ApiV2Response<ApiBaseError> profileEditFailResponse = expectApiV2Response(result, apiErrorResponseType);
		expectApiErrorCode(profileEditFailResponse.getResults(), "avatar.filename");
	}


	@Test
	public void workerEditProfileValid() throws Exception {

		mockMvc = getMockMvc(controller);

		ExtendedUserDetails currentUser = new ExtendedUserDetails("test",
			"test",
			Collections.<GrantedAuthority>emptyList());
		currentUser.setId(123L);
		currentUser.setEmail("jlevine+unittest@workmarket.com");

		ApiProfileDTO.Builder profileForm = new ApiProfileDTO.Builder();
		profileForm.withEmail("jlevine+unittestworker@workmarket.com");
		profileForm.withSecondaryEmail("jlevine+unittestworker+secondary@workmarket.com");
		profileForm.withFirstName("12345678901234567890123456789012345678901234567890");
		profileForm.withLastName("12345678901234567890123456789012345678901234567890");
		profileForm.withAddress(new AddressApiDTO.Builder()
			.setAddressLine1("address1")
			.setAddressLine2("address2")
			.setCity("city")
			.setState("state")
			.setCountry("USA")
			.setPostalCode("11218")
			.setLongitude(new BigDecimal(1.1))
			.setLatitude(new BigDecimal(2.1))
			.build());

		profileForm.withPhoneNumbers(ImmutableList.of(
			new ApiPhoneNumberDTO.Builder().withCountryCode("1").withPhone("9179711027").withType(ContactContextType.WORK),
			new ApiPhoneNumberDTO.Builder().withCountryCode("1").withPhone("6465451234").withType(ContactContextType.MOBILE)
		));

		when(messageHelper.setErrors(any(MessageBundle.class), any(BindingResult.class))).thenCallRealMethod();
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);
		when(messageHelper.newBundle()).thenReturn(new MessageBundle());
		when(messageHelper.getMessage(any(String.class))).thenReturn("validationMessage");
		when(addressFormToAddressDTOConverter.convert(any(AddressForm.class))).thenCallRealMethod();

		ProfileFacade profileFacade = new ProfileFacade();
		profileFacade.setEmail(currentUser.getEmail());
		profileFacade.setSecondaryEmail("jlevine+unittestworker+secondary@workmarket.com");
		profileFacade.setAddress(new AddressDTO());
		when(profileFacadeService.findProfileFacadeByUserNumber(any(String.class))).thenReturn(profileFacade);
		when(profileFacadeService.isCurrentUserAuthorizedToEditProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		when(profileFacadeService.isCurrentUserAuthorizedToSeeProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		Profile profile = new Profile();
		profile.setId(124L);

		when(userService.findUserByUserNumber(eq("some-user-number"))).thenReturn(user);
		when(userService.emailExists(eq(INVALID_EMAIL), eq(123L))).thenReturn(true);
		when(profileService.findCompany(eq(user.getId()))).thenReturn(user.getCompany());
		when(profileService.findProfile(eq(user.getId()))).thenReturn(profile);
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);


		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(edu.emory.mathcs.backport.java.util.Collections.emptySet())
		);

		final String profileJson = jackson.writer(filters).writeValueAsString(profileForm.build());
		final MvcResult result = mockMvc.perform(post("/worker/v2/profile/some-user-number")
			.contentType(MediaType.APPLICATION_JSON)
			.content(profileJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.meta.code", is(200)))
			.andReturn();

		final ApiV2Response<ApiProfileDTO> response = expectApiV2Response(result, profileDtoResponseType);
		final List<ApiProfileDTO> results = response.getResults();
		final ApiJSONPayloadMap responseMeta = response.getMeta();

		expectApiV3ResponseMetaSupport(responseMeta);
		expectStatusCode(HttpStatus.OK.value(), responseMeta);

		assertEquals(1, results.size());
		assertEquals("jlevine+unittest@workmarket.com", results.get(0).getEmail());
		assertEquals("jlevine+unittestworker+secondary@workmarket.com", results.get(0).getSecondaryEmail());
	}

	@Test
	public void workerEditProfileValid_narrowPayload() throws Exception {

		mockMvc = getMockMvc(controller);

		ExtendedUserDetails currentUser = new ExtendedUserDetails("test",
			"test",
			Collections.<GrantedAuthority>emptyList());
		currentUser.setId(123L);
		currentUser.setEmail("jlevine+unittest@workmarket.com");

		ApiProfileDTO.Builder profileForm = new ApiProfileDTO.Builder();
		profileForm.withFirstName("12345678901234567890123456789012345678901234567890");
		profileForm.withLastName("12345678901234567890123456789012345678901234567890");

		when(messageHelper.setErrors(any(MessageBundle.class), any(BindingResult.class))).thenCallRealMethod();
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);
		when(messageHelper.newBundle()).thenReturn(new MessageBundle());
		when(messageHelper.getMessage(any(String.class))).thenReturn("validationMessage");
		when(addressFormToAddressDTOConverter.convert(any(AddressForm.class))).thenCallRealMethod();

		ProfileFacade profileFacade = new ProfileFacade();
		profileFacade.setEmail(currentUser.getEmail());
		profileFacade.setAddress(new AddressDTO());
		when(profileFacadeService.findProfileFacadeByUserNumber(any(String.class))).thenReturn(profileFacade);
		when(profileFacadeService.isCurrentUserAuthorizedToEditProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		when(profileFacadeService.isCurrentUserAuthorizedToSeeProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		Profile profile = new Profile();
		profile.setId(124L);

		when(userService.findUserByUserNumber(eq("some-user-number"))).thenReturn(user);
		when(userService.emailExists(eq(INVALID_EMAIL), eq(123L))).thenReturn(true);
		when(profileService.findCompany(eq(user.getId()))).thenReturn(user.getCompany());
		when(profileService.findProfile(eq(user.getId()))).thenReturn(profile);
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);


		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(ImmutableSet.of("firstName", "lastName"))
		);

		String profileJson = jackson.writer(filters).writeValueAsString(profileForm.build());

		MvcResult result = mockMvc.perform(post(
			"/worker/v2/profile/some-user-number"
		).contentType(MediaType.APPLICATION_JSON).content(profileJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.meta.code", is(200)))
			.andReturn();
	}

	@Test
	public void workerEditProfileValid_narrowPayload2() throws Exception {

		mockMvc = getMockMvc(controller);

		ExtendedUserDetails currentUser = new ExtendedUserDetails("test",
			"test",
			Collections.<GrantedAuthority>emptyList());
		currentUser.setId(123L);
		currentUser.setEmail("jlevine+unittest@workmarket.com");

		ApiProfileDTO.Builder profileForm = new ApiProfileDTO.Builder();
		profileForm.withFirstName("12345678901234567890123456789012345678901234567890");
		profileForm.withLastName("12345678901234567890123456789012345678901234567890");
		profileForm.withPhoneNumbers(ImmutableList.of(
			new ApiPhoneNumberDTO.Builder().withPhone("9179711027").withType(ContactContextType.WORK)
		));

		when(messageHelper.setErrors(any(MessageBundle.class), any(BindingResult.class))).thenCallRealMethod();
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);
		when(messageHelper.newBundle()).thenReturn(new MessageBundle());
		when(messageHelper.getMessage(any(String.class))).thenReturn("validationMessage");
		when(addressFormToAddressDTOConverter.convert(any(AddressForm.class))).thenCallRealMethod();

		ProfileFacade profileFacade = new ProfileFacade();
		profileFacade.setEmail(currentUser.getEmail());
		profileFacade.setAddress(new AddressDTO());
		when(profileFacadeService.findProfileFacadeByUserNumber(any(String.class))).thenReturn(profileFacade);
		when(profileFacadeService.isCurrentUserAuthorizedToEditProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		when(profileFacadeService.isCurrentUserAuthorizedToSeeProfile(
			any(ExtendedUserDetails.class),
			any(ProfileFacade.class))).thenReturn(true);

		Profile profile = new Profile();
		profile.setId(124L);

		when(userService.findUserByUserNumber(eq("some-user-number"))).thenReturn(user);
		when(userService.emailExists(eq(INVALID_EMAIL), eq(123L))).thenReturn(true);
		when(profileService.findCompany(eq(user.getId()))).thenReturn(user.getCompany());
		when(profileService.findProfile(eq(user.getId()))).thenReturn(profile);
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);


		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(ImmutableSet.of("firstName", "lastName", "phoneNumbers.phone", "phoneNumbers.type"))
		);

		String profileJson = jackson.writer(filters).writeValueAsString(profileForm.build());

		MvcResult result = mockMvc.perform(post(
			"/worker/v2/profile/some-user-number"
		).contentType(MediaType.APPLICATION_JSON).content(profileJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.meta.code", is(200)))
			.andReturn();
	}

	private ApiCreateWorkerRequestDTO.Builder makeBasicCreateWorkerRequestDTO() {
		final String userName = "worker" + RandomUtilities.generateNumericString(10);
		return new ApiCreateWorkerRequestDTO.Builder()
			.withFirstName(RandomUtilities.generateNumericString(10))
			.withLastName(RandomUtilities.generateNumericString(10))
			.withEmail(userName + "@workmarket.com")
			.withSecondaryEmail(userName + "+secondary@workmarket.com")
			.withPassword("workerPassword" + userName)
			.withLocale("en_US")
			.withJobTitle(userName)
			.withCompanyName("company" + userName)
			.withAddress1("240 west 27th street")
			.withAddress2("9th floor")
			.withCountry("USA")
			.withCity("New York")
			.withPostalCode("11218")
			.withState("NY")
			.withLatitude(new BigDecimal(0.01))
			.withLongitude(new BigDecimal(1.01));
	}
}
