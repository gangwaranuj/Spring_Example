package com.workmarket.data.solr.indexer.user;

import com.google.common.collect.Sets;
import com.workmarket.data.solr.indexer.SolrDocumentMapper;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.model.SolrAssessmentData;
import com.workmarket.data.solr.model.SolrCertificationData;
import com.workmarket.data.solr.model.SolrCompanyLaneData;
import com.workmarket.data.solr.model.SolrCompanyUserTag;
import com.workmarket.data.solr.model.SolrContractData;
import com.workmarket.data.solr.model.SolrGroupData;
import com.workmarket.data.solr.model.SolrInsuranceCoverageData;
import com.workmarket.data.solr.model.SolrInsuranceType;
import com.workmarket.data.solr.model.SolrLicenseData;
import com.workmarket.data.solr.model.SolrSkillData;
import com.workmarket.data.solr.model.SolrUserData;
import com.workmarket.data.solr.model.SolrVendorData;
import com.workmarket.data.solr.repository.UserBoostFields;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.company.CompanyStatusType;
import com.workmarket.domains.model.company.CompanyType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.hamcrest.core.IsNull;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.core.query.Field;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.avg;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.flatten;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.max;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectDistinct;
import static ch.lambdaj.Lambda.sum;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Solr DocumentMapper for vendor.
 */
@Component
public class SolrVendorDocumentMapper implements SolrDocumentMapper<SolrVendorData> {

	private static final Logger logger = LoggerFactory.getLogger(SolrVendorDocumentMapper.class);

	private static final SolrUserData EMPLOYEE_CLAZZ = on(SolrUserData.class);

	@Override
	public SolrInputDocument toSolrDocument(final SolrVendorData vendor) {
		if (vendor == null) {
			return null;
		}

		SolrInputDocument document = new SolrInputDocument();

		setBasicVendorData(document, vendor);
		setUserStatusType(document, vendor);

		if (CollectionUtils.isNotEmpty(vendor.getEmployees())) {
			final List<SolrUserData> employees = vendor.getEmployees();
			aggregateJobTitlesAndSet(document, employees);
			aggregateLocationsAndSet(document, vendor);
			aggregateIndustriesAndSet(document, employees);
			aggregateLicensesAndSet(document, employees);
			aggregateCertificationsAndSet(document, employees);
			aggregateAssessmentsAndSet(document, employees);
			setGroups(document, vendor);
			aggregateInsurancesAndSet(document, employees);
			aggregateQualificationsAndSet(document, employees);
			aggregateBlockedAndSet(document, employees);
			aggregateCountsRatiosAndSet(document, employees);
			aggregateContractsAndSet(document, employees);
			aggregateLanesAndSet(document, employees);
			setScreenings(document, employees);
		} else {
			logger.info("vendor with uuid {} has no employees", vendor.getUuid());
		}

		// TODO: for now always set vendor lane3ApprovalStatus to approved, though it is not used in new search
		document.setField(UserSearchableFields.LANE3_APPROVAL_STATUS.getName(), ApprovalStatus.APPROVED.getCode());
		// TODO: always set sharedWorkerRole to true for vendor because it means it is listed in search.
		// TODO: Though it is not used in new search.
		document.setField(UserSearchableFields.SHARED_WORKER_ROLE.getName(), Boolean.TRUE);

		// NOTE: no LinkedIn for vendor
		// NOTE: MBO should be deprecated
		// NOTE: verificationIds are deprecated
		// NOTE: deprecate recruiting
		// NOTE: companyPaidAssignmentCount_* are dynamic fields, which are not used in new worker search. Deprecate!
		// NOTE: no warp for vendor

		return document;
	}

	/**
	 * Safely sets the date field with DateTime type.
	 *
	 * @param document Solr input document
	 * @param field    The field to be set
	 * @param date     Date in DateTime type
	 */
	private void addDateFieldSafe(final SolrInputDocument document, final Field field, final DateTime date) {
		if (date != null) {
			document.addField(field.getName(), date.toDate());
		}
	}

	/**
	 * Sets an object to the field as long as it is not null.
	 *
	 * @param document Solr input document
	 * @param field    Field to be set
	 * @param object   Object to set
	 * @param <T>      Type of the object
	 */
	private <T> void addFieldSafe(final SolrInputDocument document, final Field field, final T object) {
		if (object != null) {
			document.addField(field.getName(), object);
		}
	}

	/**
	 * Merges a list of longs and sets to Solr input doc.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 * @param arg       Argument to extract a list of strings from SolrUserData
	 * @param field     Field to set in Solr input document
	 * @param <T>       Type T
	 */
	private <T> void mergeLongsAndSet(
		final SolrInputDocument document, final List<SolrUserData> employees, final T arg, final Field field) {

		final List<Long> ids = extract(selectDistinct(flatten(extract(employees, arg))), on(Long.class));
		if (ids.size() > 0) {
			document.setField(field.getName(), ids);
		}
	}

	/**
	 * Merges a list of strings and sets to Solr input doc.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 * @param arg       Argument to extract a list of strings from SolrUserData
	 * @param field     Field to set in Solr input document
	 * @param <T>       Type T
	 */
	private <T> void mergeStringsAndSet(
		final SolrInputDocument document, final List<SolrUserData> employees, final T arg, final Field field) {

		final Set<String> uniqueStrs = Sets.newHashSet();
		final List<String> extracted = extract(flatten(extract(employees, arg)), on(String.class));
		for (final String str : extracted) {
			if (StringUtils.isNotBlank(str)) {
				uniqueStrs.add(str.toLowerCase());
			}
		}
		if (CollectionUtils.isNotEmpty(uniqueStrs)) {
			document.setField(field.getName(), uniqueStrs);
		}
	}

	/**
	 * For a collection of objects, extracts ids from the objects and sets to Solr input doc.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 * @param arg1      The object to extract
	 * @param param     The field of the object to extract
	 * @param arg2      The field to de-dup.
	 * @param field     The field to set in Solr input document
	 * @param <T>       Type T
	 * @param <U>       Type U
	 */
	private <T, U> void extractIdsAndSet(
		final SolrInputDocument document,
		final List<SolrUserData> employees,
		final T arg1,
		final String param,
		final U arg2,
		final Field field) {

		final Collection<U> ids =
			extract(
				selectDistinct(
					flatten(extract(employees, arg1)),
					param),
				arg2);
		if (CollectionUtils.isNotEmpty(ids)) {
			document.setField(field.getName(), ids);
		}
	}

	private <T> void selectMaxAndSet(
		final SolrInputDocument document,
		final List<SolrUserData> employees,
		final T arg,
		final Field field) {

		final T max = max(employees, arg);
		if (max != null) {
			document.setField(field.getName(), max);
		}
	}

	private <T> void sumAndSet(
		final SolrInputDocument document,
		final List<SolrUserData> employees,
		final T arg,
		final Field field) {

		final T total = sum(employees, arg);
		if (total != null) {
			document.setField(field.getName(), total);
		} else {
			document.setField(field.getName(), 0);
		}
	}

	private <T> void avgAndSet(
		final SolrInputDocument document,
		final List<SolrUserData> employees,
		final T arg,
		final Field field) {

		final T average = avg(select(employees, having(arg, greaterThan(0D))), arg);
		if (average != null) {
			document.setField(field.getName(), average);
		} else {
			document.setField(field.getName(), 0);
		}
	}

	/**
	 * Sets the basic vendor information.
	 * NOTE:
	 * 1. For name, if vendor is solr proprietor, use effective name to set first and last name.
	 * 2. There is no default email for vendor.
	 * 3. There is no video for vendor.
	 * 4. Ignore job title for now until there is a job title/function field for company.
	 * 5. Ignore hourly rate for vendor.
	 *
	 * @param document Solr input document
	 * @param vendor   Solr vendor data
	 */
	private void setBasicVendorData(final SolrInputDocument document, final SolrVendorData vendor) {
		addFieldSafe(document, UserSearchableFields.ID, vendor.getId());
		addFieldSafe(document, UserSearchableFields.UUID, vendor.getUuid());
		addFieldSafe(document, UserSearchableFields.USER_NUMBER, vendor.getVendorNumber());
		addFieldSafe(document, UserSearchableFields.USER_TYPE, SolrVendorData.getUserType().getSolrUserTypeCode());
		if (CompanyType.SOLE_PROPRIETOR.equals(vendor.getCompanyType()) && vendor.getEffectiveName() != null) {
			addFieldSafe(document, UserSearchableFields.FULL_NAME, vendor.getEffectiveName());
			// split on effective name and set firstName and lastName
			final String[] nameComponents = vendor.getEffectiveName().split(" ", 2);
			addFieldSafe(document, UserSearchableFields.FIRST_NAME, nameComponents[0]);
			if (nameComponents.length == 2) {
				addFieldSafe(document, UserSearchableFields.LAST_NAME, nameComponents[1]);
			} else {
				addFieldSafe(document, UserSearchableFields.LAST_NAME, nameComponents[0]);
			}
		} else {
			addFieldSafe(document, UserSearchableFields.FIRST_NAME, vendor.getName());
			addFieldSafe(document, UserSearchableFields.LAST_NAME, vendor.getName());
			addFieldSafe(document, UserSearchableFields.FULL_NAME, vendor.getName());
		}
		addFieldSafe(document, UserSearchableFields.COMPANY_ID, vendor.getId());
		addFieldSafe(document, UserSearchableFields.COMPANY_NAME, vendor.getName());
		addFieldSafe(document, UserSearchableFields.COMPANY_TYPE, vendor.getCompanyType().getId());
		addDateFieldSafe(document, UserSearchableFields.CREATED_ON, vendor.getCreatedOn());
		if (vendor.getGeoPoint() != null) {
			addFieldSafe(document, UserSearchableFields.LAT, vendor.getGeoPoint().getLatitude());
			addFieldSafe(document, UserSearchableFields.LNG, vendor.getGeoPoint().getLongitude());
		}
		addFieldSafe(document, UserSearchableFields.CITY, vendor.getCity());
		addFieldSafe(document, UserSearchableFields.STATE, vendor.getState());
		addFieldSafe(document, UserSearchableFields.POSTAL_CODE, vendor.getPostalCode());
		addFieldSafe(document, UserSearchableFields.COUNTRY, vendor.getCountry());
		addFieldSafe(document, UserSearchableFields.AVATAR_SMALL_ASSET_URI, vendor.getAvatarSmallAssetUri());
		addFieldSafe(document, UserSearchableFields.HAS_AVATAR, (vendor.getAvatarSmallAssetUri() != null));
		addFieldSafe(document, UserSearchableFields.HAS_VIDEO, false);
		addFieldSafe(document, UserSearchableFields.MBO, false);
		addFieldSafe(document, UserSearchableFields.EMAIL_CONFIRMED, false);

		// TODO: always set vendor LANE4_ACTIVE to true?
		addFieldSafe(document, UserSearchableFields.LANE4_ACTIVE, true);
	}

	private void setUserStatusType(final SolrInputDocument document, final SolrVendorData vendor) {
		if (vendor.getCompanyStatusType().equals(CompanyStatusType.SUSPENDED)) {
			addFieldSafe(document, UserSearchableFields.USER_STATUS_TYPE, UserStatusType.SUSPENDED);
		} else if (CollectionUtils.isEmpty(vendor.getEmployees())) {
			// we use "hold" in userStatusType to indicate vendor inactive b/c there is no public workers available
			addFieldSafe(document, UserSearchableFields.USER_STATUS_TYPE, UserStatusType.HOLD);
		} else if (vendor.getCompanyStatusType().equals(CompanyStatusType.ACTIVE)) {
			addFieldSafe(document, UserSearchableFields.USER_STATUS_TYPE, UserStatusType.APPROVED);
		} else if (vendor.getCompanyStatusType().equals(CompanyStatusType.LOCKED)) {
			addFieldSafe(document, UserSearchableFields.USER_STATUS_TYPE, UserStatusType.LOCKED);
		} else {
			// Assume company always has status. If missing, default to "approved". Not sure if this is correct call.
			addFieldSafe(document, UserSearchableFields.USER_STATUS_TYPE, UserStatusType.APPROVED);
		}
	}

	/**
	 * Sets aggregated user licenses.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateLicensesAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		final Collection<SolrLicenseData> licenses =
			selectDistinct(flatten(extract(employees, EMPLOYEE_CLAZZ.getLicenses())), "id");
		for (final SolrLicenseData license : licenses) {
			addFieldSafe(document, UserSearchableFields.LICENSE_IDS, license.getId());
			addFieldSafe(document, UserSearchableFields.LICENSE_NAMES, license.getName());
			addFieldSafe(document, UserSearchableFields.LICENSE_STATES, license.getLicenseState());
			addFieldSafe(document, UserSearchableFields.LICENSE_VENDORS, license.getLicenseVendor());
			addFieldSafe(
				document,
				UserSearchableFields.STATE_LICENSE_IDS,
				license.getLicenseState() + "_" + license.getId());
		}
	}

	/**
	 * Sets aggregated user certifications.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateCertificationsAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		final Collection<SolrCertificationData> certifications =
			selectDistinct(flatten(extract(employees, EMPLOYEE_CLAZZ.getCertifications())), "id");
		for (final SolrCertificationData certification : certifications) {
			addFieldSafe(document, UserSearchableFields.CERTIFICATION_IDS, certification.getId());
			addFieldSafe(document, UserSearchableFields.CERTIFICATION_NAMES, certification.getName());
			addFieldSafe(document, UserSearchableFields.CERTIFICATION_VENDORS, certification.getCertificationVendor());
		}
	}

	/**
	 * Sets aggregated user assessments.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateAssessmentsAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		final Collection<SolrAssessmentData> assessments =
			selectDistinct(flatten(extract(employees, EMPLOYEE_CLAZZ.getAssessments())), "assessmentId");
		for (final SolrAssessmentData assessment : assessments) {
			addFieldSafe(document, UserSearchableFields.ASSESSMENT_IDS, assessment.getAssessmentId());
			addFieldSafe(document, UserSearchableFields.COMPANY_ASSESSMENT_IDS, assessment.getCompanyId() + "_" + assessment.getAssessmentId());
		}

		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getPassedAssessments(),
			"assessmentId",
			on(SolrAssessmentData.class).getAssessmentId(),
			UserSearchableFields.PASSED_ASSESSMENT_IDS);

		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getFailedTests(),
			"assessmentId",
			on(SolrAssessmentData.class).getAssessmentId(),
			UserSearchableFields.FAILED_TEST_IDS);

		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getInvitedAssessments(),
			"assessmentId",
			on(SolrAssessmentData.class).getAssessmentId(),
			UserSearchableFields.INVITED_ASSESSMENT_IDS);
	}

	/**
	 * Sets aggregated user groups and statuses.
	 *
	 * @param document Solr input document
	 * @param vendor   Solr Vendor data
	 */
	private void setGroups(final SolrInputDocument document, SolrVendorData vendor) {
		// NOTE: looks like we are missing COMPANY_GROUP_IDS
		// COMPANY_GROUP_IDS is a concatenated string of company id and group id
		// Not sure about the initial design, but it is a combination of MEMBER_GROUP_IDS and OVERRIDE
		// In the vendor context, it is essentially MEMBER_GROUP_IDS
		// We should get rid of COMPANY_GROUP_IDS, and instead update service side to provide corresponding queries
		document.setField(
			UserSearchableFields.MEMBER_GROUP_IDS.getName(),
			extract(vendor.getGroupMember(), on(SolrGroupData.class).getGroupId()));
		document.setField(
			UserSearchableFields.MEMBER_GROUP_UUIDS.getName(),
			extract(vendor.getGroupMember(), on(SolrGroupData.class).getGroupUuid()));
		document.setField(
			UserSearchableFields.PENDING_GROUP_IDS.getName(),
			extract(vendor.getGroupPending(), on(SolrGroupData.class).getGroupId()));
		document.setField(
			UserSearchableFields.PENDING_GROUP_UUIDS.getName(),
			extract(vendor.getGroupPending(), on(SolrGroupData.class).getGroupUuid()));
		document.setField(
			UserSearchableFields.INVITED_GROUP_IDS.getName(),
			extract(vendor.getGroupInvited(), on(SolrGroupData.class).getGroupId()));
		document.setField(
			UserSearchableFields.INVITED_GROUP_UUIDS.getName(),
			extract(vendor.getGroupInvited(), on(SolrGroupData.class).getGroupUuid()));
		document.setField(
			UserSearchableFields.DECLINED_GROUP_IDS.getName(),
			extract(vendor.getGroupDeclined(), on(SolrGroupData.class).getGroupId()));
		document.setField(
			UserSearchableFields.DECLINED_GROUP_UUIDS.getName(),
			extract(vendor.getGroupDeclined(), on(SolrGroupData.class).getGroupUuid()));
	}

	/**
	 * Sets aggregated user insurance coverage.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateInsurancesAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		final Collection<SolrInsuranceType> insurances =
			selectDistinct(flatten(extract(employees, EMPLOYEE_CLAZZ.getInsurances())), "id");
		for (final SolrInsuranceType insurance : insurances) {
			addFieldSafe(document, UserSearchableFields.INSURANCE_IDS, insurance.getId());
			addFieldSafe(document, UserSearchableFields.INSURANCE_NAMES, insurance.getName());
		}

		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getWorkersCompCoverage(),
			"coverage",
			on(SolrInsuranceCoverageData.class).getCoverage(),
			UserSearchableFields.WORKERS_COMPENSATION_COVERAGE);

		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getGeneralLiabilityCoverage(),
			"coverage",
			on(SolrInsuranceCoverageData.class).getCoverage(),
			UserSearchableFields.GENERAL_LIABILITY_COVERAGE);

		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getErrorsAndOmissionsCoverage(),
			"coverage",
			on(SolrInsuranceCoverageData.class).getCoverage(),
			UserSearchableFields.ERRORS_AND_OMISSIONS_COVERAGE);

		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getAutomobileCoverage(),
			"coverage",
			on(SolrInsuranceCoverageData.class).getCoverage(),
			UserSearchableFields.AUTOMOBILE_COVERAGE);

		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getCommercialGeneralLiabilityCoverage(),
			"coverage",
			on(SolrInsuranceCoverageData.class).getCoverage(),
			UserSearchableFields.COMMERCIAL_GENERAL_LIABILITY_COVERAGE);

		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getBusinessLiabilityCoverage(),
			"coverage",
			on(SolrInsuranceCoverageData.class).getCoverage(),
			UserSearchableFields.BUSINESS_LIABILITY_COVERAGE);

		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getContractorsCoverage(),
			"coverage",
			on(SolrInsuranceCoverageData.class).getCoverage(),
			UserSearchableFields.CONTRACTORS_COVERAGE);
	}

	/**
	 * Sets aggregated qualifications.
	 * NOTE: ignore "skillMatchingString", which is a bad design and not used in new worker search.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateQualificationsAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		final Collection<SolrSkillData> skills =
			selectDistinct(flatten(extract(employees, EMPLOYEE_CLAZZ.getSkills())), "id");
		for (final SolrSkillData skill : skills) {
			addFieldSafe(document, UserSearchableFields.SKILL_IDS, skill.getId());
			addFieldSafe(document, UserSearchableFields.SKILL_NAMES, skill.getName());
		}

		final Collection<SolrCompanyUserTag> userTags =
			flatten(extract(employees, EMPLOYEE_CLAZZ.getUserTags()));
		final Set<String> companyUserTags = Sets.newHashSet();
		for (final SolrCompanyUserTag userTag : userTags) {
			final String[] individualTags = StringUtils.split(userTag.getTag(), " ");
			for (final String s : individualTags) {
				companyUserTags.add(userTag.getCompanyTag(s));
			}
		}
		if (companyUserTags.size() > 0) {
			document.setField(UserSearchableFields.SEARCHABLE_COMPANY_USER_TAGS.getName(), companyUserTags);
		}

		mergeStringsAndSet(document, employees, EMPLOYEE_CLAZZ.getSpecialtyNames(), UserSearchableFields.SPECIALTY_NAMES);
		mergeStringsAndSet(document, employees, EMPLOYEE_CLAZZ.getToolNames(), UserSearchableFields.TOOL_NAMES);
		mergeStringsAndSet(document, employees, EMPLOYEE_CLAZZ.getCompletedWorkKeywords(), UserBoostFields.COMPLETED_WORK_KEYWORDS);
		mergeStringsAndSet(document, employees, EMPLOYEE_CLAZZ.getAppliedGroupsKeywords(), UserBoostFields.APPLIED_GROUPS_KEYWORDS);
		mergeLongsAndSet(document, employees, EMPLOYEE_CLAZZ.getWorkCompletedForCompanies(), UserBoostFields.WORK_COMPLETED_FOR_COMPANIES);
	}

	/**
	 * Sets aggregated blocked user and company ids for vendor.
	 * TODO: We may want to do this differently.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateBlockedAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		mergeLongsAndSet(document, employees, EMPLOYEE_CLAZZ.getBlockedUserIds(), UserSearchableFields.BLOCKED_USER_IDS);
		mergeLongsAndSet(document, employees, EMPLOYEE_CLAZZ.getBlockedCompanyIds(), UserSearchableFields.BLOCKED_COMPANY_IDS);
	}

	/**
	 * Sets screenings for vendor.
	 * If vendor has only one employee, use employee's screenings.
	 * Else set screening as pass!!!
	 * TODO: should we do above?
	 * NOTE: UserSearchableFields.SCREENING_STATUS is never set, and thus removed here.
	 * We should do the same for user's screenings.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void setScreenings(final SolrInputDocument document, final List<SolrUserData> employees) {
		if (employees.size() == 1) {
			final SolrUserData employee = employees.get(0);
			addFieldSafe(document, UserSearchableFields.LAST_DRUG_TEST_DATE, employee.getLastDrugTestDate());
			addFieldSafe(document, UserSearchableFields.LAST_BACKGROUND_CHECK_DATE, employee.getLastBackgroundCheckDate());
			addFieldSafe(document, UserSearchableFields.PASSED_BACKGROUND_CHECK, employee.isPassedBackgroundCheck());
			addFieldSafe(document, UserSearchableFields.PASSED_DRUG_TEST, employee.isPassedDrugTest());
		} else {
			addFieldSafe(document, UserSearchableFields.LAST_DRUG_TEST_DATE, DateTime.now());
			addFieldSafe(document, UserSearchableFields.LAST_BACKGROUND_CHECK_DATE, DateTime.now());
			addFieldSafe(document, UserSearchableFields.PASSED_BACKGROUND_CHECK, true);
			addFieldSafe(document, UserSearchableFields.PASSED_DRUG_TEST, true);
		}
	}

	/**
	 * Sets aggregated counts and ratios for vendor.
	 * TODO: if it is COUNT, use sum of counts. If it is ratio or average, use average of ratio or average.
	 * TODO: Except that RecentWorkingWeekRatio uses max to promote best workers of the vendor.
	 * TODO: RecentWorkingWeekRatio is boosted heavily in new worker search.
	 * TODO: use employee max travel distance for vendor?
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateCountsRatiosAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		sumAndSet(document, employees, EMPLOYEE_CLAZZ.getWorkCancelledCount(), UserSearchableFields.WORK_CANCELLED_COUNT);
		sumAndSet(document, employees, EMPLOYEE_CLAZZ.getWorkCompletedCount(), UserBoostFields.WORK_COMPLETED_COUNT);
		sumAndSet(document, employees, EMPLOYEE_CLAZZ.getRepeatClientsCount(), UserBoostFields.REPEATED_CLIENTS_COUNT);
		sumAndSet(document, employees, EMPLOYEE_CLAZZ.getBlocksCount(), UserBoostFields.DISTINCT_COMPANY_BLOCKS_COUNT);
		sumAndSet(document, employees, EMPLOYEE_CLAZZ.getLateLabelCount(), UserBoostFields.LATE_LABEL_COUNT);
		sumAndSet(document, employees, EMPLOYEE_CLAZZ.getAbandonedLabelCount(), UserBoostFields.ABANDONED_LABEL_COUNT);
		sumAndSet(document, employees, EMPLOYEE_CLAZZ.getCancelledLabelCount(), UserBoostFields.CANCELLED_LABEL_COUNT);
		// not sure what it means!
		sumAndSet(document, employees, EMPLOYEE_CLAZZ.getDelayedLabelCount(), UserBoostFields.COMPLETED_ON_TIME_LABEL_COUNT);
		sumAndSet(document, employees, EMPLOYEE_CLAZZ.getPaidAssignmentsCount(), UserBoostFields.PAID_ASSIGNMENTS_COUNT);

		avgAndSet(document, employees, EMPLOYEE_CLAZZ.getAverageStarRating(), UserBoostFields.AVERAGE_STAR_RATING);
		avgAndSet(document, employees, EMPLOYEE_CLAZZ.getSatisfactionRate(), UserBoostFields.SATISFACTION_RATE);
		avgAndSet(document, employees, EMPLOYEE_CLAZZ.getWeightedAverageRating(), UserBoostFields.WEIGHTED_AVERAGE_RATING);
		avgAndSet(document, employees, EMPLOYEE_CLAZZ.getOnTimePercentage(), UserBoostFields.ON_TIME_PERCENTAGE);
		avgAndSet(document, employees, EMPLOYEE_CLAZZ.getDeliverableOnTimePercentage(), UserBoostFields.DELIVERABLE_ON_TIME_PERCENTAGE);

		selectMaxAndSet(document, employees, EMPLOYEE_CLAZZ.getRecentWorkingWeeksRatio(), UserBoostFields.RECENT_WORKING_WEEKS_RATIO);

		selectMaxAndSet(document, employees, EMPLOYEE_CLAZZ.getMaxTravelDistance(), UserSearchableFields.MAX_TRAVEL_DISTANCE);

		// TODO: do we want to set vendor rating and rating_count by aggregating on employees'?
		List<SolrUserData> filtered = select(employees, having(EMPLOYEE_CLAZZ.getRating(), IsNull.notNullValue()));
		int ratingCountSum = sum(filtered, EMPLOYEE_CLAZZ.getRating().getRatingCount());
		document.setField(UserSearchableFields.RATING_COUNT.getName(), ratingCountSum);
		double avgRating = sum(filtered, EMPLOYEE_CLAZZ.getRating().getRating()) * 1.0D / filtered.size();
		document.setField(UserSearchableFields.RATING.getName(), (int) avgRating);
	}

	/**
	 * Sets aggregated employee job titles.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateJobTitlesAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		mergeStringsAndSet(document, employees, EMPLOYEE_CLAZZ.getTitle(), UserSearchableFields.JOB_FUNCTIONS);
	}

	/**
	 * Sets aggregated industry ids.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateIndustriesAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		mergeLongsAndSet(document, employees, EMPLOYEE_CLAZZ.getIndustries(), UserSearchableFields.INDUSTRIES_ID);
	}

	/**
	 * Sets aggregated employee lat,lon locations.
	 *
	 * @param document Solr input document
	 * @param vendor   Solr vendor data
	 */
	private void aggregateLocationsAndSet(final SolrInputDocument document, final SolrVendorData vendor) {
		final Set<String> latLons = Sets.newHashSet();
		if (vendor.getGeoPoint() != null) {
			final GeoPoint point = vendor.getGeoPoint();
			latLons.add(String.valueOf(point.getLatitude()) + "," + String.valueOf(point.getLongitude()));
		}

		final List<GeoPoint> points = extract(vendor.getEmployees(), EMPLOYEE_CLAZZ.getPoint());
		for (final GeoPoint point : points) {
			latLons.add(String.valueOf(point.getLatitude()) + "," + String.valueOf(point.getLongitude()));
		}
		if (latLons.size() > 0) {
			document.setField(UserSearchableFields.LOCATIONS.getName(), latLons);
		}
	}

	/**
	 * Sets aggregated contracts from employees.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateContractsAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		extractIdsAndSet(
			document,
			employees,
			EMPLOYEE_CLAZZ.getContracts(),
			"contractId",
			on(SolrContractData.class).getContractId(),
			UserSearchableFields.CONTRACT_IDS);
	}

	/**
	 * Sets aggregated lane association data from employees.
	 *
	 * @param document  Solr input document
	 * @param employees A list of Solr user data
	 */
	private void aggregateLanesAndSet(final SolrInputDocument document, final List<SolrUserData> employees) {
		final Collection<SolrCompanyLaneData> laneData = flatten(extract(employees, EMPLOYEE_CLAZZ.getLaneData()));
		final Set<Long> lane0CompanyIds = Sets.newHashSet(), lane1CompanyIds = Sets.newHashSet(),
			lane2CompanyIds = Sets.newHashSet(), lane3CompanyIds = Sets.newHashSet();
		for (final SolrCompanyLaneData lane : laneData) {
			switch (lane.getLaneType()) {
				case LANE_0:
					lane0CompanyIds.add(lane.getCompanyId());
					break;
				case LANE_1:
					lane1CompanyIds.add(lane.getCompanyId());
					break;
				case LANE_2:
					lane2CompanyIds.add(lane.getCompanyId());
					break;
				case LANE_3:
					lane3CompanyIds.add(lane.getCompanyId());
					break;
				default:
					break;
			}
		}
		if (CollectionUtils.isNotEmpty(lane0CompanyIds)) {
			document.setField(UserSearchableFields.LANE0_COMPANY_IDS.getName(), lane0CompanyIds);
		}
		if (CollectionUtils.isNotEmpty(lane1CompanyIds)) {
			document.setField(UserSearchableFields.LANE1_COMPANY_IDS.getName(), lane1CompanyIds);
		}
		if (CollectionUtils.isNotEmpty(lane2CompanyIds)) {
			document.setField(UserSearchableFields.LANE2_COMPANY_IDS.getName(), lane2CompanyIds);
		}
		if (CollectionUtils.isNotEmpty(lane3CompanyIds)) {
			document.setField(UserSearchableFields.LANE3_COMPANY_IDS.getName(), lane3CompanyIds);
		}
	}

}
