package com.workmarket.domains.search.worker;

import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.model.analytics.ResourceScoreField;
import com.workmarket.domains.model.analytics.ScoreCard;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.helpers.WMCallable;
import com.workmarket.screening.model.Screening;
import com.workmarket.screening.model.ScreeningStatusCode;
import com.workmarket.search.worker.query.model.GroupMembershipCriteria;
import com.workmarket.search.worker.query.model.LocationCriteria;
import com.workmarket.search.worker.query.model.UserType;
import com.workmarket.search.worker.query.model.Worker;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.FileUtilities;
import com.workmarket.utility.GeoUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Abstract class for hydration.
 */
public class AbstractHydrator {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHydrator.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("MM/dd/yyyy");

    @Autowired private WebRequestContextProvider webRequestContextProvider;
    @Autowired
    @Resource(name = "readOnlyJdbcTemplate")
    NamedParameterJdbcTemplate readOnlyJdbcTemplate;

    @Autowired @Qualifier("taskExecutor") private ThreadPoolTaskExecutor taskExecutor;

    /**
     * Extracts userId and positions from worker list
     *
     * @param workers a list of workers
     * @return map of userid and its position in the worker list
     */
    Map<Long, Integer> getUserIdsAndPositions(final List<Worker> workers, final UserType userType) {
        Map<Long, Integer> idsAndPositions = Maps.newHashMap();
        int position = 0;
        for (Worker w : workers) {
            if (userType.getUserTypeCode() == w.getUserTypeCode()) {
                idsAndPositions.put(Long.valueOf(w.getId()), position);
            }
            position++;
        }
        return idsAndPositions;
    }

    boolean hasGeoPoint(final LocationCriteria location) {
        return location != null && location.getGeoPoint() != null
            && location.getGeoPoint().getLatitude() != null
            && location.getGeoPoint().getLatitude() != 0.0
            && location.getGeoPoint().getLongitude() != null
            && location.getGeoPoint().getLongitude() != 0.0;
    }

    /**
     * Given a list of ids and sql, fetch their LatLon coordinates and then compute distance.
     * SQL query expects to select id, latitude, longitude.
     *
     * @param ids a list of ids
     * @param latitude latitude of the work location
     * @param longitude longitude of the work location
     * @param addressSql sql query to fetch users' LatLon coordinates
     * @return Map
     */
    Map<Long, Double> getDistances(
        final Collection<Long> ids,
        final Double latitude,
        final Double longitude,
        final String addressSql) {

        // TODO [lu] refactor: split fetch LatLon and distance computation
        Map<Long, Double> distances = Maps.newHashMap();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);

        try {
            List<Map<String, Object>> results = readOnlyJdbcTemplate.queryForList(addressSql, params);
            for (Map<String, Object> result : results) {
                final Long userId = ((Integer) result.get("id")).longValue();
                final double lat = ((BigDecimal) result.get("latitude")).doubleValue();
                final double lon = ((BigDecimal) result.get("longitude")).doubleValue();
                if (lat == 0.0 || lon == 0.0) {
                    continue;
                }
                double unRoundedDistance = GeoUtilities.distanceInMiles(latitude, longitude, lat, lon);
                // if it is for vendor, the closest distance is used
                if (distances.containsKey(userId)) {
                    if (unRoundedDistance < distances.get(userId)) {
                        distances.put(userId, unRoundedDistance);
                    }
                } else {
                    distances.put(userId, unRoundedDistance);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve vendor certifications", e);
        }
        return distances;
    }

	Future<Map<Long, Double>> getDistancesFuture(
		final Collection<Long> ids,
		final Double latitude,
		final Double longitude,
		final String addressSql
	) {
		return taskExecutor.submit(new WMCallable<Map<Long, Double>>(webRequestContextProvider) {
			@Override public Map<Long, Double> apply() throws Exception {
				if (latitude != null && latitude != 0.0 && longitude != null && longitude != 0.0) {
					return getDistances(ids, latitude, longitude, addressSql);
				} else {
					return Collections.emptyMap();
				}
			}
		});
	}

	Future<Map<Long, Double>> getDistancesFuture(
		final Collection<Long> ids, final LocationCriteria location, final String addressSql) {
		return taskExecutor.submit(new WMCallable<Map<Long, Double>>(webRequestContextProvider) {
			@Override
			public Map<Long, Double> apply() throws Exception {
				if (hasGeoPoint(location)) {
					return getDistances(
						ids,
						location.getGeoPoint().getLatitude(),
						location.getGeoPoint().getLongitude(),
						addressSql);
				} else {
					return Collections.emptyMap();
				}
			}
		});
	}

    /**
     * Given a list of ids, get their attributes.
     * This method assumes that sql query select id and name of the attribute.
     *
     * @param ids a list of ids
     * @param attributeSql sql query to get user attributes
     * @return Map
     */
    Map<Long, List<String>> getAttributes(final Collection<Long> ids, final String attributeSql) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);
        return retrieveAttributes(attributeSql, params);
    }

    /**
     * Wraps getAttributes(ids, attributeSql) and returns a future.
     *
     * @param ids A list of ids
     * @param attributeSql attribute sql
     * @return Future
     */
    Future<Map<Long, List<String>>> getAttributesFuture(final Collection<Long> ids, final String attributeSql) {
        return taskExecutor.submit(new WMCallable<Map<Long, List<String>>>(webRequestContextProvider) {
            @Override
            public Map<Long, List<String>> apply() throws Exception {
                return getAttributes(ids, attributeSql);
            }
        });
    }

    /**
     * Given a list of ids and searcher's company id, get their attributes.
     * This method assumes that sql query select id and name of the attribute.
     *
     * @param ids a list of ids (user or vendor)
     * @param companyId the company id of the search
     * @param attributeSql sql query to get user attributes
     * @return Map
     */
    Map<Long, List<String>> getAttributes(
        final Collection<Long> ids, final Long companyId, final String attributeSql) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);
        params.addValue("companyId", companyId);
        return retrieveAttributes(attributeSql, params);
    }

    /**
     * Wraps getAttributes(ids, companyId, sql) and returns a future.
     *
     * @param ids A list of ids
     * @param companyId company id
     * @param attributeSql attribute sql
     * @return Future
     */
    Future<Map<Long, List<String>>> getAttributesFuture(
        final Collection<Long> ids, final Long companyId, final String attributeSql) {
        return taskExecutor.submit(new WMCallable<Map<Long, List<String>>>(webRequestContextProvider) {
            @Override
            public Map<Long, List<String>> apply() throws Exception {
                return getAttributes(ids, companyId, attributeSql);
            }
        });
    }

    /**
     * Given a list of ids (user or vendor), get their profile attributes.
     * Profile attributes may include:
     * id, uuid, number, firstName, lastName, createdOn, jobTitle
     * companyName, companyEffectiveName,
     * latitude, longitude, city, state, postalCode, country
     *
     * @param ids
     * @param profileSql
     * @return
     */
    Map<Long, Map<String, Object>> getProfiles(final Collection<Long> ids, final String profileSql) {
        Map<Long, Map<String, Object>> profiles = Maps.newHashMap();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);
        try {
            List<Map<String, Object>> results = readOnlyJdbcTemplate.queryForList(profileSql, params);
            for (Map<String, Object> result : results) {
                profiles.put(((Integer) result.get("id")).longValue(), result);
            }
        } catch (Exception e) {
            logger.error("failed to retrieve profile.", e);
        }
        return profiles;
    }

    /**
     * Wraps getProfiles and returns a future.
     *
     * @param ids A list of ids
     * @param profileSql profile sql
     * @return Future
     */
    Future<Map<Long, Map<String, Object>>> getProfilesFuture(final Collection<Long> ids, final String profileSql) {
        return taskExecutor.submit(new WMCallable<Map<Long, Map<String, Object>>>(webRequestContextProvider) {
            @Override
            public Map<Long, Map<String, Object>> apply() throws Exception {
                return getProfiles(ids, profileSql);
            }
        });
    }

    Set<Long> getBlockedUsers(final Long companyId, final Collection<Long> userids, final String sql) {
        Set<Long> blockedUserIds = Sets.newHashSet();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userids", userids);
        params.addValue("companyid", companyId);
        try {
            List<Map<String, Object>> results = readOnlyJdbcTemplate.queryForList(sql, params);
            for (Map<String, Object> result : results) {
                blockedUserIds.add(((Integer)result.get("blocked_user_id")).longValue());
            }
        } catch (Exception e) {
            logger.error("failed to retrieve blocked workers.", e);
        }
        return blockedUserIds;
    }

    Future<Set<Long>> getBlockedUsersFuture(final Long companyId, final Collection<Long> userIds, final String sql) {
        return taskExecutor.submit(new WMCallable<Set<Long>>(webRequestContextProvider) {
            @Override
            public Set<Long> apply() throws Exception {
                return getBlockedUsers(companyId, userIds, sql);
            }
        });
    }

    /**
     * Given a list of ids (user or vendor), get their avatar urls.
     * This method assumes that sql query select id and smallUrlPrefix and smallUuid.
     *
     * @param ids a list of ids either users' or companies'
     * @param avatarSql sql query to get user avatars
     * @return Map
     */
    Map<Long, String> getAvatars(final Collection<Long> ids, final String avatarSql) {
        Map<Long, String> avatars = Maps.newHashMap();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);

        try {
            List<Map<String, Object>> results = readOnlyJdbcTemplate.queryForList(avatarSql, params);
            for (Map<String, Object> result : results) {
                Long id = ((Integer) result.get("id")).longValue();
                if (!avatars.containsKey(id)) {
                    String avatarUri = buildAvatarUri(result);
                    if (avatarUri != null) {
                        avatars.put(id, avatarUri);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("failed to retrieve avatars.", e);
        }
        return avatars;
    }

    Set<String> getGroupIds(final GroupMembershipCriteria criteria) {
        final Set<String> groupIds = Sets.newHashSet();

        if (CollectionUtils.isNotEmpty(criteria.getTalentPoolMemberships())) {
            groupIds.addAll(criteria.getTalentPoolMemberships());
        }

        if (CollectionUtils.isNotEmpty(criteria.getTalentPoolMembershipOverrides())) {
            groupIds.addAll(criteria.getTalentPoolMembershipOverrides());
        }

        if (CollectionUtils.isNotEmpty(criteria.getPendingPassedTalentPools())) {
            groupIds.addAll(criteria.getPendingPassedTalentPools());
        }

        if (CollectionUtils.isNotEmpty(criteria.getPendingFailedTalentPools())) {
            groupIds.addAll(criteria.getPendingFailedTalentPools());
        }

        if (CollectionUtils.isNotEmpty(criteria.getInvitedTalentPools())) {
            groupIds.addAll(criteria.getInvitedTalentPools());
        }

        if (CollectionUtils.isNotEmpty(criteria.getDeclinedTalentPools())) {
            groupIds.addAll(criteria.getDeclinedTalentPools());
        }

        return groupIds;
    }

    /**
     * Wraps getAvatars and returns a future.
     *
     * @param ids A list of ids
     * @param avatarSql avatar sql
     * @return Future
     */
    Future<Map<Long, String>> getAvatarsFuture(final Collection<Long> ids, final String avatarSql) {
        return taskExecutor.submit(new WMCallable<Map<Long, String>>(webRequestContextProvider) {
            @Override
            public Map<Long, String> apply() throws Exception {
                return getAvatars(ids, avatarSql);
            }
        });
    }

    Map<String, Object> toObjectMap(
        final Worker worker,
        final SolrUserType userType,
        final Integer position,
        final Map<String, Object> profile,
        final String avatarUri,
        final ScoreCard<ResourceScoreField> userScorecard,
        final ScoreCard<ResourceScoreField> companyScorecard,
        final Screening backgroundCheck,
        final Screening drugTest,
        final LaneContext laneContext,
        final List<String> assessments,
        final List<String> certifications,
        final List<String> licenses,
        final List<String> insurances,
        final List<String> groups,
        final Double distance,
        final String groupMembershipStatus,
        final Eligibility eligibility,
        final boolean blockedByCompany,
        Set<Criterion> talentPoolCriterion
    ) {
        ScoreCard.DateIntervalData companyWorkCompleted = companyScorecard.getValueForField(ResourceScoreField.COMPLETED_WORK);

        ScoreCard.DateIntervalData ontimeReliability = userScorecard.getValueForField(ResourceScoreField.ON_TIME_PERCENTAGE);
        ScoreCard.DateIntervalData deliverableOnTimeReliability = userScorecard.getValueForField(ResourceScoreField.DELIVERABLE_ON_TIME_PERCENTAGE);
        ScoreCard.DateIntervalData workCompleted = userScorecard.getValueForField(ResourceScoreField.COMPLETED_WORK);
        ScoreCard.DateIntervalData abandonedWork = userScorecard.getValueForField(ResourceScoreField.ABANDONED_WORK);
        ScoreCard.DateIntervalData cancelledWork = userScorecard.getValueForField(ResourceScoreField.CANCELLED_WORK);

        ScoreCard.DateIntervalData satisfactionRate = userScorecard.getValueForField(ResourceScoreField.SATISFACTION_OVER_ALL);

        return CollectionUtilities.newObjectMap(
            "userNumber", profile.get("number"),
            "userType", userType.name(),
            "position", position,
            "first_name", profile.containsKey("firstName") ? StringUtilities.toPrettyName((String) profile.get("firstName")) : StringUtils.EMPTY,
            "last_name", profile.containsKey("lastName") ? StringUtilities.toPrettyName((String) profile.get("lastName")) : StringUtils.EMPTY,
            "email", profile.containsKey("email") ? profile.get("email") : StringUtils.EMPTY,
            "avatar_asset_uri", avatarUri,
            "job_title", profile.containsKey("jobTitle") ? profile.get("jobTitle") : StringUtils.EMPTY,
            "company_name", profile.containsKey("companyName") ? profile.get("companyName") : StringUtils.EMPTY,
            "city", profile.containsKey("city") ? StringUtilities.toPrettyName((String) profile.get("city")) : StringUtils.EMPTY,
            "state", profile.containsKey("state") ? profile.get("state") : StringUtils.EMPTY,
            "postal_code", profile.containsKey("postalCode") ? profile.get("postalCode") : StringUtils.EMPTY,
            "country", profile.containsKey("country") ? profile.get("country") : StringUtils.EMPTY,
            "lane", (laneContext != null && laneContext.getLaneType() != null) ? laneContext.getLaneType().getValue() : null,
            "background_check", backgroundCheck != null && backgroundCheck.getStatus() == ScreeningStatusCode.PASSED,
            "background_check_failed", backgroundCheck != null && backgroundCheck.getStatus() == ScreeningStatusCode.FAILED,
            "background_check_date", getScreeningDateStr(backgroundCheck),
            "drug_test", drugTest != null && drugTest.getStatus() == ScreeningStatusCode.PASSED,
            "drug_test_failed", drugTest != null && drugTest.getStatus() == ScreeningStatusCode.FAILED,
            "drug_test_date", getScreeningDateStr(drugTest),
            "certifications", certifications,
            "insurances", insurances,
            "licenses", licenses,
            "company_assessments", assessments,
            "groups", groups,
            "distance", distance == null ? StringUtils.EMPTY : Double.toString(MathUtils.round(distance, 1)),
            "derivedStatus", groupMembershipStatus == null ? StringUtils.EMPTY : groupMembershipStatus,
            "rating",
            (satisfactionRate != null && satisfactionRate.getAll() != null)
                    ? BigDecimal.valueOf(satisfactionRate.getAll()).movePointRight(2).setScale(2, RoundingMode.HALF_UP) : 0,
            "ontime_reliability",
            (ontimeReliability != null && ontimeReliability.getAll() != null)
                    ? BigDecimal.valueOf(ontimeReliability.getAll()).movePointRight(2).setScale(2, RoundingMode.HALF_UP) : 0,
            "deliverable_on_time_reliability",
            (deliverableOnTimeReliability != null && deliverableOnTimeReliability.getAll() != null)
                    ? BigDecimal.valueOf(deliverableOnTimeReliability.getAll()).movePointRight(2).setScale(2, RoundingMode.HALF_UP) : 0,
            "work_completed_count",
            (workCompleted != null && workCompleted.getAll() != null) ? workCompleted.getAll().intValue() : 0,
            "abandoned_count",
            (abandonedWork != null && abandonedWork.getAll() != null) ? abandonedWork.getAll().intValue() : 0,
            "work_completed_company_count",
            (companyWorkCompleted != null && companyWorkCompleted.getAll() != null) ? companyWorkCompleted.getAll().intValue() : 0,
            "work_cancelled_count",
            (cancelledWork != null && cancelledWork.getAll() != null) ? cancelledWork.getAll().intValue() : 0,
            "created_on", DATE_FORMAT.print(new DateTime(profile.get("createdOn"))),
            "snippets", worker.getSnippets(),
            "eligibility", eligibility,
            "blocked", blockedByCompany,
            "talentPoolCriterion", talentPoolCriterion
        );
    }

    private String buildAvatarUri(final Map<String, Object> avatarAssociation) {
        if (avatarAssociation.containsKey("smallUriPrefix") && avatarAssociation.containsKey("smallUuid")) {
            return FileUtilities.createRemoteFileandDirectoryStructor(
                (String) avatarAssociation.get("smallUriPrefix"), (String) avatarAssociation.get("smallUuid"));
        } else if (avatarAssociation.containsKey("origUriPrefix") && avatarAssociation.containsKey("origUuid")) {
            return FileUtilities.createRemoteFileandDirectoryStructor(
                (String) avatarAssociation.get("origUriPrefix"), (String) avatarAssociation.get("origUuid"));
        } else if (avatarAssociation.containsKey("largeUriPrefix") && avatarAssociation.containsKey("largeUuid")) {
            return FileUtilities.createRemoteFileandDirectoryStructor(
                (String) avatarAssociation.get("largeUriPrefix"), (String) avatarAssociation.get("largeUuid"));
        } else {
            return null;
        }
    }

    /**
     * Executes the sql query and extract id and attribute name into map.
     *
     * @param sql sql query
     * @param params parameters for sql query
     * @return Map
     */
    private Map<Long, List<String>> retrieveAttributes(final String sql, final MapSqlParameterSource params) {
        Map<Long, List<String>> attributes = Maps.newHashMap();
        try {
            List<Map<String, Object>> results = readOnlyJdbcTemplate.queryForList(sql, params);
            for (Map<String, Object> result : results) {
                final Long userId = ((Integer) result.get("id")).longValue();
                final String certName = (String) result.get("name");
                if (StringUtils.isNotEmpty(certName)) {
                    if (attributes.containsKey(userId)) {
                        attributes.get(userId).add(certName);
                    } else {
                        attributes.put(userId, Lists.newArrayList(certName));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve attributes: ", e);
        }
        return attributes;
    }

    private String getScreeningDateStr(final Screening screening) {
        return screening != null
            ? DATE_FORMAT.print((screening.getVendorResponseDate() != null ? screening.getVendorResponseDate() : screening.getModifiedOn()))
            : StringUtils.EMPTY;
    }
}
