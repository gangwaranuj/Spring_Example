package com.workmarket.data.solr.indexer.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.model.SolrAssessmentData;
import com.workmarket.data.solr.model.SolrCertificationData;
import com.workmarket.data.solr.model.SolrCompanyLaneData;
import com.workmarket.data.solr.model.SolrContractData;
import com.workmarket.data.solr.model.SolrGroupData;
import com.workmarket.data.solr.model.SolrInsuranceCoverageData;
import com.workmarket.data.solr.model.SolrInsuranceType;
import com.workmarket.data.solr.model.SolrLicenseData;
import com.workmarket.data.solr.model.SolrRatingData;
import com.workmarket.data.solr.model.SolrSkillData;
import com.workmarket.data.solr.model.SolrUserData;
import com.workmarket.data.solr.model.SolrVendorData;
import com.workmarket.data.solr.repository.UserBoostFields;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.company.CompanyStatusType;
import com.workmarket.domains.model.company.CompanyType;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.utility.RandomUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for solrVendorData document mapper.
 */
public class SolrVendorDocumentMapperTest {

	private static final String LAST_NAME = "Smith";
	private static final String FIRST_NAME = "Joe";

	private static final GeoPoint VENDOR_GEO_POINT = new GeoPoint(40.7534195, -73.9931652);
	private static final GeoPoint EMPLOYEE1_GEO_POINT = VENDOR_GEO_POINT;
	private static final GeoPoint EMPLOYEE2_GEO_POINT = new GeoPoint(39.7495942, -84.210743);

	private SolrVendorDocumentMapper mapper;
	private SolrVendorData vendor;
	private SolrUserData employee1;
	private SolrUserData employee2;
	private SolrUserData employee3;

	@Before
	public void setUp() {
		mapper = new SolrVendorDocumentMapper();
		vendor = new SolrVendorData();
		setVendorBasicInfo();
		setEmployees();
	}

	@Test
	public void verify_basicInfo_mapping() {
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		assertEquals(vendor.getUuid(), doc.get(UserSearchableFields.UUID.getName()).getValue());
		assertEquals(vendor.getId(), doc.get(UserSearchableFields.ID.getName()).getValue());
		// vendor type defaults to Corporation, therefore first, last, and full name are the same
		assertEquals(vendor.getName(), doc.get(UserSearchableFields.FULL_NAME.getName()).getValue());
		assertEquals(vendor.getName(), doc.get(UserSearchableFields.FIRST_NAME.getName()).getValue());
		assertEquals(vendor.getName(), doc.get(UserSearchableFields.LAST_NAME.getName()).getValue());
		assertEquals(vendor.getGeoPoint().getLatitude(), doc.get(UserSearchableFields.LAT.getName()).getValue());
		assertEquals(vendor.getGeoPoint().getLongitude(), doc.get(UserSearchableFields.LNG.getName()).getValue());
		// lane4 active and shared worker role always true
		assertEquals(Boolean.TRUE, doc.get(UserSearchableFields.LANE4_ACTIVE.getName()).getValue());
		assertEquals(Boolean.TRUE, doc.get(UserSearchableFields.SHARED_WORKER_ROLE.getName()).getValue());
		// lane3ApprovalStatus is always 1 == approved
		assertEquals(ApprovalStatus.APPROVED.getCode(), doc.get(UserSearchableFields.LANE3_APPROVAL_STATUS.getName()).getValue());
	}

	@Test
	public void verify_soleProprietorName_mapping() {
		vendor.setCompanyType(CompanyType.SOLE_PROPRIETOR);
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		assertEquals(FIRST_NAME, doc.get(UserSearchableFields.FIRST_NAME.getName()).getValue());
		assertEquals(LAST_NAME, doc.get(UserSearchableFields.LAST_NAME.getName()).getValue());
		assertEquals(vendor.getEffectiveName(), doc.get(UserSearchableFields.FULL_NAME.getName()).getValue());
	}

	@Test
	public void verify_userStatusTypeWithEmptyEmployeeList_mapping() {
		vendor.setEmployees(Lists.<SolrUserData>newArrayList());
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);
		assertEquals(UserStatusType.HOLD, doc.get(UserSearchableFields.USER_STATUS_TYPE.getName()).getValue());
	}

	@Test
	public void verify_userStatusTypeWithNonEmptyEmployeeList_mapping() {
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);
		assertEquals(UserStatusType.APPROVED, doc.get(UserSearchableFields.USER_STATUS_TYPE.getName()).getValue());

	}

	@Test
	public void verify_jobTitles_mapping() {
		final String jobTitle1 = "HVAC tech";
		final String jobTitle2 = "writer";
		final Set<String> jobTitles = Sets.newHashSet(jobTitle1.toLowerCase(), jobTitle2.toLowerCase());
		employee1.setTitle(jobTitle1);
		employee2.setTitle(jobTitle2);
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(jobTitles, doc.get(UserSearchableFields.JOB_FUNCTIONS.getName()).getValues());
	}

	@Test
	public void verify_locations_mapping() {
		// NOTE: employee1 and vendor has the same geoPoint.
		final Set<String> locations = Sets.newHashSet(
			String.valueOf(VENDOR_GEO_POINT.getLatitude()) + "," + String.valueOf(VENDOR_GEO_POINT.getLongitude()),
			String.valueOf(EMPLOYEE2_GEO_POINT.getLatitude()) + "," + String.valueOf(EMPLOYEE2_GEO_POINT.getLongitude()));
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(locations, doc.get(UserSearchableFields.LOCATIONS.getName()).getValues());
	}

	@Test
	public void verify_industries_mapping() {
		final Set<Long> industries = Sets.newHashSet(1000L, 1001L, 1002L);
		employee1.setIndustries(Lists.newArrayList(1000L, 1001L));
		employee2.setIndustries(Lists.newArrayList(1000L, 1002L));
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(industries, doc.get(UserSearchableFields.INDUSTRIES_ID.getName()).getValues());
	}

	@Test
	public void verify_licenses_mapping() {
		final SolrLicenseData license1 = createLicense(1L, "l1", "NY", "v1");
		final SolrLicenseData license2 = createLicense(2L, "l2", "NY", "v2");
		final SolrLicenseData license3 = createLicense(3L, "l3", "CA", "v3");
		employee1.setLicenses(Lists.newArrayList(license1, license2));
		employee2.setLicenses(Lists.newArrayList(license1, license3));
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(Lists.newArrayList(1L, 2L, 3L), doc.get(UserSearchableFields.LICENSE_IDS.getName()).getValues());
		verifyMultiValuedField(Lists.newArrayList("NY", "NY", "CA"), doc.get(UserSearchableFields.LICENSE_STATES.getName()).getValues());
		verifyMultiValuedField(Lists.newArrayList("l1", "l2", "l3"), doc.get(UserSearchableFields.LICENSE_NAMES.getName()).getValues());
		verifyMultiValuedField(Lists.newArrayList("v1", "v2", "v3"), doc.get(UserSearchableFields.LICENSE_VENDORS.getName()).getValues());
		verifyMultiValuedField(
			Sets.newHashSet("NY_1", "NY_2", "CA_3"),
			doc.get(UserSearchableFields.STATE_LICENSE_IDS.getName()).getValues());
	}

	@Test
	public void verify_certifications_mapping() {
		final SolrCertificationData certification1 = createCertifications(1L, "c1", "v1");
		final SolrCertificationData certification2 = createCertifications(2L, "c2", "v3");
		final SolrCertificationData certification3 = createCertifications(3L, "c3", "v3");
		employee1.setCertifications(Lists.newArrayList(certification1, certification2));
		employee2.setCertifications(Lists.newArrayList(certification2, certification3));
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(Lists.newArrayList(1L, 2L, 3L), doc.get(UserSearchableFields.CERTIFICATION_IDS.getName()).getValues());
		verifyMultiValuedField(Lists.newArrayList("c1", "c2", "c3"), doc.get(UserSearchableFields.CERTIFICATION_NAMES.getName()).getValues());
		verifyMultiValuedField(Lists.newArrayList("v1", "v3", "v3"), doc.get(UserSearchableFields.CERTIFICATION_VENDORS.getName()).getValues());
	}

	@Test
	public void verify_assessments_mapping() {
		final SolrAssessmentData assessment1 = createAssessments(1L, 10L);
		final SolrAssessmentData assessment2 = createAssessments(2L, 20L);
		final SolrAssessmentData assessment3 = createAssessments(3L, 30L);

		employee1.setAssessments(Lists.newArrayList(assessment1, assessment2));
		employee1.setFailedTests(Lists.newArrayList(assessment3));
		employee1.setPassedAssessments(Lists.newArrayList(assessment2));
		employee1.setInvitedAssessments(Lists.newArrayList(assessment1));

		employee2.setAssessments(Lists.newArrayList(assessment2, assessment3));
		employee2.setFailedTests(Lists.newArrayList(assessment1));
		employee2.setPassedAssessments(Lists.newArrayList(assessment2));
		employee2.setInvitedAssessments(Lists.newArrayList(assessment3));

		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(Sets.newHashSet(1L, 2L, 3L), doc.get(UserSearchableFields.ASSESSMENT_IDS.getName()).getValues());
		verifyMultiValuedField(
			Sets.newHashSet("10_1", "20_2", "30_3"),
			doc.get(UserSearchableFields.COMPANY_ASSESSMENT_IDS.getName()).getValues());
		verifyMultiValuedField(Sets.newHashSet(1L, 3L), doc.get(UserSearchableFields.FAILED_TEST_IDS.getName()).getValues());
		verifyMultiValuedField(Sets.newHashSet(2L), doc.get(UserSearchableFields.PASSED_ASSESSMENT_IDS.getName()).getValues());
		verifyMultiValuedField(Sets.newHashSet(1L, 3L), doc.get(UserSearchableFields.INVITED_ASSESSMENT_IDS.getName()).getValues());
	}

	@Test
	public void verify_groups_mapping() {
		final SolrGroupData group1 = createGroup(5L, "uuid-g1", 1L);
		final SolrGroupData group2 = createGroup(6L, "uuid-g2", 1L);
		final SolrGroupData group3 = createGroup(9L, "uuid-g3", 1L);
		vendor.setGroupMember(Lists.newArrayList(group1, group2));
		vendor.setGroupInvited(Lists.newArrayList(group3));
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(Lists.newArrayList(5L, 6L), doc.get(UserSearchableFields.MEMBER_GROUP_IDS.getName()).getValues());
		verifyMultiValuedField(
			Lists.newArrayList("uuid-g1", "uuid-g2"),
			doc.get(UserSearchableFields.MEMBER_GROUP_UUIDS.getName()).getValues());
		verifyMultiValuedField(Lists.newArrayList(9L), doc.get(UserSearchableFields.INVITED_GROUP_IDS.getName()).getValues());
		verifyMultiValuedField(
			Lists.newArrayList("uuid-g3"),
			doc.get(UserSearchableFields.INVITED_GROUP_UUIDS.getName()).getValues());
		assertTrue(CollectionUtils.isEmpty(doc.get(UserSearchableFields.PENDING_GROUP_IDS.getName()).getValues()));
		assertTrue(CollectionUtils.isEmpty(doc.get(UserSearchableFields.DECLINED_GROUP_IDS.getName()).getValues()));
		assertFalse(doc.containsKey(UserSearchableFields.SHARED_GROUP_IDS.getName()));
		assertFalse(doc.containsKey(UserSearchableFields.PENDING_OVERRIDE_GROUP_IDS.getName()));
		assertFalse(doc.containsKey(UserSearchableFields.MEMBER_OVERRIDE_GROUP_IDS.getName()));
	}

	@Test
	public void verify_insurances_mapping() {
		final SolrInsuranceType type1 = createInsuranceType(1L, "t1");
		final SolrInsuranceType type2 = createInsuranceType(2L, "t2");
		final SolrInsuranceCoverageData coverage = createInsuranceCoverage(3L);
		employee1.setInsurances(Lists.newArrayList(type1));
		employee1.setAutomobileCoverage(Lists.newArrayList(coverage));
		employee2.setInsurances(Lists.newArrayList(type2));
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(Sets.newHashSet(1L, 2L), doc.get(UserSearchableFields.INSURANCE_IDS.getName()).getValues());
		verifyMultiValuedField(Sets.newHashSet("t1", "t2"), doc.get(UserSearchableFields.INSURANCE_NAMES.getName()).getValues());
		verifyMultiValuedField(Sets.newHashSet(3L), doc.get(UserSearchableFields.AUTOMOBILE_COVERAGE.getName()).getValues());
		assertFalse(doc.containsKey(UserSearchableFields.WORKERS_COMPENSATION_COVERAGE.getName()));
		assertFalse(doc.containsKey(UserSearchableFields.GENERAL_LIABILITY_COVERAGE.getName()));
		assertFalse(doc.containsKey(UserSearchableFields.ERRORS_AND_OMISSIONS_COVERAGE.getName()));
		assertFalse(doc.containsKey(UserSearchableFields.COMMERCIAL_GENERAL_LIABILITY_COVERAGE.getName()));
		assertFalse(doc.containsKey(UserSearchableFields.BUSINESS_LIABILITY_COVERAGE.getName()));
		assertFalse(doc.containsKey(UserSearchableFields.CONTRACTORS_COVERAGE.getName()));
	}

	@Test
	public void verify_qualifications_mapping() {
		final SolrSkillData skill1 = createSkills(1L, "HVAC");
		final SolrSkillData skill2 = createSkills(2L, "writing");
		final SolrSkillData skill3 = createSkills(3L, "tech service");
		employee1.setSkills(Lists.newArrayList(skill1, skill3));
		employee1.setSpecialtyNames(Lists.newArrayList("heat transfer"));
		employee1.setToolNames(Lists.newArrayList("condenser"));
		employee2.setSkills(Lists.newArrayList(skill2));
		employee2.setSpecialtyNames(Lists.newArrayList("fiction writing", "creative writing"));
		employee2.setToolNames(Lists.newArrayList("microsoft word"));
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(Sets.newHashSet(1L, 2L, 3L), doc.get(UserSearchableFields.SKILL_IDS.getName()).getValues());
		verifyMultiValuedField(
			Sets.newHashSet("HVAC", "writing", "tech service"),
			doc.get(UserSearchableFields.SKILL_NAMES.getName()).getValues());
		verifyMultiValuedField(
			Sets.newHashSet("heat transfer", "fiction writing", "creative writing"),
			doc.get(UserSearchableFields.SPECIALTY_NAMES.getName()).getValues());
		verifyMultiValuedField(
			Sets.newHashSet("condenser", "microsoft word"),
			doc.get(UserSearchableFields.TOOL_NAMES.getName()).getValues());
		assertFalse(doc.containsKey(UserSearchableFields.SEARCHABLE_COMPANY_USER_TAGS.getName()));
		assertFalse(doc.containsKey(UserBoostFields.COMPLETED_WORK_KEYWORDS.getName()));
		assertFalse(doc.containsKey(UserBoostFields.APPLIED_GROUPS_KEYWORDS.getName()));
		assertFalse(doc.containsKey(UserBoostFields.WORK_COMPLETED_FOR_COMPANIES.getName()));
	}

	@Test
	public void verify_blocked_mapping() {
		employee1.setBlockedUserIds(Lists.newArrayList(1L, 3L, 5L));
		employee1.setBlockedCompanyIds(Lists.newArrayList(10L, 20L));
		employee2.setBlockedUserIds(Lists.newArrayList(1L, 2L));
		employee2.setBlockedCompanyIds(Lists.newArrayList(10L));
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(Sets.newHashSet(1L, 2L, 3L, 5L), doc.get(UserSearchableFields.BLOCKED_USER_IDS.getName()).getValues());
		verifyMultiValuedField(Sets.newHashSet(10L, 20L), doc.get(UserSearchableFields.BLOCKED_COMPANY_IDS.getName()).getValues());
	}

	@Test
	public void verify_screeningsWithOneEmployee_mapping() {
		vendor.setEmployees(Lists.newArrayList(employee3));
		final DateTime now = DateTime.now();
		employee3.setLastDrugTestDate(now);
		employee3.setLastBackgroundCheckDate(now);
		employee3.setPassedBackgroundCheck(false);
		employee3.setPassedDrugTest(true);
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		assertEquals(Boolean.TRUE, doc.get(UserSearchableFields.PASSED_DRUG_TEST.getName()).getValue());
		assertEquals(Boolean.FALSE, doc.get(UserSearchableFields.PASSED_BACKGROUND_CHECK.getName()).getValue());
		assertEquals(now, doc.get(UserSearchableFields.LAST_BACKGROUND_CHECK_DATE.getName()).getValue());
		assertEquals(now, doc.get(UserSearchableFields.LAST_DRUG_TEST_DATE.getName()).getValue());
	}

	@Test
	public void verify_screeningsWithMultipleEmployees_mapping() {
		final DateTime oneMinuteAgo = DateTime.now().minusMinutes(1);
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		assertEquals(Boolean.TRUE, doc.get(UserSearchableFields.PASSED_DRUG_TEST.getName()).getValue());
		assertEquals(Boolean.TRUE, doc.get(UserSearchableFields.PASSED_BACKGROUND_CHECK.getName()).getValue());
		assertTrue(((DateTime) doc.get(UserSearchableFields.LAST_BACKGROUND_CHECK_DATE.getName()).getValue()).getMillis() > oneMinuteAgo.getMillis());
		assertTrue(((DateTime) doc.get(UserSearchableFields.LAST_DRUG_TEST_DATE.getName()).getValue()).getMillis() > oneMinuteAgo.getMillis());
	}

	@Test
	public void verify_contracts_mapping() {
		final SolrContractData contract1 = createContracts(1L);
		final SolrContractData contract2 = createContracts(2L);
		employee1.setContracts(Lists.newArrayList(contract1));
		employee2.setContracts(Lists.newArrayList(contract1, contract2));
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		verifyMultiValuedField(Lists.newArrayList(1L, 2L), doc.get(UserSearchableFields.CONTRACT_IDS.getName()).getValues());
	}

	@Test
	public void verify_countsAndRatios_mapping() {
		final SolrRatingData rating1 = createRating(10, 4);
		final SolrRatingData rating2 = createRating(12, 3);
		employee1.setWorkCancelledCount(1);
		employee1.setWorkCompletedCount(5);
		employee1.setSatisfactionRate(3.5);
		employee1.setAverageStarRating(4.4);
		employee1.setRecentWorkingWeeksRatio(2.7);
		employee1.setMaxTravelDistance(60);
		employee1.setRating(rating1);
		employee2.setWorkCancelledCount(2);
		employee2.setWorkCompletedCount(7);
		employee2.setSatisfactionRate(4.2);
		employee2.setAverageStarRating(4.8);
		employee2.setRecentWorkingWeeksRatio(3.2);
		employee2.setRating(rating2);
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		assertEquals(1 + 2, doc.get(UserSearchableFields.WORK_CANCELLED_COUNT.getName()).getValue());
		assertEquals(5 + 7, doc.get(UserBoostFields.WORK_COMPLETED_COUNT.getName()).getValue());
		assertEquals(0, doc.get(UserBoostFields.REPEATED_CLIENTS_COUNT.getName()).getValue());
		assertEquals(0, doc.get(UserBoostFields.DISTINCT_COMPANY_BLOCKS_COUNT.getName()).getValue());
		assertEquals(0, doc.get(UserBoostFields.LATE_LABEL_COUNT.getName()).getValue());
		assertEquals(0, doc.get(UserBoostFields.ABANDONED_LABEL_COUNT.getName()).getValue());
		assertEquals(0, doc.get(UserBoostFields.CANCELLED_LABEL_COUNT.getName()).getValue());
		assertEquals(0, doc.get(UserBoostFields.COMPLETED_ON_TIME_LABEL_COUNT.getName()).getValue());
		assertEquals(0, doc.get(UserBoostFields.PAID_ASSIGNMENTS_COUNT.getName()).getValue());

		assertEquals((3.5D + 4.2D) / 2D, doc.get(UserBoostFields.SATISFACTION_RATE.getName()).getValue());
		assertEquals((4.4D + 4.8D) / 2D, doc.get(UserBoostFields.AVERAGE_STAR_RATING.getName()).getValue());
		assertEquals(0D, doc.get(UserBoostFields.WEIGHTED_AVERAGE_RATING.getName()).getValue());
		assertEquals(0D, doc.get(UserBoostFields.ON_TIME_PERCENTAGE.getName()).getValue());
		assertEquals(0D, doc.get(UserBoostFields.DELIVERABLE_ON_TIME_PERCENTAGE.getName()).getValue());
		assertEquals(3.2D, doc.get(UserBoostFields.RECENT_WORKING_WEEKS_RATIO.getName()).getValue());
		assertEquals(60F, doc.get(UserSearchableFields.MAX_TRAVEL_DISTANCE.getName()).getValue());

		assertEquals(10 + 12, doc.get(UserSearchableFields.RATING_COUNT.getName()).getValue());
		assertEquals((4 + 3) / 2, doc.get(UserSearchableFields.RATING.getName()).getValue());
	}

	@Test
	public void verify_laneAssoications_mapping() {
		final SolrCompanyLaneData companyLaneData1 = createLanes(10, LaneType.LANE_1, ApprovalStatus.APPROVED);
		final SolrCompanyLaneData companyLaneData2 = createLanes(20, LaneType.LANE_1, ApprovalStatus.PENDING);
		final SolrCompanyLaneData companyLaneData3 = createLanes(30, LaneType.LANE_2, ApprovalStatus.APPROVED);
		final SolrCompanyLaneData companyLaneData4 = createLanes(40, LaneType.LANE_3, ApprovalStatus.APPROVED);
		employee1.setLaneData(Lists.newArrayList(companyLaneData1, companyLaneData3));
		employee2.setLaneData(Lists.newArrayList(companyLaneData1, companyLaneData2, companyLaneData4));
		final SolrInputDocument doc = mapper.toSolrDocument(vendor);

		assertFalse(doc.containsKey(UserSearchableFields.LANE0_COMPANY_IDS.getName()));
		verifyMultiValuedField(Sets.newHashSet(10L, 20L), doc.get(UserSearchableFields.LANE1_COMPANY_IDS.getName()).getValues());
		verifyMultiValuedField(Sets.newHashSet(30L), doc.get(UserSearchableFields.LANE2_COMPANY_IDS.getName()).getValues());
		verifyMultiValuedField(Sets.newHashSet(40L), doc.get(UserSearchableFields.LANE3_COMPANY_IDS.getName()).getValues());
	}

	private void setVendorBasicInfo() {
		final String vendorUuid = "vendor_uuid";
		final Long vendorId = 123L;
		final String vendorName = "vendor_name";
		final String vendorEffectiveName = FIRST_NAME + " " + LAST_NAME;
		final String defaultVendorStatusType = CompanyStatusType.ACTIVE;
		final CompanyType defaultVendorType = CompanyType.CORPORATION;

		vendor.setUuid(vendorUuid);
		vendor.setId(vendorId);
		vendor.setName(vendorName);
		vendor.setEffectiveName(vendorEffectiveName);
		vendor.setCompanyType(defaultVendorType);
		vendor.setCompanyStatusType(defaultVendorStatusType);
		vendor.setGeoPoint(VENDOR_GEO_POINT);
	}

	private SolrUserData setEmployeeBasicInfo() {
		SolrUserData employee = new SolrUserData();
		employee.setUuid(UUID.randomUUID().toString());
		employee.setId(RandomUtilities.nextLong());
		return employee;
	}

	private void setEmployees() {
		employee1 = setEmployeeBasicInfo();
		employee2 = setEmployeeBasicInfo();
		employee3 = setEmployeeBasicInfo();
		employee1.setPoint(EMPLOYEE1_GEO_POINT);
		employee2.setPoint(EMPLOYEE2_GEO_POINT);
		employee3.setPoint(EMPLOYEE2_GEO_POINT);
		vendor.setEmployees(Lists.newArrayList(employee1, employee2, employee3));
	}

	private SolrLicenseData createLicense(final Long id, final String name, final String state, final String licenseVendor) {
		final SolrLicenseData license = new SolrLicenseData();
		license.setId(id);
		license.setName(name);
		license.setLicenseState(state);
		license.setLicenseVendor(licenseVendor);
		return license;
	}

	private SolrCertificationData createCertifications(final Long id, final String name, final String vendor) {
		final SolrCertificationData certification = new SolrCertificationData();
		certification.setId(id);
		certification.setName(name);
		certification.setCertificationVendor(vendor);
		return certification;
	}

	private SolrAssessmentData createAssessments(final Long id, final Long companyId) {
		final SolrAssessmentData assessment = new SolrAssessmentData();
		assessment.setAssessmentId(id);
		assessment.setCompanyId(companyId);
		return assessment;
	}

	private SolrContractData createContracts(final Long id) {
		final SolrContractData contract = new SolrContractData();
		contract.setContractId(id);
		return contract;
	}

	private SolrSkillData createSkills(final Long id, final String name) {
		final SolrSkillData skill = new SolrSkillData();
		skill.setId(id);
		skill.setName(name);
		return skill;
	}

	private SolrInsuranceType createInsuranceType(final Long id, final String name) {
		final SolrInsuranceType insuranceType = new SolrInsuranceType();
		insuranceType.setId(id);
		insuranceType.setName(name);
		return insuranceType;
	}

	private SolrInsuranceCoverageData createInsuranceCoverage(final long coverage) {
		final SolrInsuranceCoverageData coverageData = new SolrInsuranceCoverageData();
		coverageData.setCoverage(coverage);
		return coverageData;
	}

	private SolrGroupData createGroup(final Long groupId, final String groupUuid, final Long companyId) {
		final SolrGroupData group = new SolrGroupData();
		group.setCompanyId(companyId);
		group.setGroupId(groupId);
		group.setGroupUuid(groupUuid);
		return group;
	}

	private SolrRatingData createRating(final int ratingCount, final int rating) {
		final SolrRatingData ratingData = new SolrRatingData();
		ratingData.setRating(rating);
		ratingData.setRatingCount(ratingCount);
		return ratingData;
	}

	private SolrCompanyLaneData createLanes(final long companyId, final LaneType laneType, final ApprovalStatus status) {
		final SolrCompanyLaneData lane = new SolrCompanyLaneData();
		lane.setCompanyId(companyId);
		lane.setLaneType(laneType);
		lane.setApprovalStatus(status);
		return lane;
	}

	private <T> void verifyMultiValuedField(Collection<T> expected, Collection<Object> actual) {
		assertEquals(expected.size(), actual.size());
		for (Object o : actual) {
			assertTrue(expected.contains((T) o));
		}

	}

}
