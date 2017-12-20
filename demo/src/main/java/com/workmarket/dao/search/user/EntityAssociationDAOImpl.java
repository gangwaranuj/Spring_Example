package com.workmarket.dao.search.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.solr.model.AbstractSolrData;
import com.workmarket.data.solr.model.SolrAssessmentData;
import com.workmarket.data.solr.model.SolrCertificationData;
import com.workmarket.data.solr.model.SolrCompanyLaneData;
import com.workmarket.data.solr.model.SolrCompanyUserTag;
import com.workmarket.data.solr.model.SolrContractData;
import com.workmarket.data.solr.model.SolrGroupData;
import com.workmarket.data.solr.model.SolrInsuranceCoverageData;
import com.workmarket.data.solr.model.SolrLicenseData;
import com.workmarket.data.solr.model.SolrLinkedInData;
import com.workmarket.data.solr.model.SolrLinkedInData.LinkedInCompanyData;
import com.workmarket.data.solr.model.SolrLinkedInData.LinkedInSchoolData;
import com.workmarket.data.solr.model.SolrPaidAssignmentCountData;
import com.workmarket.data.solr.model.SolrSharedGroupData;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.lane.LaneType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class EntityAssociationDAOImpl implements EntityAssociationDAO {

	private static final Log logger = LogFactory.getLog(EntityAssociationDAOImpl.class);

	@Autowired
	@Qualifier("readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractSolrData> Map<Long, List<T>> generateAssociationMap(Map<Long, List<T>> map, String sql, MapSqlParameterSource params, Class<?> clazz) {
		if (StringUtils.isBlank(sql)) {
			return Collections.EMPTY_MAP;
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		for (Map<String, Object> row : results) {

			long userId = ((Integer) row.get("user_id")).longValue();

			T data;
			try {
				data = (T) clazz.newInstance();
			} catch (IllegalAccessException | InstantiationException e) {
				throw new RuntimeException(e);
			}
			data.setId(((Integer) row.get("id")).longValue());
			data.setName((String) row.get("name"));

			if (!map.containsKey(userId)) {
				List<T> list = Lists.newArrayList();
				map.put(userId, list);
			}
			map.get(userId).add(data);
		}

		return map;
	}

	@Override
	public Map<Long, List<SolrInsuranceCoverageData>> generateInsuranceCoverageMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<SolrInsuranceCoverageData>> coverageData = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			long userId = ((Integer) row.get("user_id")).longValue();

			// TODO: maybe change datatype of 'coverage' column in user_insurance_association table so cleanRawSQLInsuranceFigure isn't needed
			SolrInsuranceCoverageData data = new SolrInsuranceCoverageData();
			Long coverageAmount = cleanRawSQLInsuranceFigure((String) row.get("coverage"));
			if (coverageAmount != null) {
				if (!coverageData.containsKey(userId)) {
					List<SolrInsuranceCoverageData> list = Lists.newArrayList();
					coverageData.put(userId, list);
				}
				data.setCoverage(coverageAmount);
				coverageData.get(userId).add(data);
			}
		}

		return coverageData;
	}

	public Long cleanRawSQLInsuranceFigure(String insuranceFigure) {
		if (StringUtils.isBlank(insuranceFigure)) {
			return null;
		}
		try {
			return NumberFormat.getNumberInstance(java.util.Locale.US).parse(insuranceFigure).longValue();
		} catch (ParseException e) {
			logger.error("Error parsing insurance figure when indexing users: " + insuranceFigure, e);
		}
		return null;
	}

	@Override
	public Map<Long, List<SolrCertificationData>> generateCertificationDataMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<SolrCertificationData>> certificationData = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			long userId = ((Integer) row.get("user_id")).longValue();

			SolrCertificationData data = new SolrCertificationData();
			data.setId(((Integer) row.get("id")).longValue());
			data.setName((String) row.get("name"));
			data.setCertificationVendor((String) row.get("vendorName"));

			if (!certificationData.containsKey(userId)) {
				List<SolrCertificationData> list = Lists.newArrayList();
				certificationData.put(userId, list);
			}
			certificationData.get(userId).add(data);
		}

		return certificationData;
	}

	@Override
	public Map<Long, List<SolrLicenseData>> generateLicenseDataMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<SolrLicenseData>> licenseData = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			long userId = ((Integer) row.get("user_id")).longValue();

			SolrLicenseData data = new SolrLicenseData();
			data.setId(((Integer) row.get("id")).longValue());
			data.setName((String) row.get("name"));
			data.setLicenseState((String) row.get("state"));

			if (!licenseData.containsKey(userId)) {
				List<SolrLicenseData> list = Lists.newArrayList();
				licenseData.put(userId, list);
			}
			licenseData.get(userId).add(data);
		}

		return licenseData;
	}

	@Override
	public Map<Long, List<SolrCompanyLaneData>> generateCompanyLaneDataMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<SolrCompanyLaneData>> companyLaneData = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			long userId = ((Integer) row.get("user_id")).longValue();

			SolrCompanyLaneData data = new SolrCompanyLaneData();
			data.setApprovalStatus(ApprovalStatus.values()[(Integer) row.get("approval_status")]);
			data.setCompanyId(((Integer) row.get("company_id")).longValue());
			data.setCompanyUuid((String) row.get("company_uuid"));
			data.setLaneType(LaneType.values()[(Integer) row.get("lane_type_id")]);

			if (!companyLaneData.containsKey(userId)) {
				List<SolrCompanyLaneData> list = Lists.newArrayList();
				companyLaneData.put(userId, list);
			}
			companyLaneData.get(userId).add(data);
		}

		return companyLaneData;
	}

	@Override
	public Map<Long, List<SolrCompanyUserTag>> generateCompanyUserTagDataMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<SolrCompanyUserTag>> companyUserTagData = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			long userId = ((Integer) row.get("user_id")).longValue();

			SolrCompanyUserTag data = new SolrCompanyUserTag();
			data.setTag((String) row.get("name"));
			data.setCompanyId(((Integer) row.get("company_id")).longValue());

			if (!companyUserTagData.containsKey(userId)) {
				List<SolrCompanyUserTag> list = Lists.newArrayList();
				companyUserTagData.put(userId, list);
			}
			companyUserTagData.get(userId).add(data);
		}

		return companyUserTagData;
	}

	@Override
	public Map<Long, List<Long>> generateListOfLongColumnDataMap(String sql, MapSqlParameterSource params, String columnName) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<Long>> data = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			long userId = ((Integer) row.get("user_id")).longValue();

			if (!data.containsKey(userId)) {
				List<Long> list = Lists.newArrayList();
				data.put(userId, list);
			}
			Integer value = (Integer) row.get(columnName);
			if (value != null) {
				data.get(userId).add(value.longValue());
			}
		}

		return data;
	}

	@Override
	public Map<Long, List<String>> generateListOfStringColumnDataMap(String sql, MapSqlParameterSource params, String columnName) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<String>> data = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			long userId = ((Integer) row.get("user_id")).longValue();

			if (!data.containsKey(userId)) {
				List<String> list = Lists.newArrayList();
				data.put(userId, list);
			}
			data.get(userId).add((String) row.get(columnName));
		}

		return data;
	}

	@Override
	public Map<Long, List<SolrGroupData>> generateCompanyUserGroupDataMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<SolrGroupData>> userGroupData = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			long userId = ((Integer) row.get("user_id")).longValue();

			SolrGroupData data = new SolrGroupData();
			data.setGroupId(((Integer) row.get("user_group_id")).longValue());
			data.setCompanyId(((Integer) row.get("company_id")).longValue());
			data.setGroupUuid((String) row.get("user_group_uuid"));

			if (!userGroupData.containsKey(userId)) {
				List<SolrGroupData> list = Lists.newArrayList();
				userGroupData.put(userId, list);
			}
			userGroupData.get(userId).add(data);
		}

		return userGroupData;
	}

	@Override
	public Map<Long, List<SolrSharedGroupData>> generateSharedGroupDataMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<SolrSharedGroupData>> sharedGroupData = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			long userId = ((Integer) row.get("user_id")).longValue();

			SolrSharedGroupData data = new SolrSharedGroupData();
			data.setGroupId(((Integer) row.get("user_group_id")).longValue());
			data.setNetworkId(((Integer) row.get("network_id")).longValue());
			data.setGroupUuid((String) row.get("user_group_uuid"));

			if (!sharedGroupData.containsKey(userId)) {
				List<SolrSharedGroupData> list = Lists.newArrayList();
				sharedGroupData.put(userId, list);
			}
			sharedGroupData.get(userId).add(data);
		}

		return sharedGroupData;
	}

	@Override
	public Map<Long, List<SolrAssessmentData>> generateAssessmentDataMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<SolrAssessmentData>> assessmentData = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			long userId = ((Integer) row.get("user_id")).longValue();

			SolrAssessmentData data = new SolrAssessmentData();
			data.setAssessmentId(((Integer) row.get("assessment_id")).longValue());
			data.setCompanyId(((Integer) row.get("company_id")).longValue());

			if (!assessmentData.containsKey(userId)) {
				List<SolrAssessmentData> list = Lists.newArrayList();
				assessmentData.put(userId, list);
			}
			assessmentData.get(userId).add(data);
		}

		return assessmentData;
	}

	@Override
	public Map<Long, List<SolrContractData>> generateContractDataMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Collections.emptyMap();
		}

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<SolrContractData>> contractData = Maps.newHashMap();
		SolrContractData data;
		Long userId;
		for (Map<String, Object> row : results) {
			userId = ((Integer) row.get("user_id")).longValue();

			data = new SolrContractData();
			data.setContractId(((Integer) row.get("contract_id")).longValue());
			data.setCompanyId(((Integer) row.get("company_id")).longValue());
			data.setContractVersionId(((Integer) row.get("contract_version_id")).longValue());

			if (!contractData.containsKey(userId)) {
				contractData.put(userId, new ArrayList<SolrContractData>());
			}

			contractData.get(userId).add(data);
		}

		return contractData;
	}

	@Override
	public Map<Long, List<SolrPaidAssignmentCountData>> generatePaidCompanyCountDataMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Maps.newHashMap();
		}

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, List<SolrPaidAssignmentCountData>> paidCompanyData = Maps.newHashMap();
		SolrPaidAssignmentCountData data;
		Long userId;
		for (Map<String, Object> row : results) {
			userId = (Long) row.get("user_id");

			data = new SolrPaidAssignmentCountData();
			data.setCompanyId((Long) row.get("company_id"));
			data.setCount(((Long) row.get("count")).intValue());

			if (!paidCompanyData.containsKey(userId)) {
				paidCompanyData.put(userId, new ArrayList<SolrPaidAssignmentCountData>());
			}

			paidCompanyData.get(userId).add(data);
		}

		return paidCompanyData;
	}

	@Override
	public Map<Long, SolrLinkedInData> generateLinkedInDataMap(String sql, MapSqlParameterSource params) {
		if (StringUtils.isBlank(sql)) {
			return Maps.newHashMap();
		}
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

		Map<Long, SolrLinkedInData> linkedInData = Maps.newHashMap();

		for (Map<String, Object> row : results) {
			Long userId = ((Integer) row.get("userId")).longValue();
			String linkedInDataType = (String) row.get("linkedInDataType");
			String name = (String) row.get("name");
			String fieldOfStudy = (String) row.get("field_of_Study");

			if (!linkedInData.containsKey(userId)) {
				linkedInData.put(userId, new SolrLinkedInData());
			}

			if (linkedInDataType.equals("education")) {
				LinkedInSchoolData schoolData = new LinkedInSchoolData();
				schoolData.setSchoolName(name);
				schoolData.setFieldOfStudy(fieldOfStudy);
				linkedInData.get(userId).getLinkedInSchools().add(schoolData);
			} else {
				LinkedInCompanyData companyData = new LinkedInCompanyData();
				companyData.setCompanyName(name);
				companyData.setCompanyTitle(fieldOfStudy);
				linkedInData.get(userId).getLinkedInCompanies().add(companyData);
			}
		}
		return linkedInData;
	}
}
