package com.workmarket.web.controllers;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.common.core.RequestContext;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.IndustryPagination;
import com.workmarket.domains.model.Language;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileLanguage;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.Link;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.UserLinkAssociation;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.certification.CertificationPagination;
import com.workmarket.domains.model.certification.CertificationVendor;
import com.workmarket.domains.model.certification.CertificationVendorPagination;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.license.LicensePagination;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.qualification.UserToQualification;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.SkillPagination;
import com.workmarket.domains.model.skill.UserSkillAssociation;
import com.workmarket.domains.model.skill.UserSkillAssociationPagination;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.model.specialty.SpecialtyPagination;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociation;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociationPagination;
import com.workmarket.domains.model.tool.Tool;
import com.workmarket.domains.model.tool.ToolPagination;
import com.workmarket.domains.model.tool.UserToolAssociation;
import com.workmarket.domains.model.tool.UserToolAssociationPagination;
import com.workmarket.domains.onboarding.model.Qualification;
import com.workmarket.dto.AddressDTO;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.search.qualification.MutateResponse;
import com.workmarket.search.qualification.QualificationBuilder;
import com.workmarket.search.qualification.QualificationClient;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CertificationService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.InsuranceService;
import com.workmarket.service.business.LicenseService;
import com.workmarket.service.business.LinkedInService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.SpecialtyService;
import com.workmarket.service.business.ToolService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.asset.AssetBundlerQueue;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.CertificationDTO;
import com.workmarket.service.business.dto.CertificationVendorDTO;
import com.workmarket.service.business.dto.CountryDTO;
import com.workmarket.service.business.dto.EducationHistoryDTO;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.service.business.dto.InsuranceDTO;
import com.workmarket.service.business.dto.LicenseDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.ProfileLanguageDTO;
import com.workmarket.service.business.dto.SkillDTO;
import com.workmarket.service.business.dto.SpecialtyDTO;
import com.workmarket.service.business.dto.ToolDTO;
import com.workmarket.service.business.dto.UserCertificationDTO;
import com.workmarket.service.business.dto.UserLicenseDTO;
import com.workmarket.service.business.event.user.ProfileUpdateEvent;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.external.ExternalLinkService;
import com.workmarket.service.business.qualification.QualificationAssociationService;
import com.workmarket.service.business.qualification.QualificationRecommender;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.GoogleCalendarService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.converters.AddressFormToAddressDTOConverter;
import com.workmarket.web.editors.IndustryEditor;
import com.workmarket.web.forms.CertificationsForm;
import com.workmarket.web.forms.GeneralProfileForm;
import com.workmarket.web.forms.InsuranceForm;
import com.workmarket.web.forms.LicensesForm;
import com.workmarket.web.forms.profileedit.QualificationsForm;
import com.workmarket.web.forms.profileedit.SkillsForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.AddressValidator;
import com.workmarket.web.validators.FilenameValidator;
import com.workmarket.web.validators.FiletypeValidator;
import com.workmarket.web.validators.ProfileLanguageValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rx.functions.Action1;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.workmarket.utility.CollectionUtilities.newObjectMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/profile-edit")
public class ProfileEditController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ProfileEditController.class);

	private static final String ADD_QUALIFICATION_FEATURE = "AddUserNonJobTitleQualification";

	@Autowired private InvariantDataService invariantDataService;
	@Autowired private InsuranceService insuranceService;
	@Autowired private LicenseService licenseService;
	@Autowired private CertificationService certificationService;
	@Autowired private ProfileService profileService;
	@Autowired private SkillService skillService;
	@Autowired private SuggestionService suggestionsService;
	@Autowired private SpecialtyService specialtyService;
	@Autowired private ToolService toolService;
	@Autowired private UserService userService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private LinkedInService linkedInService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private FormOptionsDataHelper formOptionsDataHelper;
	@Autowired private ProfileLanguageValidator profileLanguageValidator;
	@Autowired private AddressValidator addressValidator;
	@Autowired private FilenameValidator filenameValidator;
	@Autowired private AssetBundlerQueue assetbundler;
	@Autowired private FiletypeValidator filetypeValidator;
	@Autowired private ExternalLinkService externalLinkService;
	@Autowired private GoogleCalendarService googleCalendarService;
	@Autowired private EventRouter eventRouter;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private AddressFormToAddressDTOConverter addressFormToAddressDTOConverter;
	@Autowired @Qualifier("industryEditor") private IndustryEditor industryEditor;
	@Autowired private IndustryService industryService;
	@Autowired private AddressService addressService;
	@Autowired private VendorService vendorService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private QualificationClient qualificationClient;
	@Autowired private QualificationAssociationService qualificationAssociationService;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private QualificationRecommender qualificationRecommender;
	@Autowired private UserIndexer userIndexer;

	@InitBinder("profile")
	public void initProjectBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Industry.class, industryEditor);
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setAutoGrowCollectionLimit(1024); // to account for legacy data
	}

	@RequestMapping(method = GET)
	public String index(Model model) throws Exception {
		GeneralProfileForm generalProfile = new GeneralProfileForm();

		User user = userService.findUserById(getCurrentUser().getId());
		ProfileDTO profileDTO = profileService.findProfileDTO(getCurrentUser().getId());

		populateModel(model);

		generalProfile.setFirstName(user.getFirstName());
		generalProfile.setLastName(user.getLastName());
		generalProfile.setUserEmail(user.getEmail());
		generalProfile.setUserEmailSecondary(user.getSecondaryEmail());
		generalProfile.setChangedEmail(user.getChangedEmail());
		generalProfile.setJobTitle(profileDTO.getJobTitle());
		generalProfile.setWorkPhone(profileDTO.getWorkPhone());
		generalProfile.setWorkPhoneExtension(profileDTO.getWorkPhoneExtension());
		generalProfile.setMobilePhone(profileDTO.getMobilePhone());
		Long addressId = profileDTO.getAddressId();
		if (addressId != null) {
			generalProfile.setAddress1(profileDTO.getAddress1());
			generalProfile.setAddress2(profileDTO.getAddress2());
			generalProfile.setCity(profileDTO.getCity());
			generalProfile.setState(profileDTO.getState());
			generalProfile.setPostalCode(profileDTO.getPostalCode());
			generalProfile.setCountry(profileDTO.getCountry());
			generalProfile.setLongitude(profileDTO.getLongitude() != null ? profileDTO.getLongitude().toString() : null);
			generalProfile.setLatitude(profileDTO.getLatitude() != null ? profileDTO.getLatitude().toString() : null);
		}
		generalProfile.setTimezone(profileDTO.getTimeZoneId());
		generalProfile.setMobilePhoneInternationalCode(
			profileDTO.getMobilePhoneInternationalCode() != null ?  Long.toString(profileDTO.getMobilePhoneInternationalCode()) : null
		);
		generalProfile.setWorkPhoneInternationalCode(
			profileDTO.getWorkPhoneInternationalCode() != null ? Long.toString(profileDTO.getWorkPhoneInternationalCode()) : null
		);

		if (getCurrentUser().isMbo()) {
			MboProfile mboProfile = profileService.findMboProfile(user.getId());
			if (mboProfile != null) {
				model.addAttribute("mboProfile", mboProfile);
			}
		}

		model.addAttribute("generalProfile", generalProfile);
		model.addAttribute("selected_navigation_link", "profileedit.index");

		return "web/pages/profileedit/index";
	}

	private void populateModel(Model model) {
		model.addAttribute("statesCountries", formOptionsDataHelper.getStatesAsOptgroup());

		List<CountryDTO> countries = invariantDataService.getCountryDTOs();
		model.addAttribute("countryList", countries);

		List<TimeZone> timezones = invariantDataService.findAllActiveTimeZones();
		model.addAttribute("timezoneList", timezones);

		List<CallingCode> callingCodes = invariantDataService.findAllActiveCallingCodes();
		model.addAttribute("callingCodesList", callingCodes);
	}

	@RequestMapping(method = POST)
	public String indexSubmit(
		@Valid @ModelAttribute("generalProfile") GeneralProfileForm form,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) throws Exception {

		MessageBundle bundle = messageHelper.newBundle();

		if (userService.emailExists(form.getUserEmail(), getCurrentUser().getId())) {
			messageHelper.addError(bundle, "profile.edit.email.duplicate");
		}

		AddressDTO profileAddress = addressFormToAddressDTOConverter.convert(form);
		addressValidator.validate(profileAddress, bind);

		/* Adding a band-aid fix to the (0,0) (latitude,longitude) problem
		 * Background: Some how users are editing their profile data and submitting a form with (0,0) geo-location.
		 * This results in a bad entry being saved to the postal_code table, along with, a whole host of random (user-facing) errors because
		 * the assertions on GeoUtilties.java:37 and 38 fail (even though 0,0 can be a valid lat,long).
		 */
		String submittedLat = form.getLatitude();
		String submittedLong = form.getLatitude();
		if (StringUtilities.isNotEmpty(submittedLat) &&
			new BigDecimal(submittedLat).compareTo(BigDecimal.ZERO) == 0 &&
			StringUtilities.isNotEmpty(submittedLong) &&
			new BigDecimal(submittedLong).compareTo(BigDecimal.ZERO) == 0) {
			messageHelper.addError(bundle, "Invalid.address");
		}

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
		}

		if (bundle.hasErrors()) {
			model.addAttribute("bundle", bundle);
			populateModel(model);
			return "web/pages/profileedit/index";
		}

		User user = userService.findUserById(getCurrentUser().getId());

		// Determine if the user changed their email address.
		Boolean emailChanged = false;
		if (!user.getEmail().equals(form.getUserEmail())) {
			emailChanged = true;
		}

		// Update user properties.
		userService.updateUserProperties(getCurrentUser().getId(), CollectionUtilities.newStringMap(
			"email", form.getUserEmail(),
			"secondaryEmail", form.getUserEmailSecondary()));

		// Update profile properties.
		profileService.updateProfileProperties(getCurrentUser().getId(), CollectionUtilities.newStringMap(
			"workPhone", form.getWorkPhone(),
			"workPhoneExtension", form.getWorkPhoneExtension(),
			"mobilePhone", form.getMobilePhone(),
			"mobilePhoneInternationalCode", form.getMobilePhoneInternationalCode(),
			"workPhoneInternationalCode", form.getWorkPhoneInternationalCode(),
			"jobTitle", form.getJobTitle(),
			"timeZoneId", String.valueOf(form.getTimezone())));

		if (getCurrentUser().isMbo()) {
			eventRouter.sendEvent(new ProfileUpdateEvent(getCurrentUser().getId(), CollectionUtilities.newObjectMap("title", form.getJobTitle())));
		}

		// Update address properties.
		Map<String, String> addressProperties = CollectionUtilities.newStringMap(
			"address1", profileAddress.getAddress1(),
			"address2", profileAddress.getAddress2(),
			"city", profileAddress.getCity(),
			"state", profileAddress.getState(),
			"postalCode", profileAddress.getPostalCode(),
			"country", profileAddress.getCountry(),
			"latitude", String.valueOf(profileAddress.getLatitude()),
			"longitude", String.valueOf(profileAddress.getLongitude()),
			"addressType", "profile");
		profileService.updateProfileAddressProperties(getCurrentUser().getId(), addressProperties);

		// Make sure the profile postal code isn't blacklisted.
		List<String> blacklistedPostalCodes = profileService.findBlacklistedZipcodesForUser(getCurrentUser().getId());
		blacklistedPostalCodes.remove(addressProperties.get("postalCode"));
		profileService.setBlacklistedZipcodesForUser(getCurrentUser().getId(), blacklistedPostalCodes);

		if (emailChanged) {
			messageHelper.addSuccess(bundle, "profile.edit.success_emailchanged", form.getUserEmail(), user.getEmail());
			profileService.sendProfileUpdateEmail(getCurrentUser().getId(), "email address");
		} else {
			messageHelper.addSuccess(bundle, "profile.edit.success");
		}
		flash.addFlashAttribute("bundle", bundle);

		// atomic indexing call
		eventRouter.sendEvent(
			new UserSearchIndexEvent(getCurrentUser().getId())
		);

		if (getCurrentUser().getId() == null) {
			logger.debug("Trying to reindex null user for profile update");
		}

		return "redirect:/profile-edit";
	}


	@RequestMapping(
		value = "/employment",
		method = POST)
	public String saveEmployment(
		@ModelAttribute("profile") Profile profile,
		BindingResult bind,
		RedirectAttributes flash) throws Exception {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/profile-edit/employment";
		}
		Map<String, String> properties = CollectionUtilities.newStringMap("overview", profile.getOverview());

		profileService.updateProfileProperties(getCurrentUser().getId(), properties);

		if (getCurrentUser().isMbo()) {
			eventRouter.sendEvent(new ProfileUpdateEvent(getCurrentUser().getId(), CollectionUtilities.newObjectMap("overview", profile.getOverview())));
		}

		messageHelper.addSuccess(bundle, "profile.employment-update.success");

		return "redirect:/profile-edit/employment";
	}


	@RequestMapping(
		value = "/employment",
		method = GET)
	public String employment(Model model) throws Exception {

		User user = userService.findUserById(getCurrentUser().getId());

		model.addAttribute("profile", user.getProfile());
		model.addAttribute("industries", industryService.getAllIndustryDTOs());
		model.addAttribute("resumes", profileService.findAllUserResumes(getCurrentUser().getId()));
		model.addAttribute("linkedin", linkedInService.findMostRecentLinkedInPerson(getCurrentUser().getId()));
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/profile-edit/employment"));

		return "web/pages/profileedit/employment";
	}


	@RequestMapping(
		value = "/education",
		method = GET)
	public String education(Model model) {

		LinkedInPerson linkedInPerson = linkedInService.findMostRecentLinkedInPerson(getCurrentUser().getId());
		if (linkedInPerson != null) {
			List<EducationHistoryDTO> educationHistory = EducationHistoryDTO.getEducationHistory(linkedInPerson);
			model.addAttribute("educationHistory", educationHistory);
		}

		return "web/pages/profileedit/education";
	}


	@RequestMapping(
		value = "/skills",
		method = GET)
	public String skills(Model model) throws Exception {

		model.addAttribute("industries", industryService.getAllIndustryDTOs());

		Profile profile = profileService.findProfile(getCurrentUser().getId());
		model.addAttribute("industry", industryService.getDefaultIndustryForProfile(profile.getId()).getId());

		UserSkillAssociationPagination pagination = new UserSkillAssociationPagination(true);
		pagination = skillService.findAllAssociationsByUser(getCurrentUser().getId(), pagination);
		List<UserSkillAssociation> skills = pagination.getResults();

		JSONArray skillsJSON = new JSONArray();
		for (UserSkillAssociation s : skills) {
			if (s.getDeleted()) {
				continue;
			}
			skillsJSON.put(CollectionUtilities.newStringMap(
				"id", s.getSkill().getId().toString(),
				"name", s.getSkill().getName()));
		}
		model.addAttribute("skillsJSON", skillsJSON);

		return "web/pages/profileedit/skills";
	}


	@RequestMapping(
		value = "/tools",
		method = GET)
	public String tools(Model model) {
		model.addAttribute("industries", industryService.getAllIndustryDTOs());

		Profile profile = profileService.findProfile(getCurrentUser().getId());
		model.addAttribute("industry", industryService.getDefaultIndustryForProfile(profile.getId()).getId());

		UserToolAssociationPagination pagination = new UserToolAssociationPagination(true);
		pagination = toolService.findAllAssociationsByUser(getCurrentUser().getId(), pagination);
		List<UserToolAssociation> tools = pagination.getResults();

		JSONArray toolsJSON = new JSONArray();
		for (UserToolAssociation t : tools) {
			if (t.getDeleted()) {
				continue;
			}
			toolsJSON.put(CollectionUtilities.newStringMap(
				"id", t.getTool().getId().toString(),
				"name", t.getTool().getName()));
		}
		model.addAttribute("toolsJSON", toolsJSON);

		return "web/pages/profileedit/tools";
	}


	@RequestMapping(
		value = "/suggest_specialties",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void suggestSpecialties(Model model, HttpServletRequest httpRequest) {

		List<SuggestionDTO> skills = suggestionsService.suggestSpecialties(httpRequest.getParameter("term"));

		model.addAttribute("response", skills);
	}


	@RequestMapping(
		value = "/suggest_tools",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void suggestTools(Model model, HttpServletRequest httpRequest) {

			List<SuggestionDTO> skills = suggestionsService.suggestTools(httpRequest.getParameter("term"));

			model.addAttribute("response", skills);
	}


	@RequestMapping(
		value = "/browse_skills",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void browseSkills(Model model, HttpServletRequest httpRequest) {

		SkillPagination pagination = new SkillPagination(true);
		String industryIdStr = httpRequest.getParameter("industry_id");
		try {
			pagination = skillService.findAllSkillsByIndustry(NumberUtils.createInteger(industryIdStr), pagination);
			List<Skill> skills = pagination.getResults();
			model.addAttribute("response", newObjectMap(
				"skills", CollectionUtilities.extractPropertiesList(skills, "id", "name")));
		} catch (NumberFormatException e) {
			logger.error(String.format("Error parsing industry_id %s: ", industryIdStr), e);
		}
	}


	@RequestMapping(
		value = "/browse_specialties",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void browseSpecialties(Model model, HttpServletRequest httpRequest) {

		SpecialtyPagination pagination = new SpecialtyPagination(true);
		String industryIdStr = httpRequest.getParameter("industry_id");
		try {
			pagination = specialtyService.findAllSpecialtiesByIndustry(NumberUtils.createInteger(industryIdStr), pagination);
			List<Specialty> specialties = pagination.getResults();
			model.addAttribute("response", newObjectMap(
				"skills", CollectionUtilities.extractPropertiesList(specialties, "id", "name")));
		} catch (NumberFormatException e) {
			logger.error(String.format("Error parsing industry_id %s: ", industryIdStr), e);
		}
	}


	@RequestMapping(
		value = "/browse_tools",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void browseTools(Model model, HttpServletRequest httpRequest) {

		ToolPagination pagination = new ToolPagination(true);
		String industryIdStr = httpRequest.getParameter("industry_id");
		try {
			pagination = toolService.findAllToolsByIndustry(NumberUtils.createInteger(industryIdStr), pagination);
			List<Tool> tools = pagination.getResults();
			model.addAttribute("response", newObjectMap(
				"skills", CollectionUtilities.extractPropertiesList(tools, "id", "name")));
		} catch (NumberFormatException e) {
			logger.error(String.format("Error parsing industry_id %s: ", industryIdStr), e);
		}
	}

	@RequestMapping(
		value = "/save_skills",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder saveSkills(@RequestBody SkillsForm form) throws Exception {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		List<Map<String, String>> skills = form.getSkills();

		if (!form.xssValidateSkillInputs()) {
			messageHelper.addMessage(response, "profile.edit.skills.invalidName");
			return response;
		}

		if (skills.size() > Constants.PROFILE_MAX_SKILLS) {
			messageHelper.addMessage(response, "profile.edit.skills.exception", Constants.PROFILE_MAX_SKILLS);
			return response;
		}

		// mark existing skills deleted
		skillService.removeSkillsFromUser(getCurrentUser().getId());

		// re-add any skills that were kept or created
		List<Integer> ids = Lists.transform(skills, new Function<Map<String, String>, Integer>() {
			@Override
			public Integer apply(Map<String, String> s) {
				Long id = StringUtilities.parseLong(s.get("id"));
				if (id == null) {
					String name = s.get("name");

					SkillDTO dto = new SkillDTO();
					dto.setName(name);
					dto.setDescription(name);
					Skill skill = skillService.saveOrUpdateSkill(dto);

					id = skill.getId();
				}
				return id.intValue();
			}
		});

		// Set the user's skills.
		try {
			skillService.setSkillsOfUser(ids, getCurrentUser().getId());
			if (getCurrentUser().isMbo()) {
				eventRouter.sendEvent(new ProfileUpdateEvent(getCurrentUser().getId(), CollectionUtilities.newObjectMap("skills", skills)));
			}
			messageHelper.addMessage(response, "profile.edit.skills.success");
			response.setSuccessful(true);
		} catch (Exception e) {
			logger.error(String.format("error saving tools for user_id=%s", getCurrentUser().getId()), e);
			messageHelper.addMessage(response, "profile.edit.skills.exception");
		}

		if (featureEvaluator.hasGlobalFeature(ADD_QUALIFICATION_FEATURE)) {
			final List<com.workmarket.search.qualification.Qualification> qualifications = addQualifications(skills, QualificationType.skill);
			if (qualifications.size() > 0) {
				qualificationAssociationService.setUserQualifications(getCurrentUser().getId(), QualificationType.skill, qualifications);
			}
		}

		return response;
	}


	@RequestMapping(
		value = "/save_specialties",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	public @ResponseBody AjaxResponseBuilder saveSpecialties(@RequestBody SkillsForm form) throws Exception {

		List<Map<String, String>> skills = form.getSkills();
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (!form.xssValidateSkillInputs()) {
			messageHelper.addMessage(response, "profile.edit.skills.invalidName");
			return response;
		}

		if (skills.size() > Constants.PROFILE_MAX_SPECIALTIES) {
			messageHelper.addMessage(response, "profile.edit.skills.size", Constants.PROFILE_MAX_SKILLS);
			return response;
		}

		// mark existing specialties deleted
		specialtyService.removeSpecialtiesFromUser(getCurrentUser().getId());

		// re-add any specialties that were kept or created
		List<Integer> ids = Lists.transform(skills, new Function<Map<String, String>, Integer>() {
			@Override
			public Integer apply(Map<String, String> s) {
				Long id = StringUtilities.parseLong(s.get("id"));
				if (id == null) {
					String name = s.get("name");

					SpecialtyDTO dto = new SpecialtyDTO();
					dto.setName(name);
					dto.setDescription(name);
					Specialty specialty = specialtyService.saveOrUpdateSpecialty(dto);

					id = specialty.getId();
				}
				return id.intValue();
			}
		});

		// Set the user's specialties.
		try {
			specialtyService.setSpecialtiesOfUser(ids, getCurrentUser().getId());
			messageHelper.addMessage(response, "profile.edit.specialties.success");
			response.setSuccessful(true);
		} catch (Exception e) {
			logger.error(String.format("error saving specialties for user_id=%s", getCurrentUser().getId()), e);
			messageHelper.addMessage(response, "profile.edit.specialties.exception");
		}

		if (featureEvaluator.hasGlobalFeature(ADD_QUALIFICATION_FEATURE)) {
			final List<com.workmarket.search.qualification.Qualification> qualifications = addQualifications(skills, QualificationType.specialty);
			if (qualifications.size() > 0) {
				qualificationAssociationService.setUserQualifications(getCurrentUser().getId(), QualificationType.specialty, qualifications);
			}
		}

		return response;
	}


	@RequestMapping(
		value = "/save_tools",
		method = POST,
		produces = APPLICATION_JSON_VALUE
	)
	public @ResponseBody AjaxResponseBuilder saveTools(@RequestBody SkillsForm form) throws Exception {

		List<Map<String, String>> skills = form.getSkills();
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (!form.xssValidateSkillInputs()) {
			messageHelper.addMessage(response, "profile.edit.skills.invalidName");
			return response;
		}

		if (skills.size() > Constants.PROFILE_MAX_TOOLS) {
			messageHelper.addMessage(response, messageHelper.getMessage("profile.edit.tools.size", Constants.PROFILE_MAX_TOOLS));
			return response;
		}

		// mark existing tools deleted
		toolService.removeToolsFromUser(getCurrentUser().getId());

		// re-add any tools that were kept or created
		List<Integer> ids = Lists.transform(skills, new Function<Map<String, String>, Integer>() {
			@Override
			public Integer apply(Map<String, String> s) {
				Long id = StringUtilities.parseLong(s.get("id"));
				if (id == null) {
					String name = s.get("name");

					ToolDTO dto = new ToolDTO();
					dto.setName(name);
					dto.setDescription(name);
					Tool tool = toolService.saveOrUpdateTool(dto);

					id = tool.getId();
				}
				return id.intValue();
			}
		});

		// Set the user's specialties.
		try {
			toolService.setToolsOfUser(ids, getCurrentUser().getId());
			messageHelper.addMessage(response, "profile.edit.tools.success");
			response.setSuccessful(true);
		} catch (Exception e) {
			logger.error(String.format("error saving tools for user_id=%s", getCurrentUser().getId()), e);
			messageHelper.addMessage(response, "profile.edit.tools.exception");
		}

		return response;
	}


	@RequestMapping(value = "/specialties", method = GET)
	public String specialties(Model model) {

		model.addAttribute("industries", industryService.getAllIndustryDTOs());

		Profile profile = profileService.findProfile(getCurrentUser().getId());
		model.addAttribute("industry", industryService.getDefaultIndustryForProfile(profile.getId()).getId());

		UserSpecialtyAssociationPagination pagination = new UserSpecialtyAssociationPagination(true);
		pagination = specialtyService.findAllAssociationsByUser(getCurrentUser().getId(), pagination);
		List<UserSpecialtyAssociation> specialties = pagination.getResults();

		JSONArray specialtiesJSON = new JSONArray();
		for (UserSpecialtyAssociation s : specialties) {
			if (s.getDeleted()) {
				continue;
			}
			specialtiesJSON.put(CollectionUtilities.newStringMap(
				"id", s.getSpecialty().getId().toString(),
				"name", s.getSpecialty().getName()));
		}
		model.addAttribute("specialtiesJSON", specialtiesJSON);

		return "web/pages/profileedit/specialties";
	}


	@RequestMapping(value = "/licenses", method = GET)
	public String licenses(Model model) {

		UserLicenseAssociationPagination currentLPagination = new UserLicenseAssociationPagination();
		currentLPagination.setReturnAllRows();

		UserLicenseAssociationPagination temp = licenseService.findAllAssociationsByUserId(getCurrentUser().getId(), currentLPagination);
		UserLicenseAssociationPagination licenses = (currentLPagination.getRowCount() == 0) ? new UserLicenseAssociationPagination() : temp;

		List<UserLicenseAssociation> licensesList = licenses.getResults();
		List<Map<String, Object>> currentLicenses = Lists.newArrayList();
		for (UserLicenseAssociation item : licensesList) {
			License license = item.getLicense();

			Map<String, Object> table = newObjectMap(
				"id", license.getId(),
				"state", license.getState(),
				"name", license.getName(),
				"number", item.getLicenseNumber(),
				"date", item.getCreatedOn().getTime(),
				"verified", item.getVerificationStatus().isVerified());
			if (item.getLastActivityOn() != null)
				table.put("last_activity", item.getLastActivityOn().getTime());
			currentLicenses.add(table);
		}
		model.addAttribute("current_licenses", currentLicenses);

		addStateAttributesToModel(model);

		return "web/pages/profileedit/licenses";
	}


	private void addStateAttributesToModel(Model model) {

		model.addAttribute("states", formOptionsDataHelper.getStatesAsOptgroup());

		Profile profile = profileService.findProfile(getCurrentUser().getId());

		if (profile != null) {
			Long addressId = profile.getAddressId();
			if (addressId != null) {
				model.addAttribute("state", addressService.findById(addressId).getState().getShortName());
			}
		}
	}


	@RequestMapping(value = "/licensesave", method = POST, consumes = MULTIPART_FORM_DATA_VALUE)
	public String licensesave(
		@Valid @ModelAttribute("licensesForm") LicensesForm licensesForm,
		BindingResult bind,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/profile-edit/licenses";
		}

		// validate filename
		if (licensesForm.getFile() != null) {
			if (isBlank(licensesForm.getFile().getOriginalFilename())) {
				bind.reject("NotNull", new Object[]{"License attachment"}, "");
			}
			if (!bind.hasErrors()) {
				filenameValidator.validate(licensesForm.getFile().getOriginalFilename(), bind);
			}
			if (bind.hasErrors()) {
				messageHelper.setErrors(bundle, bind);
				return "redirect:/profile-edit/licenses";
			}
		}

		LicenseDTO license = new LicenseDTO();
		license.setLicenseId(Long.valueOf(licensesForm.getLicense()));
		license.setLicenseNumber(licensesForm.getNumber());

		UserLicenseDTO userLicense = new UserLicenseDTO();
		if (licensesForm.getNumber() != null)
			userLicense.setLicenseNumber(licensesForm.getNumber());
		if (licensesForm.getExpirationDate() != null)
			userLicense.setExpirationDate(licensesForm.getExpirationDate());
		if (licensesForm.getIssueDate() != null)
			userLicense.setIssueDate(licensesForm.getIssueDate());

		try {
			UserLicenseAssociation userLicenseAssoc = licenseService.saveOrUpdateUserLicense(Long.valueOf(licensesForm.getLicense()), getCurrentUser().getId(), userLicense);

			if (!licensesForm.getFile().isEmpty()) {
				AssetDTO asset = createAssetDTO(licensesForm.getFile());
				assetManagementService.storeAssetForUserLicense(asset, userLicenseAssoc.getId());
			}

			messageHelper.addSuccess(bundle, "licenses.save.add.success");
		} catch (Exception e) {
			messageHelper.addError(bundle, "licenses.save.add.error");
			logger.error("error saving license id: ", e);
		}

		return "redirect:/profile-edit/licenses";
	}


	@RequestMapping(value = "/licensesadd", method = GET)
	public String licensesadd(Model model) {

		addStateAttributesToModel(model);

		return "web/pages/profileedit/licensesadd";
	}


	@RequestMapping(value = "/licensesadd", method = POST, consumes = MULTIPART_FORM_DATA_VALUE)
	public String licensesadd(
		@Valid @ModelAttribute("licensesForm") LicensesForm licensesForm,
		BindingResult bind,
		RedirectAttributes flash,
		Model model) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasFieldErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/profile-edit/licensesadd";
		}

		LicenseDTO license = new LicenseDTO();
		license.setState(licensesForm.getState());
		license.setName(licensesForm.getName());

		try {
			License newLicense = licenseService.findLicenseByName(licensesForm.getName());
			if (newLicense == null)
				newLicense = licenseService.saveOrUpdateLicense(license);

			UserLicenseDTO userLicense = new UserLicenseDTO();
			if (licensesForm.getNumber() != null)
				userLicense.setLicenseNumber(licensesForm.getNumber());
			if (licensesForm.getExpirationDate() != null)
				userLicense.setExpirationDate(licensesForm.getExpirationDate());
			if (licensesForm.getIssueDate() != null)
				userLicense.setIssueDate(licensesForm.getIssueDate());

			UserLicenseAssociation userLicenseAssoc = licenseService.saveOrUpdateUserLicense(newLicense.getId(), getCurrentUser().getId(), userLicense);

			if (!licensesForm.getFile().isEmpty()) {
				AssetDTO asset = createAssetDTO(licensesForm.getFile());
				assetManagementService.storeAssetForUserLicense(asset, userLicenseAssoc.getId());
			}

			messageHelper.addSuccess(bundle, "licenses.add.profile.success");
			return "redirect:/profile-edit/licenses";
		} catch (Exception e) {
			messageHelper.addError(bundle, "licenses.add.create.error");
			logger.error("error saving license: ", e);
		}

		addStateAttributesToModel(model);

		return "web/pages/profileedit/licensesadd";
	}


	@RequestMapping(value = "/licensesremove", method = GET)
	public String licensesremove(
		@RequestParam("id") Long id,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		try {
			licenseService.removeLicenseFromUser(id, getCurrentUser().getId());
			messageHelper.addSuccess(bundle, "licenses.remove.success");
		} catch (Exception e) {
			messageHelper.addError(bundle, "licenses.remove.error");
			logger.error("error removing license: ", e);
		}

		return "redirect:/profile-edit/licenses";
	}


	@RequestMapping(value = "/licenselist", method = GET)
	public void licenselist(
		Model model,
		@RequestParam("state") String stateId) {

		LicensePagination pagination = new LicensePagination(true);
		pagination = licenseService.findAllLicensesByStateId(stateId.toUpperCase(), pagination);

		model.addAttribute("response", CollectionUtilities.extractPropertiesList(pagination.getResults(), "id", "name"));
	}


	@RequestMapping(value = "/certifications", method = GET)
	public String certifications(Model model) {
		Profile profile = profileService.findProfile(getCurrentUser().getId());

		if (profile != null) {
			Long id = industryService.getDefaultIndustryForProfile(profile.getId()).getId();
			model.addAttribute("prefill_industry", id);
		}

		UserCertificationAssociationPagination currentCPagination = new UserCertificationAssociationPagination();
		currentCPagination.getFilters().put(
			UserCertificationAssociationPagination.FILTER_KEYS.WITH_ASSETS.toString(),
			UserCertificationAssociationPagination.ASSETS.NO.toString()
		);
		currentCPagination.setReturnAllRows();

		//process current certifications
		currentCPagination = certificationService.findAllVerifiedCertificationsByUserId(getCurrentUser().getId(), currentCPagination);
		List<Map<String, Object>> currentTables = listToTable(currentCPagination);
		model.addAttribute("current_certifications", currentTables);

		//process unverifiedverified certifications
		currentCPagination = certificationService.findAllPendingCertificationsByUserId(getCurrentUser().getId(), currentCPagination);
		List<Map<String, Object>> unverifiedTables = listToTable(currentCPagination);
		model.addAttribute("unverified_certifications", unverifiedTables);

		addIndustryListToModel(model);

		return "web/pages/profileedit/certifications";
	}

	/**
	 * Convert a pagination result list to a map
	 */
	private List<Map<String, Object>> listToTable(UserCertificationAssociationPagination currentCPagination) {
		List<UserCertificationAssociation> currentList = currentCPagination.getResults();
		List<Map<String, Object>> retTables = Lists.newArrayList();
		for (UserCertificationAssociation assoc : currentList) {
			Certification cert = assoc.getCertification();
			CertificationVendor vendor = cert.getCertificationVendor();
			retTables.add(newObjectMap(
				"industry", vendor.getCertificationType().getName(),
				"provider", vendor.getName(),
				"name", cert.getName(),
				"number", assoc.getCertificationNumber(),
				"cert_id", cert.getId(),
				"date", assoc.getCreatedOnString()));
		}
		return retTables;
	}


	@RequestMapping(value = "/certificationssave", method = POST, consumes = MULTIPART_FORM_DATA_VALUE)
	public String certificationssave(
			@Valid @ModelAttribute("certificationsForm") CertificationsForm certificationsForm,
			BindingResult bind,
			RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/profile-edit/certifications";
		}

		// validate filename
		if (certificationsForm.getFile() != null) {
			filenameValidator.validate(certificationsForm.getFile().getOriginalFilename(), bind);
			if (bind.hasErrors()) {
				messageHelper.setErrors(bundle, bind);
			}
		}

		if (certificationsForm.getCertification() == null) {
			messageHelper.addError(bundle, "certifications.save.certification.blank");
		}

		String providerId = certificationsForm.getProvider();

		if (StringUtils.isNumeric(providerId)) {
			CertificationVendor certificationVendor = certificationService.findCertificationVendorById(Long.parseLong(providerId, 10));
			if (certificationVendor != null && certificationVendor.isCertificationNumberRequired() && StringUtils.isBlank(certificationsForm.getNumber())) {
				messageHelper.addError(bundle, "certifications.save.certification.number.blank");
			}
		}

		if (bundle.hasErrors()) {
			return "redirect:/profile-edit/certifications";
		}

		UserCertificationDTO dto = new UserCertificationDTO();
		dto.setCertificationNumber(certificationsForm.getNumber());

		if (certificationsForm.getExpirationDate() != null) {
			dto.setExpirationDate(certificationsForm.getExpirationDate());
		}

		if (certificationsForm.getIssueDate() != null) {
			dto.setIssueDate(certificationsForm.getIssueDate());
		}

		try {
			UserCertificationAssociation existingCertification = certificationService.findActiveAssociationByCertificationIdAndUserId(certificationsForm.getCertification(), getCurrentUser().getId());

			if (existingCertification == null || existingCertification.getVerificationStatus().isFailed()) {
				UserCertificationAssociation userCertificationAssociation = certificationService.saveOrUpdateUserCertification(certificationsForm.getCertification(), getCurrentUser().getId(), dto);

				if (!certificationsForm.getFile().isEmpty()) {
					AssetDTO asset = createAssetDTO(certificationsForm.getFile());
					assetManagementService.storeAssetForUserCertification(asset, userCertificationAssociation.getId());
				}
				messageHelper.addSuccess(bundle, "certifications.save.add.success");
			} else {
				messageHelper.addError(bundle, "certifications.save.add.existing");
			}
		} catch (Exception e) {
			messageHelper.addError(bundle, "certifications.save.add.error");
			logger.error("error saving certification: ", e);
		}

		return "redirect:/profile-edit/certifications";
	}


	@RequestMapping(value = "/certificationsadd", method = GET)
	public String certificationsadd(Model model) {

		addIndustryListToModel(model);

		return "web/pages/profileedit/certificationsadd";
	}


	@RequestMapping(value = "/certificationsadd", method = POST, consumes = MULTIPART_FORM_DATA_VALUE)
	public String certificationsadd(
		@Valid @ModelAttribute("certificationsForm") CertificationsForm certificationsForm,
		BindingResult bind,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasFieldErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/profile-edit/certificationsadd";
		}

		String providerId = certificationsForm.getProvider();
		if ("other".equals(providerId)) {

			CertificationVendorDTO dto = new CertificationVendorDTO();
			dto.setName(certificationsForm.getCustomProvider());
			try {
				CertificationVendor certificationVendor = certificationService.saveOrUpdateCertificationVendor(dto, certificationsForm.getIndustry());
				providerId = certificationVendor.getId().toString();
			} catch (Exception e) {
				messageHelper.addError(bundle, "certifications.add.companyname");
				logger.error("error lookingup certification vendor id: ", e);
			}
		}

		CertificationDTO certification = new CertificationDTO();
		certification.setName(certificationsForm.getName());

		try {
			certification.setCertificationVendorId(Long.valueOf(providerId));
		} catch (NumberFormatException e) {
			logger.error("error parsing certification vendor id: ", e);
		}

		try {
			Certification newCertification = certificationService.saveOrUpdateCertification(certification);

			UserCertificationDTO userCertification = new UserCertificationDTO();
			userCertification.setCertificationNumber(certificationsForm.getNumber());
			if (certificationsForm.getExpirationDate() != null)
				userCertification.setExpirationDate(certificationsForm.getExpirationDate());
			if (certificationsForm.getIssueDate() != null)
				userCertification.setIssueDate(certificationsForm.getIssueDate());

			UserCertificationAssociation userCert = certificationService.saveOrUpdateUserCertification(newCertification.getId(), getCurrentUser().getId(), userCertification);
			if (!certificationsForm.getFile().isEmpty()) {
				AssetDTO asset = createAssetDTO(certificationsForm.getFile());
				assetManagementService.storeAssetForUserCertification(asset, userCert.getId());
			}

			messageHelper.addSuccess(bundle, "certifications.add.profile.success");
			return "redirect:/profile-edit/certifications";

		} catch (Exception e) {
			logger.error("error saving certification id: ", e);
		}

		messageHelper.addError(bundle, "certifications.add.create.error");
		return "redirect:/profile-edit/certificationsadd";
	}


	@RequestMapping(value = "/certificationsremove", method = GET)
	public String certificationsRemove(
		@RequestParam("id") Long id,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		try {
			certificationService.removeCertificationFromUser(id, getCurrentUser().getId());
			messageHelper.addSuccess(bundle, "certifications.remove.success");
		} catch (Exception e) {
			logger.error("error removing certification id: ", e);
			messageHelper.addError(bundle, "certifications.remove.error");
		}

		return "redirect:/profile-edit/certifications";
	}


	@RequestMapping(value = "/certificationslist", method = GET, produces = APPLICATION_JSON_VALUE)
	public void certificationsList(
		@RequestParam("industry") Long industryId,
		@RequestParam(value = "provider", required = false) Long vendorId,
		Model model) {

		CertificationVendorPagination vendorPagination = new CertificationVendorPagination(true);
		vendorPagination = certificationService.findCertificationVendorByTypeId(vendorPagination, industryId);

		if (vendorId == null) {
			model.addAttribute("response", CollectionUtilities.extractPropertiesList(vendorPagination.getResults(), "id", "name"));
			return;
		}

		CertificationVendor vendor = certificationService.findCertificationVendorById(vendorId);

		boolean isRequired = false;
		String instructions = "";

		if (vendor != null) {
			isRequired = vendor.isCertificationNumberRequired();
			instructions = (vendor.getInstruction() != null) ? vendor.getInstruction().getDescription() : "";
		}

		CertificationPagination pagination = new CertificationPagination(true);
		pagination = certificationService.findAllCertificationByVendor(vendorId, pagination);

		model.addAttribute("response", newObjectMap(
				"list", CollectionUtilities.extractPropertiesList(pagination.getResults(), "id", "name"),
				"vendor", newObjectMap(
						"id", vendorId,
						"is_required", isRequired,
						"instructions", instructions
				),
				"all", CollectionUtilities.extractPropertiesList(vendorPagination.getResults(), "id", "name")
		));
	}

	@VelvetRope(
		venue = Venue.HIDE_PROF_INSURANCE,
		bypass = true,
		redirectPath = "/profile-edit",
		message = "You do not have access to this feature."
	)
	@RequestMapping(value = "/insurance", method = GET)
	public String insurance(Model model) {

		model.addAttribute("workers_comp_insurance_id", Constants.WORKERS_COMP_INSURANCE_ID);

		try {
			//process verified insurance list
			UserInsuranceAssociationPagination currentIPagination = new UserInsuranceAssociationPagination(true);
			currentIPagination = insuranceService.findVerifiedInsuranceAssociationsByUser(getCurrentUser().getId(), currentIPagination);
			List<UserInsuranceAssociation> currentList = currentIPagination.getResults();
			model.addAttribute("current_insurance", currentList);

			//process unverified insurance list
			UserInsuranceAssociationPagination unverifiedIPagination = new UserInsuranceAssociationPagination(true);
			unverifiedIPagination = insuranceService.findUnverifiedInsuranceAssociationsByUser(getCurrentUser().getId(), unverifiedIPagination);
			List<UserInsuranceAssociation> unverifiedList = unverifiedIPagination.getResults();
			model.addAttribute("unverified_insurance", unverifiedList);
		} catch (Exception e) {
			logger.error("error processing insurance list: ", e);
		}

		addIndustryListToModel(model);

		Profile profile= profileService.findProfile(getCurrentUser().getId());
		if (profile != null) {
			Long id = industryService.getDefaultIndustryForProfile(profile.getId()).getId();
			model.addAttribute("prefill_industry", id);
		}

		return "web/pages/profileedit/insurance";
	}


	private void addIndustryListToModel(Model model) {

		IndustryPagination pagination = new IndustryPagination();
		pagination.setReturnAllRows();

		IndustryPagination resultPagination = invariantDataService.findAllIndustries(pagination);

		List<Industry> industries = resultPagination.getResults();
		List<Map<String, Object>> list = Lists.newArrayList();
		for (Industry industry : industries) {
			list.add(newObjectMap(
				"id", industry.getId(),
				"name", industry.getName()));
		}
		model.addAttribute("industry", list);
	}


	@RequestMapping(value = "/insuranceadd", method = GET)
	public String insuranceadd() {

		return "web/pages/profileedit/insuranceadd";
	}


	@RequestMapping(value = "/insurancesave", method = POST, consumes = MULTIPART_FORM_DATA_VALUE)
	public String insurancesave(
		@Valid @ModelAttribute("insuranceForm") InsuranceForm insuranceForm,
		BindingResult bind,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (insuranceForm.isNotApplicableOverride() && !insuranceForm.getInsuranceId().equals(Constants.WORKERS_COMP_INSURANCE_ID)) {
			messageHelper.addError(bundle, "Only Workers Compensantion insurance can be overridden.");
		}

		if (!insuranceForm.isNotApplicableOverride()) {
			if (StringUtils.isEmpty(insuranceForm.getProvider())) { messageHelper.addError(bundle, "Provider is required."); }
			if (StringUtils.isEmpty(insuranceForm.getCoverage())) { messageHelper.addError(bundle, "Coverage is required."); }
			if (StringUtils.isEmpty(insuranceForm.getPolicyNumber())) { messageHelper.addError(bundle, "Policy Number is required."); }
			if (!insuranceForm.hasFile()) { messageHelper.addError(bundle, "Insurance certification attachment is required."); }
		}

		if (bundle.hasErrors()) {
			return "redirect:/profile-edit/insurance";
		}

		// validate filename
		if (insuranceForm.hasFile()) {
			filenameValidator.validate(insuranceForm.getFile().getOriginalFilename(), bind);
			if (bind.hasErrors()) {
				messageHelper.setErrors(bundle, bind);
				return "redirect:/profile-edit/insurance";
			}
		}

		InsuranceDTO insuranceDTO = new InsuranceDTO();
		insuranceDTO.setInsuranceId(insuranceForm.getInsuranceId());
		insuranceDTO.setProvider(insuranceForm.getProvider());
		insuranceDTO.setPolicyNumber(insuranceForm.getPolicyNumber());
		insuranceDTO.setCoverage(insuranceForm.getCoverage());
		insuranceDTO.setNotApplicableOverride(insuranceForm.isNotApplicableOverride());
		if (insuranceForm.getIssueDate() != null) {
			insuranceDTO.setIssueDate(insuranceForm.getIssueDate());
		}
		if (insuranceForm.getExpirationDate() != null) {
			insuranceDTO.setExpirationDate(insuranceForm.getExpirationDate());
		}

		try {
			UserInsuranceAssociation association = insuranceService.addInsuranceToUser(getCurrentUser().getId(), insuranceDTO);

			if (!insuranceForm.getFile().isEmpty()) {
				AssetDTO asset = createAssetDTO(insuranceForm.getFile());
				assetManagementService.storeAssetForUserInsurance(asset, association.getId());
			}

			messageHelper.addSuccess(bundle, "insurance.save.success");
		} catch (Exception e) {
			messageHelper.addError(bundle, "insurance.save.error");
			logger.error("error saving insurance form: ", e);
		}

		return "redirect:/profile-edit/insurance";
	}


	private AssetDTO createAssetDTO(MultipartFile file) throws IOException {
		File tempDest = File.createTempFile("profile", ".dat");
		file.transferTo(tempDest);

		AssetDTO asset = new AssetDTO();
		asset.setSourceFilePath(tempDest.getPath());
		asset.setName(file.getOriginalFilename());
		asset.setMimeType(file.getContentType());
		asset.setAssociationType("none");
		asset.setLargeTransformation(false);
		asset.setSmallTransformation(false);
		asset.setActive(true);
		return asset;
	}


	@RequestMapping(value = "/insuranceremove", method = GET)
	public String insuranceRemove(
		@RequestParam("id") Long id,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		try {
			insuranceService.removeInsuranceFromUser(id);
			messageHelper.addSuccess(bundle, "insurance.remove.success");
		} catch (Exception e) {
			messageHelper.addError(bundle, "insurance.remove.error");
			logger.error("error removing insurance from user: ", e);
		}

		return "redirect:/profile-edit/insurance";
	}


	@RequestMapping(
		value = "/insurancelist",
		method = GET)
	public void insurancelist(Model model, @RequestParam("industry") Long industryId) {
		List<Insurance> list = insuranceService.findVerifiedInsuranceByIndustry(industryId);

		model.addAttribute("response", newObjectMap(
				"list", CollectionUtilities.extractPropertiesList(list, "id", "name")
		));
	}


	@RequestMapping(
		value = "/languages",
		method = GET)
	public String languageShow(Model model) throws Exception {
		model.addAttribute("profileLanguage", new ProfileLanguageDTO());
		return showLanguages(model);
	}


	@RequestMapping(
		value = "/languages",
		method = POST)
	public String languageSave(
		@ModelAttribute("profileLanguage") ProfileLanguageDTO languageDTO,
		BindingResult result,
		Model model ) throws Exception {

		MessageBundle bundle = messageHelper.newBundle(model);

		profileLanguageValidator.validate(languageDTO, result);

		if (!result.hasErrors()) {
			try {
				profileService.saveOrUpdateProfileLanguage(getCurrentUser().getId(), languageDTO);
				messageHelper.addSuccess(bundle, "languages.save.success");
				return "redirect:/profile-edit/languages";
			} catch (Exception ex) {
				logger.error("Failed to save ProfileLanguage", ex);
				messageHelper.addError(bundle, "languages.save.failure");
			}
		} else {
			messageHelper.setErrors(bundle, result);
		}

		return showLanguages(model);
	}


	private String showLanguages(Model model) throws Exception {
		// Populate language options.
		List<ProfileLanguage> currentLanguages = profileService.findProfileLanguages(getCurrentUser().getId());
		List<Language> languages = invariantDataService.getLanguages();

		// Filter out any languages that are already selected.
		for (ProfileLanguage currentLanguage : currentLanguages) {
			for (Iterator<Language> i = languages.iterator(); i.hasNext(); ) {
				Language language = i.next();
				if (language.getId().equals(currentLanguage.getLanguage().getId())) {
					i.remove();
				}
			}
		}

		model.addAttribute("current_languages", currentLanguages);
		model.addAttribute("languages", languages);
		model.addAttribute("proficiency_types", invariantDataService.getLanguageProficiencyTypes());

		return "web/pages/profileedit/languages";
	}

	@RequestMapping(
		value = "/calendar_sync",
		method = GET)
	public String calendarSync(Model model) {

		boolean isAuthorizedToWM = googleCalendarService.isAuthorizedToWM(getCurrentUser().getId());
		boolean hasCalendarSettings = googleCalendarService.hasCalendarSettings(getCurrentUser().getId());
		if(isAuthorizedToWM) {
			Map<String, String> calendars = googleCalendarService.getCalendars(getCurrentUser().getId());
			if(calendars != null) {
				model.addAttribute("calendars", calendars);
			}
		}

		model.addAttribute("hasSettings", hasCalendarSettings);
		model.addAttribute("isAuthorized", isAuthorizedToWM);
		return "web/pages/profile-edit/calendar_sync";
	}


	@RequestMapping(
		value = "/language_remove",
		method = GET)
	public String languageRemove(
		@RequestParam(value = "id", required = false) Long id,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (id != null) {
			try {
				profileService.deleteProfileLanguage(id);
				messageHelper.addSuccess(bundle, "languages.delete.success");
			} catch (Exception ex) {
				logger.error("failed to delete languageId: " + id, ex);
				messageHelper.addError(bundle, "languages.delete.failure");
			}
		}
		return "redirect:/profile-edit/languages";
	}


	@RequestMapping(
		value = "/exclude_postal_code",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder excludePostalCode(
		@RequestParam(value = "postal_code", required = false) List<String> postalCodes) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		ExtendedUserDetails user = getCurrentUser();

		// Make sure a postal code was entered.
		if (!postalCodes.isEmpty()) {

			List<String> existing = Lists.newLinkedList();

			// Get existing postal codes.
			Address address = profileService.findCompanyAddress(user.getId());
			if (address != null) {
				existing.add(address.getPostalCode());
			}

			Profile profile = profileService.findProfile(user.getId());
			if (profile != null) {
				Long addressId = profile.getAddressId();
				if (addressId != null) {
					existing.add(addressService.findById(addressId).getPostalCode());
				}
			}

			// Make sure they aren't trying to block either their company address or profile address.
			if (existing.containsAll(postalCodes)) {
				messageHelper.addMessage(response, "profile.exclude_postal_code.self");
			}

			// Make sure they don't already have this postal code added.
			List<String> blackListedZipCodes = profileService.findBlacklistedZipcodesForUser(user.getId());
			if (blackListedZipCodes.containsAll(postalCodes)) {
				messageHelper.addMessage(response, "profile.exclude_postal_code.duplicate");
			}

			for (String code : postalCodes) {
				PostalCode pc = invariantDataService.getPostalCodeByCode(code);
				if (pc == null)
					messageHelper.addMessage(response, "NotNull.postalCode", code);
			}

			postalCodes.addAll(blackListedZipCodes);

			if (!response.hasMessages()) {
				try {
					profileService.setBlacklistedZipcodesForUser(user.getId(), postalCodes);

					return response.setSuccessful(true);

				} catch (Exception ex) {
					logger.error("error saving postalCodes", ex);
				}
			}
		}

		return response;
	}


	@RequestMapping(
		value = "/remove_postal_code",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder removePostalCode(@RequestParam(value = "postal_code", required = false) List<String> postalCodes) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);
		Long userId = getCurrentUser().getId();

		if (!postalCodes.isEmpty()) {
			// Get existing blacklisted postal codes.
			List<String> blackListedZipCodes = profileService.findBlacklistedZipcodesForUser(userId);
			if (blackListedZipCodes == null) {
				blackListedZipCodes = Collections.emptyList();
			}

			// Make sure this postal code exists.
			if (blackListedZipCodes.removeAll(postalCodes)) {
				try {
					profileService.setBlacklistedZipcodesForUser(userId, blackListedZipCodes);
					return response.setSuccessful(true);
				} catch (Exception ex) {
					logger.error("error saving postalCodes", ex);
				}
			}
		}

		return response;
	}


	@RequestMapping(
		value = "/photo",
		method = GET)
	public String photo(
		Model model,
		final HttpServletRequest request) {

		UserAssetAssociation assetAssociation = userService.findUserAvatars(getCurrentUser().getId());
		if (assetAssociation != null) {
			model.addAttribute("avatar", assetAssociation.getTransformedSmallAsset());
		}
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/profile-edit/photo"));

		cacheRefererForRedirect(request);

		return "web/pages/profileedit/photo";
	}

	private String redirectCacheKey() {
		return getCurrentUser().getId() + ":redirect_uri_after_photo_update";
	}

	/**
	 * Cache the referer in case you came from the assignment details page.
	 */
	private void cacheRefererForRedirect(final HttpServletRequest request) {
		final String referer = request.getHeader("referer");
		if (StringUtils.isNotEmpty(referer)) {
			try {
				String path = new URL(referer).getPath();
				if (path.startsWith("/assignments/details/")) {
					redisAdapter.set(redirectCacheKey(), path, 300); // expire in 5 minutes
				}
			} catch (Exception e) {
				logger.error("Error trying to cache referer: " + e.getMessage());
			}
		}
	}

	@RequestMapping(
		value = "/photocrop",
		method = GET)
	public String cropPhoto(Model model) throws IOException {

		UserAssetAssociation assetAssociation = userService.findUserAvatars(getCurrentUser().getId());
		if (assetAssociation == null) {
			return "redirect:/profile-edit/photo";
		}

		BufferedImage image = ImageIO.read(new URL(assetAssociation.getAsset().getUri()));
		int width = image.getWidth();
		int height = image.getHeight();
		int adjust = Math.round(Math.min(width, height) / 2 * .5F);

		AssetDTO dto = new AssetDTO();
		dto.getTransformerParameters().setCrop(width / 2 - adjust, height / 2 - adjust, width / 2 + adjust, height / 2 + adjust);

		model.addAttribute("avatarOriginal", assetAssociation.getAsset().getUri());
		model.addAttribute("startCoords", dto.getTransformerParameters());
		model.addAttribute("originalImageWidth", width);
		model.addAttribute("originalImageHeight", height);

		return "web/pages/profileedit/photocrop";
	}


	@RequestMapping(
		value = "/photocrop",
		method = POST)
	public String doCropPhoto(
		AssetDTO.TransformerParameters transformation,
		MessageBundle messages,
		RedirectAttributes flash) {

		UserAssetAssociation assetAssociation = userService.findUserAvatars(getCurrentUser().getId());
		if (assetAssociation == null) {
			return "redirect:/profile-edit/photo";
		}

		flash.addFlashAttribute("bundle", messages);

		if (!transformation.isConfigured()) {
			messageHelper.addError(messages, "profile.photo.crop.invalid");
			return "redirect:/profile-edit/photocrop";
		}

		Asset asset = assetAssociation.getAsset();

		AssetDTO dto = AssetDTO.newDTO(asset);
		dto.setAssociationType(UserAssetAssociationType.AVATAR);
		dto.setLargeTransformation(true);
		dto.setSmallTransformation(true);
		dto.setTransformerParameters(transformation);

		try {
			assetManagementService.addAssetToUser(dto, getCurrentUser().getId());

			updateAvatarInSession();

			messageHelper.addSuccess(messages, "profile.photo.crop.success");
		} catch (Exception e) {
			messageHelper.addSuccess(messages, "profile.photo.crop.failure");
		}

		// Sometime we want to redirect to another page after photo croping, e.g.,
		// redirect back to assignment details after user creates profile picture
		// via the requirements well
		Optional<Object> redirectUri = redisAdapter.get(redirectCacheKey());
		if (redirectUri.isPresent()) {
			redisAdapter.delete(redirectCacheKey());
			return "redirect:" + redirectUri.get();
		}

		return "redirect:/profile-edit/photo";
	}

	/**
	 * @see FileUploadController
	 */
	@RequestMapping(
		value = "/photoupload",
		method = POST,
		produces = APPLICATION_JSON_VALUE,
		consumes = APPLICATION_OCTET_STREAM_VALUE
	)
	public @ResponseBody AjaxResponseBuilder photoUpload(HttpServletRequest request) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		String fileName = StringUtilities.urlDecode(request.getHeader("X-File-Name"));
		String fileType = StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, ".") + 1);
		String referer = StringUtils.defaultIfEmpty(request.getHeader("Referer"), EMPTY);
		String path = StringUtilities.getPathFromURL(StringUtilities.urlDecode(referer));

		if(path.isEmpty()) {
			logger.error(String.format("Malformed URL in file upload: %s", referer));
			return response;
		}

		MapBindingResult bindName = new MapBindingResult(Maps.newHashMap(), "fileName");
		MapBindingResult bindType = getFiletypeErrors(fileType, path);

		if (bindName.hasErrors() || bindType.hasErrors()) {
			List<String> errorList = Lists.newLinkedList();
			errorList.addAll(messageHelper.getAllErrors(bindName));
			errorList.addAll(messageHelper.getAllErrors(bindType));
			response.setMessages(errorList);
		}
		String contentType = MimeTypeUtilities.guessMimeType(fileName);

		if (!MimeTypeUtilities.isImage(contentType)) {
			messageHelper.addMessage(response, "upload.invalid", contentType);
			return response;
		}

		try {
			// Create temp file from stream
			File tmpFile = File.createTempFile("avatar", null);
			FileUtils.copyInputStreamToFile(request.getInputStream(), tmpFile);

			AssetDTO dto = new AssetDTO();
			dto.setSourceFilePath(tmpFile.getAbsolutePath());
			dto.setName(fileName);
			dto.setMimeType(contentType);
			dto.setAssociationType(UserAssetAssociationType.AVATAR);
			dto.setLargeTransformation(true);
			dto.setSmallTransformation(true);

			Asset asset = assetManagementService.storeAssetForUser(dto, getCurrentUser().getId());

			updateAvatarInSession();

			return response
				.setSuccessful(true)
				.addData("assetId", asset.getId())
				.addData("assetUri", asset.getUri());
		} catch (Exception e) {
			messageHelper.addMessage(response, "upload.IOException", contentType);

			return response;
		}
	}


	/**
	 * @see FileUploadController
	 */
	@RequestMapping(
		value = "/photoupload",
		method = POST,
		produces = TEXT_HTML_VALUE,
		consumes = MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody String photoUploadForIE(@RequestParam("qqfile") MultipartFile attachment) {
		MessageBundle messages = messageHelper.newBundle();

		String fileName = attachment.getOriginalFilename();
		String contentType = attachment.getContentType();

		if (!MimeTypeUtilities.isImage(contentType)) {
			messageHelper.addError(messages, "upload.invalid", contentType);
			return new JSONObject(new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(messages.getAllMessages())
			).toString();
		}

		try {
			// Create temp file from stream
			File tmpFile = File.createTempFile("avatar", null);
			FileUtils.copyInputStreamToFile(attachment.getInputStream(), tmpFile);

			AssetDTO dto = new AssetDTO();
			dto.setSourceFilePath(tmpFile.getAbsolutePath());
			dto.setName(fileName);
			dto.setMimeType(contentType);
			dto.setAssociationType(UserAssetAssociationType.AVATAR);
			dto.setLargeTransformation(true);
			dto.setSmallTransformation(true);

			Asset asset = assetManagementService.storeAssetForUser(dto, getCurrentUser().getId());

			updateAvatarInSession();

			return new JSONObject(new AjaxResponseBuilder()
				.setSuccessful(true)
				.addData("assetId", asset.getId())
				.addData("assetUri", asset.getUri())
			).toString();
		} catch (Exception e) {
			messageHelper.addError(messages, "upload.IOException", contentType);

			return new JSONObject(new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(messages.getAllMessages())
			).toString();
		}
	}

	@RequestMapping(
		value = "/media-remove",
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	public @ResponseBody AjaxResponseBuilder mediaRemove(
		@RequestParam("id") Long id,
		@RequestParam("type") String type,
		@RequestParam("userNumber") String userNumber) {

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setSuccessful(false)
			.setRedirect("/profile");

		MessageBundle bundle = MessageBundle.newInstance();
		Long userId = getCurrentUser().getId();

		if (!userId.equals(userService.findUserByUserNumber(userNumber).getId())) {
			messageHelper.addError(bundle, "profile.photo.change.access.fail");
			return response.setMessages(bundle.getErrors());
		}
		try {
			if (type.equals("asset")) {
				assetManagementService.removeAssetFromUser(id, getCurrentUser().getId());
			} else if (type.equals("link")) {
				externalLinkService.removeLinkAssociation(id, userNumber);
			}
		} catch (Exception e) {
			messageHelper.addError(bundle, "profile.photo.change.delete.fail");
			return response.setMessages(bundle.getErrors());
		}
		messageHelper.addSuccess(bundle, "profile.photo.change.delete.success");

		return response.setSuccessful(true).setMessages(bundle.getSuccess());
	}

	@RequestMapping(
		value = "/media-edit",
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	public @ResponseBody AjaxResponseBuilder mediaedit(
		@RequestParam("id") Long id,
		@RequestParam("type") String type,
		@RequestParam("permission") String permission,
		@RequestParam("userNumber") String userNumber) {

		MessageBundle bundle = MessageBundle.newInstance();
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		Long userId = getCurrentUser().getId();
		if (!userId.equals(userService.findUserByUserNumber(userNumber).getId())) {
			messageHelper.addError(bundle, "profile.photo.change.access.fail");

			return response.setMessages(bundle.getErrors());
		}
		try {
			if (type.equals("asset")) {
				Asset asset = assetManagementService.findAssetById(id);
				AssetDTO dto = AssetDTO.newDTO(asset);
				dto.setAvailabilityTypeCode(permission);
				assetManagementService.updateAsset(id, dto);
				messageHelper.addSuccess(bundle, "profile.photo.change.permissions.success");

				return response.setSuccessful(true).setMessages(bundle.getSuccess());
			} else if (type.equals("link")) {
				UserLinkAssociation association = externalLinkService.findLinkAssociationById(id);
				Link link = association.getLink();
				link.setAvailability(new AvailabilityType(permission));
				messageHelper.addSuccess(bundle, "profile.photo.change.permissions.success");

				externalLinkService.saveOrUpdateLink(link);
				return response.setSuccessful(true).setMessages(bundle.getSuccess());
			}
		} catch (Exception e) {
			messageHelper.addError(bundle, "profile.photo.change.permissions.fail");
		}

		return response.setMessages(bundle.getErrors());
	}

	@RequestMapping(
		value = "/media-save-embedded-video",
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	public @ResponseBody Map<String, Object> saveProfileEmbedVideo(
		@RequestParam("embedUrl") String embedUrl,
		@RequestParam("permission") String permission,
		@RequestParam("assetOrder") Integer assetOrder,
		@RequestParam("userNumber") String userNumber) throws Exception {

		Link link = new Link(null, embedUrl, new AvailabilityType(permission));
		externalLinkService.saveOrUpdateExternalLink(link, userNumber, permission, assetOrder);

		return newObjectMap(
				"successful", true
		);
	}
	@RequestMapping(
		value = "/media-next-position",
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	public @ResponseBody Map<String, Object> nextMediaInsertPosition(
		@RequestParam("userNumber") String userNumber) throws Exception {

		Integer position;

		User user = userService.findUserByUserNumber(userNumber);

		Set<Integer> assetOrders = Sets.newHashSet();
		List<UserAssetAssociation> profileMedias = profileService.findAllUserProfileImageAssociations(user.getId());
		profileMedias.addAll(profileService.findAllUserProfileVideoAssociations(user.getId()));

		for(UserAssetAssociation association : profileMedias) {
			assetOrders.add(association.getAsset().getOrder());
		}

		position = profileMedias.size();
		List<UserLinkAssociation> linkAssociations =  profileService.findAllUserProfileEmbedVideoAssociations(user.getId());
		for(UserLinkAssociation association : linkAssociations) {
			assetOrders.add(association.getAssetOrder());
		}
		position += linkAssociations.size();

		for(int i = 0; i < assetOrders.size(); ++i) {
			if(!assetOrders.contains(i)) {
				position = i;
				break;
			}
		}

		return newObjectMap(
				"position", position
		);
	}

	@RequestMapping(
		value = "/media-download-videos",
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody Map<String, Map<String, Object>> downloadProfileVideo(
		@RequestParam("userNumber") String userNumber) throws Exception {

		List<UserAssetAssociation> associations = profileService.findAllUserProfileVideoAssociations(userNumber);

		Map<String, Map<String, Object>> response = Maps.newHashMap();

		for (UserAssetAssociation association : associations) {
			int index = association.getAsset().getName().lastIndexOf(".");
			String fileType = association.getAsset().getName().substring(index + 1);
			response.put(String.valueOf(association.getAsset().getOrder()), newObjectMap(
				"fileType", fileType,
				"id", association.getAsset().getId(),
				"code", association.getAsset().getAvailability().getCode(),
				"uuid", "/asset/" + association.getAsset().getUUID()
			));
		}

		List<UserLinkAssociation> linkAssociations = profileService.findAllUserProfileEmbedVideoAssociations(userNumber);
		for (UserLinkAssociation association : linkAssociations) {
			response.put(String.valueOf(association.getAssetOrder()), newObjectMap(
				"id", association.getId(),
				"code", association.getLink().getAvailability().getCode(),
				"remoteUri", association.getLink().getRemoteUri()
			));
		}

		return response;
	}


	@RequestMapping(
		value = "/media-save-photo",
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	public @ResponseBody AjaxResponseBuilder saveprofilephoto(
		@RequestParam("asset_id") Long asset_id,
		@RequestParam("caption") String caption,
		@RequestParam("permission") String permission,
		@RequestParam("userNumber") String userNumber) {

		MessageBundle bundle = MessageBundle.newInstance();
		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setSuccessful(false)
			.setRedirect("/profile");

		Long userId = getCurrentUser().getId();
		if (!userId.equals(userService.findUserByUserNumber(userNumber).getId())) {
			messageHelper.addError(bundle, "profile.photo.change.access.fail");
			return response.setMessages(bundle.getErrors());
		}
		try {
			Asset asset = assetManagementService.findAssetById(asset_id);
			asset.setDescription(caption);
			asset.setAvailability(new AvailabilityType(permission));
			assetManagementService.updateAsset(asset);
			UserAssetAssociation assetAssoc = profileService.findUserAssetAssociation(getCurrentUser().getId(), asset_id);
			profileService.undeleteAssetAssociation(assetAssoc);
			messageHelper.addSuccess(bundle, "profile.photo.change.save.success");

			return response.setSuccessful(true).setMessages(bundle.getSuccess());
		} catch (Exception e) {
			messageHelper.addError(bundle, "profile.photo.change.save.fail");
			return response.setMessages(bundle.getErrors());
		}
	}

	@RequestMapping(
		value = "/download_profile_photos",
		produces = APPLICATION_JSON_VALUE,
		method = GET)
	public @ResponseBody AjaxResponseBuilder downloadProfilePhotos(
		@RequestParam("userNumber") String userNumber) throws IOException {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);
		Long userId = userService.findUserByUserNumber(userNumber).getId();
		List<String> assetIds = Lists.newArrayList();
		List<UserAssetAssociation> assetAssociations = profileService.findAllUserProfileAndAvatarImageAssociations(userId);

		boolean isGroupOwner = !userGroupService.findCompanyOwnedGroupsHavingUserAsMember(getCurrentUser().getCompanyId(), userId).isEmpty();

		for (UserAssetAssociation assetassoc : assetAssociations) {
			if (assetassoc != null) {
				if ((assetassoc.getAsset().getAvailability().getCode().equals("group") && isGroupOwner) || assetassoc.getAsset().getAvailability().getCode().equals("all") || assetassoc.getAsset().getAvailability().getCode().equals("guest")) {
					assetIds.add(assetassoc.getAsset().getUUID());
				}
			}
		}
		if (CollectionUtilities.isEmpty(assetIds)) {
			messageHelper.addMessage(response, "profile.photo.change.download.empty.fail");
			return response.setSuccessful(false);
		}
		try {
			assetbundler.bundleAssetsForUser(assetIds, getCurrentUser().getId());
		} catch (Exception e) {
			messageHelper.addMessage(response, "profile.photo.change.download.exception.fail");
			return response.setSuccessful(false);
		}
		messageHelper.addMessage(response, "profile.photo.change.download.success");
		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/media-upload",
		method = POST,
		produces = APPLICATION_JSON_VALUE,
		consumes = APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody Map<String, Object> profilePhotoUploadImages(
		@RequestParam("imageType") String imageType,
		@RequestParam("order") Integer order,
		@RequestParam("userNumber") String userNumber,
		@RequestParam("caption") String caption,
		@RequestParam("permission") String permission,
		HttpServletRequest request) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		String fileName = StringUtilities.urlDecode(request.getHeader("X-File-Name"));
		String fileType = StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, ".") + 1);
		String referer = StringUtils.defaultIfEmpty(request.getHeader("Referer"), EMPTY);
		String path = StringUtilities.getPathFromURL(StringUtilities.urlDecode(referer));
		List<String> errorList = new LinkedList<>();

		if (path.isEmpty()) {
			logger.error(String.format("Malformed URL in file upload: %s", referer));
			errorList.add(messageHelper.getMessage("upload.exception"));
		}

		MapBindingResult bindName = new MapBindingResult(Maps.newHashMap(), "fileName");
		MapBindingResult bindType = getFiletypeErrors(fileType, path);
		if (bindName.hasErrors() || bindType.hasErrors() || !errorList.isEmpty()) {

			errorList.addAll(messageHelper.getAllErrors(bindName));
			errorList.addAll(messageHelper.getAllErrors(bindType));

			return ImmutableMap.of(
				"successful", false,
				"errors", errorList
			);
		}
		String contentType = MimeTypeUtilities.guessMimeType(fileName);
		Long userId = getCurrentUser().getId();

		if (!userId.equals(userService.findUserByUserNumber(userNumber).getId())) {
			messageHelper.addMessage(response, "profile.photo.change.access.fail");
			return newObjectMap(
				"successful", false,
				"errors", response.getMessages()
			);
		}

		if (!MimeTypeUtilities.isImage(contentType) && !MimeTypeUtilities.isMedia(contentType)) {
			messageHelper.addMessage(response, "upload.invalid", contentType);
			return newObjectMap(
					"successful", false,
					"errors", response.getMessages()
			);
		}

		try {
			// Create temp file from stream
			File tmpFile = File.createTempFile(imageType, null);
			FileUtils.copyInputStreamToFile(request.getInputStream(), tmpFile);

			AssetDTO dto = new AssetDTO();
			dto.setSourceFilePath(tmpFile.getAbsolutePath());
			dto.setName(fileName);
			dto.setMimeType(contentType);
			dto.setOrder(order);
			dto.setDescription(caption);
			dto.setAvailabilityTypeCode(permission);
			boolean isMediaType = MimeTypeUtilities.isMedia(contentType);
			dto.setLargeTransformation(!isMediaType);
			dto.setSmallTransformation(!isMediaType);
			dto.setAssociationType(UserAssetAssociationType.PROFILE_IMAGE);
			if (isMediaType) {
				dto.setAssociationType(UserAssetAssociationType.PROFILE_VIDEO);
			}

			UserAssetAssociation assetAssoc = assetManagementService.storeSlottedAssetForUser(dto, getCurrentUser().getId(), order);

			FileUtils.deleteQuietly(tmpFile);

			return newObjectMap(
					"successful", true,
					"assetId", assetAssoc.getAsset().getId(),
					"largeAssetUri", assetAssoc.getAsset().getUri(),
					"smallAssetUri", assetAssoc.getAsset().getUri(),
					"order", order,
					"description", caption,
					"code", permission,
					"fileByteSize", FileUtils.byteCountToDisplaySize(assetAssoc.getAsset().getFileByteSize()),
					"name", dto.getName(),
					"UUID", assetAssoc.getAsset().getUUID()
			);

		} catch (Exception e) {
			messageHelper.addMessage(response, "upload.IOException", contentType);
			return newObjectMap(
					"successful", false,
					"errors", response.getMessages()
			);
		}
	}

	 /*
	  * end of controller methods for the profile photo carousel
	  */

	@RequestMapping(
		value = "/photodelete",
		method = GET)
	public String photodelete(
		MessageBundle messages,
		RedirectAttributes flash) {

		UserAssetAssociation assetAssociation = userService.findUserAvatars(getCurrentUser().getId());
		if (assetAssociation == null) {
			return "redirect:/photo-edit/photo";
		}

		assetManagementService.removeAssetFromUser(assetAssociation.getAsset().getId(), getCurrentUser().getId());

		updateAvatarInSession();

		messageHelper.addSuccess(messages, "profile.photo.delete.success");

		flash.addFlashAttribute("bundle", messages);

		return "redirect:/profile-edit/photo";
	}

	/**
	 * The user's small avatar icon image is cached in the session for use
	 * throughout the site in the header. Update the image URL value in the session
	 * with the most recent avatar image.
	 */
	private void updateAvatarInSession() {

		UserAssetAssociation assetAssociation = userService.findUserAvatars(getCurrentUser().getId());

		String avatarUri = null;
		if (assetAssociation != null) {
			avatarUri = assetAssociation.getTransformedSmallAsset().getUri();
		}

		authenticationService.refreshSessionForUser(getCurrentUser().getId());
	}

	@RequestMapping(
		value = "/resumeupload",
		method = POST,
		consumes = ALL_VALUE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> resumeUpload(
		@RequestParam("qqfile") String filename,
		HttpServletRequest request) throws IOException {

		Map<String, Object> map = new HashMap<>();

		File file = File.createTempFile("resume_for_user_" + getCurrentUser().getId(), ".dat");

		FileOutputStream fos = new FileOutputStream(file);

		try {
			int bytesCopied = IOUtils.copy(request.getInputStream(), fos);

			AssetDTO dto = new AssetDTO();

			dto.setMimeType(MimeTypeUtilities.guessMimeType(filename));
			dto.setName(filename);
			dto.setAssociationType("resume");
			dto.setFileByteSize(bytesCopied);
			dto.setSourceFilePath(file.getAbsolutePath());

			Asset asset = assetManagementService.storeAssetForUser(dto, getCurrentUser().getId());

			CollectionUtilities.addToObjectMap(map,
				"successful", true,
				"id", asset.getId(),
				"filename", filename,
				"uuid", asset.getUUID());
		} catch (Exception e) {
			logger.error("error uploading resume: ", e);

			CollectionUtilities.addToObjectMap(map,
				"successful", false,
				"error", e.getMessage());
		}

		boolean success = file.delete();

		return map;
	}

	@RequestMapping(
		value = "/resumeupload",
		method = POST,
		produces = TEXT_HTML_VALUE,
		consumes = MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody String resumeUploadForIE(@RequestParam("qqfile") MultipartFile attachment) throws IOException {

		String fileName = attachment.getOriginalFilename();
		String contentType = attachment.getContentType();

		try {
			// Create temp file from stream
			File tmpFile = File.createTempFile("resume_for_user_" + getCurrentUser().getId(), ".dat");
			FileUtils.copyInputStreamToFile(attachment.getInputStream(),
				tmpFile);

			AssetDTO dto = new AssetDTO();
			dto.setSourceFilePath(tmpFile.getAbsolutePath());
			dto.setName(fileName);
			dto.setMimeType(contentType);
			dto.setAssociationType(UserAssetAssociationType.RESUME);

			return new JSONObject(new AjaxResponseBuilder().setSuccessful(true)).toString();

		} catch (Exception e) {
			logger.error("error uploading resume: ", e);
			return new JSONObject(new AjaxResponseBuilder().setSuccessful(false)).toString();
		}
	}

	@RequestMapping(
		value = "/resume/{id}",
		method = DELETE,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder resumeDelete(@PathVariable("id") Long assetId) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		if (assetId == null) {
			return response;
		}

		assetManagementService.removeAssetFromUser(assetId, getCurrentUser().getId());

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/blockresource",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder blockResource(
		@RequestParam("resource_id") Long blockedUserId) {

		ExtendedUserDetails user = getCurrentUser();
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		try {
			// Assuming always company wide blocking
			if (!user.hasAnyRoles("ACL_ADMIN", "ACL_MANAGER")) {
				messageHelper.addMessage(response, "blockresource.unauthorized");
				return response;
			}

			userService.blockUser(user.getId(), blockedUserId);
			messageHelper.addMessage(response, "blockworker.success");
			return response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("error occurred while blocking user with resource_id={}", blockedUserId, ex);
			messageHelper.addMessage(response, "blockresource.error");
		}

		return response;
	}

	@RequestMapping(
		value = "/blockvendor",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder blockVendor(
		@RequestParam("resource_id") Long blockedCompanyId) {

		ExtendedUserDetails user = getCurrentUser();
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		try {
			// Assuming always company wide blocking
			if (!user.hasAnyRoles("ACL_ADMIN", "ACL_MANAGER")) {
				messageHelper.addMessage(response, "blockvendor.unauthorized");
				return response;
			}

			vendorService.blockVendor(user.getId(), blockedCompanyId);
			messageHelper.addMessage(response, "blockvendor.success");
			return response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("error occurred while blocking vendor with resource_id={}", blockedCompanyId, ex);
			messageHelper.addMessage(response, "blockvendor.error");
		}

		return response;
	}

	@RequestMapping(
		value = "/unblockresource",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String unblockResourcePost(
		@RequestParam("resource_id") String blockedUserId,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		User blockedUser = userService.findUserByUserNumber(blockedUserId);

		if (blockedUser == null) {
			messageHelper.addError(bundle, "unblockresource.profile.notfound");
		} else {
			try {
				userService.unblockUser(getCurrentUser().getId(), blockedUser.getId());
				messageHelper.addSuccess(bundle, "unblockresource.success");
			} catch (Exception ex) {
				logger.error("error occurred while unblocking user with resource_id={}", blockedUserId, ex);
				messageHelper.addError(bundle, "unblockresource.error");
			}
		}
		return "redirect:/profile/" + blockedUserId;
	}


	@RequestMapping(
		value = "/unblockresource",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder unblockResourcePost(
		@RequestParam("resource_id") Long blockedUserId) {

		MessageBundle bundle = MessageBundle.newInstance();
		User blockedUser = userService.findUserById(blockedUserId);
		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setSuccessful(false)
			.setRedirect("/profile/" + blockedUser.getUserNumber());

		try {
			userService.unblockUser(getCurrentUser().getId(), blockedUserId);
			messageHelper.addSuccess(bundle, "unblockresource.success");

			return response
				.setSuccessful(true)
				.setMessages(bundle.getSuccess());
		} catch (Exception ex) {
			logger.error("error occurred while unblocking user with resource_id={}", blockedUserId, ex);
			messageHelper.addError(bundle, "unblockresource.error");
		}

		// Need to put JSON response in a text area for file uploads through JQuery AjaxForm plugin.
		return response.setMessages(bundle.getErrors());
	}

	@RequestMapping(
		value = "/unblockvendor",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder unblockVendorPost(
		@RequestParam("resource_id") Long blockedCompanyId) {

		MessageBundle bundle = MessageBundle.newInstance();
		Company blockedCompany = companyService.findCompanyById(blockedCompanyId);
		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setSuccessful(false)
			.setRedirect("/profile/company/" + blockedCompany.getCompanyNumber());

		try {
			vendorService.unblockVendor(getCurrentUser().getId(), blockedCompanyId);
			messageHelper.addSuccess(bundle, "unblockvendor.success");

			return response
				.setSuccessful(true)
				.setMessages(bundle.getSuccess());
		} catch (Exception ex) {
			logger.error("error occurred while unblocking user with resource_id={}", blockedCompanyId, ex);
			messageHelper.addError(bundle, "unblockvendor.error");
		}

		return response.setMessages(bundle.getErrors());
	}

	@RequestMapping(
		value = "/lanes",
		method = POST)
	public String updateLanes(
		@RequestParam(value = "shared_worker_role") Long sharedWorkerRole,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		ExtendedUserDetails user = getCurrentUser();
		if ((sharedWorkerRole == 1L)) {
			try {
				authenticationService.assignAclRolesToUser(user.getId(), new Long[]{AclRole.ACL_SHARED_WORKER});
				messageHelper.addSuccess(bundle, "profile.edit.success_optin");
			} catch (InvalidAclRoleException ex) {
				logger.error("error occurred while assigning acl role: {} to user id: {}", new Object[]{AclRole.ACL_SHARED_WORKER, user.getId()}, ex);
			}
		} else if ((sharedWorkerRole == 0L)) {
			authenticationService.removeAclRoleFromUser(user.getId(), AclRole.ACL_SHARED_WORKER);
			messageHelper.addSuccess(bundle, "profile.edit.success_optout");
		}

		return "redirect:/profile";
	}

	@RequestMapping(
		value = "/lanes",
		method = GET)
	public String lanes(Model model) throws Exception {
		ExtendedUserDetails userDetails = getCurrentUser();
		User user = userService.findUserById(userDetails.getId());

		model.addAttribute("has_shared_worker_role", userDetails.hasAllRoles("ACL_SHARED_WORKER"));
		model.addAttribute("completeness", profileService.getUserProfileCompleteness(userDetails.getId()));
		model.addAttribute("is_lane3_pending", user.isLane3Pending());
		model.addAttribute("is_lane3_approved", authenticationService.isLane3Active(user));
		return "web/pages/profileedit/lanes";
	}

	@RequestMapping(
		value = "/save_industries",
		method = POST)
	public String industry(
		@RequestParam(value = "industry[]", required = false) Long[] industryIds,
		@RequestParam(value = "group", required = false) Long groupId,
		RedirectAttributes flash) throws Exception {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (ArrayUtils.isNotEmpty(industryIds)) {
			Long userId = getCurrentUser().getId();
			Profile profile = profileService.findProfile(userId);
			profileService.updateProfileIndustries(profile.getId(), industryIds);
		} else {
			messageHelper.addError(bundle, "profile.edit.industries.error.no_industries");
			return "redirect:/profile-edit/industries";
		}

		messageHelper.addSuccess(bundle, "profile.edit.industries.success");

		/**
		 *  If the request had a group url parameter
		 *  (i.e. if the user visited their industry edit page from a group detail page)
		 */
		if (groupId != null) {
			return "redirect:/groups/" + groupId;
		}

		return "redirect:/profile-edit/industries";
	}

	@RequestMapping(
		value = "/industries",
		method = GET)
	public String industry(Model model) throws Exception {
		User user = userService.findUserById(getCurrentUser().getId());
		Profile profile = user.getProfile();

		List<IndustryDTO> availableIndustries = industryService.getAllIndustryDTOs();
		Map<IndustryDTO, Boolean> industries = Maps.newLinkedHashMap();
		Long profileId = profile.getId();

		for (IndustryDTO industry : availableIndustries) {
			industries.put(industry, industryService.doesProfileHaveIndustry(profileId, industry.getId()));
		}

		model.addAttribute("industries", industries);

		return "web/pages/profileedit/industries";
	}

	@RequestMapping(
		value = "/insureon",
		method = GET)
	public String insureon(Model model) throws Exception {
		model.addAttribute("companyID", getCurrentUser().getCompanyId());
		model.addAttribute("fluid", "1");

		return "web/pages/profileedit/insureon";
	}

	private MapBindingResult getFiletypeErrors(String filetype, String page) {
		MapBindingResult bind = new MapBindingResult(Maps.newHashMap(), "fileType");
		Set<String> validFileTypesForPage = MimeTypeUtilities.getMimeTypesForPage(page);
		filetypeValidator.validate(
				CollectionUtilities.newObjectMap("filetype", filetype, "pageSet", validFileTypesForPage), bind
		);
		return bind;
	}

	private List<com.workmarket.search.qualification.Qualification> addQualifications(final List<Map<String, String>> qualifications, final QualificationType qualificationType) {
		return Lists.transform(qualifications, new Function<Map<String, String>, com.workmarket.search.qualification
				.Qualification>() {
			@Override
			public com.workmarket.search.qualification.Qualification apply(Map<String, String> s) {
				final String qualificationName = s.get("name");
				final RequestContext context = webRequestContextProvider.getRequestContext();
				final QualificationBuilder qualificationBuilder = new QualificationBuilder()
					.setQualificationType(qualificationType)
					.setIsApproved(Boolean.FALSE)
					.setName(qualificationName);
				qualificationClient.createQualification(qualificationBuilder.build(), context)
					.subscribe(
						new Action1<MutateResponse>() {
							@Override
							public void call(MutateResponse mutateResponse) {
								if (mutateResponse.isSuccess()) {
									qualificationBuilder.setUuid(mutateResponse.getUuid());
								} else {
									logger.warn("failed to create job title: " + mutateResponse.getMessage());
								}
							}
						},
						new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								logger.error("Failed to create job title at qualification service: " + throwable);
							}
						});
				return qualificationBuilder.build();
			}
		});
	}

	@RequestMapping(
			value = "/qualifications",
			method = GET)
	public String qualifications(Model model) throws Exception {
		Long userId = getCurrentUser().getId();
		Profile profile = profileService.findProfile(userId);
		model.addAttribute("industry", industryService.getDefaultIndustryForProfile(profile.getId()).getId());
		String j = StringUtilities.defaultString(profile.getJobTitle(), "");
		model.addAttribute("jobTitle", StringUtilities.defaultString(profile.getJobTitle(), ""));

		UserSkillAssociationPagination pagination = new UserSkillAssociationPagination(true);
		pagination = skillService.findAllAssociationsByUser(userId, pagination);

		List<Qualification> skillsAndSpecialties = new ArrayList<>();

		JSONArray qualificationsJSON = new JSONArray();

		for (UserSkillAssociation s : pagination.getResults()) {
			if (s.getDeleted()) {
				continue;
			}

			qualificationsJSON.put(CollectionUtilities.newStringMap(
					"id", s.getSkill().getId().toString(),
					"name", s.getSkill().getName(),
					"recommended", "false",
					"type", Qualification.Type.SKILL.toString()));
		}

		SpecialtyPagination paginationSpecialty = new SpecialtyPagination(true);
		paginationSpecialty = specialtyService.findAllActiveSpecialtiesByUser(userId, paginationSpecialty);

		for (Specialty s : paginationSpecialty.getResults()) {
			qualificationsJSON.put(CollectionUtilities.newStringMap(
					"id", s.getId().toString(),
					"name", s.getName(),
					"recommended", "false",
					"type", Qualification.Type.SPECIALTY.toString()));
		}

		model.addAttribute("skills", qualificationsJSON);

		return "web/pages/profileedit/qualifications";
	}

	@RequestMapping(
			value = "/qualifications", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Boolean qualifications(@RequestBody QualificationsForm qualificationsForm) throws Exception {
		Long userId = getCurrentUser().getId();
		if (StringUtils.isNotBlank(qualificationsForm.getJobTitle())) {
			Profile profile = profileService.findProfile(userId);
			profile.setJobTitle(qualificationsForm.getJobTitle());
			profileService.saveOrUpdateProfile(profile);

			final com.workmarket.search.qualification.Qualification qualification =
					addQualification(qualificationsForm.getJobTitle(), QualificationType.job_title);
			addUserQualificationAssociation(userId, qualification);
		}

		if (CollectionUtils.isNotEmpty(qualificationsForm.getQualifications())) {
			List<Integer> skillIds = new ArrayList<>();
			List<Integer> specialtyIds = new ArrayList<>();

			for (Qualification qualification : qualificationsForm.getQualifications()) {
				switch (qualification.getType()) {
					case SKILL: {
						if (qualification.getId() == null) {
							SkillDTO skillDTO = new SkillDTO(qualification.getName());
							skillDTO.setDescription(qualification.getName());
							Skill newSkill = skillService.saveOrUpdateSkill(skillDTO);
							skillIds.add(newSkill.getId().intValue());
						} else {
							skillIds.add(qualification.getId().intValue());
						}

						if (featureEvaluator.hasGlobalFeature(ADD_QUALIFICATION_FEATURE)
								&& StringUtils.isNotBlank(qualification.getName())) {
							final com.workmarket.search.qualification.Qualification skillQualificaiton =
									addQualification(qualification.getName(), QualificationType.skill);
							addUserQualificationAssociation(userId, skillQualificaiton);
						}

						break;
					}
					case SPECIALTY: {
						if (qualification.getId() == null) {
							SpecialtyDTO specialtyDTO = new SpecialtyDTO();
							specialtyDTO.setName(qualification.getName());
							specialtyDTO.setDescription(qualification.getName());

							Specialty newSpecialty = specialtyService.saveOrUpdateSpecialty(specialtyDTO);
							specialtyIds.add(newSpecialty.getId().intValue());
						} else {
							specialtyIds.add(qualification.getId().intValue());
						}

						if (featureEvaluator.hasGlobalFeature(ADD_QUALIFICATION_FEATURE)
								&& StringUtils.isNotBlank(qualification.getName())) {
							final com.workmarket.search.qualification.Qualification specialtyQualification =
									addQualification(qualification.getName(), QualificationType.specialty);
							addUserQualificationAssociation(userId, specialtyQualification);
						}

						break;
					}

					default:
						break;
				}
			}

			skillService.setSkillsOfUser(skillIds, userId);
			specialtyService.setSpecialtiesOfUser(specialtyIds, userId);
		}

		userIndexer.reindexById(userId);
		return true;
	}

	private com.workmarket.search.qualification.Qualification addQualification(final String qualificationName, final QualificationType qualificationType) {
		final RequestContext context = webRequestContextProvider.getRequestContext();
		final QualificationBuilder qualificationBuilder = new QualificationBuilder()
				.setQualificationType(qualificationType)
				.setIsApproved(Boolean.FALSE)
				.setName(qualificationName);
		qualificationClient.createQualification(qualificationBuilder.build(), context)
				.subscribe(
						new Action1<MutateResponse>() {
							@Override
							public void call(MutateResponse mutateResponse) {
								if (mutateResponse.isSuccess()) {
									qualificationBuilder.setUuid(mutateResponse.getUuid());
								} else {
									logger.warn("failed to create job title: " + mutateResponse.getMessage());
								}
							}
						},
						new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								logger.error("Failed to create job title at qualification service: " + throwable);
							}
						});
		return qualificationBuilder.build();
	}

	private UserToQualification addUserQualificationAssociation(final Long userId, final com.workmarket.search
			.qualification.Qualification qualification) {
		final UserToQualification userToQualification =
				new UserToQualification(userId, qualification.getUuid(), qualification.getQualificationType());
		if (qualification.getUuid() != null) {
			qualificationAssociationService.saveOrUpdate(userToQualification);
		}
		return userToQualification;
	}
}
