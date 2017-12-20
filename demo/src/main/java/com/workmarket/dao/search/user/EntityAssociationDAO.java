package com.workmarket.dao.search.user;

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
import com.workmarket.data.solr.model.SolrPaidAssignmentCountData;
import com.workmarket.data.solr.model.SolrSharedGroupData;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;
import java.util.Map;

public interface EntityAssociationDAO {

	<T extends AbstractSolrData> Map<Long, List<T>> generateAssociationMap(Map<Long, List<T>> map, String sql, MapSqlParameterSource params, Class<?> clazz);
	
	Map<Long, List<SolrCertificationData>> generateCertificationDataMap(String sql, MapSqlParameterSource params);
	
	Map<Long, List<SolrLicenseData>> generateLicenseDataMap(String sql, MapSqlParameterSource params);
	
	Map<Long, List<SolrCompanyLaneData>> generateCompanyLaneDataMap(String sql, MapSqlParameterSource params);
	
	Map<Long, List<SolrCompanyUserTag>> generateCompanyUserTagDataMap(String sql, MapSqlParameterSource params);
	
	Map<Long, List<Long>> generateListOfLongColumnDataMap(String sql, MapSqlParameterSource params, String columnName);
	
	Map<Long, List<String>> generateListOfStringColumnDataMap(String sql, MapSqlParameterSource params, String columnName);
	
	Map<Long, List<SolrGroupData>> generateCompanyUserGroupDataMap(String sql, MapSqlParameterSource params);

	Map<Long, List<SolrSharedGroupData>> generateSharedGroupDataMap(String sql, MapSqlParameterSource params);

	Map<Long, List<SolrAssessmentData>> generateAssessmentDataMap(String sql, MapSqlParameterSource params);

	Map<Long, List<SolrContractData>> generateContractDataMap(String sql, MapSqlParameterSource params);

	Map<Long, List<SolrPaidAssignmentCountData>> generatePaidCompanyCountDataMap(String sql, MapSqlParameterSource params);

	Map<Long, SolrLinkedInData> generateLinkedInDataMap(String sql, MapSqlParameterSource params);

	Map<Long, List<SolrInsuranceCoverageData>> generateInsuranceCoverageMap(String sql, MapSqlParameterSource params);

}
