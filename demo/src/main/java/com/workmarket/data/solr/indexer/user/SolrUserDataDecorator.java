package com.workmarket.data.solr.indexer.user;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.business.gen.Messages.OrgUnit;
import com.workmarket.common.core.RequestContext;
import com.workmarket.data.solr.configuration.UserIndexerConfiguration;
import com.workmarket.data.solr.indexer.SolrDataDecorator;
import com.workmarket.data.solr.model.SolrUserData;
import com.workmarket.domains.work.model.WorkResourceAggregateFilter;
import com.workmarket.domains.work.model.WorkResourceLabelType;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.screening.model.Screening;
import com.workmarket.screening.model.ScreeningStatusCode;
import com.workmarket.screening.model.VendorRequestCode;
import com.workmarket.search.request.user.Verification;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Component
public class SolrUserDataDecorator implements SolrDataDecorator<SolrUserData> {

	private static final Logger logger = LoggerFactory.getLogger(SolrUserDataDecorator.class);

	private static final int SCREENING_REQUEST_PAGE_SIZE = 500;

	@Autowired private WorkResourceService workResourceService;
	@Autowired private RatingService ratingService;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private OrgStructureService orgStructureService;
	@Autowired private UserService userService;
	@Autowired private ScreeningService screeningService;

	@Override
	public Collection<SolrUserData> decorate(Collection<SolrUserData> solrData) {
		if (CollectionUtils.isEmpty(solrData)) return solrData;

		List<Long> userIds = extract(solrData, on(SolrUserData.class).getId());

		Map<Long, Integer> distinctBlocks = analyticsService.countDistinctBlockingCompaniesByUser(UserIndexerConfiguration.getDistinctBlocksCountThresholdDate(), userIds);
		Map<Long, Integer> repeatedClients = analyticsService.countRepeatedClientsByUser(UserIndexerConfiguration.getRepeatedClientsThresholdDate(), userIds);

		// load in screening data as a batch request
		RequestContext requestContext = webRequestContextProvider.getRequestContext();
		Map<Long, Screening> drugTests = getLatestScreeningsForIndexing(userIds, VendorRequestCode.DRUG);
		Map<Long, Screening> backgroundChecks = getLatestScreeningsForIndexing(userIds, VendorRequestCode.BACKGROUND);

		final Map<String, List<OrgUnit>> membershipsForUsers = getOrgMembershipsForUsers(solrData);
		final Map<Long, String> userUuidsByIds = userService.findUserUuidsByIds(userIds);

		// and now decorate the data
		for (SolrUserData solrUserData : solrData) {
			decorate(solrUserData, distinctBlocks, repeatedClients);
			decorateScreening(solrUserData, drugTests, backgroundChecks);
			decorateOrg(solrUserData, membershipsForUsers, userUuidsByIds);
		}

		return solrData;
	}

	private Map<String, List<OrgUnit>> getOrgMembershipsForUsers(final Collection<SolrUserData> solrData) {
		final Map<String, List<OrgUnit>> result = Maps.newHashMap();

		if (CollectionUtils.isEmpty(solrData)) {
			return result;
		}

		final Map<String, List<Long>> companyIdToUserIdsMap = buildCompanyUuidToUserIdsMap(solrData);
		final List<String> companyUuids = Lists.newArrayList(companyIdToUserIdsMap.keySet());
		if (CollectionUtils.isEmpty(companyUuids)) {
			return result;
		}

		for (final String companyUuid : companyUuids) {
			final List<Long> userIds = companyIdToUserIdsMap.get(companyUuid);
			final Map<String, List<OrgUnit>> memberships = orgStructureService.findDirectMembershipsForUsersInCompany(userIds, companyUuid);
			result.putAll(memberships);
		}

		return result;
	}

	private Map<String, List<Long>> buildCompanyUuidToUserIdsMap(final Collection<SolrUserData> solrData) {
		final Map<String, List<Long>> result = Maps.newHashMap();

		if (CollectionUtils.isEmpty(solrData)) {
			return result;
		}

		for (final SolrUserData solrUserData : solrData) {
			final String companyUuid = solrUserData.getCompany().getUuid();
			final Long newUserId = solrUserData.getId();
			final List<Long> userIds = result.get(companyUuid);
			if (userIds == null) {
				result.put(companyUuid, Lists.newArrayList(newUserId));
			} else {
				userIds.add(newUserId);
				result.put(companyUuid, userIds);
			}
		}

		return result;
	}

	@Override
	public SolrUserData decorate(SolrUserData solrData) {
		if (solrData != null) {
			Collection<SolrUserData> decoratedData = decorate(Lists.newArrayList(solrData));
			if (CollectionUtils.isNotEmpty(decoratedData)) {
				return decoratedData.iterator().next();
			}
		}
		return solrData;
	}

	private SolrUserData decorateScreening(SolrUserData solrUserData, Map<Long, Screening> drugTests, Map<Long, Screening> backgroundChecks) {
		if (solrUserData != null) {
			final List<Integer> verificationIds = Lists.newArrayList();
			final Screening drugTest = drugTests.get(solrUserData.getId());
			final Screening backgroundCheck = backgroundChecks.get(solrUserData.getId());

			if (backgroundCheck != null) {
				solrUserData.setLastBackgroundCheckDate(
					backgroundCheck.getVendorResponseDate() != null
						? backgroundCheck.getVendorResponseDate()
						: backgroundCheck.getModifiedOn());
				if (backgroundCheck.getStatus() == ScreeningStatusCode.PASSED) {
					verificationIds.add(Verification.BACKGROUND_CHECK.getValue());
					solrUserData.setPassedBackgroundCheck(true);
				} else if (backgroundCheck.getStatus() == ScreeningStatusCode.FAILED) {
					verificationIds.add(Verification.FAILED_BACKGROUND_CHECK.getValue());
					solrUserData.setPassedBackgroundCheck(false);
				}
			}

			if (drugTest != null) {
				solrUserData.setLastDrugTestDate(
					drugTest.getVendorResponseDate() != null
						? drugTest.getVendorResponseDate()
						: drugTest.getModifiedOn());
				if (drugTest.getStatus() == ScreeningStatusCode.PASSED) {
					verificationIds.add(Verification.DRUG_TEST.getValue());
					solrUserData.setPassedDrugTest(true);
				} else if (drugTest.getStatus() == ScreeningStatusCode.FAILED) {
					verificationIds.add(Verification.FAILED_DRUG_TEST.getValue());
					solrUserData.setPassedDrugTest(false);
				}
			}

			if (CollectionUtils.isNotEmpty(verificationIds)) {
				solrUserData.setVerificationIds(verificationIds);
			}
		}

		return solrUserData;
	}

	private SolrUserData decorate(final SolrUserData solrUserData, final Map<Long, Integer> distinctBlocks, final Map<Long, Integer> repeatedClients) {
		WorkResourceAggregateFilter resourceAggregateFilter;
		if (solrUserData != null) {
			//Blocks per user
			solrUserData.setBlocksCount(MapUtils.getInteger(distinctBlocks, solrUserData.getId(), 0));
			//Repeated clients
			solrUserData.setRepeatClientsCount(MapUtils.getInteger(repeatedClients, solrUserData.getId(), 0));

			if (solrUserData.getLastAssignedWorkDate() != null) {
				Calendar onTimePercentageThreshold = UserIndexerConfiguration.getOnTimePercentageThresholdDate();
				if (solrUserData.getLastAssignedWorkDate().after(onTimePercentageThreshold)) {
					resourceAggregateFilter = new WorkResourceAggregateFilter().setFromDate(onTimePercentageThreshold);
					solrUserData.setOnTimePercentage(workResourceService.calculateOnTimePercentageForUser(solrUserData.getId(), resourceAggregateFilter));
				}

				Calendar deliverableOnTimePercentageThreshold = UserIndexerConfiguration.getDeliverableOnTimePercentageThresholdDate();
				if (solrUserData.getLastAssignedWorkDate().after(deliverableOnTimePercentageThreshold)) {
					resourceAggregateFilter = new WorkResourceAggregateFilter().setFromDate(deliverableOnTimePercentageThreshold);
					solrUserData.setDeliverableOnTimePercentage(workResourceService.calculateDeliverableOnTimePercentageForUser(solrUserData.getId(), resourceAggregateFilter));
				}

				//Reset the filters
				resourceAggregateFilter = new WorkResourceAggregateFilter();
				//Work Resource Labels Counts
				resourceAggregateFilter.setFromDate(UserIndexerConfiguration.getWorkResourceLateLabelsThresholdDate()).setResourceLabelTypeCode(WorkResourceLabelType.LATE);
				solrUserData.setLateLabelCount(workResourceService.countConfirmedWorkResourceLabelByUserId(solrUserData.getId(), resourceAggregateFilter));

				resourceAggregateFilter.setFromDate(UserIndexerConfiguration.getWorkResourceAbandonedLabelsThresholdDate()).setResourceLabelTypeCode(WorkResourceLabelType.ABANDONED);
				solrUserData.setAbandonedLabelCount(workResourceService.countConfirmedWorkResourceLabelByUserId(solrUserData.getId(), resourceAggregateFilter));

				resourceAggregateFilter.setFromDate(UserIndexerConfiguration.getWorkResourceCancelledLabelsThresholdDate()).setResourceLabelTypeCode(WorkResourceLabelType.CANCELLED);
				solrUserData.setCancelledLabelCount(workResourceService.countConfirmedWorkResourceLabelByUserId(solrUserData.getId(), resourceAggregateFilter));

				resourceAggregateFilter.setFromDate(UserIndexerConfiguration.getWorkResourceOnTimeCompletionLabelsThresholdDate()).setResourceLabelTypeCode(WorkResourceLabelType.COMPLETED_ONTIME);
				solrUserData.setDelayedLabelCount(workResourceService.countConfirmedWorkResourceLabelByUserId(solrUserData.getId(), resourceAggregateFilter));

				solrUserData.setSatisfactionRate(ratingService.findSatisfactionRateForUser(solrUserData.getId()));
				solrUserData.setAverageStarRating(ratingService.findAverageStarRatingInLastNMonths(solrUserData.getId(), UserIndexerConfiguration.RATING_AVERAGE_THRESHOLD_IN_MONTHS));
			}
		}
		return solrUserData;
	}

	private void decorateOrg(final SolrUserData solrUserData, final Map<String, List<OrgUnit>> membershipsForUsers, final Map<Long, String> userUuidsByIds) {
		final String userUuid = userUuidsByIds.get(solrUserData.getId());
		if (StringUtils.isNotBlank(userUuid)) {
			final List<OrgUnit> userOrgUnits = membershipsForUsers.get(userUuid);
			if (CollectionUtils.isEmpty(userOrgUnits)) {
				return;
			}

			final List<String> orgUnitUuids = Lists.transform(userOrgUnits, new Function<OrgUnit, String>() {
				public String apply(OrgUnit userOrgUnit) { return userOrgUnit.getUuid(); }
			});
			solrUserData.setOrgUnits(orgUnitUuids);
		}
	}

	/**
	 * Gets latest screenings for a list of user ids.
	 * @param userIds         a list of user ids
	 * @param screeningType   the screening type
	 * @return map of user id and its latest screening
	 */
	private Map<Long, Screening> getLatestScreeningsForIndexing(
		final List<Long> userIds,
		final VendorRequestCode screeningType
	) {
		final Map<Long, Screening> result = Maps.newHashMap();

		final List<Screening> screenings =
			screeningService.findMostRecentScreeningsByUserIds(userIds, screeningType, true);

		for (final Screening screening : screenings) {
			Long userId = Long.parseLong(screening.getUserId());
			// since the request is sorted by request_date desc
			// we take the first one we see and discard the later ones
			if (!result.containsKey(userId)) {
				result.put(userId, screening);
			}
		}

		return result;
	}
}
