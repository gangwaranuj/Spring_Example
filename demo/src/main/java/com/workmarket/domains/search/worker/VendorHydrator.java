package com.workmarket.domains.search.worker;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.api.v2.worker.model.GeoPoint;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.analytics.ResourceScoreField;
import com.workmarket.domains.model.analytics.ScoreCard;
import com.workmarket.domains.model.analytics.VendorScoreCard;
import com.workmarket.helpers.WMCallable;
import com.workmarket.search.worker.FindWorkerSearchResponse;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.UserType;
import com.workmarket.search.worker.query.model.Worker;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.talentpool.TalentPoolService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static ch.lambdaj.Lambda.flatten;

/**
 * Vendor hydrator.
 */
@Component
public class VendorHydrator extends AbstractHydrator {
    private static final Logger logger = LoggerFactory.getLogger(VendorHydrator.class);

    private static final String METRIC_ROOT = "WorkerSearch";
    private static final String VENDOR_HYDRATION ="vendorHydration";

    private static final String CERTIFICATION_SQL =
        "SELECT"
            + " c.id, cert.name "
            + "FROM certification cert "
            + "LEFT JOIN user_certification_association uca ON uca.certification_id = cert.id "
            + "LEFT JOIN user u ON u.id = uca.user_id "
            + "LEFT JOIN company c ON c.id = u.company_id "
            + "WHERE u.id IN (:ids) "
            + "AND cert.deleted = 0 AND uca.deleted = 0";
    private static final String LICENSE_SQL =
        "SELECT"
            + " c.id, l.name "
            + "FROM license l "
            + "LEFT JOIN user_license_association ula ON ula.license_id = l.id "
            + "LEFT JOIN user u ON u.id = ula.user_id "
            + "LEFT JOIN company c ON c.id = u.company_id "
            + "WHERE u.id IN (:ids) "
            + "AND l.deleted = 0 AND ula.deleted = 0";
    private static final String INSURANCE_SQL =
        "SELECT"
            + " c.id, i.name "
            + "FROM insurance i "
            + "LEFT JOIN user_insurance_association uia ON uia.insurance_id = i.id "
            + "LEFT JOIN user u ON u.id = uia.user_id "
            + "LEFT JOIN company c ON c.id = u.company_id "
            + "WHERE u.id IN (:ids) "
            + "AND i.deleted = 0 AND uia.deleted = 0";
    private static final String ASSESSMENT_SQL =
        "SELECT DISTINCT"
            + " c.id, a.name "
            + "FROM assessment a "
            + "LEFT JOIN assessment_user_association aua ON aua.assessment_id = a.id "
            + "LEFT JOIN assessment_attempt aa ON aa.assessment_user_association_id = aua.id "
            + "LEFT JOIN user u ON u.id = aua.user_id "
            + "LEFT JOIN company c ON c.id = u.company_id "
            + "WHERE u.id IN (:ids) "
            + "AND aa.passed_flag = 1 "
            + "AND a.company_id = (:companyId)";
    private static final String GROUP_SQL =
        "SELECT DISTINCT"
            + " c.id, ug.name "
            + "FROM user_group ug "
            + "LEFT JOIN user_user_group_association uuga ON uuga.user_group_id = ug.id "
            + "LEFT JOIN user u ON u.id = uuga.user_id "
            + "LEFT JOIN company c ON c.id = u.company_id "
            + "WHERE u.id IN (:ids) "
            + "AND ug.deleted = 0 "
            + "AND uuga.deleted = 0 "
            + "AND uuga.approval_status = 1 "
            + "AND ug.company_id = (:companyId)";
    private static final String ADDRESS_URL_SQL =
        "SELECT DISTINCT"
            + " c.id, a.latitude, a.longitude "
            + "FROM address a "
            + "LEFT JOIN profile p ON p.address_id = a.id "
            + "LEFT JOIN user u ON u.id = p.user_id "
            + "LEFT JOIN company c ON c.id = u.company_id "
            + "WHERE u.id IN (:ids)";
    private static final String AVATAR_SQL =
        "SELECT "
            + " caa.company_id id, "
            + " origAsset.uuid origUuid, origAssetUri.cdn_uri_prefix origUriPrefix, "
            + " largeAsset.uuid largeUuid, largeAssetUri.cdn_uri_prefix largeUriPrefix, "
            + " smallAsset.uuid smallUuid, smallAssetUri.cdn_uri_prefix smallUriPrefix "
            + "FROM "
            + "(SELECT * "
            + " FROM company_asset_association ca "
            + " WHERE ca.company_id in (:ids) "
            + "     AND ca.asset_type_code = 'avatar' "
            + "     AND ca.active = 1 "
            + "     AND ca.deleted = 0 "
            + "     AND ca.approval_status = 1 "
            + " ORDER BY ca.created_on DESC) caa "
            + "LEFT JOIN asset as origAsset ON caa.asset_id = origAsset.id "
            + "LEFT JOIN asset as largeAsset ON caa.transformed_large_asset_id = largeAsset.id "
            + "LEFT JOIN asset as smallAsset ON caa.transformed_small_asset_id = smallAsset.id "
            + "LEFT JOIN asset_cdn_uri origAssetUri ON origAsset.asset_cdn_uri_id = origAssetUri.id "
            + "LEFT JOIN asset_cdn_uri largeAssetUri ON largeAsset.asset_cdn_uri_id = largeAssetUri.id "
            + "LEFT JOIN asset_cdn_uri smallAssetUri ON smallAsset.asset_cdn_uri_id = smallAssetUri.id ";
    private static final String PROFILE_SQL =
        "SELECT "
            + " c.id, c.uuid, c.company_number number, c.created_on createdOn, "
            + " c.name lastName, c.name companyName, c.effective_name companyEffectiveName, "
            + " a.latitude, a.longitude, a.city, s.short_name as state, a.postal_code postalCode, a.country "
            + "FROM company c "
            + "LEFT JOIN address a on c.address_id = a.id "
            + "LEFT JOIN state s on a.state = s.id  "
            + "WHERE c.id in (:ids)";
    private static final String COMPANY_WORKER_SQL =
        "SELECT DISTINCT"
            + " u.id, u.company_id "
            + "FROM user u "
            + "LEFT JOIN user_acl_role uar ON u.id = uar.user_id "
            + "LEFT JOIN acl_role ar ON ar.id = uar.acl_role_id "
            + "WHERE u.company_id in (:vendorIds) "
            + "    AND u.user_status_type_code = 'approved' "
            + "    AND ((u.lane3_approval_status = 1 AND ar.name = 'External' AND uar.deleted = 0) "
            + "         OR (u.lane3_approval_status = 4 AND ar.name = 'Internal' AND uar.deleted = 0)) ";

    @Autowired private AnalyticsService analyticsService;
    @Autowired private MetricRegistry metricRegistry;
    @Autowired private TalentPoolService talentPoolService;
    @Autowired @Qualifier("taskExecutor") private ThreadPoolTaskExecutor taskExecutor;
    @Autowired private WebRequestContextProvider webRequestContextProvider;

    private Timer hydrationTimer;


    @PostConstruct
    public void init() {
        final WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, METRIC_ROOT);
        hydrationTimer = wmMetricRegistryFacade.timer(VENDOR_HYDRATION);
    }

    public List<Map<String, Object>> hydrateVendors(
        final RequestContext requestContext, final ExtendedUserDetails currentUser,
        final FindWorkerSearchResponse searchResponse, final FindWorkerCriteria criteria) {

        Timer.Context timerContext = hydrationTimer.time();
        List<Map<String, Object>> results = Lists.newArrayList();

        try {
            if (CollectionUtils.isNotEmpty(searchResponse.getResults().getWorkers())) {
                final List<Worker> workers = searchResponse.getResults().getWorkers();
                final Map<Long, Integer> vendorIdsAndPositions = getUserIdsAndPositions(workers, UserType.VENDOR);

                final Collection<Long> vendorIds = vendorIdsAndPositions.keySet();

                if (vendorIds.size() > 0) {
                    Map<Long, List<Long>> workerLists = getVendorWorkers(vendorIds);
                    final Collection<Long> workerIds =  flatten(workerLists.values());

                    final Future<Map<Long, Map<String, Object>>> profiles =
                        getProfilesFuture(vendorIds, PROFILE_SQL);
                    final Future<Map<Long, String>> avatars = getAvatarsFuture(vendorIds, AVATAR_SQL);
                    final Future<Map<Long, List<String>>> vendorCertifications =
                        getAttributesFuture(workerIds, CERTIFICATION_SQL);
                    final Future<Map<Long, List<String>>> vendorLicenses = getAttributesFuture(workerIds, LICENSE_SQL);
                    final Future<Map<Long, List<String>>> vendorInsurances =
                        getAttributesFuture(workerIds, INSURANCE_SQL);
                    final Future<Map<Long, List<String>>> vendorAssessment =
                        getAttributesFuture(workerIds, currentUser.getCompanyId(), ASSESSMENT_SQL);
                    final Future<Map<Long, List<String>>> vendorGroups =
                        getAttributesFuture(workerIds, currentUser.getCompanyId(), GROUP_SQL);

                    final Future<Map<Long, Double>> vendorDistances =
                        getDistancesFuture(workerIds, criteria.getLocationCriteria(), ADDRESS_URL_SQL);

                    final Future<Map<Long, VendorScoreCard>> vendorScoreCards =
                        taskExecutor.submit(new WMCallable<Map<Long, VendorScoreCard>>(webRequestContextProvider) {
                            @Override
                            public Map<Long, VendorScoreCard> apply() throws Exception {
                                return getVendorsScoreCards(new ArrayList<>(vendorIds));
                            }
                        });

                    final Future<Map<Long, VendorScoreCard>> vendorCompanyScoreCoard =
                        taskExecutor.submit(new WMCallable<Map<Long, VendorScoreCard>>(webRequestContextProvider) {
                            @Override
                            public Map<Long, VendorScoreCard> apply() throws Exception {
                                return getVendorCompanyScoreCards(new ArrayList<>(vendorIds), currentUser.getCompanyId());
                            }
                        });

                    final Future<Map<Long, String>> vendorMembershipStatuses =
                        criteria.getGroupMembershipCriteria() == null ? null :
                            taskExecutor.submit(new WMCallable<Map<Long, String>>(webRequestContextProvider) {
                                @Override
                                public Map<Long, String> apply() throws Exception {
                                    Set<String> groupIds = getGroupIds(criteria.getGroupMembershipCriteria());
                                    return getVendorMembershipStatuses(groupIds, new ArrayList<>(vendorIds));
                                }
                            });

                    for (Worker worker : workers) {
                        Long userId = Long.valueOf(worker.getId());
                        if (!vendorIdsAndPositions.containsKey(userId)) {
                            continue;
                        }
                        // NOTE: when we reach here, it means userId is a vendor.
                        // If a vendor has no employees available to work, we should skip it.
                        // But this shouldn't happen as a vendor should have at least one employee available.
                        // There is a bug in company employee settings. Until it is fixed, we will keep the following check.
                        if (!workerLists.containsKey(userId) || workerLists.get(userId).size() == 0) {
                            logger.warn("vendor {} has no workers.", userId);
                            continue;
                        }
                        Map<String, Object> profile = profiles.get().get(userId);
                        if (profile == null) {
                            continue;
                        }
                        results.add(
                            toObjectMap(
                                worker,
                                SolrUserType.VENDOR,
                                vendorIdsAndPositions.get(userId),
                                profile,
                                avatars.get().get(userId),
                                vendorScoreCards.get().get(userId),
                                vendorCompanyScoreCoard.get().get(userId),
                                null,
                                null,
                                null,
                                vendorAssessment.get().get(userId),
                                vendorCertifications.get().get(userId),
                                vendorLicenses.get().get(userId),
                                vendorInsurances.get().get(userId),
                                vendorGroups.get().get(userId),
                                vendorDistances.get().get(userId),
                                vendorMembershipStatuses == null ? null : vendorMembershipStatuses.get().get(userId),
                                null,
                                false,
                                null));
                    }
                }

            }
        } catch (ExecutionException | InterruptedException e) {
            logger.warn("execution exception {}", e.getMessage());
        } finally {
            logger.info("Vendor hydration took {} ms", TimeUnit.NANOSECONDS.toMillis(timerContext.stop()));
        }

        return results;
    }

    public List<Map<String, Object>> hydrateVendors(final Collection<Long> vendorIds, final GeoPoint geoPoint, final ExtendedUserDetails currentUser) {

		DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
		if (CollectionUtils.isEmpty(vendorIds)) {
			return Lists.newArrayList();
		}

		final Map<Long, List<Long>> workerLists = getVendorWorkers(vendorIds);
		final Collection<Long> workerIds = flatten(workerLists.values());

		final Future<Map<Long, Map<String, Object>>> profiles =
			getProfilesFuture(vendorIds, PROFILE_SQL);
		final Future<Map<Long, String>> avatars = getAvatarsFuture(vendorIds, AVATAR_SQL);

		final Future<Map<Long, Double>> vendorDistances =
			getDistancesFuture(workerIds, geoPoint.getLatitude(), geoPoint.getLongitude(), ADDRESS_URL_SQL);

		final Future<Map<Long, VendorScoreCard>> vendorScoreCards =
			taskExecutor.submit(new WMCallable<Map<Long, VendorScoreCard>>(webRequestContextProvider) {
				@Override
				public Map<Long, VendorScoreCard> apply() throws Exception {
					return getVendorsScoreCards(new ArrayList<>(vendorIds));
				}
			});

		final Future<Map<Long, VendorScoreCard>> vendorCompanyScoreCards =
			taskExecutor.submit(new WMCallable<Map<Long, VendorScoreCard>>(webRequestContextProvider) {
				@Override
				public Map<Long, VendorScoreCard> apply() throws Exception {
					return getVendorCompanyScoreCards(new ArrayList<>(vendorIds), currentUser.getCompanyId());
				}
			});

		List<Map<String, Object>> results = Lists.newArrayList();
		try {
			for (Long vendorId : vendorIds) {
				Map<String, Object> profile = profiles.get().get(vendorId);
				if (profile == null) {
					continue;
				}
				final String city = profile.containsKey("city") ? StringUtilities.toPrettyName((String) profile.get("city")) : StringUtils.EMPTY;
				final String state = profile.containsKey("state") ? (String) profile.get("state") : StringUtils.EMPTY;
				final String country = profile.containsKey("country") ? (String) profile.get("country") : StringUtils.EMPTY;
				final String postalCode = profile.containsKey("postalCode") ? (String) profile.get("postalCode") : StringUtils.EMPTY;
				final Double latitude = profile.containsKey("latitude") ? ((BigDecimal) profile.get("latitude")).doubleValue() : null;
				final Double longitude = profile.containsKey("longitude") ? ((BigDecimal) profile.get("longitude")).doubleValue() : null;
				final Double distance = vendorDistances.get().get(vendorId);
				final VendorScoreCard vendorScoreCard = vendorScoreCards.get().get(vendorId);
				final VendorScoreCard vendorCompanyScoreCard = vendorCompanyScoreCards.get().get(vendorId);
				final ScoreCard.DateIntervalData companyWorkCompleted = vendorCompanyScoreCard.getValueForField(ResourceScoreField.COMPLETED_WORK);

				final ScoreCard.DateIntervalData ontimeReliability = vendorScoreCard.getValueForField(ResourceScoreField.ON_TIME_PERCENTAGE);
				final ScoreCard.DateIntervalData deliverableOnTimeReliability = vendorScoreCard.getValueForField(ResourceScoreField.DELIVERABLE_ON_TIME_PERCENTAGE);
				final ScoreCard.DateIntervalData workCompleted = vendorScoreCard.getValueForField(ResourceScoreField.COMPLETED_WORK);
				final ScoreCard.DateIntervalData abandonedWork = vendorScoreCard.getValueForField(ResourceScoreField.ABANDONED_WORK);
				final ScoreCard.DateIntervalData cancelledWork = vendorScoreCard.getValueForField(ResourceScoreField.CANCELLED_WORK);

				final ScoreCard.DateIntervalData satisfactionRate = vendorScoreCard.getValueForField(ResourceScoreField.SATISFACTION_OVER_ALL);

				int teamSize = workerLists.containsKey(vendorId) ? workerLists.get(vendorId).size() : 0;

				// There should be a large refactoring to use DTO instead of ObjectMap.
				results.add(CollectionUtilities.newObjectMap(
					"id", ((Integer) profile.get("id")).longValue(),
					"companyNumber", profile.get("number"),
					"address", formatAddress(city, state, postalCode),
					"avatar_asset_uri", avatars.get().get(vendorId),
					"city", city,
					"state", state,
					"country", country,
					"postal_code", postalCode,
					"company_name", profile.containsKey("companyName") ? profile.get("companyName") : StringUtils.EMPTY,
					"created_on", dateFormat.print(new DateTime(profile.get("createdOn"))),
					"latitude", latitude,
					"longitude", longitude,
					"distance", distance == null ? StringUtils.EMPTY : distance, //Double.toString(MathUtils.round(distance, 1)),
					"ontime_reliability", (ontimeReliability != null && ontimeReliability.getAll() != null)
						? BigDecimal.valueOf(ontimeReliability.getAll()).movePointRight(2).setScale(2, RoundingMode.HALF_UP) : 0,
					"rating", (satisfactionRate != null && satisfactionRate.getAll() != null)
						? BigDecimal.valueOf(satisfactionRate.getAll()).movePointRight(2).setScale(2, RoundingMode.HALF_UP) : 0,
					"teamSize", teamSize,
					"publicWorkers", teamSize,
					"work_cancelled_count", (cancelledWork != null && cancelledWork.getAll() != null) ? cancelledWork.getAll().intValue() : 0,
					"abandoned_count", (abandonedWork != null && abandonedWork.getAll() != null) ? abandonedWork.getAll().intValue() : 0,
					"work_completed_count", (workCompleted != null && workCompleted.getAll() != null) ? workCompleted.getAll().intValue() : 0,
					"work_completed_company_count",
					(companyWorkCompleted != null && companyWorkCompleted.getAll() != null) ? companyWorkCompleted.getAll().intValue() : 0,
					"deliverable_on_time_reliability", (deliverableOnTimeReliability != null && deliverableOnTimeReliability.getAll() != null)
						? BigDecimal.valueOf(deliverableOnTimeReliability.getAll()).movePointRight(2).setScale(2, RoundingMode.HALF_UP) : 0,
					"resource_scorecard", vendorScoreCard,
					"resource_scorecard_for_company", vendorCompanyScoreCard,
					"targeted", true,
					"declined", false,
					"assign_to_first_to_accept", false,
					"blocked", false));
			}
		} catch (Exception e) {
			logger.error("error hydrating vendors with requestContextId: {}, err: {}",  webRequestContextProvider.getRequestContext().getRequestId(), e.getMessage());
		}

		return results;
	}

    private Map<Long, List<Long>> getVendorWorkers(final Collection<Long> vendorIds) {
        Map<Long, List<Long>> workerLists = Maps.newHashMap();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("vendorIds", vendorIds);

        try {
            List<Map<String, Object>> results = readOnlyJdbcTemplate.queryForList(COMPANY_WORKER_SQL, params);
            for (Map<String, Object> result : results) {
                Long vendorId = ((Integer) result.get("company_Id")).longValue();
                if (!workerLists.containsKey(vendorId)) {
                    workerLists.put(vendorId, Lists.<Long>newArrayList());
                }
                workerLists.get(vendorId).add(((Integer) result.get("id")).longValue());
            }
        } catch (Throwable t) {
            logger.error("failed to retrieve public workers.", t);
        }
        return workerLists;
    }

    /**
     * Gets scorecards for a list of vendors
     * @param vendorIds a list of vendor ids
     * @return map of vendor id and its scorecard
     */
    private Map<Long, VendorScoreCard> getVendorsScoreCards(final List<Long> vendorIds) {
        try {
            return analyticsService.getVendorScoreCards(vendorIds);
        } catch (Throwable t) {
            logger.error("Failed to retrieve the resource scorecard.", t);
            return Maps.newHashMap();
        }
    }

    /**
     * Gets company scorecards for a list of vendors
     * @param vendorIds   a list of vendor ids
     * @param companyId the company id
     * @return map of vendor id and its company scorecard
     */
    private Map<Long, VendorScoreCard> getVendorCompanyScoreCards(final List<Long> vendorIds, final Long companyId) {
        try {
            return analyticsService.getVendorScoreCardsForCompany(companyId, vendorIds);
        } catch (Throwable t) {
            logger.error("Failed to retrieve the company scorecard.", t);
            return Maps.newHashMap();
        }
    }

    /**
     * Gets vendor group membership statuses for a list of user ids on a talent pool.
     *
     * @param groupIds
     * @param companyIds
     * @return
     */
    private Map<Long, String> getVendorMembershipStatuses(final Set<String> groupIds, final List<Long> companyIds) {
        try {
            Map<Long, String> groupVendorMembershipStatus = Maps.newHashMap();
            //There will only ever be one groupId. If there is ever a use case where we end up supporting multiple,
            //we can compose this map accordingly
            for (String groupId : groupIds) {
                groupVendorMembershipStatus =
                        talentPoolService.getVendorGroupMemberRequestTypeStatuses(Long.parseLong(groupId), companyIds);
            }
            return groupVendorMembershipStatus;
        } catch (Throwable t) {
            logger.error("Failed to retrieve user group vendor membership statuses',", t);
            return Maps.newHashMap();
        }
    }

	private static String formatAddress(String city, String state, String postalCode) {
		StringBuilder address = new StringBuilder();

		if (StringUtils.isNotEmpty(city)) {
			address.append(city);
		}

		if (StringUtils.isNotEmpty(state)) {
			if (address.length() > 0) {
				address.append(", ");
			}
			address.append(state);
		}

		if (StringUtils.isNotEmpty(postalCode)) {
			if (address.length() > 0) {
				address.append(" ");
			}
			address.append(postalCode);
		}

		return address.toString();
	}
}
