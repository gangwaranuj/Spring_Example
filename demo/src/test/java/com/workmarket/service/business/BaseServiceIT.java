package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.workmarket.api.ExpectApiV3Support;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.features.FeatureEvaluatorConfiguration;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeliverableRequirementGroup;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.AccountingPricingServiceTypeSummary;
import com.workmarket.domains.model.account.AccountingSummary;
import com.workmarket.domains.model.account.CreditDebitRegisterTransactionsSummary;
import com.workmarket.domains.model.account.OfflinePaymentSummary;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AbstractItem;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.domains.model.network.Network;
import com.workmarket.domains.model.option.CompanyOption;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tool.Tool;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.facade.service.WorkFacadeService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.dto.AddressDTO;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.business.account.JournalEntrySummaryService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.dto.AssessmentChoiceDTO;
import com.workmarket.service.business.dto.AssessmentDTO;
import com.workmarket.service.business.dto.AssessmentItemDTO;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.ClientCompanyDTO;
import com.workmarket.service.business.dto.ClientContactDTO;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.dto.InsuranceDTO;
import com.workmarket.service.business.dto.InvitationUserRegistrationDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.ManageMyWorkMarketDTO;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.ProjectDTO;
import com.workmarket.service.business.dto.RecruitingCampaignDTO;
import com.workmarket.service.business.dto.ScreeningDTO;
import com.workmarket.service.business.dto.SkillDTO;
import com.workmarket.service.business.dto.TaxEntityDTO;
import com.workmarket.service.business.dto.ToolDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkCustomFieldGroupDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.service.business.dto.WorkTemplateDTO;
import com.workmarket.service.business.pay.PaymentSummaryService;
import com.workmarket.service.business.registration.RegistrationServiceFacade;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.network.AclNetworkRoleAssociationService;
import com.workmarket.service.network.NetworkService;
import com.workmarket.service.option.OptionsService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.thrift.core.Industry;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.AcceptWorkOfferRequest;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.RandomUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@WebAppConfiguration
@ContextConfiguration(locations = "classpath:spring/application-context-test.xml")
@Ignore
public class BaseServiceIT extends ExpectApiV3Support {

	protected final Log logger = LogFactory.getLog(BaseServiceIT.class);

	@Autowired protected WebRequestContextProvider webRequestContextProvider;
	@Autowired protected AssessmentService assessmentService;
	@Autowired protected AddressService addressService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	protected AccountRegisterService accountRegisterService;
	@Autowired @Qualifier("accountRegisterServicePaymentTermsImpl")
	protected AccountRegisterService accountRegisterServicePaymentTerms;
	@Autowired protected AuthenticationService authenticationService;
	@Autowired protected CRMService crmService;
	@Autowired protected LicenseService licenseService;
	@Autowired protected CertificationService certificationService;
	@Autowired protected ContractService contractService;
	@Autowired protected InsuranceService insuranceService;
	@Autowired protected RegistrationService registrationService;
	@Autowired protected RegistrationServiceFacade registrationServiceFacade;
	@Autowired protected RecruitingService recruitingService;
	@Autowired protected ProfileService profileService;
	@Autowired protected TaxService taxService;
	@Autowired protected UserService userService;
	@Autowired protected ScreeningService screeningService;
	@Autowired protected WorkService workService;
	@Autowired protected WorkFacadeService workFacadeService;
	@Autowired protected WorkRoutingService workRoutingService;
	@Autowired protected RoutingStrategyService routingStrategyService;
	@Autowired protected TagService tagService;
	@Autowired protected ToolService toolService;
	@Autowired protected SkillService skillService;
	@Autowired protected SpecialtyService specialtyService;
	@Autowired protected UserGroupService userGroupService;
	@Autowired protected ProjectService projectService;
	@Autowired protected InvariantDataService invariantDataService;
	@Autowired protected LaneService laneService;
	@Autowired protected CompanyService companyService;
	@Autowired protected SubscriptionService subscriptionService;
	@Autowired protected PaymentSummaryService paymentSummaryService;
	@Autowired protected BillingService billingService;
	@Autowired protected PricingService pricingService;
	@Autowired protected AccountPricingService accountPricingService;
	@Autowired protected AssetManagementService assetManagementService;
	@Autowired protected WorkSearchService workSearchService;
	@Autowired protected WorkBundleService workBundleService;
	@Autowired protected CustomFieldService customFieldService;
	@Autowired protected WorkSubStatusService workSubStatusService;
	@Autowired protected NetworkService networkService;
	@Autowired protected TWorkService tWorkService;
	@Autowired protected DeliverableService deliverableService;
	@Autowired @Qualifier("companyOptionsService") protected OptionsService<Company> companyOptionsService;
	@Autowired protected AclRoleService aclRoleService;
	@Autowired protected AclNetworkRoleAssociationService aclNetworkRoleAssociationService;
	@Autowired protected WorkTemplateService workTemplateService;
	@Autowired protected JournalEntrySummaryService journalEntrySummaryService;

	// IMPORTANT: if you override this method use a different name than "init" otherwise this doesn't get called
	@Before
	public void init() {
		User currentUser = userService.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);
		authenticationService.setCurrentUser(currentUser);
	}

	@After
	public void tearDown() {
		webRequestContextProvider.clear();
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	protected static final String STORAGE_TEST_FILE = "/tmp/storageTest";
	protected static final String IMAGE_TEST_FILE = "/tmp/tmpPNGImage.png";

	public static final int JMS_DELAY = 60000;

	public static final Long ANONYMOUS_USER_ID = 1L;
	public static final Long FRONT_END_USER_ID = 2L;

	public static final Long ADDRESS_ID = 1L;

	public static final String ADDRESS_20_WEST_20 = "20 West 20th Street, New York, NY 10011";
	public static final String ADDRESS_200_FULTON = "200 Fulton Street, New York, NY 10007";

	public static final Long CONTRACTOR_USER_ID = 1L;

	public static final Long EMPLOYEE_USER_ID = 1L;

	public static final Long USER_GROUP_ID = 1L;

	public static final Long COMPANY_ID = 1L;

	public static final Long TAG_ID = 1L;

	public static final Long ASSESSMENT_ID = 1L;

	public static final Long LICENSE_ID = 1000L;

	public static final Long CERTIFICATION_ID = 1000L;
	public static final Long CERTIFICATION_TYPE_ID = 1000L;

	public static final Long CERTIFICATION_VENDOR_ID = 1000L;

	public static final Long INSURANCE_ID = 1000L;

	public static final Long WORK_ID = 1L;
	public static final Long WORK_2_ID = 2L;

	public static final Long PROJECT_ID = 1L;

	public static final Long INDUSTRY_ID = 1000L;
	public static final Long INDUSTRY_ID_1000 = 1000L;
	public static final Long INDUSTRY_ID_1004 = 1004L;

	public static final String LICENSE_NUMBER = "0000";
	public static final String CERTIFICATION_NUMBER = "012546HJ";

	public static final String UPDATE_CERTIFICATION_NAME = "Update certification Name";

	public static final Long CAMPAIGN_RECRUITING_DIRTY_11_ID = 11L;

	public static final String RECRUITING_VENDOR_1_CODE = "other";

	public static final Long INDUSTRY_1_ID = 1L;

	public static final Long INDUSTRY_1000_ID = 1000L;
	public static final Long ACL_ROLE_USER = 3L;
	public static final Long ACL_ROLE_WORKER = 6L;
	public static final Long ACL_ROLE_SHARED_WORKER = 7L;

	public static final Long CLIENT_COMPANY_ID = 1L;
	public static final Long CLIENT_LOCATION_ID = 1L;
	public static final Long CLIENT_CONTACT_ID = 1L;

	public static final Long FLAT_PRICING_STRATEGY = 1L;
	public static final Long WORK_CUSTOM_FIELD_1_ID = 1L;
	public static final Long WORK_CUSTOM_FIELD_2_ID = 2L;

	public static final Integer ZERO = 0;
	public static final Integer ONE = 1;
	public static final Integer TWO = 2;
	public static final Integer THREE = 3;
	public static final Integer FOUR = 4;

	public static final String DEFAULT_CASH_AMOUNT = "5000.00";
	public static final double DEFAULT_WORK_FLAT_PRICE = 100.00;

	public static final String VALID_DISCOVER_CARD = "6011000993010978";
	public static final String VALID_VISA_CARD = "4111111111111111";

	public static final Long LOCATION_ID = 1L;

	protected String generatedPassword;

	@Autowired protected ResourceLoader resourceLoader;
	@Autowired protected EmailTemplateFactory emailTemplateFactory;
	@Autowired protected NotificationTemplateFactory notificationTemplateFactory;
	@Autowired protected FeatureEvaluatorConfiguration featureEvaluatorConfiguration;

	public void setFeatureToggle(String toggle, Boolean isSet) throws IOException {
		featureEvaluatorConfiguration.put(toggle, null, isSet.toString());
	}

	public void setCompanyFeatureToggle(String toggle, long companyId) throws IOException {
		featureEvaluatorConfiguration.put(toggle, "companyId", String.valueOf(companyId));
	}

	public void initializeTestFile(String uniqueId) throws IOException {
		File testFile = new File(STORAGE_TEST_FILE + uniqueId);

		if (testFile.exists()) {
			testFile.delete();
		}

		FileWriter fw = new FileWriter(STORAGE_TEST_FILE + uniqueId);
		fw.write("1234567890");
		fw.close();

		File tmpPNGImage = new File(IMAGE_TEST_FILE + uniqueId);
		if (tmpPNGImage.exists()) {
			tmpPNGImage.delete();
		}
	}

	public void initializeTestFile() throws Exception {
		initializeTestFile(RandomUtilities.generateAlphaNumericString(10));
	}

	public void deleteTestFile(String uniqueId) throws Exception {
		File testFile = new File(STORAGE_TEST_FILE + uniqueId);

		if (testFile.exists()) {
			testFile.delete();
		}

		File tmpPNGImage = new File(IMAGE_TEST_FILE + uniqueId);
		if (tmpPNGImage.exists()) {
			tmpPNGImage.delete();
		}
	}

	public User newRegisteredWorker() throws Exception {
		String userName = "employee" + RandomUtilities.generateAlphaString(10);

		InvitationUserRegistrationDTO dto = new InvitationUserRegistrationDTO();

		dto.setFirstName("firstname" + userName);
		dto.setLastName("lastname" + userName);
		dto.setCompanyName("comanyname" + userName);
		dto.setPostalCode("10010");
		dto.setWorkPhone("212-" + RandomUtilities.generateNumericString(3) + "-" + RandomUtilities.generateNumericString(4));
		dto.setEmail(userName + "@workmarket.com");
		dto.setFindWork(false);
		dto.setManageWork(true);
		dto.setLongitude(new BigDecimal("-73.992325"));
		dto.setLatitude(new BigDecimal("40.740075"));
		dto.setPassword(getAndGeneratePassword());
		dto.setIndustryId(INDUSTRY_ID);
		dto.setCity("somecity");
		dto.setState("NY");
		dto.setCountry("USA");
		dto.setAgree("1");
		dto.setOperatingAsIndividualFlag(true);
		dto.setNotifyUser(false);

		return registrationService.registerWorker(dto);
	}

	public User newUnconfirmedContractor() throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName("lastname" + RandomUtilities.generateAlphaString(10));
		userDTO.setPassword(getAndGeneratePassword());

		Company company = newCompany();
		return registrationService.registerNewForCompany(userDTO, company.getId());
	}

	public User newContractor() throws Exception {
		User user = newUnconfirmedContractor();
		registrationService.confirmAccount(user.getId());
		return user;
	}

	public User newContractorIndependent() throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName("firstname" + RandomUtilities.generateAlphaString(10));
		userDTO.setLastName("lastname" + RandomUtilities.generateAlphaString(10));
		userDTO.setPassword(getAndGeneratePassword());
		User user = registrationServiceFacade.registerNew(userDTO, null);
		profileService.updateProfileIndustries(user.getProfile().getId(), new Long[]{INDUSTRY_ID, INDUSTRY_1000_ID});
		return user;
	}

	public User newContractorIndependentlane4Ready() throws Exception {
		User contractor = newContractorIndependent();
		profileService.updateProfileAddress(contractor.getProfile().getId(), createAndSaveAddress().getId());
		authenticationService.assignAclRoleToUser(contractor.getId(), ACL_ROLE_SHARED_WORKER);
		authenticationService.approveUser(contractor.getId());
		registrationService.confirmAccount(contractor.getId());

		return contractor;
	}

	public User newContractorIndependentLane4ReadyWithCashBalance() throws Exception {
		User contractor = newContractorIndependent();

		contractor.getProfile().setAddressId(createAndSaveAddress().getId());

		profileService.saveOrUpdateProfile(contractor.getProfile());

		authenticationService.assignAclRoleToUser(contractor.getId(), ACL_ROLE_SHARED_WORKER);
		authenticationService.approveUser(contractor.getId());
		registrationService.confirmAccount(contractor.getId());

		accountRegisterService.addFundsToRegisterFromWire(contractor.getCompany().getId(), "100.00");

		return contractor;
	}

	public User newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(boolean einFlag) throws Exception {
		return newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(einFlag, false);
	}

	public User newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(boolean einFlag, boolean randomAddress) throws Exception {
		User contractor = newContractorIndependent();

		if (randomAddress) {
			contractor.getProfile().setAddressId(newAddressRandom().getId());
		} else {
			contractor.getProfile().setAddressId(createAndSaveAddress().getId());
		}

		profileService.saveOrUpdateProfile(contractor.getProfile());

		authenticationService.assignAclRoleToUser(contractor.getId(), ACL_ROLE_SHARED_WORKER);
		authenticationService.approveUser(contractor.getId());
		registrationService.confirmAccount(contractor.getId());

		accountRegisterService.addFundsToRegisterFromWire(contractor.getCompany().getId(), "100.00");

		TaxEntityDTO taxEntityDTO = new TaxEntityDTO();

		taxEntityDTO.setAddress("20 West 20th Street");
		taxEntityDTO.setCity("New York");
		taxEntityDTO.setState("NY");
		taxEntityDTO.setPostalCode("10011");
		taxEntityDTO.setCountry("USA");
		taxEntityDTO.setTaxCountry(AbstractTaxEntity.COUNTRY_USA);
		taxEntityDTO.setBusinessFlag(einFlag);
		taxEntityDTO.setTaxEntityTypeCode(TaxEntityType.CORP);
		taxEntityDTO.setTaxName(RandomUtilities.generateAlphaString(10));
		taxEntityDTO.setTaxNumber(UUID.randomUUID().toString().substring(0, 24));
		taxEntityDTO.setActiveFlag(true);
		taxEntityDTO.setTaxVerificationStatusCode(TaxVerificationStatusType.APPROVED);
		authenticationService.setCurrentUser(contractor.getId());
		taxService.saveTaxEntityForCompany(contractor.getCompany().getId(), taxEntityDTO);

		return contractor;
	}

	public UserDTO newContractorDTO() {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword(getAndGeneratePassword());
		return userDTO;
	}

	public User newWMEmployee() throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword(getAndGeneratePassword());
		User employee = registrationService.registerNewInternalUser(userDTO);
		registrationService.confirmAccount(employee.getId());
		return employee;
	}

	public User newEmployeeWithCashBalance(String amount) throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword(getAndGeneratePassword());
		User employee = registrationServiceFacade.registerNew(userDTO, null);

		Company company = profileService.findCompany(employee.getId());
		company.setName(RandomUtilities.generateNumericString(10));
		profileService.saveOrUpdateCompany(company);
		registrationService.confirmAccount(employee.getId());

		employee = userService.findUserById(employee.getId());
		accountRegisterService.addFundsToRegisterFromWire(employee.getCompany().getId(), amount);

		return employee;
	}

	public User newEmployeeWithCashBalance() throws Exception {
		return newEmployeeWithCashBalance(DEFAULT_CASH_AMOUNT);
	}

	public User newInternalUser() throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "internal" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName("lastname" + RandomUtilities.generateAlphaString(10));
		userDTO.setPassword(getAndGeneratePassword());
		return registrationService.registerNewInternalUser(userDTO);
	}

	public User newFirstEmployeeWithCashBalance() throws Exception {
		return newFirstEmployeeWithCashBalance(DEFAULT_CASH_AMOUNT);
	}

	public User newFirstEmployeeWithCashBalance(String cash) throws Exception {
		User employee = newFirstEmployeeWithAPLimit();
		accountRegisterService.addFundsToRegisterFromWire(employee.getCompany().getId(), cash);
		return employee;
	}

	public User newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled() throws Exception {
		return newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled(30, DEFAULT_CASH_AMOUNT);
	}

	public User newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled(int paymentTerms, String defaultCash) throws Exception {
		User employee = newFirstEmployeeWithAPLimit();
		accountRegisterService.addFundsToRegisterFromWire(employee.getCompany().getId(), defaultCash);
		ManageMyWorkMarketDTO companyDTO = new ManageMyWorkMarketDTO();
		companyDTO.setPaymentTermsDays(paymentTerms);
		companyDTO.setPaymentTermsEnabled(true);
		profileService.updateManageMyWorkMarket(employee.getCompany().getId(), companyDTO);
		return employee;
	}

	public User newFirstEmployeeWithNOCashBalanceAndPaymentTerms() throws Exception {
		return newFirstEmployeeWithNOCashBalanceAndPaymentTerms(30);
	}

	public User newFirstEmployeeWithNOCashBalanceAndPaymentTerms(int paymentTerms) throws Exception {
		User employee = newFirstEmployeeWithAPLimit();
		ManageMyWorkMarketDTO companyDTO = new ManageMyWorkMarketDTO();
		companyDTO.setPaymentTermsDays(paymentTerms);
		companyDTO.setPaymentTermsEnabled(true);
		profileService.updateManageMyWorkMarket(employee.getCompany().getId(), companyDTO);
		return employee;
	}

	public User newFirstEmployeeWithMboEnabled() throws Exception {
		User employee = newFirstEmployeeWithAPLimit();
		ManageMyWorkMarketDTO companyDTO = new ManageMyWorkMarketDTO();
		profileService.updateManageMyWorkMarket(employee.getCompany().getId(), companyDTO);
		Company company = employee.getCompany();
		companyOptionsService.setOption(company, CompanyOption.MBO_ENABLED, "true");
		companyOptionsService.setOption(company, CompanyOption.MBO_REQUIRED, "true");
		return employee;
	}

	public User newFirstEmployeeWithAPLimit() throws Exception {
		User employee = newFirstEmployee();
		accountRegisterService.updateApLimit(employee.getCompany().getId(), BigDecimal.valueOf(1000));
		return employee;
	}

	private String getAndGeneratePassword() {
		if (generatedPassword == null) {
			generatedPassword = RandomUtilities.generateAlphaString(10);
		}
		return generatedPassword;
	}

	public User newFirstEmployee() throws Exception {
		String userName = "employee" + RandomUtilities.generateAlphaString(10);

		InvitationUserRegistrationDTO dto = new InvitationUserRegistrationDTO();
		dto.setFirstName("firstname" + userName);
		dto.setLastName("lastname" + userName);
		dto.setCompanyName("comanyname" + userName);
		dto.setPostalCode("10010");
		dto.setWorkPhone("212-" + RandomUtilities.generateNumericString(3) + "-" + RandomUtilities.generateNumericString(4));
		dto.setEmail(userName + "@workmarket.com");
		dto.setFindWork(false);
		dto.setManageWork(true);
		dto.setLongitude(new BigDecimal("-73.992325"));
		dto.setLatitude(new BigDecimal("40.740075"));
		dto.setPassword(getAndGeneratePassword());
		dto.setIndustryId(INDUSTRY_ID);
		dto.setCity("somecity");
		dto.setState("NY");
		dto.setCountry("USA");
		User employee = registrationServiceFacade.registerUserSimple(dto, true);
		registrationService.confirmAccount(employee.getId());
		return employee;
	}

	public User newFirstEmployeeWithCustomSpendingLimitAndCashBalance(String spendingLimit, String cash) throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled(7, cash);
		employee = userService.getUser(employee.getId());
		employee.setSpendLimit(new BigDecimal(spendingLimit));
		userService.saveOrUpdateUser(employee);
		return employee;
	}

	public User newEmployeeWithCampaign(long campaignId) throws Exception {
		String userName = "employee" + RandomUtilities.generateAlphaString(10);

		InvitationUserRegistrationDTO dto = new InvitationUserRegistrationDTO();
		dto.setFirstName("firstname" + userName);
		dto.setLastName("lastname" + userName);
		dto.setCompanyName("comanyname" + userName);
		dto.setPostalCode("10010");
		dto.setWorkPhone("212-" + RandomUtilities.generateNumericString(3) + "-" + RandomUtilities.generateNumericString(4));
		dto.setEmail(userName + "@workmarket.com");
		dto.setFindWork(false);
		dto.setManageWork(true);
		dto.setLongitude(new BigDecimal("-73.992325"));
		dto.setLatitude(new BigDecimal("40.740075"));
		dto.setPassword(getAndGeneratePassword());
		dto.setIndustryId(INDUSTRY_ID);
		dto.setCity("somecity");
		dto.setState("NY");
		dto.setCountry("USA");
		dto.setCampaignId(campaignId);

		User employee = registrationService.registerUserSimple(dto, true);
		registrationService.confirmAccount(employee.getId());

		return employee;
	}

	public User newCompanyEmployee(Long companyId) throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword(getAndGeneratePassword());
		User employee = registrationService.registerNewForCompany(userDTO, companyId);
		registrationService.confirmAccount(employee.getId());

		return employee;
	}

	public User newCompanyEmployeeStaffConfirmed(Long companyId) throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword(getAndGeneratePassword());
		User employee = registrationService.registerNewForCompany(userDTO, companyId, new Long[]{AclRole.ACL_STAFF});
		registrationService.confirmAccount(employee.getId());

		return employee;
	}

	public User newCompanyEmployeeDispatcherConfirmed(Long companyId) throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword(getAndGeneratePassword());
		User employee = registrationService.registerNewForCompany(userDTO, companyId, new Long[]{AclRole.ACL_STAFF, AclRole.ACL_DISPATCHER});
		registrationService.confirmAccount(employee.getId());

		return employee;
	}

	public User newCompanyEmployeeWorkerConfirmed(Long companyId) throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword(getAndGeneratePassword());
		User employee = registrationService.registerNewForCompany(userDTO, companyId, new Long[]{AclRole.ACL_STAFF, AclRole.ACL_WORKER});
		registrationService.confirmAccount(employee.getId());

		return employee;
	}

	public User newCompanyEmployeeSharedWorkerConfirmed(Long companyId) throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword(getAndGeneratePassword());
		User employee = registrationService.registerNewForCompany(userDTO, companyId, new Long[]{AclRole.ACL_STAFF, AclRole.ACL_SHARED_WORKER, AclRole.ACL_WORKER});
		registrationService.confirmAccount(employee.getId());

		employee = userService.findUserById(employee.getId());
		userService.updateLane3ApprovalStatus(employee.getId(), ApprovalStatus.APPROVED);
		userService.updateUserStatus(employee.getId(), new UserStatusType(UserStatusType.APPROVED));

		return employee;
	}

	public User newCompanyEmployeeSharedWorkerApproved(Long companyId) throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword(getAndGeneratePassword());
		User employee = registrationService.registerNewForCompany(userDTO, companyId, new Long[]{AclRole.ACL_STAFF, AclRole.ACL_SHARED_WORKER, AclRole.ACL_WORKER});
		// with ACL_SHARED_WORKER role, the user's UserStatusType and Lane3ApprovalStatus are all set to "pending"
		// the following two lines turn those two bits to "approved" so the employee is searchable
		authenticationService.approveUser(employee.getId());
		userService.updateLane3ApprovalStatus(employee.getId(), ApprovalStatus.APPROVED);
		registrationService.confirmAccount(employee.getId());

		return employee;
	}

	public Work newWorkWithAutoPay(Long employeeId) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(today().toString());
		workDTO.setPaymentTermsEnabled(true);
		workDTO.setPaymentTermsDays(0);
		workDTO.setAutoPayEnabled(true);

		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}


	public Work newWorkForEmployeeWithActiveResource(Long buyerId, User contractor) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(today().toString());
		workDTO.setIndustryId(INDUSTRY_ID);

		Work work = null;
		try {
			work = workFacadeService.saveOrUpdateWork(buyerId, workDTO);
			workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
			workService.acceptWork(contractor.getId(), work.getId());
		} catch (Exception e) {
			logger.error("Error in newWorkForEmployeeWithActiveResource", e);
		}

		return work;
	}

	public Work newWork(Long employeeId, WorkDTO workDTO) {
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(Calendar.getInstance()));
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setBuyerSupportUserId(employeeId);

		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}

	public Work newWork(Long employeeId, WorkDTO workDTO, Calendar scheduleTime) {
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(scheduleTime));
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setBuyerSupportUserId(employeeId);
		workDTO.setCheckinRequired(true);
		workDTO.setWorkStatusTypeCode(WorkStatusType.ACTIVE);
		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}

	public Work newWorkWithDateRange(Long employeeId, WorkDTO workDTO, Calendar scheduleTimeFrom, Calendar scheduleTimeThrough) {
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(true);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(scheduleTimeFrom));
		workDTO.setScheduleThroughString(DateUtilities.getISO8601(scheduleTimeThrough));
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setBuyerSupportUserId(employeeId);
		workDTO.setCheckinRequired(true);
		workDTO.setWorkStatusTypeCode(WorkStatusType.ACTIVE);
		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}

	public Work newHourlyWork(Long employeeId, WorkDTO workDTO, Calendar scheduleTimeFrom, Double perHourPrice, Double hours) {
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new PerHourPricingStrategy()).getId());
		workDTO.setPerHourPrice(perHourPrice);
		workDTO.setMaxNumberOfHours(hours);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(scheduleTimeFrom));
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setBuyerSupportUserId(employeeId);
		workDTO.setCheckinRequired(true);
		workDTO.setWorkStatusTypeCode(WorkStatusType.ACTIVE);
		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}

	public Work newInternalWork(Long employeeId) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new InternalPricingStrategy()).getId());
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(Calendar.getInstance()));
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setBuyerSupportUserId(employeeId);

		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}

	public Work newWork(Long employeeId) {
		return newWork(employeeId, new WorkDTO());
	}

	public Work newWork(Long employeeId, Calendar scheduleTime) {
		return newWork(employeeId, new WorkDTO(), scheduleTime);
	}


	public com.workmarket.thrift.work.Work newThriftWork(User user) throws ValidationException {
		return tWorkService.saveOrUpdateWorkDraft(newWorkSaveRequest(user)).getWork();
	}

	public WorkSaveRequest newWorkSaveRequest(User user) {
		authenticationService.setCurrentUser(user);

		com.workmarket.thrift.work.Work work = new com.workmarket.thrift.work.Work()
			.setTitle("Do Work!" + RandomUtilities.nextLong())
			.setDescription("Description of work")
			.setBuyer(new com.workmarket.thrift.core.User()
				.setId(user.getId())
				.setCompany(new com.workmarket.thrift.core.Company()
					.setId(user.getCompany().getId())))
			.setOffsiteLocation(true)
			.setConfiguration(new ManageMyWorkMarket())
			.setIndustry(new Industry(INDUSTRY_ID, "industryName"))
			.setPricing(new PricingStrategy()
				.setId(1L)
				.setType(PricingStrategyType.FLAT)
				.setFlatPrice(10.0))
			.setSchedule(new Schedule()
				.setFrom(DateUtilities.getCalendarNowUtc().getTimeInMillis())
				.setRange(false));

		return new WorkSaveRequest()
			.setUserId(user.getId())
			.setWork(work);
	}

	public Work newWorkWithApplyEnabled(Long employeeId) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(10.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(today().toString());
		workDTO.setAssignToFirstResource(false);

		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}


	public Work newWorkWithPaymentTerms(Long employeeId, Integer paymentTermsDays) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(today().toString());
		workDTO.setPaymentTermsDays(paymentTermsDays);
		workDTO.setPaymentTermsEnabled(true);
		workDTO.setIndustryId(INDUSTRY_ID);
		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}

	public Work newWorkWithOfflinePayment(Long employeeId) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(today().toString());
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setOfflinePayment(true);

		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}

	public Work newWorkWithPaymentTerms(Long employeeId, Integer paymentTermsDays, Calendar scheduleTime) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(scheduleTime));
		workDTO.setCheckinRequired(true);
		workDTO.setPaymentTermsDays(paymentTermsDays);
		workDTO.setPaymentTermsEnabled(true);
		workDTO.setIndustryId(INDUSTRY_ID);
		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}
	
	public Work newWorkWithProject(Long employeeId, String projectName){
		Work work = newWork(employeeId);

		ClientCompany clientCompany = newClientCompany(employeeId);
		Project project = newProject(employeeId, clientCompany.getId(), projectName);
		work.setProject(project);
		workService.saveOrUpdateWork(work);
		return work;
	}

	public Work newWorkOnSiteWithLocationAndDate(Long employeeId, String scheduleFrom, String scheduleThrough) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(true);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(scheduleFrom);
		if (scheduleThrough != null) {
			workDTO.setIsScheduleRange(true);
			workDTO.setScheduleThroughString(scheduleThrough);
		} else {
			workDTO.setIsScheduleRange(false);
		}

		Location location = createAndSaveLocation(userService.findUserById(employeeId).getCompany().getId());

		workDTO.setLocationId(location.getId());
		workDTO.setIndustryId(INDUSTRY_ID);

		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}

	public Work newWorkOnSiteWithLocation(Long employeeId) throws Exception {
		return newWorkOnSiteWithLocationAndDate(employeeId, today(DateTimeZone.forID(Constants.WM_TIME_ZONE)).toString(), null);
	}

	public Work newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(Long employeeId) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(true);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(today().toString());
		workDTO.setLocationId(LOCATION_ID);
		workDTO.setResourceConfirmationRequired(true);
		workDTO.setCheckinRequired(true);
		workDTO.setIndustryId(INDUSTRY_ID);
		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}


	public Work newWorkOnSiteWithLocationWithRequiredCheckin(Long employeeId) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(true);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(today().toString());
		workDTO.setLocationId(LOCATION_ID);
		workDTO.setCheckinRequired(true);
		workDTO.setIndustryId(INDUSTRY_ID);
		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}


	public Work newWorkOnSiteWithLocationWithRequiredConfirmation(Long employeeId, String scheduleFromString) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(true);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(scheduleFromString);
		workDTO.setLocationId(LOCATION_ID);
		workDTO.setResourceConfirmationRequired(true);
		workDTO.setResourceConfirmationHours(5d);
		workDTO.setIndustryId(INDUSTRY_ID);
		return workFacadeService.saveOrUpdateWork(employeeId, workDTO);
	}

	public WorkTemplateDTO newWorkTemplateBlankDTO() {
		WorkTemplateDTO workDTO = new WorkTemplateDTO();
		workDTO.setTemplateName("Template" + RandomUtilities.nextLong());
		workDTO.setTemplateDescription("Description" + RandomUtilities.nextLong());
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		return workDTO;
	}

	/**
	 * Create a new Work object with Deliverable Requirements
     */
	public Work newWorkWithDeliverableRequirements(Long employeeId, int numDeliverables, int hoursToComplete){

		Work work = newWork(employeeId);

		// Assign deliverableRequirements to work
		DeliverableRequirementGroupDTO deliverableRequirementGroupDTO = new DeliverableRequirementGroupDTO();
		deliverableRequirementGroupDTO.setHoursToComplete(hoursToComplete);
		List<DeliverableRequirementDTO> deliverableRequirementDTOList = new ArrayList<DeliverableRequirementDTO>();
		int numDeliverableRequirements = numDeliverables;
		for (int i = 0; i < numDeliverableRequirements; i++) {
			DeliverableRequirementDTO deliverableRequirementDTO = new DeliverableRequirementDTO();
			deliverableRequirementDTO.setType("photos");
			deliverableRequirementDTO.setNumberOfFiles(1);
			deliverableRequirementDTOList.add(deliverableRequirementDTO);
		}

		deliverableRequirementGroupDTO.setDeliverableRequirementDTOs(deliverableRequirementDTOList);
		DeliverableRequirementGroup deliverableRequirementGroup =
				deliverableService.saveOrUpdateDeliverableRequirementGroup(deliverableRequirementGroupDTO);
		work.setDeliverableRequirementGroup(deliverableRequirementGroup);

		return work;
	}

	public AssetDTO newAssetDTO() {
		String uniqueId = RandomUtilities.generateAlphaNumericString(10);
		try {
			PrintWriter writer = new PrintWriter(STORAGE_TEST_FILE + uniqueId, "UTF-8");
			writer.println("The first line");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		AssetDTO assetDTO = new AssetDTO();
		assetDTO.setSourceFilePath(STORAGE_TEST_FILE + uniqueId);
		assetDTO.setName("name");
		assetDTO.setDescription("description");
		assetDTO.setMimeType(MimeType.TEXT_PLAIN.getMimeType());
		return assetDTO;
	}

	public AssetDTO newDeliverableAssetDTO(Long deliverableRequirementId, Integer position) {
		AssetDTO assetDTO = newAssetDTO();

		assetDTO.setAssociationType(WorkAssetAssociationType.PHOTOS);
		assetDTO.setDeliverable(true);
		assetDTO.setDeliverableRequirementId(deliverableRequirementId);
		assetDTO.setPosition(position);
		return assetDTO;
	}

	public Asset newAsset() throws IOException, HostServiceException, AssetTransformationException {
		return assetManagementService.storeAssetForUser(newAssetDTO(), ANONYMOUS_USER_ID, true);
	}

	public UserGroup newCompanyUserGroup(Long companyId) {
		UserGroupDTO userGroupDTO = new UserGroupDTO();
		userGroupDTO.setCompanyId(companyId);
		userGroupDTO.setName("group" + RandomUtilities.generateAlphaString(10));
		userGroupDTO.setDescription("description");
		userGroupDTO.setOwnerId(ANONYMOUS_USER_ID);
		UserGroup group = userGroupService.saveOrUpdateCompanyUserGroup(userGroupDTO);

		Assert.assertNotNull(group);

		return group;
	}

	public UserGroup newCompanyUserGroupOpenActive(Long companyId, Long ownerId) {
		UserGroupDTO userGroupDTO = new UserGroupDTO();
		userGroupDTO.setCompanyId(companyId);
		userGroupDTO.setName("group" + RandomUtilities.generateAlphaString(10));
		userGroupDTO.setDescription("description");
		userGroupDTO.setOpenMembership(true);
		userGroupDTO.setActiveFlag(true);
		userGroupDTO.setOwnerId(ownerId);
		UserGroup group = userGroupService.saveOrUpdateCompanyUserGroup(userGroupDTO);

		Assert.assertNotNull(group);
		return group;
	}

	public UserGroup newPublicUserGroup(User employee) {
		UserGroupDTO userGroupDTO = new UserGroupDTO();
		userGroupDTO.setCompanyId(employee.getCompany().getId());
		userGroupDTO.setName("group" + RandomUtilities.generateAlphaString(10));
		userGroupDTO.setDescription("description");
		userGroupDTO.setOwnerId(employee.getId());
		userGroupDTO.setActiveFlag(true);
		userGroupDTO.setOpenMembership(true);
		userGroupDTO.setObjectiveType("work");

		UserGroup group = userGroupService.saveOrUpdateCompanyUserGroup(userGroupDTO);
		Assert.assertNotNull(group);

		return group;

	}

	public UserGroup newPrivateUserGroup(User employee) {

		UserGroupDTO userGroupDTO = new UserGroupDTO();
		userGroupDTO.setCompanyId(employee.getCompany().getId());
		userGroupDTO.setName("group" + RandomUtilities.generateAlphaString(10));
		userGroupDTO.setDescription("description");
		userGroupDTO.setOwnerId(employee.getId());
		userGroupDTO.setActiveFlag(true);
		userGroupDTO.setOpenMembership(false);
		userGroupDTO.setObjectiveType("work");

		UserGroup group = userGroupService.saveOrUpdateCompanyUserGroup(userGroupDTO);
		Assert.assertNotNull(group);

		return group;

	}

	public AbstractAssessment newAssessment() throws Exception {
		return newAssessmentForUser(null, null, true);
	}

	public AbstractAssessment newAssessmentForUser(User owner, com.workmarket.domains.model.Industry industry, boolean isFeatured) throws Exception {
		Long userId = (owner == null ? newContractor().getId() : owner.getId());

		AssessmentDTO assessmentDTO = new AssessmentDTO();
		assessmentDTO.setName("Test #" + RandomUtilities.nextLong());
		assessmentDTO.setDescription("A description.");
		assessmentDTO.setIndustryId((industry == null ? INDUSTRY_1000_ID : industry.getId()));
		assessmentDTO.setPassingScore(100.0);
		assessmentDTO.setAssessmentStatusTypeCode(AssessmentStatusType.ACTIVE);
		assessmentDTO.setFeatured(isFeatured);

		return assessmentService.saveOrUpdateAssessment(userId, assessmentDTO);
	}

	public AbstractAssessment newAssessmentWithItems() throws Exception {
		return newAssessmentWithItemsForUser(null);
	}

	public AbstractAssessment newAssessmentWithItemsForUser(User user) throws Exception {
		AbstractAssessment assessment = newAssessmentForUser(user, null, false);

		AssessmentItemDTO itemDTO1 = new AssessmentItemDTO();
		itemDTO1.setPrompt("What's 1 + 2?");
		itemDTO1.setType(AbstractItem.SINGLE_CHOICE_RADIO);

		List<AbstractItem> answers = assessmentService.saveOrUpdateItemsInAssessment(
			assessment.getId(),
			new AssessmentItemDTO[]{itemDTO1}
		);

		AssessmentChoiceDTO choiceDTO1 = new AssessmentChoiceDTO();
		choiceDTO1.setValue("1");
		choiceDTO1.setIsCorrect(Boolean.FALSE);

		AssessmentChoiceDTO choiceDTO2 = new AssessmentChoiceDTO();
		choiceDTO2.setValue("2");
		choiceDTO2.setIsCorrect(Boolean.FALSE);

		AssessmentChoiceDTO choiceDTO3 = new AssessmentChoiceDTO();
		choiceDTO3.setValue("3");
		choiceDTO3.setIsCorrect(Boolean.TRUE);

		AssessmentChoiceDTO choiceDTO4 = new AssessmentChoiceDTO();
		choiceDTO4.setValue("4");
		choiceDTO4.setIsCorrect(Boolean.FALSE);

		assessmentService.saveOrUpdateChoicesInItem(
			answers.get(0).getId(),
			new AssessmentChoiceDTO[]{choiceDTO1, choiceDTO2, choiceDTO3, choiceDTO4}
		);

		return assessmentService.findAssessment(assessment.getId());
	}

	public AbstractAssessment newAssessmentWithAssetItem() throws Exception {
		AbstractAssessment assessment = newAssessment();

		AssessmentItemDTO itemDTO1 = new AssessmentItemDTO();
		itemDTO1.setPrompt("Show me a picture");
		itemDTO1.setType(AbstractItem.ASSET);

		assessmentService.saveOrUpdateItemsInAssessment(
			assessment.getId(),
			new AssessmentItemDTO[]{itemDTO1}
		);

		return assessmentService.findAssessment(assessment.getId());
	}

	public RecruitingCampaign newRecruitingCampaign(Long companyId, Long groupId) throws Exception {
		RecruitingCampaignDTO dto = new RecruitingCampaignDTO();

		dto.setRecruitingVendorId(RECRUITING_VENDOR_1_CODE);
		dto.setTitle("title " + RandomUtilities.nextLong());
		dto.setDescription("description" + RandomUtilities.nextLong());
		dto.setCompanyOverview("We do good things.");
		dto.setCompanyId(companyId);
		dto.setCompanyUserGroupId(groupId);

		return recruitingService.saveOrUpdateRecruitingCampaign(dto);
	}

	// See LocationServiceImpl.java
	public static AddressDTO createAddressDTO() {
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress1("20 West 20th Street");
		addressDTO.setAddress2("4th Floor");
		addressDTO.setCity("New York");
		addressDTO.setState("NY");
		addressDTO.setCountry(Country.USA);
		addressDTO.setPostalCode("10010");
		addressDTO.setLatitude(new BigDecimal(40.740075));
		addressDTO.setLongitude(new BigDecimal(-73.992325));
		return addressDTO;
	}

	public Address createAddress() {
		Address address = new Address();
		address.setAddress1("20 West 20th Street");
		address.setAddress2("4th Floor");
		address.setCity("New York");
		address.setState(invariantDataService.findState("NY"));
		address.setCountry(Country.USA_COUNTRY);
		address.setPostalCode("10010");
		address.setAddressType(new AddressType("business"));
		address.setLatitude(new BigDecimal(40.740075));
		address.setLongitude(new BigDecimal(-73.992325));

		return address;
	}

	public Address createAndSaveAddress() {
		Address address = createAddress();
		addressService.saveOrUpdate(address);
		return address;
	}

	public Location createAndSaveLocation(Long companyId) {
		LocationDTO locationDTO = new LocationDTO();
		BeanUtilities.copyProperties(locationDTO, createAddressDTO());
		locationDTO.setLocationNumber(RandomUtilities.generateNumericString(6));
		locationDTO.setCompanyId(companyId);
		return crmService.saveOrUpdateClientLocation(null, locationDTO, null);
	}

	Address newAddressRandom() {
		Address address = new Address();
		address.setAddress1("20 West 20th Street");
		address.setAddress2("4th Floor Suite " + RandomUtilities.generateNumericString(5));
		address.setCity(RandomUtilities.generateAlphaString(10));
		address.setState(invariantDataService.findState("NY"));
		address.setCountry(Country.USA_COUNTRY);
		address.setPostalCode("10011");
		address.setAddressType(new AddressType("business"));
		addressService.saveOrUpdate(address);
		return address;
	}

	public Address newAddress200FultonStreetNewYork() {
		Address address = new Address();
		address.setAddress1("200 Fulton Street");
		address.setAddress2("4th Floor");
		address.setCity("New York");
		address.setState(invariantDataService.findState("NY"));
		address.setCountry(Country.USA_COUNTRY);
		address.setPostalCode("10007");
		address.setAddressType(new AddressType("business"));
		address.setLatitude(new BigDecimal(40.711454));
		address.setLongitude(new BigDecimal(-74.010427));
		addressService.saveOrUpdate(address);
		return address;
	}

	public BankAccount newBankAccount(Company company) {
		BankAccount bankAccount = new BankAccount();
		bankAccount.setBankName("CitiBank Business");
		bankAccount.setNameOnAccount("Bank Account");
		bankAccount.setRoutingNumber("021000089");
		bankAccount.setAccountNumber("9983235230");
		bankAccount.setBankAccountType(new BankAccountType(BankAccountType.CHECKING));
		bankAccount.setActiveFlag(Boolean.TRUE);
		bankAccount.setConfirmedFlag(Boolean.FALSE);
		bankAccount.setCompany(company);
		bankAccount.setCountry(Country.USA_COUNTRY);
		return bankAccount;
	}

	public ScreeningDTO newScreeningDTO() {
		ScreeningDTO dto = new ScreeningDTO();
		dto.setFirstName("TestFirst");
		dto.setLastName("Last");
		dto.setMaidenName("MaidenName");
		dto.setWorkIdentificationNumber("111223333");
		dto.setDateOfBirth("1978-11-04");
		dto.setBirthDay(4);
		dto.setBirthMonth(11);
		dto.setBirthYear(1978);
		dto.setEmail("test@example.com");
		dto.setAddress1("20 West 20th Street");
		dto.setAddress2("Suite 402");
		dto.setCity("New York");
		dto.setState("NY");
		dto.setCountry("USA");
		dto.setPostalCode("10011");

		return dto;
	}

	public AbstractAssessment newAssessment(User user) {
		AssessmentDTO assessmentDTO = new AssessmentDTO();
		assessmentDTO.setName("quiz" + RandomUtilities.generateAlphaString(10));
		assessmentDTO.setDescription("A description.");
		assessmentDTO.setIndustryId(INDUSTRY_1000_ID);
		assessmentDTO.setPassingScore(80.0);
		assessmentDTO.setAssessmentStatusTypeCode(AssessmentStatusType.ACTIVE);

		return assessmentService.saveOrUpdateAssessment(user.getId(), assessmentDTO);
	}

	public PaymentDTO newPaymentDTO(String amount) {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setAmount(amount);
		paymentDTO.setCardType("visa");
		paymentDTO.setCardNumber(VALID_VISA_CARD);
		paymentDTO.setCardExpirationDateString("122016");
		paymentDTO.setCardSecurityCode(RandomUtilities.generateNumericString(3));
		paymentDTO.setFirstName("Tony" + RandomUtilities.generateAlphaString(8));
		paymentDTO.setLastName("Tester" + RandomUtilities.generateAlphaString(8));
		paymentDTO.setAddress1("20 West 20th Street");
		paymentDTO.setAddress2("");
		paymentDTO.setCity("New York");
		paymentDTO.setState("NY");
		paymentDTO.setPostalCode("10001");
		paymentDTO.setCountry("US");
		return paymentDTO;
	}

	public Company newCompany() {
		return companyService.createCompany(
			"companyName" + RandomUtilities.generateNumericString(10),
			true,
			Company.TEST_CUSTOMER_TYPE
		);
	}

	public ClientCompany newClientCompany(Long employeeId) {
		ClientCompanyDTO companyDTO = new ClientCompanyDTO();
		companyDTO.setName("Client " + RandomUtilities.generateAlphaString(10));
		companyDTO.setCustomerId("Num" + RandomUtilities.generateAlphaString(10));
		companyDTO.setAddress1("20 20st");
		companyDTO.setCity("New York");
		companyDTO.setState("NY");
		companyDTO.setPostalCode("10011");
		companyDTO.setCountry("USA");

		return crmService.saveOrUpdateClientCompany(employeeId, companyDTO, null);
	}

	public Project newProject(Long employeeId, Long clientCompanyId, String projectName) {
		ProjectDTO dto = new ProjectDTO();
		dto.setName(projectName);
		dto.setDescription("Project Description");
		dto.setDueDateString(today().toString());
		dto.setClientCompanyId(clientCompanyId);

		return projectService.saveOrUpdateProject(employeeId, dto);
	}

//	public BackgroundCheck newBackgroundCheckPassed(Long contractorId) throws Exception {
//		User contractor = userService.findUserById(contractorId);
//
//		ScreeningDTO screeningDTO = new ScreeningDTO();
//		screeningDTO.setFirstName(contractor.getFirstName());
//		screeningDTO.setLastName(contractor.getLastName());
//		screeningDTO.setMaidenName(contractor.getLastName());
//		screeningDTO.setDateOfBirth("1070-01-01");
//		screeningDTO.setWorkIdentificationNumber(RandomUtilities.generateNumericString(9));
//		screeningDTO.setCountry(Country.USA);
//		screeningDTO.setBirthDay(1);
//		screeningDTO.setBirthMonth(1);
//		screeningDTO.setBirthYear(1070);
//
//		BackgroundCheck backgroundCheck = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
//			screeningService.requestBackgroundCheck(contractorId, screeningDTO));
//		backgroundCheck.setScreeningStatusType(new ScreeningStatusType(ScreeningStatusType.PASSED));
//		backgroundCheck.setUser(contractor);
//		screeningService.saveOrUpdate(backgroundCheck);
//
//		return backgroundCheck;
//	}
//
//	public BackgroundCheck newBackgroundCheckFailed(Long contractorId) throws Exception {
//		User contractor = userService.findUserById(contractorId);
//
//		ScreeningDTO screeningDTO = new ScreeningDTO();
//		screeningDTO.setFirstName(contractor.getFirstName());
//		screeningDTO.setLastName(contractor.getLastName());
//		screeningDTO.setMaidenName(contractor.getLastName());
//		screeningDTO.setDateOfBirth("1070-01-01");
//		screeningDTO.setWorkIdentificationNumber(RandomUtilities.generateNumericString(9));
//		screeningDTO.setCountry(Country.USA);
//		screeningDTO.setBirthDay(1);
//		screeningDTO.setBirthMonth(1);
//		screeningDTO.setBirthYear(1070);
//
//		BackgroundCheck backgroundCheck = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
//			screeningService.requestBackgroundCheck(contractorId, screeningDTO));
//		backgroundCheck.setScreeningStatusType(new ScreeningStatusType(ScreeningStatusType.FAILED));
//		backgroundCheck.setUser(contractor);
//		screeningService.saveOrUpdate(backgroundCheck);
//
//		return backgroundCheck;
//	}
//
//	public DrugTest newDrugTestPassed(Long contractorId) throws Exception {
//		User contractor = userService.findUserById(contractorId);
//
//		ScreeningDTO screeningDTO = new ScreeningDTO();
//		screeningDTO.setFirstName(contractor.getFirstName());
//		screeningDTO.setLastName(contractor.getLastName());
//		screeningDTO.setMaidenName(contractor.getLastName());
//		screeningDTO.setDateOfBirth("1070-01-01");
//		screeningDTO.setWorkIdentificationNumber(RandomUtilities.generateNumericString(9));
//		screeningDTO.setCountry(Country.USA);
//		screeningDTO.setBirthDay(1);
//		screeningDTO.setBirthMonth(1);
//		screeningDTO.setBirthYear(1070);
//
//		DrugTest drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
//			screeningService.requestDrugTest(contractorId, screeningDTO));
//		drugTest.setScreeningStatusType(new ScreeningStatusType(ScreeningStatusType.PASSED));
//		drugTest.setUser(contractor);
//		screeningService.saveOrUpdate(drugTest);
//
//		return drugTest;
//	}
//
//	public DrugTest newDrugTestFailed(Long contractorId) throws Exception {
//		User contractor = userService.findUserById(contractorId);
//
//		ScreeningDTO screeningDTO = new ScreeningDTO();
//		screeningDTO.setFirstName(contractor.getFirstName());
//		screeningDTO.setLastName(contractor.getLastName());
//		screeningDTO.setMaidenName(contractor.getLastName());
//		screeningDTO.setDateOfBirth("1070-01-01");
//		screeningDTO.setWorkIdentificationNumber(RandomUtilities.generateNumericString(9));
//		screeningDTO.setCountry(Country.USA);
//		screeningDTO.setBirthDay(1);
//		screeningDTO.setBirthMonth(1);
//		screeningDTO.setBirthYear(1070);
//
//		DrugTest drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
//			screeningService.requestDrugTest(contractorId, screeningDTO));
//		drugTest.setScreeningStatusType(new ScreeningStatusType(ScreeningStatusType.FAILED));
//		drugTest.setUser(contractor);
//		screeningService.saveOrUpdate(drugTest);
//
//		return drugTest;
//	}

	public InsuranceDTO newInsuranceDTO(Long insuranceId) {
		InsuranceDTO dto = new InsuranceDTO();
		dto.setInsuranceId(insuranceId);
		dto.setProvider("State Farm Insurance");
		dto.setPolicyNumber(RandomUtilities.generateNumericString(10));
		dto.setCoverage(RandomUtilities.generateNumericString(5));
		dto.setIssueDate(Calendar.getInstance());
		dto.setExpirationDate(Calendar.getInstance());
		return dto;
	}

	public ToolDTO newToolDTO() {
		ToolDTO dto = new ToolDTO();
		dto.setName("tool" + RandomUtilities.generateAlphaString(10));
		dto.setDescription("description" + RandomUtilities.generateAlphaString(10));
		dto.setIndustryId(INDUSTRY_ID_1000);

		return dto;
	}

	public Tool newTool() {
		ToolDTO dto = new ToolDTO();
		dto.setName("tool" + RandomUtilities.generateAlphaString(10));
		dto.setDescription("description" + RandomUtilities.generateAlphaString(10));
		dto.setIndustryId(INDUSTRY_ID_1000);

		return toolService.saveOrUpdateTool(dto);
	}

	public Skill newSkill() {
		SkillDTO dto = new SkillDTO();
		dto.setName("skill" + RandomUtilities.generateAlphaString(10));
		dto.setDescription("description" + RandomUtilities.nextLong());
		dto.setIndustryId(INDUSTRY_ID_1000);

		return skillService.saveOrUpdateSkill(dto);
	}

	public ClientContact newClientContactForCompany(long companyId) {
		ClientContactDTO clientContactDTO = new ClientContactDTO();

		clientContactDTO.setFirstName("First");
		clientContactDTO.setLastName("Last");
		clientContactDTO.setJobTitle("Web Developer");
		clientContactDTO.setManager(true);

		return crmService.saveOrUpdateClientContact(companyId, clientContactDTO, null);
	}

	public ClientLocation newClientLocationForClientCompany(long companyId, long clientCompanyId) {
		LocationDTO locationDTO = new LocationDTO();

		locationDTO.setAddress1("7 High St.");
		locationDTO.setAddress2("Suite 407");
		locationDTO.setCity("Huntington");
		locationDTO.setState("NY");
		locationDTO.setPostalCode("11743");
		locationDTO.setCountry("USA");
		locationDTO.setCompanyId(companyId);
		locationDTO.setName("Work Market");
		locationDTO.setLocationNumber(RandomUtilities.generateNumericString(6));
		return crmService.saveOrUpdateClientLocation(clientCompanyId, locationDTO, null);
	}

	public Work createWorkAndSendToResourceWithPaymentTerms(User employee, User contractor) throws Exception {
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		return workService.findWork(work.getId());
	}

	public Work createWorkAndSendToResourceWithOfflinePayment(User employee, User contractor) throws Exception {
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		Work work = newWorkWithOfflinePayment(employee.getId());
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		return workService.findWork(work.getId());
	}

	public Work createWorkAndSendToResourceNoPaymentTerms(User employee, User contractor) throws Exception {
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		Work work = newWorkWithPaymentTerms(employee.getId(), 0);
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		return workService.findWork(work.getId());
	}

	public Work createWorkAndSendToResourceNoPaymentTermsAndAccept(User employee, User contractor, Calendar scheduleTime) throws Exception {
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		Work work = newWorkWithPaymentTerms(employee.getId(), 0, scheduleTime);
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());
		return workService.findWork(work.getId());
	}

	public WorkBundle newWorkBundle(Long employeeId) throws Exception {
		WorkBundleDTO workBundleDTO = new WorkBundleDTO();
		workBundleDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workBundleDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workBundleDTO.setIndustryId(INDUSTRY_ID);
		workBundleDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workBundleDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);

		return workBundleService.saveOrUpdateWorkBundle(employeeId, workBundleDTO);
	}

	public WorkCustomFieldGroup addCustomFieldsToWork(Long workId) throws Exception {
		Long userId = authenticationService.getCurrentUser().getId();
		WorkCustomFieldGroup savedGroup = createCustomFieldGroup(userId);

		Work work = workService.findWork(workId);
		customFieldService.addWorkCustomFieldGroupToWork(savedGroup.getId(), work.getId(), 0);

		return savedGroup;
	}

	public WorkCustomFieldGroup createCustomFieldGroup(Long userId) throws Exception {
		WorkCustomFieldGroupDTO dto = new WorkCustomFieldGroupDTO();
		dto.setName("Test CF Group");
		dto.setRequired(false);
		dto.setPosition(0);

		List<WorkCustomFieldDTO> customFieldDTOs = new LinkedList<>();

		WorkCustomFieldDTO ownerCustomFieldDTO = new WorkCustomFieldDTO();
		ownerCustomFieldDTO.setWorkCustomFieldTypeCode(WorkCustomFieldType.OWNER);
		ownerCustomFieldDTO.setName("Owner Field");
		ownerCustomFieldDTO.setPosition(0);
		customFieldDTOs.add(ownerCustomFieldDTO);

		WorkCustomFieldDTO resourceCustomFieldDTO = new WorkCustomFieldDTO();
		resourceCustomFieldDTO.setWorkCustomFieldTypeCode(WorkCustomFieldType.RESOURCE);
		resourceCustomFieldDTO.setName("Resource Field");
		resourceCustomFieldDTO.setPosition(1);
		customFieldDTOs.add(resourceCustomFieldDTO);

		dto.setWorkCustomFields(customFieldDTOs);

		WorkCustomFieldGroup savedGroup = customFieldService.saveOrUpdateWorkFieldGroup(userId, dto);

		for (WorkCustomField field : savedGroup.getActiveWorkCustomFields()) {
			field.setWorkCustomFieldGroup(savedGroup);
			customFieldService.saveOrUpdateWorkCustomField(field);
		}
		return savedGroup;
	}

	public WorkSubStatusTypeDTO createWorkSubStatusType(Long companyId) throws Exception {
		WorkSubStatusTypeDTO dto = new WorkSubStatusTypeDTO();
		dto.setActive(true);
		dto.setCode("test_label");
		dto.setCompanyId(companyId);
		dto.setDescription("Test Label");
		dto.setWorkStatusCodes(new String[]{ WorkStatusType.DRAFT });
		workSubStatusService.saveOrUpdateCustomWorkSubStatus(dto);
		return dto;
	}

	public WorkSubStatusTypeDTO newWorkSubStatusTypeDTO() {
		WorkSubStatusTypeDTO workSubStatusTypeDTO = new WorkSubStatusTypeDTO();
		workSubStatusTypeDTO.setCode("new " + RandomUtilities.generateNumericString(5));
		workSubStatusTypeDTO.setDescription("some description");
		workSubStatusTypeDTO.setAlert(true);
		workSubStatusTypeDTO.setCompanyId(COMPANY_ID);
		workSubStatusTypeDTO.setNotifyResourceEnabled(true);
		workSubStatusTypeDTO.setRemoveOnVoidOrCancelled(true);
		workSubStatusTypeDTO.setNoteRequired(false);
		return workSubStatusTypeDTO;
	}

	public TimeTrackingResponse createCheckin(Long workId) {
		return workService.checkInActiveResource(new TimeTrackingRequest().setWorkId(workId).setDate(new DateTime().minus(1000).toGregorianCalendar()));
	}

	public TimeTrackingResponse createCheckout(Long workId, Long timeTrackingId, String noteText) {

		return workService.checkOutActiveResource(new TimeTrackingRequest()
			.setWorkId(workId)
			.setTimeTrackingId(timeTrackingId)
			.setDate(new DateTime().toGregorianCalendar())
			.setDistance(10.0).setNoteOnCheckOut(noteText));
	}

	protected DateMidnight today() {
		return new DateMidnight();
	}

	protected DateMidnight today(DateTimeZone dateTimeZone) {
		return new DateMidnight(dateTimeZone);
	}

	public Network createNetwork() {
		Network network = new Network();
		String networkName = RandomUtilities.generateAlphaString(8);
		network.setName(networkName);
		return networkService.saveOrUpdate(network);
	}

	public AcceptWorkOfferRequest createAcceptWorkOfferRequest(Work work, User worker, String dispatcherUserNumber) {
		WorkActionRequest actionRequest = new WorkActionRequest(work.getWorkNumber());
		actionRequest.setResourceUserNumber(worker.getUserNumber());
		actionRequest.setOnBehalfOfUserNumber(dispatcherUserNumber);
		AcceptWorkOfferRequest acceptWorkOfferRequest = new AcceptWorkOfferRequest();
		acceptWorkOfferRequest.setWorkAction(actionRequest);

		return acceptWorkOfferRequest;
	}

	public User createDispatcher() throws Exception {
		User dispatcher = newFirstEmployee();
		authenticationService.updateUserAclRoles(dispatcher.getId(), Lists.newArrayList(AclRole.ACL_ADMIN, AclRole.ACL_DISPATCHER));
		userService.saveOrUpdatePersonaPreference(
			userService.getPersonaPreference(dispatcher.getId()).get().setDispatcher(true)
		);

		return dispatcher;
	}

	public AccountingSummary getBaseAccountingSummary() {
		AccountingSummary summary = new AccountingSummary();
		Calendar end = Calendar.getInstance();
		Calendar start = journalEntrySummaryService.findDateOfLastSummary();
		Calendar startFiscalYear = journalEntrySummaryService.findOrCreateStartFiscalYearForDate(Calendar.getInstance());

		if (start == null) {
			start = startFiscalYear;
		}

		summary.setRequestDate(end);
		summary.setPreviousRequestDate(start);

		summary.setMoneyInFastFundsHistorical(BigDecimal.ZERO);
		summary.setMoneyInChecksHistorical(BigDecimal.ZERO);
		summary.setMoneyInAchHistorical(BigDecimal.ZERO);
		summary.setMoneyInWireHistorical(BigDecimal.ZERO);
		summary.setMoneyInCreditCardHistorical(BigDecimal.ZERO);
		summary.setMoneyInSubscriptionCreditMemoHistorical(BigDecimal.ZERO);
		summary.setMoneyInProfServicesCreditMemoHistorical(BigDecimal.ZERO);
		summary.setMoneyOutWithdrawalsHistorical(BigDecimal.ZERO);
		summary.setMoneyOutNonUSAWithdrawalsHistorical(BigDecimal.ZERO);
		summary.setMoneyOutFeesHistorical(BigDecimal.ZERO);
		summary.setMoneyOutCreditCardFeesHistorical(BigDecimal.ZERO);
		summary.setMoneyOutFastFundsReceivablePaymentsHistorical(BigDecimal.ZERO);
		summary.setMoneyOutFastFundsFeeHistorical(BigDecimal.ZERO);
		summary.setMoneyOutBackgroundChecksHistorical(BigDecimal.ZERO);
		summary.setMoneyOutDrugTestsHistorical(BigDecimal.ZERO);
		summary.setMoneyOutAchVerificationsHistorical(BigDecimal.ZERO);
		summary.setMoneyOutDebitTransactionsHistorical(BigDecimal.ZERO);
		summary.setMoneyOutCreditTransactionsHistorical(BigDecimal.ZERO);
		summary.setMoneyOutPayPalWithdrawalHistorical(BigDecimal.ZERO);
		summary.setMoneyOutPayPalFeesHistorical(BigDecimal.ZERO);
		summary.setMoneyOutWMToPayPalFeesHistorical(BigDecimal.ZERO);
		summary.setMoneyOutDepositReturnFeeHistorical(BigDecimal.ZERO);
		summary.setMoneyOutWithdrawalReturnFeeHistorical(BigDecimal.ZERO);
		summary.setMoneyOutLatePaymentFeeHistorical(BigDecimal.ZERO);
		summary.setMoneyOutMiscellaneousFeeHistorical(BigDecimal.ZERO);
		summary.setTotalMoneyOnSystemHistorical(BigDecimal.ZERO);
		summary.setTotalCompletedAssignmentsHistorical(BigDecimal.ZERO);
		summary.setTotalEarnedForAssignmentsHistorical(BigDecimal.ZERO);
		summary.setMoneyInFastFunds(BigDecimal.ZERO);
		summary.setMoneyInChecks(BigDecimal.ZERO);
		summary.setMoneyInAch(BigDecimal.ZERO);
		summary.setMoneyInWire(BigDecimal.ZERO);
		summary.setMoneyInCreditCard(BigDecimal.ZERO);
		summary.setMoneyInSubscriptionCreditMemo(BigDecimal.ZERO);
		summary.setMoneyInProfServicesCreditMemo(BigDecimal.ZERO);
		summary.setMoneyOutWithdrawals(BigDecimal.ZERO);
		summary.setMoneyOutNonUSAWithdrawals(BigDecimal.ZERO);
		summary.setMoneyOutFees(BigDecimal.ZERO);
		summary.setMoneyOutCreditCardFees(BigDecimal.ZERO);
		summary.setMoneyOutFastFundsReceivablePayments(BigDecimal.ZERO);
		summary.setMoneyOutFastFundsFee(BigDecimal.ZERO);
		summary.setMoneyOutBackgroundChecks(BigDecimal.ZERO);
		summary.setMoneyOutDrugTests(BigDecimal.ZERO);
		summary.setMoneyOutAchVerifications(BigDecimal.ZERO);
		summary.setMoneyOutDebitTransactions(BigDecimal.ZERO);
		summary.setMoneyOutCreditTransactions(BigDecimal.ZERO);
		summary.setMoneyOutPayPalWithdrawal(BigDecimal.ZERO);
		summary.setMoneyOutGCCWithdrawal(BigDecimal.ZERO);
		summary.setMoneyOutGCCWithdrawalHistorical(BigDecimal.ZERO);
		summary.setMoneyOutPayPalFees(BigDecimal.ZERO);
		summary.setMoneyOutWMToPayPalFees(BigDecimal.ZERO);
		summary.setMoneyOutDepositReturnFee(BigDecimal.ZERO);
		summary.setMoneyOutWithdrawalReturnFee(BigDecimal.ZERO);
		summary.setMoneyOutLatePaymentFee(BigDecimal.ZERO);
		summary.setMoneyOutMiscellaneousFee(BigDecimal.ZERO);
		summary.setRevenueDepositReturnFee(BigDecimal.ZERO);
		summary.setRevenueWithdrawalReturnFee(BigDecimal.ZERO);
		summary.setRevenueLatePaymentFee(BigDecimal.ZERO);
		summary.setRevenueMiscellaneousFee(BigDecimal.ZERO);
		summary.setRevenueDepositReturnFeeHistorical(BigDecimal.ZERO);
		summary.setRevenueWithdrawalReturnFeeHistorical(BigDecimal.ZERO);
		summary.setRevenueLatePaymentFeeHistorical(BigDecimal.ZERO);
		summary.setRevenueMiscellaneousFeeHistorical(BigDecimal.ZERO);
		summary.setAdHocServiceFeeReceivables(BigDecimal.ZERO);
		summary.setAdHocServiceFeeReceivablesHistorical(BigDecimal.ZERO);
		summary.setTotalMoneyOnSystem(BigDecimal.ZERO);
		summary.setTotalCompletedAssignments(BigDecimal.ZERO);
		summary.setTotalEarnedForAssignments(BigDecimal.ZERO);
		summary.setTotalInApStatus(BigDecimal.ZERO);

		summary.setOfflinePaymentSummary(new OfflinePaymentSummary());
		summary.setCreditDebitRegisterTransactionsSummary(new CreditDebitRegisterTransactionsSummary());
		summary.setAccountingPricingServiceTypeSummary(new AccountingPricingServiceTypeSummary());
		return summary;
	}
}
