package com.workmarket.api.v2.worker.controllers;

import ch.lambdaj.function.convert.Converter;
import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.GenericApiException;
import com.workmarket.api.model.ApiPhoneNumberDTO;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.AddressApiDTO;
import com.workmarket.api.v2.model.ApiImageDTO;
import com.workmarket.api.v2.model.SkillApiDTO;
import com.workmarket.api.v2.worker.model.ApiCreateWorkerRequestDTO;
import com.workmarket.api.v2.worker.model.ApiProfileDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ImageDTO;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.onboarding.model.Qualification;
import com.workmarket.domains.onboarding.model.WorkerOnboardingDTO;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CreateNewWorkerResponse;
import com.workmarket.service.business.dto.validation.CreateNewWorkerDTOValidator;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.locale.LocaleService;
import com.workmarket.service.web.ProfileFacadeService;
import com.workmarket.web.controllers.onboarding.WorkerOnboardingDTOValidator;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException403;
import com.workmarket.web.exceptions.MobileHttpException403;
import com.workmarket.web.facade.ProfileFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static ch.lambdaj.Lambda.convert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"worker", "profile"})
@RequestMapping({"/v2/worker", "/worker/v2"})
@Controller(value = "profileWorkerController")
public class ProfileController extends ApiBaseController {
	private static final String LOCALE_FEATURE_TOGGLE_ID = "locale";
	private static final Log logger = LogFactory.getLog(ProfileController.class);

	public static final String MESSAGE_COULD_NOT_CREATE_USER = "Could not create user.";
	public static final String MESSAGE_MUST_BE_AUTHENTICATED = "Must be authenticated to use selected onboarding notification strategy.";
	public static final String MESSAGE_MUST_SET_RECRUITING_CAMPAIGN_ID = "Must be set a valid recruiting campaign ID to use selected onboarding notification strategy.";

	@Autowired private RegistrationService registrationService;
	@Autowired private ProfileFacadeService profileFacadeService;
	@Autowired private UserService userService;
	@Autowired private ProfileService profileService;
	@Autowired private CreateNewWorkerDTOValidator createNewWorkerDTOValidator;
	@Autowired private EventRouter eventRouter;
	@Autowired private LocaleService localeService;
	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private RecruitingService recruitingService;

	/**
	 * Create a new user and profile.
	 *
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@ApiOperation(value = "Signup a new Worker")
	@RequestMapping(value = "/create-account", method = POST, produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = MESSAGE_OK),
		@ApiResponse(code = 400, message = MESSAGE_COULD_NOT_CREATE_USER)
	})
	public ApiV2Response<CreateNewWorkerResponse> signup(
		@RequestBody final ApiCreateWorkerRequestDTO dto,
		final BindingResult bindingResult) throws Exception {

		createNewWorkerDTOValidator.validate(dto.asCreateNewWorkerRequest(), bindingResult);

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		User user = null;
		RecruitingCampaign recruitingCampaign = null;
		
		switch (dto.getOnboardingNotificationStrategy()) {
			case DEFAULT:
				user = registrationService.registerNew(dto.asCreateNewWorkerRequest(), true);
				break;
			case SUPPRESS:
				// Suppress all notifications, useful for company that is onboarding workers (so long as they're notified later!)
				authorizeAndFetchRecruitingCampaign(dto);

				// Don't send any notification to the worker
				user = registrationService.registerNew(dto.asCreateNewWorkerRequest(), false);
				break;
			case PASSWORD_RESET:
				// Send password reset + confirmation combo email, useful for company that is onboarding workers
				recruitingCampaign = authorizeAndFetchRecruitingCampaign(dto);

				user = registrationService.registerNew(dto.asCreateNewWorkerRequest(), false);

				if (user == null) {
					break;
				}

				// Send the confirm-and-reset-password email
				registrationService.sendConfirmationWithPasswordResetEmail(recruitingCampaign.getCreatorId(), user.getId());
				break;
		}
		
		if (user == null) {
			throw new GenericApiException(MESSAGE_COULD_NOT_CREATE_USER);
		}

		if (featureEntitlementService.hasFeatureToggle(user.getId(), LOCALE_FEATURE_TOGGLE_ID)) {
			localeService.setPreferredLocale(user.getUuid(), dto.getLocale());
		}

		return ApiV2Response.OK(ImmutableList.of(convertToResponse(user)));
	}

	// Must be authenticated to use this, and must include a recruiting campaign ID, and company must own that recruiting campaign ID
	private RecruitingCampaign authorizeAndFetchRecruitingCampaign(@RequestBody ApiCreateWorkerRequestDTO dto) throws HttpException403 {
		if (getCurrentUser().isSystemUser()) {
			throw new HttpException403(MESSAGE_MUST_BE_AUTHENTICATED);
		}

		if (dto.getRecruitingCampaignId() == null) {
			throw new HttpException400(MESSAGE_MUST_SET_RECRUITING_CAMPAIGN_ID);
		}

		RecruitingCampaign recruitingCampaign = recruitingService.findRecruitingCampaign(dto.getRecruitingCampaignId());

		if (recruitingCampaign == null || !Objects.equals(recruitingCampaign.getCompany().getId(), getCurrentUser().getCompanyId())) {
			throw new HttpException403("Recruiting campaign " + dto.getRecruitingCampaignId() + " not found");
		}
		return recruitingCampaign;
	}


	/**
	 * Get a user's profile.
	 *
	 * @param userNumber User number of the user in question
	 * @return ApiResponse
	 */
	@ResponseBody
	@ApiOperation(value = "Get a profile by userNumber")
	@RequestMapping(value = "/profile/{userNumber}", method = GET, produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = MESSAGE_OK),
		@ApiResponse(code = 403, message = "api.exceptions.forbidden")
	})
	public ApiV2Response<ApiProfileDTO> getUserProfile(
		@ApiParam(name = "userNumber", value = "User Number of the user in question")
		@PathVariable("userNumber") final String userNumber) throws Exception {
		final ProfileFacade profileFacade;
		final User user;
		try {
			profileFacade = profileFacadeService.findProfileFacadeByUserNumber(userNumber);
			user = userService.findUserByUserNumber(userNumber);
		} catch (IllegalArgumentException e) {
			throw new MobileHttpException403().setMessageKey("api.exceptions.forbidden");
		}

		if (profileFacade == null || ! profileFacadeService.isCurrentUserAuthorizedToSeeProfile(getCurrentUser(), profileFacade)) {
			throw new MobileHttpException403().setMessageKey("api.exceptions.forbidden");
		}

		final List<SkillApiDTO.Builder> skillsAndSpecialties = convert(profileFacade.getSkills(), new Converter<ProfileFacade.Documentation, SkillApiDTO.Builder>() {
			@Override
			public SkillApiDTO.Builder convert(ProfileFacade.Documentation s) {
				return new SkillApiDTO.Builder(s.getId(), s.getName(), null, Qualification.Type.SKILL);
			}
		});

		skillsAndSpecialties.addAll(convert(profileFacade.getSpecialties(), new Converter<ProfileFacade.Documentation, SkillApiDTO.Builder>() {
			@Override
			public SkillApiDTO.Builder convert(ProfileFacade.Documentation s) {
				return new SkillApiDTO.Builder(s.getId(), s.getName(), null, Qualification.Type.SPECIALTY);
			}
		}));

		final Double profileFacadeMaxTravelDistance = profileFacade.getMaxTravelDistance();
		final Integer maxTravelDistance = profileFacadeMaxTravelDistance != null
			? profileFacadeMaxTravelDistance.intValue()
			: Constants.MAX_TRAVEL_DISTANCE;

		ApiImageDTO avatar = null;
		final List<ApiPhoneNumberDTO.Builder> phoneNumbers = Lists.newArrayList();
		final UserAssetAssociation assetAssociation = userService.findUserAvatars(user.getId());

		if (profileFacade.getWorkPhone() != null) {
			phoneNumbers.add(new ApiPhoneNumberDTO.Builder()
				.withPhone(profileFacade.getWorkPhone())
				.withCountryCode(profileFacade.getWorkPhoneInternationalCode())
				.withExtension(profileFacade.getWorkPhoneExtension())
				.withType(ContactContextType.WORK));
		}

		if (profileFacade.getMobilePhone() != null) {
			phoneNumbers.add(new ApiPhoneNumberDTO.Builder()
				.withPhone(profileFacade.getMobilePhone())
				.withCountryCode(profileFacade.getMobilePhoneInternationalCode())
				.withType(ContactContextType.MOBILE));
		}

		if (assetAssociation != null) {
			Asset largeAsset = assetAssociation.getTransformedLargeAsset();
			avatar = new ApiImageDTO.Builder().url(largeAsset.getCdnUri()).build();
		}

		final AddressApiDTO addressApiDTO = new AddressApiDTO.Builder(profileFacade.getAddress()).build();
		final ApiProfileDTO apiProfileDTO = new ApiProfileDTO.Builder()
			.withAvatarUri(profileFacade.getAvatarLargeAssetUri())
			.withSecondaryEmail(profileFacade.getSecondaryEmail())
			.withUserNumber(profileFacade.getUserNumber())
			.withPhoneNumber(profileFacade.getWorkPhone())
			.withFirstName(profileFacade.getFirstName())
			.withLastName(profileFacade.getLastName())
			.withJobTitle(profileFacade.getJobTitle())
			.withMaxTravelDistance(maxTravelDistance)
			.withEmail(profileFacade.getEmail())
			.withSkills(skillsAndSpecialties)
			.withPhoneNumbers(phoneNumbers)
			.withAddress(addressApiDTO)
			.withAvatar(avatar)
			.build();

		return ApiV2Response.OK(ImmutableList.of(apiProfileDTO));
	}

	/**
	 * Update the basic fields in a user's profile
	 *
	 * @param userNumber User Number
	 * @param form       Form containing details of what to update for the given user
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@ApiOperation(value = "Update a user's profile by user number")
	@RequestMapping(value = "/profile/{userNumber}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 403, message = "api.exceptions.forbidden")
	})
	public ApiV2Response<ApiProfileDTO> updateProfileFields(
		@ApiParam(name = "userNumber", required = true)
		@PathVariable String userNumber,
		@RequestBody ApiProfileDTO form) throws Exception {

		final ExtendedUserDetails currentUser = getCurrentUser();
		// Load user by the given user number
		final User user = userService.findUserByUserNumber(userNumber);
		final WorkerOnboardingDTO workerOnboardingDTO = form.asWorkerOnboardingDTO();
		final BindingResult fullWidthBinding = new BeanPropertyBindingResult(workerOnboardingDTO, "Profile");

		if (user == null) {
			fullWidthBinding.addError(new ObjectError("userNumber", "Unable to find user with number " + userNumber));
			throw new BindException(fullWidthBinding);
		}

		// load profile by the given user number
		final ProfileFacade profileFacade = profileFacadeService.findProfileFacadeByUserNumber(userNumber);
		final Profile profile = profileService.findProfile(user.getId());

		// check permissions
		// user must be able to edit the current profile
		if (profileFacade == null || !profileFacadeService.isCurrentUserAuthorizedToEditProfile(currentUser, profileFacade)) {
			throw new MobileHttpException403().setMessageKey("api.exceptions.forbidden");
		}

		if (workerOnboardingDTO.getEmail() == null) {
			workerOnboardingDTO.setEmail(profileFacade.getEmail());
		}

		// Validate the update to the profile
		final WorkerOnboardingDTOValidator validator = new WorkerOnboardingDTOValidator(messageHelper, userService, userNumber);

		validator.validate(workerOnboardingDTO, fullWidthBinding);

		if (form.getAddress() != null) {
			final AddressDTO profileAddress = form.getAddress().asAddressDTO();
			final BigDecimal longitude = profileAddress.getLongitude();
			final BigDecimal latitude = profileAddress.getLatitude();

			/* Adding a band-aid fix to the (0,0) (latitude,longitude) problem
			 * Background: Some how users are editing their profile data and submitting a form with (0,0) geo-location.
			 * This results in a bad entry being saved to the postal_code table, along with, a whole host of random (user-facing) errors because
			 * the assertions on GeoUtilties.java:37 and 38 fail (even though 0,0 can be a valid lat,long).
			 */
			if (latitude != null && longitude != null &&
				latitude.compareTo(BigDecimal.ZERO) == 0 &&
				longitude.compareTo(BigDecimal.ZERO) == 0) {
				fullWidthBinding.addError(new ObjectError("Profile.Address.latitude",
					"Latitude and Longitude cannot both be zero"));
			}
		}

		if (fullWidthBinding.hasErrors()) {
			throw new BindException(fullWidthBinding);
		}

		final Company company = profileService.findCompany(user.getId());
		final UserSearchIndexEvent indexEvent = new UserSearchIndexEvent(user.getId());

		profileService.saveOnboardPhoneCodes(profile.getId(), workerOnboardingDTO, false);
		profileService.saveOnboardProfile(user.getId(), profile.getId(), company, workerOnboardingDTO, false);

		// indexing call
		eventRouter.sendEvent(indexEvent);

		return getUserProfile(userNumber);
	}

	/**
	 * Update a user's avatar
	 *
	 * @param userNumber User Number
	 * @param image      ApiImageDTO containing the user's new avatar and any corresponding cropping information
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "Update a user's profile avatar by user number")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 403,
		message = "api.exceptions.forbidden")})
	@RequestMapping(
		value = "/profile/{userNumber}/avatar",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	ApiV2Response<ApiProfileDTO> updateProfileAvatar(
		@ApiParam(name = "userNumber", required = true) @PathVariable String userNumber,
		@RequestBody ApiImageDTO image) throws Exception {

		BindingResult fullWidthBinding = new BeanPropertyBindingResult(image, "Image");

		// load user by the given user number
		User user = userService.findUserByUserNumber(userNumber);
		if (user == null) {
			fullWidthBinding.addError(new ObjectError("userNumber", "Unable to find user with number " + userNumber));
			throw new BindException(fullWidthBinding);
		}

		// load profile by the given user number
		ProfileFacade profileFacade = profileFacadeService.findProfileFacadeByUserNumber(userNumber);

		// check permissions
		// user must be able to edit the current profile
		ExtendedUserDetails currentUser = getCurrentUser();
		if (profileFacade == null
			|| !profileFacadeService.isCurrentUserAuthorizedToEditProfile(currentUser, profileFacade)) {
			throw new MobileHttpException403().setMessageKey("api.exceptions.forbidden");
		}
		// Validate the update to the profile
		WorkerOnboardingDTOValidator validator = new WorkerOnboardingDTOValidator(messageHelper, userService, userNumber);
		ImageDTO imageDTO = image.asImageDTO();
		validator.validateAvatar(imageDTO, "", fullWidthBinding);

		if (fullWidthBinding.hasErrors()) {
			throw new BindException(fullWidthBinding);
		}

		profileService.saveProfileAvatar(user.getId(), imageDTO);

		// indexing call
		eventRouter.sendEvent(new UserSearchIndexEvent(user.getId()));

		return getUserProfile(userNumber);
	}

	private CreateNewWorkerResponse convertToResponse(final User user) {
		return new CreateNewWorkerResponse(user.getUserNumber(), user.getEmail());
	}
}
