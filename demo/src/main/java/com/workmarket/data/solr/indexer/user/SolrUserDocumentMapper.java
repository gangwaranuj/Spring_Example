package com.workmarket.data.solr.indexer.user;

import com.google.common.collect.Lists;
import com.workmarket.data.solr.indexer.SolrDocumentMapper;
import com.workmarket.data.solr.model.NamedSolrData;
import com.workmarket.data.solr.model.SolrAssessmentData;
import com.workmarket.data.solr.model.SolrCertificationData;
import com.workmarket.data.solr.model.SolrCompanyData;
import com.workmarket.data.solr.model.SolrCompanyLaneData;
import com.workmarket.data.solr.model.SolrCompanyUserTag;
import com.workmarket.data.solr.model.SolrContractData;
import com.workmarket.data.solr.model.SolrGroupData;
import com.workmarket.data.solr.model.SolrInsuranceCoverageData;
import com.workmarket.data.solr.model.SolrLicenseData;
import com.workmarket.data.solr.model.SolrLinkedInData;
import com.workmarket.data.solr.model.SolrLinkedInData.LinkedInCompanyData;
import com.workmarket.data.solr.model.SolrLinkedInData.LinkedInSchoolData;
import com.workmarket.data.solr.model.SolrRatingData;
import com.workmarket.data.solr.model.SolrSharedGroupData;
import com.workmarket.data.solr.model.SolrUserData;
import com.workmarket.data.solr.repository.UserBoostFields;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.lane.LaneType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;
import org.springframework.data.solr.core.query.Field;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isEmpty;

@Component
public class SolrUserDocumentMapper implements SolrDocumentMapper<SolrUserData> {

	public SolrInputDocument toSolrDocument(SolrUserData solrData) {
		if (solrData == null) {
			return null;
		}
		SolrInputDocument document = new SolrInputDocument();
		addFieldSafe(document, UserSearchableFields.ID, solrData.getId());
		addFieldSafe(document, UserSearchableFields.UUID, solrData.getUuid());
		addFieldSafe(document, UserSearchableFields.USER_NUMBER, solrData.getUserNumber());
		addFieldSafe(document, UserSearchableFields.FIRST_NAME, solrData.getFirstName());
		addFieldSafe(document, UserSearchableFields.LAST_NAME, solrData.getLastName());
		addFieldSafe(document, UserSearchableFields.FULL_NAME, solrData.getFullName());
		addFieldSafe(document, UserSearchableFields.CREATED_ON, solrData.getCreatedOn());
		addFieldSafe(document, UserSearchableFields.LAST_ASSIGNED_WORK_DATE, solrData.getLastAssignedWorkDate());
		addFieldSafe(document, UserSearchableFields.EMAIL, solrData.getEmail());
		addFieldSafe(document, UserSearchableFields.MOBILE_PHONE, solrData.getMobilePhone());
		addFieldSafe(document, UserSearchableFields.WORK_PHONE, solrData.getWorkPhone());
		addFieldSafe(document, UserSearchableFields.JOB_TITLE, solrData.getTitle());
		addStringListFieldSafe(document, UserSearchableFields.JOB_FUNCTIONS, solrData.getJobFunctions());
		addFieldSafe(document, UserSearchableFields.CITY, solrData.getCity());
		addFieldSafe(document, UserSearchableFields.STATE, solrData.getState());
		addFieldSafe(document, UserSearchableFields.POSTAL_CODE, solrData.getPostalCode());
		addFieldSafe(document, UserSearchableFields.CBSA_NAME, solrData.getCbsaName());
		addFieldSafe(document, UserSearchableFields.COUNTRY, solrData.getCountry());
		addLicenseFieldsSafe(document, solrData.getLicenses());
		addCertificationFieldsSafe(document, solrData.getCertifications());
		addCompanyFieldsSafe(document, solrData.getCompany());

		//Assessment Fields
		addAssessmentFieldsSafe(document, solrData.getAssessments());
		addOtherAssessmentFieldsSafe(document, solrData.getInvitedAssessments(), UserSearchableFields.INVITED_ASSESSMENT_IDS);
		addOtherAssessmentFieldsSafe(document, solrData.getFailedTests(), UserSearchableFields.FAILED_TEST_IDS);
		addOtherAssessmentFieldsSafe(document, solrData.getPassedAssessments(), UserSearchableFields.PASSED_ASSESSMENT_IDS);

		//Group fields
		addGroupFieldsSafe(document, solrData.getGroupData());
		addSharedGroupFieldsSafe(document, solrData.getSharedGroupData());
		addGroupStatusFieldsSafe(document, solrData.getGroupMember(), UserSearchableFields.MEMBER_GROUP_IDS, UserSearchableFields.MEMBER_GROUP_UUIDS);
		addGroupStatusFieldsSafe(document, solrData.getGroupMemberOverride(), UserSearchableFields.MEMBER_OVERRIDE_GROUP_IDS, UserSearchableFields.MEMBER_OVERRIDE_GROUP_UUIDS);
		addGroupStatusFieldsSafe(document, solrData.getGroupPending(), UserSearchableFields.PENDING_GROUP_IDS, UserSearchableFields.PENDING_GROUP_UUIDS);
		addGroupStatusFieldsSafe(document, solrData.getGroupPendingOverride(), UserSearchableFields.PENDING_OVERRIDE_GROUP_IDS, UserSearchableFields.PENDING_OVERRIDE_GROUP_UUIDS);
		addGroupStatusFieldsSafe(document, solrData.getGroupInvited(), UserSearchableFields.INVITED_GROUP_IDS, UserSearchableFields.INVITED_GROUP_UUIDS);
		addGroupStatusFieldsSafe(document, solrData.getGroupDeclined(), UserSearchableFields.DECLINED_GROUP_IDS, UserSearchableFields.DECLINED_GROUP_UUIDS);

		addLongFieldListSafe(document, UserSearchableFields.INDUSTRIES_ID, solrData.getIndustries());
		addFieldSafe(document, UserSearchableFields.HOURLY_RATE, solrData.getHourlyRate());
		addRatingFieldsSafe(document, solrData.getRating());
		addIntegerFieldListSafe(document, UserSearchableFields.VERIFICATION_IDS, solrData.getVerificationIds());
		addLaneFieldsSafe(document, solrData.getLaneData());
		addField(document, UserSearchableFields.WORK_CANCELLED_COUNT, solrData.getWorkCancelledCount());
		addContractFieldsSafe(document, solrData.getContracts());

		//Counts and Quality Boost Fields
		addFieldSafe(document, UserBoostFields.ON_TIME_PERCENTAGE, solrData.getOnTimePercentage());
		addFieldSafe(document, UserBoostFields.DELIVERABLE_ON_TIME_PERCENTAGE, solrData.getDeliverableOnTimePercentage());
		addField(document, UserBoostFields.WORK_COMPLETED_COUNT, solrData.getWorkCompletedCount());
		addField(document, UserBoostFields.AVERAGE_STAR_RATING, solrData.getAverageStarRating());
		addField(document, UserBoostFields.SATISFACTION_RATE, solrData.getSatisfactionRate());
		addField(document, UserBoostFields.REPEATED_CLIENTS_COUNT, solrData.getRepeatClientsCount());
		addField(document, UserBoostFields.DISTINCT_COMPANY_BLOCKS_COUNT, solrData.getBlocksCount());
		addField(document, UserBoostFields.LATE_LABEL_COUNT, solrData.getLateLabelCount());
		addField(document, UserBoostFields.ABANDONED_LABEL_COUNT, solrData.getAbandonedLabelCount());
		addField(document, UserBoostFields.CANCELLED_LABEL_COUNT, solrData.getCancelledLabelCount());
		addField(document, UserBoostFields.COMPLETED_ON_TIME_LABEL_COUNT, solrData.getDelayedLabelCount());
		addField(document, UserBoostFields.PAID_ASSIGNMENTS_COUNT, solrData.getPaidAssignmentsCount());
		addField(document, UserBoostFields.RECENT_WORKING_WEEKS_RATIO, solrData.getRecentWorkingWeeksRatio());
		addField(document, UserBoostFields.WEIGHTED_AVERAGE_RATING, solrData.getWeightedAverageRating());
		addStringListFieldSafe(document, UserBoostFields.COMPLETED_WORK_KEYWORDS, solrData.getCompletedWorkKeywords());
		addStringListFieldSafe(document, UserBoostFields.APPLIED_GROUPS_KEYWORDS, solrData.getAppliedGroupsKeywords());
		addLongFieldListSafe(document, UserBoostFields.WORK_COMPLETED_FOR_COMPANIES, solrData.getWorkCompletedForCompanies());
		addSkillsMatchingString(document, solrData);

		addFieldSafe(document, UserSearchableFields.AVATAR_SMALL_ASSET_URI, solrData.getAvatarSmallAssetUri());
		addFieldSafe(document, UserSearchableFields.VIDEO_ASSET_URI, solrData.getVideoAssetUri());
		addFieldSafe(document, UserSearchableFields.HAS_AVATAR, (solrData.getAvatarSmallAssetUri() != null));
		addFieldSafe(document, UserSearchableFields.HAS_VIDEO, (solrData.getVideoAssetUri() != null));
		addFieldSafe(document, UserSearchableFields.MAX_TRAVEL_DISTANCE, solrData.getMaxTravelDistance());
		addNamedSolrDataList(document, solrData.getSkills());
		addCompanyUserTagFieldsSafe(document, solrData.getUserTags());
		addFieldSafe(document, UserSearchableFields.LAST_DRUG_TEST_DATE, solrData.getLastDrugTestDate());
		addFieldSafe(document, UserSearchableFields.LAST_BACKGROUND_CHECK_DATE, solrData.getLastBackgroundCheckDate());
		addField(document, UserSearchableFields.SCREENING_STATUS, solrData.getScreeningStatus());
		addFieldSafe(document, UserSearchableFields.PASSED_BACKGROUND_CHECK, solrData.isPassedBackgroundCheck());
		addFieldSafe(document, UserSearchableFields.PASSED_DRUG_TEST, solrData.isPassedDrugTest());

		//Insurance related fields
		addNamedSolrDataList(document, solrData.getInsurances());
		addInsuranceCoverageFieldSafe(document, UserSearchableFields.WORKERS_COMPENSATION_COVERAGE, solrData.getWorkersCompCoverage());
		addInsuranceCoverageFieldSafe(document, UserSearchableFields.GENERAL_LIABILITY_COVERAGE, solrData.getGeneralLiabilityCoverage());
		addInsuranceCoverageFieldSafe(document, UserSearchableFields.ERRORS_AND_OMISSIONS_COVERAGE, solrData.getErrorsAndOmissionsCoverage());
		addInsuranceCoverageFieldSafe(document, UserSearchableFields.AUTOMOBILE_COVERAGE, solrData.getAutomobileCoverage());
		addInsuranceCoverageFieldSafe(document, UserSearchableFields.COMMERCIAL_GENERAL_LIABILITY_COVERAGE, solrData.getCommercialGeneralLiabilityCoverage());
		addInsuranceCoverageFieldSafe(document, UserSearchableFields.BUSINESS_LIABILITY_COVERAGE, solrData.getBusinessLiabilityCoverage());
		addInsuranceCoverageFieldSafe(document, UserSearchableFields.CONTRACTORS_COVERAGE, solrData.getContractorsCoverage());

		addNamedSolrDataSafe(document, solrData.getRecruitingData());
		addStringListFieldSafe(document, UserSearchableFields.TOOL_NAMES, solrData.getToolNames());
		addStringListFieldSafe(document, UserSearchableFields.SPECIALTY_NAMES, solrData.getSpecialtyNames());
		addLongFieldListSafe(document, UserSearchableFields.BLOCKED_USER_IDS, solrData.getBlockedUserIds());
		addLongFieldListSafe(document, UserSearchableFields.BLOCKED_COMPANY_IDS, solrData.getBlockedCompanyIds());
		addLinkedInFieldsSafe(document, solrData.getSolrLinkedInData());

		addFieldSafe(document, UserSearchableFields.LANE4_ACTIVE, solrData.isLane4Active());
		addFieldSafe(document, UserSearchableFields.SHARED_WORKER_ROLE, solrData.isSharedWorkerRole());
		addFieldSafe(document, UserSearchableFields.EMAIL_CONFIRMED, solrData.isEmailConfirmed());
		addFieldSafe(document, UserSearchableFields.LANE3_APPROVAL_STATUS, solrData.getLane3ApprovalStatus());
		addFieldSafe(document, UserSearchableFields.USER_STATUS_TYPE, solrData.getUserStatusType());
		addFieldSafe(document, UserSearchableFields.MBO, solrData.isMbo());
		addFieldSafe(document, UserSearchableFields.MBO_STATUS, solrData.getMboStatus());

		addOrgUnitsFieldSafe(document, solrData.getOrgUnits());

		Map<Long, Integer> companyCounts = solrData.getPaidCompanyAssignmentCounts();
		if (MapUtils.isNotEmpty(companyCounts)) {
			for (Map.Entry<Long, Integer> entry : companyCounts.entrySet()) {
				document.addField(UserSearchableFields.PAID_COMPANY_COUNT.getName() + entry.getKey(), entry.getValue());
			}
		}

		addLocationFields(document, solrData);
		addFieldSafe(document, UserSearchableFields.WARP_REQUISITION_ID, solrData.getWarpRequisitionId());
		document.addField(UserSearchableFields.USER_TYPE.getName(), SolrUserData.getUserType().getSolrUserTypeCode());
		return document;
	}

	private void addOrgUnitsFieldSafe(SolrInputDocument document, final List<String> listOfOrgUnits) {
		if (CollectionUtils.isEmpty(listOfOrgUnits)) {
			return;
		}

		document.addField(UserSearchableFields.ORG_UNITS.getName(), listOfOrgUnits);
	}

	private void addLocationFields(SolrInputDocument document, SolrUserData solrData) {
		document.addField(UserSearchableFields.LAT.getName(), solrData.getPoint().getLatitude());
		document.addField(UserSearchableFields.LNG.getName(), solrData.getPoint().getLongitude());
		String latLon = String.valueOf(solrData.getPoint().getLatitude()) + "," + String.valueOf(solrData.getPoint().getLongitude());
		document.addField(UserSearchableFields.LOCATION.getName(), latLon);
		document.addField(UserSearchableFields.LOCATIONS.getName(), Lists.newArrayList(latLon)); // locations is multiValued
	}

	private void addCompanyUserTagFieldsSafe(SolrInputDocument document, List<SolrCompanyUserTag> userTags) {
		if (userTags == null) return;
		for (SolrCompanyUserTag userTag : userTags) {
			addFieldSafe(document, UserSearchableFields.COMPANY_USER_TAGS, userTag.getCompanyTag());
			String[] individualTags = StringUtils.tokenizeToStringArray(userTag.getTag(), " ");
			for (String s : individualTags) {
				addFieldSafe(document, UserSearchableFields.SEARCHABLE_COMPANY_USER_TAGS, userTag.getCompanyTag(s));
			}
		}
	}

	private void addSkillsMatchingString(SolrInputDocument document, SolrUserData solrData) {
		String skills = org.apache.commons.lang3.StringUtils.join(solrData.getSkills(), " ");
		String tools = org.apache.commons.lang3.StringUtils.join(solrData.getToolNames(), " ");
		String certifications = org.apache.commons.lang3.StringUtils.join(solrData.getCertifications(), " ");
		String specialties = org.apache.commons.lang3.StringUtils.join(solrData.getSpecialtyNames(), " ");
		addFieldSafe(document, UserSearchableFields.WORK_SKILLS_MATCHING, skills + tools + certifications + specialties);
	}

	private void addLinkedInFieldsSafe(SolrInputDocument document, SolrLinkedInData linkedInData) {
		if (linkedInData == null) {
			return;
		}

		List<LinkedInCompanyData> linkedInCompanies = linkedInData.getLinkedInCompanies();
		for (LinkedInCompanyData linkedInCompanyData : linkedInCompanies) {
			String companyName = linkedInCompanyData.getCompanyName();
			addFieldSafe(document, UserSearchableFields.LINKED_IN_COMPANIES, companyName);
			String companyTitle = linkedInCompanyData.getCompanyTitle();
			addFieldSafe(document, UserSearchableFields.LINKED_IN_TITLES, companyTitle);
		}

		List<LinkedInSchoolData> linkedInSchools = linkedInData.getLinkedInSchools();
		for (LinkedInSchoolData linkedInSchoolData : linkedInSchools) {
			String schoolName = linkedInSchoolData.getSchoolName();
			addFieldSafe(document, UserSearchableFields.LINKED_IN_SCHOOL_NAMES, schoolName);
			//High school, MBA, etc..
			String fieldOfStudy = linkedInSchoolData.getFieldOfStudy();
			addFieldSafe(document, UserSearchableFields.LINKED_IN_FIELD_OF_STUDY, fieldOfStudy);
		}
	}

	private void addFieldSafe(SolrInputDocument document, Field fieldName, boolean boolValue) {
		document.addField(fieldName.getName(), boolValue);
	}

	private void addFieldSafe(SolrInputDocument document, Field doubleFieldName, Double doubleValue) {
		if (doubleValue != null) {
			document.addField(doubleFieldName.getName(), doubleValue);
		}
	}

	private void addFieldSafe(SolrInputDocument document, Field field, DateTime date) {
		if (date == null) {
			return;
		}
		document.addField(field.getName(), date.toDate());
	}

	private void addFieldSafe(SolrInputDocument document, Field field, Calendar date) {
		if (date == null) {
			return;
		}
		document.addField(field.getName(), new DateTime(date).toDate());
	}

	private void addNamedSolrDataSafe(SolrInputDocument doc, NamedSolrData data) {
		if (data.getId() > 0) {
			doc.addField(data.getIdField().getName(), data.getId());
		}
		if (!isEmpty(data.getName())) {
			doc.addField(data.getNameField().getName(), data.getName());
		}
	}

	private void addNamedSolrDataList(SolrInputDocument doc, Collection<? extends NamedSolrData> dataList) {
		if (dataList == null) {
			return;
		}
		for (NamedSolrData data : dataList) {
			addNamedSolrDataSafe(doc, data);
		}
	}

	// Will not index users who enter $0 for a coverage amount
	private void addInsuranceCoverageFieldSafe(SolrInputDocument doc, Field field, List<SolrInsuranceCoverageData> dataList) {
		if (dataList == null) {
			return;
		}
		for (SolrInsuranceCoverageData data : dataList) {
			addFieldSafe(doc, field, data.getCoverage());
		}
	}

	private void addStringListFieldSafe(SolrInputDocument document, Field field, Collection<String> fieldVals) {
		if (fieldVals == null || fieldVals.size() == 0) {
			return;
		}
		for (String fieldValue : fieldVals) {
			addFieldSafe(document, field, fieldValue);
		}
	}

	private void addFieldSafe(SolrInputDocument document, Field fieldName, float fieldValue) {
		document.addField(fieldName.getName(), fieldValue);
	}

	private void addLaneFieldsSafe(SolrInputDocument document,
								   List<SolrCompanyLaneData> laneDataList) {
		if (laneDataList == null) {
			return;
		}
		for (SolrCompanyLaneData laneData : laneDataList) {
			Long companyId = laneData.getCompanyId();
			document.addField(laneData.companyLaneField().getName(), companyId);
			document.addField(laneData.companyLaneUuidField().getName(), laneData.getCompanyUuid());
			document.addField("C" + companyId + "_i", laneData.laneNumber());
			if (laneData.getLaneType() == LaneType.LANE_2) {
				ApprovalStatus approvalStatus = laneData.getApprovalStatus();
				addFieldSafe(document, UserSearchableFields.LANE2_APPROVAL_STATUSES, approvalStatus.toString());
			}
		}
	}

	private void addLongFieldListSafe(SolrInputDocument document, Field field, List<Long> fieldVals) {
		if (CollectionUtils.isEmpty(fieldVals)) {
			return;
		}
		for (Long fieldVal : fieldVals) {
			addFieldSafe(document, field, fieldVal);
		}
	}

	private void addLongFieldListSafe(SolrInputDocument document, Field field, Set<Long> fieldVals) {
		addLongFieldListSafe(document, field, Lists.newArrayList(fieldVals));
	}

	private void addIntegerFieldListSafe(SolrInputDocument document, Field field, List<Integer> fieldVals) {
		if (CollectionUtils.isEmpty(fieldVals)) {
			return;
		}
		for (Integer fieldVal : fieldVals) {
			addFieldSafe(document, field, fieldVal);
		}
	}

	private void addIntegerFieldListSafe(SolrInputDocument document, Field field, Set<Integer> fieldVals) {
		addIntegerFieldListSafe(document, field, Lists.newArrayList(fieldVals));
	}

	private void addRatingFieldsSafe(SolrInputDocument document, SolrRatingData rating) {
		if (rating == null) {
			return;//unrated
		}
		addFieldSafe(document, UserSearchableFields.RATING, rating.getRating());
		addFieldSafe(document, UserSearchableFields.RATING_COUNT, rating.getRatingCount());
	}

	private void addGroupFieldsSafe(SolrInputDocument document, List<SolrGroupData> groupDataList) {
		if (CollectionUtils.isEmpty(groupDataList)) {
			return;
		}
		for (SolrGroupData groupData : groupDataList) {
			Long companyId = groupData.getCompanyId();
			Long groupId = groupData.getGroupId();
			String companyGroupId = companyId + "_" + groupId;
			addFieldSafe(document, UserSearchableFields.GROUP_IDS, groupId);
			addFieldSafe(document, UserSearchableFields.GROUP_UUIDS, groupData.getGroupUuid());
			addFieldSafe(document, UserSearchableFields.COMPANY_GROUP_IDS, companyGroupId);
		}
	}

	private void addSharedGroupFieldsSafe(SolrInputDocument document, List<SolrSharedGroupData> groupDataList) {
		if (CollectionUtils.isEmpty(groupDataList)) {
			return;
		}
		for (SolrSharedGroupData groupData : groupDataList) {
			Long networkId = groupData.getNetworkId();
			Long groupId = groupData.getGroupId();
			String sharedGroupId = networkId + "_" + groupId;
			addFieldSafe(document, UserSearchableFields.SHARED_GROUP_IDS, sharedGroupId);
		}
	}

	private void addGroupStatusFieldsSafe(
		final SolrInputDocument document,
		final List<SolrGroupData> groupDataList,
		final UserSearchableFields searchableGroupId,
		final UserSearchableFields searchableGroupUuid) {
		if (CollectionUtils.isEmpty(groupDataList)) {
			return;
		}
		for (final SolrGroupData groupData : groupDataList) {
			addFieldSafe(document, searchableGroupId, groupData.getGroupId());
			addFieldSafe(document, searchableGroupUuid, groupData.getGroupUuid());
		}
	}

	private void addAssessmentFieldsSafe(SolrInputDocument document, List<SolrAssessmentData> assessments) {
		if (CollectionUtils.isEmpty(assessments)) {
			return;
		}

		for (SolrAssessmentData assessment : assessments) {
			Long assessmentId = assessment.getAssessmentId();
			addFieldSafe(document, UserSearchableFields.ASSESSMENT_IDS, assessmentId);
			Long companyId = assessment.getCompanyId();
			String companyAssessmentId = companyId + "_" + assessmentId;
			addFieldSafe(document, UserSearchableFields.COMPANY_ASSESSMENT_IDS, companyAssessmentId);
		}
	}

	private void addContractFieldsSafe(SolrInputDocument document, List<SolrContractData> contracts) {
		if (CollectionUtils.isEmpty(contracts)) {
			return;
		}

		for (SolrContractData contract : contracts) {
			addFieldSafe(document, UserSearchableFields.CONTRACT_IDS, contract.getContractId());
		}
	}

	private void addOtherAssessmentFieldsSafe(SolrInputDocument document,
											  List<SolrAssessmentData> assessments,
											  UserSearchableFields userSearchableField) {
		if (CollectionUtils.isEmpty(assessments)) {
			return;
		}

		for (SolrAssessmentData assessment : assessments) {
			Long assessmentId = assessment.getAssessmentId();
			addFieldSafe(document, userSearchableField, assessmentId);
		}
	}

	private void addCertificationFieldsSafe(SolrInputDocument document, List<SolrCertificationData> certifications) {
		if (CollectionUtils.isEmpty(certifications)) {
			return;
		}
		addNamedSolrDataList(document, certifications);

		for (SolrCertificationData cert : certifications) {
			document.addField(UserSearchableFields.CERTIFICATION_VENDORS.getName(), cert.getCertificationVendor());
		}
	}

	private void addLicenseFieldsSafe(SolrInputDocument document, List<SolrLicenseData> licenses) {
		if (licenses == null) {
			return;
		}
		//adds the license name and license ID
		addNamedSolrDataList(document, licenses);

		//still need:
		//1) License state
		//2) State License ID
		//3) License Vendor
		for (SolrLicenseData license : licenses) {
			String licenseState = license.getLicenseState();
			addFieldSafe(document, UserSearchableFields.LICENSE_STATES, licenseState);
			addFieldSafe(document, UserSearchableFields.STATE_LICENSE_IDS, licenseState + "_" + license.getId());

			String licenseVendor = license.getLicenseVendor();
			addFieldSafe(document, UserSearchableFields.LICENSE_VENDORS, licenseVendor);
		}
	}

	private void addCompanyFieldsSafe(SolrInputDocument document, SolrCompanyData company) {
		if (company != null) {
			addFieldSafe(document, UserSearchableFields.COMPANY_TYPE, company.getType().getId());
			addFieldSafe(document, UserSearchableFields.COMPANY_UUID, company.getUuid());
			addNamedSolrDataSafe(document, company);
		}
	}

	private void addFieldSafe(SolrInputDocument document, Field field, String value) {
		if (value != null) {
			document.addField(field.getName(), value);
		}
	}

	private void addFieldSafe(SolrInputDocument document, Field field, long id) {
		if (id > 0) {
			addFieldSafe(document, field, String.valueOf(id));
		}
	}

	private void addField(SolrInputDocument document, Field field, Integer value) {
		document.addField(field.getName(), value);
	}

	private void addField(SolrInputDocument document, Field field, Double value) {
		document.addField(field.getName(), value);
	}

}
