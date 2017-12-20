package com.workmarket.search.cache;

import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.data.report.work.WorkSubStatusTypeCompanyConfig;
import com.workmarket.domains.model.company.CompanyHydrateData;
import com.workmarket.domains.groups.model.UserGroupHydrateData;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.rating.AverageRating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface to all data that's used to fill out search results. Because the
 * services need to be fast, the entire database of hydratable data will be
 * cached in memory. It will update any changes from the database once every X
 * minutes. When lookups are done and cache misses are found, they are looked up
 * in the database. If they are not found then it assume a stale expired data
 * piece was returned.
 *
 */
public interface HydratorCache {
	Map<Long, AverageRating> findAverageForUsersByCompany(ArrayList<Long> newArrayList, Long id);

	Map<Long, String> findAllAssessmentNamesToHydrateSearchData(Set<Long> assessmentIdsInResponse);

	Map<Long, Integer> getCompletedWorkCountToHydrateSearchData(Set<Long> userIdsInResponse, long companyId);

	Map<Long, String> findAllCertificationNamesToHydrateSearchData(Set<Long> certificationIdsInResponse);

	Map<Long, String> findAllIndustryNamesToHydrateSearchData(Set<Long> industryIdsInResponse);

	Map<Long, UserGroupHydrateData> findAllGroupHydrateSearchData(Set<Long> groupIdsInResponse);

	Map<Long, CompanyHydrateData> findAllCompanyHydrateSearchData(Set<Long> companyIdsInResponse);

	Map<Long, License> findAllLicenseNames(Set<Long> licenseIdsInResponse);

	Map<Long, String> findAllInsuranceNamesByInsuranceId(Set<Long> insuranceIdsInResponse);

	WorkSubStatusTypeCompanyConfig getLabelDisplayInfo(Long labelId, Long companyId);

	void updateWorkLabel(long labelId, WorkSubStatusTypeCompanyConfig workSubStatusTypeCompanyConfig);

	void hydrateInsuranceCache();

	void hydrateCertificationCache();

	void hydrateLicenseCache();

	void hydrateGroupCache();

	void hydrateAssessmentsCache();

	void hydrateWorkLabels();

	void hydrateCompanyInfo();

	void updateCompanyCache(long companyId);

	void updateGroupCache(long groupId);

	Map<Long, List<CustomFieldReportRow>> getDashboardOwnerWorkCustomFieldsMap(Long companyId, Collection<Long> workIds);
}
