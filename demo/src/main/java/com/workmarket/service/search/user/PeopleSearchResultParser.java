package com.workmarket.service.search.user;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.data.solr.repository.UserBoostFields;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.domains.groups.model.GroupMemberRequestType;
import com.workmarket.domains.model.assessment.AssessmentDerivedStatusType;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.search.model.AbstractSearchTransientData;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.model.query.PeopleSearchQuery;
import com.workmarket.search.request.user.BackgroundScreeningChoice;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.request.user.RatingsChoice;
import com.workmarket.search.request.user.RecruitingCampaign;
import com.workmarket.search.request.user.Verification;
import com.workmarket.search.response.FacetResult;
import com.workmarket.search.response.user.PeopleFacetResultType;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.business.requirementsets.EligibilityService;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.search.SearchResultParser;
import com.workmarket.service.search.SearchResultParserImpl;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.RatingSummary;
import com.workmarket.utility.DateUtilities;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.workmarket.data.solr.query.SearchQueryCreatorUtil.createFacetQueryWithSeparateExParam;
import static com.workmarket.service.search.SearchParserUtil.parseIntegerSetFromSolr;
import static com.workmarket.service.search.SearchParserUtil.parseLongArrayFromSolr;
import static com.workmarket.service.search.SearchParserUtil.parseLongSetFromSolr;
import static com.workmarket.service.search.SearchParserUtil.parseStringSetToLongSetFromSolr;
import static com.workmarket.utility.NumberUtilities.safeLongToInt;
import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.WordUtils.capitalizeFully;
import static org.apache.commons.lang.math.NumberUtils.createLong;

@Service
public class PeopleSearchResultParser extends SearchResultParserImpl implements SearchResultParser<PeopleSearchResponse> {

	@Autowired private EligibilityService eligibilityService;

	private static final String[] companyIdFields = {
      UserSearchableFields.LANE0_COMPANY_IDS.getName(), UserSearchableFields.LANE1_COMPANY_IDS.getName(),
      UserSearchableFields.LANE2_COMPANY_IDS.getName(), UserSearchableFields.LANE3_COMPANY_IDS.getName()};

	@Override
	public PeopleSearchResponse parseSolrQueryResponse(PeopleSearchResponse searchResponse, AbstractSearchTransientData hydrateData, QueryResponse queryResponse) throws SearchException {
		return parsePeopleSearchResponse(searchResponse, (PeopleSearchTransientData)hydrateData, queryResponse);
	}

	@Override
	protected Enum findFacetResultType(FacetField facetField) {
		for (PeopleFacetResultType resultType : PeopleFacetResultType.values()) { //TODO: change to map, rather than list
			if (facetField.getName().equals(findFacetFieldName(resultType))) {
				return resultType;
			}
		}
		return null;
	}

	private String findFacetFieldName(PeopleFacetResultType resultType) {
		switch (resultType) {
			case LICENSE:
				return UserSearchableFields.STATE_LICENSE_IDS.getName();
			case CERTIFICATION:
				return UserSearchableFields.CERTIFICATION_IDS.getName();
			case ASSESSMENT:
				return UserSearchableFields.COMPANY_ASSESSMENT_IDS.getName();
			case INVITED_ASSESSMENT:
				return UserSearchableFields.INVITED_ASSESSMENT_IDS.getName();
			case NOT_INVITED_ASSESSMENT:
				return UserSearchableFields.NOT_INVITED_ASSESSMENT_IDS.getName();
			case PASSED_ASSESSMENT:
				return UserSearchableFields.PASSED_ASSESSMENT_IDS.getName();
			case FAILED_TEST:
				return UserSearchableFields.FAILED_TEST_IDS.getName();
			case GROUP:
				return UserSearchableFields.COMPANY_GROUP_IDS.getName();
			case SHARED_GROUP:
				return UserSearchableFields.SHARED_GROUP_IDS.getName();
			case INDUSTRY:
				return UserSearchableFields.INDUSTRIES_ID.getName();
			case VERIFICATION:
				return UserSearchableFields.VERIFICATION_IDS.getName();
			case COMPANY_TYPE:
				return UserSearchableFields.COMPANY_TYPE.getName();
			case INSURANCE:
				return UserSearchableFields.INSURANCE_IDS.getName();
			case AVATAR:
				return UserSearchableFields.HAS_AVATAR.getName();
			case COUNTRY:
				return UserSearchableFields.COUNTRY.getName();
			case COMPANY_ID:
				return UserSearchableFields.COMPANY_ID.getName();
			case MBO:
				return UserSearchableFields.MBO.getName();
			case LANE:
				return "IGNORE";
				/*
				FIXME: XXX HACK KER - this mapping isn't a clean solution.. fix later but this works for now
				 */

			default:
				return "NOT SUPPORTED FACET";
		}
	}

	private PeopleSearchResponse parsePeopleSearchResponse(PeopleSearchResponse searchResponse, PeopleSearchTransientData hydrateData, QueryResponse queryResponse) {
		SearchUser currentUser = hydrateData.getCurrentUser();
		List<FacetField> facets = queryResponse.getFacetFields();
		Map<String, Integer> facetQueries = queryResponse.getFacetQuery();

		Integer totalCount = queryResponse.getFacetQuery().get("*:*");
		searchResponse.setTotalResultsCount(totalCount);

		int rank = 1;
		for (SolrDocument doc : queryResponse.getResults()) {
			searchResponse.addToResults(parsePeopleSearchResultFromDocument(doc, hydrateData, rank, currentUser));
			rank++;
		}
		if ( CollectionUtils.isEmpty(queryResponse.getResults()) ) {
			searchResponse.setResults(Lists.<PeopleSearchResult>newArrayList());
		}

		if (isNotEmpty(facets)) {
			// For parsing sharedGroup facets - a dynamic facet field
			List<FacetField> sharedGroupFacets = Lists.newArrayList();

			for (FacetField facetField : facets) {
				if (isStaticFacetFieldName(facetField.getName())) {
					parseFacetResult(facetField, searchResponse, false);
				} else if (StringUtils.contains(facetField.getName(), UserSearchableFields.SHARED_GROUP_IDS.getName())) {
					sharedGroupFacets.add(facetField);
				}
			}
			if (isNotEmpty(sharedGroupFacets)) {
				parseSharedGroupFacets(sharedGroupFacets, searchResponse, hydrateData);
			}
		}

		// Solr queries will always have at least 1 base facet query, see PeopleSearchQueryCreator::addBaseQueryParam
		if (MapUtils.isNotEmpty(facetQueries)) {

			// parsing the lane facets requires carrying over the facet query.
			// yes it sounds strange. KER
			parseLaneFacets(queryResponse.getFacetQuery(), searchResponse, hydrateData);
			parseAssessmentStatusFacets(queryResponse.getFacetQuery(), searchResponse, hydrateData);
			parseGroupStatusFacets(queryResponse.getFacetQuery(), searchResponse, hydrateData);
			parseRatingsFacets(queryResponse.getFacetQuery(), searchResponse, hydrateData);

			// at this point, verifications we auto filled and the 0 counts don't exist or the ordering preserved
			postProcessVerifications(queryResponse.getFacetQuery(), searchResponse, hydrateData);
		}

		return searchResponse;
	}

	private PeopleSearchResult parsePeopleSearchResultFromDocument(SolrDocument doc, PeopleSearchTransientData hydrateData, int rank, SearchUser currentUser) {
		PeopleSearchResult result = new PeopleSearchResult();

		parseScore(doc, result);
		parseCompany(doc, hydrateData, result);
		parseJobTitle(doc, result);
		parseVerificationIds(doc, result);
		parseUserBaseInfo(doc, result);
		parseAddress(doc, result);
		parseUserIdentifiers(doc, result);
		parseLane(doc, currentUser.getCompanyId(), result);
		parseAvatar(doc, result);
		parseVideoUri(doc, result);
		parseGeoPoint(doc, result);
		parseBlocked(result, currentUser.getBlockedUserIds());
		parseCertifications(doc, result);
		parseLicenses(doc, result);
		parseInsurances(doc, result);
		parseGroups(doc, result, hydrateData);
		parseCompanyAssessments(doc, hydrateData);
		parseCompanyUserTags(doc, result, hydrateData);
		parseRecruiting(doc, result);
		parseFeedback(doc, result);
		parseUserStatus(doc, result, currentUser);
		parseScreeningDates(doc, result);
		parseCompletedAssignments(doc, result);
		parseCancelledAssignments(doc, result);
		parseAbandonedAssignments(doc, result);
		parseGroupStatus(doc, hydrateData, result);
		parseAssessmentStatus(doc, hydrateData, result);
		parsePaidCompanyCounts(doc, result);
		parseMbo(doc, result);
		parsePassedAssessments(doc, result);
		parseContracts(doc, result);
		parseIndustries(doc, result);
		parseCountry(doc, result);
		parseCompanyLaneTypes(doc, result);
		parseSatisfactionRate(doc, result);
		parseSkillNames(doc, result);
		parseToolNames(doc, result);
		parseMaxTravelDistance(doc, result);
		parseUserUserType(doc, result);
		parseUuid(doc, result);

		if(hydrateData.getWork() != null) {
			parseUserEligibility(doc, hydrateData, result);
		}

		result.setRank(rank);
		return result;
	}

	private void parseUserEligibility(SolrDocument doc, PeopleSearchTransientData hydrateData, PeopleSearchResult result) {
		Long userId = (Long) doc.getFieldValue(UserSearchableFields.ID.getName());
		result.setEligibility(eligibilityService.getEligibilityFor(userId, hydrateData.getWork()));
	}

	private void parseMaxTravelDistance(SolrDocument doc, PeopleSearchResult result) {
		if (!doc.containsKey(UserSearchableFields.MAX_TRAVEL_DISTANCE.getName())) {
			return;
		}

		Float maxTravelDistance = (Float) doc.getFieldValue(UserSearchableFields.MAX_TRAVEL_DISTANCE.getName());
		if (maxTravelDistance != null) {
			result.setMaxTravelDistance(maxTravelDistance);
		}
	}

	private void parseToolNames(SolrDocument doc, PeopleSearchResult result) {
		if (!doc.containsKey(UserSearchableFields.TOOL_NAMES.getName())) {
			return;
		}

		Collection<Object> response = doc.getFieldValues(UserSearchableFields.TOOL_NAMES.getName());

		if (response == null) {
			return;
		}

		List<String> toolNames = Lists.newArrayListWithCapacity(response.size());
		for (Object tool : response) {
			toolNames.add((String) tool);
		}

		result.setToolNames(toolNames);
	}

	private void parseSkillNames(SolrDocument doc, PeopleSearchResult result) {
		if (!doc.containsKey(UserSearchableFields.SKILL_NAMES.getName())) {
			return;
		}

		Collection<Object> response = doc.getFieldValues(UserSearchableFields.SKILL_NAMES.getName());

		if (response == null) {
			return;
		}

		List<String> skillNames = Lists.newArrayListWithCapacity(response.size());
		for (Object skill : response) {
			skillNames.add((String) skill);
		}

		result.setSkillNames(skillNames);
	}

	private void parseCompany(SolrDocument doc, PeopleSearchTransientData hydrateData, PeopleSearchResult result) {
		String companyName = (String) doc.getFieldValue(UserSearchableFields.COMPANY_NAME.getName());
		Long companyId = (Long) doc.getFieldValue(UserSearchableFields.COMPANY_ID.getName());
		Long userId = (Long) doc.getFieldValue("id");
		Long companyType = (Long) doc.getFieldValue(UserSearchableFields.COMPANY_TYPE.getName());

		result.setCompanyId(companyId);
		result.setCompanyName(companyName);
		result.setCompanyType(companyType);
		hydrateData.getUserCompanyMap().put(userId, companyId);
 	}

	private void parseScore(SolrDocument doc, PeopleSearchResult result) {
		Float score = (Float) doc.getFieldValue("score");
		if (score != null) {
			if (!score.isNaN()) {
				result.setScore(score.doubleValue());
			}
		}
	}

	private void parseGeoPoint(SolrDocument doc, PeopleSearchResult result) {
		Double longitude = (Double) doc.getFieldValue(UserSearchableFields.LNG.getName());
		Double latitude = (Double) doc.getFieldValue(UserSearchableFields.LAT.getName());
		if (latitude != null && longitude != null) {
			result.setLocationPoint(new GeoPoint().setLatitude(latitude).setLongitude(longitude));
		}
	}

	private void parseAddress(SolrDocument doc, PeopleSearchResult result) {
		String city = (String) doc.getFieldValue(UserSearchableFields.CITY.getName());
		String state = (String) doc.getFieldValue(UserSearchableFields.STATE.getName());
		String zip = (String) doc.getFieldValue(UserSearchableFields.POSTAL_CODE.getName());
		String country = (String) doc.getFieldValue(UserSearchableFields.COUNTRY.getName());
		Address address = new Address();
		address.setCity(city);
		address.setState(state);
		address.setZip(zip);
		address.setCountry(country);
		result.setAddress(address);
		result.setCbsaName((String) doc.getFieldValue(UserSearchableFields.CBSA_NAME.getName()));
	}

	private void parseUserBaseInfo(SolrDocument doc, PeopleSearchResult result) {
		String firstName = (String) doc.getFieldValue(UserSearchableFields.FIRST_NAME.getName());
		String lastName = (String) doc.getFieldValue(UserSearchableFields.LAST_NAME.getName());
		result.setName(new Name(firstName, lastName));

		result.setEmail((String) doc.getFieldValue(UserSearchableFields.EMAIL.getName()));

		if (doc.containsKey(UserSearchableFields.CREATED_ON.getName())) {
			String createdOn = DateUtilities.getISO8601((Date) doc.getFieldValue(UserSearchableFields.CREATED_ON.getName()));
			result.setCreatedOn(createdOn);
		}

		if (doc.containsKey(UserSearchableFields.WORK_PHONE.getName())) {
			result.setWorkPhone((String) doc.getFieldValue(UserSearchableFields.WORK_PHONE.getName()));
		}

		if (doc.containsKey(UserSearchableFields.MOBILE_PHONE.getName())) {
			result.setMobilePhone((String) doc.getFieldValue(UserSearchableFields.MOBILE_PHONE.getName()));
		}

		if (doc.containsKey(UserBoostFields.LAST_ASSIGNED_WORK_DATE.getName())) {
			String lastAssignedDate = DateUtilities.getISO8601((Date) doc.getFieldValue(UserBoostFields.LAST_ASSIGNED_WORK_DATE.getName()));
			result.setLastAssignedWorkDate(lastAssignedDate);
		}
	}

	private void parseVerificationIds(SolrDocument doc, PeopleSearchResult result) {
		Collection<Object> verificationIds = doc.getFieldValues(UserSearchableFields.VERIFICATION_IDS.getName());
		if (isNotEmpty(verificationIds)) {
			for (Object verificationIdObj : verificationIds) {
				result.addToVerifications(Verification.findByValue(safeLongToInt((Long) verificationIdObj)));
			}
		}
	}

	private void parseBlocked(PeopleSearchResult result, List<Long> blockedUserIds) {
		if (blockedUserIds.contains(result.getUserId())) {
			result.setBlocked(true);
		}
	}

	private void parseAvatar(SolrDocument doc, PeopleSearchResult result) {
		if (!doc.containsKey(UserSearchableFields.AVATAR_SMALL_ASSET_URI.getName())) {
			return;
		}
		String smallAvatarAssetUri = (String) doc.getFieldValue(UserSearchableFields.AVATAR_SMALL_ASSET_URI.getName());
		result.setSmallAvatarAssetUri(smallAvatarAssetUri);
	}

	private void parseVideoUri(SolrDocument doc, PeopleSearchResult result) {
		if (!doc.containsKey(UserSearchableFields.VIDEO_ASSET_URI.getName())) {
			return;
		}

		String uri = (String) doc.getFieldValue(UserSearchableFields.VIDEO_ASSET_URI.getName());
		result.setVideoAssetUri(uri);
	}

	private void parseUserIdentifiers(SolrDocument doc, PeopleSearchResult result) {
		String userNumber = (String) doc.getFieldValue(UserSearchableFields.USER_NUMBER.getName());
		result.setUserNumber(userNumber);

		result.setUserId((Long) doc.getFieldValue("id"));
	}

	private void parseLane(SolrDocument doc, Long companyId, PeopleSearchResult result) {
		for (int i = 0; i < companyIdFields.length; i++) {
			Collection<Object> laneFields = doc.getFieldValues(companyIdFields[i]);
			if (laneFields == null) {
				continue;
			}
			if (laneFields.contains(companyId)) {
				result.setLane(LaneType.findByValue(i));
				return;
			}
		}
		// assume lane 4
		result.setLane(LaneType.LANE_4);
	}

	private void parseCompanyLaneTypes(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.LANE0_COMPANY_IDS.getName())) {
			Set<Long> ids = parseLongSetFromSolr(UserSearchableFields.LANE0_COMPANY_IDS.getName(), doc);
			if (isNotEmpty(ids)) {
				for (Long id : ids) {
					result.addToCompanyLaneTypes(id, LaneType.LANE_0);
				}
			}
		}

		if (doc.containsKey(UserSearchableFields.LANE1_COMPANY_IDS.getName())) {
			Set<Long> ids = parseLongSetFromSolr(UserSearchableFields.LANE1_COMPANY_IDS.getName(), doc);
			if (isNotEmpty(ids)) {
				for (Long id : ids) {
					result.addToCompanyLaneTypes(id, LaneType.LANE_1);
				}
			}
		}

		if (doc.containsKey(UserSearchableFields.LANE2_COMPANY_IDS.getName())) {
			Set<Long> ids = parseLongSetFromSolr(UserSearchableFields.LANE2_COMPANY_IDS.getName(), doc);
			if (isNotEmpty(ids)) {
				for (Long id : ids) {
					result.addToCompanyLaneTypes(id, LaneType.LANE_2);
				}
			}
		}

		if (doc.containsKey(UserSearchableFields.LANE3_COMPANY_IDS.getName())) {
			Set<Long> ids = parseLongSetFromSolr(UserSearchableFields.LANE3_COMPANY_IDS.getName(), doc);
			if (isNotEmpty(ids)) {
				for (Long id : ids) {
					result.addToCompanyLaneTypes(id, LaneType.LANE_3);
				}
			}
		}
	}

	private void parseSatisfactionRate(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.SATISFACTION_RATE.getName())) {
			Double rate = (Double) doc.getFieldValue(UserSearchableFields.SATISFACTION_RATE.getName());
			result.setSatisfactionRate(rate);
		}
	}

	private void parseScreeningDates(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.LAST_BACKGROUND_CHECK_DATE.getName())) {
			String bgrdDate = DateUtilities.getISO8601((Date) doc.getFieldValue(UserSearchableFields.LAST_BACKGROUND_CHECK_DATE.getName()));
			result.setLastBackgroundCheckDate(bgrdDate);
		}
		if (doc.containsKey(UserSearchableFields.LAST_DRUG_TEST_DATE.getName())) {
			String drugDate = DateUtilities.getISO8601((Date) doc.getFieldValue(UserSearchableFields.LAST_DRUG_TEST_DATE.getName()));
			result.setLastDrugTestDate(drugDate);
		}
	}

	private void parseCompletedAssignments(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserBoostFields.WORK_COMPLETED_COUNT.getName())) {
			Integer workCompleted = (Integer) doc.getFieldValue(UserBoostFields.WORK_COMPLETED_COUNT.getName());
			result.setWorkCompletedCount(workCompleted);
		}
	}

	private void parseCancelledAssignments(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.WORK_CANCELLED_COUNT.getName())) {
			Integer cancelledWork = (Integer) doc.getFieldValue(UserSearchableFields.WORK_CANCELLED_COUNT.getName());
			result.setWorkCancelledCount(cancelledWork);
		}
	}

	private void parseAbandonedAssignments(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.ABANDONED_COUNT.getName())) {
			Integer abandonedCount = (Integer) doc.getFieldValue(UserSearchableFields.ABANDONED_COUNT.getName());
			result.setWorkAbandonedCount(abandonedCount);
		}
	}

	private void parsePassedAssessments(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.PASSED_ASSESSMENT_IDS.getName())) {
			Collection<Object> ids = doc.getFieldValues(UserSearchableFields.PASSED_ASSESSMENT_IDS.getName());
			if (isNotEmpty(ids)) {
				for (Object id : ids) {
					result.addToPassedAssessments(Long.parseLong((String)id));
				}
			}
		}
	}

	private void parseContracts(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.CONTRACT_IDS.getName())) {
			Set<Integer> ids = parseIntegerSetFromSolr(UserSearchableFields.CONTRACT_IDS.getName(), doc);
			if (isNotEmpty(ids)) {
				for (Integer id : ids) {
					result.addToContracts(id.longValue());
				}
			}
		}
	}

	private void parseJobTitle(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.JOB_TITLE.getName())) {
			String jobTitle = (String) doc.getFieldValue(UserSearchableFields.JOB_TITLE.getName());
			result.setJobTitle(jobTitle);
		}
	}

	private void parseUserUserType(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.USER_TYPE.getName())) {
			int solrUserTypeCode = (int) doc.getFieldValue(UserSearchableFields.USER_TYPE.getName());
			SolrUserType userType = SolrUserType.getSolrUserTypeByCode(solrUserTypeCode);
			// default to worker if missing
			if (userType == null) {
				userType = SolrUserType.WORKER;
			}
			result.setUserType(userType);
		}
	}

	private void parseUuid(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.UUID.getName())) {
			String uuid = (String) doc.getFieldValue(UserSearchableFields.UUID.getName());
			result.setUuid(uuid);
		}
	}

	private void postProcessVerifications(Map<String, Integer> facetQuery, PeopleSearchResponse peopleSearchResponse, PeopleSearchTransientData hydrateData) {
		// do we have verifications?
		final List<FacetResult> currentVerifications;
		if (peopleSearchResponse.getFacets().containsKey(PeopleFacetResultType.VERIFICATION)) {
			currentVerifications = peopleSearchResponse.getFacets().get(PeopleFacetResultType.VERIFICATION);
		} else {
			currentVerifications = emptyList();
		}
		final List<FacetResult> verifications = newArrayListWithCapacity(BackgroundScreeningChoice.values().length);
		verifications.add(createVerificationFacet(currentVerifications, hydrateData, BackgroundScreeningChoice.backgroundCheck, facetQuery));
		verifications.add(createVerificationFacet(currentVerifications, hydrateData, BackgroundScreeningChoice.backgroundCheckedWithinLast6Months, facetQuery));
		verifications.add(createVerificationFacet(currentVerifications, hydrateData, BackgroundScreeningChoice.drugTested, facetQuery));

		peopleSearchResponse.getFacets().put(PeopleFacetResultType.VERIFICATION, verifications);
	}

	private FacetResult createVerificationFacet(List<FacetResult> currentVerifications, PeopleSearchTransientData hydrateData, BackgroundScreeningChoice backgroundcheck, Map<String, Integer> facetQuery) {
		switch (backgroundcheck) {
			case backgroundCheck:
			case drugTested:
				return createStandardVerificationFacet(currentVerifications, hydrateData, backgroundcheck);
			case backgroundCheckedWithinLast6Months:
				return createBackgroundCheckDateFacet(facetQuery, hydrateData);
		}
		return null;
	}

	private FacetResult createStandardVerificationFacet(List<FacetResult> currentVerifications, PeopleSearchTransientData hydrateData, BackgroundScreeningChoice backgroundcheck) {
		// check to see if the verification exists
		FacetResult facetResult = null;
		for (FacetResult currentVerification : currentVerifications) {
			if (currentVerification.getFacetId().equals(valueOf(backgroundcheck.getValue()))) {
				facetResult = currentVerification;
				break;
			}
		}
		if (facetResult == null) {
			// facet was never returned to the results, but we are required to
			// return 0 count facets
			facetResult = new FacetResult().setActive(false).setFacetCount(0).setFacetId(valueOf(backgroundcheck.getValue()));
		}
		// check the facets
		PeopleSearchRequest originalRequest = (PeopleSearchRequest)hydrateData.getOriginalRequest();
		if (originalRequest.isSetBackgroundScreeningFilter()) {
			Collection<BackgroundScreeningChoice> choices = originalRequest.getBackgroundScreeningFilter();
			if (choices.contains(backgroundcheck)) {
				facetResult.setActive(true);
			}
		}
		facetResult.setFacetName(lookupFacetDisplayName(backgroundcheck));
		return facetResult;
	}

	private FacetResult createBackgroundCheckDateFacet(Map<String, Integer> facetQuery, PeopleSearchTransientData hydrateData) {
		// have to find the key that begins with "lastbackgroundcheck date"
		// This is because the last background check's facet query looks
		// something like this:
		// lastBackgroundCheckDate:[ 2010-10-20T00:00:00Z TO
		// 2011-04-21T00:00:00Z ] and we only
		// know the name of it. It always has to exist because it's part of the
		// query, even if it's 0 size.
		// so we're going to take it out
		FacetResult result = new FacetResult();
		result.setFacetCount(0);
		result.setFacetId(valueOf(BackgroundScreeningChoice.backgroundCheckedWithinLast6Months.getValue()));
		result.setFacetName("Checked within last 6 months.");
		for (Map.Entry<String, Integer> facet : facetQuery.entrySet()) {
			if (StringUtils.startsWith(facet.getKey(), UserSearchableFields.LAST_BACKGROUND_CHECK_DATE.getName())) {
				// we have a facet.. get the count
				result.setFacetCount(facet.getValue());
				// was it in the request?
				if (hasBackground6Month( (PeopleSearchRequest) hydrateData.getOriginalRequest() )) {
					result.setActive(true);
				}
			}
		}
		return result;
	}

	private String lookupFacetDisplayName(BackgroundScreeningChoice choice) {
		switch (choice) {
			case backgroundCheck:
				return "Background Check";
			case backgroundCheckedWithinLast6Months:
				return "Checked within last 6 months";
			case drugTested:
				return "Drug Test";
			default:
				return "Unknown";
		}
	}

	private boolean hasBackground6Month(PeopleSearchRequest originalRequest) {
		return originalRequest.isSetBackgroundScreeningFilter()
				&& originalRequest.getBackgroundScreeningFilter().contains(BackgroundScreeningChoice.backgroundCheckedWithinLast6Months);
	}

	private void parseRatingsFacets(Map<String, Integer> facetQuery, PeopleSearchResponse peopleSearchResponse,
									PeopleSearchTransientData hydrateData) {
		Set<RatingsChoice> ratings = ( (PeopleSearchRequest) hydrateData.getOriginalRequest()).getRatingsFilter();
		if (ratings == null) {
			ratings = emptySet();
		}

		List<FacetResult> ratingsFacets = Lists.newArrayListWithExpectedSize(5);

		ratingsFacets.add(createRatingFacet(ratings, MapUtils.getInteger(facetQuery, "{!ex=rating}rating:[80 TO 100]", 0), RatingsChoice.SHOW_FOUR_STARS));
		ratingsFacets.add(createRatingFacet(ratings, MapUtils.getInteger(facetQuery, "{!ex=rating}rating:[60 TO 79]", 0), RatingsChoice.SHOW_THREE_STARS));
		ratingsFacets.add(createRatingFacet(ratings, MapUtils.getInteger(facetQuery, "{!ex=rating}rating:[40 TO 59]", 0), RatingsChoice.SHOW_TWO_STARS));
		ratingsFacets.add(createRatingFacet(ratings, MapUtils.getInteger(facetQuery, "{!ex=rating}rating:[20 TO 39]", 0), RatingsChoice.SHOW_ONE_STAR));
		ratingsFacets.add(createRatingFacet(ratings, MapUtils.getInteger(facetQuery, "{!ex=rating}-rating:[0 TO 100]", 0), RatingsChoice.SHOW_UNRATED));

		peopleSearchResponse.putToFacets(PeopleFacetResultType.RATING, ratingsFacets);
	}

	private FacetResult createRatingFacet(Collection<RatingsChoice> ratings, int starCount, RatingsChoice choice) {
		FacetResult result = new FacetResult().setFacetCount(starCount).setFacetId(valueOf(choice.getValue()));
		if (ratings.contains(choice)) {
			result.setActive(true);
		}
		result.setFacetName(formatRatingNameFromEnum(choice));
		return result;
	}

	public void parseAssessmentStatusFacets(Map<String, Integer> facetQuery, PeopleSearchResponse searchResponse, PeopleSearchTransientData data) {
		if (data.isAssessmentInviteSearch()) {
			PeopleSearchRequest originalRequest = (PeopleSearchRequest) data.getOriginalRequest();
			long assessmentId = originalRequest.getCurrentAssessmentId();

			String assessmentStatus = UserSearchableFields.ASSESSMENT_STATUS.getName();
			String invitedAssessmentKey = createFacetQueryWithSeparateExParam(assessmentStatus, UserSearchableFields.INVITED_ASSESSMENT_IDS, assessmentId);

			parseStatusFacet(
					facetQuery, searchResponse, assessmentId, originalRequest.isSetInvitedAssessmentFilter(),
					invitedAssessmentKey,
					PeopleFacetResultType.INVITED_ASSESSMENT
			);

			String passedAssessmentKey = createFacetQueryWithSeparateExParam(assessmentStatus, UserSearchableFields.PASSED_ASSESSMENT_IDS, assessmentId);
			parseStatusFacet(
					facetQuery, searchResponse, assessmentId, originalRequest.isSetPassedAssessmentFilter(),
					passedAssessmentKey,
					PeopleFacetResultType.PASSED_ASSESSMENT
			);

			String failedTestKey = createFacetQueryWithSeparateExParam(assessmentStatus, UserSearchableFields.FAILED_TEST_IDS, assessmentId);
			parseStatusFacet(
					facetQuery, searchResponse, assessmentId, originalRequest.isSetFailedTestFilter(),
					failedTestKey,
					PeopleFacetResultType.FAILED_TEST
			);

			String notInvitedKey = PeopleSearchQuery.createNotInvitedToAssessmentQuery(assessmentId);
			parseStatusFacet(
					facetQuery, searchResponse, assessmentId, originalRequest.isSetNotInvitedAssessmentFilter(),
					notInvitedKey,
					PeopleFacetResultType.NOT_INVITED_ASSESSMENT
			);
		}
	}

	public void parseSharedGroupFacets(List<FacetField> sharedGroupFacets, PeopleSearchResponse searchResponse, PeopleSearchTransientData data)  {
		// Build shared data map to flatten the raw solr data
		Map<String, Long> sharedGroupData =  Maps.newHashMap();
		for (FacetField facet : sharedGroupFacets) {
			for (FacetField.Count count: facet.getValues()) {
				String facetName = count.getName();
				Long memberCount = count.getCount();
				if (memberCount > 0 && isNotBlank(facetName)) {
					String groupId = facetName.split("_")[1];
					if (!sharedGroupData.containsKey(groupId)) {
						sharedGroupData.put(groupId, memberCount);
					}
				}
			}
		}

		// Transfer map data to list of facet results
		List<FacetResult> facetResults = new ArrayList<>(sharedGroupData.size());
		for (Map.Entry<String, Long> group : sharedGroupData.entrySet()) {
			String groupId = group.getKey();
			Long memberCount = group.getValue();
			FacetResult result = new FacetResult().setFacetCount(memberCount).setFacetId(groupId);
			facetResults.add(result);
		}

		// Sort facets by member count, descending
		Collections.sort(facetResults, new Comparator<FacetResult>() {
			public int compare(FacetResult o1, FacetResult o2) {
				return -1 * Long.valueOf(o1.getFacetCount()).compareTo(o2.getFacetCount());
			}
		});

		// Add to search response
		if (isNotEmpty(facetResults)) {
			searchResponse.putToFacets(PeopleFacetResultType.SHARED_GROUP, facetResults);
		}

	}

	public void parseGroupStatusFacets(Map<String, Integer> facetQuery, PeopleSearchResponse searchResponse, PeopleSearchTransientData data) {
		if (data.isGroupMemberSearch()) {
			long groupId = data.getMemberOfGroupId();
			String groupStatus = UserSearchableFields.GROUP_STATUS.getName();
			PeopleSearchRequest request = (PeopleSearchRequest)data.getOriginalRequest();

			String groupMemberKey = createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.MEMBER_GROUP_IDS, groupId);
			String groupOverrideKey = createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.MEMBER_OVERRIDE_GROUP_IDS, groupId);
			String groupPendingKey = createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.PENDING_GROUP_IDS, groupId);
			String groupPendingOverrideKey = createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.PENDING_OVERRIDE_GROUP_IDS, groupId);
			String groupInvitedKey = createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.INVITED_GROUP_IDS, groupId);
			String groupDeclinedKey = createFacetQueryWithSeparateExParam(groupStatus, UserSearchableFields.DECLINED_GROUP_IDS, groupId);

			parseStatusFacet(
				facetQuery, searchResponse, groupId, request.isGroupMemberFilter(),
				groupMemberKey,
				PeopleFacetResultType.GROUP_MEMBERS
			);
			parseStatusFacet(
				facetQuery, searchResponse, groupId, request.isGroupOverrideMemberFilter(),
				groupOverrideKey,
				PeopleFacetResultType.GROUP_MEMBERS_OVERRIDE
			);
			parseStatusFacet(
				facetQuery, searchResponse, groupId, request.isGroupPendingFilter(),
				groupPendingKey,
				PeopleFacetResultType.GROUP_PENDING_MEMBERS
			);
			parseStatusFacet(
				facetQuery, searchResponse, groupId, request.isGroupPendingFailedFilter(),
				groupPendingOverrideKey,
				PeopleFacetResultType.GROUP_PENDING_OVERRIDE_MEMBERS
			);
			parseStatusFacet(
				facetQuery, searchResponse, groupId, request.isGroupInvitedFilter(),
				groupInvitedKey,
				PeopleFacetResultType.GROUP_INVITED
			);
			parseStatusFacet(
				facetQuery, searchResponse, groupId, request.isGroupDeclinedFilter(),
				groupDeclinedKey,
				PeopleFacetResultType.GROUP_DECLINED
			);
		}
	}

	public void parseStatusFacet(
		Map<String, Integer> facetQuery,
		PeopleSearchResponse peopleSearchResponse,
		long assessmentId,
		boolean isActive,
		String facetKey,
		PeopleFacetResultType peopleFacetResultType) {

		List<FacetResult> statusFacet = Lists.newArrayListWithExpectedSize(1);
		Integer facetCount = MapUtils.getInteger(facetQuery, facetKey, 0);
		String assessId = valueOf(assessmentId);

		FacetResult result = new FacetResult()
			.setFacetCount(facetCount)
			.setFacetId(assessId)
			.setActive(isActive);

		statusFacet.add(result);
		peopleSearchResponse.putToFacets(peopleFacetResultType, statusFacet);
	}

	private String formatRatingNameFromEnum(RatingsChoice choice) {
		return capitalizeFully(choice.name().replace("_", " "));
	}

	private void parseLaneFacets(Map<String, Integer> facetQuery, PeopleSearchResponse peopleSearchResponse,
								 PeopleSearchTransientData hydrateData) {
		List<FacetResult> laneFacets = Lists.newArrayListWithCapacity(5);
		for (int i = 0; i < 5; i++) {
			LaneType currentLane = LaneType.findByValue(i);
			Integer laneCount = 0;
			if (i != 4) {
				if (!hydrateData.isIgnoredLane(currentLane)) {
					laneCount = MapUtils.getInteger(facetQuery, "lane" + i, 0);
				}
			} else {
				laneCount = MapUtils.getInteger(facetQuery, "marketplace", 0);
			}
			if (laneCount != null) {
				FacetResult result = new FacetResult();
				result.setFacetCount(laneCount);
				result.setFacetId("" + i);
				laneFacets.add(result);
			}
		}
		peopleSearchResponse.putToFacets(PeopleFacetResultType.LANE, laneFacets);
	}

	private void parseUserStatus(SolrDocument doc, PeopleSearchResult result, SearchUser currentUser) {
		if (doc.containsKey(UserSearchableFields.LANE2_COMPANY_IDS.getName())) {
			Long[] lane2CompanyIds = parseLongArrayFromSolr(UserSearchableFields.LANE2_COMPANY_IDS.getName(), doc);
			String[] lane2ApprovalStatuses = parseStringArrayFromSolr(UserSearchableFields.LANE2_APPROVAL_STATUSES.getName(), doc);
			long companyId = currentUser.getCompanyId();
			for (int i = 0; i < lane2CompanyIds.length; i++) {
				if (lane2CompanyIds[i] == companyId) {
					result.setLane2ApprovalStatus(lane2ApprovalStatuses[i].toUpperCase());
				}
			}
		}

		if (doc.containsKey(UserSearchableFields.SHARED_WORKER_ROLE.getName())) {
			result.setSharedWorkerRole((Boolean)doc.get(UserSearchableFields.SHARED_WORKER_ROLE.getName()));
		}
		if (doc.containsKey(UserSearchableFields.EMAIL_CONFIRMED.getName())) {
			result.setEmailConfirmed((Boolean)doc.get(UserSearchableFields.EMAIL_CONFIRMED.getName()));
		}
		if (doc.containsKey(UserSearchableFields.LANE3_APPROVAL_STATUS.getName())) {
			result.setLane3ApprovalStatus((Integer)doc.get(UserSearchableFields.LANE3_APPROVAL_STATUS.getName()));
		}
		if (doc.containsKey(UserSearchableFields.USER_STATUS_TYPE.getName())) {
			result.setUserStatusType((String) doc.get(UserSearchableFields.USER_STATUS_TYPE.getName()));
		}
	}

	private void parseFeedback(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.RATING.getName())) {
			RatingSummary rating = new RatingSummary()
					.setNumberOfRatings((Integer) doc.get(UserSearchableFields.RATING_COUNT.getName()))
					.setRating(((Integer) doc.get(UserSearchableFields.RATING.getName())).shortValue());
			result.setRating(rating);
		}

		if (doc.containsKey(UserBoostFields.ON_TIME_PERCENTAGE.getName())) {
			result.setOnTimePercentage((Double) doc.get(UserBoostFields.ON_TIME_PERCENTAGE.getName()));
		}

		if (doc.containsKey(UserBoostFields.DELIVERABLE_ON_TIME_PERCENTAGE.getName())) {
			result.setDeliverableOnTimePercentage((Double) doc.get(UserBoostFields.DELIVERABLE_ON_TIME_PERCENTAGE.getName()));
		}
	}

	private void parseRecruiting(SolrDocument doc, PeopleSearchResult result) {
		if (!doc.containsKey(UserSearchableFields.RECRUITING_CAMPAIGN_ID.getName())) {
			return;
		}

		Long recruitingCampaignId = (Long) doc.getFieldValue(UserSearchableFields.RECRUITING_CAMPAIGN_ID.getName());
		String recruitingCampaignName = (String) doc.getFieldValue(UserSearchableFields.RECRUITING_CAMPAIGN_NAME.getName());
		RecruitingCampaign campaign = new RecruitingCampaign().setId(recruitingCampaignId).setName(recruitingCampaignName);
		result.setRecruitingCampaign(campaign);
	}

	private void parseGroups(SolrDocument doc, PeopleSearchResult result, PeopleSearchTransientData data) {
		if (!doc.containsKey(UserSearchableFields.GROUP_IDS.getName())) {
			return;
		}
		addGroupIdsToHydrateData(doc, data);
		addCompanyGroupIdsToHydrateData(doc, result, data);
		result.setGroupIds(new ArrayList<>(data.getGroupIdsInResponse()));
	}

	private void addCompanyGroupIdsToHydrateData(SolrDocument doc, PeopleSearchResult result, PeopleSearchTransientData data) {
		Set<PeopleSearchTransientData.CompanyGroupId> companyGroupIds = parseCompanyGroupIds(doc);
		Map<Long, Set<PeopleSearchTransientData.CompanyGroupId>> userCompanyGroups = data.getUserGroupsInResponse();
		if (userCompanyGroups == null) {
			userCompanyGroups = Maps.newHashMapWithExpectedSize(20);
		}
		userCompanyGroups.put(result.getUserId(), companyGroupIds);
		data.setUserGroupsInResponse(userCompanyGroups);
	}

	private Set<PeopleSearchTransientData.CompanyGroupId> parseCompanyGroupIds(SolrDocument doc) {
		// we simply make a list of company group IDs in teh response
		Collection<Object> values = doc.getFieldValues(UserSearchableFields.COMPANY_GROUP_IDS.getName());
		// each value is COMPANYID_GROUPID
		Set<PeopleSearchTransientData.CompanyGroupId> cgi = Sets.newHashSetWithExpectedSize(values.size());
		for (Object value : values) {
			String[] companyGroupIdArr = ((String) value).split("_");
			Long companyId = Long.valueOf(companyGroupIdArr[0]);
			Long groupId = Long.valueOf(companyGroupIdArr[1]);
			PeopleSearchTransientData.CompanyGroupId companyGroupId = new PeopleSearchTransientData.CompanyGroupId();
			companyGroupId.setCompanyId(companyId);
			companyGroupId.setGroupId(groupId);
			cgi.add(companyGroupId);
		}
		return cgi;
	}

	private void addGroupIdsToHydrateData(SolrDocument doc, PeopleSearchTransientData data) {
		List<Long> responseGroupsList = extractGroupIdsFromDoc(doc);
		Set<Long> groupIdsInResponse = MoreObjects.firstNonNull(data.getGroupIdsInResponse(), new HashSet<Long>());
		groupIdsInResponse.addAll(responseGroupsList);
		data.setGroupIdsInResponse(groupIdsInResponse);
	}

	private void parseCompanyAssessments(SolrDocument doc, PeopleSearchTransientData data) {
		Map<Long, Set<Long>> responseCompanyAssessmentMap = extractCompanyAssessmentIdsFromDoc(doc, data);
		Map<Long, Set<Long>> assessmentIdsInResponse = MoreObjects.firstNonNull(data.getCompanyAssessmentIdsInResponse(), new HashMap<Long, Set<Long>>());
		if (responseCompanyAssessmentMap != null && !responseCompanyAssessmentMap.isEmpty())
			assessmentIdsInResponse.putAll(responseCompanyAssessmentMap);

		data.setCompanyAssessmentIdsInResponse(assessmentIdsInResponse);
	}

	private void parseCompanyUserTags(SolrDocument doc, PeopleSearchResult result, PeopleSearchTransientData data) {
		List<String> companyTags = extractCompanyUserTagsFromDoc(doc, data.getCurrentUser().getCompanyId());
		if (!CollectionUtils.isEmpty(companyTags)) result.addToCompanyTags(companyTags);
	}

	/**
	 * @param doc
	 * @return a list of long types which represent the group IDs from the user
	 */
	private List<Long> extractGroupIdsFromDoc(SolrDocument doc) {
		List<Long> responseGroups;
		Collection<Object> responseGroupsList;
		responseGroupsList = doc.getFieldValues(UserSearchableFields.GROUP_IDS.getName());
		if (responseGroupsList == null) {
			return null;
		}
		responseGroups = Lists.newArrayListWithCapacity(responseGroupsList.size());
		for (Object groupId : responseGroupsList) {
			responseGroups.add((Long) groupId);
		}
		return responseGroups;
	}

	private Map<Long, Set<Long>> extractCompanyAssessmentIdsFromDoc(SolrDocument doc, PeopleSearchTransientData data) {
		Collection<Object> response = doc.getFieldValues(UserSearchableFields.COMPANY_ASSESSMENT_IDS.getName());
		if (response == null) {
			return Maps.newHashMap();
		}
		Long userId = (Long) doc.get(UserSearchableFields.ID.getName());
		Long companyId = data.getCurrentUser().getCompanyId();
		Map<Long, Set<Long>> responseAssessments = Maps.newHashMapWithExpectedSize(1);
		Set<Long> assessments = Sets.newHashSetWithExpectedSize(response.size());

		for (Object assessmentId : response) {
			if (StringUtils.isNotBlank((String) assessmentId)) {
				String[] companyAssessmentId = ((String) assessmentId).split("_");
				if (Long.valueOf(companyAssessmentId[0]).equals(companyId)) {
					assessments.add(createLong(companyAssessmentId[1]));
				}
			}
		}
		responseAssessments.put(userId, assessments);
		return responseAssessments;
	}

	@SuppressWarnings("unchecked")
	private List<String> extractCompanyUserTagsFromDoc(SolrDocument doc, Long companyId) {
		Collection<Object> response = doc.getFieldValues(UserSearchableFields.COMPANY_USER_TAGS.getName());
		if (response == null) {
			return Collections.EMPTY_LIST;
		}
		List<String> tags = Lists.newArrayListWithExpectedSize(response.size());

		for (Object tagString : response) {
			if (StringUtils.isNotBlank((String) tagString)) {
				String[] companyTagArray = ((String) tagString).split("_");
				if (Long.valueOf(companyTagArray[0]).equals(companyId)) tags.add(companyTagArray[1]);
			}
		}
		return tags;
	}

	public void parseInsurances(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.INSURANCE_NAMES.getName())) {
			String[] insuranceNames = parseStringArrayFromSolr(UserSearchableFields.INSURANCE_NAMES.getName(), doc);
			for (String insuranceName : insuranceNames) {
				result.addToInsurances(insuranceName);
			}
		}

		if (doc.containsKey(UserSearchableFields.INSURANCE_IDS.getName())) {
			Set<Integer> ids = parseIntegerSetFromSolr(UserSearchableFields.INSURANCE_IDS.getName(), doc);
			if (isNotEmpty(ids)) {
				for (Integer id : ids) {
					result.addToInsuranceIds(id.longValue());
				}
			}
		}
	}

	@VisibleForTesting
	protected void parseCertifications(SolrDocument doc, PeopleSearchResult result) {
		if (!doc.containsKey(UserSearchableFields.CERTIFICATION_IDS.getName())) {
			return;
		}

		String[] certNames = parseStringArrayFromSolr(UserSearchableFields.CERTIFICATION_NAMES.getName(), doc);
		String[] certVendors = parseStringArrayFromSolr(UserSearchableFields.CERTIFICATION_VENDORS.getName(), doc);
		for (int i = 0; i < certNames.length; i++) {
			final String certificationDescription = getCertificationDescription(certNames, certVendors, i);
			result.addToCertifications(certificationDescription);
		}

		Set<Long> ids = parseLongSetFromSolr(UserSearchableFields.CERTIFICATION_IDS.getName(), doc);
		if (isNotEmpty(ids)) {
			for (Long id : ids) {
				result.addToCertificationIds(id);
			}
		}
	}

	private String getCertificationDescription(final String[] certNames, final String[] certVendors, final int i) {
		if (i < certVendors.length) {
			return certVendors[i] + " - " + certNames[i];
		} else {
			return certNames[i];
		}
	}

	private void parseIndustries(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.INDUSTRIES_ID.getName())) {
			Set<Long> ids = parseLongSetFromSolr(UserSearchableFields.INDUSTRIES_ID.getName(), doc);
			if (isNotEmpty(ids)) {
				for (Long id : ids) {
					result.addToIndustryIds(id);
				}
			}
		}
	}

	private void parseCountry(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.COUNTRY.getName())) {
			result.setCountry((String) doc.getFieldValue(UserSearchableFields.COUNTRY.getName()));
		}
	}

	private void parsePaidCompanyCounts(SolrDocument doc, PeopleSearchResult result) {
		for (String field : doc.getFieldNames()) {
			if (field.startsWith(UserSearchableFields.PAID_COMPANY_COUNT.getName())) {
				String[] tokens = field.split("_");
				result.addToCompanyPaidCounts(Long.valueOf(tokens[1]), (Integer) doc.getFieldValue(field));
			}
		}
	}

	private void parseLicenses(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.LICENSE_IDS.getName())) {

			String[] licenseNames = parseStringArrayFromSolr(UserSearchableFields.LICENSE_NAMES.getName(), doc);
			String[] licenseStates = parseStringArrayFromSolr(UserSearchableFields.LICENSE_STATES.getName(), doc);
			Set<Integer> ids = parseIntegerSetFromSolr(UserSearchableFields.LICENSE_IDS.getName(), doc);

			for (int i = 0; i < licenseNames.length; i++) {
				result.addToLicenses(licenseStates[i] + " - " + licenseNames[i]);
			}

			if (isNotEmpty(ids)) {
				for (Integer id : ids) {
					result.addToLicenseIds(id.longValue());
				}
			}
		}
	}

	private void parseGroupStatus(SolrDocument doc, PeopleSearchTransientData data, PeopleSearchResult result) {
		if (data.isGroupMemberSearch()) {
			long groupId = data.getMemberOfGroupId();

			Set<Long> m = parseLongSetFromSolr(UserSearchableFields.MEMBER_GROUP_IDS.getName(), doc);
			if (m.contains(groupId)) {
				result.setGroupMemberStatus(GroupMemberRequestType.MEMBER.toString());
				return;
			}

			Set<Long> mo = parseLongSetFromSolr(UserSearchableFields.MEMBER_OVERRIDE_GROUP_IDS.getName(), doc);
			if (mo.contains(groupId)) {
				result.setGroupMemberStatus(GroupMemberRequestType.MEMBER_OVERRIDE.toString());
				return;
			}

			Set<Long> p = parseLongSetFromSolr(UserSearchableFields.PENDING_GROUP_IDS.getName(), doc);
			if (p.contains(groupId)) {
				result.setGroupMemberStatus(GroupMemberRequestType.PENDING.toString());
				return;
			}

			Set<Long> pf = parseLongSetFromSolr(UserSearchableFields.PENDING_OVERRIDE_GROUP_IDS.getName(), doc);
			if (pf.contains(groupId)) {
				result.setGroupMemberStatus(GroupMemberRequestType.PENDING_FAILED.toString());
				return;
			}

			Set<Long> i = parseLongSetFromSolr(UserSearchableFields.INVITED_GROUP_IDS.getName(), doc);
			if (i.contains(groupId)) {
				result.setGroupMemberStatus(GroupMemberRequestType.INVITED.toString());
				return;
			}

			Set<Long> d = parseLongSetFromSolr(UserSearchableFields.DECLINED_GROUP_IDS.getName(), doc);
			if (d.contains(groupId)) {
				result.setGroupMemberStatus(GroupMemberRequestType.DECLINED.toString());
			}
		}
	}

	private void parseAssessmentStatus(SolrDocument doc, PeopleSearchTransientData data, PeopleSearchResult result) {
		if (data.isAssessmentInviteSearch()) {
			long assessmentId = data.getInviteToAssessmentId();

			Set<Long> i = parseStringSetToLongSetFromSolr(UserSearchableFields.INVITED_ASSESSMENT_IDS.getName(), doc);
			if (i.contains(assessmentId)) {
				result.setAssessmentStatus(AssessmentDerivedStatusType.INVITED.toString());
			}
		}
	}

	private void parseMbo(SolrDocument doc, PeopleSearchResult result) {
		if (doc.containsKey(UserSearchableFields.MBO.getName())) {
			result.setMbo((boolean)doc.get(UserSearchableFields.MBO.getName()));
		} else {
			result.setMbo(false);
		}

		if (doc.containsKey(UserSearchableFields.MBO_STATUS.getName())) {
			result.setMboStatus((String)doc.get(UserSearchableFields.MBO_STATUS.getName()));
		} else {
			result.setMboStatus(null);
		}
	}

}
