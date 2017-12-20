package com.workmarket.service.business;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Education;
import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.PhoneNumber;
import com.google.code.linkedinapi.schema.PhoneType;
import com.google.code.linkedinapi.schema.Position;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.AddressDAO;
import com.workmarket.dao.LanguageDAO;
import com.workmarket.dao.LocationTypeDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.UserLocationTypeAssociationDAO;
import com.workmarket.dao.UserProfileModificationDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.asset.CompanyAssetAssociationDAO;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.dao.asset.UserLinkAssociationDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.industry.ProfileIndustryAssociationDAO;
import com.workmarket.dao.oauth.OAuthTokenDAO;
import com.workmarket.dao.postalcode.PostalCodeDAO;
import com.workmarket.dao.profile.ProfileActionTypeDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.dao.profile.ProfileLanguageDAO;
import com.workmarket.dao.profile.ProfilePhoneAssociationDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.domains.model.*;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.UserLinkAssociation;
import com.workmarket.domains.model.asset.type.CompanyAssetAssociationType;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.domains.model.company.CompanyStatusType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.domains.model.oauth.OAuthToken;
import com.workmarket.domains.model.oauth.OAuthTokenProviderType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.model.qualification.UserToQualification;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.onboarding.model.OnboardCompleteValidator;
import com.workmarket.domains.onboarding.model.OnboardProfilePropertyMap;
import com.workmarket.domains.onboarding.model.OnboardProfilePropertyMapCreator;
import com.workmarket.domains.onboarding.model.OnboardingIndustryDTO;
import com.workmarket.domains.onboarding.model.OnboardingSkillDTO;
import com.workmarket.domains.onboarding.model.WorkerOnboardingDTO;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.HideBackgroundChecksRope;
import com.workmarket.domains.velvetrope.rope.HideDrugTestsRope;
import com.workmarket.domains.velvetrope.rope.HideProfileInsuranceRope;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.dto.AddressDTO;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.search.qualification.MutateResponse;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationBuilder;
import com.workmarket.search.qualification.QualificationClient;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.service.business.LinkedInServiceImpl.LinkedInImportFailed;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.CompanyCommentDTO;
import com.workmarket.service.business.dto.CompanyDTO;
import com.workmarket.service.business.dto.EducationHistoryDTO;
import com.workmarket.service.business.dto.EmploymentHistoryDTO;
import com.workmarket.service.business.dto.LinkedInProfileDTO;
import com.workmarket.service.business.dto.ManageMyWorkMarketDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.ProfileLanguageDTO;
import com.workmarket.service.business.dto.SkillDTO;
import com.workmarket.service.business.dto.SpecialtyDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.UserProfileCompletenessDTO;
import com.workmarket.service.business.integration.mbo.MboProfileDAO;
import com.workmarket.service.business.qualification.QualificationAssociationService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.GeocodingService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.ProjectionUtilities;
import com.workmarket.utility.SerializationUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.RestCode;
import com.workmarket.web.exceptions.BadRequestException;
import com.workmarket.web.exceptions.ValidationException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import rx.functions.Action1;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class ProfileServiceImpl implements ProfileService {

	private static final Log logger = LogFactory.getLog(ProfileServiceImpl.class);

	private static final String TEMP_FILENAME = "company_logo_%s";
	public static final String PROFILE = RedisConfig.PROFILE;
	public static final long TWO_WEEKS_IN_SECONDS = TimeUnit.DAYS.toSeconds(14);

	private static final String ADD_QUALIFICATION_FEATURE = "AddUserNonJobTitleQualification";

	@Autowired private UserDAO userDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private ProfileDAO profileDAO;
	@Autowired private LanguageDAO languageDAO;
	@Autowired private ProfileLanguageDAO profileLanguageDAO;
	@Autowired private LocationTypeDAO locationTypeDAO;
	@Autowired private UserLocationTypeAssociationDAO userLocationTypeAssociationDAO;
	@Autowired private AddressDAO addressDAO;
	@Autowired private ProfileActionTypeDAO profileActionTypeDAO;
	@Autowired private UserProfileModificationDAO userProfileModificationDAO;
	@Autowired private UserAssetAssociationDAO userAssetAssociationDAO;
	@Autowired private UserLinkAssociationDAO userLinkAssociationDAO;
	@Autowired private CompanyAssetAssociationDAO companyAssetAssociationDAO;
	@Autowired private PostalCodeDAO postalCodeDAO;
	@Autowired private ProfilePhoneAssociationDAO profilePhoneAssociationDAO;
	@Autowired private OAuthTokenDAO oAuthTokenDAO;
	@Autowired private IndustryDAO industryDAO;
	@Autowired private DateTimeService dateTimeService;
	@Autowired private UserGroupValidationService userGroupValidationService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private UserService userService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private GeocodingService geocodingService;
	@Autowired private LinkedInService linkedInService;
	@Autowired private AddressService addressService;
	@Autowired private DirectoryService directoryService;
	@Autowired private CompanyService companyService;
	@Autowired private UserIndexer userIndexer;
	@Autowired private TaxService taxService;
	@Autowired private BankingService bankingService;
	@Autowired private CommentService commentService;
	@Autowired private ProjectService projectService;
	@Autowired private MboProfileDAO mboProfileDAO;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private OnboardCompleteValidator onboardCompleteValidator;
	@Autowired private EventRouter eventRouter;
	@Autowired private IndustryService industryService;
	@Autowired private ProfileIndustryAssociationDAO profileIndustryAssociationDAO;
	@Autowired private UserRoleService userRoleService;
	@Autowired private SkillService skillService;
	@Autowired private SpecialtyService specialtyService;
	@Autowired private QualificationClient qualificationClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private QualificationAssociationService qualificationAssociationService;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private LaneService laneService;
	@Autowired private EmailTemplateFactory emailTemplateFactory;
	@Autowired private NotificationService notificationService;

	@Autowired @Qualifier("hideDrugTestsDoorman") Doorman hideDrugTestsDoorman;
	@Autowired @Qualifier("hideBackgroundChecksDoorman") Doorman hideBackgroundChecksDoorman;
	@Autowired @Qualifier("hideProfileInsuranceDoorman") Doorman hideProfileInsuranceDoorman;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.service.business.ProfileService#editCompany(java.lang.Long, com.workmarket.service.business.dto.CompanyDTO)
	 */
	@Override
	public Company saveOrUpdateCompany(Long userId, CompanyDTO companyDTO) {
		Assert.notNull(userId);

		User user = userDAO.get(userId);
		Company company = user.getCompany();

		if (StringUtils.isNotBlank(companyDTO.getName()) && !StringUtilities.same(companyDTO.getName(), company.getName())) {
			company.setNameOldValue(company.getName());
			registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.COMPANY_NAME));
		}

		if (StringUtils.isNotBlank(companyDTO.getOverview()) && !StringUtilities.same(companyDTO.getOverview(), company.getOverview())) {
			company.setOverviewOldValue(company.getOverview());
			registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.COMPANY_OVERVIEW));
		}

		if (StringUtils.isNotBlank(companyDTO.getWebsite()) && !StringUtilities.same(companyDTO.getWebsite(), company.getWebsite())) {
			company.setWebsiteOldValue(company.getWebsite());
			registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.COMPANY_WEBSITE));
		}

		BeanUtils.copyProperties(companyDTO, company, new String[]{"effectiveName"});

		if (!companyDTO.getOperatingAsIndividualFlag()) {
			company.setEffectiveName(companyDTO.getName());
		}

		authenticationService.refreshSessionForCompany(company.getId());

		return company;
	}

	@Override
	public Company saveOrUpdateCompany(CompanyDTO companyDTO) {
		Company company;

		if (companyDTO.getCompanyId() != null) {
			company = findCompanyById(companyDTO.getCompanyId());
		} else {
			company = companyService.createCompany(companyDTO.getName(), true, Company.UKNOWN_CUSTOMER_TYPE);
		}

		BeanUtils.copyProperties(companyDTO, company, new String[]{"effectiveName"});

		if (!companyDTO.getOperatingAsIndividualFlag()) {
			company.setEffectiveName(companyDTO.getName());
		}

		companyDAO.saveOrUpdate(company);

		authenticationService.refreshSessionForCompany(company.getId());

		return company;
	}

	@Override
	public Company saveOrUpdateCompany(Company company) {
		Assert.notNull(company);

		companyDAO.saveOrUpdate(company);

		authenticationService.refreshSessionForCompany(company.getId());

		return company;
	}

	@Override
	public Company updateManageMyWorkMarket(Long companyId, ManageMyWorkMarketDTO manageMyWorkMarketDTO) {
		Company company = companyDAO.get(companyId);
		Assert.notNull(company);

		if (company.getManageMyWorkMarket() == null) {
			company.setManageMyWorkMarket(new ManageMyWorkMarket());
		}

		boolean projectBudgetReset = company.getManageMyWorkMarket().getBudgetEnabledFlag() && (!manageMyWorkMarketDTO.getBudgetEnabledFlag());

		//If statements are enabled we shouldn't override the payment configuration
		if (company.hasStatementsEnabled()) {
			BeanUtilities.copyProperties(company.getManageMyWorkMarket(), manageMyWorkMarketDTO, new String[]{"paymentTermsDays", "paymentTermsEnabled"});
		} else {
			BeanUtilities.copyProperties(company.getManageMyWorkMarket(), manageMyWorkMarketDTO);
		}

		company.setCustomSignatureLine(manageMyWorkMarketDTO.getCustomSignatureLine());

		if (projectBudgetReset) {
			projectService.resetAllProjectBudget(companyId);
		}

		return company;
	}

	@Override
	@CacheEvict(
			value = PROFILE,
			key = "#root.target.PROFILE + #userId"
	)
	public ProfileLanguage saveOrUpdateProfileLanguage(Long userId, ProfileLanguageDTO profileLanguageDTO) {

		User user = userDAO.get(userId);

		Profile profile = profileDAO.findByUser(userId);

		if (profile == null) {

			profile = new Profile();
			profile.setUser(user);

			profile.setTimeZone(dateTimeService.matchTimeZoneForUser(user.getId()));

		} else {
			List<ProfileLanguage> profileLanguages = profileLanguageDAO.findAllProfileLanguageByProfileId(profile.getId());
			if (isNotEmpty(profileLanguages)) {
				for (ProfileLanguage profileLanguage : profileLanguages) {
					Assert.state(!profileLanguage.getLanguage().getId().equals(profileLanguageDTO.getLanguageId()),
							String.format("Language %s already exists on profile %d",
									profileLanguage.getLanguage().getDescription(), profile.getId()));
				}
			}
		}
		profileDAO.saveOrUpdate(profile);
		Language language = languageDAO.get(profileLanguageDTO.getLanguageId());

		ProfileLanguage profileLanguage;

		if (profileLanguageDTO.getProfileLanguageId() != null) {
			profileLanguage = profileLanguageDAO.get(profileLanguageDTO.getProfileLanguageId());
			profileLanguage.setLanguageProficiencyType(new LanguageProficiencyType(profileLanguageDTO.getLanguageProficiencyTypeCode()));

		} else {
			profileLanguage = new ProfileLanguage();
			profileLanguage.setProfile(profile);
			profileLanguage.setLanguage(language);
			profileLanguage.setLanguageProficiencyType(new LanguageProficiencyType(profileLanguageDTO.getLanguageProficiencyTypeCode()));
			profileLanguageDAO.saveOrUpdate(profileLanguage);
		}

		authenticationService.refreshSessionForUser(user.getId());

		return profileLanguage;
	}

	@Override
	public void deleteProfileLanguage(Long profileLanguageId) {
		ProfileLanguage profileLanguage = profileLanguageDAO.get(profileLanguageId);
		profileLanguageDAO.delete(profileLanguage);
	}

	@Override
	public User updateUser(Long userId, String firstName, String lastName, String email) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

		User user = userDAO.get(userId);
		Assert.notNull(user);

		Map<String, String> properties = Maps.newHashMap();
		properties.put("firstName", firstName);
		properties.put("lastName", lastName);
		properties.put("email", email);

		return userService.updateUserProperties(userId, properties);
	}

	@Override
	public User updateUser(Long userId, UserDTO userDTO) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		Assert.notNull(userId);
		Assert.notNull(userDTO);
		User user = userDAO.get(userId);

		user.setSpendLimit(userDTO.getSpendLimit());
		user.setSalary(userDTO.getSalary());
		user.setStockOptions(userDTO.getStockOptions());
		user.setStartDate(userDTO.getStartDate());

		// This is required since we need to check for the profile modifications
		updateUser(userId, userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
		return user;
	}

	@Override
	public Address saveOrUpdateCompanyAddress(Long userId, AddressDTO addressDTO) {
		User user = userDAO.get(userId);
		Address address = user.getCompany().getAddress();

		if (address == null) {
			address = new Address(AddressType.COMPANY);
		}

		if (addressDTO.getLocationTypeId() != null) {
			address.setLocationType(locationTypeDAO.findLocationTypeById(addressDTO.getLocationTypeId()));
		}

		if (addressDTO.getPostalCode() == null) {
			addressDTO.setPostalCode("");
		}
		if (StringUtils.isBlank(addressDTO.getState())) {
			addressDTO.setState(Constants.NO_STATE);
		}

		BeanUtilities.copyProperties(address, addressDTO, new String[]{"country", "state"});
		address.setCountry(Country.valueOf(addressDTO.getCountry()));
		State state = invariantDataService.findStateWithCountryAndState(addressDTO.getCountry(), addressDTO.getState());
		if (state != null) {
			address.setState(state);
			address.setCountry(state.getCountry());
		} else {
			addressService.addNewStateToAddress(address, addressDTO.getCountry(), addressDTO.getState());
		}

		addressService.saveOrUpdate(address);
		user.getCompany().setAddress(address);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.ADDRESS, null);
		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);

		return address;
	}

	@Override
	public PostalCode updateProfilePostalCode(Profile profile, String profilePostalCode, AddressDTO addressDto) {
		State stateEntity = invariantDataService.findStateWithCountryAndState(
				Country.valueOf(addressDto.getCountry()).getId(), addressDto.getState()
		);
		PostalCode postalCodeEntity = postalCodeDAO.findByPostalCodeCountryStateCity(
				profilePostalCode, Country.valueOf(addressDto.getCountry()).getId(), stateEntity, addressDto.getCity()
		);
		profile.setProfilePostalCode(postalCodeEntity);
		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.ADDRESS, null);
		userGroupValidationService.revalidateAllAssociationsByUserAsync(profile.getUser().getId(), params);
		authenticationService.refreshSessionForUser(profile.getUser().getId());
		return postalCodeEntity;
	}

	@Override
	public Company findCompany(Long userId) {
		Assert.notNull(userId);
		Company company = null;
		User user = userDAO.get(userId);

		if (user != null) {
			company = user.getCompany();
			companyDAO.initialize(company);
		}
		return company;
	}

	@Override
	public Company findCompanyById(Long companyId) {
		return companyDAO.findCompanyById(companyId);
	}

	@Override
	@CacheEvict(
			value = PROFILE,
			key = "#root.target.PROFILE + #profile.getUser().getId()"
	)
	public void saveOrUpdateProfile(Profile profile) {
		profileDAO.saveOrUpdate(profile);
	}

	@Override
	public void saveOrUpdateLocationTypePreferences(Long userId, List<LocationType> locationTypes) {
		Assert.notNull(userId);
		Assert.notNull(locationTypes);

		List<LocationType> currentLocations = userLocationTypeAssociationDAO.findActiveLocationTypesByUserId(userId);

		List<LocationType> newLocations = new ArrayList<>(currentLocations);
		currentLocations.removeAll(locationTypes);
		newLocations.removeAll(currentLocations);
		locationTypes.removeAll(newLocations);

		// Adding
		for (LocationType location : locationTypes) {
			assignLocationTypeToUser(userId, location.getId());
		}

		// Removing
		for (LocationType location : currentLocations) {
			removeLocationTypeFromUser(userId, location.getId());
		}
	}

	@Override
	public void updateLocationTypePreferences(Long userId, Long[] locationTypeIds) {
		Assert.notNull(userId);
		Assert.noNullElements(locationTypeIds);
		List<LocationType> locationTypes = new ArrayList<>();
		for (Long id : locationTypeIds) {
			locationTypes.add(locationTypeDAO.findLocationTypeById(id));
		}

		saveOrUpdateLocationTypePreferences(userId, locationTypes);
	}

	@Override
	public List<LocationType> findLocationTypesPreferenceByUserId(Long userId) {
		Assert.notNull(userId);
		return userLocationTypeAssociationDAO.findActiveLocationTypesByUserId(userId);
	}

	private void assignLocationTypeToUser(Long userId, Long locationTypeId) {
		Assert.notNull(userId);
		Assert.notNull(locationTypeId);

		UserLocationTypeAssociation userLocation = userLocationTypeAssociationDAO.findByUserAndLocationType(userId, locationTypeId);

		if (userLocation == null) {
			User user = userDAO.get(userId);
			Assert.notNull(user);

			LocationType location = locationTypeDAO.findLocationTypeById(locationTypeId);
			Assert.notNull(location);

			userLocation = new UserLocationTypeAssociation();
			userLocation.setUser(user);
			userLocation.setLocationType(location);
			userLocationTypeAssociationDAO.saveOrUpdate(userLocation);
		} else {
			// If the record already exists we just activate the association
			userLocation.setDeleted(Boolean.FALSE);
		}
	}

	private void removeLocationTypeFromUser(Long userId, Long locationTypeId) {
		Assert.notNull(userId);
		Assert.notNull(locationTypeId);

		UserLocationTypeAssociation userLocation = userLocationTypeAssociationDAO.findByUserAndLocationType(userId, locationTypeId);

		if (userLocation != null) {
			userLocation.setDeleted(Boolean.TRUE);
		}
	}

	@Override
	public Address findCompanyAddress(Long userId) {
		Assert.notNull(userId);
		Company company = findCompany(userId);
		Address address = null;
		if (company != null) {
			address = company.getAddress();
			addressDAO.initialize(company.getAddress());
		}

		return address;
	}

	@Override
	public Address findAddress(Long workerId) {
		Assert.notNull(workerId);

		ProfileDTO profile = findProfileDTO(workerId);
		if (profile != null) {
			return addressService.findById(profile.getAddressId());
		}

		return findCompany(workerId).getAddress();
	}

	@Override
	public Profile findProfile(Long userId) {
		return profileDAO.findByUser(userId);
	}

	@Override
	public Long findProfileId(Long userId) {
		return profileDAO.findProfileId(userId);
	}

	@Override
	@Cacheable(
			value = PROFILE,
			key = "#root.target.PROFILE + #userId"
	)
	public ProfileDTO findProfileDTO(Long userId) {
		Assert.notNull(userId);

		ProfileDTO profileDTO = new ProfileDTO();
		Profile profile = profileDAO.findByUser(userId);
		if (profile == null) {
			return profileDTO;
		}
		profileDTO.setProfileId(profile.getId());
		profileDTO.setFirstName(profile.getUser().getFirstName());
		profileDTO.setLastName(profile.getUser().getLastName());
		profileDTO.setJobTitle(profile.getJobTitle());
		profileDTO.setOverview(profile.getOverview());
		profileDTO.setMobilePhone(profile.getMobilePhone());
		if (profile.isMobilePhoneInternationalCodeSet()) {
			profileDTO.setMobilePhoneInternationalCallingCodeId(profile.getMobilePhoneInternationalCode().getCallingCodeId());
		}
		profileDTO.setWorkPhone(profile.getWorkPhone());
		profileDTO.setWorkPhoneExtension(profile.getWorkPhoneExtension());
		if (profile.isWorkPhoneInternationalCodeSet()) {
			profileDTO.setWorkPhoneInternationalCallingCodeId(profile.getWorkPhoneInternationalCode().getCallingCodeId());
			profileDTO.setWorkPhoneInternationalCode(profile.getWorkPhoneInternationalCode().getId());
		}
		profileDTO.setTimeZoneId(profile.getTimeZone().getId());
		profileDTO.setTimeZoneCode(profile.getTimeZone().getTimeZoneId());
		profileDTO.setMaxTravelDistance(profile.getMaxTravelDistance());
		profileDTO.setMinOnsiteHourlyRate(profile.getMinOnsiteHourlyRate());
		profileDTO.setMinOffsiteHourlyRate(profile.getMinOffsiteHourlyRate());
		profileDTO.setMinOnsiteWorkPrice(profile.getMinOnsiteWorkPrice());
		profileDTO.setMinOffsiteWorkPrice(profile.getMinOffsiteWorkPrice());

		List<String> blacklistedPostalCodes = Lists.newArrayListWithCapacity(profile.getBlacklistedPostalCodes().size());
		for (PostalCode postalCode : profile.getBlacklistedPostalCodes()) {
			blacklistedPostalCodes.add(postalCode.getPostalCode());
		}
		profileDTO.setBlacklistedPostalCodes(blacklistedPostalCodes);

		profileDTO.setSmsPhone(profile.getSmsPhone());
		profileDTO.setSmsPhoneVerified(profile.getSmsPhoneVerified());

		profileDTO.setAddressId(profile.getAddressId());
		if (profile.getAddressId() != null) {
			Address address = addressService.findById(profile.getAddressId());
			profileDTO.setAddress1(address.getAddress1());
			profileDTO.setAddress2(address.getAddress2());
			profileDTO.setCity(address.getCity());
			profileDTO.setState(address.getState().getShortName());
			profileDTO.setCountry(address.getCountry().getId());
			profileDTO.setPostalCode(address.getPostalCode());
			profileDTO.setLatitude(address.getLatitude());
			profileDTO.setLongitude(address.getLongitude());
		}

		if (profile.getProfilePostalCode() != null) {
			profileDTO.setPostalCode(profile.getProfilePostalCode().getPostalCode());
			profileDTO.setLatitude(new BigDecimal(profile.getProfilePostalCode().getLatitude()));
			profileDTO.setLongitude(new BigDecimal(profile.getProfilePostalCode().getLongitude()));
		}

		return profileDTO;
	}

	@Override
	public Profile findById(Long profileId) {
		return profileDAO.findById(profileId);
	}

	@Override
	public List<ProfileLanguage> findProfileLanguages(Long userId) {
		Profile profile = profileDAO.findByUser(userId);
		if (profile != null) {
			return profileLanguageDAO.findAllProfileLanguageByProfileId(profile.getId());
		}
		return null;
	}

	// Profile Modifications
	@Override
	public void registerUserProfileModification(Long userId, ProfileModificationType modificationType) {
		Assert.notNull(userId);
		Assert.notNull(modificationType);

		User user = userService.findUserById(userId);
		if (!userRoleService.isInternalUser(user) && (userRoleService.hasAclRole(user, AclRole.ACL_SHARED_WORKER) && user.isLane3Approved())) {

			List<UserProfileModification> profileModifications = userProfileModificationDAO.findAllPendingProfileModificationsByUserIdAndType(userId, modificationType);

			if (profileModifications.isEmpty()) {

				UserProfileModification profileModification = new UserProfileModification();
				profileModification.setProfileModificationType(modificationType);
				profileModification.setUser(user);
				userProfileModificationDAO.saveOrUpdate(profileModification);
			} else {
				for (UserProfileModification p : profileModifications)
					// trigger the modification date audit change
					p.setUser(user);
			}

			// Change the User status back to PENDING
			// user.setUserStatusType(new UserStatusType(UserStatusType.PENDING));
		}

		if (modificationType.getCode().equals(ProfileModificationType.USER_NAME)) {
			//Check if his company has his name as effective name
			if (user.getCompany() != null && user.getCompany().getOperatingAsIndividualFlag()
					&& user.getCompany().getCreatedBy().getId().equals(userId)) {
				user.getCompany().setEffectiveName(StringUtilities.fullName(user.getFirstName(), user.getLastName()));
			}
		}

		companyDAO.saveOrUpdate(user.getCompany());
		authenticationService.refreshSessionForCompany(user.getCompany().getId());
	}

	@Override
	public ProfileModificationPagination findAllProfileModificationsByUserId(Long userId, ProfileModificationPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		return userProfileModificationDAO.findAllProfileModificationsByUserId(userId, pagination);

	}

	@Override
	public List<UserProfileModification> findAllPendingModificationsByUserId(Long userId) {
		Assert.notNull(userId);
		return userProfileModificationDAO.findAllPendingProfileModificationsByUserId(userId);
	}

	@Override
	public ProfileModificationPagination findAllPendingProfileModifications(ProfileModificationPagination pagination) {
		Assert.notNull(pagination);
		return userProfileModificationDAO.findAllPendingProfileModifications(pagination);

	}

	@Override
	public void approveUserProfileModifications(Long userId, Long[] profileModificationIds) {
		List<String> description = new ArrayList<>();

		for (Long id : profileModificationIds)
			description.add((approveProfileModification(id, true)).getDescription());

		userNotificationService.onProfileModificationApproved(userId, description);

	}

	private ProfileModificationType approveProfileModification(Long profileModificationId, boolean suppresNotification) {
		Assert.notNull(profileModificationId);

		UserProfileModification profileModification = userProfileModificationDAO.findUserProfileModificationById(profileModificationId);
		Assert.notNull(profileModification);

		Long userId = profileModification.getUser().getId();
		User user = userService.getUser(userId);

		updateProfileModificationStatus(profileModificationId, UserProfileModificationStatus.APPROVED);

		// Send notification
		if (!suppresNotification)
			userNotificationService.onProfileModificationApproved(user.getId(), Arrays.asList(profileModification.getProfileModificationType().getDescription()));

		// Verify if there are more pending modifications
		if (userProfileModificationDAO.findAllPendingProfileModificationsByUserId(userId).isEmpty()) {
			authenticationService.approveUser(user.getId());
		}

		return profileModification.getProfileModificationType();
	}

	@Override
	public void declineProfileModification(Long profileModificationId) {
		Assert.notNull(profileModificationId);
		updateProfileModificationStatus(profileModificationId, UserProfileModificationStatus.REJECTED);
	}

	@Override
	public void approveUserProfileModifications(Long userId) {
		Assert.notNull(userId);
		List<UserProfileModification> modifications = userProfileModificationDAO.findAllPendingProfileModificationsByUserId(userId);

		if (modifications.isEmpty()) {
			authenticationService.approveUser(userId);
		} else {
			List<String> description = new ArrayList<>();

			for (UserProfileModification modification : modifications) {
				approveProfileModification(modification.getId(), true);
				description.add(modification.getProfileModificationType().getDescription());
			}
			userNotificationService.onProfileModificationApproved(userId, description);

		}
	}

	@Override
	public void declineUserProfileModifications(Long userId) {
		Assert.notNull(userId);
		List<UserProfileModification> modifications = userProfileModificationDAO.findAllPendingProfileModificationsByUserId(userId);

		for (UserProfileModification modification : modifications)
			declineProfileModification(modification.getId());
	}

	@Override
	public Map<Long, String> findAllUsersByCompanyId(Long companyId) {
		Assert.notNull(companyId);
		return userDAO.findAllUsersByCompanyId(companyId);
	}

	@Override
	public Map<Long, String> findAllActiveUsersByCompanyId(Long companyId) {
		Assert.notNull(companyId);
		return userDAO.findAllActiveUsersByCompanyId(companyId);
	}

	@Override
	public List<User> findAllUsersByCompanyId(Long companyId, List<String> userStatusTypeCodes) {
		Assert.notNull(companyId);
		Assert.notEmpty(userStatusTypeCodes);

		return userDAO.findAllUsersByCompanyIdAndStatus(companyId, userStatusTypeCodes);
	}

	@Override
	public List<User> findApprovedLane3UsersByCompanyId(Long companyId) {
		Assert.notNull(companyId);
		return userDAO.findApprovedLane3UsersByCompanyId(companyId);
	}

	private void updateProfileModificationStatus(Long profileModificationId, UserProfileModificationStatus status) {
		Assert.notNull(profileModificationId);
		Assert.notNull(status);

		UserProfileModification profileModification = userProfileModificationDAO.findUserProfileModificationById(profileModificationId);

		Assert.notNull(profileModification);

		profileModification.setUserProfileModificationStatus(status);

		// If rejecting the modification, revert to the original/old value or remove where old values aren't stored.

		if (status.equals(UserProfileModificationStatus.REJECTED))
			restoreProfileModificationToOriginalValue(profileModification.getUser(), profileModification.getProfileModificationType());
	}

	private void restoreProfileModificationToOriginalValue(User user, ProfileModificationType type) {
		if (type.getCode().equals(ProfileModificationType.USER_NAME)) {
			user.setFirstName(user.getFirstNameOldValue());
			user.setLastName(user.getLastNameOldValue());
			user.setFirstNameOldValue(null);
			user.setLastNameOldValue(null);
		} else if (type.getCode().equals(ProfileModificationType.COMPANY_NAME)) {
			user.getCompany().setName(user.getCompany().getNameOldValue());
			user.getCompany().setNameOldValue(null);
		} else if (type.getCode().equals(ProfileModificationType.COMPANY_OVERVIEW)) {
			user.getCompany().setOverview(user.getCompany().getOverviewOldValue());
			user.getCompany().setOverviewOldValue(null);
		} else if (type.getCode().equals(ProfileModificationType.COMPANY_WEBSITE)) {
			user.getCompany().setWebsite(user.getCompany().getWebsiteOldValue());
			user.getCompany().setWebsiteOldValue(null);
		} else if (type.getCode().equals(ProfileModificationType.USER_AVATAR)) {
			// Assumes the current (and rejected) avatar was pre-approved rather than pending
			UserAssetAssociation avatars = userAssetAssociationDAO.findUserAvatars(user.getId());
			avatars.setApprovalStatus(ApprovalStatus.DECLINED);
		} else if (type.getCode().equals(ProfileModificationType.COMPANY_AVATAR)) {
			// Assumes the current (and rejected) avatar was pre-approved rather than pending
			CompanyAssetAssociation avatars = companyAssetAssociationDAO.findCompanyAvatars(user.getCompany().getId());
			avatars.setApprovalStatus(ApprovalStatus.DECLINED);
		} else if (type.getCode().equals(ProfileModificationType.MOBILE_PHONE_NUMBER)) {
			user.getProfile().setMobilePhone(user.getProfile().getMobilePhoneOldValue());
			user.getProfile().setMobilePhoneOldValue(null);
		} else if (type.getCode().equals(ProfileModificationType.SMS_PHONE_NUMBER)) {
			user.getProfile().setSmsPhone(user.getProfile().getSmsPhoneOldValue());
			user.getProfile().setSmsPhoneOldValue(null);
		} else if (type.getCode().equals(ProfileModificationType.WORK_PHONE_NUMBER)) {
			user.getProfile().setWorkPhone(user.getProfile().getWorkPhoneOldValue());
			user.getProfile().setWorkPhoneExtension(user.getProfile().getWorkPhoneExtensionOldValue());
			user.getProfile().setWorkPhoneOldValue(null);
			user.getProfile().setWorkPhoneExtensionOldValue(null);
		} else if (type.getCode().equals(ProfileModificationType.USER_OVERVIEW)) {
			user.getProfile().setOverview(user.getProfile().getOverviewOldValue());
			user.getProfile().setOverviewOldValue(null);
		} else if (type.getCode().equals(ProfileModificationType.RESUME)) {
			for (UserAssetAssociation resume : userAssetAssociationDAO.findAllActiveUserAssetsByUserAndType(user.getId(), UserAssetAssociationType.RESUME)) {
				resume.setApprovalStatus(ApprovalStatus.DECLINED);
			}
		}

		authenticationService.refreshSessionForUser(user.getId());
	}

	// LinkedIn
	// TODO Refactor/repackage somewhere more appropriate (especially private methods)

	@Autowired
	private LinkedInOAuthService linkedInOAuthService;
	@Autowired
	private LinkedInApiClientFactory linkedInApiClientFactory;

	@Override
	public String getLinkedInAuthorizationUrl(Long userId, String callbackUrl) {
		OAuthToken oauthToken = oAuthTokenDAO.findByUserAndProvider(userId, OAuthTokenProviderType.LINKEDIN);
		if (oauthToken == null) {
			oauthToken = new OAuthToken();
			oauthToken.setUser(userDAO.get(userId));
			oauthToken.setProviderType(new OAuthTokenProviderType(OAuthTokenProviderType.LINKEDIN));
		}
		return getLinkedInAuthorizationUrl(oauthToken, callbackUrl);
	}

	@Override
	public Boolean authorizeLinkedIn(Long userId, String verifier) {
		OAuthToken oauthToken = oAuthTokenDAO.findByUserAndProvider(userId, OAuthTokenProviderType.LINKEDIN);
		if (oauthToken == null) {
			return false;
		}
		oauthToken = authorizeLinkedIn(oauthToken, verifier);

		Boolean status = (oauthToken.getAccessToken() != null);

		if (status) {
			LinkedInProfileDTO dto = getLinkedInProfile(userId);
			Profile profile = findProfile(userId);
			profileDAO.findById(userId);
			Assert.notNull(profile, "Unable to find profile");

			profile.setLinkedInId(dto.getLinkedInId());
		}

		return status;
	}

	@Override
	public LinkedInProfileDTO getLinkedInProfile(Long userId) {
		LinkedInProfileDTO profileDTO = new LinkedInProfileDTO();
		LinkedInApiClient apiClient = getLinkedInClient(userId);
		if (apiClient != null) {
			profileDTO = getProfileForLinkedInProfile(apiClient);
		}
		return profileDTO;
	}

	private LinkedInApiClient getLinkedInClient(Long userId) {
		LinkedInApiClient apiClient = null;

		OAuthToken oauthToken = oAuthTokenDAO.findByUserAndProvider(userId, OAuthTokenProviderType.LINKEDIN);
		if (oauthToken != null) {
			LinkedInAccessToken accessToken = new LinkedInAccessToken(oauthToken.getAccessToken(), oauthToken.getAccessTokenSecret());
			apiClient = linkedInApiClientFactory.createLinkedInApiClient(accessToken);
		}

		return apiClient;
	}

	private String getLinkedInAuthorizationUrl(OAuthToken oauthToken, String callbackUrl) {
		LinkedInRequestToken requestToken = (callbackUrl != null) ?
				linkedInOAuthService.getOAuthRequestToken(callbackUrl) :
				linkedInOAuthService.getOAuthRequestToken();

		oauthToken.setRequestToken(requestToken.getToken());
		oauthToken.setRequestTokenSecret(requestToken.getTokenSecret());
		oauthToken.setAccessToken(null);
		oauthToken.setAccessTokenSecret(null);
		oAuthTokenDAO.saveOrUpdate(oauthToken);

		return requestToken.getAuthorizationUrl();
	}

	private OAuthToken authorizeLinkedIn(OAuthToken oauthToken, String verifier) {
		LinkedInRequestToken requestToken = new LinkedInRequestToken(oauthToken.getRequestToken(), oauthToken.getRequestTokenSecret());
		LinkedInAccessToken accessToken = linkedInOAuthService.getOAuthAccessToken(requestToken, verifier);
		oauthToken.setAccessToken(accessToken.getToken());
		oauthToken.setAccessTokenSecret(accessToken.getTokenSecret());

		return oauthToken;
	}

	private LinkedInProfileDTO getProfileForLinkedInProfile(LinkedInApiClient client) {
		Person profile = client.getProfileForCurrentUser(EnumSet.of(
				ProfileField.ID,
				ProfileField.FIRST_NAME,
				ProfileField.LAST_NAME,
				ProfileField.MAIN_ADDRESS,
				ProfileField.LOCATION,
				ProfileField.LOCATION_COUNTRY,
				ProfileField.PHONE_NUMBERS,
				ProfileField.PICTURE_URL,
				ProfileField.PUBLIC_PROFILE_URL,
				ProfileField.POSITIONS,
				ProfileField.SUMMARY,
				ProfileField.SPECIALTIES,
				ProfileField.ASSOCIATIONS,
				ProfileField.EDUCATIONS,
				ProfileField.HEADLINE
		));

		LinkedInProfileDTO p = new LinkedInProfileDTO();
		p.setLinkedInId(profile.getId());
		p.setFirstName(profile.getFirstName());
		p.setLastName(profile.getLastName());
		p.setOverview(profile.getSummary());
		p.setJobTitle(profile.getHeadline());

		// Associations
		p.setAssociations(StringUtilities.defaultString(profile.getAssociations(), null));
		// Specialties
		p.setSpecialties(StringUtilities.defaultString(profile.getSpecialties(), null));

		if (profile.getPhoneNumbers() != null) {
			for (PhoneNumber number : profile.getPhoneNumbers().getPhoneNumberList()) {
				if (number.getPhoneType().equals(PhoneType.WORK)) {
					p.setWorkPhone(number.getPhoneNumber());
				} else if (number.getPhoneType().equals(PhoneType.MOBILE)) {
					p.setMobilePhone(number.getPhoneNumber());
					p.setSmsPhone(number.getPhoneNumber());
				}
			}
		}

		// Employment History
		List<EmploymentHistoryDTO> employmentHistory = new ArrayList<>();

		for (Position position : profile.getPositions().getPositionList()) {

			EmploymentHistoryDTO dto = new EmploymentHistoryDTO();
			String companyName = StringUtilities.defaultString(position.getCompany().getName(), null);
			dto.setCompanyName(companyName);
			dto.setTitle(StringUtilities.defaultString(position.getTitle(), null));
			dto.setDescription(StringUtilities.defaultString(position.getSummary(), null));

			if (position.getStartDate() != null) {
				dto.setDateFromYear(position.getStartDate().getYear());
				dto.setDateFromMonth(position.getStartDate().getMonth());
			}
			if (position.getEndDate() != null) {
				dto.setDateToYear(position.getEndDate().getYear());
				dto.setDateToMonth(position.getEndDate().getMonth());
			}
			dto.setCurrent(position.isIsCurrent());
			if (position.isIsCurrent()) {
				p.setCompanyName(companyName);
				p.setIndustry(StringUtilities.defaultString(position.getCompany().getIndustry(), null));
			}

			employmentHistory.add(dto);
		}
		p.setEmploymentHistory(employmentHistory);

		// Education History
		List<EducationHistoryDTO> educationHistory = new ArrayList<>();

		for (Education e : profile.getEducations().getEducationList()) {
			EducationHistoryDTO dto = new EducationHistoryDTO();
			dto.setSchoolName(StringUtilities.defaultString(e.getSchoolName(), null));
			dto.setDegree(StringUtilities.defaultString(e.getDegree(), null));
			dto.setFieldOfStudy(StringUtilities.defaultString(e.getFieldOfStudy(), null));
			dto.setActivities(StringUtilities.defaultString(e.getActivities(), null));
			if (e.getStartDate() != null) {
				dto.setDateFromYear(e.getStartDate().getYear());
				dto.setDateFromMonth(e.getStartDate().getMonth());
			}
			if (e.getEndDate() != null) {
				dto.setDateToYear(e.getEndDate().getYear());
				dto.setDateToMonth(e.getEndDate().getMonth());
			}

			educationHistory.add(dto);
		}
		p.setEducationHistory(educationHistory);

		if (profile.getMainAddress() != null) {
			AddressDTO address = geocodingService.parseAddress(profile.getMainAddress());
			BeanUtils.copyProperties(address, p);
		}

		p.setAvatarAbsoluteURI(profile.getPictureUrl());

		return p;
	}

	@Override
	public void suspendCompany(Long companyId, String comment) {
		updateCompanySuspendedStatus(companyId, Boolean.TRUE, comment);
	}

	@Override
	public void unsuspendCompany(Long companyId) {
		updateCompanySuspendedStatus(companyId, Boolean.FALSE);
	}

	/**
	 * Updates the Company suspended status
	 *
	 * @param companyId
	 * @param status
	 */
	private void updateCompanySuspendedStatus(Long companyId, boolean status) {
		updateCompanySuspendedStatus(companyId, status, StringUtils.EMPTY);
	}

	private void updateCompanySuspendedStatus(Long companyId, boolean status, String comment) {
		Assert.notNull(companyId);
		Company company = findCompanyById(companyId);
		Assert.notNull(company, "Unable to find Company");

		for (User user : userDAO.findAllActiveEmployees(companyId)) {
			if (status && !authenticationService.isSuspended(user)) {
				userService.suspendUser(user.getId(), status);
			}
		}
		if (StringUtils.isNotBlank(comment)) {
			CompanyCommentDTO companyCommentDTO = new CompanyCommentDTO();
			companyCommentDTO.setComment(comment);
			companyCommentDTO.setCompanyId(company.getId());
			commentService.saveOrUpdateClientServiceCompanyComment(companyCommentDTO);
		}

		company.setCompanyStatusType(new CompanyStatusType(status ? CompanyStatusType.SUSPENDED : CompanyStatusType.ACTIVE));

		authenticationService.refreshSessionForCompany(company.getId());
	}

	@Override
	public List<String> findBlacklistedZipcodesForUser(Long userId) {
		Assert.notNull(userId);
		Profile profile = findProfile(userId);
		List<String> postalCodes = Lists.newArrayListWithCapacity(profile.getBlacklistedPostalCodes().size());
		for (PostalCode z : profile.getBlacklistedPostalCodes()) {
			postalCodes.add(z.getPostalCode());
		}
		return postalCodes;
	}

	@Override
	@CacheEvict(
			value = PROFILE,
			key = "#root.target.PROFILE + #userId"
	)
	public void setBlacklistedZipcodesForUser(Long userId, String[] zipcodes) {
		Assert.notNull(userId);
		Assert.notNull(zipcodes);

		Profile profile = profileDAO.findByUser(userId);
		Assert.notNull(profile);

		profile.getBlacklistedPostalCodes().clear();
		for (String z : zipcodes) {
			profile.getBlacklistedPostalCodes().add(invariantDataService.getPostalCodeByCode(z));
		}
	}

	@Override
	@CacheEvict(
			value = PROFILE,
			key = "#root.target.PROFILE + #userId"
	)
	public void setBlacklistedZipcodesForUser(Long userId, List<String> zipcodes) {
		setBlacklistedZipcodesForUser(userId, zipcodes.toArray(new String[zipcodes.size()]));
	}

	@Override
	public List<Asset> findAllUserResumes(Long userId) {
		Assert.notNull(userId);
		List<Asset> list = new ArrayList<>();
		List<UserAssetAssociation> assetList = userAssetAssociationDAO.findAllActiveUserAssetsByUserAndType(userId, UserAssetAssociationType.RESUME);
		for (UserAssetAssociation asset : assetList) {
			list.add(asset.getAsset());
		}
		return list;
	}

	@Override
	public List<UserAssetAssociation> findAllUserProfileAndAvatarImageAssociations(Long userId) {
		Assert.notNull(userId);
		List<UserAssetAssociation> userAssets = userAssetAssociationDAO.findAllActiveUserAssetsWithAvailabilityByUserAndType(userId, UserAssetAssociationType.PROFILE_IMAGE);
		List<UserAssetAssociation> avatars = userAssetAssociationDAO.findUserAssetAssociationAvatarWithAvailability(userId);
		if (!avatars.isEmpty()) {
			userAssets.add(avatars.get(0));
		}
		return userAssets;
	}

	@Override
	public List<UserAssetAssociation> findAllUserProfileImageAssociations(Long userId) {
		Assert.notNull(userId);
		return userAssetAssociationDAO.findAllActiveUserAssetsWithAvailabilityByUserAndType(userId, UserAssetAssociationType.PROFILE_IMAGE);
	}

	@Override
	public List<UserAssetAssociation> findAllUserProfileVideoAssociations(Long userId) {
		Assert.notNull(userId);
		return userAssetAssociationDAO.findAllActiveUserAssetsWithAvailabilityByUserAndType(userId, UserAssetAssociationType.PROFILE_VIDEO);
	}

	@Override
	public List<UserAssetAssociation> findAllUserProfileOrderedAssetAssociations(Long userId) {
		Assert.notNull(userId);
		return userAssetAssociationDAO.findAllActiveOrderedUserAssetsWithAvailabilityByUserAndType(userId, new String[]{UserAssetAssociationType.PROFILE_VIDEO, UserAssetAssociationType.PROFILE_IMAGE});
	}

	@Override
	public List<UserAssetAssociation> findAllUserProfileVideoAssociations(String userNumber) {
		Assert.notNull(userNumber);
		User user = userService.findUserByUserNumber(userNumber);
		Assert.notNull(user);
		return findAllUserProfileVideoAssociations(user.getId());
	}

	@Override
	public List<UserLinkAssociation> findAllUserProfileEmbedVideoAssociations(String userNumber) {
		Assert.notNull(userNumber);
		User user = userService.findUserByUserNumber(userNumber);
		Assert.notNull(user);
		return findAllUserProfileEmbedVideoAssociations(user.getId());
	}

	@Override
	public List<UserLinkAssociation> findAllUserProfileEmbedVideoAssociations(Long userId) {
		Assert.notNull(userId);
		return userLinkAssociationDAO.findUserLinkAssociationsByUserId(userId);
	}

	@Override
	public UserAssetAssociation findUserAssetAssociation(Long userId, Long AssetId) {
		Assert.notNull(userId);
		return userAssetAssociationDAO.findUserAssetAssociation(userId, AssetId);
	}

	@Override
	public UserAssetAssociation undeleteAssetAssociation(UserAssetAssociation assetAssoc) {
		assetAssoc.setDeleted(false);
		userAssetAssociationDAO.saveOrUpdate(assetAssoc);
		return assetAssoc;
	}

	@Override
	@CacheEvict(
			value = PROFILE,
			key = "#root.target.PROFILE + #userId"
	)
	public void closeCsrProfileLead(Long userId) {
		updateProfileCSROpenStatus(userId, false);
	}

	@Override
	@CacheEvict(
			value = PROFILE,
			key = "#root.target.PROFILE + #userId"
	)
	public void openCsrProfileLead(Long userId) {
		updateProfileCSROpenStatus(userId, true);
	}

	private void updateProfileCSROpenStatus(long userId, boolean isOpen) {
		Profile profile = findProfile(userId);
		Assert.notNull(profile, "Unable to find user profile.");
		profile.setIsCsrOpen(isOpen);
	}

	@Override
	public UserProfileCompletenessDTO getUserProfileCompleteness(Long userId) throws LinkedInImportFailed {
		Assert.notNull(userId);
		Map<String, Boolean> completeness = profileDAO.getProfileCompleteness(userId);
		User user = userDAO.get(userId);
		Assert.notNull(user);
		long companyId = user.getCompany().getId();

		Integer userTotal = 20;

		List<ProfileActionType> actions = profileActionTypeDAO.findAll();
		List<ProfileActionType> missingActions = Lists.newLinkedList();

		completeness.put(ProfileActionType.WORKMARKET_101, true);
		completeness.put(ProfileActionType.PHOTO, userAssetAssociationDAO.findUserAvatarOriginal(userId) != null);
		completeness.put(ProfileActionType.RESUME, isNotEmpty(findAllUserResumes(userId)));
		completeness.put(ProfileActionType.COMPANY_LOGO, companyAssetAssociationDAO.findCompanyAvatarOriginal(companyId) != null);
		completeness.put(ProfileActionType.BANK, isNotEmpty(bankingService.findBankAccountsByCompany(companyId)));
		completeness.put(ProfileActionType.TAX, taxService.findActiveTaxEntity(userId) != null);

		// LinkedIn
		LinkedInPerson person = linkedInService.findMostRecentLinkedInPerson(userId);
		completeness.put(ProfileActionType.EDUCATION, person != null && CollectionUtils.isNotEmpty(EducationHistoryDTO.getEducationHistory(person)));
		completeness.put(ProfileActionType.EMPLOYMENT_HISTORY, person != null && CollectionUtils.isNotEmpty(EmploymentHistoryDTO.getEmploymentHistory(person)));
		completeness.put(ProfileActionType.LINKEDIN, (person != null));

		ProfileActionType lane3Action = new ProfileActionType();
		for (ProfileActionType action : actions) {
			if (ProfileActionType.LANE_3.equals(action.getCode())) {
				lane3Action = action;
			} else {
				boolean isComplete = MapUtils.getBoolean(completeness, action.getCode(), false);
				if (isComplete) {
					userTotal = userTotal + action.getWeight();
				} else {
					missingActions.add(action);
				}
			}
		}

		// If eligible and not yet lane 4 active, add to top of list (without affecting completeness total)
		if (userTotal >= Constants.PROFILE_COMPLETENESS_THRESHOLD && !authenticationService.isLane4Active(user)) {
			missingActions.add(0, lane3Action);
		}

		hideDrugTestsDoorman.welcome(
			new UserGuest(user),
			new HideDrugTestsRope(
				missingActions,
				profileActionTypeDAO.findBy("code", ProfileActionType.DRUG_TEST)
			)
		);

		hideBackgroundChecksDoorman.welcome(
			new UserGuest(user),
			new HideBackgroundChecksRope(
				missingActions,
				profileActionTypeDAO.findBy("code", ProfileActionType.BACKGROUND_CHECK)
			)
		);

		hideProfileInsuranceDoorman.welcome(
			new UserGuest(user),
			new HideProfileInsuranceRope(
				missingActions,
				profileActionTypeDAO.findBy("code", ProfileActionType.INSURANCE)
			)
		);

		UserProfileCompletenessDTO dto = new UserProfileCompletenessDTO();
		dto.setMissingActions(missingActions);
		dto.setCompletedPercentage(userTotal < 100 ? userTotal : 100);

		return dto;
	}

	@Override
	@CacheEvict(
			value = PROFILE,
			key = "#root.target.PROFILE + #userId"
	)
	public void updateProfileProperties(Long userId, Map<String, String> properties) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		updateProfileProperties(userId, properties, null, true);
	}
	
	public void updateProfileProperties(Long userId, Map<String, String> properties, boolean shouldSendEmail) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		updateProfileProperties(userId, properties, null, shouldSendEmail);
	}

	private void updateProfileProperties(Long userId, Map<String, String> properties, Profile profile, boolean shouldSendEmail) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		Assert.notNull(userId, "User id must be provided");
		Assert.notNull(properties, "Properties must be provided");

		if (profile == null) {
			profile = profileDAO.findByUser(userId);
		}

		Assert.notNull(profile, "Unable to find profile");

		Map<String, Object> params = new HashMap<>();
		if (properties.containsKey("maxTravelDistance")) {
			params.put(ProfileModificationType.MAX_TRAVEL_DISTANCE, null);
		}
		if (properties.containsKey("hourlyRate") || properties.containsKey("minOnsiteHourlyRate") || properties.containsKey("minOffsiteHourlyRate")) {
			params.put(ProfileModificationType.HOURLY_RATE, null);
		}

		String workPhone = properties.get("workPhone");
		String workPhoneExtension = properties.get("workPhoneExtension");
		String mobilePhone = properties.get("mobilePhone");
		String smsPhone = properties.get("smsPhone");
		String overview = properties.get("overview");
		String workInternational = properties.get("workPhoneInternationalCode");
		String mobileInternational = properties.get("mobilePhoneInternationalCode");

		if (properties.containsKey("workPhoneInternationalCode")) {
			properties.remove("workPhoneInternationalCode");
		}

		if (properties.containsKey("mobilePhoneInternationalCode")) {
			properties.remove("mobilePhoneInternationalCode");
		}

		if (StringUtilities.isNotEmpty(workInternational)) {
			profile.setWorkPhoneInternationalCode(invariantDataService.findCallingCodeFromID(Long.valueOf(workInternational)));
		}

		if (StringUtilities.isNotEmpty(mobileInternational)) {
			profile.setMobilePhoneInternationalCode(invariantDataService.findCallingCodeFromID(Long.valueOf(mobileInternational)));
		}
		// Register the profile modifications
		if (StringUtils.isNotBlank(workPhone) && !StringUtilities.same(workPhone, profile.getWorkPhone())) {
			registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.WORK_PHONE_NUMBER));
			properties.put("workPhoneOldValue", profile.getWorkPhone());
			if (profile.getOnboardCompleted() && shouldSendEmail) {
				sendProfileUpdateEmail(userId, "work phone number");
			}
		}

		if (StringUtils.isNotBlank(workPhoneExtension) && !StringUtilities.same(workPhoneExtension, profile.getWorkPhoneExtension())) {
			registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.WORK_PHONE_NUMBER));
			properties.put("workPhoneExtensionOldValue", profile.getWorkPhoneExtension());
			if (profile.getOnboardCompleted() && shouldSendEmail) {
				sendProfileUpdateEmail(userId, "work phone extension");
			}
		}

		if (StringUtils.isNotBlank(mobilePhone) && !StringUtilities.same(mobilePhone, profile.getMobilePhone())) {
			registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.MOBILE_PHONE_NUMBER));
			properties.put("mobilePhoneOldValue", profile.getMobilePhone());
			if (profile.getOnboardCompleted() && shouldSendEmail) {
				sendProfileUpdateEmail(userId, "mobile phone number");
			}
		}

		if (StringUtils.isNotBlank(smsPhone) && !StringUtilities.same(smsPhone, profile.getSmsPhone())) {
			registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.SMS_PHONE_NUMBER));
			properties.put("smsPhoneOldValue", profile.getSmsPhone());
		}

		if (StringUtils.isNotBlank(overview) && !StringUtilities.same(overview, profile.getOverview())) {
			registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.USER_OVERVIEW));
			properties.put("overviewOldValue", profile.getOverview());
		}

		if (properties.containsKey("timeZoneId")) {
			Long timeZoneId = Long.parseLong(properties.get("timeZoneId"));

			Assert.notNull(timeZoneId);
			profile.setTimeZone(invariantDataService.findTimeZonesById(timeZoneId));
			properties.remove("timeZoneId");
		}

		if (properties.containsKey("industry.id")) {
			Long industryId = Long.parseLong(properties.get("industry.id"));
			Long profileId = profile.getId();
			Assert.notNull(profileId);
			Assert.notNull(industryId);

			if (!industryService.doesProfileHaveIndustry(profileId, industryId)) {
				ProfileIndustryAssociation profileIndustryAssociation = industryService.findProfileIndustryAssociationByProfileIdAndIndustryId(profileId, industryId);
				if (profileIndustryAssociation != null) {
					profileIndustryAssociation.setDeleted(false);
				} else {
					Industry industry = invariantDataService.findIndustry(industryId);
					Assert.notNull(industry);
					industryService.makeProfileIndustryAssociation(industry, profile);
				}
				params.put(ProfileModificationType.INDUSTRY, industryId);
			}
			properties.remove("industry.id");
		}

		String yearsOfExperience = properties.get(OnboardProfilePropertyMap.YEARS_OF_EXPERIENCE);
		if (StringUtils.isBlank(yearsOfExperience)) {
			profile.setYearsOfExperienceEnum(null);
		} else {
			profile.setYearsOfExperienceEnum(YearsOfExperienceEnum.getEnumFromDescription(yearsOfExperience));
			properties.remove("yearsOfExperience");
		}

		String gender = properties.get(OnboardProfilePropertyMap.GENDER);
		if (StringUtils.isBlank(gender)) {
			profile.setGender(null);
		} else {
			profile.setGender(Gender.getEnumFromCode(gender));
			properties.remove("gender");
		}

		if (StringUtils.isNotBlank(properties.get(OnboardProfilePropertyMap.JOB_TITLE))) {
			final Qualification qualification =
				addQualification(properties.get(OnboardProfilePropertyMap.JOB_TITLE), QualificationType.job_title);
			addUserQualificationAssociation(userId, qualification);
		}

		if (properties.containsKey("findWork")) {
			profile.setFindWork(BooleanUtils.toBoolean(properties.get("findWork"), "true", "false"));
			properties.remove("findWork");
		}

		BeanUtilities.updateProperties(profile, properties);

		if (!params.isEmpty()) {
			userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);
		}

		userIndexer.reindexById(userId);

		authenticationService.refreshSessionForUser(userId);

		if (properties.containsKey("onboardCompleted")) {
			Map<String, Object> propMap = userDAO.getProjectionMapById(userId, "userNumber");
			redisAdapter.delete(propMap.get("userNumber") + "-onboardCompleted");
		}
	}

	@Override
	public void sendProfileUpdateEmail(Long userId, String fieldName) {
		EmailTemplate template = emailTemplateFactory.buildProfileUpdateEmailTemplate(userId, fieldName);
		notificationService.sendNotification(template);
	}

	@Override
	@CacheEvict(
			value = PROFILE,
			key = "#root.target.PROFILE + #userId"
	)
	public Profile updateProfileAddressProperties(Long userId, Map<String, String> properties) {
		Assert.notNull(userId, "User id must be provided");
		Assert.notNull(properties, "Properties must be provided");

		Profile profile = profileDAO.findByUser(userId);
		Assert.notNull(profile, "Unable to find profile");

		ProfileDTO profileDTO = new ProfileDTO();
		BeanUtilities.copyProperties(profileDTO, properties);

		String addressType = properties.containsKey("addressType") ? properties.get("addressType") : AddressType.PROFILE;
		profileDTO.setUseCompanyAddress(addressType.equals(AddressType.COMPANY));

		if (profileDTO.getPostalCode() == null) {
			profileDTO.setPostalCode("");
		}
		if (StringUtils.isBlank(profileDTO.getState())) {
			profileDTO.setState(Constants.NO_STATE);
		}
		updateProfileAddress(profile.getId(), profileDTO);
		updateProfilePostalCode(profile, properties.get("postalCode"), profileDTO);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.ADDRESS, null);
		userGroupValidationService.revalidateAllAssociationsByUserAsync(profile.getUser().getId(), params);

		return profile;
	}

	@Override
	public void updateProfileAddress(Long profileId, Long addressId) {
		Assert.notNull(profileId, "Profile id is required");
		Assert.notNull(addressId, "Address id is required");

		Profile profile = findById(profileId);
		Assert.notNull(profile, "Unable to find profile");
		profile.setAddressId(addressId);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.ADDRESS, null);
		userGroupValidationService.revalidateAllAssociationsByUserAsync(profile.getUser().getId(), params);

	}

	@Override
	public void updateAddress(Long profileId, Address address) {
		Assert.notNull(profileId);
		Assert.notNull(address);
		Profile profile = findById(profileId);
		Assert.notNull(profile, "Unable to find profile");

		if (address.getId() == null) {
			addressService.saveOrUpdate(address);
		}

		profile.setAddressId(address.getId());
		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.ADDRESS, null);
		userGroupValidationService.revalidateAllAssociationsByUserAsync(profile.getUser().getId(), params);
	}

	@Override
	public void updateProfileIndustries(Long profileId, Long[] industryIds) {
		Assert.notNull(profileId);
		Assert.notNull(industryIds);

		Profile profile = findById(profileId);
		Assert.notNull(profile, "Unable to find profile");

		Set<Industry> industries = Sets.newLinkedHashSet(industryDAO.get(industryIds));
		if (industries.isEmpty()) { return; }

		industryService.setIndustriesForProfile(profileId, industries);
		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.INDUSTRY, null);

		userGroupValidationService.revalidateAllAssociationsByUserAsync(profile.getUser().getId(), params);

		userIndexer.reindexById(profile.getUser().getId());
	}

	@Override
	public void addPhoneToProfile(Long profileId, PhoneNumberDTO phoneDTO) {
		Assert.notNull(profileId);
		Profile profile = findById(profileId);
		Assert.notNull(profile, "Unable to find Profile");

		Phone phone = directoryService.saveOrUpdatePhoneNumber(phoneDTO);

		ProfilePhoneAssociation phoneAssociation = new ProfilePhoneAssociation(profile, phone);
		profilePhoneAssociationDAO.saveOrUpdate(phoneAssociation);
	}

	private void updateProfileAddress(long profileId, ProfileDTO profileDTO) {
		Profile profile = findById(profileId);
		Assert.notNull(profile, "Unable to find Profile");

		Address address = addressService.findById(profile.getAddressId());
		User user = userDAO.get(profile.getUser().getId());

		if (profileDTO.isUseCompanyAddress()) {
			// if the current address was a profile address, deactive it before
			// attaching the company address - the address row will be orphaned in the db
			// so the flag lets us know which rows are orphaned
			if (address != null && address.getAddressType().isProfileAddress()) {
				address.setDeactivatedFlag(true);
			}
			addressService.saveOrUpdate(address);
		} else {
			// Not using a company address any longer:
			// * If no address, create a new profile address row
			// * If a company address, create a new profile address row
			// * If already a profile address, simply update
			if (address == null || address.getAddressType().isCompanyAddress()) {
				address = new Address(AddressType.PROFILE);
			}

			Country country = Country.USA_COUNTRY;
			State state = invariantDataService.findStateWithCountryAndState(
					Country.valueOf(profileDTO.getCountry()).getId(), profileDTO.getState()
			);
			if (state != null) {
				address.setState(state);
				address.setCountry(state.getCountry());
			} else {
				addressService.addNewStateToAddress(address, profileDTO.getCountry(), profileDTO.getState());
			}
			if (StringUtils.isNotBlank(profileDTO.getCountry())) {
				country = Country.valueOf(profileDTO.getCountry());
			}
			address.setCountry(country);
			BeanUtilities.copyProperties(address, profileDTO, new String[]{"country", "state"});
			addressService.saveOrUpdate(address);
			profile.setAddressId(address.getId());
			saveOrUpdateProfile(profile);
		}

		authenticationService.refreshSessionForUser(user.getId());
	}


	@Override
	public Coordinate findLatLongForUser(Long userId) {
		Assert.notNull(userId);
		ProfileDTO profileDTO = findProfileDTO(userId);
		Assert.notNull(profileDTO);

		if (profileDTO.getAddressId() != null) {
			Coordinate coordinate = addressService.getCoordinatesByAddressId(profileDTO.getAddressId());
			if (coordinate != null) {
				return coordinate;
			}
		}

		if (profileDTO.isLatLongSet()) {
			return geocodingService.newCoordinate(profileDTO.getLatitude().doubleValue(), profileDTO.getLongitude().doubleValue());
		}

		return null;
	}

	@Override
	public Optional<PostalCode> findPostalCodeForUser(Long userId) {
		Profile profile = profileDAO.findByUser(userId);
		if (profile.getAddressId() != null) {
			Address address = addressService.findById(profile.getAddressId());
			if (address != null) {
				return Optional.fromNullable(invariantDataService.getPostalCodeByCodeCountryStateCity(address.getPostalCode(), address.getCountry().getId(), address.getState().getShortName(), address.getCity()));
			}
		}
		return Optional.absent();
	}

	@Override
	public TimeZone findUserProfileTimeZone(Long userId) {
		return profileDAO.findUserProfileTimeZone(userId);
	}

	@Override
	public MboProfile findMboProfile(Long userId) {
		return mboProfileDAO.findMboProfile(userId);
	}

	@Override
	public void saveMboProfile(MboProfile profile) {
		mboProfileDAO.saveOrUpdate(profile);
	}

	@Override
	public MboProfile findMboProfileByGUID(String objectGUID) {
		return mboProfileDAO.findMboProfileByGUID(objectGUID);
	}

	@Override
	public List<Phone> findPhonesByProfileId(long profileId) {
		return profilePhoneAssociationDAO.findPhonesByProfileId(profileId);
	}

	@Override
	public Map<String, Object> getProjectionMapByUserNumber(String userNumber, String... fields) {
		Assert.notEmpty(fields);
		String key = userNumber + "-onboardCompleted";

		if (fields.length == 1 && fields[0].equals("onboardCompleted")) {
			Optional<Object> onboardedCompleted = redisAdapter.get(key);
			if (onboardedCompleted.isPresent()) {
				return ImmutableMap.of("onboardCompleted", onboardedCompleted.get());
			}
		}

		Map<String, Object> result = profileDAO.getProjectionMapByUserNumber(userNumber, fields);

		if (result.containsKey("onboardCompleted")) {
			redisAdapter.set(key, result.get("onboardCompleted"), TWO_WEEKS_IN_SECONDS);
		}

		return result;
	}

	@Override
	public ImmutableList<Map> getProjectedFollowers(String[] fields) throws Exception {
		Long companyId = authenticationService.getCurrentUserCompanyId();
		return ImmutableList.copyOf(ProjectionUtilities.projectAsArray(fields, findAllUsersByCompanyId(companyId, Lists.newArrayList(UserStatusType.APPROVED))));
	}

	@Override
	public void saveOnboardPhoneCodes(Long profileId, WorkerOnboardingDTO dto, boolean overwriteMissingPhoneNumbers) throws Exception {
		OnboardProfilePropertyMapCreator creator = new OnboardProfilePropertyMapCreator(dto);
		OnboardProfilePropertyMap map = creator.profilePhoneCodePropertiesMap(overwriteMissingPhoneNumbers);
		boolean profileNeedsUpdating = false;
		Profile profile = findById(profileId);
		CallingCode code;

		if (creator.hasPhoneInfo("mobile") && hasCallingCodeChanged(map.getMobileCode(), profile.getMobilePhoneInternationalCode())) {
			code = null;
			if (map.getMobileCode() != null) {
				code = invariantDataService.findCallingCodeFromID(Long.valueOf(map.getMobileCode()));
				if (code == null) {
					code = invariantDataService.findCallingCodeFromCallingCodeId(map.getMobileCode());
					if (code == null) {
						throw new ValidationException("phones.code", String.format("Unknown phone code %s", map.getMobileCode()));
					}
				}
			}
			profile.setMobilePhoneInternationalCode(code);
			profileNeedsUpdating = true;
		}

		if (creator.hasPhoneInfo("sms") && hasCallingCodeChanged(map.getSmsCode(), profile.getSmsPhoneInternationalCode())) {
			code = null;
			if (map.getSmsCode() != null) {
				code = invariantDataService.findCallingCodeFromID(Long.valueOf(map.getSmsCode()));
				if (code == null) {
					code = invariantDataService.findCallingCodeFromCallingCodeId(map.getSmsCode());
					if (code == null) {
						throw new ValidationException("phones.code", String.format("Unknown phone code %s", map.getSmsCode()));
					}
				}
			}
			profile.setSmsPhoneInternationalCode(code);
			profileNeedsUpdating = true;
		}

		if (creator.hasPhoneInfo("work") && hasCallingCodeChanged(map.getWorkCode(), profile.getWorkPhoneInternationalCode())) {
			code = null;
			if (map.getWorkCode() != null) {
				code = invariantDataService.findCallingCodeFromID(Long.valueOf(map.getWorkCode()));
				if (code == null) {
					code = invariantDataService.findCallingCodeFromCallingCodeId(map.getWorkCode());
					if (code == null) {
						throw new ValidationException("phones.code", String.format("Unknown phone code %s", map.getWorkCode()));
					}
				}
			}
			profile.setWorkPhoneInternationalCode(code);
			profileNeedsUpdating = true;
		}

		if (profileNeedsUpdating) {
			saveOrUpdateProfile(profile);
		}
	}

	private boolean hasCallingCodeChanged(String codeId, CallingCode otherCode) {
		return (codeId == null && otherCode != null) || (codeId != null && otherCode == null)
				|| (codeId != null && otherCode != null && !codeId.equals(otherCode.getCallingCodeId()));
	}

	@Override
	@CacheEvict(
			value = PROFILE,
			key = "#root.target.PROFILE + #userId"
	)
	public void saveOnboardProfile(Long userId, Long profileId, Company company, WorkerOnboardingDTO dto, boolean overwriteMissingPhoneNumbers) throws Exception {
		OnboardProfilePropertyMapCreator creator = new OnboardProfilePropertyMapCreator(dto);
		Profile profile = findById(profileId);
		User user = profile.getUser();

		OnboardProfilePropertyMap map = creator.userPropertiesMap();
		if (MapUtils.isNotEmpty(map)) {
			userService.updateUserProperties(userId, map);
		}

		map = creator.profilePropertiesMap(overwriteMissingPhoneNumbers);
		if (MapUtils.isNotEmpty(map)) {
			updateProfileProperties(userId, map, profile, true);
		}

		map = creator.profileAddressPropertiesMap();
		if (MapUtils.isNotEmpty(map)) {
			updateProfileAddressProperties(userId, map);
		}

		map = creator.companyPropertiesMap();
		if (MapUtils.isNotEmpty(map)) {
			companyService.updateCompanyProperties(company.getId(), map);
		}

		if (dto.getIndustries() != null && dto.getIndustries().size() > 0) {
			List<OnboardingIndustryDTO> selected = Lists.newArrayList(Iterables.filter(dto.getIndustries(), new Predicate<OnboardingIndustryDTO>() {
				@Override
				public boolean apply(@Nullable OnboardingIndustryDTO onboardingIndustryDTO) {
					return onboardingIndustryDTO.isChecked();
				}
			}));
			List<Long> industryIds = project(selected, Long.class, on(OnboardingIndustryDTO.class).getId());

			if (CollectionUtils.isEmpty(industryIds)) {
				industryIds.add(Industry.NONE.getId());
			}

			for(OnboardingIndustryDTO industryDTO : selected){
				if(industryDTO.getId().equals(Industry.GENERAL.getId())){
					//save otherName
					profileIndustryAssociationDAO.saveOtherNameForProfileId(profile.getId(), industryDTO.getOtherName());
					break;
				}
			}

			updateProfileIndustries(profile.getId(), industryIds.toArray(new Long[industryIds.size()]));
		}

		if (dto.hasAvatar()) {
			saveProfileAvatar(userId, dto.getAvatar());
		}

		if (dto.hasCompanyLogo()) {
			saveCompanyLogo(user.getCompany().getId(), dto);
		}

		if (dto.getSkills() != null) {
			List<Integer> skillIds = new ArrayList<>();
			List<Integer> specialtyIds = new ArrayList<>();

			for (OnboardingSkillDTO skill : dto.getSkills()) {
				switch (skill.getType()) {
					case SKILL: {
						if (skill.getId() == null) {
							SkillDTO skillDTO = new SkillDTO(skill.getName());
							skillDTO.setDescription(skill.getName());
							Skill newSkill = skillService.saveOrUpdateSkill(skillDTO);
							skillIds.add(newSkill.getId().intValue());
						} else {
							skillIds.add(skill.getId().intValue());
						}

						if (featureEvaluator.hasGlobalFeature(ADD_QUALIFICATION_FEATURE)
							&& StringUtils.isNotBlank(skill.getName())) {
							final Qualification skillQualificaiton =
								addQualification(skill.getName(), QualificationType.skill);
							addUserQualificationAssociation(userId, skillQualificaiton);
						}

						break;
					}
					case SPECIALTY: {
						if(skill.getId() == null) {
							SpecialtyDTO specialtyDTO = new SpecialtyDTO();
							specialtyDTO.setName(skill.getName());
							specialtyDTO.setDescription(skill.getName());

							Specialty newSpecialty = specialtyService.saveOrUpdateSpecialty(specialtyDTO);
							specialtyIds.add(newSpecialty.getId().intValue());
						} else {
							specialtyIds.add(skill.getId().intValue());
						}

						if (featureEvaluator.hasGlobalFeature(ADD_QUALIFICATION_FEATURE)
							&& StringUtils.isNotBlank(skill.getName())) {
							final Qualification specialtyQualification =
								addQualification(skill.getName(), QualificationType.specialty);
							addUserQualificationAssociation(userId, specialtyQualification);
						}

						break;
					}

					default:
						break;
				}

			}

			skillService.setSkillsOfUser(skillIds, user.getId());
			specialtyService.setSpecialtiesOfUser(specialtyIds, user.getId());
		}

		checkOnboardCompleteness(user, dto.getIsLastStep());
	}

	private static final Long TELAID_COMPANY_ID = 8298L;

	@Override
	public boolean isUserTelaidPrivate(Long userId) {
		// TODO: delete this code when we support multi-tenant installation and Telaid has a dedicated site
		User user = userDAO.get(userId);
		List<Long> exclusiveCompanyIds =
			user.isUserExclusive() ? laneService.findAllCompaniesWhereUserIsResource(userId, LaneType.LANE_2): Collections.EMPTY_LIST;
		return exclusiveCompanyIds.size() == 1 && exclusiveCompanyIds.contains(TELAID_COMPANY_ID);
	}

	private void saveCompanyLogo(Long userCompanyId, WorkerOnboardingDTO dto) throws Exception {
		ImageDTO image = dto.getLogo();
		String contentType = MimeTypeUtilities.guessMimeType(image.getFilename());

		if (!MimeTypeUtilities.isImage(contentType)) {
			throw new BadRequestException(RestCode.WRONG_IMAGE_FORMAT);
		}

		File file = File.createTempFile(String.format(TEMP_FILENAME, userCompanyId), ".dat");
		SerializationUtilities.decodeBase64File(image.stripContentTypeFromImage(), file);

		AssetDTO assetDTO = new AssetDTO();
		assetDTO.setSourceFilePath(file.getAbsolutePath());
		assetDTO.setName(image.getFilename());
		assetDTO.setMimeType(contentType);
		assetDTO.setAssociationType(CompanyAssetAssociationType.AVATAR);
		assetDTO.setLargeTransformation(true);
		assetDTO.setSmallTransformation(true);

		try {
			assetManagementService.storeAssetForCompany(assetDTO, userCompanyId);
		} catch (AssetTransformationException e) {
			logger.error(e.getMessage(), e);
			throw new BadRequestException(RestCode.UNREADEABLE_IMAGE);
		}

		CompanyAssetAssociation avatars = companyService.findCompanyAvatars(userCompanyId);
		Asset asset = avatars.getAsset();

		assetDTO = AssetDTO.newDTO(asset);
		assetDTO.setDescription("Company logo");
		assetDTO.setDisplayable(true);
		assetDTO.setAddToCompanyLibrary(true);
		assetManagementService.addAssetToCompany(assetDTO, userCompanyId);
	}

	@Override
	public TimeZone getTimeZoneByUserId(Long userId) {
		Assert.notNull(userId);
		Profile profile = findProfile(userId);
		if (profile == null) {
			return invariantDataService.findTimeZonesById(Constants.WM_TIME_ZONE_ID);
		}

		if (profile.getAddressId() != null) {
			Address address = addressService.findById(profile.getAddressId());
			if (address != null) {
				TimeZone tz = dateTimeService.matchTimeZoneForPostalCode(
					address.getPostalCode(),
					address.getCountry().getId(),
					address.getState().getShortName(),
					address.getCity());
				if (tz != null) {
					return tz;
				}
			}
		}

		return profile.getTimeZone();
	}

	public void saveProfileAvatar(Long userId, ImageDTO image) throws Exception {
		String contentType = MimeTypeUtilities.guessMimeType(image.getFilename());

		if (!MimeTypeUtilities.isImage(contentType)) {
			throw new BadRequestException(RestCode.WRONG_IMAGE_FORMAT);
		}

		File tmpFile = File.createTempFile("avatar", null);
		SerializationUtilities.decodeBase64File(image.stripContentTypeFromImage(), tmpFile);

		AssetDTO assetDTO = new AssetDTO();
		assetDTO.setSourceFilePath(tmpFile.getAbsolutePath());
		assetDTO.setName(image.getFilename());
		assetDTO.setMimeType(contentType);
		assetDTO.setAssociationType(UserAssetAssociationType.AVATAR);
		assetDTO.setLargeTransformation(true);
		assetDTO.setSmallTransformation(true);

		Asset asset;
		try {
			asset = assetManagementService.storeAssetForUser(assetDTO, userId);
		} catch (AssetTransformationException e) {
			logger.error(e);
			throw new BadRequestException(RestCode.UNREADEABLE_IMAGE);
		}

		AssetDTO assetDTO2 = AssetDTO.newDTO(asset);
		assetDTO2.setAssociationType(UserAssetAssociationType.AVATAR);
		assetDTO2.setLargeTransformation(true);
		assetDTO2.setSmallTransformation(true);

		AssetDTO.TransformerParameters transformation = new AssetDTO.TransformerParameters();
		if(image.getCoordinates() != null) {
			ImageCoordinates coordinates = image.getCoordinates();
			transformation.setCrop(coordinates.getX(), coordinates.getY(), coordinates.getX2(), coordinates.getY2());
		}
		assetDTO2.setTransformerParameters(transformation);
		assetManagementService.addAssetToUser(assetDTO2, userId);

		authenticationService.refreshSessionForUser(userId);
	}

	private boolean isOnboardComplete(Long userId, Boolean isLastStep) {
		Profile profile = findProfile(userId);
		Company company = findCompany(userId);

		return onboardCompleteValidator.validateWeb(profile, company, isLastStep);
	}

	private void checkOnboardCompleteness(User user, Boolean isLastStep) throws Exception {
		Map<String, Object> props = getProjectionMapByUserNumber(user.getUserNumber(), "onboardCompleted");
		if ("false".equals(props.get("onboardCompleted")) && isOnboardComplete(user.getId(), isLastStep)) {
			updateProfileProperties(
					user.getId(),
					ImmutableMap.of("onboardCompleted", "1", "onboardCompletedOn", Calendar.getInstance().getTime().toString())
			);
		}
	}

	private Qualification addQualification(final String qualificationName, final QualificationType qualificationType) {
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

	private UserToQualification addUserQualificationAssociation(final Long userId, final Qualification qualification) {
		final UserToQualification userToQualification =
			new UserToQualification(userId, qualification.getUuid(), qualification.getQualificationType());
		if (qualification.getUuid() != null) {
			qualificationAssociationService.saveOrUpdate(userToQualification);
		}
		return userToQualification;
	}
}
