package com.workmarket.service.search;

import ch.lambdaj.function.convert.PropertyExtractor;
import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.groups.model.UserGroupHydrateData;
import com.workmarket.domains.model.company.CompanyHydrateData;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.rating.AverageRating;
import com.workmarket.search.cache.HydratorCache;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.request.user.CompanyType;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.FacetResult;
import com.workmarket.search.response.user.PeopleFacetResultType;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.thrift.core.RatingSummary;
import com.workmarket.utility.GeoUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import static ch.lambdaj.Lambda.convert;
import static java.lang.String.valueOf;
import static java.util.Collections.emptySet;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.ArrayUtils.contains;
import static org.apache.commons.lang.math.NumberUtils.createLong;

@Service
public abstract class SearchResultHydratorImpl {

	private static final BeanComparator comparator = new BeanComparator("facetCount", new ReverseComparator(new ComparableComparator()));

	private static final PeopleFacetResultType[] sortableFacets = {
		PeopleFacetResultType.ASSESSMENT,
		PeopleFacetResultType.CERTIFICATION,
		PeopleFacetResultType.GROUP,
		PeopleFacetResultType.INDUSTRY,
		PeopleFacetResultType.INSURANCE,
		PeopleFacetResultType.LICENSE,
		PeopleFacetResultType.COUNTRY
	};

	public static final String INVALID_LANE_NUMBER = "Invalid Lane Number";

	@Autowired protected HydratorCache hydratorCache;
	protected static final String FIELD_SEPARATOR = "_";

	@Async
	protected void hydrateDistances(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		if (!response.isSetResults() || hydrateData.getGeopoint() == null) {
			return;
		}
		double userLatitude = hydrateData.getGeopoint().getLatitude();
		double userLongitude = hydrateData.getGeopoint().getLongitude();

		if (userLatitude == 0.0 || userLongitude == 0.0) {
			return;
		}

		for (PeopleSearchResult result : response.getResults()) {
			if (!result.isSetLocationPoint() || (result.getLocationPoint().getLatitude() == 0.0 && result.getLocationPoint().getLongitude() == 0.0)) {
				continue;
			}
			double unRoundedDistance = GeoUtilities.distanceInMiles(userLatitude, userLongitude, result.getLocationPoint().getLatitude(),
					result.getLocationPoint().getLongitude());
			result.setDistance(MathUtils.round(unRoundedDistance, 1));
		}
	}

	@SuppressWarnings("unchecked")
	@Async
	protected void hydrateContextualWorkCounts(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		if (!response.isSetResults()) {
			return;
		}

		Map<Long, Integer> workCompletedForCompany = hydratorCache.getCompletedWorkCountToHydrateSearchData(
			Sets.newHashSet(convert(response.getResults(), new PropertyExtractor("userId"))), hydrateData.getCurrentUser().getCompanyId()
		);

		if (!workCompletedForCompany.isEmpty()) {
			for (PeopleSearchResult result : response.getResults()) {
				result.setWorkCompletedForSearchCompanyCount(MapUtils.getInteger(workCompletedForCompany, result.getUserId(), 0));
			}
		}
	}

	@Async
	protected void addGroupNamesToResults(PeopleSearchResponse response, PeopleSearchTransientData hydrateData, Map<Long, UserGroupHydrateData> userGroupHydrateDataMap) {
		if (!response.isSetResults()) {
			return;
		}
		if (MapUtils.isEmpty(hydrateData.getUserGroupsInResponse()) || isEmpty(hydrateData.getGroupIdsInResponse())) {
			return;
		}

		// fill in the results
		for (PeopleSearchResult result : response.getResults()) {
			Collection<Long> responseUserGroupsToShow = hydrateData.findCompanyViewableByUserCompany(result.getUserId());
			if (responseUserGroupsToShow == null) {
				continue;
			}

			for (Long responseUserGroupId : responseUserGroupsToShow) {
				UserGroupHydrateData data = userGroupHydrateDataMap.get(responseUserGroupId);
				if (data != null) {
					result.addToGroups(data.getGroupName());
				}
			}
		}
	}

	@Async
	protected void addCompanyAssessmentNamesToResults(PeopleSearchResponse response, PeopleSearchTransientData hydrateData, Map<Long, String> assessments) {
		if (!response.isSetResults() || MapUtils.isEmpty(hydrateData.getCompanyAssessmentIdsInResponse())) {
			return;
		}
		Set<Long> assessmentIds = Sets.newHashSetWithExpectedSize(hydrateData.getCompanyAssessmentIdsInResponse().size());
		for (Map.Entry<Long, Set<Long>> entry : hydrateData.getCompanyAssessmentIdsInResponse().entrySet()) {
			assessmentIds.addAll(entry.getValue());
		}

		// fill in the results
		for (PeopleSearchResult result : response.getResults()) {
			Collection<Long> assessmentsByUser = hydrateData.getCompanyAssessmentIdsInResponse().get(result.getUserId());
			if (assessmentsByUser == null) {
				continue;
			}

			for (Long responseAssessmentId : assessmentsByUser) {
				String name = assessments.get(responseAssessmentId);
				if (StringUtils.isNotBlank(name)) {
					result.addToCompanyAssessments(name);
				}

			}
		}
	}

	protected void sortFacets(PeopleSearchResponse response) {
		// we will go through all the facets, if the facet is sortable then we
		// will sort it and remove all 0 elements
		Map<Enum<PeopleFacetResultType>, List<FacetResult>> facetsInResult = response.getFacets();
		if (MapUtils.isEmpty(facetsInResult)) {
			return;
		}
		// iterate over each entry in the map and reset the list in the entry to
		// the sorted list
		for (Entry<Enum<PeopleFacetResultType>, List<FacetResult>> facetEntry : facetsInResult.entrySet()) {
			if (isSortableFacet(facetEntry.getKey())) {
				facetEntry.setValue(sortFacetList(facetEntry.getValue()));
			}

		}
	}

	@SuppressWarnings("unchecked")
	protected List<FacetResult> sortFacetList(List<FacetResult> value) {
		Collections.sort(value, comparator);
		return value;
	}

	protected boolean isSortableFacet(Enum key) {
		return contains(sortableFacets, key);
	}

	@Async
	protected void fixAssessmentFacetIds(PeopleSearchResponse response) {
		fixFacetIds(response, PeopleFacetResultType.ASSESSMENT);
	}

	@Async
	protected void fixGroupFacetIds(PeopleSearchResponse peopleSearchResponse) {
		fixFacetIds(peopleSearchResponse, PeopleFacetResultType.GROUP);
	}

	protected void fixFacetIds(PeopleSearchResponse peopleSearchResponse, PeopleFacetResultType facetType) {
		if (!peopleSearchResponse.isSetFacets()) {
			return;
		}
		if (peopleSearchResponse.getFacets().containsKey(facetType)) {
			Collection<FacetResult> facets = peopleSearchResponse.getFacets().get(facetType);
			// groups are returned with owningEntity_groupId.. we only need the group id
			for (FacetResult facet : facets) {
				facet.setFacetId(StringUtils.substringAfter(facet.getFacetId(), FIELD_SEPARATOR));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Async
	protected void hydrateRatings(PeopleSearchResponse response, SearchUser currentUser) {
		if (!response.isSetResults() || isEmpty(response.getResults())) {
			return;
		}

		Map<Long, AverageRating> ratings = hydratorCache.findAverageForUsersByCompany(
			Lists.newArrayList(convert(response.getResults(), new PropertyExtractor("userId"))),
			currentUser.getCompanyId()
		);

		for (PeopleSearchResult result : response.getResults()) {
			AverageRating averageRating = (AverageRating) MapUtils.getObject(ratings, result.getUserId(), null);
			if (averageRating != null && averageRating.getAverage() != null && averageRating.getCount() != null) {
				result.setCompanyRating(new RatingSummary(averageRating.getAverage().shortValue(), averageRating.getCount().intValue()));
			}
		}
	}

	/**
	 * My apologies, there is certainly a better way to hydrate this but the
	 * logic works and it's not TOO crazy. -KER
	 *
	 * @param response
	 * @param hydrateData
	 */
	@Async
	protected void hydrateCompanyTypes(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		Collection<CompanyType> companyFiltersSelected = ((PeopleSearchRequest)hydrateData.getOriginalRequest()).getCompanyTypeFilter();
		FacetResult corporation = null;
		FacetResult soleProprietor = null;
		if (isFacetSet(response, PeopleFacetResultType.COMPANY_TYPE)) {
			for (FacetResult facetResult : response.getFacets().get(PeopleFacetResultType.COMPANY_TYPE)) {
				if (facetResult.getFacetId().equals("1")) {
					facetResult.setFacetName("Corporation");
					if (companyFiltersSelected != null && companyFiltersSelected.contains(CompanyType.Corporation)) {
						facetResult.setActive(true);
					}
					corporation = facetResult;
				} else if (facetResult.getFacetId().equals("2")) {
					facetResult.setFacetName("Sole Proprietor");
					if (companyFiltersSelected != null && companyFiltersSelected.contains(CompanyType.SoleProprietor)) {
						facetResult.setActive(true);
					}
					soleProprietor = facetResult;
				}
			}
		}
		if (soleProprietor == null) {
			soleProprietor = new FacetResult("Sole Proprietor", valueOf(CompanyType.SoleProprietor.getValue()), 0, false);
		}
		if (corporation == null) {
			corporation = new FacetResult("Corporation", valueOf(CompanyType.Corporation.getValue()), 0, false);
		}
		// hack way to preserve the order KER
		List<FacetResult> newCompanyTypes = Lists.newArrayListWithCapacity(2);
		newCompanyTypes.add(corporation);
		newCompanyTypes.add(soleProprietor);
		response.putToFacets(PeopleFacetResultType.COMPANY_TYPE, newCompanyTypes);
	}

	@Async
	protected void hydrateLanes(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		PeopleSearchRequest originalRequest = (PeopleSearchRequest)hydrateData.getOriginalRequest();
		if (!isFacetSet(response, PeopleFacetResultType.LANE)) {
			return;
		}
		for (FacetResult laneFacetResponse : response.getFacets().get(PeopleFacetResultType.LANE)) {

			String facetName = getFacetName(laneFacetResponse.getFacetId());
			laneFacetResponse.setFacetName(facetName);
			if (originalRequest.isSetLaneFilter()) {
				for (LaneType lane : originalRequest.getLaneFilter()) {
					if (laneFacetResponse.getFacetId().equals(String.valueOf(lane.getValue()))) {
						laneFacetResponse.setActive(true);
					}
				}

			}
		}
	}

	protected String getFacetName(String facetId) {
		LaneType lane = LaneType.findByValue(NumberUtils.createInteger(facetId));
		if (lane != null) {
			switch (lane) {
				case LANE_0:
					return "Internal Users";
				case LANE_1:
					return "Employees";
				case LANE_2:
					return "Contractors";
				case LANE_3:
					return "Third Parties";
				case LANE_4:
					return "Everyone Else";
				default:
					return "Invalid Lane Number";
			}
		}
		return "Invalid Lane Number";
	}

	@Async
	protected void hydrateInsurance(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		Set<Long> insuranceIdsInResponse = extractIdsFromResponseFacet(response, PeopleFacetResultType.INSURANCE);
		if (isNotEmpty(insuranceIdsInResponse)) {
			Map<Long, String> insuranceFromResponse = hydratorCache.findAllInsuranceNamesByInsuranceId(insuranceIdsInResponse);
			if (MapUtils.isNotEmpty(insuranceFromResponse)) {
				hydrateFacet(response, PeopleFacetResultType.INSURANCE, insuranceFromResponse);
				setActivesForFacetResult(response, ((PeopleSearchRequest) hydrateData.getOriginalRequest()).getInsuranceFilter(), PeopleFacetResultType.INSURANCE);
			}
		}
	}

	@Async
	protected Future<Map<Long, String>> hydrateAssessmentsFacets(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		Map<Long, String> assessments = Maps.newHashMap();
		Set<Long> assessmentIdsInResponse = extractIdsFromResponseFacet(response, PeopleFacetResultType.ASSESSMENT);
		if (isNotEmpty(assessmentIdsInResponse)) {
			assessments = hydratorCache.findAllAssessmentNamesToHydrateSearchData(assessmentIdsInResponse);
			if (MapUtils.isNotEmpty(assessments)) {
				hydrateFacet(response, PeopleFacetResultType.ASSESSMENT, assessments);
				setActivesForFacetResult(response, ((PeopleSearchRequest) hydrateData.getOriginalRequest()).getAssessmentFilter(), PeopleFacetResultType.ASSESSMENT);
				return new AsyncResult<>(assessments);
			}
		}
		return new AsyncResult<>(assessments);
	}

	@Async
	protected void hydrateCertificationData(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		Set<Long> certificationIdsInResponse = extractIdsFromResponseFacet(response, PeopleFacetResultType.CERTIFICATION);
		if (isNotEmpty(certificationIdsInResponse)) {
			Map<Long, String> certificationsFromResponse = hydratorCache.findAllCertificationNamesToHydrateSearchData(certificationIdsInResponse);
			if (MapUtils.isNotEmpty(certificationsFromResponse)) {
				hydrateFacet(response, PeopleFacetResultType.CERTIFICATION, certificationsFromResponse);
				setActivesForFacetResult(response, ((PeopleSearchRequest) hydrateData.getOriginalRequest()).getCertificationFilter(), PeopleFacetResultType.CERTIFICATION);
			}
		}
	}

	/**
	 * This will hydrate the industry facet and fill in the facet if it was
	 * chosen
	 *
	 * @param response
	 * @param hydrateData
	 */
	@SuppressWarnings("unchecked")
	@Async
	protected Future<Map<Long, String>> hydrateIndustryFacets(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		Map<Long, String> industryFacetNames = Maps.newHashMap();

		if (response.isSetFacets()) {
			List<FacetResult> facets = response.getFacets().get(PeopleFacetResultType.INDUSTRY);
			if (facets == null) {
				return new AsyncResult<>(industryFacetNames);

			}
			PeopleSearchRequest request = (PeopleSearchRequest) hydrateData.getOriginalRequest();
			final Set<Long> industriesInFilter = request.getIndustryFilter() != null ? Sets.newHashSet(request.getIndustryFilter()) : Collections.EMPTY_SET;

			Set<Long> facetIdsToGet = Sets.newHashSetWithExpectedSize(facets.size());
			for (FacetResult facet : facets) {
				Long facetId = Long.valueOf(facet.getFacetId());
				if (industriesInFilter.contains(facetId)) {
					facet.setActive(true);
				}
				facetIdsToGet.add(facetId);
			}
			industryFacetNames = hydratorCache.findAllIndustryNamesToHydrateSearchData(facetIdsToGet);
			for (FacetResult facet : facets) {
				String industryName = industryFacetNames.get(Long.valueOf(facet.getFacetId()));
				facet.setFacetName(industryName);
			}
		}
		return new AsyncResult<>(industryFacetNames);
	}

	@Async
	protected void hydrateIndustryData(PeopleSearchResponse response, PeopleSearchTransientData hydrateData, Map<Long, String> industryNames) {
		if (MapUtils.isEmpty(industryNames)) {
			return;
		}
		hydrateFacet(response, PeopleFacetResultType.INDUSTRY, industryNames);
		setActivesForFacetResult(response, ((PeopleSearchRequest) hydrateData.getOriginalRequest()).getIndustryFilter(), PeopleFacetResultType.INDUSTRY);
	}

	@Async
	protected Future<Map<Long, UserGroupHydrateData>> hydrateGroupFacetData(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		Set<Long> groupFilters = ((PeopleSearchRequest) hydrateData.getOriginalRequest()).getGroupFilter();
		return new AsyncResult<>(
				hydrateGroupFacetData(response, PeopleFacetResultType.GROUP, groupFilters));
	}

	@Async
	protected Future<Map<Long, UserGroupHydrateData>> hydrateSharedGroupFacetData(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		Set<Long> sharedGroupFilters = ((PeopleSearchRequest) hydrateData.getOriginalRequest()).getSharedGroupFilter();
		return new AsyncResult<>(
				hydrateGroupFacetData(response, PeopleFacetResultType.SHARED_GROUP, sharedGroupFilters));
	}

	protected Map<Long, UserGroupHydrateData> hydrateGroupFacetData(PeopleSearchResponse response, PeopleFacetResultType groupFacetResultType, Collection<Long> groupFilters) {
		Set<Long> groupIdsInResponse = extractIdsFromResponseFacet(response, groupFacetResultType);
		Map<Long, UserGroupHydrateData> namesFromResponse = Maps.newHashMap();

		if (isNotEmpty(groupIdsInResponse)) {
			namesFromResponse = hydratorCache.findAllGroupHydrateSearchData(groupIdsInResponse);
			if (MapUtils.isNotEmpty(namesFromResponse)) {
				Collection<FacetResult> facets = response.getFacets().get(groupFacetResultType);
				hydrateGroupFacet(namesFromResponse, facets);
				setActivesForFacetResult(response, groupFilters, groupFacetResultType);
			}
		}
		return namesFromResponse;
	}

	protected void setActivesForFacetResult(PeopleSearchResponse response, Collection<Long> filters, PeopleFacetResultType facetType) {
		if (isNotEmpty(filters) && response.getFacets().containsKey(facetType)) {
			// group filter is set - so we have to mark the facets that were selected
			for (FacetResult facet : response.getFacets().get(facetType)) {
				if (filters.contains(convertFacetIdStrToLong(facet))) {
					facet.setActive(true);
				}
			}
		}
	}

	@Async
	protected void hydrateGroupFacet(Map<Long, UserGroupHydrateData> namesFromResponse, Collection<FacetResult> facets) {
		for (FacetResult facet : facets) {
			Long groupId = convertFacetIdStrToLong(facet);
			UserGroupHydrateData data = namesFromResponse.get(groupId);
			if (data != null) {
				String groupName = data.getGroupName();
				facet.setFacetName(groupName);
			}
		}
	}

	@Async
	protected void hydrateCountryFacets(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		Collection<FacetResult> facets = response.getFacets().get(PeopleFacetResultType.COUNTRY);
		if (isEmpty(facets)) {
			return;
		}
		PeopleSearchRequest originalRequest = (PeopleSearchRequest) hydrateData.getOriginalRequest();
		Set<String> selected = originalRequest.isSetCountryFilter() ?
				originalRequest.getCountryFilter() :
				Collections.<String>emptySet();

		for (FacetResult facet : facets) {
			String countryName = StringUtilities.toPrettyName(Country.valueOf(facet.getFacetId()).getName());
			facet.setFacetName(countryName);
			facet.setActive(selected.contains(facet.getFacetId()));
		}
	}

	@Async
	protected void hydrateCompanyData(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		Collection<FacetResult> facets = response.getFacets().get(PeopleFacetResultType.COMPANY_ID);
		Set<Long> companyIdsInResponse;
		Map<Long, CompanyHydrateData> companyHydrateData = null;

		//facets
		if (isNotEmpty(facets)) {
			PeopleSearchRequest originalRequest = (PeopleSearchRequest) hydrateData.getOriginalRequest();
			Set<Long> selected = originalRequest.isSetCompanyFilter() ?
					originalRequest.getCompanyFilter() :
					Collections.<Long>emptySet();

			companyIdsInResponse = extractIdsFromResponseFacet(response, PeopleFacetResultType.COMPANY_ID);
			companyHydrateData = hydratorCache.findAllCompanyHydrateSearchData(companyIdsInResponse);
			for (FacetResult facet : facets) {
				CompanyHydrateData companyData = (CompanyHydrateData)MapUtils.getObject(companyHydrateData, Long.valueOf(facet.getFacetId()));
				if (companyData != null) {
					facet.setFacetName(companyData.getName());
				}
				facet.setActive(selected.contains(Long.valueOf(facet.getFacetId())));
			}
		}
		PeopleSearchRequest originalRequest = (PeopleSearchRequest) hydrateData.getOriginalRequest();
		if (originalRequest.isExportSearch()) {
			hydrateCompanyDataForExport(companyHydrateData, response.getResults());
		}
	}

	private void hydrateCompanyDataForExport(Map<Long, CompanyHydrateData> companyHydrateData, Collection<PeopleSearchResult> response) {
		if (MapUtils.isNotEmpty(companyHydrateData)) {
			Set<Long> misses = Sets.newHashSet();
			Set<PeopleSearchResult> missesPeopleSearchResult = Sets.newHashSet();
			for (PeopleSearchResult result : response) {
				CompanyHydrateData companyData = (CompanyHydrateData)MapUtils.getObject(companyHydrateData, result.getCompanyId());
				if (companyData == null) {
					misses.add(result.getCompanyId());
					missesPeopleSearchResult.add(result);
				} else {
					result.setCompanyName(companyData.getName());
					result.setCompanyStatusType(companyData.getCompanyStatusType());
					result.setApprovedTIN(companyData.isApprovedTIN());
					result.setConfirmedBankAccount(companyData.isConfirmedBankAccount());
				}
			}

			if (isNotEmpty(misses)) {
				hydrateCompanyDataForExport(hydratorCache.findAllCompanyHydrateSearchData(misses), missesPeopleSearchResult);
			}
		}
	}

	protected void hydrateFacet(PeopleSearchResponse response, PeopleFacetResultType facetType, Map<Long, String> namesFromResponse) {
		Collection<FacetResult> facets = response.getFacets().get(facetType);

		for (FacetResult facet : facets) {
			facet.setFacetName(namesFromResponse.get(convertFacetIdStrToLong(facet)));
		}
	}

	protected Set<Long> extractIdsFromResponseFacet(PeopleSearchResponse response, PeopleFacetResultType type) {
		if (!isFacetSet(response, type)) {
			return emptySet();
		}
		Collection<FacetResult> facets = response.getFacets().get(type);
		Set<Long> returnVal = new HashSet<>(facets.size());
		for (FacetResult facet : facets) {
			insertFacetIdToSet(returnVal, facet);
		}
		return returnVal;
	}

	protected void insertFacetIdToSet(Set<Long> returnVal, FacetResult facet) {
		long lId = convertFacetIdStrToLong(facet);
		returnVal.add(lId);
	}

	protected long convertFacetIdStrToLong(FacetResult facet) {
		final String id = facet.getFacetId();
		return createLong(id);
	}

	@Async
	protected void hydrateLicenseData(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		Set<Long> licenseIdsInResponse = getLicenseIdsFromResponse(response);
		if (isEmpty(licenseIdsInResponse)) {
			return;
		}

		Map<Long, License> licenseMap = hydratorCache.findAllLicenseNames(licenseIdsInResponse);
		if (MapUtils.isEmpty(licenseMap)) {
			return;
		}
		for (FacetResult facet : response.getFacets().get(PeopleFacetResultType.LICENSE)) {
			Long licenseId = Long.valueOf(StringUtils.substringAfter(facet.getFacetId(), FIELD_SEPARATOR));
			License license = licenseMap.get(licenseId);
			if (license != null) {
				facet.setFacetName(license.getState() + " - " + license.getName());
				facet.setActive(isActiveLicenseFacet(license, hydrateData));
			}
		}
	}

	protected boolean isActiveLicenseFacet(License license, PeopleSearchTransientData hydrateData) {
		// iterate over the license data
		PeopleSearchRequest originalRequest = (PeopleSearchRequest) hydrateData.getOriginalRequest();
		if (!originalRequest.isSetStateLicenseFilter()) {
			return false;
		} else if (originalRequest.getStateLicenseFilter().contains(license.getState() + '_' + license.getId())) {
			return true;
		}
		return false;
	}

	protected Set<Long> getLicenseIdsFromResponse(PeopleSearchResponse response) {
		if (!response.isSetFacets()) {
			return Collections.emptySet();
		}
		Collection<FacetResult> licenseFacets = response.getFacets().get(PeopleFacetResultType.LICENSE);
		if (isEmpty(licenseFacets)) {
			return Collections.emptySet();
		}
		Set<Long> returnVal = new HashSet<>(licenseFacets.size());
		for (FacetResult licenseFacet : licenseFacets) {
			// populate the license facets with the license names
			returnVal.add(createLong(StringUtils.substringAfter(licenseFacet.getFacetId(), FIELD_SEPARATOR)));
		}
		return returnVal;
	}

	@Async
	protected void shortenLane4LastNames(PeopleSearchResponse response) {
		if (!response.isSetResults()) {
			return;
		}
		for (PeopleSearchResult result : response.getResults()) {
			if (result.getLane().equals(LaneType.LANE_4)) {
				if (result.getName().isSetLastName()) {
					result.getName().setLastName(result.getName().getLastName().charAt(0) + ".");
				}
			}
		}
	}

	protected boolean isFacetSet(PeopleSearchResponse response, PeopleFacetResultType facetType) {
		return response.isSetFacets() && response.getFacets().containsKey(facetType);
	}

}
