package com.workmarket.dao.search.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.model.SolrAssessmentData;
import com.workmarket.data.solr.model.SolrCertificationData;
import com.workmarket.data.solr.model.SolrCompanyData;
import com.workmarket.data.solr.model.SolrCompanyLaneData;
import com.workmarket.data.solr.model.SolrCompanyUserTag;
import com.workmarket.data.solr.model.SolrContractData;
import com.workmarket.data.solr.model.SolrData;
import com.workmarket.data.solr.model.SolrGroupData;
import com.workmarket.data.solr.model.SolrInsuranceCoverageData;
import com.workmarket.data.solr.model.SolrInsuranceType;
import com.workmarket.data.solr.model.SolrLicenseData;
import com.workmarket.data.solr.model.SolrLinkedInData;
import com.workmarket.data.solr.model.SolrPaidAssignmentCountData;
import com.workmarket.data.solr.model.SolrRatingData;
import com.workmarket.data.solr.model.SolrRecruitingCampaignData;
import com.workmarket.data.solr.model.SolrSharedGroupData;
import com.workmarket.data.solr.model.SolrSkillData;
import com.workmarket.data.solr.model.SolrUserData;
import com.workmarket.domains.model.company.CompanyType;
import com.workmarket.domains.model.insurance.UserInsuranceType;
import com.workmarket.domains.model.request.RequestStatusType;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.FileUtilities;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Repository
public class SolrUserDAOImpl implements SolrUserDAO {

	private static final Logger logger = LoggerFactory.getLogger(SolrUserDAOImpl.class);

	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired private EntityAssociationDAO entityAssociationDAO;

	private final class SolrUserDataMapper implements RowMapper<SolrUserData> {

		@Override
		public SolrUserData mapRow(ResultSet rs, int rowNum) throws SQLException {
			SolrUserData row = new SolrUserData();
			row.setId(rs.getLong("u.id"));
			row.setUuid(rs.getString("u.uuid"));
			row.setFirstName(rs.getString("u.first_name"));
			row.setLastName(rs.getString("u.last_name"));
			row.setEmail(rs.getString("u.email"));
			row.setOverview(rs.getString("p.overview"));
			row.setAvatarSmallAssetUri(rs.getString("avatarUri"));
			row.setVideoAssetUri(rs.getString("videoUri"));
			row.setUserNumber(rs.getString("u.user_number"));
			row.setMbo(rs.getString("mbo_status") != null);
			row.setMboStatus(rs.getString("mbo_status"));

			if (StringUtils.isEmpty(row.getAvatarSmallAssetUri()) && rs.getString("avatarUriPrefix") != null && rs.getString("avatarUuid") != null) {
				row.setAvatarSmallAssetUri(FileUtilities.createRemoteFileandDirectoryStructor(rs.getString("avatarUriPrefix"), rs.getString("avatarUuid")));
			}

			if (StringUtils.isEmpty(row.getVideoAssetUri()) && rs.getString("videoUriPrefix") != null && rs.getString("videoUuid") != null) {
				row.setVideoAssetUri(FileUtilities.createRemoteFileandDirectoryStructor(rs.getString("videoUriPrefix"), rs.getString("videoUuid")));
			}

			SolrCompanyData companyData = new SolrCompanyData();
			companyData.setId(rs.getLong("companyId"));
			companyData.setUuid(rs.getString("companyUuid"));
			companyData.setName(rs.getString("companyName"));
			companyData.setType(CompanyType.getById(rs.getLong("companyType")));
			row.setCompany(companyData);

			row.setTitle(rs.getString("job_title"));
			if (row.getTitle() != null) {
				row.setJobFunctions(Lists.newArrayList(row.getTitle()));
			}
			row.setHourlyRate(rs.getInt("hourly_rate"));
			row.setMaxTravelDistance(rs.getFloat("max_travel_distance"));

			row.setWorkPhone(rs.getString("work_phone"));
			row.setMobilePhone(rs.getString("mobile_phone"));

			row.setCity(rs.getString("city"));
			row.setState(rs.getString("state"));
			row.setPostalCode(rs.getString("postal_code"));
			row.setCountry(rs.getString("country"));
			row.setCbsaName(rs.getString("cbsa_name"));

			row.setPoint(new GeoPoint(rs.getDouble("latitude"), rs.getDouble("longitude")));

			SolrRecruitingCampaignData recruitingCampaign = new SolrRecruitingCampaignData();
			recruitingCampaign.setId(rs.getLong("recruiting_campaign_id"));
			recruitingCampaign.setName(rs.getString("recruiting_campaign.title"));
			row.setRecruitingData(recruitingCampaign);
			row.setSharedWorkerRole(rs.getBoolean("sharedWorkerRole"));
			row.setEmailConfirmed(rs.getBoolean("email_confirmed"));
			row.setLane3ApprovalStatus(rs.getInt("lane3_approval_status"));
			row.setUserStatusType(rs.getString("user_status_type_code"));

			row.setWorkCompletedCount(rs.getInt("workCount"));
			row.setWorkCancelledCount(rs.getInt("workCancelled"));
			row.setPaidAssignmentsCount(rs.getInt("paidWorkLastSixMonths"));

			int ratingCount = rs.getInt("ratingCount");
			int goodRatingCount = rs.getInt("goodRatingCount");
			int satisfactionValue = NumberUtilities.percentage(ratingCount, goodRatingCount).intValue();
			double satisfactionRate = 0d;

			SolrRatingData ratingData = new SolrRatingData();
			if (ratingCount > 0) {
				ratingData.setRatingCount(ratingCount);
				ratingData.setRating(satisfactionValue);
				row.setRating(ratingData);
				satisfactionRate = NumberUtilities.percentage(ratingCount, goodRatingCount).doubleValue();
			}

			row.setSatisfactionRate(satisfactionRate);
			row.setTimeZoneId(rs.getString("time_zone.time_zone_id"));
			row.setDeliverableOnTimePercentage(0);
			row.setLastAssignedWorkDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("last_assigned_work_date")));
			row.setCreatedOn(new DateTime(rs.getTimestamp("u.created_on")));
			row.setWarpRequisitionId(rs.getInt("u.warp_requisition_id"));

			// New measures for workercore
			row.setRecentWorkingWeeksRatio(rs.getDouble("recentWorkingWeeksRatio"));
			row.setWeightedAverageRating(rs.getDouble("weightedAverageRating"));
			return row;
		}
	}

	private final class SolrUserDataUuidMapper implements RowCallbackHandler {

		private List<String> uuids = Lists.newArrayList();

		@Override
		public void processRow(ResultSet rs) throws SQLException {
			final String uuid = rs.getString("uuid");
			if (uuid != null) {
				uuids.add(uuid);
			}
		}

		public List<String> getUuids() {
			return uuids;
		}
	}

	@Override
	public SolrUserData getSolrDataById(Long id) {
		String whereClause = " AND 	u.id = :userId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", id);
		List<SolrUserData> data = getResults(whereClause, params);
		if (CollectionUtils.isEmpty(data)) {
			return null;
		}
		return data.get(0);
	}

	@Override
	public List<SolrUserData> getSolrDataById(List<Long> ids) {
		if (CollectionUtils.isEmpty(CollectionUtilities.filterNull(ids))) {
			return Collections.emptyList();
		}
		String whereClause = " AND 	u.id IN (" + StringUtils.join(ids, ",") + ")";

		return getResults(whereClause, new MapSqlParameterSource());
	}

	@Override
	public List<SolrUserData> getSolrDataBetweenIds(Long fromId, Long toId) {
		String whereClause = " AND 	u.id BETWEEN :fromId AND :toId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fromId", fromId);
		params.addValue("toId", toId);

		return getResults(whereClause, params);
	}

	@Override
	public List<SolrUserData> getSolrDataChanged(Calendar from) {
		String whereClause = " AND 	u.modified_on >= :from ";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("from", from);

		return getResults(whereClause, params);
	}

	@Override
	public List<String> getSolrDataUuidsByIds(final List<Long> ids) {
		final String sql = SolrUserSqlUtil.UUIDS.replace(":userIds", StringUtils.join(ids, ","));
		final SolrUserDataUuidMapper uuidMapper = new SolrUserDataUuidMapper();
		jdbcTemplate.query(sql, uuidMapper);
		return uuidMapper.getUuids();
	}

	private List<SolrUserData> getResults(String whereClause, MapSqlParameterSource params) {

		String sql = SolrUserSqlUtil.SELECT_SQL + whereClause;

		List<SolrUserData> users = jdbcTemplate.query(sql, params, new SolrUserDataMapper());

		// Certifications
		Map<Long, List<SolrCertificationData>> certificationData = entityAssociationDAO.generateCertificationDataMap(
				SolrUserSqlUtil.CERTIFICATION_ASSOCIATION_SQL.concat(whereClause), params);
		// Licenses
		Map<Long, List<SolrLicenseData>> licenseData = entityAssociationDAO.generateLicenseDataMap(
				SolrUserSqlUtil.LICENSE_ASSOCIATION_SQL.concat(whereClause), params);

		//Industries
		Map<Long, List<Long>> industries = entityAssociationDAO.generateListOfLongColumnDataMap(
				SolrUserSqlUtil.INDUSTRY_ASSOCIATION_SQL.concat(whereClause), params, "industryId");
		// Skills
		Map<Long, List<SolrSkillData>> skillData = Maps.newHashMap();
		skillData = entityAssociationDAO.generateAssociationMap(skillData, SolrUserSqlUtil.getEntityAssociationQuery("skill", whereClause),
				params, SolrSkillData.class);

		// Insurance Related Fields
		Map<Long, List<SolrInsuranceType>> insuranceData = Maps.newHashMap();
		insuranceData = entityAssociationDAO.generateAssociationMap(insuranceData,
				SolrUserSqlUtil.getEntityAssociationQuery("insurance", whereClause), params, SolrInsuranceType.class);

		Map<Long, List<SolrInsuranceCoverageData>> workersCompCoverage = entityAssociationDAO.generateInsuranceCoverageMap(
				SolrUserSqlUtil.createInsuranceCoverageQuery(UserInsuranceType.WORKERS_COMPENSATION, whereClause), params
		);
		Map<Long, List<SolrInsuranceCoverageData>> generalLiabilityCoverage = entityAssociationDAO.generateInsuranceCoverageMap(
				SolrUserSqlUtil.createInsuranceCoverageQuery(UserInsuranceType.GENERAL_LIABILITY, whereClause), params
		);
		Map<Long, List<SolrInsuranceCoverageData>> errorsAndOmissionsCoverage = entityAssociationDAO.generateInsuranceCoverageMap(
				SolrUserSqlUtil.createInsuranceCoverageQuery(UserInsuranceType.ERRORS_AND_OMISSIONS, whereClause), params
		);
		Map<Long, List<SolrInsuranceCoverageData>> automobileCoverage = entityAssociationDAO.generateInsuranceCoverageMap(
				SolrUserSqlUtil.createInsuranceCoverageQuery(UserInsuranceType.AUTOMOBILE, whereClause), params
		);
		Map<Long, List<SolrInsuranceCoverageData>> contractorsCoverage = entityAssociationDAO.generateInsuranceCoverageMap(
				SolrUserSqlUtil.createInsuranceCoverageQuery(UserInsuranceType.CONTRACTORS, whereClause), params
		);
		Map<Long, List<SolrInsuranceCoverageData>> businessLiabilityCoverage = entityAssociationDAO.generateInsuranceCoverageMap(
				SolrUserSqlUtil.createInsuranceCoverageQuery(UserInsuranceType.BUSINESS_LIABILITY, whereClause), params
		);
		Map<Long, List<SolrInsuranceCoverageData>> commercialGeneralLiability = entityAssociationDAO.generateInsuranceCoverageMap(
				SolrUserSqlUtil.createInsuranceCoverageQuery(UserInsuranceType.COMMERCIAL_GENERAL_LIABILITY, whereClause), params
		);

		// Lanes
		Map<Long, List<SolrCompanyLaneData>> companyLaneData = entityAssociationDAO.generateCompanyLaneDataMap(
				SolrUserSqlUtil.LANE_ASSOCIATION_SQL.concat(whereClause), params);
		// Tags
		Map<Long, List<SolrCompanyUserTag>> companyUserTagData = entityAssociationDAO.generateCompanyUserTagDataMap(
				SolrUserSqlUtil.COMPANY_TAG_ASSOCIATION_SQL.concat(whereClause), params);
		
		// Blocked by users
		Map<Long, List<Long>> blockedUsersData = entityAssociationDAO.generateListOfLongColumnDataMap(
				SolrUserSqlUtil.BLOCKED_BY_USERS_SQL.concat(whereClause), params, "blocked_by_user_id");

		// Blocked companies (companies you block, and who have blocked you)
		String blockedCompaniesSQL = SolrUserSqlUtil.BLOCKED_COMPANIES_SQL.concat(whereClause);
		String blockedByCompaniesSQL = SolrUserSqlUtil.BLOCKED_BY_COMPANIES_SQL.concat(whereClause);

		Map<Long, List<Long>> blockedCompaniesData = entityAssociationDAO.generateListOfLongColumnDataMap(
				blockedCompaniesSQL + " UNION ALL " + blockedByCompaniesSQL, params, "blocked_company_id");
		
		// Groups
		Map<Long, List<SolrGroupData>> groupData = entityAssociationDAO.generateCompanyUserGroupDataMap(
				SolrUserSqlUtil.COMPANY_USER_GROUP_SQL.concat(whereClause), params);
		Map<Long, List<SolrGroupData>> groupMember = entityAssociationDAO.generateCompanyUserGroupDataMap(
				SolrUserSqlUtil.MEMBER_GROUP_ASSOCIATION_SQL.concat(whereClause), params);
		Map<Long, List<SolrGroupData>> groupMemberOverride = entityAssociationDAO.generateCompanyUserGroupDataMap(
				SolrUserSqlUtil.MEMBER_OVERRIDE_GROUP_ASSOCIATION_SQL.concat(whereClause), params);
		Map<Long, List<SolrGroupData>> groupPending = entityAssociationDAO.generateCompanyUserGroupDataMap(
				SolrUserSqlUtil.PENDING_PASSED_GROUP_ASSOCIATION_SQL.concat(whereClause), params);
		Map<Long, List<SolrGroupData>> groupPendingOverride = entityAssociationDAO.generateCompanyUserGroupDataMap(
				SolrUserSqlUtil.PENDING_FAILED_GROUP_ASSOCIATION_SQL.concat(whereClause), params);
		Map<Long, List<SolrGroupData>> groupInvited = entityAssociationDAO.generateCompanyUserGroupDataMap(
				SolrUserSqlUtil.INVITED_GROUP_ASSOCIATION_SQL.concat(whereClause), params.addValue("sent", RequestStatusType.SENT));
		Map<Long, List<SolrGroupData>> groupDeclined = entityAssociationDAO.generateCompanyUserGroupDataMap(
				SolrUserSqlUtil.DECLINED_GROUP_ASSOCIATION_SQL.concat(whereClause), params);

		// Shared groups
		Map<Long, List<SolrSharedGroupData>> sharedGroups = entityAssociationDAO.generateSharedGroupDataMap(
				SolrUserSqlUtil.getSharedGroupsQuery(whereClause), params);

		// Passed Tests
		Map<Long, List<SolrAssessmentData>> assessmentData = entityAssociationDAO.generateAssessmentDataMap(
				SolrUserSqlUtil.ASSESSMENT_ASSOCIATION_SQL.concat(whereClause), params);
		// Invited Assessments
		Map<Long, List<SolrAssessmentData>> invitedAssessmentData = entityAssociationDAO.generateAssessmentDataMap(
				SolrUserSqlUtil.INVITED_ASSESSMENT_ASSOCIATION_SQL.concat(whereClause), params);
		//Passed Assessments
		Map<Long, List<SolrAssessmentData>> passedAssessmentData = entityAssociationDAO.generateAssessmentDataMap(
				SolrUserSqlUtil.PASSED_ASSESSMENT_ASSOCIATION_SQL.concat(whereClause), params);
		//Failed Tests
		Map<Long, List<SolrAssessmentData>> failedTestData = entityAssociationDAO.generateAssessmentDataMap(
				SolrUserSqlUtil.FAILED_TEST_ASSOCIATION_SQL.concat(whereClause), params);

		// Tools
		Map<Long, List<String>> toolsData = entityAssociationDAO.generateListOfStringColumnDataMap(
				SolrUserSqlUtil.TOOLS_ASSOCIATION_SQL.concat(whereClause), params, "name");
		// Specialties
		Map<Long, List<String>> specialtiesData = entityAssociationDAO.generateListOfStringColumnDataMap(
				SolrUserSqlUtil.SPECIALTIES_ASSOCIATION_SQL.concat(whereClause), params, "name");
		// LinkedIn
		Map<Long, SolrLinkedInData> linkedInData = entityAssociationDAO.generateLinkedInDataMap(
				SolrUserSqlUtil.getLinkedInQuery(whereClause), params);

		List<Long> userIds = extract(users, on(SolrUserData.class).getId());

		// Paid assignment counts grouped by company
		Map<Long, List<SolrPaidAssignmentCountData>> paidCompanyAssignmentCountsData = entityAssociationDAO.generatePaidCompanyCountDataMap(
				SolrUserSqlUtil.getPaidAssignmentsGroupedByCompanyAndUserQuery(userIds), params);

		// Signed contracts
		Map<Long, List<SolrContractData>> contractData = entityAssociationDAO.generateContractDataMap(
				SolrUserSqlUtil.getContractAssociationUserQuery(userIds), params);

		for (SolrData userData : users) {
			SolrUserData user = (SolrUserData) userData;
			long userId = user.getId();

			if (paidCompanyAssignmentCountsData.containsKey(userId)) {
				List<SolrPaidAssignmentCountData> companyCounts = paidCompanyAssignmentCountsData.get(userId);
				Map<Long, Integer> paidCompanyCounts = Maps.newHashMap();

				for (SolrPaidAssignmentCountData data : companyCounts) {
					paidCompanyCounts.put(data.getCompanyId(), data.getCount());
				}

				user.setPaidCompanyAssignmentCounts(paidCompanyCounts);
			}
			if (contractData.containsKey(userId)) {
				user.setContracts(contractData.get(userId));
			}
			if (certificationData.containsKey(userId)) {
				user.setCertifications(certificationData.get(userId));
			}
			if (licenseData.containsKey(userId)) {
				user.setLicenses(licenseData.get(userId));
			}
			if (industries.containsKey(userId)) {
				user.setIndustries(industries.get(userId));
			}
			if (skillData.containsKey(userId)) {
				user.setSkills(skillData.get(userId));
			}
			if (insuranceData.containsKey(userId)) {
				user.setInsurances(insuranceData.get(userId));
			}
			if (workersCompCoverage.containsKey(userId)) {
				user.setWorkersCompCoverage(workersCompCoverage.get(userId));
			}
			if (generalLiabilityCoverage.containsKey(userId)) {
				user.setGeneralLiabilityCoverage(generalLiabilityCoverage.get(userId));
			}
			if (errorsAndOmissionsCoverage.containsKey(userId)) {
				user.setErrorsAndOmissionsCoverage(errorsAndOmissionsCoverage.get(userId));
			}
			if (automobileCoverage.containsKey(userId)) {
				user.setAutomobileCoverage(automobileCoverage.get(userId));
			}
			if (commercialGeneralLiability.containsKey(userId)) {
				user.setCommercialGeneralLiabilityCoverage(commercialGeneralLiability.get(userId));
			}
			if (businessLiabilityCoverage.containsKey(userId)) {
				user.setBusinessLiabilityCoverage(businessLiabilityCoverage.get(userId));
			}
			if (contractorsCoverage.containsKey(userId)) {
				user.setContractorsCoverage(contractorsCoverage.get(userId));
			}
			if (companyLaneData.containsKey(userId)) {
				user.setLaneData(companyLaneData.get(userId));
			}
			if (companyUserTagData.containsKey(userId)) {
				user.setUserTags(companyUserTagData.get(userId));
			}
			if (blockedUsersData.containsKey(userId)) {
				user.setBlockedUserIds(blockedUsersData.get(userId));
			} else {
				user.setBlockedUserIds(null);
			}
			if (blockedCompaniesData.containsKey(userId)) {
				user.setBlockedCompanyIds(blockedCompaniesData.get(userId));
			} else {
				user.setBlockedCompanyIds(null);
			}
			if (groupData.containsKey(userId)) {
				user.setGroupData(groupData.get(userId));
			}
			if (groupMember.containsKey(userId)) {
				user.setGroupMember(groupMember.get(userId));
			}
			if (groupMemberOverride.containsKey(userId)) {
				user.setGroupMemberOverride(groupMemberOverride.get(userId));
			}
			if (groupPending.containsKey(userId)) {
				user.setGroupPending(groupPending.get(userId));
			}
			if (groupPendingOverride.containsKey(userId)) {
				user.setGroupPendingOverride(groupPendingOverride.get(userId));
			}
			if (groupInvited.containsKey(userId)) {
				user.setGroupInvited(groupInvited.get(userId));
			}
			if (groupDeclined.containsKey(userId)) {
				user.setGroupDeclined(groupDeclined.get(userId));
			}
			if (sharedGroups.containsKey(userId)) {
				user.setSharedGroupData(sharedGroups.get(userId));
			}
			if (assessmentData.containsKey(userId)) {
				user.setAssessments(assessmentData.get(userId));
			}
			if (invitedAssessmentData.containsKey(userId)) {
				user.setInvitedAssessments(invitedAssessmentData.get(userId));
			}
			if (passedAssessmentData.containsKey(userId)) {
				user.setPassedAssessments(passedAssessmentData.get(userId));
			}
			if (failedTestData.containsKey(userId)) {
				user.setFailedTests(failedTestData.get(userId));
			}
			if (toolsData.containsKey(userId)) {
				user.setToolNames(toolsData.get(userId));
			}
			if (specialtiesData.containsKey(userId)) {
				user.setSpecialtyNames(specialtiesData.get(userId));
			}
			if (linkedInData.containsKey(userId)) {
				user.setSolrLinkedInData(linkedInData.get(userId));
			}
		}

		return users;
	}
}

