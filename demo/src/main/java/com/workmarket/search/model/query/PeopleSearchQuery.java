package com.workmarket.search.model.query;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.request.user.BackgroundScreeningChoice;
import com.workmarket.search.request.user.CompanyType;
import com.workmarket.search.request.user.NumericFilter;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.request.user.Verification;
import com.workmarket.utility.SearchUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static com.workmarket.data.solr.query.SearchQueryCreatorUtil.*;
import static com.workmarket.utility.DateUtilities.*;
import static com.workmarket.utility.SearchUtilities.joinWithAND;
import static com.workmarket.utility.SearchUtilities.joinWithOR;
import static java.lang.String.valueOf;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PeopleSearchQuery extends SearchQuery<PeopleSearchRequest> {

	private static final long serialVersionUID = -934342985539014086L;

	private static final int MAX_ROWS = 25;
	private static final int FACET_LIMIT_SMALL = 100;
	private static final int FACET_LIMIT_LARGE = 300;
	private static final String EXCLUDE_STRING_WITH_PREFIX = "-%s:%s_%s";
	private SearchUser currentUser;
	private PeopleSearchTransientData transientData;

	public PeopleSearchQuery(PeopleSearchTransientData transientData) {
		super( (PeopleSearchRequest)transientData.getOriginalRequest() );
		this.currentUser = transientData.getCurrentUser();
		this.transientData = transientData;
	}

	public PeopleSearchQuery addBaseQueryParams() {
		return this
			.addStartPosition()
			.addRowNumberToQuery()
			.enableFaceting();
	}

	public PeopleSearchQuery addBaseFilters() {
		return this
			.addCountryFilterQuery()
			.addGroupFilterQuery()
			.addSharedGroupFilterQuery()
			.addCompanyTypeFilterQuery()
			.addIndustryFilterQuery()
			.addMinimumSatisfactionRateQuery()
			.addMinimumOnTimePercentageQuery()
			.addMinimumDeliverableOnTimePercentageQuery()
			.addAssessmentFilter()
			.addInvitedToWorkFilter()
			.addBackgroundCheckFilter()
			.addLicenseFilters()
			.addHasAvatarFilter()
			.addHasVideoFilter()
			.addHasMboFilter()
			.addCertificationsFilterQuery()
			.addInsuranceFilterQuery()
			.addSkillsFilterQuery()
			.addCompanyFilterQuery()
			.addDeclinedCompanyFilterQuery()
			.addVerificationFilterQuery()
			.addUserFilterQuery()
			.addUserTypeFilterQuery();
	}

	public PeopleSearchQuery addBaseFacets() {
		if (request.isNoFacetsFlag()) {
			return this;
		}
		return this.addUserSearchBaseFacets()
				.addLicenseFacetOptions()
				.addBackgroundSetFacet()
				.addSharedGroupFacets();
	}

	public PeopleSearchQuery addSharedGroupFacets() {
		if (request.isSetNetworkIds()) {
			for (Long networkId : request.getNetworkIds()) {
				addFacetField(createFacetFieldWithFacetPrefix(UserSearchableFields.SHARED_GROUP_IDS, String.valueOf(networkId)));
			}
		}
		return this;
	}

	public PeopleSearchQuery addStartPosition() {
		int start = 0;
		if (request.isSetPaginationRequest()) {
			Pagination pagination = request.getPaginationRequest();
			if (pagination.isSetCursorPosition()) {
				start = (int)(request.getPaginationRequest().getCursorPosition());
			} else if (pagination.isSetPageNumber() && pagination.isSetPageSize()) {
				start = (pagination.getPageNumber() - 1) * pagination.getPageSize();
			}
		}
		this.setStart(start);
		return this;
	}

	public PeopleSearchQuery addRowNumberToQuery() {
		int rows = MAX_ROWS;
		if (request.isSetPaginationRequest() && request.getPaginationRequest().isSetPageSize()) {
			rows = request.getPaginationRequest().getPageSize();
		}
		this.setRows(rows);
		return this;
	}

	public PeopleSearchQuery enableFaceting() {
		setFacet(true);
		setFacetMissing(true);
		addFacetQuery("*:*");
		return this;
	}

	public PeopleSearchQuery addUserSearchBaseFacets() {
		 Long currentUserCompanyId = currentUser.getCompanyId();

		//Facet Limits
		set(createFacetLimitString(UserSearchableFields.STATE_LICENSE_IDS), FACET_LIMIT_SMALL);
		set(createFacetLimitString(UserSearchableFields.INSURANCE_IDS), FACET_LIMIT_SMALL);
		set(createFacetLimitString(UserSearchableFields.INDUSTRIES_ID), FACET_LIMIT_SMALL);
		set(createFacetLimitString(UserSearchableFields.CERTIFICATION_IDS), FACET_LIMIT_LARGE);

		//Facet Fields
		addFacetField(createFacetFieldString(UserSearchableFields.STATE_LICENSE_IDS));
		addFacetField(createFacetFieldString(UserSearchableFields.LICENSE_IDS));
		addFacetField(createFacetFieldString(UserSearchableFields.CERTIFICATION_IDS));
		addFacetField(createFacetFieldString(UserSearchableFields.COMPANY_GROUP_IDS));
		addFacetField(createFacetFieldString(UserSearchableFields.COMPANY_ASSESSMENT_IDS));
		addFacetField(createFacetFieldString(UserSearchableFields.SEARCHABLE_COMPANY_USER_TAGS));
		addFacetField(createFacetFieldString(UserSearchableFields.INDUSTRIES_ID));
		addFacetField(createFacetFieldString(UserSearchableFields.SKILL_IDS));
		addFacetField(createFacetFieldString(UserSearchableFields.VERIFICATION_IDS));
		addFacetField(createFacetFieldString(UserSearchableFields.INDUSTRIES_ID));
		addFacetField(createFacetFieldString(UserSearchableFields.COMPANY_TYPE));
		addFacetField(createFacetFieldString(UserSearchableFields.COUNTRY));
		addFacetField(createFacetFieldString(UserSearchableFields.HAS_AVATAR));
		addFacetField(createFacetFieldString(UserSearchableFields.COMPANY_ID));

		// Facet Prefix is required
		setFacetPrefix(UserSearchableFields.COMPANY_GROUP_IDS.getName(), valueOf(currentUserCompanyId) + "_");
		setFacetPrefix(UserSearchableFields.COMPANY_ASSESSMENT_IDS.getName(), valueOf(currentUserCompanyId) + "_");
		setFacetPrefix(UserSearchableFields.SEARCHABLE_COMPANY_USER_TAGS.getName(), valueOf(currentUserCompanyId) + "_");

		// Facet Queries
		addFacetQuery("{!ex=tl key=lane0}lane0CompanyIds:" + currentUserCompanyId);
		addFacetQuery("{!ex=tl key=lane1}lane1CompanyIds:" + currentUserCompanyId);
		addFacetQuery("{!ex=tl key=lane2}lane2CompanyIds:" + currentUserCompanyId);
		addFacetQuery("{!ex=tl key=lane3}lane3CompanyIds:" + currentUserCompanyId);
		addFacetQuery("{!ex=" + UserSearchableFields.COMPANY_ASSESSMENT_IDS.getName() + "} "
				+ UserSearchableFields.COMPANY_ASSESSMENT_IDS.getName() + ":" + currentUserCompanyId + "_*");
		addFacetQuery(createFacetFieldString(UserSearchableFields.COMPANY_GROUP_IDS) + ":" + currentUserCompanyId + "_*");
		addFacetQuery(buildMarketplaceLaneFacet(currentUserCompanyId));
		return this;
	}

	String buildMarketplaceLaneFacet(Long currentUserCompanyId) {
		Assert.notNull(currentUserCompanyId);
		String query;

		if (request.isSetNetworkIds()) {
			String sharedGroupsFilter = buildSharedGroupsFilter();
			if (request.isDisableMarketplace()) {
				query =  "{!ex=tl key=marketplace}(" + sharedGroupsFilter + String.format(" AND -lane0CompanyIds:%1$d AND -lane1CompanyIds:%1$d AND -lane2CompanyIds:%1$d AND -lane3CompanyIds:%1$d)", currentUserCompanyId);
			} else {
				String everyoneElse = "(lane4Active:true OR " + sharedGroupsFilter + ")";
				query = "{!ex=tl key=marketplace}(" + everyoneElse + String.format(" AND -lane0CompanyIds:%1$d AND -lane1CompanyIds:%1$d AND -lane2CompanyIds:%1$d AND -lane3CompanyIds:%1$d)", currentUserCompanyId);
			}

		} else {
			if (request.isDisableMarketplace()) {
				query = String.format(
						"{!ex=tl key=marketplace}(-lane0CompanyIds:%1$d AND -lane1CompanyIds:%1$d AND -lane2CompanyIds:%1$d AND -lane3CompanyIds:%1$d)", currentUserCompanyId);
			} else {
				query = String.format(
						"{!ex=tl key=marketplace}(lane4Active:true AND -lane0CompanyIds:%1$d AND -lane1CompanyIds:%1$d AND -lane2CompanyIds:%1$d AND -lane3CompanyIds:%1$d)", currentUserCompanyId);
			}
		}

		return query;
	}

	String buildSharedGroupsFilter() {
		if (request.isSetNetworkIds()) {
			Set<String> strFilters = newHashSetWithExpectedSize(request.getNetworkIds().size());
			for (Long networkId : request.getNetworkIds()) {
				strFilters.add(UserSearchableFields.SHARED_GROUP_IDS.getName() + ":" + String.valueOf(networkId) + "_*");
			}
			return "(" + joinWithOR(strFilters) + ")";
		}
		return StringUtils.EMPTY;
	}

	public PeopleSearchQuery addLicenseFacetOptions() {
		if (request.isSetStateLicenseFilter()) {

			Set<String> statesInLicenses = extractStatesFromLicenseFilter(request.getStateLicenseFilter());
			for (String state : statesInLicenses) {
				setFacetPrefix(UserSearchableFields.STATE_LICENSE_IDS.getName(), state + "_");
			}
		}
		return this;
	}

	private Set<String> extractStatesFromLicenseFilter(Set<String> stateLicenseFilter) {
		Set<String> states = Sets.newHashSetWithExpectedSize(4);
		for (String stateLicense : stateLicenseFilter) {
			states.add(stateLicense.split("_")[0]);
		}
		return states;
	}

	private List<String> createCompanyGroupIds(Collection<Long> groupFilter, Long companyId) {
		List<String> companyGroupIds = Lists.newArrayListWithCapacity(groupFilter.size());
		for (Long groupId : groupFilter) {
			companyGroupIds.add(String.valueOf(companyId) + "_" + String.valueOf(groupId));
		}
		return companyGroupIds;
	}

	private Set<String> createAssessmentFilterStrs(Collection<Long> assessmentFilter, Long companyId) {
		Set<String> companyAssessmentIds = Sets.newHashSetWithExpectedSize(assessmentFilter.size());
		for (Long assessmentId : assessmentFilter) {
			companyAssessmentIds.add(companyId + "_" + assessmentId);
		}
		return companyAssessmentIds;
	}

	public PeopleSearchQuery addBackgroundSetFacet() {
		addFacetQuery(UserSearchableFields.LAST_BACKGROUND_CHECK_DATE.getName() + ":[ " + getLuceneDate(getMidnightMonthsBefore(getNow(), 6))
				+ " TO " + getLuceneDate(getMidnightNextDay(getNow())) + " ]");

		return this;
	}

	public PeopleSearchQuery addCountryFilterQuery() {
		if (request.isSetCountryFilter()) {
			addFilterQueryStr(this, UserSearchableFields.COUNTRY, request.getCountryFilter());
		}
		return this;
	}

	public PeopleSearchQuery addGroupFilterQuery() {
		if (request.isSetGroupFilter()) {
			Collection<String> companyGroupIds = createCompanyGroupIds(request.getGroupFilter(), currentUser.getCompanyId());
			addFilterQueryStr(this, UserSearchableFields.COMPANY_GROUP_IDS, companyGroupIds);
		}
		return this;
	}

	public PeopleSearchQuery addSharedGroupFilterQuery() {
		if (request.isSetSharedGroupFilter()) {

			// Put group filters into this format: *_groupId.
			Set<String> groupFilters = Sets.newHashSet();
			for (Long id : request.getSharedGroupFilter()) {
				groupFilters.add("*_" + String.valueOf(id));
			}
			addFilterQueryStr(this, UserSearchableFields.SHARED_GROUP_IDS, groupFilters);
		}
		return this;
	}

	public PeopleSearchQuery addCompanyTypeFilterQuery() {
		if (isNotEmpty(request.getCompanyTypeFilter())) {
			Collection<Long> companyFilter = Sets.newHashSetWithExpectedSize(request.getCompanyTypeFilter().size());

			for (CompanyType companyType : request.getCompanyTypeFilter()) {
				companyFilter.add((long) companyType.getValue());
			}
			addFilterQuery(createFilterQuery(UserSearchableFields.COMPANY_TYPE, companyFilter));
		}
		return this;
	}

	public PeopleSearchQuery addIndustryFilterQuery() {
		if (isNotEmpty(request.getIndustryFilter())) {

			String queryString = "{!tag=industryId}lane1CompanyIds:(" + currentUser.getCompanyId() + ") OR " + UserSearchableFields.INDUSTRIES_ID.getName() + ":";
			String ids = "(" + StringUtils.join(request.getIndustryFilterIterator(), " ") + ")";
			this.addFilterQuery(queryString + ids);
		}
		return this;
	}

	public PeopleSearchQuery addMinimumSatisfactionRateQuery() {
		if (request.getSatisfactionRateFilter() != null) {
			String from = request.getSatisfactionRateFilter().isSetFrom() ? StringUtils.EMPTY + request.getSatisfactionRateFilter().getFrom() * 100 : "0";
			String to = request.getSatisfactionRateFilter().isSetTo() ? "" + request.getSatisfactionRateFilter().getTo() * 100 : "100";

			this.addFilterQuery(" ( satisfactionRate :[" + from + " TO " + to + "] )");
		}
		return this;
	}

	public PeopleSearchQuery addMinimumOnTimePercentageQuery() {
		if (request.getOnTimePercentageFilter() != null) {
			String from = request.getOnTimePercentageFilter().isSetFrom() ? StringUtils.EMPTY + request.getOnTimePercentageFilter().getFrom() : "0";
			String to = request.getOnTimePercentageFilter().isSetTo() ? "" + request.getOnTimePercentageFilter().getTo() : "1";

			this.addFilterQuery(" ( onTimePercentage :[" + from + " TO " + to + "] )");
		}
		return this;
	}

	public PeopleSearchQuery addMinimumDeliverableOnTimePercentageQuery() {
		NumericFilter deliverableOnTimePercentageFilter = request.getDeliverableOnTimePercentageFilter();

		if (deliverableOnTimePercentageFilter != null) {
			String from = deliverableOnTimePercentageFilter.isSetFrom() ? StringUtils.EMPTY + deliverableOnTimePercentageFilter.getFrom() : "0";
			String to = deliverableOnTimePercentageFilter.isSetTo() ? "" + deliverableOnTimePercentageFilter.getTo() : "1";

			this.addFilterQuery(" ( deliverableOnTimePercentage :[" + from + " TO " + to + "] )");
		}
		return this;
	}

	public PeopleSearchQuery addAssessmentFilter() {
		if (request.isSetAssessmentFilter()) {
			addFilterQueryStr(this, UserSearchableFields.COMPANY_ASSESSMENT_IDS, createAssessmentFilterStrs(request.getAssessmentFilter(), currentUser.getCompanyId()));
		}
		return this;
	}

	public PeopleSearchQuery addInvitedToWorkFilter() {
		if (request.isSetInvitedToWorkIdFilter()) {
			this.addFilterQuery(String.format("-%s:%s", UserSearchableFields.WORK_INVITED_IDS.getName(), request.getInvitedToWorkIdFilter()));
		}
		return this;
	}

	public PeopleSearchQuery addBackgroundCheckFilter() {
		if (!request.isSetBackgroundScreeningFilter() || request.getBackgroundScreeningFilter().size() == 0) {
			return this;
		}

		List<String> backgroundCheck = Lists.newArrayListWithExpectedSize(2);
		for (BackgroundScreeningChoice backgroundType : request.getBackgroundScreeningFilter()) {
			if (backgroundType != BackgroundScreeningChoice.backgroundCheckedWithinLast6Months) {
				backgroundCheck.add(UserSearchableFields.VERIFICATION_IDS.getName() + ":" + backgroundType.getValue());
			}
		}

		if (backgroundCheck.size() != 0) {
			this.addFilterQuery("{!tag=" + UserSearchableFields.VERIFICATION_IDS.getName() + "}" + joinWithAND(backgroundCheck));
		}

		if (request.getBackgroundScreeningFilter().contains(BackgroundScreeningChoice.backgroundCheckedWithinLast6Months)) {
			this.addFilterQuery(UserSearchableFields.LAST_BACKGROUND_CHECK_DATE.getName() + ":[ "
					+ getLuceneDate(getMidnightMonthsBefore(getNow(), 6)) + " TO " + getLuceneDate(getMidnightNextDay(getNow())) + " ]");
		}
		return this;
	}

	public PeopleSearchQuery addLicenseFilters() {
		if (request.isSetStateLicenseFilter()) {
			Set<String> licenseIds = request.getStateLicenseFilter();
			addFilterQueryStr(this, UserSearchableFields.STATE_LICENSE_IDS, licenseIds);
		}
		return this;
	}

	public PeopleSearchQuery addHasAvatarFilter() {
		//Check to see if "No Preference" is selected
		if (request == null || !request.isAvatarFilter()) {
			return this;
		}
		addFilterQuery("hasAvatar:true");
		return this;
	}

	public PeopleSearchQuery addCurrentAssessmentFilters() {
		if (request.isSetCurrentAssessmentId()) {
			long assessmentId = request.getCurrentAssessmentId();
			List<String> strFilters = new ArrayList<>(4);

			if (request.isSetInvitedAssessmentFilter()) {
				strFilters.add(UserSearchableFields.INVITED_ASSESSMENT_IDS.getName() + ":" + assessmentId);
			}
			if (request.isSetPassedAssessmentFilter()) {
				strFilters.add(UserSearchableFields.PASSED_ASSESSMENT_IDS.getName() + ":" + assessmentId);
			}
			if (request.isSetFailedTestFilter()) {
				strFilters.add(UserSearchableFields.FAILED_TEST_IDS.getName() + ":" + assessmentId);
			}
			if (request.isSetNotInvitedAssessmentFilter()) {
				strFilters.add(createNotInvitedFilterString(assessmentId));
			}

			if (isEmpty(strFilters)) {
				return this;
			}
			this.addFilterQuery("{!tag=" + UserSearchableFields.ASSESSMENT_STATUS.getName() + "}" + joinWithOR(strFilters));
		}
		return this;
	}

	public PeopleSearchQuery addCurrentAssessmentFacets() {
		if (request.isSetCurrentAssessmentId()) {
			long assessmentId = request.getCurrentAssessmentId();
			String assessmentStatus = UserSearchableFields.ASSESSMENT_STATUS.getName();
			addFacetQuery(createFacetQueryWithSeparateExParam(assessmentStatus, UserSearchableFields.INVITED_ASSESSMENT_IDS, assessmentId));
			addFacetQuery(createFacetQueryWithSeparateExParam(assessmentStatus, UserSearchableFields.PASSED_ASSESSMENT_IDS, assessmentId));
			addFacetQuery(createFacetQueryWithSeparateExParam(assessmentStatus, UserSearchableFields.FAILED_TEST_IDS, assessmentId));
			addFacetQuery(createNotInvitedToAssessmentQuery(assessmentId));
		}
		return this;
	}

	public static String createNotInvitedToAssessmentQuery(long assessmentId) {
		return "{!ex=" + UserSearchableFields.ASSESSMENT_STATUS.getName() + "}" +
				createNotInvitedFilterString(assessmentId);
	}

	private static String createNotInvitedFilterString(long assessmentId) {
		ImmutableList<String> excludedFields = ImmutableList.of(
				UserSearchableFields.INVITED_ASSESSMENT_IDS.getName(),
				UserSearchableFields.PASSED_ASSESSMENT_IDS.getName(),
				UserSearchableFields.FAILED_TEST_IDS.getName());

		Set<String> strFilters = newHashSetWithExpectedSize(excludedFields.size());
		for (String fieldName : excludedFields) {
			strFilters.add("-" + fieldName + ":" + assessmentId);
		}
		return "(" + "*:*"  + " AND " + joinWithAND(strFilters) + ")";
	}

	public PeopleSearchQuery addLaneFilterQuery() {
		// if there are no lane filters, no lanes are selected, and no resources selected
		boolean allLanes = !request.isSetLaneFilter();

		Set<LaneType> laneFilter = request.getLaneFilter();
		Long companyId = request.getLaneFilterCompanyId() == null ? currentUser.getCompanyId() : request.getLaneFilterCompanyId();
		List<String> queries = new LinkedList<>();

		for (LaneType lane : LaneType.values()) {
			if (!lane.isLane4() && transientData.isIgnoredLane(lane)) {
				continue;
			}

			if (allLanes || laneFilter.contains(lane)) {
				switch (lane) {
					case LANE_4: //Lane 4 means everyone else
						String laneCompanyFilters = String.format(" -lane0CompanyIds:(%1$d) -lane1CompanyIds:(%1$d) -lane2CompanyIds:(%1$d) -lane3CompanyIds:(%1$d) ", companyId);
						String sharedGroupsFilter = buildSharedGroupsFilter();

						if (request.isDisableMarketplace() || transientData.isIgnoredLane(lane)) {
							if (isNotBlank(sharedGroupsFilter)) {
								queries.add("(" + laneCompanyFilters + "AND " + sharedGroupsFilter + ")");
							}
						} else {
							if (isNotBlank(sharedGroupsFilter)) {
								queries.add("(" + laneCompanyFilters + " AND (lane4Active:true OR " + sharedGroupsFilter + "))");
							} else {
								queries.add("(" + laneCompanyFilters + " +lane4Active:true )");
							}
						}
						break;
					default:
						queries.add("lane" + lane.getValue() + "CompanyIds:(" + companyId + ")");
				}
			}
		}

		if (isNotEmpty(queries)) {
			this.addFilterQuery("{!tag=tl}" + SearchUtilities.joinWithOR(queries));
		}
		return this;
	}

	public PeopleSearchQuery addBlockedUserFilters() {
		this
			.addFilterQuery(UserSearchableFields.BLOCKED_USER_IDS.getName() + ":( -" + currentUser.getId() + ")")
			.addFilterQuery(UserSearchableFields.BLOCKED_COMPANY_IDS.getName() + ":( -" + currentUser.getCompanyId() + ")");
		return this;
	}

	public PeopleSearchQuery addInsuranceTypesFilterQuery() {
		return addInsuranceMinimumFilterQuery(request.getWorkersCompCoverageFilter(), UserSearchableFields.WORKERS_COMPENSATION_COVERAGE.getName())
				.addInsuranceMinimumFilterQuery(request.getGeneralLiabilityCoverageFilter(), UserSearchableFields.GENERAL_LIABILITY_COVERAGE.getName())
				.addInsuranceMinimumFilterQuery(request.getErrorsAndOmissionsCoverageFilter(), UserSearchableFields.ERRORS_AND_OMISSIONS_COVERAGE.getName())
				.addInsuranceMinimumFilterQuery(request.getAutomobileCoverageFilter(), UserSearchableFields.AUTOMOBILE_COVERAGE.getName())
				.addInsuranceMinimumFilterQuery(request.getCommercialGeneralLiabilityCoverageFilter(), UserSearchableFields.COMMERCIAL_GENERAL_LIABILITY_COVERAGE.getName())
				.addInsuranceMinimumFilterQuery(request.getBusinessLiabilityCoverageFilter(), UserSearchableFields.BUSINESS_LIABILITY_COVERAGE.getName())
				.addInsuranceMinimumFilterQuery(request.getContractorsCoverageFilter(), UserSearchableFields.CONTRACTORS_COVERAGE.getName());
	}

	public PeopleSearchQuery addInsuranceMinimumFilterQuery(NumericFilter numericFilter, String fieldName) {
		if (numericFilter != null) {
			addFilterQuery(createInsuranceMinimumFilterQuery(fieldName, numericFilter));
		}
		return this;
	}

	public PeopleSearchQuery addGroupInviteFilters() {
		if (transientData.isSetInviteToGroupId()) {
			long companyId = currentUser.getCompanyId();
			long groupId = transientData.getInviteToGroupId();
			this.addFilterQuery(String.format(EXCLUDE_STRING_WITH_PREFIX, UserSearchableFields.COMPANY_GROUP_IDS.getName(), companyId, groupId))
					.addFilterQuery("-" + UserSearchableFields.INVITED_GROUP_IDS.getName() + ":" + groupId)
					.addFilterQuery("-" + UserSearchableFields.PENDING_GROUP_IDS.getName() + ":" + groupId)
					.addFilterQuery("-" + UserSearchableFields.PENDING_OVERRIDE_GROUP_IDS.getName() + ":" + groupId)
					.addFilterQuery("-" + UserSearchableFields.DECLINED_GROUP_IDS.getName() + ":" + groupId);
		}
		return this;
	}

	public PeopleSearchQuery addGroupStatusFacets() {
		if (transientData.isSetMemberOfGroupId() && !request.isNoFacetsFlag()) {
			long groupId = transientData.getMemberOfGroupId();

			String groupStatus = UserSearchableFields.GROUP_STATUS.getName();
			this.addFacetQuery(createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.MEMBER_GROUP_IDS, groupId))
					.addFacetQuery(createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.MEMBER_OVERRIDE_GROUP_IDS, groupId))
					.addFacetQuery(createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.PENDING_GROUP_IDS, groupId))
					.addFacetQuery(createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.PENDING_OVERRIDE_GROUP_IDS, groupId))
					.addFacetQuery(createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.INVITED_GROUP_IDS, groupId))
					.addFacetQuery(createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.DECLINED_GROUP_IDS, groupId));
		}

		return this;
	}

	public PeopleSearchQuery addGroupStatusFilters() {
		if (transientData.isSetMemberOfGroupId()) {
			long groupId = transientData.getMemberOfGroupId();

			List<String> strFilters = new ArrayList<>(6);

			if (request.isGroupMemberFilter()) {
				strFilters.add(UserSearchableFields.MEMBER_GROUP_IDS.getName() + ":" + groupId);
			}
			if (request.isGroupOverrideMemberFilter()) {
				strFilters.add(UserSearchableFields.MEMBER_OVERRIDE_GROUP_IDS.getName() + ":" +groupId);
			}
			if (request.isGroupPendingFilter()) {
				strFilters.add(UserSearchableFields.PENDING_GROUP_IDS.getName() + ":" + groupId);
			}
			if (request.isGroupPendingFailedFilter()) {
				strFilters.add(UserSearchableFields.PENDING_OVERRIDE_GROUP_IDS.getName() + ":" + groupId);
			}
			if (request.isGroupInvitedFilter()) {
				strFilters.add(UserSearchableFields.INVITED_GROUP_IDS.getName() + ":" + groupId);
			}
			if (request.isGroupDeclinedFilter()) {
				strFilters.add(UserSearchableFields.DECLINED_GROUP_IDS.getName() + ":" + groupId);
			}

			// if no filters are set, then return all results
			if (isEmpty(strFilters)) {
				strFilters.add(UserSearchableFields.MEMBER_GROUP_IDS.getName() + ":" + groupId);
				strFilters.add(UserSearchableFields.MEMBER_OVERRIDE_GROUP_IDS.getName() + ":" +groupId);
				strFilters.add(UserSearchableFields.PENDING_GROUP_IDS.getName() + ":" + groupId);
				strFilters.add(UserSearchableFields.PENDING_OVERRIDE_GROUP_IDS.getName() + ":" + groupId);
				strFilters.add(UserSearchableFields.INVITED_GROUP_IDS.getName() + ":" + groupId);
				strFilters.add(UserSearchableFields.DECLINED_GROUP_IDS.getName() + ":" + groupId);
			}
			addFilterQuery("{!tag=" + UserSearchableFields.GROUP_STATUS.getName() + "}" + joinWithOR(strFilters));
		}

		return this;
	}

	public PeopleSearchQuery addHasVideoFilter() {
		if (request == null || !request.isVideoFilter()) {
			return this;
		}
		addFilterQuery("hasVideo:true");
		return this;
	}

	public PeopleSearchQuery addUserFilterQuery() {
		if (request == null || CollectionUtils.isEmpty(request.getUserIds())) {
			return this;
		}
		addFilterQuery("id:(" + Joiner.on(" OR ").join(request.getUserIds()) + ")");
		return this;
	}

	public PeopleSearchQuery addCertificationsFilterQuery() {
		String fq = createFilterQuery(UserSearchableFields.CERTIFICATION_IDS, request.getCertificationFilter());
		if (isNotBlank(fq)) {
			this.addFilterQuery(fq);
		}
		return this;
	}

	public PeopleSearchQuery addSkillsFilterQuery() {
		String fq = createFilterQuery(UserSearchableFields.SKILL_IDS, request.getSkillFilter());
		if (isNotBlank(fq)) {
			this.addFilterQuery(fq);
		}
		return this;
	}

	public PeopleSearchQuery addVerificationFilterQuery() {
		String fq = createFilterQuery(UserSearchableFields.VERIFICATION_IDS, request.getTestFilter());
		if (isNotBlank(fq)) {
			this.addFilterQuery(fq);
		}
		return this;
	}

	public PeopleSearchQuery addFailedScreeningFilterQuery() {
		String fq = ("-" + UserSearchableFields.VERIFICATION_IDS.getName() + ":(" + Verification.FAILED_BACKGROUND_CHECK.getValue() + " " + Verification.FAILED_DRUG_TEST.getValue() + ")");

		if (isNotBlank(fq)) {
			this.addFilterQuery(fq);
		}
		return this;
	}

	public PeopleSearchQuery addDeclinedCompanyFilterQuery() {
		if (request == null || CollectionUtils.isEmpty(request.getDeclinedCompanyFilter())) {
			return this;
		}
		addFilterQuery("-" + UserSearchableFields.COMPANY_ID.getName() + ":(" + Joiner.on(" OR ").join(request.getDeclinedCompanyFilter()) + ")");
		return this;
	}

	public PeopleSearchQuery addCompanyFilterQuery() {
		String fq = createFilterQuery(UserSearchableFields.COMPANY_ID, request.getCompanyFilter());
		if (isNotBlank(fq)) {
			this.addFilterQuery(fq);
		}
		return this;
	}

	public PeopleSearchQuery addInsuranceFilterQuery() {
		String fq = createFilterQuery(UserSearchableFields.INSURANCE_IDS, request.getInsuranceFilter());
		if (isNotBlank(fq)) {
			this.addFilterQuery(fq);
		}
		return this;
	}


	// MBO filter
	public PeopleSearchQuery addHasMboFilter() {
		//Check to see if "No Preference" is selected
		if (request == null || !request.isMboFilter()) {
			return this;
		}
		addFilterQuery("mbo:true");
		return this;
	}

	public PeopleSearchQuery addUserTypeFilterQuery() {
		if (request.getUserTypeFilter() != null) {
			final Set<Long> solrUserTypes = Sets.newHashSet();
			for (final SolrUserType userType : request.getUserTypeFilter()) {
				solrUserTypes.add((long) userType.getSolrUserTypeCode());
			}
			final String fq = createFilterQuery(UserSearchableFields.USER_TYPE, solrUserTypes);
			if (isNotBlank(fq)) {
				this.addFilterQuery(fq);
			}
		}
		return this;
	}

}
