package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.AddressDAO;
import com.workmarket.dao.LocationDAO;
import com.workmarket.dao.account.payment.PaymentTermsDurationCompanyAssociationDAO;
import com.workmarket.dao.account.payment.PaymentTermsDurationDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.asset.CompanyAssetAssociationDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.velvetrope.guest.CompanyGuest;
import com.workmarket.domains.velvetrope.rope.FastFundsRope;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.dao.company.CompanyLocationAssociationDAO;
import com.workmarket.dao.company.CompanyPreferenceDAO;
import com.workmarket.dao.company.CompanySearchTrackingDAO;
import com.workmarket.dao.company.CompanySignUpInfoDAO;
import com.workmarket.dao.directory.EmailDAO;
import com.workmarket.dao.random.CompanyRandomIdentifierDAO;
import com.workmarket.dao.skill.CompanySkillAssociationDAO;
import com.workmarket.dao.skill.SkillDAO;
import com.workmarket.dao.summary.company.CompanySummaryDAO;
import com.workmarket.data.aggregate.CompanyAggregate;
import com.workmarket.data.aggregate.CompanyAggregatePagination;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyEmployeeCountRangeEnum;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentTermsDuration;
import com.workmarket.domains.model.account.payment.PaymentTermsDurationCompanyAssociation;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.company.CompanySearchTracking;
import com.workmarket.domains.model.company.CustomerType;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.model.skill.CompanyLocationAssociation;
import com.workmarket.domains.model.skill.CompanySkillAssociation;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.summary.company.CompanySummary;
import com.workmarket.domains.payments.dao.PaymentConfigurationDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.dto.CompanyCommentDTO;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.service.business.dto.SkillDTO;
import com.workmarket.service.business.event.company.VendorSearchIndexEvent;
import com.workmarket.service.infra.business.AuthTrialCommon;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.GeocodingService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.EncryptionUtilities;
import com.workmarket.utility.ProjectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.controllers.users.EmployeeSettingsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;
import static com.google.common.collect.Lists.partition;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class CompanyServiceImpl implements CompanyService {
	private static final Log logger = LogFactory.getLog(CompanyServiceImpl.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private ProfileService profileService;
	@Autowired private CommentService commentService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private AddressDAO addressDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private UserGroupDAO userGroupDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private CompanyPreferenceDAO companyPreferenceDAO;
	@Autowired private CompanyAssetAssociationDAO companyAssetAssociationDAO;
	@Autowired private CompanyRandomIdentifierDAO companyNumberGeneratorDAO;
	@Autowired private CompanySignUpInfoDAO companySignUpInfoDAO;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private DirectoryService directoryService;
	@Autowired private EmailDAO emailDAO;
	@Autowired private PaymentConfigurationDAO paymentConfigurationDAO;
	@Autowired private PaymentTermsDurationCompanyAssociationDAO paymentTermsDurationCompanyAssociationDAO;
	@Autowired private PaymentTermsDurationDAO paymentTermsDurationDAO;
	@Autowired private CompanySummaryDAO companySummaryDAO;
	@Autowired private GeocodingService geocodingService;
	@Autowired private CompanyAlertService companyAlertService;
	@Autowired private CompanySearchTrackingDAO companySearchTrackingDAO;
	@Autowired private EventRouter eventRouter;
	@Autowired private UserRoleService userRoleService;
	@Autowired private AuthTrialCommon trialCommon;
	@Autowired private AuthenticationClient authClient;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private AddressService addressService;
	@Autowired private SkillService skillService;
	@Autowired private CompanyLocationAssociationDAO companyLocationAssociationDAO;
	@Autowired private CompanySkillAssociationDAO companySkillAssociationDAO;
	@Autowired private LocationDAO locationDAO;
	@Autowired private SkillDAO skillDAO;
	@Qualifier("fastFundsDoorman")
	@Autowired private Doorman doorman;

	@Value("${company.onboarding.rollout.date}")
	private String COMPANY_ONBOARDING_ROLLOUT_DATE;

	@Override
	public Company createCompany(String name, boolean operatingAsIndividualFlag, String customerType) {
		Assert.hasText(name);
		final Company company = new Company();
		company.setName(name);
		company.setOperatingAsIndividualFlag(operatingAsIndividualFlag);
		company.setEffectiveName(name);
		company.setCustomerType(customerType);
		company.getManageMyWorkMarket().setInstantWorkerPoolEnabled(true);
		company.getManageMyWorkMarket().setAutoRateEnabledFlag(true);

		PaymentConfiguration paymentConfiguration = new PaymentConfiguration();
		paymentConfigurationDAO.saveOrUpdate(paymentConfiguration);
		company.setPaymentConfiguration(paymentConfiguration);

		setCompanyNumber(company);
		companyDAO.saveOrUpdate(company);

		authClient.createCompany(company.getUuid(), trialCommon.getApiContext()).toBlocking().single();
		companySummaryDAO.saveOrUpdate(new CompanySummary(company));
		workSubStatusService.addDefaultWorkSubStatusToCompany(company);
		return company;
	}

	private void setCompanyNumber(Company company) {
		company.setCompanyNumber(companyNumberGeneratorDAO.generateUniqueNumber());
	}

	@Override
	public Company findById(Long companyId) {
		Assert.notNull(companyId);

		return companyDAO.findById(companyId);
	}

	@Override
	public List<CompanyIdentityDTO> findCompanyIdentitiesByCompanyNumbers(Collection<String> companyNumbers) {
		if (isEmpty(companyNumbers)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		return companyDAO.findCompanyIdentitiesByCompanyNumbers(companyNumbers);
	}

	@Override
	public List<CompanyIdentityDTO> findCompanyIdentitiesByUuids(Collection<String> uuids) {
		if (isEmpty(uuids)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		return companyDAO.findCompanyIdentitiesByUuids(uuids);
	}

	@Override
	public List<CompanyIdentityDTO> findCompanyIdentitiesByIds(Collection<Long> ids) {
		if (isEmpty(ids)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		return companyDAO.findCompanyIdentitiesByIds(ids);
	}

	@Override
	public Company findCompanyById(Long companyId) {
		Assert.notNull(companyId);
		return companyDAO.findCompanyById(companyId);
	}

	@Override
	public Company findCompanyByNumber(String companyNumber) {
		Assert.notNull(companyNumber);
		return companyDAO.findCompanyByNumber(companyNumber);
	}

	@Override
	public Company findCompanyByName(String companyName) {
		Assert.notNull(companyName);
		return companyDAO.findCompanyByName(companyName);
	}

	@Override
	public Company findCompanyByEncryptedId(String encryptedId) {
		Assert.notNull(encryptedId);
		Long companyId;

		try {
			companyId = EncryptionUtilities.decryptLong(encryptedId);
		} catch (EncryptionOperationNotPossibleException e) {
			logger.error(e.getMessage());
			return null;
		}

		return companyDAO.findCompanyById(companyId);
	}

	@Override
	public CompanyAggregatePagination findAllCompanies(CompanyAggregatePagination pagination) {
		Assert.notNull(pagination);
		return companyDAO.findAllCompanies(pagination);
	}


	@Override
	public CompanyAggregate findCompanyAggregate(Long companyId) {
		Assert.notNull(companyId);
		CompanyAggregatePagination pagination = new CompanyAggregatePagination();
		pagination.setFilters(new HashMap<String, String>());
		pagination.getFilters().put(CompanyAggregatePagination.FILTER_KEYS.COMPANY_ID.toString(), companyId.toString());
		pagination.setResultsLimit(1);
		pagination = companyDAO.findAllCompanies(pagination);

		if (!pagination.getResults().isEmpty()) {
			return pagination.getResults().get(0);
		}
		return null;
	}

	@Override
	public Coordinate findLatLongForCompany(Long companyId) {
		Assert.notNull(companyId);
		Company company = findCompanyById(companyId);
		Assert.notNull(company);

		if (company.getAddress() != null && company.getAddress().getLatitude() != null && company.getAddress().getLongitude() != null) {
			return geocodingService.newCoordinate(company.getAddress().getLatitude().doubleValue(), company.getAddress().getLongitude().doubleValue());
		}

		return null;
	}

	@Override
	public void saveOrUpdateCompany(Company company) {
		companyDAO.saveOrUpdate(company);
	}

	private List<UserGroup> findCompanyUserGroupsOfUser(Long userID) {
		return userGroupDAO.findAllUserGroupsByUserIsMember(userID);
	}

	@Override
	public Set<User> findCompanyUsersOfCompanyUserGroups(Long userID) {
		List<UserGroup> listCompanyUserGroups = findCompanyUserGroupsOfUser(userID);
		return userGroupDAO.findUsersFromCompanyUserGroups(listCompanyUserGroups);
	}

	@Override
	public void updateCompanyProperties(Long companyId, Map<String, String> properties)
			throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

		Assert.notNull(companyId, "Company id must be provided");
		Assert.notNull(properties, "Properties must be provided");

		Company company = findCompanyById(companyId);

		Assert.notNull(company, "Unable to find company");

		Long userId = authenticationService.getCurrentUser().getId();

		String name = null;
		if (properties.containsKey("name")) {
			name = properties.get("name");
			properties.remove("name");
		}

		if (properties.containsKey("companyNumWorkersEnum")) {
			company.setCompanyEmployeeCountRangeEnum(CompanyEmployeeCountRangeEnum.getEnumFromDescription(properties.get("companyNumWorkersEnum")));
			properties.remove("companyNumWorkersEnum");
		}

		String overview = properties.containsKey("overview") ? properties.get("overview") : null;
		String website = properties.containsKey("website") ? properties.get("website") : null;
		Boolean isOperatingAsIndividualFlag = properties.containsKey("operatingAsIndividualFlag") ?
				BooleanUtils.toBoolean(properties.get("operatingAsIndividualFlag")) :
				null;
		if (StringUtils.isNotBlank(name) && !StringUtilities.same(name, company.getName())) {
			if (BooleanUtils.isFalse(isOperatingAsIndividualFlag) || !company.getOperatingAsIndividualFlag()) {
				company.setNameOldValue(company.getName());
				company.setName(name);
			}
			profileService.registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.COMPANY_NAME));
			company.setEffectiveName(name);
		}

		if (StringUtils.isNotBlank(overview) && !StringUtilities.same(overview, company.getOverview())) {
			company.setOverviewOldValue(company.getOverview());
			profileService.registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.COMPANY_OVERVIEW));
		}

		if (StringUtils.isNotBlank(website) && !StringUtilities.same(website, company.getWebsite())) {
			company.setWebsiteOldValue(company.getWebsite());
			profileService.registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.COMPANY_WEBSITE));
		}

		if (properties.keySet().contains("address.id")) {
			Address address = addressDAO.get(Long.parseLong(properties.get("address.id")));

			Assert.notNull(address, "Unable to find address.");
			company.setAddress(address);
			properties.remove("address.id");
		}

		if (properties.keySet().contains("customLowBalanceFlag")) {
			if (Boolean.parseBoolean(properties.get("customLowBalanceFlag"))) {
				Assert.isTrue(properties.keySet().contains("lowBalanceAmount"), "Low Balance Amount should be specified.");
				Assert.isTrue(StringUtils.isNotBlank(properties.get("lowBalanceAmount")), "Low Balance Amount should be specified.");
			}
		}

		BeanUtilities.updateProperties(company, properties);

		companyDAO.saveOrUpdate(company);
		authenticationService.refreshSessionForCompany(companyId);
		if (company.isInVendorSearch()) {
			eventRouter.sendEvent(new VendorSearchIndexEvent(companyId));
		}
	}

	@Override
	public void lockCompanyAccount(Long companyId) {
		Company company = companyDAO.findCompanyById(companyId);
		Assert.notNull(company, "Unable to find company.");

		if (company.lock()) {
			// Notify admins, controllers and CSR
			userNotificationService.onCompanyAccountLocked(companyId);
			authenticationService.refreshSessionForCompany(companyId);
		}
	}

	private User assertAndGetCurrentInternalUser() {
		User user = authenticationService.getCurrentUser();
		Assert.isTrue(userRoleService.isInternalUser(user), "User is not an internal WM user");

		return user;
	}

	private void saveAndRefreshSession(Company company) {
		companyDAO.saveOrUpdate(company);
		authenticationService.refreshSessionForCompany(company.getId());
	}


	@Override
	public void markAsVip(Long companyId, boolean vip) {
		Company company = companyDAO.findCompanyById(companyId);
		Assert.notNull(company, "Unable to find company.");

		if (!company.isVipFlag() && vip) {
			company.setUnlockedBy(authenticationService.getCurrentUser());
		}

		if (company.markAsVip(vip)) {
			company.setVipSetBy(authenticationService.getCurrentUser().getId());
			company.setVipSetOn(DateUtilities.getCalendarNow());
			saveAndRefreshSession(company);
		}
	}

	@Override
	public void unlockCompanyAccount(Long companyId, int unlockedPeriodGraceHours) {
		logger.debug("Unlocking company id: " + companyId);
		Assert.isTrue(unlockedPeriodGraceHours >= 24, "Minimum time frame is 24 hours for next suspension");

		assertAndGetCurrentInternalUser();

		Company company = companyDAO.findCompanyById(companyId);
		Assert.notNull(company, "Unable to find company.");
		Assert.isTrue(company.isLocked(), "Company status is " + company.getCompanyStatusType().getCode());
		unlockCompany(company, unlockedPeriodGraceHours);
	}

	@Override
	public void unlockCompanyAccount(Long companyId) {
		Company company = companyDAO.findCompanyById(companyId);
		Assert.notNull(company, "Unable to find company.");

		unlockCompanyAccount(company);
	}

	private void unlockCompanyAccount(Company company) {
		Integer dueWork = workDAO.countAllDueWorkByCompany(company.getId());
		logger.debug("Found " + dueWork + " assignments due for company " + company.getId());

		if (dueWork == 0) {
			company.setOverdueAccountWarningSentOn(null);
			company.setLockAccountWarningSentOn(null);
			unlockCompany(company, 0);
			authenticationService.refreshSessionForCompany(company.getId());
		}
	}

	private void unlockCompany(Company company, int unlockedPeriodGraceHours) {
		if (company.unlock(unlockedPeriodGraceHours)) {
			company.setUnlockedBy(authenticationService.getCurrentUser());
			saveAndRefreshSession(company);
		}
	}

	@Override
	public boolean hasPaymentTermsEnabled(Long companyId) {
		return companyDAO.isPaymentTermsEnabledForCompany(companyId);
	}

	@Override
	public Integer getDefaultPaymentTermsDays() {
		return authenticationService.getCurrentUser().getCompany().getPaymentTermsDays();
	}

	@Override
	public void overridePaymentTerms(Long companyId, String note) {
		Company company = companyDAO.get(companyId);
		Assert.notNull(company, "Invalid company ID");
		Assert.notNull(note, "Note required");
		company.getManageMyWorkMarket().setPaymentTermsOverride(true);

		CompanyCommentDTO comment = new CompanyCommentDTO();
		comment.setComment(String.format("PAYTERMS OVERRIDE: %s", note));
		comment.setCompanyId(companyId);
		commentService.saveOrUpdateClientServiceCompanyComment(comment);

		userNotificationService.onOverridePaymentTerms(company, note);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PaymentTermsDuration> findPaymentTermsDurations(Long companyId) {
		Company company = companyDAO.get(companyId);
		Assert.notNull(company, "Invalid company ID");

		List<PaymentTermsDuration> result = CollectionUtilities.newListPropertyProjection(
				paymentTermsDurationCompanyAssociationDAO.findPaymentTermsDurationCompanyAssociationByCompanyId(companyId),
				"paymentTermsDuration");

		if (isNotEmpty(result)) {
			return result;
		}

		// if no custom mapping exists, give the default set
		return paymentTermsDurationDAO.findDefaultPaymentTermsDurations();
	}

	@Override
	public ImmutableList<Map> getProjectedPaymentTermsDurations(String[] fields) throws Exception {
		Long companyId = authenticationService.getCurrentUserCompanyId();
		Integer defaultDays = getManageMyWorkMarket(companyId).getPaymentTermsDays();

		Map[] paymentTermsDurations = ProjectionUtilities.projectAsArray(
			fields,
			ImmutableMap.of("days", "numDays"),
			findPaymentTermsDurations(companyId)
		);

		for (Map duration : paymentTermsDurations) {
			String key = duration.containsKey("days") ? "days" : "numDays";
			duration.put("default", duration.get(key).equals(String.valueOf(defaultDays)));
		}

		return ImmutableList.copyOf(paymentTermsDurations);
	}

	@Override
	public Boolean hasConfirmedBankAccounts(Long companyId) {
		Company company = companyDAO.get(companyId);
		Assert.notNull(company, "Invalid company ID");

		for (AbstractBankAccount a : company.getBankAccounts()) {
			if (a.getActiveFlag() && a.getConfirmedFlag()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getNextInvoiceSummaryNumber(Company company) {
		Integer invoiceNumber = company.getLastInvoiceSummaryId() + 1;
		company.setLastInvoiceSummaryId(invoiceNumber);
		return String.format("%s-%s-%05d", company.getCompanyNumber(), Constants.INVOICE_BUNDLE_NUMBER_PREFIX, invoiceNumber);
	}

	@Override
	public String getNextStatementNumber(Long companyId) {
		return getNextStatementNumber(companyDAO.findById(companyId));
	}

	private String getNextStatementNumber(Company company) {
		Integer invoiceNumber = company.getLastStatementId() + 1;
		company.setLastStatementId(invoiceNumber);
		return String.format("%s-%s-%05d", company.getCompanyNumber(), Constants.STATEMENT_NUMBER_PREFIX, invoiceNumber);
	}

	@Override
	public CompanyAssetAssociation findCompanyAvatars(Long companyId) {
		Assert.notNull(companyId);
		return companyAssetAssociationDAO.findCompanyAvatars(companyId);
	}

	@Override
	public CompanyAssetAssociation findPreviousCompanyAvatars(Long companyId) {
		Assert.notNull(companyId);
		return companyAssetAssociationDAO.findPreviousCompanyAvatars(companyId);
	}

	@Override
	public ManageMyWorkMarket getManageMyWorkMarket(Long companyId) {
		Company company = companyDAO.get(companyId);
		Assert.notNull(company);
		return company.getManageMyWorkMarket();
	}

	@Override
	public void setPaymentTermsDurations(Long companyId, List<Integer> paymentTermsDurations) {
		Company company = companyDAO.get(companyId);

		Set<PaymentTermsDurationCompanyAssociation> companyDurations = company.getPaymentTermsDurationCompanyAssociations();
		for (PaymentTermsDurationCompanyAssociation companyDuration : companyDurations) {
			companyDuration.setDeleted(true);
			paymentTermsDurationCompanyAssociationDAO.saveOrUpdate(companyDuration);
		}

		for (Integer duration : paymentTermsDurations) {
			PaymentTermsDuration d = paymentTermsDurationDAO.getOrInitializeBy(
					"numDays", duration
			);

			if (d.getType() == null) {
				d.setType("custom");
			}

			paymentTermsDurationDAO.saveOrUpdate(d);

			PaymentTermsDurationCompanyAssociation companyDuration =
					paymentTermsDurationCompanyAssociationDAO.getOrInitializeBy(
							"company", company,
							"paymentTermsDuration", d
					);
			companyDuration.setDeleted(false);
			paymentTermsDurationCompanyAssociationDAO.saveOrUpdate(companyDuration);
		}
	}

	@Override
	public void saveAssignmentAlertEmailToCompany(Long companyId, EmailAddressDTO emailDTO) {
		if (StringUtils.isNotBlank(emailDTO.getEmail())) {
			Company company = companyDAO.get(companyId);
			Assert.notNull(company, "Unable to find Company");

			Email emailAddress = directoryService.saveOrUpdateEmailAddress(emailDTO);
			//Since CASCADE.ALL doesn't work with @ManyToMany and @JoinTable together we need to delete the entities manually
			//First delete the entities
			emailDAO.delete(company.getAgingAlertEmails());
			//Second, delete the associations
			company.getAgingAlertEmails().clear();
			//Third, add the new values
			company.getAgingAlertEmails().add(emailAddress);
		}
	}

	@Override
	public void saveOrUpdateSubscriptionInvoicesEmailToCompany(Long companyId, List<EmailAddressDTO> emailDTOs) {
		Assert.notNull(emailDTOs);
		Company company = companyDAO.get(companyId);
		Assert.notNull(company, "Unable to find Company");
		if (company.hasDefaultAddressForSubscriptionInvoices()) {
			//Since CASCADE.ALL doesn't work with @ManyToMany and @JoinTable together we need to delete the entities manually
			//First delete the entities
			emailDAO.delete(company.getSubscriptionInvoiceEmails());
			//Second, delete the associations
			company.getSubscriptionInvoiceEmails().clear();
		}

		//Third, add the new values
		for (EmailAddressDTO emailDTO : emailDTOs) {
			if (StringUtils.isNotBlank(emailDTO.getEmail())) {
				Email emailAddress = directoryService.saveOrUpdateEmailAddress(emailDTO);
				company.getSubscriptionInvoiceEmails().add(emailAddress);
			}
		}
	}

	@Override
	public PaymentConfiguration getPaymentConfiguration(Long companyId) {
		Assert.notNull(companyId);

		Company company = companyDAO.get(companyId);
		Assert.notNull(company);
		return company.getPaymentConfiguration();
	}

	public void saveOrUpdatePaymentCalculatorType(Long companyId, Integer paymentCalculatorType) {
		Assert.notNull(companyId);
		Assert.isTrue(paymentCalculatorType != null && paymentCalculatorType >= 0 && paymentCalculatorType <= 2, "invalid pricing type");
		Company company = companyDAO.get(companyId);
		Assert.notNull(company, "Unable to find company");

		PaymentConfiguration paymentConfig = company.getPaymentConfiguration();
		if (paymentConfig.isSubscriptionPricing()) {
			return;
		}

		paymentConfig.setPaymentCalculatorType(paymentCalculatorType);
		paymentConfigurationDAO.saveOrUpdate(paymentConfig);

	}

	@Override
	public List<Long> findAllCompaniesWithWorkPayment(Calendar fromDate) {
		Assert.notNull(fromDate);
		return companyDAO.findAllCompaniesWithWorkPayment(fromDate);
	}

	@Override
	public List<Long> findAllCompaniesWithWorkCancellations(Calendar fromDate) {
		Assert.notNull(fromDate);
		return companyDAO.findAllCompaniesWithWorkCancellations(fromDate);
	}

	public boolean doesCompanyHaveReservedFundsEnabledProject(Long companyId) {
		return companyDAO.doesCompanyHaveReservedFundsEnabledProject(companyId);
	}

	@Override
	public void resetLowBalanceAlertSentToday(Long companyId) {
		Assert.notNull(companyId, "Company id must be provided");
		companyAlertService.resetLowBalanceAlertSentToday(companyId);
	}

	@Override
	public List<Long> findCompanyIdsWithAgingAlert() {
		return companyDAO.findCompaniesWithAgingAlert();
	}

	@Override
	public Map<Long, Company> getCompaniesByIds(Collection<Long> companyIds) {
		if(companyIds.isEmpty()) {
			return Collections.EMPTY_MAP;
		}
		List<Company> companies = companyDAO.get(companyIds);
		return index(companies, on(Company.class).getId());
	}

	@Override
	public boolean isInstantWorkerPoolEnabled(Long companyId) {
		Assert.notNull(companyId, "Company id must be provided");
		return companyDAO.isInstantWorkerPoolEnabled(companyId);
	}

	public void updateCompanyPreference(CompanyPreference companyPreference) {
		Assert.notNull(companyPreference);
		companyPreferenceDAO.saveOrUpdate(companyPreference);
	}

	@Override
	public boolean doesCompanyHaveOverdueInvoice(Long companyId, Calendar calendar) {
		Assert.notNull(companyId);
		Assert.notNull(calendar);
		return companyDAO.doesCompanyHaveOverdueInvoice(companyId, calendar);

	}

	@Override
	public void processDueInvoicesForCompany(long companyId) {
		if (!doesCompanyHaveOverdueInvoice(companyId, Calendar.getInstance())) {
			Company company = companyDAO.get(companyId);
			Assert.notNull(company);
			//no over due
			company.setOverdueAccountWarningSentOn(null);
			if (company.isLocked()) {
				unlockCompanyAccount(company);
			}
		}

		Calendar midnightNextDay = DateUtilities.getMidnightNextDay(Calendar.getInstance());
		if (!doesCompanyHaveOverdueInvoice(companyId, midnightNextDay)) {
			Company company = companyDAO.get(companyId);
			Assert.notNull(company);
			//no over due
			company.setLockAccountWarningSentOn(null);
		}
		authenticationService.refreshSessionForCompany(companyId);
	}

	@Override
	public String getCompanySignUpPricingPlan(long companyId) {
		return companySignUpInfoDAO.getCompanySignUpPricingPlan(companyId);
	}

	@Override
	public CompanySearchTracking saveCompanySearchTrackingSetting(long companyId, String email, Set<Long> trackedCompanies) {
		if (isNotBlank(email) && isNotEmpty(trackedCompanies)) {
			Company company = companyDAO.get(companyId);
			if (company != null) {
				CompanySearchTracking companySearchTracking = companySearchTrackingDAO.findCompanySearchTrackingByCompanyId(companyId);
				if (companySearchTracking == null) {
					companySearchTracking = new CompanySearchTracking();
				}
				companySearchTracking.setTrackingCompany(company);
				companySearchTracking.setTrackedCompanyIds(trackedCompanies);
				companySearchTracking.setEmailTo(email);
				companySearchTrackingDAO.saveOrUpdate(companySearchTracking);
			}
		}
		return null;
	}

	@Override
	public boolean hasWorkPastDueMoreThanXDays(long companyId, int pastDueDays) {
		return companyDAO.hasWorkPastDueMoreThanXDays(companyId, pastDueDays);
	}

	@Override
	public void saveEmployeeSettings(Long companyId, EmployeeSettingsDTO dto) {
		Company company = companyDAO.findById(companyId);
		company.setHidePricing(dto.isHidePricing());
	}

	@Override
	public void updateListInVendorSearch(long companyId, Boolean wantToListInVendorSearch) {
		boolean shouldListInVendorSearch = shouldListInVendorSearch(companyId, BooleanUtils.toBoolean(wantToListInVendorSearch));
		Company company = companyDAO.findById(companyId);
		listInVendorSearch(company, shouldListInVendorSearch);
	}

	@Override
	public boolean shouldListInVendorSearch(long companyId, boolean wantToListInVendorSearch) {
		boolean isBuyerCompany = getCustomerType(companyId).equals(CustomerType.BUYER.value());
		return wantToListInVendorSearch && (isEligibleToBeListedInVendorSearch(companyId) || isBuyerCompany);
	}

	@Override
	public boolean isEligibleToBeListedInVendorSearch(long companyId) {
		return hasAtLeastOneActiveDispatcher(companyId) && hasAtLeastOneActiveWorker(companyId);
	}

	@Override
	public boolean hasAtLeastOneActiveWorker(long companyId) {
		return companyDAO.hasAtLeastOneUserWithActiveRoles(companyId, AclRole.ACL_WORKER, AclRole.ACL_SHARED_WORKER);
	}

	@Override
	public boolean hasAtLeastOneActiveDispatcher(long companyId) {
		return companyDAO.hasAtLeastOneUserWithActiveRoles(companyId, AclRole.ACL_DISPATCHER);
	}

	@Override
	public void listInVendorSearch(Company company, boolean shouldListInVendorSearch) {
		company.setInVendorSearch(shouldListInVendorSearch);
		saveOrUpdateCompany(company);
		final VendorSearchIndexEvent vendorSearchIndexEvent = new VendorSearchIndexEvent(company.getId());
		if (!shouldListInVendorSearch) {
			vendorSearchIndexEvent.setDelete(true);
		}
		eventRouter.sendEvent(vendorSearchIndexEvent);
	}

	@Override
	public void updateListInVendorSearch(long companyId) {
		if ( ! isEligibleToBeListedInVendorSearch(companyId)) {
			Company company = companyDAO.findById(companyId);
			listInVendorSearch(company, false);
		}
	}

	@Override
	public int getMaxCompanyId() {
		return companyDAO.getMaxCompanyId();
	}

	@Override
	public List<Long> findCompanyIdsForUsers(List<Long> userIds) {
		List<Long> companies = Lists.newArrayList();

		List<List<Long>> partitionIds = partition(userIds, 500);
		for (List<Long> part : partitionIds) {
			List<Long> result = companyDAO.findCompanyIdsForUserIds(part);
			if (isNotEmpty(result)) {
				companies.addAll(result);
			}
		}
		return companies;
	}

	@Override
	public List<String> findCompanyNumbersFromCompanyIds(Collection<Long> companyIds) {
		Assert.notNull(companyIds);
		if (companyIds.isEmpty()) {
			return Collections.emptyList();
		}

		return companyDAO.findCompanyNumbersFromCompanyIds(companyIds);
	}

	@Override
	public List<String> getCompanyUuidsForCompanyNumbers(Collection<String> companyNumbers) {
		if (CollectionUtils.isEmpty(companyNumbers)) {
			return Collections.emptyList();
		}

		return companyDAO.findCompanyUuidsByCompanyNumbers(companyNumbers);
	}

	@Override
	public List<String> getCompanyUuidsForCompanyIds(Collection<Long> companyIds) {
		if (CollectionUtils.isEmpty(companyIds)) {
			return Collections.emptyList();
		}

		return companyDAO.findCompanyUuidsByCompanyIds(companyIds);
	}

	@Override
	public String getCustomerType(Long companyId) {
		String customerType = null;
		Company company = findById(companyId);
		if (company != null) {
			customerType = company.getCustomerType();
		}
		return customerType;
	}

	@Override
	public CompanyPreference getCompanyPreference(Long companyId) {
		Assert.notNull(companyId);
		Company company = findCompanyById(companyId);
		Assert.notNull(company);
		return company.getCompanyPreference();
	}

	@Override
	public void setCustomerType(Long companyId, String customerType) {
		Assert.notNull(companyId);
		Assert.notNull(customerType);

		Company company = companyDAO.get(companyId);
		Assert.notNull(company);

		company.setCustomerType(customerType);
		companyDAO.saveOrUpdate(company);
	}

	@Override
	public void setOverview(final long companyId, final String overview) {
		Assert.notNull(overview);

		Company company = companyDAO.get(companyId);
		Assert.notNull(company);

		company.setOverview(overview);
		companyDAO.saveOrUpdate(company);
	}

	@Override
	public boolean isApplicableToRenderOnboardingProgress(Long companyId) {
		Company company = companyDAO.findCompanyById(companyId);
		Calendar createdOn = company.getCreatedOn();
		return !createdOn.before(DateUtilities.getCalendarFromISO8601(COMPANY_ONBOARDING_ROLLOUT_DATE));
	}

	@Override
	public List<String> findWorkerNumbers(String companyNumber) {
		Assert.notNull(companyNumber);
		return companyDAO.findWorkerNumbers(companyNumber);

	}

	@Override
	public Company findByUUID(String uuid) {
		return companyDAO.findBy("uuid", uuid);
	}

	@Override
	public List<Long> getCompanyIdsByUuids(final Collection<String> uuids) {
		if (CollectionUtils.isEmpty(uuids)) {
			return Collections.emptyList();
		}
		return companyDAO.findCompanyIdsByUuids(uuids);
	}

	@Override
	public Long findCompanyIdByUuid(String companyUuid) {
		return companyDAO.findCompanyIdByUuid(companyUuid);
	}

	@Override
	public List<Long> findVendorIdsFromCompanyIds(final Collection<Long> companyIds) {
		Assert.notNull(companyIds);
		if (companyIds.isEmpty()) {
			return Collections.emptyList();
		}

		return companyDAO.findVendorIdsFromCompanyIds(companyIds);
	}

	@Override
	public int getTeamSize(Long companyId) {
		Assert.notNull(companyId);
		return companyDAO.getTeamSize(companyId);
	}

	@Override
	public Location saveCompanyLocation(Long companyId, LocationDTO locationDTO) {
		Assert.notNull(companyId);
		Assert.notNull(locationDTO);

		Address address = new Address();
		State state = invariantDataService.findStateWithCountryAndState(
			Country.valueOf(locationDTO.getCountry()).getId(), StringUtils.isEmpty(locationDTO.getState()) ? Constants.NO_STATE : locationDTO.getState()
		);
		if (state != null) {
			address.setState(state);
			address.setCountry(state.getCountry());
		} else {
			addressService.addNewStateToAddress(address, locationDTO.getCountry(), locationDTO.getState());
			address.setCountry(Country.valueOf(locationDTO.getCountry()));
		}

		address.setState(state);
		address.setAddressType(new AddressType(AddressType.SERVICE_AREA));
		address.setAddress1(locationDTO.getAddressLine1());
		address.setAddress2(locationDTO.getAddressLine2());
		address.setCity(locationDTO.getCity());
		address.setPostalCode(locationDTO.getZip());
		address.setCountry(Country.newInstance(locationDTO.getCountry()));
		if (locationDTO.getLatitude() != null && locationDTO.getLongitude() != null) {
			address.setLatitude(BigDecimal.valueOf(locationDTO.getLatitude()));
			address.setLongitude(BigDecimal.valueOf(locationDTO.getLongitude()));
		}

		Location location = new Location();
		location.setAddress(address);
		location.setCompany(findById(companyId));
		locationDAO.saveOrUpdate(location);
		return location;
	}

	@Override
	public Skill saveCompanySkill(String skill) {
		SkillDTO skillDTO = new SkillDTO(skill);
		skillDTO.setDescription(skill);
		return skillService.saveOrUpdateSkill(skillDTO);
	}

	@Override
	public void addCompanyLocation(Long companyId, Long locationId) {
		Assert.notNull(companyId);
		Assert.notNull(locationId);

		Company company = companyDAO.findCompanyById(companyId);
		Location location = locationDAO.findLocationById(locationId);
		companyLocationAssociationDAO.addCompanyLocation(location, company);
	}

	@Override
	public void removeCompanyLocation(Long companyId, Long locationId) {
		Assert.notNull(companyId);
		Assert.notNull(locationId);

		Company company = companyDAO.findCompanyById(companyId);
		Location location = locationDAO.findLocationById(locationId);
		companyLocationAssociationDAO.removeCompanyLocation(location, company);
	}

	@Override
	public List<Location> getCompanyLocations(Long companyId) {
		Assert.notNull(companyId);
		Company company = companyDAO.findCompanyById(companyId);
		return companyLocationAssociationDAO.findCompanyLocations(company);
	}

	@Override
	public void setCompanyLocations(List<Long> locationIds, Long companyId) {
		Assert.notNull(companyId);
		Company company = companyDAO.findCompanyById(companyId);
		List<CompanyLocationAssociation> locationsServiced = companyLocationAssociationDAO.findCompanyLocationAssociations(company);

		List<Long> newLocationIds = Lists.newArrayList(locationIds);

		for(CompanyLocationAssociation locationServiced : locationsServiced) {
			if(newLocationIds.contains(locationServiced.getLocation().getId().intValue())) {
				locationServiced.setDeleted(false);
				newLocationIds.remove(Integer.valueOf(locationServiced.getLocation().getId().intValue()));
			} else {
				locationServiced.setDeleted(true);
			}
		}

		for(Long newLocationId : newLocationIds) {
			addCompanyLocation(companyId, newLocationId);
		}
	}

	@Override
	public void addCompanySkill(Long companyId, Long skillId) {
		Assert.notNull(companyId);
		Assert.notNull(skillId);

		Company company = companyDAO.findCompanyById(companyId);
		Skill skill = skillDAO.findSkillById(skillId);
		companySkillAssociationDAO.addCompanySkill(skill, company);
	}

	@Override
	public void removeCompanySkill(Long companyId, Long skillId) {
		Assert.notNull(companyId);
		Assert.notNull(skillId);

		Company company = companyDAO.findCompanyById(companyId);
		Skill skill = skillDAO.findSkillById(skillId);
		companySkillAssociationDAO.removeCompanySkill(skill, company);
	}

	@Override
	public List<Skill> getCompanySkills(Long companyId) {
		Assert.notNull(companyId);
		Company company = companyDAO.findCompanyById(companyId);
		return companySkillAssociationDAO.findCompanySkills(company);
	}

	@Override
	public void setCompanySkills(List<Long> skillIds, Long companyId) {
		Assert.notNull(companyId);
		Company company = companyDAO.findCompanyById(companyId);

		List<CompanySkillAssociation> skillAssociations = companySkillAssociationDAO.findCompanySkillAssociations(company);

		List<Long> newSkillIds = Lists.newArrayList(skillIds);

		for(CompanySkillAssociation skillAssociation : skillAssociations) {

			if(newSkillIds.contains(skillAssociation.getSkill().getId().longValue())) {
				skillAssociation.setDeleted(false);
				newSkillIds.remove(Long.valueOf(skillAssociation.getSkill().getId().longValue()));
			} else {
				skillAssociation.setDeleted(true);
			}
		}

		for(Long newSkillId : newSkillIds) {
			addCompanySkill(companyId, newSkillId);
		}
	}

	@Override
	public List<String> findWorkerNumbersForCompanies(List<String> companyNumbers) {
		return companyDAO.findWorkerNumbersForCompanies(companyNumbers);
	}

	@Override
	public boolean isFastFundsEnabled(Long companyId) {
		Assert.notNull(companyId);
		return isFastFundsEnabled(findCompanyById(companyId));
	}

	@Override
	public boolean isFastFundsEnabled(Company company) {
		Assert.notNull(company);
		MutableBoolean mutableFastFundsEnabled = new MutableBoolean(false);
		doorman.welcome(new CompanyGuest(company), new FastFundsRope(mutableFastFundsEnabled));
		return mutableFastFundsEnabled.isTrue();
	}
}
