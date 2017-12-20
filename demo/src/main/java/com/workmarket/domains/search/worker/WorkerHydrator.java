package com.workmarket.domains.search.worker;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.dao.GroupMembershipDAO;
import com.workmarket.domains.model.analytics.ResourceScoreCard;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.helpers.WMCallable;
import com.workmarket.screening.model.Screening;
import com.workmarket.screening.model.VendorRequestCode;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.worker.FindWorkerSearchResponse;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.UserType;
import com.workmarket.search.worker.query.model.Worker;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.requirementsets.EligibilityService;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
public class WorkerHydrator extends AbstractHydrator {
	private static final Logger logger = LoggerFactory.getLogger(WorkerHydrator.class);

	private static final String METRIC_ROOT = "WorkerSearch";
	private static final String WORKER_HYDRATION = "workerHydration";

	private static final String ASSESSMENT_SQL =
			"SELECT DISTINCT"
					+ " u.id, a.name "
					+ "FROM assessment a "
					+ "LEFT JOIN assessment_user_association aua ON aua.assessment_id = a.id "
					+ "LEFT JOIN assessment_attempt aa ON aa.assessment_user_association_id = aua.id "
					+ "LEFT JOIN user u ON u.id = aua.user_id "
					+ "WHERE u.id IN (:ids) "
					+ "AND aa.passed_flag = 1 "
					+ "AND a.company_id = (:companyId)";
	private static final String CERTIFICATION_SQL =
			"SELECT"
					+ " u.id, cert.name "
					+ "FROM certification cert "
					+ "LEFT JOIN user_certification_association uca ON uca.certification_id = cert.id "
					+ "LEFT JOIN user u ON u.id = uca.user_id "
					+ "WHERE u.id IN (:ids) "
					+ "AND cert.deleted = 0 AND uca.deleted = 0";
	private static final String LICENSE_SQL =
			"SELECT"
					+ " u.id, l.name "
					+ "FROM license l "
					+ "LEFT JOIN user_license_association ula ON ula.license_id = l.id "
					+ "LEFT JOIN user u ON u.id = ula.user_id "
					+ "WHERE u.id IN (:ids) "
					+ "AND l.deleted = 0 AND ula.deleted = 0";
	private static final String GROUP_SQL =
			"SELECT DISTINCT"
					+ " u.id, ug.name "
					+ "FROM user_group ug "
					+ "LEFT JOIN user_user_group_association uuga ON uuga.user_group_id = ug.id "
					+ "LEFT JOIN user u ON u.id = uuga.user_id "
					+ "WHERE u.id IN (:ids) "
					+ "AND ug.deleted = 0 "
					+ "AND uuga.deleted = 0 "
					+ "AND uuga.approval_status = 1 "
					+ "AND ug.company_id = (:companyId)"
					+ "AND ug.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'";
	private static final String INSURANCE_SQL =
			"SELECT"
					+ " u.id, i.name "
					+ "FROM insurance i "
					+ "LEFT JOIN user_insurance_association uia ON uia.insurance_id = i.id "
					+ "LEFT JOIN user u ON u.id = uia.user_id "
					+ "WHERE u.id IN (:ids) "
					+ "AND i.deleted = 0 AND uia.deleted = 0";
	private static final String ADDRESS_URL_SQL =
			"SELECT DISTINCT"
					+ " u.id, a.latitude, a.longitude "
					+ "FROM address a "
					+ "LEFT JOIN profile p ON p.address_id = a.id "
					+ "LEFT JOIN user u ON u.id = p.user_id "
					+ "WHERE u.id IN (:ids)";
	private static final String AVATAR_SQL =
			"SELECT uaa.user_id id, "
					+ "origAsset.uuid origUuid, origAssetUri.cdn_uri_prefix origUriPrefix, "
					+ "largeAsset.uuid largeUuid, largeAssetUri.cdn_uri_prefix largeUriPrefix, "
					+ "smallAsset.uuid smallUuid, smallAssetUri.cdn_uri_prefix smallUriPrefix "
					+ "FROM "
					+ "(SELECT * "
					+ " FROM user_asset_association ua "
					+ " WHERE ua.user_id in (:ids) "
					+ "     AND ua.asset_type_code = 'avatar' "
					+ "     AND ua.active = 1 "
					+ "     AND ua.deleted = 0 "
					+ "     AND ua.approval_status = 1 "
					+ " ORDER BY ua.created_on DESC) uaa "
					+ "LEFT JOIN asset as origAsset ON uaa.asset_id = origAsset.id "
					+ "LEFT JOIN asset as largeAsset ON uaa.transformed_large_asset_id = largeAsset.id "
					+ "LEFT JOIN asset as smallAsset ON uaa.transformed_small_asset_id = smallAsset.id "
					+ "LEFT JOIN asset_cdn_uri origAssetUri ON origAsset.asset_cdn_uri_id = origAssetUri.id "
					+ "LEFT JOIN asset_cdn_uri largeAssetUri ON largeAsset.asset_cdn_uri_id = largeAssetUri.id "
					+ "LEFT JOIN asset_cdn_uri smallAssetUri ON smallAsset.asset_cdn_uri_id = smallAssetUri.id ";
	private static final String PROFILE_SQL =
			"SELECT "
					+ " u.id, u.uuid, u.user_number number, u.first_name firstName, u.last_name lastName, "
					+ " p.job_title jobTitle, u.created_on createdOn, "
					+ " c.name companyName, c.effective_name companyEffectiveName, "
					+ " a.latitude, a.longitude, a.city, s.short_name as state, a.postal_code postalCode, a.country "
					+ "FROM user u "
					+ "LEFT JOIN profile p on p.user_id = u.id "
					+ "LEFT JOIN company c on c.id = u.company_id "
					+ "LEFT JOIN address a on p.address_id = a.id "
					+ "LEFT JOIN state s on a.state = s.id  "
					+ "WHERE u.id in (:ids)";

	private static final String USERS_BLOCKED_BY_COMPANY =
		" SELECT blocked_user_id " +
			" FROM blocked_user_association " +
			" WHERE blocked_user_id IN (:userids) " +
			" AND blocking_company_id = :companyid " +
			" AND deleted = 0 ";

    @Autowired private AnalyticsService analyticsService;
    @Autowired private LaneService laneService;
	@Autowired private GroupMembershipDAO groupMembershipDAO;
    @Autowired private MetricRegistry metricRegistry;
    @Autowired @Qualifier("taskExecutor") private ThreadPoolTaskExecutor taskExecutor;
    @Autowired private WebRequestContextProvider webRequestContextProvider;
    @Autowired private TWorkFacadeService tWorkFacadeService;
    @Autowired private WorkService workService;
    @Autowired private EligibilityService eligibilityService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private ScreeningService screeningService;

	private Timer hydrationTimer;

	@PostConstruct
	public void init() {
		WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, METRIC_ROOT);
		hydrationTimer = wmMetricRegistryFacade.timer(WORKER_HYDRATION);
	}

	public List<Map<String, Object>> hydrateWorkers(
			final RequestContext requestContext, final ExtendedUserDetails currentUser,
			final FindWorkerSearchResponse searchResponse, final FindWorkerCriteria criteria) {

		Timer.Context timerContext = hydrationTimer.time();
		final List<Map<String, Object>> results = Lists.newArrayList();

		try {
			if (CollectionUtils.isNotEmpty(searchResponse.getResults().getWorkers())) {
				final List<Worker> workers = searchResponse.getResults().getWorkers();
				final Map<Long, Integer> userIdsAndPositions = getUserIdsAndPositions(workers, UserType.WORKER);
				final Collection<Long> userIds = new ArrayList<>(userIdsAndPositions.keySet());
				if (userIds.size() > 0) {
					final Future<Map<Long, Map<String, Object>>> profiles =
							getProfilesFuture(userIds, PROFILE_SQL);
					final Future<Set<Long>> blockedUsers =
						getBlockedUsersFuture(currentUser.getCompanyId(), userIds, USERS_BLOCKED_BY_COMPANY);
					final Future<Map<Long, List<String>>> assessments =
							getAttributesFuture(userIds, currentUser.getCompanyId(), ASSESSMENT_SQL);
					final Future<Map<Long, List<String>>> certifications =
							getAttributesFuture(userIds, CERTIFICATION_SQL);
					final Future<Map<Long, List<String>>> licenses = getAttributesFuture(userIds, LICENSE_SQL);
					final Future<Map<Long, List<String>>> groups =
							getAttributesFuture(userIds, currentUser.getCompanyId(), GROUP_SQL);
					final Future<Map<Long, List<String>>> insurances = getAttributesFuture(userIds, INSURANCE_SQL);
					final Future<Map<Long, Double>> userDistances =
							getDistancesFuture(userIds, criteria.getLocationCriteria(), ADDRESS_URL_SQL);

					final Future<Map<Long, ResourceScoreCard>> companyScorecards =
						taskExecutor.submit(new WMCallable<Map<Long, ResourceScoreCard>>(webRequestContextProvider) {
							@Override
							public Map<Long, ResourceScoreCard> apply() throws Exception {
								return getCompanyScoreCards(userIds, Long.valueOf(criteria.getRequestingCompanyId()));
							}
						});
					final Future<Map<Long, ResourceScoreCard>> resourceScorecards =
						taskExecutor.submit(new WMCallable<Map<Long, ResourceScoreCard>>(webRequestContextProvider) {
							@Override
							public Map<Long, ResourceScoreCard> apply() throws Exception {
								return getResourceScoreCards(userIds);
							}
						});

					final Future<Map<Long, Screening>> backgroundChecks =
						taskExecutor.submit(new WMCallable<Map<Long, Screening>>(webRequestContextProvider) {
							@Override
							public Map<Long, Screening> apply() throws Exception {
								return getLatestScreeningsForSearchCard(userIds, VendorRequestCode.BACKGROUND);
							}
						});
					final Future<Map<Long, Screening>> drugTests =
						taskExecutor.submit(new WMCallable<Map<Long, Screening>>(webRequestContextProvider) {
							@Override
							public Map<Long, Screening> apply() throws Exception {
								return getLatestScreeningsForSearchCard(userIds, VendorRequestCode.DRUG);
							}
						});

					final Future<Map<Long, String>> avatars = getAvatarsFuture(userIds, AVATAR_SQL);

					final Future<Map<Long, LaneContext>> laneContexts =
						taskExecutor.submit(new WMCallable<Map<Long, LaneContext>>(webRequestContextProvider) {
							@Override
							public Map<Long, LaneContext> apply() throws Exception {
								return getUserLaneContexts(userIds, currentUser.getCompanyId());
							}
						});

					final Future<Map<Long, String>> membershipStatus =
						criteria.getGroupMembershipCriteria() == null ? null :
							taskExecutor.submit(new WMCallable<Map<Long, String>>(webRequestContextProvider) {
								@Override
								public Map<Long, String> apply() throws Exception {
									Set<String> groupIds = getGroupIds(criteria.getGroupMembershipCriteria());
									return getGroupMembershipStatus(groupIds, ImmutableSet.copyOf(userIds));
								}
							});

					final Future<Map<Long, Eligibility>> eligibilities =
						taskExecutor.submit(new WMCallable<Map<Long, Eligibility>>(webRequestContextProvider) {
							@Override public Map<Long, Eligibility> apply() throws Exception {
								return getEligibilities(currentUser, criteria, userIds);
							}
						});

					Boolean isTalentPoolSearch = StringUtils.equals(SearchType.PEOPLE_SEARCH_TALENT_POOL_INVITE.toString(), criteria.getSearchType().toString())
							|| StringUtils.equals(SearchType.PEOPLE_SEARCH_GROUP_MEMBER.toString(), criteria.getSearchType().toString());
					Long talentPoolId = isTalentPoolSearch ? NumberUtilities.safeParseLong(criteria.getRequestingTalentPoolId()) : null;

					for (Worker worker : workers) {
						Long userId = Long.valueOf(worker.getId());
						if (!userIdsAndPositions.containsKey(userId)) {
							continue;
						}
						Map<String, Object> profile = profiles.get().get(userId);
						if (profile == null) {
							continue;
						}

						Set<Criterion> talentPoolCriterion = null;

						if (isTalentPoolSearch && talentPoolId != null) {
							Eligibility eligibility = userGroupService.validateRequirementSets(talentPoolId, userId);
							talentPoolCriterion = eligibility.getCriteria();
						}

						results.add(
							toObjectMap(
								worker,
								SolrUserType.WORKER,
								userIdsAndPositions.get(userId),
								profile,
								avatars.get().get(userId),
								resourceScorecards.get().get(userId),
								companyScorecards.get().get(userId),
								backgroundChecks.get().get(userId),
								drugTests.get().get(userId),
								laneContexts.get().get(userId),
								assessments.get().get(userId),
								certifications.get().get(userId),
								licenses.get().get(userId),
								insurances.get().get(userId),
								groups.get().get(userId),
								userDistances.get().get(userId),
								membershipStatus == null ? null : membershipStatus.get().get(userId),
								eligibilities.get().get(userId),
								blockedUsers.get().contains(userId),
								talentPoolCriterion));
					}
				}
			}
		} catch (ExecutionException | InterruptedException e) {
			logger.warn("execution exception {}", e.getMessage());
		} finally {
			logger.info("Worker hydration took {} ms", TimeUnit.NANOSECONDS.toMillis(timerContext.stop()));
		}
		return results;
	}

	/**
	 * Gets scorecards for a list of users.
	 *
	 * @param userIds a list of user ids
	 * @return map of user id and its scorecard
	 */
	private Map<Long, ResourceScoreCard> getResourceScoreCards(final Collection<Long> userIds) {
		try {
			return analyticsService.getResourceScoreCards(Lists.newArrayList(userIds));
		} catch (Throwable t) {
			logger.error("Failed to retrieve the resource scorecard.", t);
			return Maps.newHashMap();
		}
	}

	/**
	 * Gets company scorecards for a list of users.
	 *
	 * @param userIds   a list of user ids
	 * @param companyId the company id
	 * @return map of user id and it scorecard
	 */
	private Map<Long, ResourceScoreCard> getCompanyScoreCards(final Collection<Long> userIds, final Long companyId) {
		try {
			return analyticsService.getResourceScoreCardsForCompany(companyId, Lists.newArrayList(userIds));
		} catch (Throwable t) {
			logger.error("Failed to retrieve the company scorecard.", t);
			return Maps.newHashMap();
		}
	}

	/**
	 * Gets latest screenings for a list of user ids.
	 * @param userIds         a set of user ids
	 * @param screeningType   the screening type
	 * @return map of user id and its latest screening
	 */
	private Map<Long, Screening> getLatestScreeningsForSearchCard(
		final Collection<Long> userIds,
		final VendorRequestCode screeningType
	) {
		final Map<Long, Screening> result = Maps.newHashMap();

		final List<Screening> screenings =
			screeningService.findMostRecentScreeningsByUserIds(userIds, screeningType, false);

		for (final Screening screening : screenings) {
			Long userId = Long.parseLong(screening.getUserId());
			// only use the first seen since data is sorted by date desc
			if (!result.containsKey(userId)) {
				result.put(userId, screening);
			}
		}

		return result;
	}

	/**
	 * Gets lane associations (with searcher's company) for a list of user ids.
	 *
	 * @param userIds   a list of user ids
	 * @param companyId searcher's company id
	 * @return map of user id and lane relationships with this company
	 */
	private Map<Long, LaneContext> getUserLaneContexts(final Collection<Long> userIds, final Long companyId) {
		Map<Long, LaneContext> laneContextMap = new HashMap<>(userIds.size());
		try {

			Set<LaneAssociation> laneAssociationSet =
					laneService.findAllAssociationsWhereUserIdIn(companyId, new HashSet<>(userIds));

			for (LaneAssociation laneAssociation : laneAssociationSet) {
				laneContextMap.put(
						laneAssociation.getUser().getId(),
						new LaneContext(laneAssociation.getLaneType(), laneAssociation.getApprovalStatus()));
			}
		} catch (Throwable t) {
			logger.error("Failed to retrieve the user lane contexts.", t);
		}
		return laneContextMap;
	}

	/**
	 * Gets worker group membership statuses for a list of user ids on a talent pool.
	 *
	 * @param groupIds
	 * @param userIds
	 * @return
	 */
	private Map<Long, String> getGroupMembershipStatus(final Set<String> groupIds, final ImmutableSet<Long> userIds) {
		try {
			Map<Long, String> groupMembershipStatus = Maps.newHashMap();
			//There will only ever be one groupId. If there is ever a use case where we end up supporting multiple,
			//we can compose this map accordingly
			for (String groupId : groupIds) {
				groupMembershipStatus = groupMembershipDAO.getDerivedStatusesByGroupIdAndUserIds(Long.parseLong(groupId), userIds);
			}
			return groupMembershipStatus;
		} catch (Throwable t) {
			logger.error("Failed to retrieve user group worker membership status',", t);
			return Maps.newHashMap();
		}
	}

	/**
	 * Gets eligibility for a set of users.
	 * Eligibility hydration is only available when current user is a dispatcher and search talent for assignment.
	 *
	 * @param currentUser current user who tries to dispatch work
	 * @param criteria search criteria to indicate whether there is a work criteria
	 * @param userIds a set of users we want to check eligibility
	 * @return Map
	 */
	private Map<Long, Eligibility> getEligibilities(
		final ExtendedUserDetails currentUser,
		final FindWorkerCriteria criteria,
		final Collection<Long> userIds
	) {
		Map<Long, Eligibility> eligibilities = Maps.newHashMap();
		if (!currentUser.isDispatcher() || criteria.getWorkCriteria() == null || criteria.getWorkCriteria().getWorkNumber() == null) {
			return eligibilities;
		}
		final Long workId = workService.findWorkId(criteria.getWorkCriteria().getWorkNumber());
		if (workId == null) {
			return eligibilities;
		}
		final WorkRequest workRequest = new WorkRequest(currentUser.getId(), workId, criteria.getWorkCriteria().getWorkNumber());
		try {
			final WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);
			final Work work = workResponse.getWork();
			for (final Long userId : userIds) {
				Eligibility eligibility = eligibilityService.getEligibilityFor(userId, work);
				eligibilities.put(userId, eligibility);
			}
		} catch (WorkActionException e) {
			logger.error("hydrate eligibility failure -- find work exception {}", e.getMessage());
		}
		return eligibilities;
	}
}
