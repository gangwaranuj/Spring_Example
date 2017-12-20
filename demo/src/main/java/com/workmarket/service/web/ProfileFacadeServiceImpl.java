package com.workmarket.service.web;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.biz.esignature.gen.Messages.Template;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileLanguage;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.UserAclRoleAssociation;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.assessment.AssessmentUserAssociationPagination;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.DefaultBackgroundImage;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.model.certification.UserCertificationAssociationPagination;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.insurance.UserInsuranceAssociationPagination;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.domains.model.rating.RatingSummary;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.screening.ScreeningObjectConverter;
import com.workmarket.domains.model.screening.ScreeningStatusType;
import com.workmarket.domains.model.skill.UserSkillAssociation;
import com.workmarket.domains.model.skill.UserSkillAssociationPagination;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociation;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociationPagination;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.model.tool.UserToolAssociation;
import com.workmarket.domains.model.tool.UserToolAssociationPagination;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.ESignatureRope;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.admin.DefaultBackgroundImageService;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CertificationService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.InsuranceService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.LicenseService;
import com.workmarket.service.business.LinkedInService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.SpecialtyService;
import com.workmarket.service.business.TagService;
import com.workmarket.service.business.ToolService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.dto.EducationHistoryDTO;
import com.workmarket.service.business.dto.EmploymentHistoryDTO;
import com.workmarket.service.business.dto.EsignatureTemplateDTO;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.UserAvailabilityDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.esignature.EsignatureService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.service.network.NetworkService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.facade.ProfileFacade;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProfileFacadeServiceImpl implements ProfileFacadeService {

	@Autowired private UserService userService;
	@Autowired private TagService tagService;
	@Autowired private TaxService taxService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private AuthorizationService authorizationService;
	@Autowired private EsignatureService esignatureService;
	@Autowired private LaneService laneService;
	@Autowired private ProfileService profileService;
	@Autowired private ScreeningService screeningService;
	@Autowired private LinkedInService linkedInService;
	@Autowired private RatingService ratingService;
	@Autowired private UserGroupService groupService;
	@Autowired private SkillService skillService;
	@Autowired private ToolService toolService;
	@Autowired private SpecialtyService specialtyService;
	@Autowired private UserAssetAssociationDAO userAssetAssociationDAO;
	@Autowired private CertificationService certificationService;
	@Autowired private LicenseService licenseService;
	@Autowired private InsuranceService insuranceService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private DefaultBackgroundImageService defaultBackgroundImageService;
	@Autowired private CompanyService companyService;
	@Autowired private IndustryService industryService;
	@Autowired private AddressService addressService;
	@Autowired private VendorService vendorService;
	@Autowired private UserRoleService userRoleService;
	@Autowired private NetworkService networkService;

	@Autowired @Qualifier("ESignatureDoorman")
	private Doorman eSignatureDoorman;

	@Override
	public boolean isCurrentUserAuthorizedToSeeProfile(ExtendedUserDetails currentUser, ProfileFacade profileFacade) {
		if (profileFacade == null) {
			return false;
		}
		// Determine various permissions / roles
		boolean isInternal = currentUser.hasAnyRoles("ROLE_INTERNAL");
		boolean isCurrentUserBlocked = userService.isUserBlockedForCompany(currentUser.getId(), currentUser.getCompanyId(), profileFacade.getCompanyId());
		boolean isProfileViewableViaGroupSharing = networkService.isProfileViewableViaGroupSharing(currentUser.getCompanyId(), profileFacade.getId());

		// When not the owner, not co-workers, if there's no existing relationship
		// and the user is not a shared worker and the user is not viewable via a shared group,
		// disallow access to the profile.
		if (!profileFacade.isOwner() && !isInternal) {
			if (isCurrentUserBlocked ||
							(!isProfileViewableViaGroupSharing && profileFacade.getLaneType() == null &&
											!profileFacade.getRequestContext().equals(RequestContext.ADMIN.getCode()))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * When not the company owner, not the same user as the profile, not internal, and not blocked for this company
	 * and finally, if we are not an admin, disallow access to edit the profile.
	 *
	 * @param currentUser The user requesting to edit the profile
	 * @param profileFacade The profile in question
	 *
	 * @return true if the requesting user can edit, false otherwise
	 */
	@Override
	public boolean isCurrentUserAuthorizedToEditProfile(ExtendedUserDetails currentUser, ProfileFacade profileFacade) {
		if (profileFacade == null) {
			return false;
		}
		// Determine various permissions / roles
		boolean isInternal = currentUser.hasAnyRoles("ROLE_INTERNAL");

		// When not the company owner, not the same user as the profile, not internal, and not blocked for this company
		// and finally, if we are not an admin, disallow access to edit the profile.
		if (!profileFacade.isOwner() && !isInternal && !currentUser.getUserNumber().equals(profileFacade.getUserNumber())) {
			if (!profileFacade.getRequestContext().equals(RequestContext.ADMIN.getCode())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ProfileFacade findProfileFacadeByUserNumber(String userNumber) throws Exception {
		Assert.notNull(userNumber);
		return findProfile(userService.findUserByUserNumber(userNumber));
	}

	@Override
	public ProfileFacade findProfileFacadeByCompanyNumber(String companyNumber) throws Exception {
		Assert.notNull(companyNumber);
		return findCompanyProfile(companyService.findCompanyByNumber(companyNumber));
	}

	@Override
	public List<ProfileFacade> findSearchCardProfileFacadeByUserIds(final List<Long> userIds, final Long currentUserId) throws Exception {
		Assert.notEmpty(userIds);
		Assert.notNull(currentUserId);
		final List<User> users = userService.findAllUsersWithProfileAndCompanyByIds(userIds);

		final User currentUser = userService.getUser(currentUserId);


		// do some bulk retrieval so we can get all data in one call vs per user
		Map<Long, Address> userAddresses = findUserAddresses(users);
		List<Long> blockedUsers = userService.ListBlockedUsers(currentUser.getCompany().getId());
		List<Long> blockedCompanies = userService.ListBlockedCompanies(currentUser.getCompany().getId());

		// now populate our response
		final List<ProfileFacade> profileFacades = Lists.newArrayList();
		for (User user : users) {
			profileFacades.add(
				findSearchCardProfile(user, userAddresses.get(user.getId()),
					blockedUsers.contains(user.getId()) || blockedCompanies.contains(user.getCompany().getId()),
					currentUser));
		}
		return profileFacades;
	}

	private Map<Long, Address> findUserAddresses(final List<User> users) {
		Map<Long, Address> result = Maps.newHashMap();

		List<Long> userIds = Lists.newArrayListWithCapacity(users.size());
		Map<Long, Long> addressIdToUser = Maps.newHashMap();
		for (User u : users) {
			Profile p = u.getProfile();
			if (p != null && p.getAddressId() != null) {
				addressIdToUser.put(p.getAddressId(), u.getId());
			}
		}

		// now retrieve our user ids
		if (addressIdToUser.size() > 0) {
			List<Address> addresses = addressService.findByIds(Lists.newArrayList(addressIdToUser.keySet()));
			for (Address a : addresses) {
				Long userId = addressIdToUser.get(a.getId());
				if (userId != null) {
					result.put(userId, a);
				}
			}
		}

		return result;
	}

	private ProfileFacade findCompanyProfile(Company company) throws Exception {
		Assert.notNull(company);

		final User currentUser = authenticationService.getCurrentUser();

		ProfileFacade facade = new ProfileFacade();
		facade.initializeDefaults();
		if (company.getAddress() != null) {
			facade.setCompanyAddress(new AddressDTO(company.getAddress()));
		}
		facade.setCompanyId(company.getId());
		facade.setCompanyNumber(company.getCompanyNumber());
		facade.setCompanyName(company.getEffectiveName());
		facade.setCompanyOverview(company.getOverview());
		facade.setCompanyWebsite(company.getWebsite());
		facade.setCreatedOn(company.getCreatedOn());

		setCompanyAvatars(facade, company);
		setTaxEntityInformation(facade, company);

		facade.setConfirmedBankAccount(companyService.hasConfirmedBankAccounts(company.getId()));

		RatingSummary ratingSummary = ratingService.findRatingSummaryForUserByCompany(null, company.getId());
		facade.setRating(ratingSummary.getSatisfactionRate());
		facade.setRatingCount((ratingSummary.getCount().intValue()));

		facade.setBlocked(vendorService.isVendorBlockedByCompany(currentUser.getCompany().getId(), company.getId()));

		return facade;
	}

	private ProfileFacade findProfile(User user) throws Exception {
		Assert.notNull(user);

		final User currentUser = authenticationService.getCurrentUser();
		final RequestContext context = authorizationService.getRequestContext(user.getId());

		ProfileFacade facade = new ProfileFacade();
		facade.initializeDefaults();

		setUserDefaultProfile(facade, user, context, currentUser);
		setUserCompanyProperties(facade, user, currentUser);
		setUserRating(facade, user);
		setUserSkills(facade, user);
		setUserTools(facade, user);
		setUserSpecialty(facade, user);
		setUserScreenings(facade, user);
		setUserLinkedIn(facade, user);
		setUserProfileLanguages(facade, user);
		setUserCertifications(facade, user, context, currentUser);
		setUserLicenses(facade, user, context, currentUser);
		setUserInsurances(facade, user, context, currentUser);

		// Conditionally include protected elements
		// If this is a "public" view, then this is all that we need.
		// Unless a WM employee; we get special snooping privileges.
		if (RequestContext.PUBLIC.equals(context)
				&& (facade.getLaneType() == null || facade.getLaneType() == LaneType.LANE_4.ordinal()) && !userRoleService.isInternalUser(user))
			return facade;

		setUserAssessments(facade, user, context, currentUser);
		setUserResumes(facade, user);
		setUserGroups(facade, user, context, currentUser);
		setPrivateTags(facade, user, context, currentUser);
		setUserProfileCompleteness(facade, user, context);
		setUserRoles(facade, user, context, currentUser);
		setUserLane3Properties(facade, user, context, currentUser);
		setRecruitingCampaign(facade, user);

		final MutableBoolean esignatureEnabled = new MutableBoolean(false);
		eSignatureDoorman.welcome(new UserGuest(authenticationService.getCurrentUser()), new ESignatureRope(esignatureEnabled));
		if (esignatureEnabled.booleanValue()) {
			setEsignatures(facade, user, currentUser);
		}

		return facade;
	}

	private ProfileFacade findSearchCardProfile(User user, Address address, boolean isBlocked, User currentUser) throws Exception {
		Assert.notNull(user);

		ProfileFacade facade = new ProfileFacade();
		facade.initializeDefaults();

		setUserBasicProfile(facade, user);
		setUserProfileSummary(facade, address, user);
		setUserCompanySummary(facade, user, isBlocked, currentUser);

		// Not currently used in search card
		// setUserTools(facade, user);
		// setUserSpecialty(facade, user);

		setUserSkills(facade, user);
		setUserProfileLanguages(facade, user);

		// TODO: these fields are being highlighted
		// TODO: if no match, what do we display?
		//setUserCertificationSummary(facade, user);
		//setUserLicenseSummary(facade, user);
		//setUserInsuranceSummary(facade, user);

		//setSearchUserGroups(facade, user, currentUser);

		return facade;
	}

	private void setUserDefaultProfile(final ProfileFacade facade, final User user,
	                                   final RequestContext context, final User currentUser) {
		// Build default/public view
		setUserBasicProfile(facade, user);
		setUserAdditionalProfile(facade, user, context, currentUser);
	}

	private void setUserBasicProfile(final ProfileFacade facade, final User user) {
		final Long userId = user.getId();
		facade.setId(userId);
		facade.setUuid(user.getUuid());
		facade.setUserNumber(user.getUserNumber());
		facade.setFirstName(user.getFirstName());
		facade.setLastName(user.getLastName());
		facade.setEmail(user.getEmail());
		facade.setChangedEmail(user.getChangedEmail());
		facade.setSecondaryEmail(user.getSecondaryEmail());
		facade.setEmailConfirmed(authenticationService.getEmailConfirmed(user));
		facade.setCreatedOn(user.getCreatedOn());
		facade.setUserStatusType(authenticationService.getUserStatus(user).getCode());

	}

	private void setUserAdditionalProfile(final ProfileFacade facade, final User user,
										  final RequestContext context, final User currentUser) {
		final LaneContext laneContext =
				laneService.getLaneContextForUserAndCompany(user.getId(), currentUser.getCompany().getId(), false);
		final ProfileDTO profileDTO = profileService.findProfileDTO(user.getId());
		copyDataFromProfile(facade, profileDTO);
		setUserAvatars(facade, user);
		setCoordinates(facade, user);
		setMboProfile(facade, user);
		setBackgroundImage(facade, user);
		setConfirmedBankAccount(facade, user);
		setTaxEntityInformation(facade, user);
		setCurrentUserGroupPermissions(facade);
		for (UserAvailability a : userService.findWeeklyWorkingHours(user.getId())) {
			facade.getWorkingHours().add(UserAvailabilityDTO.newDTO(a));
		}

		if (laneContext != null) {
			facade.setLaneType(laneContext.getLaneType().ordinal());
			facade.setLaneTypeApprovalStatus(laneContext.getApprovalStatus().name());
		}

		facade.setRequestContext(context.getCode());
	}

	private void setUserCompanyProperties(final ProfileFacade facade, final User user, final User currentUser) {
		final Company company = profileService.findCompany(user.getId());
		facade.setBlocked(userService.isUserBlockedByCompany(user.getId(), company.getId(), currentUser.getCompany().getId()));
		if (company.getAddress() != null) {
			facade.setCompanyAddress(new AddressDTO(company.getAddress()));
		}

		facade.setCompanyId(company.getId());
		facade.setCompanyName(company.getEffectiveName());
		facade.setCompanyOverview(company.getOverview());
		facade.setCompanyWebsite(company.getWebsite());
	}

	private void setUserCompanySummary(final ProfileFacade facade, final User user, final boolean isBlocked, final User currentUser) {
		final Company company = user.getCompany();

		if (company == null) {
			return;
		}

		facade.setCompanyId(company.getId());
		facade.setCompanyName(company.getName());
		facade.setCompanyOverview(company.getOverview());
		facade.setCompanyWebsite(company.getWebsite());

		// Blocked by User?
		facade.setBlocked(isBlocked);
	}

	private void setUserRating(final ProfileFacade facade, final User user) {
		facade.setRating(ratingService.findSatisfactionRateForUser(user.getId()));
		facade.setRatingCount(ratingService.countAllUserRatings(user.getId()));
	}

	private void setUserSkills(final ProfileFacade facade, final User user) {
		List<UserSkillAssociation> skills = skillService
				.findAllAssociationsByUser(user.getId(), new UserSkillAssociationPagination(true))
				.getResults();
		for (UserSkillAssociation a : skills) {
			if (!a.getVerificationStatus().isFailed()) {
				facade.getSkills().add(new ProfileFacade.Documentation(
						a.getSkill().getId(),
						a.getSkill().getName(),
						a.getSkill().getIndustry().getName(),
						null
				));
			}
		}
	}

	private void setUserTools(final ProfileFacade facade, final User user) {
		List<UserToolAssociation> tools = toolService
				.findAllAssociationsByUser(user.getId(), new UserToolAssociationPagination(true))
				.getResults();
		for (UserToolAssociation a : tools) {
			if (!a.getVerificationStatus().isFailed()) {
				facade.getTools().add(new ProfileFacade.Documentation(
						a.getTool().getId(),
						a.getTool().getName(),
						a.getTool().getIndustry().getName(),
						null
				));
			}
		}
	}

	private void setUserSpecialty(final ProfileFacade facade, final User user) {
		List<UserSpecialtyAssociation> specialties = specialtyService
				.findAllAssociationsByUser(user.getId(), new UserSpecialtyAssociationPagination(true))
				.getResults();
		for (UserSpecialtyAssociation a : specialties) {
			if (!a.getVerificationStatus().isFailed()) {
				facade.getSpecialties().add(new ProfileFacade.Documentation(
						a.getSpecialty().getId(),
						a.getSpecialty().getName(),
						a.getSpecialty().getIndustry().getName(),
						null
				));
			}
		}
	}

	private void setUserScreenings(final ProfileFacade facade, final User user) {
		final Long userId = user.getId();
		Screening bg = ScreeningObjectConverter.convertScreeningResponseToMonolith(
				screeningService.findMostRecentBackgroundCheck(userId));
		if (bg != null) {
			facade.setBackgroundCheckStatus(bg.getScreeningStatusType().getCode());
			facade.setBackgroundCheckVerified(bg.getScreeningStatusType().getCode().equals(ScreeningStatusType.PASSED));
			facade.setLastBackgroundCheckRequestDate(bg.getRequestDate().getTime());
			facade.setPriorPassedBackgroundCheck(screeningService.hasPassedBackgroundCheck(userId));

			if (bg.getResponseDate() != null) {
				facade.setLastBackgroundCheckResponseDate(bg.getResponseDate().getTime());
			}
		}

		Screening drug = ScreeningObjectConverter.convertScreeningResponseToMonolith(
				screeningService.findMostRecentDrugTest(userId));
		if (drug != null) {
			facade.setDrugTestStatus(drug.getScreeningStatusType().getCode());
			facade.setDrugTestVerified(drug.getScreeningStatusType().getCode().equals(ScreeningStatusType.PASSED));
			facade.setLastDrugTestRequestDate(drug.getRequestDate().getTime());
			facade.setPriorPassedDrugTest(screeningService.hasPassedDrugTest(userId));

			if (drug.getResponseDate() != null) {
				facade.setLastDrugTestResponseDate(drug.getResponseDate().getTime());
			}
		}
	}

	private void setUserLinkedIn(final ProfileFacade facade, final User user) {
		LinkedInPerson linkedInPerson = linkedInService.findMostRecentLinkedInPerson(user.getId());
		facade.setLinkedInVerified(linkedInPerson != null);
		if (facade.getLinkedInVerified() && linkedInPerson != null) {
			facade.setLinkedInPublicProfileUrl(linkedInPerson.getPublicProfileUrl());
			facade.setLinkedInEducation(EducationHistoryDTO.getEducationHistory(linkedInPerson));
			facade.setLinkedInPositions(EmploymentHistoryDTO.getEmploymentHistory(linkedInPerson));
		}
	}

	private void setUserProfileLanguages(final ProfileFacade facade, final User user) {
		// TODO: n+1
		for (ProfileLanguage l : profileService.findProfileLanguages(user.getId())) {
			facade.getLanguages().add(new ProfileFacade.Documentation(
					l.getId(),
					l.getLanguage().getDescription(),
					l.getLanguageProficiencyType().getDescription(),
					null
			));
		}
	}

	private void setUserCertifications(final ProfileFacade facade, final User user,
	                                   final RequestContext context, final User currentUser) {
		UserCertificationAssociationPagination userCertificationAssociationPagination = new UserCertificationAssociationPagination(true);
		for (UserCertificationAssociation a : certificationService.findAllAssociationsByUserId(user.getId(), userCertificationAssociationPagination).getResults()) {
			if (!a.getVerificationStatus().isFailed()) {
				ProfileFacade.Documentation d = new ProfileFacade.Documentation(
						a.getCertification().getId(),
						a.getCertification().getName(),
						a.getCertification().getCertificationVendor().getName(),
						null
				);
				d.setVerficationStatus(a.getVerificationStatus().name());

				if (RequestContext.OWNER.equals(context) || userRoleService.isInternalUser(user))
					for (Asset asset : a.getAssets())
						d.getAssets().add(new ProfileFacade.Documentation.Asset(asset.getId(), asset.getUUID(), asset.getName(), asset.getMimeType(), asset.getLocalUri()));

				facade.getCertifications().add(d);
			}
		}
	}

	private void setUserLicenses(final ProfileFacade facade, final User user,
	                             final RequestContext context, final User currentUser) {
		UserLicenseAssociationPagination licenseAssociationPagination = new UserLicenseAssociationPagination(true);
		licenseAssociationPagination = licenseService.findAllAssociationsByUserId(user.getId(), licenseAssociationPagination);
		for (UserLicenseAssociation a : licenseAssociationPagination.getResults()) {
			if (!a.getVerificationStatus().isFailed()) {
				ProfileFacade.Documentation d = new ProfileFacade.Documentation(
						a.getLicense().getId(),
						a.getLicense().getName(),
						a.getLicense().getState(),
						null
				);
				d.setVerficationStatus(a.getVerificationStatus().name());

				if (RequestContext.OWNER.equals(context) || userRoleService.isInternalUser(user))
					for (Asset asset : a.getAssets())
						d.getAssets().add(new ProfileFacade.Documentation.Asset(asset.getId(), asset.getUUID(), asset.getName(), asset.getMimeType(), asset.getLocalUri()));

				facade.getLicenses().add(d);
			}
		}
	}

	private void setUserInsurances(final ProfileFacade facade, final User user,
	                               final RequestContext context, final User currentUser) {
		UserInsuranceAssociationPagination insuranceAssociationPagination = new UserInsuranceAssociationPagination(true);
		insuranceAssociationPagination = insuranceService.findAllUserInsuranceAssociationsByUserId(user.getId(), insuranceAssociationPagination);
		for (UserInsuranceAssociation a : insuranceAssociationPagination.getResults()) {
			if (!a.getVerificationStatus().isFailed()) {
				ProfileFacade.Documentation d = new ProfileFacade.Documentation(
						a.getInsurance().getId(),
						a.getInsurance().getName(),
						a.getInsurance().getIndustry().getName(),
						a.getCoverage()
				);
				d.setVerficationStatus(a.getVerificationStatus().name());

				if (RequestContext.OWNER.equals(context) || userRoleService.isInternalUser(user))
					for (Asset asset : a.getAssets())
						d.getAssets().add(new ProfileFacade.Documentation.Asset(asset.getId(), asset.getUUID(), asset.getName(), asset.getMimeType(), asset.getLocalUri()));

				facade.getInsurance().add(d);
			}
		}
	}

	private void setUserAssessments(final ProfileFacade facade, final User user,
	                                final RequestContext context, final User currentUser) {
		List<AssessmentUserAssociation> assessments = Lists.newArrayList();
		AssessmentUserAssociationPagination assessmentUserAssociationPagination = new AssessmentUserAssociationPagination(true);
		if (RequestContext.OWNER.equals(context) || RequestContext.ADMIN.equals(context) || userRoleService.isInternalUser(user)) {
			assessmentUserAssociationPagination = assessmentService.findAssessmentUserAssociationsByUser(user.getId(), assessmentUserAssociationPagination);
		} else {
			assessmentUserAssociationPagination.setFilters(CollectionUtilities.newStringMap(
					AssessmentUserAssociationPagination.FILTER_KEYS.COMPANY_ID.toString(), currentUser.getCompany().getId().toString()
			));
			assessmentUserAssociationPagination = assessmentService.findAssessmentUserAssociationsByUser(user.getId(), assessmentUserAssociationPagination);
		}

		if (assessmentUserAssociationPagination != null && assessmentUserAssociationPagination.getResults() != null) {
			assessments = assessmentUserAssociationPagination.getResults();
		}

		for (AssessmentUserAssociation a : assessments) {
			if (!a.isComplete()) continue;
			if (AbstractAssessment.SURVEY_ASSESSMENT_TYPE.equals(a.getAssessment().getType())) continue;

			ProfileFacade.Documentation d = new ProfileFacade.Documentation(
					a.getAssessment().getId(),
					a.getAssessment().getName(),
					a.getAssessment().getDescription(),
					a.getScore().toString()
			);
			d.setSecondaryId(CollectionUtilities.last(a.getAttempts()).getId());
			d.setVerficationStatus(a.getPassedFlag() ? VerificationStatus.VERIFIED.name() : VerificationStatus.FAILED.name());
			d.setCreatedOn(a.getCompletedOn().getTime());

			facade.getAssessments().add(d);
		}
	}

	private void setUserResumes(final ProfileFacade facade, final User user) {
		for (Asset a : profileService.findAllUserResumes(user.getId())) {
			ProfileFacade.Documentation d = new ProfileFacade.Documentation(
					a.getId(),
					a.getName(),
					null,
					null
			);
			d.getMeta().put("uuid", a.getUUID());
			d.getAssets().add(new ProfileFacade.Documentation.Asset(a.getId(), a.getUUID(), a.getName(), a.getMimeType(), null));
			facade.getResumes().add(d);
		}
	}

	private void setSearchUserGroups(final ProfileFacade facade, final User user, final User currentUser)
	{
		ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();
		pagination.setReturnAllRows();  // omits shared and private groups by default

		for (ManagedCompanyUserGroupRow g : groupService.findMyGroupMemberships(user, pagination).getResults()) {
			Map groupData = ImmutableMap.of("id", g.getGroupId(), "name", g.getName());
			facade.getPublicGroups().add(groupData);
		}
	}

	private void setUserGroups(final ProfileFacade facade, final User user,
	                           final RequestContext context, final User currentUser) {
		ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();
		pagination.setReturnAllRows();

		if (!RequestContext.OWNER.equals(context) && userRoleService.isAdminOrManager(user)) {
			pagination.setShowPrivateGroups(true);
			pagination.setShowSharedGroups(true);
			pagination.setCurrentUserCompanyId(currentUser.getCompany().getId());
		}

		Long myCompanyId = currentUser.getCompany().getId();
		for (ManagedCompanyUserGroupRow g : groupService.findMyGroupMemberships(user.getId(), pagination).getResults()) {
			Map groupData = ImmutableMap.of("id", g.getGroupId(), "name", g.getName());

			if (g.isSharedWithMe(myCompanyId) && !g.isSharedByMe(myCompanyId)) {
				facade.getSharedGroups().add(groupData);
			} else if (g.isOpenMembership()) {
				facade.getPublicGroups().add(groupData);
			} else {
				facade.getPrivateGroups().add(groupData);
			}
		}
	}

	private void setPrivateTags(final ProfileFacade facade, final User user,
	                            final RequestContext context, final User currentUser) {
		if (!RequestContext.OWNER.equals(context) && authenticationService.userHasAclRoles(
				currentUser.getId(), new Long[]{AclRole.ACL_ADMIN, AclRole.ACL_MANAGER, AclRole.ACL_USER}, false
		)) {
			if (facade.getLaneType() != null
					&& (facade.getLaneType() == LaneType.LANE_0.ordinal() || facade.getLaneType() == LaneType.LANE_1.ordinal())) {

				facade.setPrivateTags(Lists.newArrayList(tagService
						.findAllUniqueCompanyAdminUserTagNames(currentUser.getCompany().getId(), user.getId(), context)));
			} else {
				facade.setPrivateTags(Lists.newArrayList(tagService
						.findAllUniqueCompanyUserTagNames(currentUser.getCompany().getId(), user.getId(), context)));
			}
		}
	}

	private void setUserProfileCompleteness(final ProfileFacade facade, final User user, final RequestContext context)
			throws Exception {
		if (RequestContext.ADMIN.equals(context) || RequestContext.OWNER.equals(context)) {
			facade.setProfileCompleteness(profileService.getUserProfileCompleteness(user.getId()));
		}
	}

	private void setUserRoles(final ProfileFacade facade, final User user,
	                          final RequestContext context, final User currentUser) {
		if (RequestContext.ADMIN.equals(context) || (RequestContext.OWNER.equals(context) && userRoleService.isAdminOrManager(user))) {
			for (UserAclRoleAssociation a : userRoleService.getUserRoleAssociations(user)) {
				if (!a.getDeleted()) {
					facade.getRoleIds().add(a.getRole().getId());
					facade.getRoleNames().add(a.getRole().getName());
				}
			}
		}
	}

	private void setUserLane3Properties(final ProfileFacade facade, final User user,
	                                    final RequestContext context, final User currentUser) {
		if (RequestContext.OWNER.equals(context) || userRoleService.isInternalUser(user)) {
			facade.setLane3Pending(user.isLane3Pending());
			facade.setLane3Active(authenticationService.isLane3Active(user));
		}
	}

	private void setRecruitingCampaign(final ProfileFacade facade, final User user) {
		if (facade.getLaneType() != null && facade.getLaneType() == LaneType.LANE_2.ordinal()) {
			if (user.getRecruitingCampaign() != null) {
				facade.setRecruitingCampaign(new ProfileFacade.Documentation(
						user.getRecruitingCampaign().getId(),
						user.getRecruitingCampaign().getTitle(),
						null,
						null
				));
			}
		}
	}

	private void setEsignatures(final ProfileFacade facade, final User user, final User currentUser) {
		facade.setEsignatures(getEsignatures(user, currentUser));
	}

	private List<EsignatureTemplateDTO> getEsignatures(final User user, final User currentUser) {
		final List<Template> templates =
				esignatureService.getSignedTemplates(currentUser.getCompany().getUuid(), user.getUuid());
		final List<EsignatureTemplateDTO> esignatures = new ArrayList<>();
		for (final Template template : templates) {
			esignatures.add(
					EsignatureTemplateDTO.newBuilder()
							.setTemplateUuid(template.getId())
							.setName(template.getTitle())
							.build());
		}
		return esignatures;
	}

	private void setUserAvatars(ProfileFacade facade, User user) {
		UserAssetAssociation avatars = userAssetAssociationDAO.findUserAvatars(user.getId());
		if (avatars != null) {
			Asset avatarOriginal = avatars.getAsset();
			Asset avatarSmall = avatars.getTransformedSmallAsset();
			Asset avatarLarge = avatars.getTransformedLargeAsset();

			if (avatarOriginal != null) {
				facade.setAvatarOriginalAssetUri(avatarOriginal.getCdnUri());
			}
			if (avatarSmall != null) {
				facade.setAvatarSmallAssetUri(avatarSmall.getCdnUri());
			}
			if (avatarLarge != null) {
				facade.setAvatarLargeAssetUri(avatarLarge.getCdnUri());
			}
		}
	}

	private void setCompanyAvatars(ProfileFacade facade, Company company) {
		CompanyAssetAssociation avatars = companyService.findCompanyAvatars(company.getId());
		if (avatars != null) {
			Asset avatarOriginal = avatars.getAsset();
			Asset avatarSmall = avatars.getTransformedSmallAsset();
			Asset avatarLarge = avatars.getTransformedLargeAsset();

			if (avatarOriginal != null) {
				facade.setAvatarOriginalAssetUri(avatarOriginal.getCdnUri());
			}
			if (avatarSmall != null) {
				facade.setAvatarSmallAssetUri(avatarSmall.getCdnUri());
			}
			if (avatarLarge != null) {
				facade.setAvatarLargeAssetUri(avatarLarge.getCdnUri());
			}
		}
	}

	private void setCoordinates(ProfileFacade facade, User user) {
		Coordinate userCoordinates = addressService.getCoordinatesForUser(user.getId());
		if (userCoordinates == null) {
			userCoordinates = companyService.findLatLongForCompany(user.getCompany().getId());
		}

		if (userCoordinates != null) {
			facade.setLatitude(userCoordinates.getLatitude());
			facade.setLongitude(userCoordinates.getLongitude());
		}
	}

	private void setBackgroundImage(ProfileFacade facade, User user) {
		UserAssetAssociation backgroundImage = userAssetAssociationDAO.findBackgroundImage(user.getId());
		if (backgroundImage != null) {
			facade.setBackgroundImageUri(backgroundImage.getAsset().getUri());
		} else {
			Optional<DefaultBackgroundImage> image = defaultBackgroundImageService.getCurrentDefaultBackgroundImage();
			if (image.isPresent()) {
				facade.setBackgroundImageUri(image.get().getAsset().getUri());
			}
		}
	}

	private void setConfirmedBankAccount(ProfileFacade facade, User user) {
		facade.setConfirmedBankAccount(companyService.hasConfirmedBankAccounts(user.getCompany().getId()));
	}

	private void setTaxEntityInformation(ProfileFacade facade, User user) {
		AbstractTaxEntity activeTaxEntity = taxService.findActiveTaxEntity(user.getId());
		setTaxEntityInformation(facade, activeTaxEntity);
	}

	private void setTaxEntityInformation(ProfileFacade facade, Company company) {
		AbstractTaxEntity activeTaxEntity = taxService.findActiveTaxEntityByCompany(company.getId());
		setTaxEntityInformation(facade, activeTaxEntity);
	}

	private void setTaxEntityInformation(ProfileFacade facade, AbstractTaxEntity activeTaxEntity){
		if (activeTaxEntity != null) {
			facade.setTaxEntityExists(true);
			facade.setTaxEntityCountry(activeTaxEntity.getCountry());
			if (activeTaxEntity instanceof UsaTaxEntity) {
				facade.setVerifiedTaxEntity(activeTaxEntity.getStatus().isApproved());
				facade.setRejectedTaxEntity(activeTaxEntity.getStatus().isRejected());
			} else{
				//setting the proper "verified" status based on activeFlag and verificationPending flag for Canadian or International tax entity
				facade.setVerifiedTaxEntity(activeTaxEntity.getActiveFlag() && (activeTaxEntity.getStatus().getCode().equals(TaxVerificationStatusType.VALIDATED) || activeTaxEntity.getStatus().getCode().equals(TaxVerificationStatusType.SIGNED_FORM_W8)));
			}
		}
	}

	private void setCurrentUserGroupPermissions(ProfileFacade facade) {
		final Map<Long, String> inviteGroups = Maps.newLinkedHashMap();
		final Map<Long, Boolean> groupPermissions = Maps.newHashMap();

		UserGroupPagination groupPagination = new UserGroupPagination(true);
		groupPagination.addFilter(UserGroupPagination.FILTER_KEYS.IS_ACTIVE, Boolean.TRUE);
		groupPagination = groupService.findAllGroupsByCompanyId(authenticationService.getCurrentUser().getCompany().getId(), groupPagination);
		for (UserGroup g : groupPagination.getResults()) {
			inviteGroups.put(g.getId(), g.getName());
			groupPermissions.put(g.getId(), g.getOpenMembership());
		}

		facade.setGroupsAvailableToInvite(inviteGroups);
		facade.setCurrentCompanyGroupPermission(groupPermissions);
	}

	private void setMboProfile(ProfileFacade facade, User user) {
		facade.setMboProfile(profileService.findMboProfile(user.getId()));
	}

	private void setUserProfileSummary(ProfileFacade facade, Address address, User user) {
		Profile profile = user.getProfile();

		if (profile == null) {
			return;
		}

		facade.setJobTitle(profile.getJobTitle());
		facade.setOverview(profile.getOverview());

		if (address != null) {
			facade.setAddress(new AddressDTO(address));
		}
	}

	private void copyDataFromProfile(ProfileFacade facade, ProfileDTO profile) {
		if (profile == null) {
			return;
		}
		facade.setJobTitle(profile.getJobTitle());
		facade.setOverview(profile.getOverview());
		facade.setZipCode(profile.getPostalCode());

		if (profile.getMaxTravelDistance() != null) {
			facade.setMaxTravelDistance(profile.getMaxTravelDistance().doubleValue());
		}
		if (profile.getMinOnsiteHourlyRate() != null) {
			facade.setMinOnsiteHourlyRate(profile.getMinOnsiteHourlyRate().doubleValue());
		}
		if (profile.getMinOnsiteWorkPrice() != null) {
			facade.setMinOnsiteWorkPrice(profile.getMinOnsiteWorkPrice().doubleValue());
		}
		if (profile.getMinOffsiteHourlyRate() != null) {
			facade.setMinOffsiteHourlyRate(profile.getMinOffsiteHourlyRate().doubleValue());
		}
		if (profile.getMinOffsiteWorkPrice() != null) {
			facade.setMinOffsiteWorkPrice(profile.getMinOffsiteWorkPrice().doubleValue());
		}

		facade.setBlacklistedPostalCodes(profile.getBlacklistedPostalCodes());

		facade.setWorkPhone(profile.getWorkPhone());
		facade.setWorkPhoneExtension(profile.getWorkPhoneExtension());
		facade.setWorkPhoneInternationalCode(profile.getWorkPhoneInternationalCallingCodeId());

		facade.setMobilePhone(profile.getMobilePhone());
		facade.setMobilePhoneInternationalCode(profile.getMobilePhoneInternationalCallingCodeId());

		Long addressId = profile.getAddressId();
		if (addressId != null) {
			Address address = addressService.findById(addressId);
			if (address != null) {
				facade.setAddress(new AddressDTO(address));
			}
		}
		Set<IndustryDTO> industries = industryService.getIndustryDTOsForProfile(profile.getProfileId());
		for (IndustryDTO industry : industries) {
			facade.getIndustries().add(new ProfileFacade.Documentation(
				industry.getId(),
				industry.getName(),
				null,
				null));
		}
	}
}
