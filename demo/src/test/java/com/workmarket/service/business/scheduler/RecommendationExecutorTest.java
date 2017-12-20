package com.workmarket.service.business.scheduler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.business.recommendation.gen.Messages;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToTalentPoolResponse;
import com.workmarket.business.recommendation.gen.Messages.Talent;
import com.workmarket.business.recommendation.gen.Messages.UserType;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.InviteToGroupFromRecommendationEvent;
import com.workmarket.service.business.recommendation.RecommendationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * tests for recommendation scheduler.
 */
@RunWith(MockitoJUnitRunner.class)
public class RecommendationExecutorTest {

	@Mock private UserService userService;
	@Mock private UserGroupService userGroupService;
	@Mock private NamedParameterJdbcTemplate readOnlyJdbcTemplate;
	@Mock private RecommendationService recommendationService;
	@Mock private EventRouter eventRouter;
	@Mock private EventFactory eventFactory;
	@Mock private UserGroup userGroup;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@InjectMocks private RecommendationExecutor recommendationExecutor;

	@Before
	public void setUp() throws Exception {
		when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
		doCallRealMethod().when(webRequestContextProvider).setWebRequestContext(any(WebRequestContext.class));
	}

	/**
	 * Executes recommendation but no recommendation if there are no suitable talent pools for recommendation.
	 *
	 * @throws Exception
	 */
	@Test
	public void executeNoTalentPoolToRecommend() throws Exception {
		final List<Map<String, Object>> noResults = Lists.newArrayList();
		when(readOnlyJdbcTemplate.queryForList(anyString(), any(MapSqlParameterSource.class))).thenReturn(noResults);

		recommendationExecutor.recommendTalentsToTalentPools();
		verifyZeroInteractions(recommendationService);
		verifyZeroInteractions(eventRouter);
	}

	/**
	 * Executes recommendation failed b/c specified talent pool is not indexed.
	 * This test is subject to change if we execute recommendation without using Solr.
	 *
	 * @throws Exception
	 */
	@Test
	public void executeNoTalentPoolFoundFromService() throws Exception {
		final Map<String, Object> talentPool =
			ImmutableMap.of("id", 12, "name", "talentpool", "routing_count", (Object) 2l);
		final List<Map<String, Object>> oneResult = Lists.newArrayList(talentPool);
		final RecommendTalentToTalentPoolResponse response =
			RecommendTalentToTalentPoolResponse.newBuilder()
				.setStatus(Messages.Status.newBuilder().setSuccess(false).build())
				.build();
		when(readOnlyJdbcTemplate.queryForList(anyString(), any(MapSqlParameterSource.class))).thenReturn(oneResult);
		when(recommendationService.recommendTalentForTalentPool(anyLong(), any(RequestContext.class)))
			.thenReturn(Lists.<Talent>newArrayList());
		recommendationExecutor.recommendTalentsToTalentPools();
		verify(recommendationService, times(1))
			.recommendTalentForTalentPool(anyLong(), any(RequestContext.class));
		verifyZeroInteractions(eventRouter);
	}

	/**
	 * Executes recommendation successfully.
	 *
	 * @throws Exception
	 */
	@Test
	public void executeRecommendationSuccess() throws Exception {
		final Long groupId = 12L;
		final String uuid = "user-uuid";
		final Long userId = 1234L;
		final List<Long> userIds = Lists.newArrayList(userId);
		final Map<String, Object> talentPool =
			ImmutableMap.of("id", 12, "name", "talentpool", "routing_count", (Object) 2l);
		final List<Map<String, Object>> oneResult = Lists.newArrayList(talentPool);
		final List<Talent> talents = Lists.newArrayList(
			Talent.newBuilder()
				.setUuid(uuid)
				.setUserType(UserType.WORKER)
				.build());
		when(readOnlyJdbcTemplate.queryForList(anyString(), any(MapSqlParameterSource.class))).thenReturn(oneResult);
		when(recommendationService.recommendTalentForTalentPool(anyLong(), any(RequestContext.class))).thenReturn(talents);
		when(userGroupService.findGroupById(anyLong())).thenReturn(userGroup);
		when(userGroup.getModifierId()).thenReturn(1L);
		when(userService.findUserIdByUuid(uuid)).thenReturn(userId);
		when(userGroupService.getEligibleUserIdsForInvitationToGroup(userIds, groupId)).thenReturn(userIds);

		recommendationExecutor.recommendTalentsToTalentPools();
		verify(recommendationService, times(1))
			.recommendTalentForTalentPool(anyLong(), any(RequestContext.class));
		verify(userGroupService, times(1)).findGroupById(groupId);
		verify(userService, times(1)).findUserIdByUuid(uuid);
		verify(eventRouter, times(1))
			.sendEvents(anyListOf(InviteToGroupFromRecommendationEvent.class));

	}
}
