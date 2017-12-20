package com.workmarket.domains.search.worker;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.model.company.CompanyHydrateData;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.velvetrope.guest.WebGuest;
import com.workmarket.domains.velvetrope.rope.MarketplaceFilterHidingRope;
import com.workmarket.search.cache.HydratorCache;
import com.workmarket.search.request.user.CompanyType;
import com.workmarket.search.request.user.Verification;
import com.workmarket.search.worker.FindWorkerSearchResponse;
import com.workmarket.search.worker.query.model.Counts;
import com.workmarket.search.worker.query.model.Facet;
import com.workmarket.search.worker.query.model.Facets;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.OrgUnitsCriteria;
import com.workmarket.search.worker.query.model.ScreeningCriteria;
import com.workmarket.search.worker.query.model.WorkerRelation;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.velvetrope.Doorman;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class WorkerFacetHydrator {
	private static final Logger logger = LoggerFactory.getLogger(WorkerFacetHydrator.class);

	private static final String METRIC_ROOT = "WorkerSearch";
	private static final String STATE_LICENSE_SEPARATOR = "_";
	private static final boolean RETURN_ALL_GROUP_PAGINATION_ROWS = true;

	@Autowired private HydratorCache hydratorCache;
	@Autowired private UserGroupService groupService;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private OrgStructureService orgStructureService;
	@Autowired private FeatureEntitlementService featureEntitlementService;

    @Qualifier("marketplaceFilterHidingDoorman")
    @Autowired private Doorman doorman;private WMMetricRegistryFacade wmMetricRegistryFacade;
    private Timer hydrationTimer;

	@PostConstruct
	public void init() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, METRIC_ROOT);
		hydrationTimer = wmMetricRegistryFacade.timer("workerFacetHydration");
	}

	/**
	 * Maps our facets from a call to find workers.
	 *
	 * @param findWorkerResponse
	 * @param criteria
	 * @return
	 */
	public Map<String, Object> hydrateFacets(final FindWorkerSearchResponse findWorkerResponse, final FindWorkerCriteria criteria,
											 final ExtendedUserDetails currentUser) {

		Timer.Context timerContext = hydrationTimer.time();

		try {
			Map<String, Object> filters = Maps.newHashMap();
			Map<String, Map<String, String>> labels = Maps.newHashMap();
			filters.put("labels", labels);

			Facets facets = findWorkerResponse.getResults().getFacets();
			Counts counts = findWorkerResponse.getResults().getCounts();

			List<String> sharedGroups = null;
			List<String> privateGroups = null;
			if (criteria.getGroupCriteria() != null) {
				sharedGroups = criteria.getGroupCriteria().getSharedTalentPools();
				privateGroups = criteria.getGroupCriteria().getPrivateTalentPools();
			}
			mapSharedGroupFacet("sharedgroups", facets.getSharedGroupIds(), filters, labels, sharedGroups, currentUser);
			mapPrivateGroups("groups", filters, labels, privateGroups, currentUser);

			mapCompanyFacet("companies", facets.getCompanyId(), filters, labels, Lists.newArrayList(criteria.getRequestingCompanyId()),
				hydratorCache.findAllCompanyHydrateSearchData(getFacetIds(facets.getCompanyId(), currentUser)));

			List<String> assessments = null;
			if (criteria.getQualificationsCriteria() != null) {
				assessments = criteria.getQualificationsCriteria().getTests();
			}
			mapFacet("assessments", facets.getCompanyAssessmentIds(), filters, labels, assessments,
				hydratorCache.findAllAssessmentNamesToHydrateSearchData(getFacetIds(facets.getCompanyAssessmentIds(), currentUser)),
				currentUser);

			List<String> licenses = null;
			if (criteria.getQualificationsCriteria() != null) {
				licenses = criteria.getQualificationsCriteria().getLicenses();
			}
			mapLicenseFacet("licenses", facets.getStateLicenseIds(), filters, labels, licenses,
				hydratorCache.findAllLicenseNames(getStateLicenseIds(facets.getStateLicenseIds(), currentUser)));

			List<String> certifications = null;
			if (criteria.getQualificationsCriteria() != null) {
				certifications = criteria.getQualificationsCriteria().getCertifications();
			}
			mapFacet("certifications", facets.getCertificationIds(), filters, labels, certifications,
				hydratorCache.findAllCertificationNamesToHydrateSearchData(getFacetIds(facets.getCertificationIds(), currentUser)),
				currentUser);

			mapAvatarFacet("avatar", facets.getHasAvatar(), filters, labels, null);

			List<String> countries = null;
			if (criteria.getLocationCriteria() != null) {
				countries = criteria.getLocationCriteria().getCountries();
			}
			mapCountriesFacet("countries", facets.getCountry(), filters, labels, countries);

			mapVerificationFacet(
				"verifications",
				facets.getVerificationIds(),
				counts.getLastBackgroundCheckDate(),
				counts.getLastPassedDrugTestDate(),
				filters, labels, criteria);

			List<String> industries = null;
			ImmutableList<com.workmarket.search.worker.query.model.CompanyType> companyTypes = null;
			if (criteria.getCompanyCriteria() != null) {
				industries = criteria.getCompanyCriteria().getIndustries();
				companyTypes = criteria.getCompanyCriteria().getCompanyTypes();
			}
			mapFacet("industries", facets.getIndustryIds(), filters, labels, industries,
				hydratorCache.findAllIndustryNamesToHydrateSearchData(getFacetIds(facets.getIndustryIds(), currentUser)),
				currentUser);

			final boolean hasOrgFeatureToggle = featureEntitlementService.hasFeatureToggle(currentUser.getId(), "org_structures");

			if (hasOrgFeatureToggle) {
				mapOrgUnits("orgUnits", filters, labels, criteria.getOrgUnitsCriteria(), currentUser);
			}

			//mapFacet("ratings", facets.getI, filters, labels);

			mapCompanyTypeFacet("companytypes", facets.getCompanyType(), filters, labels, companyTypes);

			// map our facet query
			mapFacetQuery("lanes", findWorkerResponse, filters, labels, criteria, currentUser);

			return filters;
		} finally {
			long time = timerContext.stop();

			logger.info("Worker facet hydration took " + TimeUnit.NANOSECONDS.toMillis(time) + "ms");
		}

	}

	private Set<Long> getFacetIds(final Facet facet, final ExtendedUserDetails currentUser) {
		Set<Long> results = Sets.newHashSet();

		if (facet != null && facet.getData() != null) {
			for (String s : facet.getData().keySet()) {
				if (NumberUtils.isDigits(s)) {
					results.add(Long.valueOf(s));
				} else {
					if (!StringUtils.equals("null", s)) {
						logger.warn("Failed to process facet - facet key: " + s + " was not a number");
					}
				}
			}
		}
		return results;
	}

	/**
	 * Extracts license ids from stateLicense facet.
	 * StateLicense is formatted as "NY_1334". We need to split the string to extract long value.
	 *
	 * @param facet       StateLicense facet
	 * @param currentUser current user
	 * @return
	 */
	private Set<Long> getStateLicenseIds(final Facet facet, final ExtendedUserDetails currentUser) {
		final Set<Long> results = Sets.newHashSet();

		if (facet != null && facet.getData() != null) {
			for (final String stateLicense : facet.getData().keySet()) {
				if (StringUtils.equals("null", stateLicense)) {
					logger.info("Ignore null facet");
				} else {
					results.add(NumberUtils.createLong(StringUtils.substringAfter(stateLicense, STATE_LICENSE_SEPARATOR)));
				}
			}
		}
		return results;
	}

	private void mapFacetQuery(final String name, final FindWorkerSearchResponse findWorkerResponse,
							   final Map<String, Object> filters, final Map<String, Map<String, String>> labels,
							   final FindWorkerCriteria criteria, ExtendedUserDetails currentUser
) {
		Counts counts = findWorkerResponse.getResults().getCounts();

		// handle the lanes
		List<Map<String, Object>> facetDetails = Lists.newArrayList();
		Map<String, String> facetLabels = Maps.newHashMap();
		filters.put(name, facetDetails);
		labels.put(name, facetLabels);

		ImmutableList<WorkerRelation> workerRelations =
			(criteria.getProfileCriteria() == null) ? null : criteria.getProfileCriteria().getWorkerRelations();



		facetDetails.add(CollectionUtilities.newObjectMap("id", "1", "name", "Employees", "count", counts.getLane1(),
			"filter_on", workerRelations != null && workerRelations.contains(WorkerRelation.employees)));
		facetLabels.put("1", "Employees");

		facetDetails.add(CollectionUtilities.newObjectMap("id", "2", "name", "Contractors", "count", counts.getLane2(),
			"filter_on", workerRelations != null && workerRelations.contains(WorkerRelation.contractors)));
		facetLabels.put("2", "Contractors");

        MutableBoolean shouldHideMarketplaceFilters = new MutableBoolean(false);
        doorman.welcome(new WebGuest(currentUser), new MarketplaceFilterHidingRope(shouldHideMarketplaceFilters));

        if (shouldHideMarketplaceFilters.isFalse()) {
            facetDetails.add(CollectionUtilities.newObjectMap("id", "0", "name", "Internal Users", "count", counts.getLane0(),
              "filter_on", workerRelations != null && workerRelations.contains(WorkerRelation.internal)));
            facetLabels.put("0", "Internal Users");facetDetails.add(CollectionUtilities.newObjectMap("id", "3", "name", "Third Parties", "count", counts.getLane3(),
            "filter_on", workerRelations != null && workerRelations.contains(WorkerRelation.thirdParties)));
        facetLabels.put("3", "Third Parties");

		// calculate our lane 4 values
		long otherLanes = counts.getLane0() + counts.getLane1() + counts.getLane2() + counts.getLane3();
		long lane4Counts = findWorkerResponse.getTotalResults() - otherLanes;

		facetDetails.add(CollectionUtilities.newObjectMap("id", "4", "name", "Everyone Else", "count", lane4Counts,
			"filter_on", workerRelations != null && workerRelations.contains(WorkerRelation.everyoneElse)));
		facetLabels.put("4", "Everyone Else");
}
	}


	private void mapCompanyTypeFacet(final String name, final Facet facet, final Map<String, Object> filters,
									 final Map<String, Map<String, String>> labels,
									 final ImmutableList<com.workmarket.search.worker.query.model.CompanyType> companyTypes) {

		List<Map<String, Object>> facetDetails = Lists.newArrayList();
		Map<String, String> facetLabels = Maps.newHashMap();
		filters.put(name, facetDetails);
		labels.put(name, facetLabels);

		if (facet != null && facet.getData() != null) {
			for (Map.Entry<String, Long> entry : facet.getData().entrySet()) {
				String facetId = entry.getKey();
				String label = null;
				boolean filterOn = false;
				if (!StringUtils.equals(facetId, "null") && NumberUtils.isDigits(facetId)) {
					CompanyType companyType = CompanyType.findByValue(Integer.valueOf(facetId));
					switch (companyType) {
						case Corporation:
							label = companyType.getDescription();
							if (companyTypes != null) {
								for (com.workmarket.search.worker.query.model.CompanyType ct : companyTypes) {
									if (ct == com.workmarket.search.worker.query.model.CompanyType.corp) {
										filterOn = true;
										break;
									}
								}
							}
							break;
						case SoleProprietor:
							label = companyType.getDescription();
							if (companyTypes != null) {
								for (com.workmarket.search.worker.query.model.CompanyType ct : companyTypes) {
									if (ct == com.workmarket.search.worker.query.model.CompanyType.individual) {
										filterOn = true;
										break;
									}
								}
							}
							break;
					}
				}

				if (label != null) {
					facetDetails.add(CollectionUtilities.newObjectMap("id", facetId,
						"name", label,
						"count", entry.getValue(),
						"filter_on", filterOn));
					facetLabels.put(entry.getKey(), label);
				}
			}
		}
	}

	private void mapVerificationFacet(final String name, final Facet facet, final long lastBackgroundCheck,
									  final long lastPassedDrugTest,
									  final Map<String, Object> filters,
									  final Map<String, Map<String, String>> labels, final FindWorkerCriteria criteria) {
		List<Map<String, Object>> facetDetails = Lists.newArrayList();
		Map<String, String> facetLabels = Maps.newHashMap();
		filters.put(name, facetDetails);
		labels.put(name, facetLabels);

		ScreeningCriteria screeningCriteria = criteria.getScreeningCriteria();
		if (facet != null && facet.getData() != null) {
			for (Map.Entry<String, Long> entry : facet.getData().entrySet()) {
				String facetId = entry.getKey();
				String label = null;
				boolean filterOn = false;
				if (StringUtils.equals(Integer.toString(Verification.BACKGROUND_CHECK.getValue()),
					facetId)) {
					label = "Background Check";
					filterOn = screeningCriteria != null && BooleanUtils.isTrue(screeningCriteria.getHasBackgroundCheck());

				} else if (StringUtils.equals(Integer.toString(Verification.DRUG_TEST.getValue()), facetId)) {
					label = "Drug Test";
					filterOn = screeningCriteria != null && BooleanUtils.isTrue(screeningCriteria.getHasPassedDrugTest());

				}

				if (label != null) {
					facetDetails.add(CollectionUtilities.newObjectMap("id", facetId,
						"name", label,
						"count", entry.getValue(),
						"filter_on", filterOn));
					facetLabels.put(entry.getKey(), label);
				}
			}
		}

        // add in our last background check count - 4 doesn't map to the above Verification object, so this is
        // an outlier and treated as a separate criteria all-together. The use of "4" is just a carry-over
        // from legacy to insure functional parity.
        facetDetails.add(CollectionUtilities.newObjectMap("id", "4",
            "name", "Background Check in last 6 months",
            "count", lastBackgroundCheck,
            "filter_on", screeningCriteria != null && BooleanUtils.isTrue(screeningCriteria.getHasCurrentBackgroundCheck())));
        facetLabels.put("4", "Background Check in last 6 months");
    facetDetails.add(CollectionUtilities.newObjectMap("id", "5",
			"name", "Background Check in last 12 months",
			"count", lastBackgroundCheck,
			"filter_on", screeningCriteria != null && BooleanUtils.isTrue(screeningCriteria.getHas12MonthBackgroundCheck())));
		facetLabels.put("5", "Background Check in last 12 months");
		facetDetails.add(CollectionUtilities.newObjectMap("id", "6",
			"name", "Drug Test in last 6 months",
			"count", lastPassedDrugTest,
			"filter_on", screeningCriteria != null && BooleanUtils.isTrue(screeningCriteria.getHas6MonthPassedDrugTest())));
		facetLabels.put("6", "Drug Test in last 6 months");
		facetDetails.add(CollectionUtilities.newObjectMap("id", "7",
			"name", "Drug Test in last 12 months",
			"count", lastPassedDrugTest,
			"filter_on", screeningCriteria != null && BooleanUtils.isTrue(screeningCriteria.getHas12MonthPassedDrugTest())));
		facetLabels.put("7", "Drug Test in last 12 months");}

	private void mapFacet(final String name, final Facet facet, final Map<String, Object> filters,
						  final Map<String, Map<String, String>> labels, final List<String> selectedCriteria,
						  final Map<Long, String> references, final ExtendedUserDetails currentUser) {
		List<Map<String, Object>> facetDetails = Lists.newArrayList();
		Map<String, String> facetLabels = Maps.newHashMap();
		filters.put(name, facetDetails);
		labels.put(name, facetLabels);

		// get our company number
		if (facet != null && facet.getData() != null) {
			for (Map.Entry<String, Long> entry : facet.getData().entrySet()) {
				String facetId = entry.getKey();
				String label = facetId.equals("null") ? null : MapUtils.getString(references, Long.valueOf(facetId), entry.getKey());
				if (label != null) {
					facetDetails.add(CollectionUtilities.newObjectMap("id", facetId,
						"name", label,
						"count", entry.getValue(),
						"filter_on", selectedCriteria != null && selectedCriteria.contains(entry.getKey())));
					facetLabels.put(entry.getKey(), label);
				}
			}
		}
	}

	private void mapAvatarFacet(final String name, final Facet facet, final Map<String, Object> filters,
								final Map<String, Map<String, String>> labels, final List<String> selectedCriteria) {
		List<Map<String, Object>> facetDetails = Lists.newArrayList();
		Map<String, String> facetLabels = Maps.newHashMap();
		filters.put(name, facetDetails);
		labels.put(name, facetLabels);

		if (facet != null && facet.getData() != null) {
			for (Map.Entry<String, Long> entry : facet.getData().entrySet()) {
				facetDetails.add(CollectionUtilities.newObjectMap("id", entry.getKey(),
					"name", "null",
					"count", entry.getValue(),
					"filter_on", selectedCriteria != null && selectedCriteria.contains(entry.getKey())));
				facetLabels.put(entry.getKey(), "null");
			}
		}
	}

	private void mapCompanyFacet(final String name, final Facet facet, final Map<String, Object> filters,
								 final Map<String, Map<String, String>> labels, final List<String> selectedCriteria,
								 final Map<Long, CompanyHydrateData> references) {
		List<Map<String, Object>> facetDetails = Lists.newArrayList();
		Map<String, String> facetLabels = Maps.newHashMap();
		filters.put(name, facetDetails);
		labels.put(name, facetLabels);

		if (facet != null && facet.getData() != null) {
			for (Map.Entry<String, Long> entry : facet.getData().entrySet()) {
				CompanyHydrateData chd = "null".equals(entry.getKey()) ? null : references.get(Long.valueOf(entry.getKey()));
				String companyName = chd == null ? entry.getKey() : chd.getName();
				facetDetails.add(CollectionUtilities.newObjectMap("id", entry.getKey(),
					"name", companyName,
					"count", entry.getValue(),
					"filter_on", selectedCriteria != null && selectedCriteria.contains(entry.getKey())));
				facetLabels.put(entry.getKey(), companyName);
			}
		}
	}

	private void mapCountriesFacet(final String name, final Facet facet, final Map<String, Object> filters,
								   final Map<String, Map<String, String>> labels, final List<String> selectedCriteria) {
		List<Map<String, Object>> facetDetails = Lists.newArrayList();
		Map<String, String> facetLabels = Maps.newHashMap();
		filters.put(name, facetDetails);
		labels.put(name, facetLabels);

		if (facet != null && facet.getData() != null) {
			for (Map.Entry<String, Long> entry : facet.getData().entrySet()) {
				Country country = "null".equals(entry.getKey()) ? null : Country.valueOf(entry.getKey());
				String countryName = country == null ? entry.getKey() : country.getName();
				facetDetails.add(CollectionUtilities.newObjectMap("id", entry.getKey(),
					"name", countryName,
					"count", entry.getValue(),
					"filter_on", selectedCriteria != null && selectedCriteria.contains(entry.getKey())));
				facetLabels.put(entry.getKey(), countryName);
			}
		}
	}

	private void mapLicenseFacet(final String name, final Facet facet, final Map<String, Object> filters,
								 final Map<String, Map<String, String>> labels, final List<String> selectedCriteria,
								 final Map<Long, License> references) {
		List<Map<String, Object>> facetDetails = Lists.newArrayList();
		Map<String, String> facetLabels = Maps.newHashMap();
		filters.put(name, facetDetails);
		labels.put(name, facetLabels);

		if (facet != null && facet.getData() != null) {
			for (Map.Entry<String, Long> entry : facet.getData().entrySet()) {
				if (!StringUtils.equals("null", entry.getKey())) {
					final Long licenseId = NumberUtils.createLong(StringUtils.substringAfter(entry.getKey(), STATE_LICENSE_SEPARATOR));
					final License license = references.get(licenseId);
					if (license != null) {
						final String licenseLabel = license.getState() + " - " + license.getName();
						facetDetails.add(
							CollectionUtilities.newObjectMap(
								"id", entry.getKey(),
								"name", licenseLabel,
								"count", entry.getValue(),
								"filter_on", selectedCriteria != null && selectedCriteria.contains(entry.getKey())));
						facetLabels.put(entry.getKey(), licenseLabel);
					}
				}
			}
		}
	}

	private void mapSharedGroupFacet(
		final String name,
		final Facet facet,
		final Map<String, Object> filters,
		final Map<String, Map<String, String>> labels,
		final List<String> selectedCriteria,
		final ExtendedUserDetails currentUser) {

		List<Map<String, Object>> facetDetails = Lists.newArrayList();
		Map<String, String> facetLabels = Maps.newHashMap();
		filters.put(name, facetDetails);
		labels.put(name, facetLabels);

		// get our facet details
		Set<Long> sharedGroupIds = getFacetIds(facet, currentUser);

		final ManagedCompanyUserGroupRowPagination allRowsPagination =
			new ManagedCompanyUserGroupRowPagination(RETURN_ALL_GROUP_PAGINATION_ROWS);
		ManagedCompanyUserGroupRowPagination pagination =
			groupService.findGroupsActiveOpenMembershipByGroupIds(sharedGroupIds, allRowsPagination);

		// map out our group details
		Map<String, Map<String, Object>> groupDetails = Maps.newHashMap();
		for (final ManagedCompanyUserGroupRow r : pagination.getResults()) {
			if (!r.getCompanyId().equals(currentUser.getCompanyId())) {
				groupDetails.put(r.getGroupId().toString(),
					CollectionUtilities.newObjectMap(
						"id", r.getGroupId().toString(),
						"open_membership", r.isOpenMembership(),
						"name", r.getName(),
						"group_owner", r.getCompanyName()));
			}
		}

		// now map our the facet details
		if (facet != null && facet.getData() != null) {
			for (Map.Entry<String, Long> entry : facet.getData().entrySet()) {
				String facetId = entry.getKey();
				Map<String, Object> details = groupDetails.get(facetId);
				if (details != null) {
					details.put("count", entry.getValue());
					details.put("filter_on", selectedCriteria != null && selectedCriteria.contains(entry.getKey()));
					facetDetails.add(details);
					facetLabels.put(entry.getKey(), details.get("name").toString());
				}
			}
		}
	}

	private void mapPrivateGroups(
		final String name,
		final Map<String, Object> filters,
		final Map<String, Map<String, String>> labels,
		final List<String> selectedCriteria,
		final ExtendedUserDetails currentUser) {

		List<Map<String, Object>> facetDetails = Lists.newArrayList();
		Map<String, String> facetLabels = Maps.newHashMap();
		filters.put(name, facetDetails);
		labels.put(name, facetLabels);

		final ManagedCompanyUserGroupRowPagination allRowsPagination =
			new ManagedCompanyUserGroupRowPagination(RETURN_ALL_GROUP_PAGINATION_ROWS);
		ManagedCompanyUserGroupRowPagination pagination =
			groupService.findCompanyGroupsActiveOpenMembership(currentUser.getCompanyId(), allRowsPagination);

		// map out our membership status
		for (final ManagedCompanyUserGroupRow r : pagination.getResults()) {
			final String groupId = r.getGroupId().toString();
			final String groupName = StringUtils.defaultIfEmpty(r.getName(), groupId);
			facetDetails.add(
				CollectionUtilities.newObjectMap(
					"id", groupId,
					"open_membership", r.isOpenMembership(),
					"name", groupName,
					"filter_on", selectedCriteria != null && selectedCriteria.contains(groupId)));
			facetLabels.put(groupId, groupName);
		}
	}

	private void mapOrgUnits(
		final String name,
		final Map<String, Object> filters,
		final Map<String, Map<String, String>> labels,
		final OrgUnitsCriteria orgCriteria,
		final ExtendedUserDetails extendedUserDetails) {

		final List<Map<String, Object>> facetDetails = Lists.newArrayList();
		final Map<String, String> facetLabels = Maps.newHashMap();


        final Long userId = extendedUserDetails.getId();
        final Long companyId = extendedUserDetails.getCompanyId();
        final String rootOrgUnit = orgStructureService.getOrgModeSetting(userId);
        final List<OrgUnitDTO> orgUnits = orgStructureService.getSubtreePaths(userId, companyId, rootOrgUnit);

		if (CollectionUtils.isNotEmpty(orgUnits)) {
			final List<String> selectedOrgUnits = orgCriteria != null ? orgCriteria.getOrgUnits() : Lists.<String>newArrayList();
			for (final OrgUnitDTO orgUnit : orgUnits) {
				final String orgUnitUuid = orgUnit.getUuid();
				final String orgUnitName = orgUnit.getName();
				final Map<String, Object> facetDetail =
					CollectionUtilities.newObjectMap(
						"id", orgUnitUuid,
						"name", orgUnitName,
						"filter_on", selectedOrgUnits.contains(orgUnitUuid)
					);
				facetDetails.add(facetDetail);
				facetLabels.put(orgUnitUuid, orgUnitName);
			}
		}

		filters.put(name, facetDetails);
		labels.put(name, facetLabels);
	}
}
