package com.workmarket.web.controllers.admin.manage;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileModificationPagination;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.RoleType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserProfileModification;
import com.workmarket.domains.model.UserProfileModificationStatus;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.domains.model.changelog.user.UserChangeLogPagination;
import com.workmarket.domains.model.comment.Comment;
import com.workmarket.domains.model.comment.CommentPagination;
import com.workmarket.domains.model.comment.UserComment;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.rating.RatingPagination;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.UserSkillAssociation;
import com.workmarket.domains.model.skill.UserSkillAssociationPagination;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.CertificationService;
import com.workmarket.service.business.CommentService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.LicenseService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.UserChangeLogService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserCommentDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.admin.profiles.EditNameForm;
import com.workmarket.web.forms.profile.AddCommentForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.CompanyValidator;
import com.workmarket.web.validators.UserCommentValidator;
import com.workmarket.web.validators.UserValidator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/admin/manage/profiles")
public class AdminProfilesController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(AdminProfilesController.class);

	@Autowired private SuggestionService suggestionService;
	@Autowired private ProfileService profileService;
	@Autowired private UserService userService;
	@Autowired private CompanyService companyService;
	@Autowired private WorkService workService;
	@Autowired private SkillService skillService;
	@Autowired private RatingService ratingService;
	@Autowired private ScreeningService screeningService;
	@Autowired private CertificationService certificationService;
	@Autowired private LicenseService licenseService;
	@Autowired private UserChangeLogService userChangeLogService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private RegistrationService registrationService;
	@Autowired private CommentService commentService;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private AddressService addressService;
	@Autowired private IndustryService industryService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private UserValidator userValidator;
	@Autowired private CompanyValidator companyValidator;
	@Autowired private UserCommentValidator userCommentValidator;
	@Autowired private UserRoleService userRoleService;// appears unused

	private static Map<Integer, String> updateQueueSortMap = ImmutableMap.of(
		0, ProfileModificationPagination.SORTS.MODIFIED_DATE.toString(),
		1, ProfileModificationPagination.SORTS.NAME.toString(),
		2, ProfileModificationPagination.SORTS.COMPANY_NAME.toString()
	);


	/**
	 * This method displays the user's profile.
	 *
	 * @param userNumber  is user number
	 * @param httpRequest is HttpServletRequest
	 * @param model       is model
	 * @throws InstantiationException if error occurs
	 * @throws IllegalAccessException if error occurs
	 */
	@RequestMapping(
		value = "/index/{id}.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void indexList(
		@PathVariable("id") String userNumber,
		HttpServletRequest httpRequest,
		Model model) throws InstantiationException,
		IllegalAccessException {

		if (StringUtils.isBlank(userNumber)) return;

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		Long userId = userService.findUserId(userNumber);

		UserCertificationAssociationPagination pagination = request.newPagination(UserCertificationAssociationPagination.class);
		pagination.setStartRow(MoreObjects.firstNonNull(request.getStart(), 0));
		pagination.setResultsLimit(MoreObjects.firstNonNull(request.getLimit(), 10));

		// Figure out what type of certification list we need.
		String type = httpRequest.getParameter("type");

		// Validate the type.
		if ("verified".equals(type)) {
			pagination = certificationService.findAllVerifiedCertificationsByUserId(userId, pagination);

			DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

			for (UserCertificationAssociation item : pagination.getResults()) {
				Certification cert = item.getCertification();

				List<String> data = Lists.newArrayList(
					cert.getCertificationVendor().getName(), // provider
					cert.getName(),
					cert.getIdHash()                         // number
				);

				Map<String, Object> meta = ImmutableMap.<String, Object>of(
					"id", cert.getId()
				);

				response.addRow(data, meta);
			}

			model.addAttribute("response", response);
		}
	}


	@RequestMapping(
		value = "/index/{id}",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String index(
		@PathVariable("id") String userNumber,
		Model model) {

		User user = userService.findUserByUserNumber(userNumber);

		if (user == null) {
			throw new HttpException404()
				.setMessageKey("admin.manage.profiles.invalid.userid")
				.setRedirectUri("redirect:/admin/manage/profiles/queue");
		}

		Long userId = user.getId();

		model.addAttribute("user", user);
		model.addAttribute("id", userId);
		model.addAttribute("user_id", userNumber);
		model.addAttribute("first_name", user.getFirstName());
		model.addAttribute("last_name", user.getLastName());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("suspended", authenticationService.isSuspended(user));
		model.addAttribute("user_status_type", authenticationService.getUserStatus(user));

		model.addAttribute("first_name_old", user.getFirstNameOldValue());
		model.addAttribute("last_name_old", user.getLastNameOldValue());

		UserAssetAssociation userAvatars = userService.findUserAvatars(userId);
		if (userAvatars != null) {
			model.addAttribute("avatar_small", (userAvatars.getTransformedSmallAsset() != null) ? userAvatars.getTransformedSmallAsset().getUri() : null);
			model.addAttribute("avatar_large", (userAvatars.getTransformedLargeAsset() != null) ? userAvatars.getTransformedLargeAsset().getUri() : null);
		}

		UserAssetAssociation previousAvatars = userService.findPreviousUserAvatars(userId);
		if (previousAvatars != null) {
			model.addAttribute("avatar_small_old", (previousAvatars.getTransformedSmallAsset() != null) ? previousAvatars.getTransformedSmallAsset().getUri() : null);
			model.addAttribute("avatar_large_old", (previousAvatars.getTransformedLargeAsset() != null) ? previousAvatars.getTransformedLargeAsset().getUri() : null);
		}

		// Send the profile data to the view.
		Profile profile = profileService.findProfile(userId);
		model.addAttribute("profile", profile);

		if (profile != null) {
			model.addAttribute("work_phone", profile.getWorkPhone());
			model.addAttribute("old_work_phone", profile.getWorkPhoneOldValue());
			model.addAttribute("mobile_phone", profile.getMobilePhone());
			model.addAttribute("old_mobile_phone", profile.getMobilePhoneOldValue());
			model.addAttribute("sms_phone", profile.getSmsPhone());
			model.addAttribute("old_sms_phone", profile.getSmsPhoneOldValue());
			model.addAttribute("user_overview", profile.getOverview());
			model.addAttribute("user_overview_old", profile.getOverviewOldValue());
			model.addAttribute("job_title", profile.getJobTitle());

			Long addressId = profile.getAddressId();
			if (addressId != null) {
				Address address = addressService.findById(addressId);
				model.addAttribute("full_address", address.getFullAddress());
				model.addAttribute("address1", address.getAddress1());
				model.addAttribute("address2", address.getAddress2());
				model.addAttribute("city", address.getCity());
				model.addAttribute("state", address.getState().getShortName());
				model.addAttribute("postal_code", address.getPostalCode());
				model.addAttribute("country", address.getCountry());
				model.addAttribute("use_company_address", address.getAddressType().getCode().equals(AddressType.COMPANY));
			}

			// Get resumes.
			List<Asset> resumes = profileService.findAllUserResumes(userId);
			model.addAttribute("resumes", resumes);

			model.addAttribute("industries", industryService.getIndustryDTOsForProfile(profile.getId()));
		}

		// Send the company address data to the view.
		Address companyAddress = profileService.findCompanyAddress(userId);
		if ((companyAddress != null) && (companyAddress.getAddress1() != null)) {
			Map<String, Object> company_address = CollectionUtilities.newObjectMap(
				"address1", companyAddress.getAddress1(),
				"address2", companyAddress.getAddress2(),
				"city", companyAddress.getCity(),
				"state", companyAddress.getState().getShortName(),
				"postal_code", companyAddress.getPostalCode(),
				"country", companyAddress.getCountry().getId()
			);
			try {
				model.addAttribute("company_address", jsonSerializationService.toJson(company_address));
				model.addAttribute("has_company_address", Boolean.TRUE);
			} catch (Exception e){
				model.addAttribute("has_company_address", Boolean.FALSE);
			}
		} else {
			model.addAttribute("has_company_address", Boolean.FALSE);
		}

		// Get company
		Company company = profileService.findCompany(userId);
		model.addAttribute("company", company);

		if (company != null) {
			model.addAttribute("company_id", company.getId());
			model.addAttribute("company_number", company.getCompanyNumber());
			model.addAttribute("company_name", company.getName());
			model.addAttribute("company_name_old", company.getNameOldValue());
			model.addAttribute("company_overview", company.getOverview());
			model.addAttribute("company_overview_old", company.getOverviewOldValue());
			model.addAttribute("company_website", company.getWebsite());
			model.addAttribute("company_website_old", company.getWebsiteOldValue());

			CompanyAssetAssociation companyAvatars = companyService.findCompanyAvatars(company.getId());
			if (companyAvatars != null) {
				model.addAttribute("company_avatar_small", (companyAvatars.getSmall() != null) ? companyAvatars.getSmall().getUri() : null);
			}

			CompanyAssetAssociation previousCompanyAvatars = companyService.findPreviousCompanyAvatars(company.getId());
			if (previousCompanyAvatars != null) {
				model.addAttribute("company_avatar_small_old", (previousCompanyAvatars.getSmall() != null) ? previousCompanyAvatars.getSmall().getUri() : null);
			}
		}

		// Get the user roles to check for superuser (owner)
		model.addAttribute("is_superuser", userRoleService.hasRole(user, RoleType.SUPERUSER));

		// Get all pending profile modifications
		Map<String, Map<String, Object>> profileModifications = getListOfPendingProfileModifications(userId, profile, company);
		model.addAttribute("profile_modifications", profileModifications);

		model.addAttribute("lane_association", Boolean.FALSE);

		// Get created on date.
		model.addAttribute("created_on", user.getCreatedOn().getTime());

		// Send the company address data to the view.
		Address address = profileService.findCompanyAddress(userId);
		model.addAttribute("address", address);

		// Get average ratings.
		Double rating = ratingService.findSatisfactionRateForUser(userId);
		model.addAttribute("rating", rating);

		// Get number of ratings.
		Integer numberRatings = ratingService.countAllUserRatings(userId);
		model.addAttribute("number_ratings", numberRatings);

		model.addAttribute("is_owner", Boolean.FALSE);
		model.addAttribute("is_wm_admin", Boolean.TRUE);

		// Get Skills.
		UserSkillAssociationPagination skillPagination = new UserSkillAssociationPagination();
		skillPagination.setStartRow(0);
		skillPagination.setResultsLimit(50);
		skillPagination = skillService.findAllAssociationsByUser(userId, skillPagination);
		List<UserSkillAssociation> skills = skillPagination.getResults();

		// Build out skill data structure for JSON
		// Filter out deleted skill associations
		List<Skill> skillsList = new LinkedList<Skill>();
		for (UserSkillAssociation skill : skills) {
			if (skill.getDeleted()) {
				continue;
			}

			skillsList.add(skill.getSkill());
		}

		model.addAttribute("skills", skillsList);

		// Get certifications.
		UserCertificationAssociationPagination certPagination = new UserCertificationAssociationPagination();
		certPagination.setReturnAllRows();
		certPagination = certificationService.findAllAssociationsByUserId(userId, certPagination);
		List<UserCertificationAssociation> certResults = certPagination.getResults();
		model.addAttribute("certifications", certResults);

		UserLicenseAssociationPagination licensePagination = new UserLicenseAssociationPagination();
		licensePagination.setReturnAllRows();
		licensePagination = licenseService.findAllAssociationsByUserId(userId, licensePagination);
		List<UserLicenseAssociation> licenseResults = licensePagination.getResults();
		model.addAttribute("licenses", licenseResults);

		// Set background checked status.
		model.addAttribute("background_check", screeningService.findMostRecentBackgroundCheck(userId));

		// Get the company's ACL Roles
		Map<Long, String> companyRoles = Maps.newHashMap();
		if (company != null) {
			companyRoles = listCompanyAclRoles(company.getId());
		}
		model.addAttribute("company_roles", companyRoles);

		// Get the user's current roles
		model.addAttribute("user_roles", listUserAclRoles(userId));

		UserChangeLogPagination changeLogPagination = new UserChangeLogPagination();
		changeLogPagination.setSortColumn(UserChangeLogPagination.SORTS.CREATED_ON);
		changeLogPagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		changeLogPagination.setReturnAllRows();

		try {
			changeLogPagination = userChangeLogService.findAllUserChangeLogsByUserId(userId, changeLogPagination);
		} catch (Exception e) {
			throw new RuntimeException("Unknown exception while executing findAllUserChangeLogsByUserId for userId=" + userId, e);
		}

		model.addAttribute("changelog", changeLogPagination.getResults());

		// If there are no profile modifications pending and user status type is "pending", then
		// still display the approve profile button.

		model.addAttribute("timeZoneId", getCurrentUser().getTimeZoneId());

		model.addAttribute("form_edit_name", new EditNameForm());

		return "web/pages/admin/manage/profiles/index";
	}


	protected Map<Long, String> listUserAclRoles(Long userId) {
		List<AclRole> roles = authenticationService.findAllAssignedAclRolesByUser(userId);
		Map<Long, String> aclRoleMap = Maps.newHashMap();

		if (roles != null) {
			for (AclRole row : roles) {
				aclRoleMap.put(row.getId(), row.getDescription());
			}
		}

		return aclRoleMap;
	}


	protected Map<Long, String> listCompanyAclRoles(Long companyId) {
		List<AclRole> roles = authenticationService.findAllAvailableAclRolesByCompany(companyId);
		Map<Long, String> aclRoleMap = Maps.newHashMap();

		if (roles != null) {
			for (AclRole item : roles) {
				if (!item.getId().equals(AclRole.ACL_EMPLOYEE_WORKER)) {
					aclRoleMap.put(item.getId(), item.getDescription());
				}
			}
		}

		return aclRoleMap;
	}

	/**
	 * @param userId  is user ID
	 * @param profile is user profile
	 * @param company if null, will default use User
	 * @return a Map of Maps, eg: Map&lt;"userName", Map&lt;"first_name", "Peter"&gt;&gt;
	 */
	protected Map<String, Map<String, Object>> getListOfPendingProfileModifications(Long userId, Profile profile, Company company) {
		Map<String, Map<String, Object>> toApprove = new HashMap<String, Map<String, Object>>();

		// Make sure we have a profile object.
		if (profile == null) {
			profile = profileService.findProfile(userId);
		}

		User user = userService.findUserById(userId);

		List<UserProfileModification> profileModifications = profileService.findAllPendingModificationsByUserId(userId);

		if (profileModifications == null) {
			return toApprove;
		}

		// to_approve = array(); // define var
		for (UserProfileModification row : profileModifications) {
			if (UserProfileModificationStatus.PENDING_APPROVAL.equals(row.getUserProfileModificationStatus())) {
				String modificationType = row.getProfileModificationType().getCode();

				if (ProfileModificationType.CERTIFICATION.equals(modificationType)) {
					// Not implemented yet.
				} else if (ProfileModificationType.WORK_PHONE_NUMBER.equals(modificationType)) {
					Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap(
						"value", profile.getWorkPhone(),
						"value_old", null,                            // TODO: resolve "value_old"
						"id", row.getId()
					);

					toApprove.put(modificationType, tmp);
				} else if (ProfileModificationType.MOBILE_PHONE_NUMBER.equals(modificationType)) {
					Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap(
						"value", profile.getMobilePhone(),
						"value_old", null,                            // TODO: resolve "value_old"
						"id", row.getId()
					);

					toApprove.put(modificationType, tmp);
				} else if (ProfileModificationType.USER_NAME.equals(modificationType)) {
					Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap(
						"first_name", user.getFirstName(),
						"last_name", user.getLastName(),
						"first_name_old", null,                       // TODO: resolve "first_name_old"
						"last_name_old", null,                        // TODO: resolve "last_name_old"
						"value_old", null,                            // TODO: resolve "value_old"
						"id", row.getId()
					);

					toApprove.put(modificationType, tmp);
				} else if (ProfileModificationType.USER_AVATAR.equals(modificationType)) {
					String avatarUri = "";
					if (company != null && company.getAvatarSmall() != null) {
						avatarUri = company.getAvatarSmall().getUri();
					}

					Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap(
						"value", avatarUri,
						"value_old", null,                            // TODO: resolve "value_old"
						"id", row.getId()
					);

					toApprove.put(modificationType, tmp);
				} else if (ProfileModificationType.USER_OVERVIEW.equals(modificationType)) {
					Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap(
						"value", profile.getOverview(),
						"value_old", null,                            // TODO: resolve "value_old"
						"id", row.getId()
					);

					toApprove.put(modificationType, tmp);
				} else if (ProfileModificationType.SMS_PHONE_NUMBER.equals(modificationType)) {
					Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap(
						"value", profile.getSmsPhone(),
						"value_old", profile.getSmsPhone(),
						"id", row.getId()
					);

					toApprove.put(modificationType, tmp);
				} else if (ProfileModificationType.SKILL_ADDED.equals(modificationType)) {
					// Not implemented here yet.
				} else if (ProfileModificationType.LICENSE.equals(modificationType)) {
					// Not implemented here yet.
				} else if (ProfileModificationType.INSURANCE_ADDED.equals(modificationType)) {
					// Not implemented here yet.
				} else if (ProfileModificationType.COMPANY_OVERVIEW.equals(modificationType)) {
					if (company != null) {
						Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap(
							"value", company.getOverview(),
							"value_old",                            // TODO: resolve "value_old"
							"id", row.getId()
						);

						toApprove.put(modificationType, tmp);
					}
				} else if (ProfileModificationType.COMPANY_NAME.equals(modificationType)) {
					if (company != null) {
						Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap(
							"value", company.getName(),
							"value_old",                            // TODO: resolve "value_old"
							"id", row.getId()
						);

						toApprove.put(modificationType, tmp);
					}
				} else if (ProfileModificationType.COMPANY_WEBSITE.equals(modificationType)) {
					if (company != null) {
						Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap(
							"value", company.getWebsite(),
							"value_old",                            // TODO: resolve "value_old"
							"id", row.getId()
						);

						toApprove.put(modificationType, tmp);
					}
				} else if (ProfileModificationType.COMPANY_AVATAR.equals(modificationType)) {
					Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap("id", row.getId());
					toApprove.put(modificationType, tmp);
				} else if (ProfileModificationType.RESUME.equals(modificationType)) {
					Map<String, Object> tmp = CollectionUtilities.newTypedObjectMap("id", row.getId());
					toApprove.put(modificationType, tmp);
				}
			}
		}

		return toApprove;
	}


	/**
	 * This method displays the service profile approval queue.
	 *
	 * @return ModelAndView
	 */
	@RequestMapping(
		value = "/queue",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String queue() {
		return "web/pages/admin/manage/profiles/queue";
	}


	/**
	 * This method displays the service profile approval queue.
	 *
	 * @param httpRequest HTTP Servlet Request
	 * @param model       Model
	 * @throws InstantiationException if error occurs
	 * @throws IllegalAccessException if error occurs
	 */
	@RequestMapping(
		value = "/queue.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void queueList(
		HttpServletRequest httpRequest,
		Model model) throws InstantiationException,
		IllegalAccessException {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(updateQueueSortMap);
		request.setFilterMapping(ImmutableMap.<String, Enum<?>>of(
			"sSearch", ProfileModificationPagination.FILTER_KEYS.NAME
		));

		ProfileModificationPagination pagination = request.newPagination(ProfileModificationPagination.class);
		pagination = profileService.findAllPendingProfileModifications(pagination);

		DataTablesResponse<List<String>, Map> response = DataTablesResponse.newInstance(request, pagination);

		for (UserProfileModification item : pagination.getResults()) {
			List<String> data = ImmutableList.of(
				DateUtilities.format("E MMM-dd-yyyy hh:mm", item.getModifiedOn()),
				item.getUser().getFullName(),
				item.getUser().getEmail(),
				item.getUser().getCompany().getName()
			);

			Map meta = ImmutableMap.of(
				"id", item.getUser().getUserNumber(),
				"suspended", authenticationService.isSuspended(item.getUser())
			);

			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}


	/**
	 * Get a list of ratings for user.
	 *
	 * @param id          is userId
	 * @param httpRequest is HTTP Servlet Request
	 * @return map
	 * @throws Exception id error occurs
	 */
	@RequestMapping(
		value = "/getflaggedratings",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getRatings(
		@RequestParam("id") Long id,
		HttpServletRequest httpRequest,
		Model model) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.<Integer, String>builder()
			.put(0, RatingPagination.SORTS.CREATED_ON.toString())
			.put(1, RatingPagination.SORTS.VALUE.toString()).build());


		RatingPagination pagination = new RatingPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		pagination = ratingService.findRatingsFlaggedForUser(id, pagination);

		DataTablesResponse<List<String>, Object> response = DataTablesResponse.newInstance(request, pagination);

		for (Rating r : pagination.getResults()) {
			response.addRow(Lists.newArrayList(
				r.getId().toString(),
				r.getScaledValue().toString(),
				DateUtilities.format("MMM d, yyyy", r.getCreatedOn()),
				r.getReview(),
				(r.getWork() != null) ? r.getWork().getWorkNumber() : null
			));
		}

		model.addAttribute("response", response);
	}


	/**
	 * Approve a certification.
	 *
	 * @param certificationId is certification id
	 * @param userId          is user id
	 * @return ModelAndView
	 */
	@RequestMapping(
		value = "/approvecertification",
		method = GET)
	public ModelAndView approveCertification(
		@RequestParam("id") Long certificationId,
		@RequestParam("user_id") Long userId) {

		ModelAndView mav = new ModelAndView();
		MessageBundle bundle = messageHelper.newBundle();
		mav.addObject("bundle", bundle);

		try {
			certificationService.updateCertificationStatus(certificationId, VerificationStatus.VERIFIED);
			messageHelper.addSuccess(bundle, "admin.manage.profiles.approvecertification.success");
		} catch (IllegalArgumentException ex) {
			logger.warn("Error approving certification with id={} and userId={}", new Object[]{certificationId, userId}, ex);
			messageHelper.addError(bundle, "admin.manage.profiles.approvecertification.error");
		}

		String userNumber = userService.findUserNumber(userId);
		mav.setViewName("redirect:/admin/manage/profiles/index/" + userNumber);
		return mav;
	}


	/**
	 * Reject a certification.
	 *
	 * @param certificationId is certification id
	 * @param userId          is user id
	 * @return ModelAndView
	 */
	@RequestMapping(
		value = "/rejectcertification",
		method = GET)
	public ModelAndView rejectCertification(
		@RequestParam("id") Long certificationId,
		@RequestParam("user_id") Long userId) {

		ModelAndView mav = new ModelAndView();
		MessageBundle bundle = messageHelper.newBundle();
		mav.addObject("bundle", bundle);

		try {
			certificationService.rejectCertification(certificationId);
			messageHelper.addSuccess(bundle, "admin.manage.profiles.rejectcertification.success");
		} catch (IllegalArgumentException ex) {
			logger.warn("Error declining the certification with id={} and userId={}", new Object[]{certificationId, userId}, ex);
			messageHelper.addError(bundle, "admin.manage.profiles.rejectcertification.error");
		}

		String userNumber = userService.findUserNumber(userId);
		mav.setViewName("/admin/manage/profiles/index/" + userNumber);
		return mav;
	}


	/**
	 * This method deletes the specified rating.
	 *
	 * @param ratingId is rating id
	 * @param userId   is user id
	 * @return ModelAndView
	 */
	@RequestMapping(
		value = "/deleterating",
		method = GET)
	public String deleteRating(
		RedirectAttributes redirectAttributes,
		@RequestParam(value = "id", required = false) Long ratingId,
		@RequestParam(value = "user_id", required = false) Long userId) {

		String userNumber = (userId == null) ? null : userService.findUserNumber(userId);
		MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);

		if ((ratingId == null) || (userId == null)) {
			messageHelper.addError(bundle, "admin.manage.profiles.deleterating.error");

			if (userId == null) {
				return "redirect:/admin";
			} else {
				return "redirect:/admin/manage/profiles/index/" + userNumber;
			}
		}

		try {
			ratingService.deleteRating(ratingId);
			messageHelper.addSuccess(bundle, "admin.manage.profiles.deleterating.success");
		} catch (Exception ex) {
			logger.error("Error deleting rating for ratingId={} and userId={}", new Object[]{ratingId, userId}, ex);
			messageHelper.addError(bundle, "admin.manage.profiles.deleterating.error");
		}

		return "redirect:/admin/manage/profiles/index/" + userNumber;
	}


	/**
	 * This method unflags the specified rating.
	 *
	 * @param ratingId is rating id
	 * @param userId   is user id
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/unflag", method = RequestMethod.GET)
	public String unFlag(RedirectAttributes redirectAttributes,
						 @RequestParam(value = "id", required = false) Long ratingId,
						 @RequestParam(value = "user_id", required = false) Long userId) {

		String userNumber = (userId == null) ? null : userService.findUserNumber(userId);
		MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);

		if ((ratingId == null) || (userId == null)) {
			messageHelper.addError(bundle, "admin.manage.profiles.unflag.error");

			if (userId == null) {
				return "redirect:/admin";
			} else {
				return "redirect:/admin/manage/profiles/index/" + userNumber;
			}
		}

		try {
			ratingService.flagRatingForReview(ratingId, false);
			messageHelper.addSuccess(bundle, "admin.manage.profiles.unflag.success");
		} catch (Exception ex) {
			logger.error("Error unflagging a review (rating) for ratingId={} and userId={}", new Object[]{ratingId, userId}, ex);
			messageHelper.addError(bundle, "admin.manage.profiles.unflag.error");
		}

		return "redirect:/admin/manage/profiles/index/" + userNumber;
	}


	/**
	 * Suspends a user.
	 *
	 * @param id is userId
	 * @return ModelAndView
	 */
	@RequestMapping(
		value = "/suspend/{userId}",
		method = POST)
	public ModelAndView suspend(@PathVariable("userId") Long id) {
		ModelAndView mav = new ModelAndView();
		MessageBundle bundle = messageHelper.newBundle();
		mav.addObject("bundle", bundle);

		if (id == null) {
			messageHelper.addError(bundle, "admin.manage.profiles.suspend.notfound");
			mav.setViewName("redirect:/admin/manage/profile/search");
			return mav;
		}

		try {
			userService.suspendUser(id, Boolean.TRUE);
			messageHelper.addSuccess(bundle, "admin.manage.profiles.suspend.success");
		} catch (IllegalStateException ex) {
			logger.warn("An error occurred. The user with id={} could not be suspended.", new Object[]{id}, ex);
			messageHelper.addError(bundle, "admin.manage.profiles.suspend.error");
		}

		String userNumber = userService.findUserNumber(id);
		mav.setViewName("redirect:/admin/manage/profiles/index/" + userNumber);

		return mav;
	}


	/**
	 * Unsuspends a user.
	 *
	 * @param id is userId
	 * @return ModelAndView
	 */
	@RequestMapping(
		value = "/unsuspend/{userId}",
		method = GET)
	public ModelAndView unsuspend(@PathVariable("userId") Long id) {
		ModelAndView mav = new ModelAndView();
		MessageBundle bundle = messageHelper.newBundle();
		mav.addObject("bundle", bundle);

		if (id == null) {
			messageHelper.addError(bundle, "admin.manage.profiles.unsuspend.notfound");
			mav.setViewName("redirect:/admin/manage/company/search");
			return mav;
		}

		try {
			userService.suspendUser(id, Boolean.FALSE);
			messageHelper.addSuccess(bundle, "admin.manage.profiles.unsuspend.success");
		} catch (IllegalArgumentException ex) {
			logger.warn("An error occurred. The user with id={} could not be unsuspended.", new Object[]{id}, ex);
			messageHelper.addError(bundle, "admin.manage.profiles.unsuspend.error");
		}

		String userNumber = userService.findUserNumber(id);
		mav.setViewName("redirect:/admin/manage/profiles/index/" + userNumber);

		return mav;
	}


	/**
	 * Generate password request URL for user
	 *
	 * @param id is userId
	 * @return ModelAndView
	 */
	@RequestMapping(
		value = "/reset_password/{id}",
		method = GET)
	public String resetPassword(
		@PathVariable("id") Long id,
		Model model) throws Exception {

		User user = userService.findUserById(id);

		if (!Constants.WM_COMPANY_ID.equals(getCurrentUser().getCompanyId())) {
			throw new HttpException401().setMessageKey("admin.manage.profiles.reset_password.notwm");
		}

		if (userRoleService.isInternalUser(user)) {
			throw new HttpException401().setMessageKey("admin.manage.profiles.reset_password.error");
		}

		model.addAttribute("url", registrationService.generateForgotPasswordURL(id));

		return "web/pages/admin/manage/profiles/reset_password";
	}


	/**
	 * Generate password request URL for user
	 *
	 * @param id is account id
	 * @return String
	 */
	@RequestMapping(
		value = "/confirm_account/{id}",
		method = GET)
	public String confirmAccount(
		@PathVariable("id") Long id,
		Model model) {

		MessageBundle bundle = messageHelper.newBundle(model);

		if (!Constants.WM_COMPANY_ID.equals(getCurrentUser().getCompanyId())) {
			throw new HttpException401().setMessageKey("admin.manage.profiles.reset_password.notwm");
		}

		if (id == null) {
			messageHelper.addError(bundle, "admin.manage.profiles.confirm_account.notfound");
		} else {
			try {
				String url = registrationService.generateConfirmationURL(id);
				User user = userService.findUserById(id);
				model.addAttribute("user", user);
				model.addAttribute("url", url);
			} catch (Exception ex) {
				throw new RuntimeException("Error occurred calling generateConfirmationURL for id=" + id, ex);
			}
		}

		return "web/partials/admin/manage/profiles/confirm_account";
	}


	/**
	 * Review the profile modifications pending approval queue.
	 *
	 * @param userId  is user id
	 * @param bind  is binding result
	 * @param request is HttpServletRequest
	 * @return ModelAndView
	 * @throws Exception if error occurs
	 */
	@RequestMapping(
		value = "/approve_modifications",
		method = POST)
	public String approveModifications(
		@ModelAttribute("id") Long userId,
		BindingResult bind,
		HttpServletRequest request,
		Model model) throws Exception {

		MessageBundle bundle = messageHelper.newBundle(model);

		// Check for userID.
		if (userId == null) {
			messageHelper.addError(bundle, "admin.manage.profiles.approve_modifications.notfound");
			return "redirect:/admin/manage/profiles/queue";
		}

		// Load up service and models.
		User user = userService.findUserById(userId);
		Profile profile = profileService.findProfile(userId);
		Company company = user.getCompany();
		Assert.notNull(user);
		Assert.notNull(profile);
		Assert.notNull(company);

		// Get all pending profile modifications
		Map<String, Map<String, Object>> profileModifications = getListOfPendingProfileModifications(userId, profile, null);

		List<Long> approveIds = new LinkedList<Long>();
		Map<String, String> userData = Maps.newHashMap();
		Map<String, String> profileData = Maps.newHashMap();
		boolean saveProfile = false;
		boolean saveCompany = false;
		boolean saveCompanyOverview = false;

		Pattern pattern = Pattern.compile("(approve|deny)\\[(.*)\\]");

		// Process the approved modifications.
		// TODO: Refactor. This is a bit sloppy. However, just not sure how to better handle this.
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
			String paramName = (String) e.nextElement();
			Matcher matcher = pattern.matcher(paramName);

			if (matcher.matches()) {
				String actionName = matcher.group(1);
				String modificationType = matcher.group(2);
				Assert.notNull(actionName);
				Assert.notNull(modificationType);

				if ("approve".equals(actionName)) {
					Map<String, Object> data = profileModifications.get(modificationType);
					if(data == null || StringUtils.isEmpty((String)data.get("value"))){
						logger.error("could not match user profile param " + modificationType);
						continue;
					}

					if (ProfileModificationType.USER_NAME.equals(modificationType)) {
						// Compare the to approve first name with what came from the form to see if the admin user changed it.
						if (!StringUtils.equals((String)data.get("first_name"), request.getParameter("first_name"))) {
							user.setFirstName(request.getParameter("first_name"));
							userData.put("first_name", user.getFirstName());
							saveProfile = true;
						}

						if (!StringUtils.equals((String)data.get("last_name"), request.getParameter("last_name"))) {
							user.setLastName(request.getParameter("last_name"));
							userData.put("last_name", user.getLastName());
							saveProfile = true;
						}

						approveIds.add((Long) data.get("id"));
					} else if (ProfileModificationType.USER_AVATAR.equals(modificationType)) {
						approveIds.add((Long) data.get("id"));
					} else if (ProfileModificationType.COMPANY_AVATAR.equals(modificationType)) {
						approveIds.add((Long) data.get("id"));
					} else if (ProfileModificationType.WORK_PHONE_NUMBER.equals(modificationType)) {
						if (!StringUtils.equals((String)data.get("value"), request.getParameter(modificationType))) {
							profile.setWorkPhone(request.getParameter(modificationType));
							profileData.put("workPhone", profile.getWorkPhone());
							saveProfile = true;
						}
						approveIds.add((Long) data.get("id"));
					} else if (ProfileModificationType.MOBILE_PHONE_NUMBER.equals(modificationType)) {
						if (!StringUtils.equals((String)data.get("value"), request.getParameter(modificationType))) {
							profile.setMobilePhone(request.getParameter(modificationType));
							profileData.put("mobilePhone", profile.getMobilePhone());
							saveProfile = true;
						}
						approveIds.add((Long) data.get("id"));
					} else if (ProfileModificationType.SMS_PHONE_NUMBER.equals(modificationType)) {
						if (!StringUtils.equals((String)data.get("value"), request.getParameter(modificationType))) {
							profile.setSmsPhone(request.getParameter(modificationType));
							profileData.put("smsPhone", profile.getSmsPhone());
							saveProfile = true;
						}
						approveIds.add((Long) data.get("id"));
					} else if (ProfileModificationType.USER_OVERVIEW.equals(modificationType)) {
						if (!StringUtils.equals((String)data.get("value"), request.getParameter(modificationType))) {
							profile.setOverview(request.getParameter(modificationType));
							saveProfile = true;
						}
						approveIds.add((Long) data.get("id"));
					} else if (ProfileModificationType.COMPANY_NAME.equals(modificationType)) {
						if (!StringUtils.equals((String)data.get("value"), request.getParameter(modificationType))) {
							company.setName(request.getParameter(modificationType));
							saveCompany = true;
						}
						approveIds.add((Long) data.get("id"));
					} else if (ProfileModificationType.COMPANY_OVERVIEW.equals(modificationType)) {
						if (!StringUtils.equals((String)data.get("value"), request.getParameter(modificationType))) {
							company.setOverview(request.getParameter(modificationType));
							saveCompany = true;
							// When in Rome.
							saveCompanyOverview = true;
						}
						approveIds.add((Long) data.get("id"));
					} else if (ProfileModificationType.COMPANY_WEBSITE.equals(modificationType)) {
						if (!StringUtils.equals((String)data.get("value"), request.getParameter(modificationType))) {
							company.setWebsite(request.getParameter(modificationType));
							saveCompany = true;
						}
						approveIds.add((Long) data.get("id"));
					} else if (ProfileModificationType.RESUME.equals(modificationType)) {
						approveIds.add((Long) data.get("id"));
					}
				} else if ("deny".equals(actionName)) {
					// See what profile modifications we need to deny.
					if (profileModifications.containsKey(modificationType)) {
						Map<String, Object> data = profileModifications.get(modificationType);

						// get the profilemodificationid
						Long id = (Long) data.get("id");
						profileService.declineProfileModification(id);
					}
				}
			}
		} // end "for" enumeration loop

		if (saveProfile) {
			userValidator.validate(user, bind);
		}

		if (saveCompany) {
			companyValidator.validate(company, bind);
		}

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
		} else {
			// Save Profile modifications, if needed.
			if (saveProfile) {
				userService.updateUserProperties(userId, userData);
				profileService.updateProfileProperties(userId, profileData);

				authenticationService.refreshSessionForUser(user.getId());
			}

			// Save company, if needed.
			if (saveCompany) {
				companyService.saveOrUpdateCompany(company);
				if (saveCompanyOverview) {
					companyService.setOverview(company.getId(), company.getOverview());
				}
				authenticationService.refreshSessionForCompany(company.getId());
			}

			// Approve those valid profiles.
			if (!approveIds.isEmpty()) {
				profileService.approveUserProfileModifications(userId, approveIds.toArray(new Long[approveIds.size()]));
			}
		}

		return "redirect:/admin/manage/profiles/queue";
	}


	/**
	 * Changes the company a user is related to.
	 *
	 * @param userId      is user id
	 * @param companyId   is company id
	 * @param aclRoleIds  acl roles
	 * @param result      binding result
	 * @param companyAttr company
	 * @return model and view
	 *
	 */
	@RequestMapping(
		value = "/change_company_relation",
		method = POST)
	public String changeCompanyRelation(
		@RequestParam(value = "user_id", required = false) Long userId,
		@RequestParam(value = "company_id", required = false) Long companyId,
		@RequestParam(value = "roles[]", required = false) Long[] aclRoleIds,
		@ModelAttribute("company") @Valid Company companyAttr,
		BindingResult result,
		RedirectAttributes redirectAttributes) {

		MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);

		// Load up the User.
		User user;
		if (userId == null || (user = userService.findUserById(userId)) == null) {
			messageHelper.addError(bundle, "admin.manage.profiles.change_company_relation.user.notfound");
			return "redirect:/admin";
		}

		// Load up the requested company
		if (companyId == null || profileService.findCompanyById(companyId) == null) {
			messageHelper.addError(bundle, "admin.manage.profiles.change_company_relation.company.notfound");
			return "redirect:/admin/manage/profiles/index/" + user.getUserNumber();
		}

		// Do the company relation change.
		try {
			registrationService.registerExistingUserForCompany(userId, companyId, aclRoleIds);
			messageHelper.addSuccess(bundle, "admin.manage.profiles.change_company_relation.company.success");
		} catch (Exception ex) {
			logger.error("Error registering existing user with id={} for company with id={}", new Object[]{userId, companyId}, ex);
			messageHelper.addError(bundle, "admin.manage.profiles.change_company_relation.company.failure");
		}

		return "redirect:/admin/manage/profiles/index/" + user.getUserNumber();
	}


	@RequestMapping(
		value = "/get_user_comments",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getUserComments(
		@RequestParam("id") String userNumber,
		HttpServletRequest httpRequest,
		Model model)
		throws IllegalAccessException,
		InstantiationException {

		User user = userService.findUserByUserNumber(userNumber);

		if (user == null) {
			return;
		}

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		CommentPagination pagination = new CommentPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(CommentPagination.SORTS.CREATED_ON);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);

		pagination = commentService.findAllActiveClientServiceUserComments(user.getId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		List<Comment> comments = pagination.getResults();
		Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(comments, "creatorId"),
			"firstName", "lastName");

		for (Comment item : comments) {
			List<String> data = Lists.newArrayList(
				DateUtilities.format("MM/dd/yyyy", item.getCreatedOn()),
				item.getComment(),
				StringUtilities.fullName((String) creatorProps.get(item.getCreatorId()).get("firstName"), (String) creatorProps.get(item.getCreatorId()).get("lastName"))
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", item.getId(),
				"suspended", authenticationService.isSuspended(user)
			);

			response.addRow(data, meta);
		}
		model.addAttribute("response", response);

	}


	/**
	 * Access point to save a comment from client service to a user.
	 */
	@RequestMapping(
		value = "/add_comment_to_user",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder addCommentToUser(
		@Valid @ModelAttribute AddCommentForm form,
		BindingResult result) {


		// Find the user id based on the user number.
		User user = userService.findUserByUserNumber(form.getId());

		// New comment.
		UserComment userComment = new UserComment();
		userComment.setComment(form.getComment());
		userComment.setUser(user);

		userCommentValidator.validate(userComment, result);
		MessageBundle bundle = messageHelper.newBundle();

		if (!result.hasFieldErrors()) {
			UserCommentDTO dto = new UserCommentDTO();
			dto.setComment(form.getComment());
			dto.setUserId(user.getId());
			try {
				commentService.saveOrUpdateClientServiceUserComment(dto);

				return new AjaxResponseBuilder().setSuccessful(true);
			} catch (Exception ex) {
				logger.warn("Error saving userComment for userId={} and comment: {}", new Object[]{dto.getUserId(), dto.getComment()}, ex);
				messageHelper.addError(bundle, "admin.manage.profiles.add_comment_to_user.error");
			}
		} else {
			messageHelper.setErrors(bundle, result);
		}

		return new AjaxResponseBuilder()
			.setSuccessful(false)
			.setMessages(bundle.getErrors());
	}


	@RequestMapping(
		value = "/update_user_fullname",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder updateUserFullName(
		@Valid @ModelAttribute("form_edit_name") EditNameForm form,
		BindingResult bind) {

		AjaxResponseBuilder output = new AjaxResponseBuilder().setSuccessful(false);
		MessageBundle bundle = messageHelper.newBundle();

		User user = userService.findUserById(form.getId());
		user.setFirstName(form.getFirst_name());
		user.setLastName(form.getLast_name());

		// Validate input.
		//userValidator.validate(user, result);

		if (!bind.hasFieldErrors()) {
			Map<String, String> userData = CollectionUtilities.newStringMap(
				"firstName", user.getFirstName(),
				"lastName", user.getLastName()
			);

			try {
				userService.updateUserProperties(user.getId(), userData);
				authenticationService.refreshSessionForUser(user.getId());
				return output.setSuccessful(true);
			} catch (Exception ex) {
				logger.error("error updating user properties for userId={} and properties: {}", new Object[]{user.getId(), userData}, ex);
			}
		} else {
			messageHelper.setErrors(bundle, bind);
		}

		return output.setMessages(bundle.getErrors());
	}


	/**
	 * Returns a company suggestions in a JSON response.
	 */
	@RequestMapping(
		value = "/suggest_company",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody List<SuggestionDTO> suggestCompany(@RequestParam String term) {

		return suggestionService.suggestCompany(term);
	}
}
