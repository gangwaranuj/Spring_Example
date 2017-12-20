package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.data.aggregate.CompanyAggregate;
import com.workmarket.data.aggregate.CompanyAggregatePagination;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentTermsDuration;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.company.CompanySearchTracking;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.web.controllers.users.EmployeeSettingsDTO;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CompanyService {

	/**
	 * Creates and saves a new company
	 *
	 * @param name
	 * @param operatingAsIndividualFlag
	 * @return Company
	 */
	Company createCompany(String name, boolean operatingAsIndividualFlag, String customerType);

	/**
	 * Finds a company by id, basic
	 *
	 * @param companyId
	 * @return
	 */
	Company findById(Long companyId);

	List<CompanyIdentityDTO> findCompanyIdentitiesByCompanyNumbers(Collection<String> companyNumbers);

	List<CompanyIdentityDTO> findCompanyIdentitiesByUuids(Collection<String> uuids);

	List<CompanyIdentityDTO> findCompanyIdentitiesByIds(Collection<Long> ids);

	/**
	 * Also finds a company by id, with more attributes
	 *
	 * @param companyId
	 * @return
	 */
	Company findCompanyById(Long companyId);

	Company findCompanyByNumber(String companyNumber);

	Company findCompanyByName(String companyName);

	Company findCompanyByEncryptedId(String encryptedId);

	CompanyAggregatePagination findAllCompanies(CompanyAggregatePagination pagination);

	CompanyAggregate findCompanyAggregate(Long companyId);

	Set<User> findCompanyUsersOfCompanyUserGroups(Long userId);

	Coordinate findLatLongForCompany(Long companyId);

	void saveOrUpdateCompany(Company company) ;

	/**
	 * Update company with only the properties specified in the map.
	 *
	 * @param companyId
	 * @param properties
	 *            - map of { property name => property value } property names have to be camel cased
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 *
	 * TODO: service methods should not throw reflection exceptions
	 */
	void updateCompanyProperties(Long companyId, Map<String, String> properties) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException ;

	/**
	 * Sets the company status to LOCKED
	 *
	 * @param companyId
	 */
	void lockCompanyAccount(Long companyId);

    /**
     * Mark a company as VIP
     * @param companyId
     * @param vip True to set this company as VIP; false otherwise
     */
    void markAsVip(Long companyId, boolean vip);

	/**
	 * Sets the company status to ACTIVE. Current user must be an INTERNAL user
	 *
	 * @param companyId
	 * @
	 */
	@Deprecated
	void unlockCompanyAccount(Long companyId, int unlockedPeriodGraceHours) ;

	void unlockCompanyAccount(Long companyId);

	/**
	 * Returns TRUE if the company has payment terms enabled.
	 *
	 * @param companyId
	 * @return Boolean
	 */
	boolean hasPaymentTermsEnabled(Long companyId);

	/**
	 * Override a company's ability to enable payment terms without the need for a linked bank account.
	 *
	 * @param companyId
	 * @param note
	 */
	void overridePaymentTerms(Long companyId, String note);

	List<PaymentTermsDuration> findPaymentTermsDurations(Long companyId);

	/**
	 * Check whether or not a company has any confirmed bank accounts.
	 *
	 * @param companyId
	 * @return
	 */
	Boolean hasConfirmedBankAccounts(Long companyId);

	String getNextInvoiceSummaryNumber(Company company);
	String getNextStatementNumber(Long companyId);

	CompanyAssetAssociation findCompanyAvatars(Long companyId);
	CompanyAssetAssociation findPreviousCompanyAvatars(Long companyId);

	Integer getDefaultPaymentTermsDays();

	ManageMyWorkMarket getManageMyWorkMarket(Long companyId);

	void saveAssignmentAlertEmailToCompany(Long companyId, EmailAddressDTO emailDTO);

	/**
	 * Saves the default emails addresses to send the subscription invoices.
	 * If emailDTOs is Empty it will clear the current setting.
	 *
	 * @param companyId
	 * @param emailDTOs
	 */
	void saveOrUpdateSubscriptionInvoicesEmailToCompany(Long companyId, List<EmailAddressDTO> emailDTOs);

	void setPaymentTermsDurations(Long companyId, List<Integer> paymentTermsDurations);

	/**
	 * Get payment configuration for a company.
	 *
	 * @param companyId
	 * @return
	 */
	PaymentConfiguration getPaymentConfiguration(Long companyId);

	void saveOrUpdatePaymentCalculatorType(Long companyId, Integer paymentCalculatorType);

	List<Long> findAllCompaniesWithWorkPayment(Calendar fromDate);

	List<Long> findAllCompaniesWithWorkCancellations(Calendar fromDate);

	boolean doesCompanyHaveReservedFundsEnabledProject(Long companyId);

	void resetLowBalanceAlertSentToday(Long companyId);

	List<Long> findCompanyIdsWithAgingAlert();

	Map<Long, Company> getCompaniesByIds(Collection<Long> companyIds);

	boolean isInstantWorkerPoolEnabled(Long companyId);

	void updateCompanyPreference(CompanyPreference companyPreference);

	boolean doesCompanyHaveOverdueInvoice(Long companyId, Calendar calendar);

	/**
	 * Check if the company still have due/overdue invoices
	 * If there is none, clear overdueAccountWarningSentOn/lockAccountWarningSentOn
	 *
	 * @param companyId
	 * @return
	 */
	void processDueInvoicesForCompany(long companyId);

	String getCompanySignUpPricingPlan(long companyId);

	//User Search tracking
	CompanySearchTracking saveCompanySearchTrackingSetting(long companyId, String email, Set<Long> trackedCompanies);

	boolean hasWorkPastDueMoreThanXDays(long companyId, int pastDueDays);

	void saveEmployeeSettings(Long companyId, EmployeeSettingsDTO dto);

	void updateListInVendorSearch(long companyId);

	void updateListInVendorSearch(long companyId, Boolean shouldListInVendorSearch);

	boolean shouldListInVendorSearch(long companyId, boolean shouldListInVendorSearch);

	boolean isEligibleToBeListedInVendorSearch(long companyId);

	boolean hasAtLeastOneActiveWorker(long companyId);

	boolean hasAtLeastOneActiveDispatcher(long companyId);

	void listInVendorSearch(Company company, boolean shouldListInVendorSearch);

	int getMaxCompanyId();

	List<Long> findCompanyIdsForUsers(List<Long> userIds);

	List<String> findCompanyNumbersFromCompanyIds(Collection<Long> companyId);

	List<String> getCompanyUuidsForCompanyNumbers(Collection<String> companyNumbers);

	List<String> getCompanyUuidsForCompanyIds(Collection<Long> companyIds);

	String getCustomerType(Long companyId);

	CompanyPreference getCompanyPreference(Long companyId);

	void setCustomerType(Long companyId, String customerType);

	void setOverview(long companyId, String overview);

	ImmutableList<Map> getProjectedPaymentTermsDurations(String[] fields) throws Exception;

	boolean isApplicableToRenderOnboardingProgress(Long companyId);

	List<String> findWorkerNumbers(String companyNumber);

	Company findByUUID(String uuid);

	List<Long> getCompanyIdsByUuids(Collection<String> uuids);

	Long findCompanyIdByUuid(String companyUuid);

	List<Long> findVendorIdsFromCompanyIds(Collection<Long> companyIds);

	int getTeamSize(Long vendorId);

	Location saveCompanyLocation(Long companyId, LocationDTO locationDTO);

	Skill saveCompanySkill(String skill);

	void addCompanyLocation(Long companyId, Long locationId);

	void removeCompanyLocation(Long companyId, Long locationId);

	List<Location> getCompanyLocations(Long companyId);

	void setCompanyLocations(List<Long> locationIds, Long companyId);

	void addCompanySkill(Long companyId, Long skillId);

	void removeCompanySkill(Long companyId, Long skillId);

	List<Skill> getCompanySkills(Long companyId);

	void setCompanySkills(List<Long> skillIds, Long companyId);

	List<String> findWorkerNumbersForCompanies(List<String> companyNumbers);

	boolean isFastFundsEnabled(Long companyId);

	boolean isFastFundsEnabled(Company company);
}
