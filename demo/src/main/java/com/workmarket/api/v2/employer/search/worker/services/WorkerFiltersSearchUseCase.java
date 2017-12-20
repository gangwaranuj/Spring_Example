package com.workmarket.api.v2.employer.search.worker.services;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.employer.search.common.model.LongFilter;
import com.workmarket.api.v2.employer.search.common.model.StringFilter;
import com.workmarket.api.v2.employer.search.common.services.SearchUseCase;
import com.workmarket.api.v2.employer.search.worker.model.LaneType;
import com.workmarket.api.v2.employer.search.worker.model.VerificationType;
import com.workmarket.api.v2.employer.search.worker.model.WorkerFilters;
import com.workmarket.api.v2.employer.search.worker.model.WorkerFiltersResponseDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRequestDTO;
import com.workmarket.data.solr.query.location.LocationQueryCreationService;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.model.company.CompanyHydrateData;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.search.cache.HydratorCache;
import com.workmarket.search.cache.StateLookupCache;

import com.workmarket.search.request.user.Verification;
import com.workmarket.search.worker.FindWorkerClient;
import com.workmarket.search.worker.FindWorkerSearchResponse;
import com.workmarket.search.worker.query.model.CompanyType;
import com.workmarket.search.worker.query.model.Counts;
import com.workmarket.search.worker.query.model.Facet;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.VendorService;

import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import rx.functions.Action1;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Use case for getting worker filter data (names and counts) for a given search
 * criteria.
 */
@Component
@Scope("prototype")
public class WorkerFiltersSearchUseCase extends SearchUseCase<WorkerFiltersSearchUseCase, WorkerFiltersResponseDTO> {
	private static final Logger logger = LoggerFactory.getLogger(WorkerDetailsUseCase.class);

	private static final String NULL_FACET_ID = "null";

	private final WorkService workService;
	private final VendorService vendorService;
	private final LocationQueryCreationService locationQueryCreationService;
	private final StateLookupCache stateLookupCache;
	private final FindWorkerClient findWorkerClient;
	private final HydratorCache hydratorCache;
	private final UserGroupService userGroupService;


	private final ExtendedUserDetails userDetails;
	private final WebRequestContextProvider webRequestContextProvider;
	private final WorkerSearchRequestDTO criteria;

	private BaseWorkerSearchCriteriaBuilder workerSearchCriteriaBuilder;
	private FindWorkerCriteria findWorkerCriteria;
	private FindWorkerSearchResponse findWorkerSearchResponse;
	private WorkerFiltersResponseDTO workerFiltersResponseDTO;

	/**
	 * Constructor.
	 * @param workService The work service used to retrieve our work
	 * @param vendorService The vendor service
	 * @param locationQueryCreationService The location service
	 * @param stateLookupCache The state cache
	 * @param findWorkerClient The client for our worker-search-service
	 * @oaram hydratorCache The holder for our mapping from facet id's to facet text
	 * @param userGroupService The group service
	 * @param extendedUserDetails The user details making the request
	 * @param requestContext The request context
	 * @param criteria The incoming criteria
	 */
	public WorkerFiltersSearchUseCase(final WorkService workService,
	                                  final VendorService vendorService,
	                                  final LocationQueryCreationService locationQueryCreationService,
	                                  final StateLookupCache stateLookupCache,
	                                  final FindWorkerClient findWorkerClient,
	                                  final HydratorCache hydratorCache,
	                                  final UserGroupService userGroupService,
	                                  final ExtendedUserDetails extendedUserDetails,
	                                  final WebRequestContextProvider requestContext,
	                                  final WorkerSearchRequestDTO criteria) {
		this.workService = workService;
		this.vendorService = vendorService;
		this.locationQueryCreationService = locationQueryCreationService;
		this.stateLookupCache = stateLookupCache;
		this.findWorkerClient = findWorkerClient;
		this.hydratorCache = hydratorCache;
		this.userGroupService = userGroupService;
		this.userDetails = extendedUserDetails;
		this.webRequestContextProvider = requestContext;
		this.criteria = criteria;
	}

	@Override
	public WorkerFiltersResponseDTO andReturn() throws Exception {
		return workerFiltersResponseDTO;
	}

	@Override
	protected WorkerFiltersSearchUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(criteria);
	}

	@Override
	protected void init() {
		workerSearchCriteriaBuilder = new WorkerFiltersCriteriaBuilder(workService, vendorService,
			locationQueryCreationService, stateLookupCache, userDetails, criteria);
	}

	@Override
	protected void prepare() {
		findWorkerCriteria = workerSearchCriteriaBuilder.build();
	}

	@Override
	protected void process() {
		final MutableObject<Throwable> errorCondition = new MutableObject<>();

		// fixing offset and limit to 0 since this is just getting facets
		findWorkerClient.findWorkers(findWorkerCriteria, 0, 0, webRequestContextProvider.getRequestContext())
			.subscribe(
				new Action1<FindWorkerSearchResponse>() {
					@Override
					public void call(FindWorkerSearchResponse searchResponse) {
						findWorkerSearchResponse = searchResponse;
					}
				},
				new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						errorCondition.setValue(throwable);
					}
				});

		if (errorCondition.getValue() != null) {
			throw new RuntimeException(errorCondition.getValue());
		}
	}

	@Override
	protected void finish() {
		final WorkerFiltersResponseDTO.Builder responseBuilder = new WorkerFiltersResponseDTO.Builder();

		responseBuilder.setResultCount(findWorkerSearchResponse.getTotalResults());
		responseBuilder.setQueryTimeMillis(findWorkerSearchResponse.getQueryTimeMillis());

		final WorkerFilters.Builder workerFiltersBuilder = new WorkerFilters.Builder();

		workerFiltersBuilder.addAvatars(mapAvatarFacet());
		workerFiltersBuilder.addAssessments(mapAssessmentFacet());
		workerFiltersBuilder.addCertifications(mapCertificationFacet());
		workerFiltersBuilder.addLicenses(mapLicenseFacet());
		workerFiltersBuilder.addGroups(mapGroupFacet());
		workerFiltersBuilder.addSharedGroups(mapSharedGroupFacet());
		workerFiltersBuilder.addCompanies(mapCompanyFacet());
		workerFiltersBuilder.addIndustries(mapIndustryFacet());
		workerFiltersBuilder.addCompanyTypes(mapCompanyTypeFacet());
		workerFiltersBuilder.addCountries(mapCountryFacet());
		workerFiltersBuilder.addLanes(mapLaneFacet());
		workerFiltersBuilder.addVerifications(mapVerificationFacet());

		responseBuilder.setFilters(workerFiltersBuilder.build());
		workerFiltersResponseDTO = responseBuilder.build();
	}

	private List<StringFilter> mapVerificationFacet() {
		List<StringFilter> filters = Lists.newArrayList();

		filters.add(new StringFilter.Builder()
			.setId(VerificationType.background_check_last_6months.name())
			.setName("Background Check in last 6 months")
			.setCount(findWorkerSearchResponse.getResults().getCounts().getLastBackgroundCheckDate())
			.build());

		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getVerificationIds();

		if (facetDetails != null) {
			for (Map.Entry<String, Long> facet : facetDetails.getData().entrySet()) {
				if (StringUtils.equals(Integer.toString(Verification.BACKGROUND_CHECK.getValue()), facet.getKey())) {
					filters.add(new StringFilter.Builder()
						.setId(VerificationType.background_check.name())
						.setName("Background Check")
						.setCount(facet.getValue())
						.build());
				} else if (StringUtils.equals(Integer.toString(Verification.DRUG_TEST.getValue()), facet.getKey())) {
					filters.add(new StringFilter.Builder()
						.setId(VerificationType.drug_test.name())
						.setName("Drug Test")
						.setCount(facet.getValue())
						.build());
				}
			}
		}

		return filters;
	}

	private List<StringFilter> mapCountryFacet() {
		List<StringFilter> filters = Lists.newArrayList();

		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getCountry();

		if (facetDetails != null) {
			for (Map.Entry<String, Long> facet : facetDetails.getData().entrySet()) {
				if (!org.apache.commons.lang.StringUtils.equals(facet.getKey(), "null") && NumberUtils.isDigits(facet.getKey())) {
					Country country = "null".equals(facet.getKey()) ? null : Country.valueOf(facet.getKey());
					String countryName = country == null ? facet.getKey() : country.getName();

					filters.add(new StringFilter.Builder()
						.setId(facet.getKey())
						.setName(countryName)
						.setCount(facet.getValue())
						.build());
				}
			}
		}

		return filters;
	}

	private List<StringFilter> mapCompanyTypeFacet() {
		List<StringFilter> filters = Lists.newArrayList();

		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getCompanyType();

		if (facetDetails != null) {
			for (Map.Entry<String, Long> facet : facetDetails.getData().entrySet()) {
				if (!org.apache.commons.lang.StringUtils.equals(facet.getKey(), "null") && NumberUtils.isDigits(facet.getKey())) {
					com.workmarket.search.request.user.CompanyType companyType =
						com.workmarket.search.request.user.CompanyType.findByValue(Integer.valueOf(facet.getKey()).intValue());

					switch (companyType) {
						case Corporation:
							filters.add(new StringFilter.Builder()
								.setId(CompanyType.corp.name())
								.setName(companyType.getDescription())
								.setCount(facet.getValue())
								.build());
							break;
						case SoleProprietor:
							filters.add(new StringFilter.Builder()
								.setId(CompanyType.individual.name())
								.setName(companyType.getDescription())
								.setCount(facet.getValue())
								.build());
							break;
					}
				}
			}
		}

		return filters;
	}

	private List<LongFilter> mapIndustryFacet() {
		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getIndustryIds();

		Map<Long, String> industryNames = hydratorCache.findAllIndustryNamesToHydrateSearchData(getFacetIdsFromFacet(facetDetails));

		return mapLongFacet(facetDetails, industryNames);
	}

	private List<LongFilter> mapCompanyFacet() {
		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getCompanyId();

		Map<Long, CompanyHydrateData> companyData = hydratorCache.findAllCompanyHydrateSearchData(getFacetIdsFromFacet(facetDetails));

		Map<Long, String> companyNames = Maps.newHashMap();
		for (CompanyHydrateData companyHydrateData : companyData.values()) {
			companyNames.put(companyHydrateData.getId(), companyHydrateData.getName());
		}

		return mapLongFacet(facetDetails, companyNames);
	}


	private List<LongFilter> mapSharedGroupFacet() {
		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getSharedGroupIds();

		ManagedCompanyUserGroupRowPagination pagination =
				userGroupService.findGroupsActiveOpenMembershipByGroupIds(getFacetIdsFromFacet(facetDetails), new ManagedCompanyUserGroupRowPagination());

		// map out our group details
		Map<Long, String> groupNames = Maps.newHashMap();
		for (final ManagedCompanyUserGroupRow r : pagination.getResults()) {
			if (!r.getCompanyId().equals(userDetails.getCompanyId())) {
				groupNames.put(r.getGroupId(), r.getName());
			}
		}

		return mapLongFacet(facetDetails, groupNames);
	}

	private List<LongFilter> mapGroupFacet() {
		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getCompanyGroupIds();

		ManagedCompanyUserGroupRowPagination pagination =
				userGroupService.findCompanyGroupsActiveOpenMembership(userDetails.getCompanyId(), new ManagedCompanyUserGroupRowPagination());

		// map out our membership status
		Map<Long, String> groupNames = Maps.newHashMap();
		for (final ManagedCompanyUserGroupRow r : pagination.getResults()) {
			String groupName = org.apache.commons.lang.StringUtils.isEmpty(r.getName()) ? r.getGroupId().toString() : r.getName();
			groupNames.put(r.getGroupId(), groupName);
		}

		return mapLongFacet(facetDetails, groupNames);

	}

	private List<LongFilter> mapLicenseFacet() {
		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getLicenseIds();

		Map<Long, License> licenses = hydratorCache.findAllLicenseNames(getFacetIdsFromFacet(facetDetails));

		Map<Long, String> licenseNames = Maps.newHashMap();
		for (License license : licenses.values()) {
			licenseNames.put(license.getId(), license.getName());
		}
		return mapLongFacet(facetDetails, licenseNames);
	}

	private List<LongFilter> mapCertificationFacet() {
		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getCertificationIds();

		Map<Long, String> certificationNames = hydratorCache.findAllCertificationNamesToHydrateSearchData(getFacetIdsFromFacet(facetDetails));

		return mapLongFacet(facetDetails, certificationNames);
	}

	private List<LongFilter> mapAssessmentFacet() {
		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getCompanyAssessmentIds();

		Map<Long, String> assessmentNames = hydratorCache.findAllAssessmentNamesToHydrateSearchData(getFacetIdsFromFacet(facetDetails));

		return mapLongFacet(facetDetails, assessmentNames);
	}

	private List<StringFilter> mapLaneFacet() {
		final List<StringFilter> filters = Lists.newArrayList();

		final Counts counts = findWorkerSearchResponse.getResults().getCounts();

		filters.add(new StringFilter.Builder()
			.setId(LaneType.lane0.name())
			.setName("Internal Users")
			.setCount(counts.getLane0())
			.build());

		filters.add(new StringFilter.Builder()
			.setId(LaneType.lane1.name())
			.setName("Employees")
			.setCount(counts.getLane1())
			.build());

		filters.add(new StringFilter.Builder()
			.setId(LaneType.lane2.name())
			.setName("Contractors")
			.setCount(counts.getLane2())
			.build());

		filters.add(new StringFilter.Builder()
			.setId(LaneType.lane3.name())
			.setName("Third Parties")
			.setCount(counts.getLane3())
			.build());

		long totalLanes = counts.getLane0() + counts.getLane1() + counts.getLane2() + counts.getLane3();
		filters.add(new StringFilter.Builder()
			.setId(LaneType.lane4.name())
			.setName("Everyone Else")
			.setCount(findWorkerSearchResponse.getTotalResults() - totalLanes)
			.build());

		return filters;
	}

	private List<LongFilter> mapLongFacet(final Facet facetDetails, final Map<Long, String> facetNames) {
		final List<LongFilter> filters = Lists.newArrayList();

		if (facetDetails != null) {
			for (Map.Entry<String, Long> facet : facetDetails.getData().entrySet()) {
				if (!StringUtils.equals(NULL_FACET_ID, facet.getKey())) {
					Long id = Long.valueOf(facet.getKey());
					filters.add(new LongFilter.Builder()
						.setName(facetNames.get(id))
						.setId(Long.valueOf(facet.getKey()))
						.setCount(facet.getValue()).build());
				}
			}
		}

		return filters;
	}

	private List<StringFilter> mapAvatarFacet() {
		final Facet facetDetails = findWorkerSearchResponse.getResults().getFacets().getHasAvatar();
		return mapStringFacet(facetDetails);
	}

	private List<StringFilter> mapStringFacet(final Facet facetDetails) {
		final List<StringFilter> filters = Lists.newArrayList();

		if (facetDetails != null) {
			for (Map.Entry<String, Long> facet : facetDetails.getData().entrySet()) {
				filters.add(new StringFilter.Builder()
					.setName(facet.getKey())
					.setId(facet.getKey())
					.setCount(facet.getValue()).build());
			}
		}

		return filters;
	}

	private Set<Long> getFacetIdsFromFacet(final Facet facet) {
		Set<Long> results = Sets.newHashSet();

		if (facet != null && facet.getData() != null) {
			for (String s : facet.getData().keySet()) {
				if (NumberUtils.isDigits(s)) {
					results.add(Long.valueOf(s));
				} else {
					if (!StringUtils.equals(NULL_FACET_ID, s)) {
						logger.warn("Failed to process facet - facet key: " + s + " was not a number");
					}
				}
			}
		}
		return results;
	}


	@Override
	protected WorkerFiltersSearchUseCase handleExceptions() throws Exception {
		if (exception != null) {
			logger.error("Failed executing " + this.getClass().getSimpleName() +
				" for Request Id: " + webRequestContextProvider.getWebRequestContext().getRequestId(), exception);
			throw exception;
		}
		return this;
	}
}
