package com.workmarket.dao.company;

import com.workmarket.dao.DAOInterface;
import com.workmarket.data.aggregate.CompanyAggregatePagination;
import com.workmarket.domains.model.Company;
import com.workmarket.service.business.dto.CompanyIdentityDTO;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CompanyDAO extends DAOInterface<Company> {

	Company findCompanyById(Long id);

	Company findCompanyByNumber(String companyNumber);

	Company findCompanyByName(String companyName);

	Company findById(Long id);

	Company findByUuid(String uuid);

	Long findCompanyIdByUuid(String userUuid);

	List<Long> findCompanyIdsByUuids(Collection<String> uuids);

	List<CompanyIdentityDTO> findCompanyIdentitiesByCompanyNumbers(Collection<String> companyNumbers);

	List<CompanyIdentityDTO> findCompanyIdentitiesByUuids(Collection<String> uuids);

	List<CompanyIdentityDTO> findCompanyIdentitiesByIds(Collection<Long> ids);

	List<Company> findSimilarCompaniesByName(Long companyId, String matchName);

	CompanyAggregatePagination findAllCompanies(CompanyAggregatePagination pagination);

	Integer countAllLane3UsersWithEINsByCompany(Long companyId);

	List<Long> findCompaniesWithAgingAlert();

	List<Long> findAllCompaniesWithLowBalanceAlertEnabled();

	List<Long> findAllCompaniesWithWorkPayment(Calendar fromDate);

	List<Long> findAllCompaniesWithWorkCancellations(Calendar fromDate);

	boolean doesCompanyHaveReservedFundsEnabledProject(Long companyId);

	boolean isPaymentTermsEnabledForCompany(Long companyId);

	Map<Long, Map<String, String>> getAllCompaniesForCache(Set<Long> companyIds);

	boolean isInstantWorkerPoolEnabled(Long companyId);

	Company getSharingCompany(Long userGroupId);

	boolean doesCompanyHaveOverdueInvoice(Long companyId, Calendar calendar);

	boolean hasWorkPastDueMoreThanXDays(long companyId, int pastDueDays);

	boolean hasAtLeastOneUserWithActiveRoles(long companyId, Long... aclRoleIds);

	List<Long> getUserIdsWithActiveRole(long companyId, long aclRoleId);

	Integer getMaxCompanyId();

	List<Long> findCompanyIdsForUserIds(List<Long> userIds);

	List<String> findCompanyNumbersFromCompanyIds(Collection<Long> companyIds);

	List<String> findCompanyUuidsByCompanyNumbers(Collection<String> companyNumbers);

	List<String> findCompanyUuidsByCompanyIds(Collection<Long> companyIds);

	List<String> findWorkerNumbers(String companyNumber);

	List<Long> findVendorIdsFromCompanyIds(Collection<Long> companyIds);

	int getTeamSize(Long companyId);

	List<String> findWorkerNumbersForCompanies(List<String> companyNumbers);

	List<Company> suggest(String prefix, boolean vendorOnly);
}
