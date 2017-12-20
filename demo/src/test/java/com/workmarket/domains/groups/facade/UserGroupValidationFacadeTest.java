package com.workmarket.domains.groups.facade;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.workmarket.domains.groups.model.ScheduledRun;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.service.business.ScheduledRunService;
import com.workmarket.service.business.UserGroupEvaluationScheduledRunService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.event.UserGroupValidationEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserGroupValidationFacadeTest {

	@Mock private MetricRegistry metricRegistry;
	@Mock private ScheduledRunService scheduledRunService;
	@Mock private UserGroupEvaluationScheduledRunService userGroupEvaluationScheduleService;
	@Mock private UserGroupService userGroupService;
	@Mock private UserGroupValidationService userGroupValidationService;
	@Mock private EventRouter eventRouter;

	@InjectMocks @Spy
	private UserGroupValidationFacadeImpl userGroupValidationFacade;

	@Test
	public void revalidateUserGroups_calls_revalidateUserGroup_forEachDueUserGroupId() {
		List<Long> userGroupIds = ImmutableList.of(1L, 2L, 3L, 4L, 5L);
		when(userGroupService.getDueForValidationUserGroupIds()).thenReturn(userGroupIds);
		when(userGroupEvaluationScheduleService.startScheduledRun(anyLong()))
			.thenReturn(Optional.of(mock(ScheduledRun.class)));

		userGroupValidationFacade.revalidateUserGroups();

		verify(eventRouter, times(5)).sendEvent(any(UserGroupValidationEvent.class));
	}

	@Test
	public void revalidateUserGroup_returnsEarly_ifNoScheduledRun() {
		when(userGroupEvaluationScheduleService.startScheduledRun(anyLong()))
			.thenReturn(Optional.<ScheduledRun>absent());

		userGroupValidationFacade.revalidateUserGroup(1L);

		verify(userGroupValidationService, never()).revalidateAllAssociations(anyLong());
	}
}
