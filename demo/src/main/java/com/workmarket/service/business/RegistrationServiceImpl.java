package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import au.com.bytecode.opencsv.CSVReader;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.BlacklistedEmailDAO;
import com.workmarket.dao.InvitationDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.callingcodes.CallingCodeDAO;
import com.workmarket.dao.changelog.user.UserChangeLogDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.company.CompanySignUpInfoDAO;
import com.workmarket.dao.datetime.TimeZoneDAO;
import com.workmarket.dao.integration.autotask.AutotaskUserDAO;
import com.workmarket.dao.postalcode.PostalCodeDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.dao.random.UserRandomIdentifierDAO;
import com.workmarket.dao.recruiting.RecruitingCampaignDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.groups.facade.GroupInvitationFacade;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.service.UserGroupRequirementSetService;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.BlacklistedEmail;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanySignUpInfo;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationPagination;
import com.workmarket.domains.model.InvitationStatusType;
import com.workmarket.domains.model.InvitationType;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.RoleType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.UserAclRoleAssociation;
import com.workmarket.domains.model.changelog.user.UserAclRoleRemovedChangeLog;
import com.workmarket.domains.model.changelog.user.UserCreatedChangeLog;
import com.workmarket.domains.model.company.CustomerType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.integration.IntegrationUser;
import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.request.PasswordResetRequest;
import com.workmarket.domains.model.request.RequestStatusType;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import com.workmarket.service.business.dto.InvitationDTO;
import com.workmarket.service.business.dto.InvitationUserRegistrationDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.event.BuyerSignUpSugarIntegrationEvent;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.infra.EncryptionService;
import com.workmarket.service.infra.business.AuthTrialCommon;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.network.NetworkService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class RegistrationServiceImpl implements RegistrationService, MessageSourceAware {

	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private AddressService addressService;
	@Autowired private AssetService assetService;
	@Autowired private CompanyService companyService;
	@Autowired private DateTimeService dateTimeService;
	@Autowired private EmailTemplateFactory emailTemplateFactory;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private LaneService laneService;
	@Autowired private NotificationService notificationService;
	@Autowired private ProfileService profileService;
	@Autowired private RecruitingService recruitingService;
	@Autowired private RequestService requestService;
	@Autowired private SummaryService summaryService;
	@Autowired private TemplateService templateService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private GroupInvitationFacade groupInvitationFacade;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private UserService userService;
	@Autowired private InvitationService invitationService;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private AdmissionService admissionService;
	@Autowired private PlanService planService;
	@Autowired private NetworkService networkService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private IndustryService industryService;
	@Autowired private MessageSource messageSource;
	@Autowired private TimeZoneDAO timeZoneDAO;
	@Autowired private UserRandomIdentifierDAO randomNumberGenerator;
	@Autowired private UserIndexer userIndexer;
	@Autowired private BlacklistedEmailDAO blacklistedEmailDAO;
	@Autowired private AutotaskUserDAO autotaskUserDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private RecruitingCampaignDAO recruitingCampaignDAO;
	@Autowired private InvitationDAO invitationDAO;
	@Autowired private ProfileDAO profileDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserChangeLogDAO userChangeLogDAO;
	@Autowired private PostalCodeDAO postalCodeDAO;
	@Autowired private CallingCodeDAO callingCodeDAO;
	@Autowired private CompanySignUpInfoDAO companySignUpInfoDAO;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private UserRoleService userRoleService;
	@Autowired private AuthTrialCommon trialCommon;
	@Autowired private EncryptionService encryptionService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private UserGroupRequirementSetService userGroupRequirementSetService;
	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired private AuthenticationClient authClient;

	private Meter registerBuyerMeter;
	private Meter registerSellerMeter;
	private static final long USER_REGISTRATION_EMAIL_DELAY = 5000L;
	private static final InvitationStatusType INVITATION_SENT = new InvitationStatusType(InvitationStatusType.SENT);
	private static final InvitationStatusType INVITATION_DECLINED = new InvitationStatusType(InvitationStatusType.DECLINED);
	private static final InvitationStatusType INVITATION_INSYSTEM = new InvitationStatusType(InvitationStatusType.INSYSTEM);

	@PostConstruct
	private void init() {
		WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "registration-service");
		registerBuyerMeter = wmMetricRegistryFacade.meter("register_buyer");
		registerSellerMeter = wmMetricRegistryFacade.meter("register_seller");
	}

	private static final Log logger = LogFactory.getLog(RegistrationServiceImpl.class);

	@Value("${baseurl}")
	private String baseUrl;

	@Override
	public void blacklistEmail(String email) {
		if (isBlacklisted(email)) {
			return;
		}
		blacklistedEmailDAO.saveOrUpdate(new BlacklistedEmail(email));
	}


	private void removeFromBlacklist(String email) {
		if (isBlacklisted(email)) {
			blacklistedEmailDAO.deleteFromBlackList(email);
		}
	}

	@Override
	public void addNewIntegrationUser(IntegrationUser integrationUser) {
		autotaskUserDAO.addUser((AutotaskUser) integrationUser);
	}

	@Override
	public Optional<AutotaskUser> getIntegrationUserByUserId(Long userId) {
		return autotaskUserDAO.findUserByUserId(userId);
	}

	@Override
	public Boolean isBlacklisted(String email) {
		return blacklistedEmailDAO.isBlacklisted(email);
	}

	@Override
	public User registerNew(UserDTO userDTO, Long invitationId) throws Exception {
		return registerNew(userDTO, invitationId, null, null, null, false);
	}

	@Override
	public void validatePassword(String password) {
		Assert.hasText(password);
		Assert.state(password.length() >= Constants.PASSWORD_MIN_LENGTH, "Password has to be at least 6 characters long.");
	}

	@Override
	public void checkForExistingUser(String email) throws Exception {
		User user = userService.findUserByEmail(email);

		if (user != null) {
			logger.error(String.format("[registration] user already registered with email %s", email));
			throw new Exception(messageSource.getMessage("register.emailuseerror", new Object[]{email}, null));
		}
	}

	@Override
	public Company createUserCompany(String companyName, AddressDTO addressDTO, boolean operatingAsIndividualFlag, boolean isBuyer, boolean isCompanyNameBlank) {
		if (operatingAsIndividualFlag || isCompanyNameBlank) {
			companyName = Constants.DEFAULT_COMPANY_NAME;
		}

		Address address = null;
		if (addressDTO != null &&
			StringUtilities.all(addressDTO.getCountry(), addressDTO.getCity(), addressDTO.getPostalCode())) {
			addressDTO.setAddressTypeCode(AddressType.COMPANY);
			address = addressService.saveOrUpdate(addressDTO);
		}

		Company company = companyService.createCompany(
			companyName,
			operatingAsIndividualFlag,
			isBuyer ? Company.BUYER_CUSTOMER_TYPE : Company.RESOURCE_CUSTOMER_TYPE
		);

		if (address != null) {
			company.setAddress(address);
		}
		return company;
	}

	@Override
	public User createUserObject(UserDTO userDTO, Company userCompany) {

		// Create user object
		User user = new User();

		// Set status type
		// deprecated, but this line will eventually die.
		user.setUserStatusType(new UserStatusType(UserStatusType.APPROVED));

		// Copy DTO properties into user
		BeanUtils.copyProperties(userDTO, user);

		// Set roles
		Set<RoleType> roles = new HashSet<>();
		roles.add(new RoleType(RoleType.SUPERUSER));
		roles.add(new RoleType(RoleType.CONTRACTOR));
		userRoleService.setRoles(user, roles);

		// Set company, userNumber
		user.setCompany(userCompany);
		setUserNumber(user);

		return user;
	}

	@Override
	public void registerUserInvitation(User user, Long invitationId) {
		// Register that the user registered using the invitation
		if (invitationId != null) {
			Invitation invitation = invitationService.findInvitationById(invitationId);
			user.setInvitation(invitation);
		}
	}

	@Override
	public void associateCompanyWithUser(Company company, User user) {
		company.setCreatedBy(user);
		if (company.getOperatingAsIndividualFlag()) {
			company.setEffectiveName(StringUtilities.fullName(user.getFirstName(), user.getLastName()));
		}
	}

	@Override
	public void createUserProfile(
		User user,
		ProfileDTO profileDTO,
		AddressDTO addressDTO,
		boolean isBuyer,
		Address companyAddress,
		Long recruitingCampaignId,
		Long invitationId) {

		// Assign location type COMMERCIAL by default
		profileService.updateLocationTypePreferences(user.getId(), new Long[]{LocationType.COMMERCIAL_CODE});

		// Create default profile for all new users
		Profile profile = new Profile();
		TimeZone timezone = dateTimeService.findTimeZonesByTimeZoneId(Constants.WM_TIME_ZONE);

		if (profileDTO != null) {
			profile = profileDTO.toProfile();

			if (profileDTO.getWorkPhoneInternationalCode() != null) {
				profile.setWorkPhoneInternationalCode(invariantDataService.findCallingCodeFromID(profileDTO.getWorkPhoneInternationalCode()));
			}
			if (profileDTO.getMobilePhoneInternationalCode() != null) {
				profile.setMobilePhoneInternationalCode(invariantDataService.findCallingCodeFromID(profileDTO.getMobilePhoneInternationalCode()));
			}
			if (profileDTO.getSmsPhoneInternationalCode() != null) {
				profile.setSmsPhoneInternationalCode(invariantDataService.findCallingCodeFromID(profileDTO.getSmsPhoneInternationalCode()));
			}

			profile.setUser(user);

			// Set the profile's zipcode
			if (addressDTO != null && StringUtilities.all(addressDTO.getCity(), addressDTO.getCountry())) {
				profileService.updateProfilePostalCode(profile, profileDTO.getPostalCode(), addressDTO);
			}

			if (addressDTO != null && addressDTO.getPostalCode() != null) {
				timezone = dateTimeService.matchTimeZoneForPostalCode(addressDTO.getPostalCode(), addressDTO.getCountry(), addressDTO.getState(), addressDTO.getCity());
			} else if (profileDTO.getPostalCode() != null) {
				timezone = dateTimeService.matchTimeZoneForPostalCode(profileDTO.getPostalCode(), profileDTO.getCountry(), addressDTO.getState(), profileDTO.getCity());
			}

			profileDTO.setFindWork(!isBuyer);

		} else if (addressDTO != null && addressDTO.getPostalCode() != null) {
			timezone = dateTimeService.matchTimeZoneForPostalCode(addressDTO.getPostalCode(), addressDTO.getCountry(), addressDTO.getState(), addressDTO.getCity());
		}

		profile.setTimeZone(timezone);
		profile.setUser(user);

		user.setProfile(profile);
		if (companyAddress != null) {
			profile.setAddressId(companyAddress.getId());
		}

		// only one option can be set on initial registration - invitees should be sellers
		boolean isFindWork = !isBuyer || recruitingCampaignId != null || invitationId != null;
		profile.setFindWork(isFindWork);
		profile.setManageWork(!isFindWork);

		profileService.saveOrUpdateProfile(profile);

		if (profileDTO != null && profileDTO.getIndustryId() != null) {
			Industry industry = invariantDataService.findIndustry(profileDTO.getIndustryId());
			Set<Industry> industries = industryService.getIndustriesForProfile(profile.getId());
			industries.add(industry);

			industryService.setIndustriesForProfile(profile.getId(), industries);
		}
	}

	@Override
	public void assignRolesAndRelationships(User user, ProfileDTO profileDTO) throws InvalidAclRoleException {
		Long userId = user.getId();

		//Calling the summary service before assigning the roles since that could change the status to pending
		summaryService.saveUserHistorySummary(user);

		// Assign default ACL roles
		authenticationService.assignAclRolesToUser(userId, new Long[]{AclRole.ACL_ADMIN, AclRole.ACL_DEPUTY});

		if (profileDTO != null && profileDTO.getFindWork()) {
			authenticationService.assignAclRoleToUser(userId, AclRole.ACL_SHARED_WORKER);
		}

		// For any contractor invitations, setup the relationship
		processContractorInvitationsForUser(userId);
	}

	@Override
	public void handleRecruitingCampaignScenario(Long recruitingCampaignId, User user) {
		/*
		 *  Register that the user was invited through a recruiting campaign
		 *  Add user to lane 2 for the recruiting campaign's company (PENDING APPROVAL)
		 */
		if (recruitingCampaignId != null) {
			Long userId = user.getId();

			RecruitingCampaign recruitingCampaign = recruitingService.findRecruitingCampaign(recruitingCampaignId);
			Assert.notNull(recruitingCampaign);

			if (recruitingCampaign.isPrivateCampaign()) {
				user.setLane3ApprovalStatus(ApprovalStatus.OPT_OUT);
			}
			user.setRecruitingCampaign(recruitingCampaign);
			laneService.addUserToCompanyLane2(userId, recruitingCampaign.getCompany().getId());
			laneService.updateLaneAssociationApprovalStatus(userId, recruitingCampaign.getCompany().getId(), ApprovalStatus.PENDING);

			UserGroup campaignGroup = recruitingCampaign.getCompanyUserGroup();
			if (campaignGroup != null) {
				userGroupService.applyToGroup(campaignGroup.getId(), userId);

				// TODO: Micah - temporary to deal with agreements issue on recruiting campaigns.
				// This will go away once agreements are handled like other requirements.
				if (userGroupRequirementSetService.userGroupHasAgreementRequirements(campaignGroup.getId())) {
					userGroupService.removeAssociation(campaignGroup.getId(), userId);
				}

				groupInvitationFacade.inviteUsersToGroups(campaignGroup.getId(), campaignGroup.getOwner().getId(), null, Lists.newArrayList(userId));
			}
		}
	}

	@Override
	public void dispatchNotifications(User user, Company company, boolean isBlankCompanyName, boolean isBuyer) {
		Long userId = user.getId();
		String email = user.getEmail();

		removeFromBlacklist(email);

		EmailTemplate template = emailTemplateFactory.buildRegistrationConfirmUserEmailTemplate(userId);
		notificationService.sendNotification(template);

		if (!isBlankCompanyName) {
			// Check if the company name is similar to one already in the system
			// and alert client services
			// We just want to check this when the user entered a company name
			userNotificationService.onNewCompany(user);
		}

		// Send the lead to Russell
		if (isBuyer) {
			eventRouter.sendEvent(new BuyerSignUpSugarIntegrationEvent(company.getId()));
		}

		userChangeLogDAO.saveOrUpdate(
			new UserCreatedChangeLog(userId, authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId())
		);
	}

	@Override
	public User registerNew(final CreateNewWorkerRequest dto) throws Exception {
		return registerNew(
				convertToUserDTO(dto),
				null,
				dto.getCompanyName(),
				convertToAddressDTO(dto),
				convertToProfileDTO(dto),
				false);
	}

	@Override
	public User registerNew(final CreateNewWorkerRequest dto, boolean notify) throws Exception {
		return registerNew(
			convertToUserDTO(dto),
			null,
			dto.getCompanyName(),
			convertToAddressDTO(dto),
			convertToProfileDTO(dto),
			false,
			notify);
	}

	private ProfileDTO convertToProfileDTO(final CreateNewWorkerRequest dto) {
		final ProfileDTO profileDTO = new ProfileDTO();
		profileDTO.setCountry(dto.getCountry());
		profileDTO.setState(dto.getState());
		profileDTO.setCity(dto.getCity());
		profileDTO.setPostalCode(dto.getPostalCode());
		profileDTO.setJobTitle(dto.getJobTitle());
		return profileDTO;
	}

	private AddressDTO convertToAddressDTO(final CreateNewWorkerRequest dto) {
		final AddressDTO addressDTO = new AddressDTO();
		addressDTO.setCountry(dto.getCountry());
		addressDTO.setAddress1(dto.getAddress1());
		addressDTO.setAddress2(dto.getAddress2());
		addressDTO.setCity(dto.getCity());
		addressDTO.setPostalCode(dto.getPostalCode());
		addressDTO.setState(dto.getState());
		addressDTO.setLongitude(dto.getLongitude());
		addressDTO.setLatitude(dto.getLatitude());
		return addressDTO;
	}

	private UserDTO convertToUserDTO(final CreateNewWorkerRequest dto) {
		final UserDTO userDTO = new UserDTO();
		userDTO.setEmail(dto.getEmail());
		userDTO.setSecondaryEmail(dto.getSecondaryEmail());
		userDTO.setPassword(dto.getPassword());
		userDTO.setRecruitingCampaignId(dto.getRecruitingCampaignId());
		userDTO.setFirstName(dto.getFirstName());
		userDTO.setLastName(dto.getLastName());
		userDTO.setOperatingAsIndividualFlag(true);
		return userDTO;
	}

	@Override
	public User registerNew(
		UserDTO userDTO,
		Long invitationId,
		String companyName,
		AddressDTO addressDTO,
		ProfileDTO profileDTO,
		boolean isBuyer) throws Exception {

		return registerNew(userDTO, invitationId, companyName, addressDTO, profileDTO, isBuyer, true);
	}
	@Override
	public User registerNew(
			UserDTO userDTO,
			Long invitationId,
			String companyName,
			AddressDTO addressDTO,
			ProfileDTO profileDTO,
			boolean isBuyer,
			boolean notifyUser) throws Exception {

		Assert.hasText(userDTO.getPassword());
		Assert.state(userDTO.getPassword().length() >= Constants.PASSWORD_MIN_LENGTH, "Password has to be at least 6 characters long.");

		boolean blankCompany = StringUtils.isBlank(companyName);
		User user = userService.findUserByEmail(userDTO.getEmail());

		if (user != null) {
			logger.error(String.format("[registration] user already registered with email %s", userDTO.getEmail()));
			BindException bindException = new BindException(userDTO, "user");
			bindException.reject("register.emailuseerror", new Object[]{userDTO.getEmail()}, null);
			throw bindException;
		}

		/*
		 * All the users that register are approved unless they change something
		 * in their profile that triggers approval or they opt in for lane 3.
		 */
		user = new User();
		// will eventually go away
		user.setUserStatusType(new UserStatusType(UserStatusType.APPROVED));

		BeanUtils.copyProperties(userDTO, user);
		setUserNumber(user);

		Set<RoleType> roles = new HashSet<>();
		roles.add(new RoleType(RoleType.SUPERUSER));
		roles.add(new RoleType(RoleType.CONTRACTOR));

		userRoleService.setRoles(user, roles);

		if (userDTO.getOperatingAsIndividualFlag() || blankCompany) {
			companyName = Constants.DEFAULT_COMPANY_NAME;
		}

		Company company = companyService.createCompany(companyName,
			userDTO.getOperatingAsIndividualFlag(),
			isBuyer ? Company.BUYER_CUSTOMER_TYPE : Company.RESOURCE_CUSTOMER_TYPE
		);

		if (addressDTO != null &&
			StringUtilities.all(addressDTO.getCountry(), addressDTO.getCity(), addressDTO.getPostalCode())) {
			Address address;
			addressDTO.setAddressTypeCode(AddressType.COMPANY);
			address = addressService.saveOrUpdate(addressDTO);
			company.setAddress(address);
		}

		user.setCompany(company);
		boolean shouldSetSharedWorker = profileDTO != null && !isBuyer && userDTO.getRecruitingCampaignId() == null;

		// Register that the user registered using the invitation
		if (invitationId != null) {
			Invitation invitation = invitationDAO.findInvitationById(invitationId);
			user.setInvitation(invitation);
			invitation.setInvitedUser(user);
			if (invitation.getInvitationType() == InvitationType.EXCLUSIVE) {
				user.setLane3ApprovalStatus(ApprovalStatus.OPT_OUT);
				shouldSetSharedWorker = false;
			}
		}

		userDAO.saveOrUpdate(user);
		authenticationService.createUser(user.getUuid(), user.getEmail(), userDTO.getPassword(), company.getUuid(), new UserStatusType(UserStatusType.APPROVED));
		Long userId = user.getId();
		if (shouldSetSharedWorker) {
			authenticationService.assignAclRoleToUser(userId, AclRole.ACL_SHARED_WORKER);
		}

		// First employee
		company.setCreatedBy(user);
		if (company.getOperatingAsIndividualFlag()) {
			company.setEffectiveName(StringUtilities.fullName(user.getFirstName(), user.getLastName()));
		}

		// Assign location type COMMERCIAL by default
		profileService.updateLocationTypePreferences(userId, new Long[]{LocationType.COMMERCIAL_CODE});

		// Create default profile for all new users
		Profile profile = new Profile();
		TimeZone timezone = dateTimeService.findTimeZonesByTimeZoneId(Constants.WM_TIME_ZONE);

		if (profileDTO != null) {
			profile = profileDTO.toProfile();

			if (profileDTO.getWorkPhoneInternationalCode() != null) {
				profile.setWorkPhoneInternationalCode(callingCodeDAO.findCallingCodeById(profileDTO.getWorkPhoneInternationalCode()));
			}
			if (profileDTO.getMobilePhoneInternationalCode() != null) {
				profile.setMobilePhoneInternationalCode(callingCodeDAO.findCallingCodeById(profileDTO.getMobilePhoneInternationalCode()));
			}
			if (profileDTO.getSmsPhoneInternationalCode() != null) {
				profile.setSmsPhoneInternationalCode(callingCodeDAO.findCallingCodeById(profileDTO.getSmsPhoneInternationalCode()));
			}

			profile.setUser(user);

			// Set the profile's postal code
			if (addressDTO != null && addressDTO.getLongitude() != null && addressDTO.getLatitude() != null) {
				profileService.updateProfilePostalCode(profile, profileDTO.getPostalCode(), addressDTO);
			}

			if (addressDTO != null && addressDTO.getPostalCode() != null) {
				timezone = dateTimeService.matchTimeZoneForPostalCode(addressDTO.getPostalCode(), addressDTO.getCountry(), addressDTO.getState(), addressDTO.getCity());
			} else if (profileDTO != null && profileDTO.getPostalCode() != null) {
				timezone = dateTimeService.matchTimeZoneForPostalCode(profileDTO.getPostalCode(), profileDTO.getCountry(), addressDTO.getState(), profileDTO.getCity());
			}

			profileDTO.setFindWork(!isBuyer);

		} else if (addressDTO != null && addressDTO.getPostalCode() != null) {
			timezone = dateTimeService.matchTimeZoneForPostalCode(addressDTO.getPostalCode(), addressDTO.getCountry(), addressDTO.getState(), addressDTO.getCity());
		}

		profile.setTimeZone(timezone);
		profile.setUser(user);

		user.setProfile(profile);
		if (company.getAddress() != null) {
			profile.setAddressId(company.getAddress().getId());
		}

		// only one option can be set on initial registration - invitees should be sellers
		boolean isFindWork = !isBuyer || userDTO.getRecruitingCampaignId() != null || invitationId != null;
		profile.setFindWork(isFindWork);
		profile.setManageWork(!isFindWork);
		profile.setOnboardCompleted(isBuyer);

		profileDAO.saveOrUpdate(profile);

		if (profileDTO != null && profile != null) {
			Long industryId = profileDTO.getIndustryId();
			if (industryId != null) {
				Industry industry = invariantDataService.findIndustry(industryId);
				if (industry != null) {
					industryService.makeProfileIndustryAssociation(industry, profile);
				}
			}
		}

		//Calling the summary service before assigning the roles since that could change the status to pending
		summaryService.saveUserHistorySummary(user);

		// Assign default ACL roles
		authenticationService.assignAclRolesToUser(userId, new Long[]{AclRole.ACL_ADMIN, AclRole.ACL_DEPUTY});

		// For any contractor invitations, setup the relationship
		processContractorInvitationsForUser(userId);

		/*
		 *  Register that the user was invited through a recruiting campaign
		 *  Add user to lane 2 for the recruiting campaign's company (PENDING APPROVAL)
		 */

		if (userDTO.getRecruitingCampaignId() != null) {

			RecruitingCampaign recruitingCampaign = recruitingService.findRecruitingCampaign(userDTO.getRecruitingCampaignId());
			if (recruitingCampaign == null) {
				logger.error(String.format("[registration] invalid recruiting campaign %d", userDTO.getRecruitingCampaignId()));
				BindException bindException = new BindException(userDTO, "user");
				bindException.reject("register.recruitingCampaignId",
					new Object[]{userDTO.getRecruitingCampaignId()},
					"This campaign does not exist");
				throw bindException;
			}

			if (recruitingCampaign.isPrivateCampaign()) {
				user.setLane3ApprovalStatus(ApprovalStatus.OPT_OUT);
			} else {
				authenticationService.assignAclRoleToUser(userId, AclRole.ACL_SHARED_WORKER);
			}

			user.setRecruitingCampaign(recruitingCampaign);
			laneService.addUserToCompanyLane2(userId, recruitingCampaign.getCompany().getId());
			laneService.updateLaneAssociationApprovalStatus(userId, recruitingCampaign.getCompany().getId(), ApprovalStatus.APPROVED);

			UserGroup campaignGroup = recruitingCampaign.getCompanyUserGroup();
			if (campaignGroup != null) {
				if (campaignGroup.getOpenMembership()) {
					// TODO: Micah - temporary to deal with agreements issue on recruiting campaigns.
					// This will go away once agreements are handled like other requirements.
					if (userGroupRequirementSetService.userGroupHasAgreementRequirements(campaignGroup.getId())) {
						userGroupService.removeAssociation(campaignGroup.getId(), userId);
					}
					logger.info("[inviteToGroupViaRecruitingCampaign] - SEND EVENT");
					eventRouter.sendEvents(eventFactory.buildInviteToGroupEvent(Lists.newArrayList(user.getId()), campaignGroup.getId(), campaignGroup.getCreatorId()));
				} else {
					userGroupService.applyToGroup(campaignGroup.getId(), userId);
				}
			}
		}

		removeFromBlacklist(user.getEmail());

		if(notifyUser) {
			EmailTemplate template = emailTemplateFactory.buildRegistrationConfirmUserEmailTemplate(userId);
			Calendar deliveryTime = Calendar.getInstance();
			deliveryTime.setTimeInMillis(deliveryTime.getTimeInMillis() + USER_REGISTRATION_EMAIL_DELAY);
			notificationService.sendNotification(template, deliveryTime);
		}

		userService.saveOrUpdatePersonaPreference(new PersonaPreference()
			.setUserId(userId)
			.setBuyer(isBuyer)
			.setSeller(!isBuyer));

		// Send the lead to the Sales Team
		if (isBuyer) {
			eventRouter.sendEvent(new BuyerSignUpSugarIntegrationEvent(company.getId()));
			companySignUpInfoDAO.saveOrUpdate(new CompanySignUpInfo(company.getId(), userDTO.getPlanCode()));
		}

		userChangeLogDAO.saveOrUpdate(
			new UserCreatedChangeLog(userId, authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId())
		);
		userIndexer.reindexById(userId);

		admissionService.grantAdmissionsForCompanyIdByPlanCode(
			user.getCompany().getId(),
			userDTO.getPlanCode()
		);

		planService.applyPlanConfigs(
			user.getCompany().getId(),
			userDTO.getPlanCode()
		);

		if (StringUtils.isNotBlank(userDTO.getNetworkId())) {
			networkService.addCompanyToNetworkWithRole(user.getCompany().getId(), Long.parseLong(userDTO.getNetworkId()), AclRole.ACL_NETWORK_READ);
		}

		// Track it
		if (isBuyer) {
			registerBuyerMeter.mark();
		} else {
			registerSellerMeter.mark();
		}

		return user;
	}

	@Override
	public User registerNewForCompany(UserDTO userDTO, Long companyId) throws Exception {
		// Default roles for an employee
		return registerNewForCompany(userDTO, companyId, new Long[]{AclRole.ACL_STAFF, AclRole.ACL_WORKER}, true);
	}

	@Override
	public User registerUserSimple(InvitationUserRegistrationDTO invitationDTO, boolean isBuyer) throws Exception {
		UserDTO userDTO = new UserDTO();
		ProfileDTO profileDTO = new ProfileDTO();
		copyInvitationDTOToUserDTOandProfileDTO(invitationDTO, profileDTO, userDTO);

		return registerNew(
				userDTO, invitationDTO.getInvitationId(), invitationDTO.getCompanyName(), invitationDTO, profileDTO,
				isBuyer, invitationDTO.isNotifyUser()
		);
	}

	@Override
	public User registerWorker(InvitationUserRegistrationDTO invitationDTO) throws Exception {
		UserDTO userDTO = new UserDTO();
		ProfileDTO profileDTO = new ProfileDTO();
		copyInvitationDTOToUserDTOandProfileDTO(invitationDTO, profileDTO, userDTO);

		return registerNew(
				userDTO, invitationDTO.getInvitationId(), invitationDTO.getCompanyName(), invitationDTO, profileDTO, false,
				invitationDTO.isNotifyUser()
		);
	}

	private void copyInvitationDTOToUserDTOandProfileDTO(InvitationUserRegistrationDTO invitationDTO, ProfileDTO profileDTO, UserDTO userDTO) {
		BeanUtilities.copyProperties(userDTO, invitationDTO);
		userDTO.setRecruitingCampaignId(invitationDTO.getCampaignId());
		userDTO.setWarpRequisitionId(invitationDTO.getWarpRequisitionId());
		BeanUtilities.copyProperties(profileDTO, invitationDTO);
		profileDTO.setPostalCode(invitationDTO.getPostalCode());
	}

	@Override
	public User registerNewForCompany(UserDTO userDTO, Long companyId, Long[] aclRoleIds) throws Exception {
		return registerNewForCompany(userDTO, companyId, aclRoleIds, true);
	}
	@Override
	public User registerNewForCompany(UserDTO userDTO, Long companyId, Long[] aclRoleIds, boolean emailUser) throws Exception {
		User user = userDAO.findUser(userDTO.getEmail());

		if (user != null) {
			throw new Exception("E-mail already registered!");
		}

		user = userDAO.findDeletedUsersByEmail(userDTO.getEmail());
		if (user == null) {
			user = new User();
		}

		final Set<RoleType> roles = new HashSet<>();
		roles.add(new RoleType(RoleType.EMPLOYEE));

		/*
		 * All the users that register are approved unless they change something
		 * in their profile that triggers approval or they opt in for lane 3.
		 */
		// deprecated and will go away
		user.setUserStatusType(new UserStatusType(UserStatusType.APPROVED));
		final Company company = companyDAO.get(companyId);
		user.setCompany(company);
		userRoleService.setRoles(user, roles);
		final Long id = user.getId();
		BeanUtils.copyProperties(userDTO, user);
		user.setId(id);

		setUserNumber(user);
		userDAO.saveOrUpdate(user);
		authenticationService.createUser(user.getUuid(), user.getEmail(), userDTO.getPassword(), company.getUuid(), new UserStatusType(UserStatusType.APPROVED));
		
		logger.debug("registerNewForCompany(): user " + user.getEmail() 
			+ " created in authnSvc, approved with roles " + Arrays.toString(roles.toArray()));
		// Assign location type COMMERCIAL by default
		profileService.updateLocationTypePreferences(user.getId(), new Long[]{LocationType.COMMERCIAL_CODE});

		Profile profile = profileDAO.findByUser(user.getId());
		if (profile == null) {
			// Create default profile for all new users
			profile = new Profile();
		}
		profile.setUser(user);
		user.setProfile(profile);

		profile.setTimeZone(dateTimeService.findTimeZonesByTimeZoneId(Constants.WM_TIME_ZONE));
		Long addressId = user.getCompany().getAddress() == null ? null : user.getCompany().getAddress().getId();
		profile.setAddressId(addressId);

		// new employees are always Buyer, and are Seller only if either "allow to take internal/external assignments" are checked
		profile.setManageWork(true);
		profile.setFindWork(
			ArrayUtils.contains(aclRoleIds, AclRole.ACL_SHARED_WORKER) || ArrayUtils.contains(aclRoleIds, AclRole.ACL_WORKER ) || ArrayUtils.contains(aclRoleIds, AclRole.ACL_EMPLOYEE_WORKER)
		);

		profileDAO.saveOrUpdate(profile);
		//Calling the summary service before assigning the roles since that could change the status to pending
		summaryService.saveUserHistorySummary(user);
		industryService.setIndustriesForProfile(profile.getId(), Sets.newHashSet(invariantDataService.findIndustry(Constants.WM_TIME_INDUSTRY_ID)));

		authenticationService.assignAclRolesToUser(user.getId(), aclRoleIds);

		if (authenticationService.authorizeUserByAclPermission(user.getId(), com.workmarket.domains.model.acl.Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS)) {
			authenticationService.assignAclRoleToUser(user.getId(), AclRole.ACL_DEPUTY);
		}

		logger.debug("registerNewForCompany(): ACLs assigned to user " + userDTO.getEmail());


		boolean isBuyer = CustomerType.BUYER.value().equals(company.getCustomerType()) ||
			CustomerType.MANAGED.value().equals(company.getCustomerType());

		if (ArrayUtils.contains(aclRoleIds, AclRole.ACL_SHARED_WORKER)) {
			userService.updateLane3ApprovalStatus(user.getId(), ApprovalStatus.APPROVED);
		}

		PersonaPreference personaPreference = new PersonaPreference()
			.setUserId(user.getId());

		if (ArrayUtils.contains(aclRoleIds, AclRole.ACL_EMPLOYEE_WORKER)) {
			personaPreference.setBuyer(!isBuyer)
			.setSeller(isBuyer);
		} else {
			personaPreference.setBuyer(isBuyer)
			.setSeller(!isBuyer);
		}

		userService.saveOrUpdatePersonaPreference(personaPreference);

		PasswordResetRequest request = requestService.requestPasswordReset(Constants.EMAIL_USER_ID_TRANSACTIONAL, user.getId());
		EmailTemplate template = emailTemplateFactory.buildRegistrationConfirmWithPasswordResetEmailTemplate(authenticationService
			.getCurrentUser().getId(), user.getId(), request);
		if(emailUser) {
			notificationService.sendNotification(template);
		}
		return user;
	}

	@Override
	public User registerNewInternalUser(UserDTO userDTO) throws Exception {
		Assert.isNull(userService.findUserByEmail(userDTO.getEmail()), "E-mail already registered!");

		User user = new User();
		final Company company = companyService.findCompanyById(Constants.WM_COMPANY_ID);
		// deprecated, but ok, it's a create.
		user.setCompany(company);
		// ok for now, but will go away
		user.setUserStatusType(new UserStatusType(UserStatusType.APPROVED));

		Set<RoleType> roles = new HashSet<>();
		roles.add(new RoleType(RoleType.INTERNAL));
		userRoleService.setRoles(user, roles);
		BeanUtils.copyProperties(userDTO, user);
		setUserNumber(user);
		userDAO.saveOrUpdate(user);
		authenticationService.createUser(user.getUuid(), user.getEmail(), userDTO.getPassword(), company.getUuid(), new UserStatusType(UserStatusType.APPROVED));

		// Create default profile for all new users

		Profile profile = new Profile();

		profile.setTimeZone(dateTimeService.findTimeZonesByTimeZoneId(Constants.WM_TIME_ZONE));
		profile.setProfilePostalCode(postalCodeDAO.findByPostalCodeCountryStateCity(Constants.WM_POSTAL_CODE, "USA", user.getCompany().getAddress().getState(), user.getCompany().getAddress().getCity()));
		profile.setUser(user);
		profile.setAddressId(user.getCompany().getAddress().getId());
		profileDAO.saveOrUpdate(profile);
		industryService.setIndustriesForProfile(profile.getId(), Sets.newHashSet(invariantDataService.findIndustry(Constants.WM_TIME_INDUSTRY_ID)));
		user.setProfile(profile);

		// Add Administrator ACLRole by Default
		authenticationService.assignAclRoleToUser(user.getId(), authenticationService.findSystemRoleByName(Constants.ADMINISTRATOR_ROLE).getId());

		return user;
	}

	/*
		 * TODO: Lots of business logic is missing here, we'll address this once we
		 * have real use cases.(non-Javadoc) For now, data editions on the user will
		 * be handled by client services manually.
		 *
		 * @see com.workmarket.service.business.RegistrationService#
		 * registerExistingUserForCompany(java.lang.Long, java.lang.Long,
		 * java.lang.Long[])
		 */
	@Override
	public User registerExistingUserForCompany(Long userId, Long newCompanyId, Long[] aclRoleIds) throws Exception {
		Assert.notNull(newCompanyId);
		Assert.notNull(userId);

		Company newCompany = companyService.findCompanyById(newCompanyId);
		User user = userService.getUser(userId);

		Assert.notNull(newCompany);
		Assert.notNull(user);

		if (userRoleService.isInternalUser(user)) {
			throw new Exception("User " + user.getId() + " is an internal user, company relationship can't be modified");
		}
		if (user.getCompany().getId().equals(newCompanyId)) {
			throw new Exception("User " + user.getId() + " already belongs to Company " + newCompanyId);
		}

		// remove all user acl roles from old company
		final List<User> adminsForOldCompany = authenticationService.findAllUsersByACLRoleAndCompany(user.getCompany().getId(), AclRole.ACL_ADMIN);
		final List<UserAclRoleAssociation> associationList = userRoleService.findAllRolesByUser(userId, true);
		for (UserAclRoleAssociation association : associationList) {
			final Long roleId = association.getRole().getId();
			if (adminsForOldCompany.size() == 1 && roleId.equals(AclRole.ACL_ADMIN)) {
				logger.warn("Only admin role of User [" + user.getId() + "] for company [" + user.getCompany().getId() + "] will be removed, and thus result in orphaned company.");
			}
			if (roleId.equals(AclRole.ACL_SHARED_WORKER)) {
				user.setLane3ApprovalStatus(ApprovalStatus.OPT_OUT);
			}
			userRoleService.removeAclRoleAssociation(user, association);
			userChangeLogDAO.saveOrUpdate(new UserAclRoleRemovedChangeLog(userId, authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId(), association.getRole()));
		}
		// remove any association it may exist with the new company
		laneService.removeUserFromCompanyLane(userId, newCompanyId);

		// Set the first employee
		if (!newCompany.hasUsers()) {
			newCompany.setCreatedBy(user);
			if (newCompany.getOperatingAsIndividualFlag()) {
				newCompany.setEffectiveName(StringUtilities.fullName(user.getFirstName(), user.getLastName()));
			}
		}

		final String userUuid = user.getUuid();
		final String newCompanyUuid = newCompany.getUuid();
		final String oldCompanyUuid = user.getCompany().getUuid();
		authClient.changeUserCompany(userUuid, oldCompanyUuid, newCompanyUuid, trialCommon.getApiContext())
			.toBlocking().single();
		user.setCompany(newCompany);

		Set<RoleType> roles = new HashSet<>();
		roles.add(new RoleType(RoleType.EMPLOYEE));
		userRoleService.setRoles(user, roles);

		authenticationService.updateUserAclRoles(userId, Lists.newArrayList(aclRoleIds));

		List<User> admins = authenticationService.findAllUsersByACLRoleAndCompany(newCompany.getId(), AclRole.ACL_ADMIN);

		if (admins.isEmpty()) {
			authenticationService.assignAclRoleToUser(userId, AclRole.ACL_ADMIN);
		}
		authenticationService.refreshSessionForUser(userId);

		return user;
	}

	@Override
	public User registerNewApiUserForCompany(long companyId) {
		final Company company = companyService.findCompanyById(companyId);

		final String companyUuidEmail = String.format(
				Constants.DEFAULT_API_USER_EMAIL_FORMAT_USING_COMPANY_UUID, company.getUuid());
		final User userByUuidEmail = userService.findUserByEmail(companyUuidEmail);
		if (userByUuidEmail != null) {
			return userByUuidEmail;
		}

		final String companyIdEmail = String.format(
				Constants.DEFAULT_API_USER_EMAIL_FORMAT_USING_COMPANY_ID, company.getId());
		final User userByIdEmail = userService.findUserByEmail(companyIdEmail);
		if (userByIdEmail != null) {
			return userByIdEmail;
		}

		return reallyRegisterNewApiUserForCompany(company, companyUuidEmail);
	}

	private User reallyRegisterNewApiUserForCompany(final Company company, final String companyUuidEmail) {
		final User user = new User();
		user.setFirstName(Constants.DEFAULT_API_USER_FIRST_NAME);
		user.setLastName(Constants.DEFAULT_API_USER_LAST_NAME);
		// deprecated but ok
		user.setEmail(companyUuidEmail);
		final String password = RandomUtilities.generateAlphaNumericString(15);
		// while deprecated, these are still ok
		user.setCompany(company);
		user.setApiEnabled(true);
		setUserNumber(user);
		user.setUserStatusType(new UserStatusType(UserStatusType.APPROVED));

		userDAO.saveOrUpdate(user);
		authenticationService.createUser(user.getUuid(), user.getEmail(), password, company.getUuid(),
				new UserStatusType(UserStatusType.APPROVED));
		authenticationService.setEmailConfirmed(user, true);
		authenticationService.setEmailConfirmedOn(user, Calendar.getInstance());
		userDAO.saveOrUpdate(user);

		final Address address = company.getAddress();

		final Profile profile = new Profile();
		profile.setUser(user);
		if (address != null && StringUtils.isNotEmpty(address.getPostalCode())) {
			profile.setTimeZone(dateTimeService.matchTimeZoneForPostalCode(
					address.getPostalCode(), address.getCountry().getId(), address.getState().getShortName(), address.getCity()));
			profile.setAddressId(address.getId());
		} else {
			profile.setTimeZone(timeZoneDAO.findTimeZonesByTimeZoneId(Constants.WM_TIME_ZONE));
		}
		profileDAO.saveOrUpdate(profile);
		user.setProfile(profile);

		try {
			authenticationService.assignAclRoleToUser(user.getId(), authenticationService.findSystemRoleByName(
					Constants.ADMINISTRATOR_ROLE).getId());
		} catch (final InvalidAclRoleException e) {
			logger.error("Error adding acl role to user ", e);
		}

		return user;
	}

	@Override
	public Optional<User> getApiUserByUserId(Long userId) {
		User user = checkNotNull(userService.getUser(checkNotNull(userId)));
		final String emailViaCompanyUuid = String.format(Constants.DEFAULT_API_USER_EMAIL_FORMAT_USING_COMPANY_UUID, user.getCompany().getUuid());
		final User apiUser = userService.findUserByEmail(emailViaCompanyUuid);

		if (apiUser != null) {
			return Optional.of(apiUser);
		}

		final String emailViaCompanyId = String.format(Constants.DEFAULT_API_USER_EMAIL_FORMAT_USING_COMPANY_ID, user.getCompany().getId());
		return Optional.fromNullable(userService.findUserByEmail(emailViaCompanyId));
	}

	@Override
	public void sendRemindConfirmationEmail(Long id) throws Exception {
		Assert.notNull(id);
		User found = userDAO.get(id);

		if (found == null) {
			throw new Exception("User " + id + " Not Found");
		}
		EmailTemplate template = emailTemplateFactory.buildRegistrationRemindUserEmailTemplate(id);
		notificationService.sendNotification(template);
	}

	@Override
	public String generateConfirmationURL(Long id) {
		Assert.notNull(id);
		User found = userDAO.get(id);

		// from the email template -
		// ${baseurl}/user/confirm_account/$!{toUser.encryptedId}
		return baseUrl + "/user/confirm_account/" + found.getEncryptedId();
	}

	@Override
	public String generateForgotPasswordURL(Long id) {
		Assert.notNull(id);
		User found = userDAO.get(id);

		Assert.state(!userRoleService.isInternalUser(found), "Unauthorized to reset password for internal user.");

		// from the email template
		// ${baseurl}/user/reset_password/$!{template.request.encryptedId}
		StringBuilder url = new StringBuilder(baseUrl);

		PasswordResetRequest request = requestService.findLatestSentPasswordResetRequestByInvitedUser(found.getId());

		if (request == null || request.isExpired()) {
			request = requestService.requestPasswordReset(Constants.EMAIL_USER_ID_TRANSACTIONAL, found.getId());
		}

		url.append("/user/reset_password/");
		url.append(request.getEncryptedId());

		return url.toString();

	}


	@Override
	public void sendConfirmationWithPasswordResetEmail(Long fromUserId, Long toUserId) throws Exception {
		Assert.notNull(fromUserId);
		Assert.notNull(toUserId);
		User toUser = userDAO.get(toUserId);
		User fromUser = userDAO.get(fromUserId);

		if (toUser == null)
			throw new Exception("User " + toUserId + " Not Found");

		if (authenticationService.getEmailConfirmed(toUser))
			throw new Exception("Email " + toUser.getEmail() + " is confirmed.");

		PasswordResetRequest request = requestService.findLatestSentPasswordResetRequestByInvitedUser(toUser.getId());

		if (request == null || request.isExpired()) {
			request = requestService.requestPasswordReset(Constants.EMAIL_USER_ID_TRANSACTIONAL, toUser.getId());
		}

		EmailTemplate template = emailTemplateFactory.buildRegistrationConfirmWithPasswordResetEmailTemplate(fromUser.getId(), toUser.getId(), request);
		notificationService.sendNotification(template);
	}

	@Override
	public void sendRemindConfirmationWithPasswordResetEmail(Long id) throws Exception {
		Assert.notNull(id);
		User found = userDAO.get(id);

		if (found == null)
			throw new Exception("User " + id + " Not Found");

		if (authenticationService.getEmailConfirmed(found))
			throw new Exception("Email " + found.getEmail() + " is confirmed.");

		PasswordResetRequest request = requestService.findLatestSentPasswordResetRequestByInvitedUser(found.getId());

		if (request == null || request.isExpired()) {
			request = requestService.requestPasswordReset(Constants.EMAIL_USER_ID_TRANSACTIONAL, found.getId());
		}

		EmailTemplate template = emailTemplateFactory.buildRegistrationRemindConfirmationWithPasswordResetEmailTemplate(authenticationService.getCurrentUser().getId(), found.getId(), request);
		notificationService.sendNotification(template);
	}

	@Override
	public User confirmAccount(Long id) {
		return confirmAccount(id, true);
	}

	@Override
	public User confirmAccount(Long id, boolean sendWelcomeEmail) {

		final User user = userService.findUserById(id);

		if (user != null) {

			if (authenticationService.isSuspended(user)) {
				return null;
			}

			User userWithChangedEmailAddress = userService.findUserByEmail(user.getChangedEmail());

			// Check whether someone else has the user's changedEmail
			if (userWithChangedEmailAddress != null) {
				user.setChangedEmail(null);
				return null;
			}

			// the email address has been changed
			if (user.getChangedEmail() != null) {
				final String newUsername = user.getChangedEmail();
				 authClient.changeUsername(user.getUuid(), newUsername, trialCommon.getApiContext())
					 .toBlocking().single();
				user.setEmail(user.getChangedEmail());
				user.setChangedEmail(null);
			}

			// Just do it when is not really confirmed, otherwise we'll send duplicate emails.
			if (!authenticationService.getEmailConfirmed(user)) {
				authenticationService.setEmailConfirmed(user, true);
				authenticationService.setEmailConfirmedOn(user, Calendar.getInstance());
				userNotificationService.onConfirmAccount(user, sendWelcomeEmail);
			}

			authenticationService.refreshSessionForUser(user.getId());
		}
		return user;
	}

	@Override
	public User confirmAndApproveAccount(Long id) {

		User user = confirmAccount(id);
		if (user != null) {
			authenticationService.approveUser(id);
		}
		return user;
	}

	@Override
	public void sendForgotPasswordEmail(String emailAddress) {

		User found = userDAO.findUser(emailAddress);

		if (found == null) {
			throw new BadCredentialsException("Invalid username");
		}
		Calendar expiration = DateUtilities.getCalendarNow();
		expiration.add(Calendar.HOUR_OF_DAY, 24);

		PasswordResetRequest request = requestService.requestPasswordReset(Constants.EMAIL_USER_ID_TRANSACTIONAL, found.getId(), expiration);

		EmailTemplate template = emailTemplateFactory.buildRegistrationRemindPasswordEmailTemplate(found.getId(), request);
		notificationService.sendNotification(template);
	}

	@Override
	public User validatePasswordResetRequest(String encryptedId) {
		PasswordResetRequest request = requestService.findPasswordResetRequest(encryptedId);
		if (request == null || request.isExpired() || !request.getRequestStatusType().getCode().equals(RequestStatusType.SENT)) {
			return null;
		}
		return request.getInvitedUser();
	}

	@Override
	public MessageBundle acceptInvitation(String encryptedId) {
		Long id = encryptionService.decryptId(encryptedId);
		Invitation invitation = invitationService.findInvitationById(id);
		MessageBundle bundle = messageHelper.newBundle();
		if (invitation == null) {
			bundle.addError(messageHelper.getMessage("invitations.accept.failed_not_found"));
		} else if (invitation.getInvitedUser() == null) {
			messageHelper.getMessage("invitations.accept.failed_no_user", invitation.getCompany().getEffectiveName());
		} else if (!invitation.getInvitedUser().getId().equals(authenticationService.getCurrentUserId())) {
			bundle.addError(
				messageHelper.getMessage("invitations.accept.failed_not_target",
					invitation.getCompany().getEffectiveName(),
					invitation.getInvitedUser().getFullName()));
		} else if (invitation.getInvitationStatusType().equals(INVITATION_INSYSTEM)) {
			bundle.addError(messageHelper.getMessage("invitations.accept.failed_already_accepted", invitation.getCompany().getEffectiveName()));
		} else {
			laneService.addUserToCompanyLane2(invitation.getInvitedUser().getId(), invitation.getCompany().getId());
			invitation.setInvitationStatusType(INVITATION_INSYSTEM);
			invitationDAO.saveOrUpdate(invitation);
			bundle.addSuccess(messageHelper.getMessage("invitations.accept.success", invitation.getCompany().getEffectiveName()));
		}
		return bundle;
	}

	@Override
	public MessageBundle declineInvitation(String encryptedId) {
		Long id = encryptionService.decryptId(encryptedId);
		Invitation invitation = invitationService.findInvitationById(id);
		MessageBundle bundle = messageHelper.newBundle();
		if (invitation == null) {
			bundle.addError(messageHelper.getMessage("invitations.decline.failed_not_found"));
		} else if (invitation.getInvitedUser() == null) {
			messageHelper.getMessage("invitations.decline.failed_no_user", invitation.getCompany().getEffectiveName());
		} else if (!invitation.getInvitedUser().getId().equals(authenticationService.getCurrentUserId())) {
			bundle.addError(
				messageHelper.getMessage("invitations.decline.failed_not_target",
					invitation.getCompany().getEffectiveName(),
					invitation.getInvitedUser().getFullName()));
		} else if (invitation.getInvitationStatusType().equals(INVITATION_DECLINED)) {
			bundle.addError(messageHelper.getMessage("invitations.decline.failed_already_declined", invitation.getCompany().getEffectiveName()));
		} else {
			if (invitation.getInvitationStatusType().equals(INVITATION_INSYSTEM)) {
				laneService.removeUserFromCompanyLane(invitation.getInvitedUser().getId(), invitation.getCompany().getId());
			}
			invitation.setInvitationStatusType(INVITATION_DECLINED);
			invitationDAO.saveOrUpdate(invitation);
			bundle.addSuccess(messageHelper.getMessage("invitations.decline.success", invitation.getCompany().getEffectiveName()));
		}
		return bundle;
	}

	@Override
	public List<String[]> getUsersFromCSVUpload(String fileUUID) throws IOException, HostServiceException {
		File file = remoteFileAdapter.getFile(RemoteFileType.TMP, fileUUID);
		CSVReader reader = new CSVReader(new FileReader(file));
		List<String[]> entries = reader.readAll();
		return entries;
	}

	@Override
	public Invitation inviteUser(InvitationDTO invitationDTO, Long[] groupIds) {

		User inviter = userDAO.get(invitationDTO.getInviterUserId());
		boolean isCampaignInvitaiton = invitationDTO.getRecruitingCampaignId() != null;
		Assert.notNull(inviter);

		Long companyId = inviter.getCompany().getId();

		Integer invitationsSent = invitationDAO.countInvitationsByCompanyStatusAndDate(companyId, InvitationStatusType.SENT, DateUtilities.getMidnightToday());
		Assert.state(invitationsSent < Constants.INVITATIONS_PER_DAY_PER_COMPANY_LIMIT,
			"Company " + companyId + " have reached the invite limit for today of " + Constants.INVITATIONS_PER_DAY_PER_COMPANY_LIMIT
				+ " invitations ");

		/*
		 * Denormalizing to avoid joins when seeing company wide invitations.
		 * Also is possible that the inviter user may change companies.
		 */
		Invitation invitation = new Invitation(inviter.getCompany());
		BeanUtilities.copyProperties(invitation, invitationDTO);
		invitation.setInvitingUser(inviter);
		invitation.setInvitationDate(Calendar.getInstance());
		invitation.setInvitationType(invitationDTO.getInvitationType());
		if (isCampaignInvitaiton) {
			invitation.setRecruitingCampaign(recruitingCampaignDAO.findBy("id", invitationDTO.getRecruitingCampaignId()));
		}
		if (invitationDTO.getCompanyLogoAssetId() != null) {
			invitation.setCompanyLogo(assetService.get(invitationDTO.getCompanyLogoAssetId()));
		}

		String emailAddress = invitationDTO.getEmail();
		User invitedUser = userDAO.findUser(emailAddress);
		invitation.setInvitedUser(invitedUser);
		invitation.setInvitationStatusType(INVITATION_SENT);

		invitationDAO.saveOrUpdate(invitation);

		EmailTemplate template = getInvitationEmailTemplate(invitationDTO, inviter, invitation, emailAddress, invitedUser);
		notificationService.sendNotification(template);

		// Default group associations
		// If user is already in the system, funnel em into the group
		// Otherwise associate request w/invitation to pick up when they
		// register

		if (!isCampaignInvitaiton && groupIds != null) {
			for (Long groupId : groupIds) {
				// Check if the group belongs to the company
				if (userGroupService.isCompanyUserGroup(companyId, groupId)) {
					if (invitedUser != null) {
						userGroupService.applyToGroup(groupId, invitedUser.getId());
					} else {
						requestService.extendInvitationToGroup(inviter.getId(), invitation.getId(), groupId);
					}
				}
			}
		}

		return invitation;
	}

	private EmailTemplate getInvitationEmailTemplate(InvitationDTO dto, User inviter, Invitation invitation, String email, User invitee) {
		EmailTemplate template;
		if (invitee != null) {
			template = emailTemplateFactory.buildRegistrationExistingInviteUserEmailTemplate(inviter.getId(), email, invitation);
		} else if (dto.getRecruitingCampaignId() == null) {
			template = emailTemplateFactory.buildRegistrationInviteUserEmailTemplate(inviter.getId(), email, invitation);
		} else {
			template =
				emailTemplateFactory.buildRecruitingCampaignInvitationEmailTemplate(
					inviter.getId(),
					invitation.getEmail(),
					invitation.getRecruitingCampaign(),
					inviter,
					invitation.getFirstName()
				);
		}
		return template;
	}

	@Override
	public boolean invitationExists(Long userId, String emailAddress) {
		Assert.notNull(userId);
		Assert.hasText(emailAddress);
		User user = userDAO.get(userId);
		Invitation invitation = invitationDAO.findInvitationByCompany(user.getCompany().getId(), emailAddress);
		return (invitation != null);
	}

	@Override
	public String previewInvitation(InvitationDTO invitationDTO) {
		Invitation invitation = new Invitation();
		BeanUtils.copyProperties(invitationDTO, invitation);

		// Set dummy data for the rendering
		invitation.setId(0L);
		invitation.setInvitingUser(userDAO.get(authenticationService.getCurrentUser().getId()));

		EmailTemplate template = emailTemplateFactory.buildRegistrationInviteUserEmailTemplate(invitation.getInvitingUser().getId(), null, invitation);
		return templateService.render(template);
	}

	@Override
	public void remindInvitations(List<Long> invitationIds) {
		for (Long invitationId : invitationIds) {
			Invitation invitation = invitationDAO.get(invitationId);

			if (invitation.isReminderBlocked()) {
				continue;
			}
			EmailTemplate template =
				invitation.getInvitedUser() == null
					? emailTemplateFactory.buildRegistrationRemindInviteUserEmailTemplate(
						authenticationService.getCurrentUser().getId(), invitation.getEmail(), invitation)
					: emailTemplateFactory.buildRegistrationRemindInviteExistingUserEmailTemplate(
						authenticationService.getCurrentUser().getId(), invitation.getEmail(), invitation);
			notificationService.sendNotification(template);
			invitation.setLastReminderDate(DateUtilities.getCalendarNow());
		}
	}

	@Override
	public void updateUserStatusToDeleted(final String email) {
		final User user = userDAO.findUser(email);
		user.setUserStatusType(new UserStatusType(UserStatusType.DELETED));
		userDAO.saveOrUpdate(user);
		authenticationService.deleteUser(user.getUuid(), user.getEmail(), webRequestContextProvider.getRequestContext());
	}

	@Override
	public void processContractorInvitationsForUser(Long userId) {
		User user = userDAO.get(userId);
		Assert.notNull(user);

		List<Invitation> invitations = invitationDAO.findInvitationsByStatus(user.getEmail(), INVITATION_SENT);

		for (Invitation invitation : invitations) {
			// process lane associations
			laneService.addUserToCompanyLane2(user.getId(), invitation.getCompany().getId());
			invitation.setInvitationStatusType(new InvitationStatusType(InvitationStatusType.REGISTERED));
			invitation.setInvitedUser(user);
		}
	}

	@Override
	public InvitationPagination findInvitations(Long invitingUserId, InvitationPagination pagination) {

		User invitingUser = userDAO.get(invitingUserId);

		// A manager or admin at company should be able to see all users who are
		// sending invitations
		/*
		 * Is the ability of view company's invitations a type of permission
		 * that could be set to any other role?? Or should we just assume that's
		 * an inherited permission to only admins and managers?
		 */
		if (authenticationService.userHasAclRole(invitingUserId, AclRole.ACL_MANAGER) || authenticationService.userHasAclRole(invitingUserId, AclRole.ACL_ADMIN)) {

			pagination = invitationDAO.findInvitationsByCompany(invitingUser.getCompany().getId(), pagination);

		} else {
			pagination = invitationDAO.findInvitations(invitingUserId, pagination);
		}

		return pagination;
	}

	private void setUserNumber(User user) {
		user.setUserNumber(randomNumberGenerator.generateUniqueNumber());
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
