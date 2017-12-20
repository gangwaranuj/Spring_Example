package com.workmarket.service.business.scheduler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.business.recommendation.gen.Messages.Talent;
import com.workmarket.business.recommendation.gen.Messages.UserType;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.id.IdGenerator;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.business.recommendation.RecommendationService;
import com.workmarket.service.web.WebRequestContextProvider;
import edu.emory.mathcs.backport.java.util.Collections;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Scheduled recommendations.
 */
@Service
@ManagedResource(
	objectName = "bean:name=recommendationExecutor",
	description = "scheduled recommendations")
public class RecommendationExecutor {
	private static final Logger logger = LoggerFactory.getLogger(RecommendationExecutor.class);

	private static final int NUMBER_OF_ROUTED_TALENT_POOLS = 2;
	private static final int NUMBER_OF_NEW_TALENT_POOLS = 2;
	private static final int MAX_RECOMMENDATIONS_PER_TALENT_POOL = 15;
	private static final String TALENT_POOL_SQL =
		"SELECT ug.id,ug.uuid,ug.name, COUNT(grsa.routing_strategy_id) AS routing_count FROM user_group ug\n"
			+ "LEFT JOIN group_routing_strategy_association grsa on grsa.user_group_id = ug.id\n"
			+ "WHERE ug.deleted = 0\n"
			+ "  AND ug.searchable = 1\n"
			+ "  AND ug.public = 1\n"
			+ "  AND ug.active_flag = 1\n"
			+ "  AND ug.auto_generated = 0\n"
			+ "  AND ug.company_id != 1\n"
			+ "  AND ug.created_on > :lastThreeMonths\n"
			+ "GROUP BY ug.id";

	@Autowired private RecommendationService recommendationService;
	@Autowired private IdGenerator idGenerator;
	@Autowired private UserService userService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	@ManagedOperation(description = "Recommends talents to talent pool")
	public void recommendTalentsToTalentPools() {
		logger.info("****** Running recommend talents to talentPools at " + new Date());

		final List<Long> talentPoolIds = findTalentPoolsForRecommendation();
		for (final Long talentPoolId : talentPoolIds) {
			final List<Talent> recommended = getRecommendationForTalentPool(talentPoolId);
			final List<String> invited = inviteTalentsToGroup(recommended, talentPoolId);
			logger.info("for talentPool {}: Recommended {}; invited {}", talentPoolId, recommended.size(), invited.size());
		}
	}

	/**
	 * Gets a list of talent pools for recommendation.
	 *
	 * @return
	 */
	private List<Long> findTalentPoolsForRecommendation() {
		final List<Long> routedGroups = Lists.newArrayList();
		final List<Long> newGroups = Lists.newArrayList();
		final List<Long> groupsForRecommendation = Lists.newArrayList();
		final MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lastThreeMonths", new DateTime().minusMonths(3).toDate());
		try {
			List<Map<String, Object>> results = readOnlyJdbcTemplate.queryForList(TALENT_POOL_SQL, params);
			for (Map<String, Object> result : results) {
				if (result.containsKey("routing_count")) {
					final long routingCount = (Long) result.get("routing_count");
					if (routingCount > 0) {
						routedGroups.add(((Integer) result.get("id")).longValue());
					} else {
						newGroups.add(((Integer) result.get("id")).longValue());
					}
				}
			}
			groupsForRecommendation.addAll(chooseRandomItemsFromList(routedGroups, NUMBER_OF_ROUTED_TALENT_POOLS));
			groupsForRecommendation.addAll(chooseRandomItemsFromList(newGroups, NUMBER_OF_NEW_TALENT_POOLS));
		} catch (Exception e) {
			logger.error("failed to fetch talent pool candidates for recommendation", e);
		}
		return groupsForRecommendation;
	}

	/**
	 * Fetches recommendations from recommendation service.
	 *
	 * @param talentPoolId
	 * @return
	 */
	private List<Talent> getRecommendationForTalentPool(final Long talentPoolId) {
		final List<Talent> talents =
			recommendationService.recommendTalentForTalentPool(talentPoolId, webRequestContextProvider.getRequestContext());
		if (talents.size() < MAX_RECOMMENDATIONS_PER_TALENT_POOL) {
			return talents;
		} else {
			return talents.subList(0, MAX_RECOMMENDATIONS_PER_TALENT_POOL);
		}
	}

	/**
	 * Invites talents to a group.
	 *
	 * @param talents
	 * @param talentPoolId
	 * @return A list of invited uuids.
	 */
	private List<String> inviteTalentsToGroup(final List<Talent> talents, final Long talentPoolId) {
		final List<String> workers = Lists.newArrayList();
		final List<String> vendors = Lists.newArrayList();
		final List<String> invited = Lists.newArrayList();
		final UserGroup userGroup = userGroupService.findGroupById(talentPoolId);
		final Long requestorId = userGroup != null && userGroup.getCreatorId() != null ? userGroup.getCreatorId() : 1L;
		for (final Talent talent : talents) {
			if (UserType.WORKER.equals(talent.getUserType())) {
				workers.add(talent.getUuid());
			} else if (UserType.VENDOR.equals(talent.getUserType())) {
				vendors.add(talent.getUuid());
			}
		}
		if (vendors.size() > 0) {
			logger.info("TODO: invite vendors to talent pool when talent pool service is available.");
		}
		if (workers.size() > 0) {
			final Map<Long, String> idMap = Maps.newHashMap();
			// TODO: we should have a bulk get
			for (final String workerUuid : workers) {
				final Long workerId = userService.findUserIdByUuid(workerUuid);
				if (workerId != null) {
					idMap.put(workerId, workerUuid);
				}
			}
			final List<Long> workerIds = Lists.newArrayList(idMap.keySet());
			final List<Long> eligibleWorkerIds =
				userGroupService.getEligibleUserIdsForInvitationToGroup(workerIds, talentPoolId);
			if (eligibleWorkerIds.size() > 0) {
				eventRouter.sendEvents(
					eventFactory.buildInviteToGroupFromRecommendationEvent(eligibleWorkerIds, talentPoolId, requestorId));
				for (Long invitedWorkerId : eligibleWorkerIds) {
					invited.add(idMap.get(invitedWorkerId));
				}
			} else {
				logger.info("No eligible talents for invite.");
			}
		}
		return invited;
	}

	private List<Long> chooseRandomItemsFromList(final List<Long> groupIds, final int numberOfItems) {
		if (groupIds.size() < numberOfItems) {
			return groupIds;
		} else {
			Collections.shuffle(groupIds);
			return groupIds.subList(0, numberOfItems);
		}
	}
}
