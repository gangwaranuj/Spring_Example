package com.workmarket.service.business;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.LanguageProficiencyType;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileActionType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserProfileModification;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.comment.CommentPagination;
import com.workmarket.domains.model.company.CompanyStatusType;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.onboarding.model.OnboardingIndustryDTO;
import com.workmarket.domains.onboarding.model.WorkerOnboardingDTO;
import com.workmarket.dto.AddressDTO;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.dto.CompanyDTO;
import com.workmarket.service.business.dto.InsuranceDTO;
import com.workmarket.service.business.dto.ManageMyWorkMarketDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.ProfileLanguageDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.UserProfileCompletenessDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.RandomUtilities;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ProfileServiceIT extends BaseServiceIT {

	@Autowired private ProfileService profileService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private UserService userService;
	@Autowired private CompanyService companyService;
	@Autowired private CommentService commentService;
	@Autowired private LicenseService licenseService;
	@Autowired private CertificationService certificationService;
	@Autowired private InsuranceService insuranceService;
	@Autowired private IndustryService industryService;
	@Autowired @Qualifier("redisCacheOnly") private RedisAdapter redisAdapter;

	@Test
	public void saveOrUpdateProfile() throws Exception {
		User user = newRegisteredWorker();

		Map<String, String> properties = Maps.newHashMap();

		properties.put("overview", "Im a super hero with super powers.");
		properties.put("jobTitle", "Super Hero");
		properties.put("mobilePhone", "5235628945");
		properties.put("smsPhone", "2355648942");
		properties.put("onboardCompleted", "true");

		profileService.updateProfileProperties(user.getId(), properties);

		Profile userProfile = profileService.findProfile(user.getId());

		assertNotNull(userProfile);
		assertEquals(userProfile.getOverview(), "Im a super hero with super powers.");
		assertEquals(userProfile.getMobilePhone(), "5235628945");
		assertEquals(userProfile.getSmsPhone(), "2355648942");
		assertTrue(userProfile.getOnboardCompleted());
		assertFalse(userProfile.getManageWork());
		assertTrue(userProfile.getFindWork());

		profileService.updateProfileAddressProperties(user.getId(), makeAddressProperties());
		userProfile = profileService.findProfile(user.getId());

		assertNotNull(userProfile);

		Address address = addressService.findById(userProfile.getAddressId());
		assertEquals(address.getAddress1(), "some address 1");
		assertEquals(address.getCountry().getId(), Country.USA);
	}

	@Test
	public void saveOrUpdateProfile_findWork_for_worker() throws Exception {
		final Company company = companyService.createCompany("Test", false, "unknown");
		User user = newCompanyEmployeeStaffConfirmed(company.getId());
		Profile userProfile = profileService.findProfile(user.getId());

		assertNotNull(userProfile);
		assertFalse(userProfile.getFindWork());

		List<Long> updatedRoles = Lists.asList(AclRole.ACL_WORKER, new Long[]{AclRole.ACL_EMPLOYEE_WORKER});
		authenticationService.updateUserAclRoles(user.getId(), updatedRoles);
		Map<String, String> properties = Maps.newHashMap();
		properties.put("findWork", BooleanUtils.toStringTrueFalse(CollectionUtilities.containsAny(updatedRoles, AclRole.ACL_SHARED_WORKER, AclRole.ACL_WORKER, AclRole.ACL_EMPLOYEE_WORKER)));

		profileService.updateProfileProperties(user.getId(), properties);
		userProfile = profileService.findProfile(user.getId());
		assertTrue(userProfile.getFindWork());
	}

	private Map<String, String> makeAddressProperties() {
		Map<String, String> properties = Maps.newHashMap();
		properties.put("address1", "some address 1");
		properties.put("city", "some city");
		properties.put("state", "NY");
		properties.put("country", "US");
		properties.put("postalCode", "10011");
		return properties;
	}

	@Test
	@Transactional
	public void setBlacklistedZipcodes() throws Exception {
		User contractor = newContractorIndependent();
		Long userId = contractor.getId();

		profileService.setBlacklistedZipcodesForUser(userId, new String[]{
				"10010",
				"10001",
				"10001",
				"11238"
		});

		User user = userService.getUser(userId);
		assertEquals(3, user.getProfile().getBlacklistedPostalCodes().size());

		profileService.setBlacklistedZipcodesForUser(userId, new String[]{
				"10010",
				"10001",
				"10003",
				"10012"
		});

		user = userService.getUser(userId);
		assertEquals(4, user.getProfile().getBlacklistedPostalCodes().size());
	}

	@Test
	public void findLocationTypesPreferenceByUserId() throws Exception {
		User contractor = newContractorIndependent();
		Long userId = contractor.getId();

		List<LocationType> userLocationTypes = profileService.findLocationTypesPreferenceByUserId(userId);
		assertNotNull(userLocationTypes);

		List<LocationType> locationTypes = invariantDataService.getLocationTypes();
		profileService.saveOrUpdateLocationTypePreferences(userId, locationTypes);

		userLocationTypes = profileService.findLocationTypesPreferenceByUserId(userId);
		assertNotNull(userLocationTypes);
		assertEquals(userLocationTypes.size(), 4);
	}

	@Test
	public void saveOrUpdateLocationTypePreferences() throws Exception {
		User contractor = newContractorIndependent();
		Long userId = contractor.getId();
		Long[] locationTypeIds = {1L, 2L, 4L};

		profileService.updateLocationTypePreferences(userId, locationTypeIds);
		List<LocationType> userLocationTypes = profileService.findLocationTypesPreferenceByUserId(userId);
		assertNotNull(userLocationTypes);
		assertEquals(userLocationTypes.size(), 3);
	}

	@Test
	public void saveOrUpdateCompany() throws Exception {
		User contractor = newContractorIndependent();
		Long userId = contractor.getId();

		CompanyDTO dto = new CompanyDTO();
		dto.setName("Company");
		dto.setOverview("Some overview about my awesome company");
		dto.setWebsite("www.workmarket.com");
		dto.setEmployees(150);
		dto.setYearFounded(2010);
		dto.setIndustryId(INDUSTRY_ID);

		Company company = profileService.saveOrUpdateCompany(userId, dto);
		assertNotNull(company);
		assertEquals(company.getName(), "Company");
		assertEquals(company.getOverview(), "Some overview about my awesome company");
		assertEquals(company.getWebsite(), "www.workmarket.com");
		assertTrue(company.getEmployees() == 150);
		assertTrue(company.getYearFounded() == 2010);
		assertFalse(company.getCustomLowBalanceFlag());
		assertTrue(company.getLowBalancePercentage() == 20);
		assertNull(company.getLowBalanceAmount());

		dto.setName("Company");
		dto.setOverview("Some overview about my awesome company");
		dto.setWebsite("www.workmarket.com");
		dto.setEmployees(150);
		dto.setYearFounded(2010);
		dto.setIndustryId(INDUSTRY_ID);
		dto.setLowBalancePercentage(56);
		dto.setLowBalanceAmount(1256.23);

		company = profileService.saveOrUpdateCompany(userId, dto);
		assertTrue(company.getCustomLowBalanceFlag());
		assertTrue(company.getLowBalancePercentage() == 56);
		assertTrue(company.getLowBalanceAmount().equals(new BigDecimal(1256.23)));
	}

	@Test
	@Transactional
	public void updateUser() throws Exception {
		User contractor = newContractorIndependent();
		Long userId = contractor.getId();

		UserDTO dto = new UserDTO();
		dto.setFirstName("Hello");
		dto.setLastName("World");
		dto.setEmail("hello" + RandomUtilities.nextLong() + "@world.com");
		dto.setRecruitingCampaignId(1L);
		dto.setSpendLimit(new BigDecimal(1000.567));
		dto.setSalary(new BigDecimal(1000.567));
		dto.setStartDate(Calendar.getInstance());
		dto.setStockOptions(1520);

		profileService.updateUser(userId, dto);

		User user = userService.getUser(userId);
		assertTrue(user.getSpendLimit().equals(new BigDecimal(1000.567)));
		assertTrue(user.getStockOptions().equals(1520));
		assertTrue(user.getSalary().equals(new BigDecimal(1000.567)));
		assertTrue(user.hasSpendLimit());
	}

	@Test
	public void critsLeadIdUpdate() throws Exception {
		User contractor = newContractorIndependent();

		profileService.closeCsrProfileLead(contractor.getId());
		Profile profile = profileService.findProfile(contractor.getId());
		assertFalse(profile.getIsCsrOpen());

		profileService.openCsrProfileLead(contractor.getId());
		profile = profileService.findProfile(contractor.getId());
		assertTrue(profile.getIsCsrOpen());
	}

	@Test
	public void declineUserProfileModifications() throws Exception {
		User user = newContractorIndependentlane4Ready();

		authenticationService.setCurrentUser(user);

		// Update properties

		userService.updateUserProperties(user.getId(), CollectionUtilities.newStringMap(
			"firstName", "Shit",
			"lastName", "Bagger"
		));

		profileService.updateProfileProperties(user.getId(), CollectionUtilities.newStringMap(
			"overview", "I bag shit.",
			"workPhone", "2125551212",
			"workPhoneExtension", "911",
			"mobilePhone", "2126661212",
			"smsPhone", "2127771212"
		));

		companyService.updateCompanyProperties(user.getCompany().getId(), CollectionUtilities.newStringMap(
			"name", "Shit Baggers R Us",
			"overview", "We bag shit.",
			"website", "http://shitbaggersr.us"
		));

		List<UserProfileModification> modifications = profileService.findAllPendingModificationsByUserId(user.getId());

		assertEquals(8, modifications.size());

		user = userService.findUserById(user.getId());
		Company company = companyService.findCompanyById(user.getCompany().getId());

		assertEquals("Shit", user.getFirstName());
		assertEquals("Bagger", user.getLastName());

		assertEquals("I bag shit.", user.getProfile().getOverview());
		assertEquals("2125551212", user.getProfile().getWorkPhone());
		assertEquals("911", user.getProfile().getWorkPhoneExtension());
		assertEquals("2126661212", user.getProfile().getMobilePhone());
		assertEquals("2127771212", user.getProfile().getSmsPhone());

		assertEquals("Shit Baggers R Us", company.getName());
		assertEquals("We bag shit.", company.getOverview());
		assertEquals("http://shitbaggersr.us", company.getWebsite());

		profileService.declineUserProfileModifications(user.getId());

		user = userService.findUserById(user.getId());
		company = companyService.findCompanyById(user.getCompany().getId());

		assertFalse(user.getFirstName().equals("Shit"));
		assertFalse(user.getLastName().equals("Bagger"));

		assertNull(user.getProfile().getOverview());
		assertNull(user.getProfile().getWorkPhone());
		assertNull(user.getProfile().getWorkPhoneExtension());
		assertNull(user.getProfile().getMobilePhone());
		assertNull(user.getProfile().getSmsPhone());

		assertFalse(company.getName().equals("Shit Baggers R Us"));
		assertNull(company.getOverview());
		assertNull(company.getWebsite());
	}

	@Test
	public void getUserProfileCompleteness() throws Exception {
		User user = newEmployeeWithCashBalance();
		UserProfileCompletenessDTO dto = profileService.getUserProfileCompleteness(user.getId());

		assertTrue(dto.getCompletedPercentage() > 0);
		assertTrue(dto.getMissingActions().contains(new ProfileActionType(ProfileActionType.CERTIFICATIONS)));
		assertTrue(dto.getMissingActions().contains(new ProfileActionType(ProfileActionType.INSURANCE)));
		assertTrue(dto.getMissingActions().contains(new ProfileActionType(ProfileActionType.TAX)));
		assertTrue(dto.getMissingActions().contains(new ProfileActionType(ProfileActionType.WORKING_HOURS)));
		assertTrue(dto.getMissingActions().contains(new ProfileActionType(ProfileActionType.COMPANY_OVERVIEW)));
		assertFalse(dto.getMissingActions().contains(new ProfileActionType(ProfileActionType.LANE_3)));

		Company co = profileService.findCompany(user.getId());

		//User adds COMPANY_OVERVIEW
		CompanyDTO companyDTO = new CompanyDTO();
		companyDTO.setOverview("Some overview");
		companyDTO.setName(co.getName());

		co = profileService.saveOrUpdateCompany(user.getId(), companyDTO);
		assertTrue(co.getOverview().equals("Some overview"));

		dto = profileService.getUserProfileCompleteness(user.getId());
		assertFalse(dto.getMissingActions().contains(new ProfileActionType(ProfileActionType.COMPANY_OVERVIEW)));

	}

	@Test
	public void profileCompletenessIncludesUnverifiedCertifications() throws Exception {
		User user = newEmployeeWithCashBalance();
		certificationService.addCertificationToUser(CERTIFICATION_ID, user.getId(), CERTIFICATION_NUMBER);

		UserProfileCompletenessDTO dto = profileService.getUserProfileCompleteness(user.getId());
		assertFalse(dto.getMissingActions().contains(new ProfileActionType(ProfileActionType.CERTIFICATIONS)));
	}

	@Test
	public void profileCompletenessIncludesUnverifiedLicenses() throws Exception {
		User user = newEmployeeWithCashBalance();
		licenseService.addLicenseToUser(LICENSE_ID, user.getId(), LICENSE_NUMBER);

		UserProfileCompletenessDTO dto = profileService.getUserProfileCompleteness(user.getId());
		assertFalse(dto.getMissingActions().contains(new ProfileActionType(ProfileActionType.LICENSES)));
	}

	@Test
	public void profileCompletenessIncludesUnverifiedInsurance() throws Exception {
		User user = newEmployeeWithCashBalance();

		InsuranceDTO idto = new InsuranceDTO();
		idto.setInsuranceId(INSURANCE_ID);
		idto.setProvider("State Farm Insurance");
		idto.setPolicyNumber("123456");
		idto.setCoverage("Complete");
		idto.setIssueDate(Calendar.getInstance());
		idto.setExpirationDate(Calendar.getInstance());
		idto.setCoverage("Complete");

		insuranceService.addInsuranceToUser(user.getId(), idto);

		UserProfileCompletenessDTO dto = profileService.getUserProfileCompleteness(user.getId());
		assertFalse(dto.getMissingActions().contains(new ProfileActionType(ProfileActionType.INSURANCE)));
	}

	@Test
	public void saveOrUpdateCompanyCSR() throws Exception {
		User employee = newEmployeeWithCashBalance();

		CompanyDTO dto = new CompanyDTO();
		dto.setCompanyId(employee.getCompany().getId());
		dto.setName("Company");
		dto.setOverview("Some overview about my awesome company");
		dto.setWebsite("www.workmarket.com");
		dto.setEmployees(150);
		dto.setYearFounded(2010);

		Company company = profileService.saveOrUpdateCompany(dto);
		assertNotNull(company);
		assertEquals(company.getName(), "Company");
		assertEquals(company.getOverview(), "Some overview about my awesome company");
		assertEquals(company.getWebsite(), "www.workmarket.com");
		assertTrue(company.getEmployees() == 150);
		assertTrue(company.getYearFounded() == 2010);
		assertFalse(company.getCustomLowBalanceFlag());
		assertTrue(company.getLowBalancePercentage() == 20);
		assertNull(company.getLowBalanceAmount());
	}

	@Test
	public void updateManageMyWorkMarket() throws Exception {
		User employee = newEmployeeWithCashBalance();

		ManageMyWorkMarketDTO dto = new ManageMyWorkMarketDTO();
		dto.setAutocloseEnabledFlag(true);
		dto.setAutoRateEnabledFlag(true);
		dto.setEnableAssignmentPrintout(true);
		dto.setStandardTermsEndUserFlag(true);
		dto.setEnablePrintoutSignature(true);
		dto.setBadgeIncludedOnPrintout(true);
		dto.setHideWorkMarketLogoFlag(true);
		dto.setAutocloseDelayInHours(40);
		dto.setStandardInstructions("standard instructions for work");
		dto.setBuyerSupportContactFlag(true);
		dto.setBuyerSupportContactFirstName("first name");
		dto.setBuyerSupportContactLastName("last name");
		dto.setStandardInstructionsFlag(true);
		dto.setStandardInstructions("please enter the 4 digits code and some other instructions.");
		dto.setStandardTermsFlag(true);
		dto.setStandardTerms("You must agree with this terms otherwise you can't work :P.");
		dto.setIvrEnabledFlag(true);
		dto.setPaymentTermsDays(20);

		Company company = profileService.updateManageMyWorkMarket(employee.getCompany().getId(), dto);
		assertNotNull(company);

		assertTrue(company.getManageMyWorkMarket().getAutocloseEnabledFlag());
		assertTrue(company.getManageMyWorkMarket().isEnableAssignmentPrintout());
		assertTrue(company.getManageMyWorkMarket().isStandardTermsEndUserFlag());
		assertTrue(company.getManageMyWorkMarket().isEnablePrintoutSignature());
		assertTrue(company.getManageMyWorkMarket().isBadgeIncludedOnPrintout());
		assertTrue(company.getManageMyWorkMarket().getAutoRateEnabledFlag());
		assertTrue(company.getManageMyWorkMarket().getHideWorkMarketLogoFlag());
		assertTrue(company.getManageMyWorkMarket().getAutocloseDelayInHours() == 40);
		assertTrue(company.getManageMyWorkMarket().getUseCompanyLogoFlag());

		assertEquals(company.getManageMyWorkMarket().getStandardInstructions(), "please enter the 4 digits code and some other instructions.");
		assertTrue(company.getManageMyWorkMarket().getStandardInstructionsFlag());
		assertEquals(company.getManageMyWorkMarket().getStandardTerms(), "You must agree with this terms otherwise you can't work :P.");
		assertTrue(company.getManageMyWorkMarket().getIvrEnabledFlag());
		assertTrue(company.getManageMyWorkMarket().getPaymentTermsDays() == 20);
	}

	@Test
	public void updateSessionTimeout() throws Exception {
		User contractor = newContractorIndependent();
		profileService.updateProfileProperties(contractor.getId(), ImmutableMap.of("sessionDuration", "80"));

		Profile profile = profileService.findProfile(contractor.getId());
		assertNotNull(profile);
		assertTrue(profile.getSessionDuration() == 80);
	}

	@Test
	public void addPhoneToProfile() throws Exception {
		PhoneNumberDTO dto = new PhoneNumberDTO();
		dto.setPhone("203-562311245");
		dto.setContactContextType(ContactContextType.WORK);

		Profile profile = profileService.findProfile(ANONYMOUS_USER_ID);

		profileService.addPhoneToProfile(profile.getId(), dto);

		List<Phone> phones = profileService.findPhonesByProfileId(profile.getId());

		assertFalse(phones.isEmpty());

		for (Phone p : phones) {
			assertNotNull(p);
		}
	}

	@Test
	@Transactional
	public void getCoordinate() throws Exception {
		User user = newContractorIndependent();

		AddressDTO address = new AddressDTO();
		address.setCity("NY");
		address.setCountry("US");
		address.setPostalCode("10010");
		profileService.updateProfilePostalCode(user.getProfile(), "10010", address);

		Coordinate point = profileService.findLatLongForUser(user.getId());
		assertNotNull(point);
		assertEquals(40.738600, point.getLatitude(), 0);
		assertEquals(-73.982600, point.getLongitude(), 0);
	}

	@Test
	public void updateIndustry() throws Exception {
		User contractor = newContractorIndependent();

		// All collections should be different
		Long[] industryIds1 = {INDUSTRY_1000_ID, INDUSTRY_ID_1004};
		Long[] industryIds2 = {INDUSTRY_1000_ID};
		Long[] industryIds3 = {INDUSTRY_ID_1004};
		profileService.updateProfileIndustries(contractor.getProfile().getId(), industryIds1);

		Profile profile = profileService.findProfile(contractor.getId());
		assertNotNull(profile);

		List<Long> loadedProfileIndustryIds = CollectionUtilities.newListPropertyProjection(
			industryService.getIndustryDTOsForProfile(profile.getId()),
			"id"
		);
		assertTrue(Sets.newHashSet(loadedProfileIndustryIds).equals(Sets.newHashSet(industryIds1)));

		profileService.updateProfileIndustries(contractor.getProfile().getId(), industryIds2);
		profile = profileService.findProfile(contractor.getId());
		assertNotNull(profile);

		loadedProfileIndustryIds = CollectionUtilities.newListPropertyProjection(
			industryService.getIndustryDTOsForProfile(profile.getId()),
			"id"
		);
		assertTrue(Sets.newHashSet(loadedProfileIndustryIds).equals(Sets.newHashSet(industryIds2)));
		assertFalse(Sets.newHashSet(loadedProfileIndustryIds).equals(Sets.newHashSet(industryIds1)));

		profileService.updateProfileIndustries(contractor.getProfile().getId(), industryIds3);
		profile = profileService.findProfile(contractor.getId());
		assertNotNull(profile);

		loadedProfileIndustryIds = CollectionUtilities.newListPropertyProjection(
			industryService.getIndustryDTOsForProfile(profile.getId()),
			"id"
		);
		assertTrue(Sets.newHashSet(loadedProfileIndustryIds).equals(Sets.newHashSet(industryIds3)));
		assertFalse(Sets.newHashSet(loadedProfileIndustryIds).equals(Sets.newHashSet(industryIds2)));
		assertFalse(Sets.newHashSet(loadedProfileIndustryIds).equals(Sets.newHashSet(industryIds1)));
	}

	@Test
	public void saveOnboardProfile_ifIndustriesSelected_defaultToNONE() throws Exception {
		User contractor = newContractorIndependent();
		Profile profile = profileService.findProfile(contractor.getId());
		WorkerOnboardingDTO dto = new WorkerOnboardingDTO();
		OnboardingIndustryDTO industryDTO = new OnboardingIndustryDTO();
		industryDTO.setChecked(false);
		industryDTO.setId(INDUSTRY_1000_ID);
		industryDTO.setName("General Motors Inc.");
		dto.setIndustries(Lists.newArrayList(industryDTO));

		profileService.saveOnboardProfile(profile.getUser().getId(), profile.getId(), contractor.getCompany(), dto, true);
		profile = profileService.findProfile(contractor.getId());
		Long[] industryIds = {Industry.NONE.getId()};
		List<Long> loadedProfileIndustryIds = CollectionUtilities.newListPropertyProjection(
			industryService.getIndustryDTOsForProfile(profile.getId()),
			"id"
		);
		assertTrue(Sets.newHashSet(loadedProfileIndustryIds).equals(Sets.newHashSet(industryIds)));
	}


	@Test
	public void suspendCompany_success() throws Exception {
		User user = newFirstEmployeeWithCashBalance();

		Company company = companyService.findCompanyById(user.getCompany().getId());
		assertEquals(company.getCompanyStatusType().getCode(), CompanyStatusType.ACTIVE);
		for (User u : userService.findAllActiveEmployees(company.getId())) {
			assertFalse(authenticationService.isSuspended(u));
		}
		profileService.suspendCompany(company.getId(), "comments");
		company = companyService.findCompanyById(user.getCompany().getId());
		assertEquals(company.getCompanyStatusType().getCode(), CompanyStatusType.SUSPENDED);
		for (User u : userService.findAllActiveEmployees(company.getId())) {
			assertTrue(authenticationService.isSuspended(u));
		}
		CommentPagination pagination = new CommentPagination();
		pagination = commentService.findAllActiveClientServiceCompanyComments(company.getId(), pagination);
		assertNotNull(pagination.getResults());
	}

	@Test
	public void getProjectionMapByUserId_fetchCorrectAfterUpdate() throws Exception {
		User contractor = newContractorIndependent();
		Map<String, Object> props = profileService.getProjectionMapByUserNumber(contractor.getUserNumber(), "onboardCompleted");

		String assertCondition = "false".equals(props.get("onboardCompleted").toString()) ? "true" : "false";
		String toggle = "false".equals(props.get("onboardCompleted").toString()) ? "1" : "0";

		profileService.updateProfileProperties(contractor.getId(), ImmutableMap.of("onboardCompleted", toggle));
		redisAdapter.delete(contractor.getId() + "-onboardCompleted");
		props = profileService.getProjectionMapByUserNumber(contractor.getUserNumber(), "onboardCompleted");
		assertTrue(assertCondition.equals(props.get("onboardCompleted").toString()));
	}

	@Test
	public void profileService_updateProfileProperties_shouldSaveCorrectOnboardVideoWatchedOnDate() throws Exception {
		User contractor = newContractorIndependent();
		Date date = Calendar.getInstance().getTime();

		profileService.updateProfileProperties(contractor.getId(), ImmutableMap.of("onboardVideoWatchedOn", date.toString()));
		Map<String, Object> props = profileService.getProjectionMapByUserNumber(contractor.getUserNumber(), "onboardVideoWatchedOn");
		assertTrue(((Calendar) props.get("onboardVideoWatchedOn")).getTime().toString().equals(date.toString()));
	}

	@Test
	public void setBlacklistedZipcodesForUser_withArrayArg__methodCallDoesCacheEvict() throws Exception {
		User worker = newContractor();

		profileService.findProfileDTO(worker.getId());
		assertTrue(isCached(worker.getId()));

		profileService.setBlacklistedZipcodesForUser(worker.getId(), new String[]{"12345"});
		assertFalse(isCached(worker.getId()));
	}

	@Test
	public void setBlacklistedZipcodesForUser_withListArg_methodCallDoesCacheEvict() throws Exception {
		User worker = newContractor();

		profileService.findProfileDTO(worker.getId());
		assertTrue(isCached(worker.getId()));

		profileService.setBlacklistedZipcodesForUser(worker.getId(), Lists.newArrayList("12345"));
		assertFalse(isCached(worker.getId()));
	}

	@Test
	public void closeCsrProfileLead_methodCallDoesCacheEvict() throws Exception {
		User worker = newContractor();

		profileService.findProfileDTO(worker.getId());
		assertTrue(isCached(worker.getId()));

		profileService.closeCsrProfileLead(worker.getId());
		assertFalse(isCached(worker.getId()));
	}

	@Test
	public void openCsrProfileLead_methodCallDoesCacheEvict() throws Exception {
		User worker = newContractor();

		profileService.findProfileDTO(worker.getId());
		assertTrue(isCached(worker.getId()));

		profileService.openCsrProfileLead(worker.getId());
		assertFalse(isCached(worker.getId()));
	}

	@Test
	public void updateProfileProperties_methodCallDoesCacheEvict() throws Exception {
		User worker = newContractor();

		profileService.findProfileDTO(worker.getId());
		assertTrue(isCached(worker.getId()));

		profileService.updateProfileProperties(worker.getId(), Maps.<String, String>newHashMap());
		assertFalse(isCached(worker.getId()));
	}

	@Test
	public void updateProfileAddressProperties_methodCallDoesCacheEvict() throws Exception {
		User worker = newContractor();

		profileService.findProfileDTO(worker.getId());
		assertTrue(isCached(worker.getId()));

		profileService.updateProfileAddressProperties(worker.getId(), makeAddressProperties());
		assertFalse(isCached(worker.getId()));
	}

	@Test
	public void saveOrUpdateProfileLanguage_methodCallDoesCacheEvict() throws Exception {
		User worker = newContractor();

		profileService.findProfileDTO(worker.getId());
		assertTrue(isCached(worker.getId()));

		ProfileLanguageDTO profileLanguageDTO = new ProfileLanguageDTO();
		profileLanguageDTO.setLanguageId(1L);
		profileLanguageDTO.setLanguageProficiencyTypeCode(LanguageProficiencyType.BASIC);
		profileService.saveOrUpdateProfileLanguage(worker.getId(), profileLanguageDTO);
		assertFalse(isCached(worker.getId()));
	}

	@Test
	public void saveOrUpdateProfile_methodCallDoesCacheEvict() throws Exception {
		User worker = newContractor();

		profileService.findProfileDTO(worker.getId());
		assertTrue(isCached(worker.getId()));

		profileService.saveOrUpdateProfile(profileService.findProfile(worker.getId()));
		assertFalse(isCached(worker.getId()));
	}

	private boolean isCached(Long userId) {
		return redisAdapter.get(RedisConfig.PROFILE + userId).isPresent();
	}
}
