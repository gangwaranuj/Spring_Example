package com.workmarket.search.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.dao.assessment.AbstractAssessmentDAO;
import com.workmarket.dao.certification.CertificationDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.dao.insurance.InsuranceDAO;
import com.workmarket.dao.license.LicenseDAO;
import com.workmarket.dao.state.WorkSubStatusDAO;
import com.workmarket.dao.state.WorkSubStatusTypeCompanySettingDAO;
import com.workmarket.dao.summary.work.WorkHistorySummaryDAO;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.data.report.work.WorkSubStatusTypeCompanyConfig;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.groups.model.UserGroupHydrateData;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.company.CompanyHydrateData;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.rating.AverageRating;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisCacheFilters;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.dto.IndustryDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Since we have a different project and we're avoiding a dependency on JMS,
 * this is a simple hydration cache for all hydration methods that hit a
 * database.
 *
 * Since a search only returns simple data (usually name lookups) we are going
 * to cache the entire database with the search hydration data and perform
 * incremental updates
 *
 * Upon startup, the entire cache gets warmed up, this is an optional field
 */
@Service
public class HydratorCacheImpl implements HydratorCache {
	private static final Log logger = LogFactory.getLog(HydratorCacheImpl.class);

	@Autowired private CertificationDAO certificationDAO;
	@Autowired private IndustryService industryService;
	@Autowired private LicenseDAO licenseDAO;
	@Autowired private UserGroupDAO groupDAO;
	@Autowired private AbstractAssessmentDAO assessmentsDAO;
	@Autowired private RatingService ratingService;
	@Autowired private InsuranceDAO insuranceDAO;
	@Autowired private WorkHistorySummaryDAO workHistorySummaryDAO;
	@Autowired private WorkSubStatusDAO workSubStatusDAO;
	@Autowired private WorkCustomFieldDAO workCustomFieldDAO;
	@Autowired private JsonSerializationService jsonSerializationService;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private WorkSubStatusTypeCompanySettingDAO workSubStatusTypeCompanySettingDAO;

	private static long TWO_WEEKS_IN_SECONDS = TimeUnit.DAYS.toSeconds(14);

	@Override
	public void hydrateInsuranceCache() {
		Map<Long, String> insurances = insuranceDAO.findAllInsuranceNamesAndId();
		for (Entry<Long, String> entry : insurances.entrySet()) {
			redisAdapter.set(RedisCacheFilters.getInsuranceHashKey(entry.getKey()), entry.getValue(), TWO_WEEKS_IN_SECONDS);
		}
		logger.info("Insurance cache populated with " + insurances.size() + " elements.");
	}

	@Override
	public void hydrateCertificationCache() {
		for (Certification certification: certificationDAO.findAll()) {
			redisAdapter.set(RedisCacheFilters.getCertificationHashKey(certification.getId()), certification.getName(), TWO_WEEKS_IN_SECONDS);
		}
		logger.info("Certification name cache has been populated ");
	}

	@Override
	public void hydrateLicenseCache() {
		List<License> licenses = licenseDAO.findAll();
		for (License license : licenses) {
			redisAdapter.set(RedisCacheFilters.getLicenseHashKey(license.getId()), jsonSerializationService.toJson(license), TWO_WEEKS_IN_SECONDS);
		}
		logger.info("License name cache populated with " + licenses.size() + " elements.");
	}

	@Override
	public void hydrateGroupCache() {
		Map<Long, UserGroupHydrateData> groupResult = groupDAO.findAllCompanyUserGroupHydrateData();
		for (Entry<Long, UserGroupHydrateData> entry : groupResult.entrySet()) {
			redisAdapter.set(RedisCacheFilters.getHydrateGroupHashKey(entry.getKey()), jsonSerializationService.toJson(entry.getValue()), TWO_WEEKS_IN_SECONDS);
		}
		logger.info("Group name cache populated with " + groupResult.size() + " elements.");
	}

	@Override
	public void hydrateAssessmentsCache() {
		Map<Long, String> assessmentResult = assessmentsDAO.findAllAssessmentNamesToHydrateSearchData();
		for (Entry<Long, String> entry : assessmentResult.entrySet()) {
			redisAdapter.set(RedisCacheFilters.getAssessmentHashKey(entry.getKey()), entry.getValue(), TWO_WEEKS_IN_SECONDS);
		}
		logger.info("Assessment cache populated with " + assessmentResult.size() + " elements.");
	}

	@Override
	public void hydrateWorkLabels() {
		Map<String, WorkSubStatusTypeCompanyConfig> elements = workSubStatusDAO.findAllWorkSubStatusColorConfiguration();

		for (Map.Entry<String, WorkSubStatusTypeCompanyConfig> entry : elements.entrySet()) {
			updateWorkLabel(entry.getKey(), entry.getValue());
		}

		logger.info("Work labels cache populated with " + elements.size() + " elements.");
	}

	@Override
	public void hydrateCompanyInfo() {
		Map<Long, Map<String, String>> objectEntries =  companyDAO.getAllCompaniesForCache(Collections.<Long>emptySet());
		for (Map.Entry<Long, Map<String, String>> entry : objectEntries.entrySet()) {
			redisAdapter.setAll(RedisFilters.companyHashKeyFor(entry.getKey()), entry.getValue(), TWO_WEEKS_IN_SECONDS);
		}
	}

	@Override
	public void updateCompanyCache(long companyId) {
		hydrateCompanyInfo(Sets.newHashSet(companyId));
	}

	@Override
	public void updateGroupCache(long groupId) {
		Map<Long, UserGroupHydrateData> groups = groupDAO.findAllCompanyUserGroupHydrateDataByGroupIds(Sets.newHashSet(groupId));
		// add to cache
		for (Entry<Long, UserGroupHydrateData> entry : groups.entrySet()) {
			redisAdapter.set(
				RedisCacheFilters.getHydrateGroupHashKey(entry.getKey()),
				jsonSerializationService.toJson(entry.getValue()),
				TWO_WEEKS_IN_SECONDS
			);
		}
	}

	private Map<Long, CompanyHydrateData> hydrateCompanyInfo(Set<Long> companyIds) {
		if (CollectionUtils.isEmpty(companyIds)) {
			return Collections.emptyMap();
		}
		final Map<Long, CompanyHydrateData> hydrateDataMap = Maps.newHashMapWithExpectedSize(companyIds.size());
		final Map<Long, Map<String, String>> mapOfCompanies = companyDAO.getAllCompaniesForCache(companyIds);
		for (Map.Entry<Long, Map<String, String>> entry : mapOfCompanies.entrySet()) {
			redisAdapter.setAll(RedisFilters. companyHashKeyFor(entry.getKey()), entry.getValue(), TWO_WEEKS_IN_SECONDS);

			CompanyHydrateData companyHydrateData = new CompanyHydrateData();
			companyHydrateData.setApprovedTIN(MapUtils.getBoolean(entry.getValue(), "approvedTIN"));
			companyHydrateData.setCompanyStatusType(MapUtils.getString(entry.getValue(), "status"));
			companyHydrateData.setId(entry.getKey());
			companyHydrateData.setName(MapUtils.getString(entry.getValue(), "name"));
			companyHydrateData.setConfirmedBankAccount(MapUtils.getBoolean(entry.getValue(), "confirmedBankAccount"));
			hydrateDataMap.put(entry.getKey(), companyHydrateData);
		}
		return hydrateDataMap;
	}

	@Override
	public Map<Long, List<CustomFieldReportRow>> getDashboardOwnerWorkCustomFieldsMap(Long companyId, Collection<Long> workIds) {
		if (CollectionUtils.isEmpty(workIds)) {
			return Collections.emptyMap();
		}
		CustomFieldReportFilters customFieldReportFilters = new CustomFieldReportFilters();
		customFieldReportFilters.setVisibleToBuyer(true);
		customFieldReportFilters.setShowOnDashboard(true);
		customFieldReportFilters.setWorkIds(Lists.newArrayList(workIds));
		//TODO: Alex - move to a cache
		return workCustomFieldDAO.getWorkCustomFieldsMap(null, companyId, customFieldReportFilters);
	}

	@Override
	public Map<Long, String> findAllInsuranceNamesByInsuranceId(Set<Long> insuranceIds) {
		CacheBuilder insuranceCache = new CacheBuilder(insuranceIds);

		List<Object> allValues = redisAdapter.getMultiple(insuranceCache.buildKeys(RedisCacheFilters.INSURANCE_HASH_KEY));
		insuranceCache.setCachedObjectValues(allValues);
		insuranceCache.synchronize();

		if (CollectionUtils.isNotEmpty(insuranceCache.getMisses())) {
			Map<Long, String> missResults = insuranceDAO.findAllInsuranceNamesByInsuranceId(insuranceCache.getMisses());
			// add to cache
			for (Entry<Long, String> entry : missResults.entrySet()) {
				redisAdapter.set(RedisCacheFilters.getInsuranceHashKey(entry.getKey()), entry.getValue(), TWO_WEEKS_IN_SECONDS);
				insuranceCache.addToEntityIdNameMap(entry.getKey(), entry.getValue());
			}
		}
		return insuranceCache.getEntityIdNameMap();
	}

	@Override
	public WorkSubStatusTypeCompanyConfig getLabelDisplayInfo(Long labelId, Long companyId) {
		Map<Object, Object> results = redisAdapter.getAllForHash(RedisFilters.labelConfigKeyFor(labelId, companyId));

		WorkSubStatusTypeCompanyConfig workSubStatusTypeCompanyConfig = new WorkSubStatusTypeCompanyConfig();
		if (results.isEmpty()) {
			WorkSubStatusTypeCompanySetting settings = workSubStatusTypeCompanySettingDAO.findWorkSubStatusTypeCompanySettingByWorkSubStatusAndCompany(labelId, companyId);
			workSubStatusTypeCompanyConfig.setCompanyId(companyId);
			if (settings == null) {
				workSubStatusTypeCompanyConfig.setColorRgb(StringUtils.EMPTY);
				workSubStatusTypeCompanyConfig.setDashboardDisplayType(WorkSubStatusTypeCompanySetting.DashboardDisplayType.SHOW);
			} else {
				workSubStatusTypeCompanyConfig.setColorRgb(settings.getColorRgb());
				workSubStatusTypeCompanyConfig.setDashboardDisplayType(settings.getDashboardDisplayType());
			}

			updateWorkLabel(labelId, workSubStatusTypeCompanyConfig);
		} else {
			workSubStatusTypeCompanyConfig.setDashboardDisplayType(WorkSubStatusTypeCompanySetting.DashboardDisplayType.valueOf(
					(String) results.get(WorkSubStatusTypeCompanyConfig.CACHE_HASH_DISPLAY))
			);
			workSubStatusTypeCompanyConfig.setColorRgb((String) results.get(WorkSubStatusTypeCompanyConfig.CACHE_HASH_COLOR));
		}

		return workSubStatusTypeCompanyConfig;
	}


	private void updateWorkLabel(String labelIdCompanyIdKey, WorkSubStatusTypeCompanyConfig workSubStatusTypeCompanyConfig) {
		updateWorkLabel(Long.valueOf(StringUtils.substringBefore(labelIdCompanyIdKey, "_")), workSubStatusTypeCompanyConfig);
	}

	@Override
	public void updateWorkLabel(long labelId, WorkSubStatusTypeCompanyConfig workSubStatusTypeCompanyConfig) {
		Map<String, String> fieldValues = Maps.newHashMapWithExpectedSize(2);
		fieldValues.put(WorkSubStatusTypeCompanyConfig.CACHE_HASH_COLOR, workSubStatusTypeCompanyConfig.getColorRgb());
		fieldValues.put(WorkSubStatusTypeCompanyConfig.CACHE_HASH_DISPLAY, workSubStatusTypeCompanyConfig.getDashboardDisplayType().toString());
		redisAdapter.setAll(
			RedisFilters.labelConfigKeyFor(labelId, workSubStatusTypeCompanyConfig.getCompanyId()),
			fieldValues,
			TWO_WEEKS_IN_SECONDS
		);
	}

	@Override
	public Map<Long, AverageRating> findAverageForUsersByCompany(ArrayList<Long> userIds, Long companyId) {
		Map<Long, AverageRating> results = Maps.newHashMapWithExpectedSize(userIds.size());
		for (Long userId : userIds) {
			AverageRating rating = ratingService.findAverageRatingForUserByCompany(userId, companyId);
			if (rating != null) {
				results.put(userId, rating);
			}
		}

		return results;
	}

	@Override
	public Map<Long, String> findAllAssessmentNamesToHydrateSearchData(Set<Long> assessmentIdsInResponse) {
		CacheBuilder assessmentCache = new CacheBuilder(assessmentIdsInResponse);

		List<Object> allValues = redisAdapter.getMultiple(assessmentCache.buildKeys(RedisCacheFilters.ASSESSMENT_HASH_KEY));
		assessmentCache.setCachedObjectValues(allValues);
		assessmentCache.synchronize();

		if (CollectionUtils.isNotEmpty(assessmentCache.getMisses())) {
			Map<Long, String> missResults = assessmentsDAO.findAllAssessmentNamesToHydrateSearchData(assessmentCache.getMisses());
			// add to cache
			for (Entry<Long, String> entry : missResults.entrySet()) {
				redisAdapter.set(RedisCacheFilters.getAssessmentHashKey(entry.getKey()), entry.getValue(), TWO_WEEKS_IN_SECONDS);
				assessmentCache.addToEntityIdNameMap(entry.getKey(), entry.getValue());
			}
		}
		return assessmentCache.getEntityIdNameMap();
	}

	@Override
	public Map<Long, Integer> getCompletedWorkCountToHydrateSearchData(Set<Long> userIdsInResponse, long companyId) {
		return workHistorySummaryDAO.countWorkForCompany(Lists.newArrayList(userIdsInResponse), companyId, WorkStatusType.PAID);
	}

	@Override
	public Map<Long, String> findAllCertificationNamesToHydrateSearchData(Set<Long> certificationIdsInResponse) {
		CacheBuilder certificationCache = new CacheBuilder(certificationIdsInResponse);

		List<Object> allValues = redisAdapter.getMultiple(certificationCache.buildKeys(RedisCacheFilters.CERTIFICATION_HASH_KEY));
		certificationCache.setCachedObjectValues(allValues);
		certificationCache.synchronize();

		if (CollectionUtils.isNotEmpty(certificationCache.getMisses())) {
			Map<Long, String> missResults = certificationDAO.findAllCertificationNamesToHydrateSearchData(certificationCache.getMisses());
			// add to cache
			for (Entry<Long, String> entry : missResults.entrySet()) {
				redisAdapter.set(RedisCacheFilters.getCertificationHashKey(entry.getKey()), entry.getValue(), TWO_WEEKS_IN_SECONDS);
				certificationCache.addToEntityIdNameMap(entry.getKey(), entry.getValue());
			}
		}
		return certificationCache.getEntityIdNameMap();
	}

	@Override
	public Map<Long, String> findAllIndustryNamesToHydrateSearchData(Set<Long> industryIdsInResponse) {
		List<IndustryDTO> industries = industryService.getAllIndustryDTOs();
		Map<Long, String> industriesForResponse = Maps.newHashMapWithExpectedSize(industryIdsInResponse.size());
		for (IndustryDTO industry : industries) {
			if (industryIdsInResponse.contains(industry.getId())) {
				industriesForResponse.put(industry.getId(), industry.getName());
			}
		}

		return industriesForResponse;
	}

	@Override
	public Map<Long, UserGroupHydrateData> findAllGroupHydrateSearchData(Set<Long> groupIdsInResponse) {
		final Map<Long, UserGroupHydrateData> result = Maps.newLinkedHashMap();
		final List<Long> entityIds = Lists.newArrayList(groupIdsInResponse);
		final Set<Long> misses = Sets.newHashSet();
		final List<Object> cachedObjectValues = Lists.newArrayList();

		CacheBuilder groupCache = new CacheBuilder(groupIdsInResponse);

		List<Object> allValues = redisAdapter.getMultiple(groupCache.buildKeys(RedisCacheFilters.HYDRATE_GROUP_HASH_KEY));
		cachedObjectValues.addAll(allValues);
		for (int i = 0; i < cachedObjectValues.size(); i++) {
			Object object = cachedObjectValues.get(i);
			if (object == null) {
				misses.add(entityIds.get(i));
			} else {
				UserGroupHydrateData groupData = jsonSerializationService.fromJson((String) object, UserGroupHydrateData.class);
				result.put(groupData.getGroupId(), groupData);
			}
		}

		if (CollectionUtils.isNotEmpty(misses)) {
			Map<Long, UserGroupHydrateData> missResults = groupDAO.findAllCompanyUserGroupHydrateDataByGroupIds(misses);
			// add to cache
			for (Entry<Long, UserGroupHydrateData> entry : missResults.entrySet()) {
				redisAdapter.set(
					RedisCacheFilters.getHydrateGroupHashKey(entry.getKey()),
					jsonSerializationService.toJson(entry.getValue()),
					TWO_WEEKS_IN_SECONDS
				);
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	@Override
	public Map<Long, CompanyHydrateData> findAllCompanyHydrateSearchData(Set<Long> companyIdsInResponse) {
		Set<Long> misses = Sets.newHashSet();
		Map<Long, CompanyHydrateData> companyHydrateDataMap = Maps.newHashMapWithExpectedSize(companyIdsInResponse.size());
		for (Long id: companyIdsInResponse) {
			Map<Object, Object> fieldValueMap = redisAdapter.getAllForHash(RedisFilters.companyHashKeyFor(id));
			if (MapUtils.isEmpty(fieldValueMap)) {
				misses.add(id);
			} else {
				CompanyHydrateData companyHydrateData = new CompanyHydrateData();
				companyHydrateData.setId(id);
				companyHydrateData.setApprovedTIN(MapUtils.getBoolean(fieldValueMap, "approvedTIN"));
				companyHydrateData.setCompanyStatusType(MapUtils.getString(fieldValueMap, "status"));
				companyHydrateData.setName(MapUtils.getString(fieldValueMap, "name"));
				companyHydrateData.setConfirmedBankAccount(MapUtils.getBoolean(fieldValueMap, "confirmedBankAccount"));
				companyHydrateDataMap.put(id, companyHydrateData);
			}
		}

		if (CollectionUtils.isNotEmpty(misses)) {
			final Map<Long, CompanyHydrateData> missedData = hydrateCompanyInfo(misses);
			if (MapUtils.isNotEmpty(missedData)) {
				companyHydrateDataMap.putAll(missedData);
			}
		}
		return companyHydrateDataMap;
	}

	@Override
	public Map<Long, License> findAllLicenseNames(Set<Long> licenseIdsInResponse) {
		final Map<Long, License> result = Maps.newLinkedHashMap();
		final List<Long> entityIds = Lists.newArrayList(licenseIdsInResponse);
		final Set<Long> misses = Sets.newHashSet();
		final List<Object> cachedObjectValues = Lists.newArrayList();

		CacheBuilder licenseCache = new CacheBuilder(licenseIdsInResponse);

		List<Object> allValues = redisAdapter.getMultiple(licenseCache.buildKeys(RedisCacheFilters.LICENSE_HASH_KEY));
		cachedObjectValues.addAll(allValues);
		for (int i = 0; i < cachedObjectValues.size(); i++) {
			Object object = cachedObjectValues.get(i);
			if (object == null) {
				misses.add(entityIds.get(i));
			} else {
				License license = jsonSerializationService.fromJson((String) object, License.class);
				result.put(license.getId(), license);
			}
		}

		if (CollectionUtils.isNotEmpty(misses)) {
			List<License> missResults = licenseDAO.findAllLicenseNamesByIds(misses);
			// add to cache
			for (License license : missResults) {
				redisAdapter.set(
					RedisCacheFilters.getLicenseHashKey(license.getId()),
					jsonSerializationService.toJson(license),
					TWO_WEEKS_IN_SECONDS
				);
				result.put(license.getId(), license);
			}
		}
		return result;
	}

}
