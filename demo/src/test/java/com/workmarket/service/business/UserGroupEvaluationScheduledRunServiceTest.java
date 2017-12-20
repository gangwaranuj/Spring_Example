package com.workmarket.service.business;


import com.google.common.base.Optional;
import com.workmarket.domains.groups.dao.UserGroupEvaluationScheduledRunDAO;
import com.workmarket.domains.groups.model.ScheduledRun;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupEvaluationScheduledRun;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserGroupEvaluationScheduledRunServiceTest {

	@Mock private UserGroupService userGroupService;
	@Mock private ScheduledRunService scheduledRunService;
	@Mock private UserGroupEvaluationScheduledRunDAO userGroupEvaluationScheduledRunDAO;

	@InjectMocks private UserGroupEvaluationScheduledRunService userGroupEvaluationScheduledRunService =
		new UserGroupEvaluationScheduledRunServiceImpl();

	private UserGroup userGroup;

	private static final long USER_GROUP_ID = 1L;
	private static final int INTERVAL = 7;

	@Before
	public void setup() {
		userGroup = mock(UserGroup.class);
		when(userGroupService.findGroupById(anyLong())).thenReturn(userGroup);
	}

	@Test
	public void startScheduledRun_returnsEarly_withoutScheduledRun() {
		when(userGroupEvaluationScheduledRunDAO.findNextScheduledRunForActiveGroup(anyLong())).thenReturn(null);

		userGroupEvaluationScheduledRunService.startScheduledRun(USER_GROUP_ID);

		verify(scheduledRunService, never()).saveOrUpdate(any(ScheduledRun.class));
	}

	@Test
	public void startScheduledRun_updatesExistingScheduledRun_createsNewScheduledRun() {
		ScheduledRun scheduledRun = mock(ScheduledRun.class);
		when(scheduledRun.getNextRun()).thenReturn(DateUtilities.getCalendarNow());
		when(userGroupEvaluationScheduledRunDAO.findNextScheduledRunForActiveGroup(anyLong())).thenReturn(scheduledRun);

		Optional<ScheduledRun> nextSceduledRun = userGroupEvaluationScheduledRunService.startScheduledRun(USER_GROUP_ID);

		verify(scheduledRunService, times(1)).saveOrUpdate(scheduledRun);
		nextSceduledRun.get().getNextRun().after(scheduledRun.getNextRun());
	}

	@Test
	public void turnOnAutomaticEvaluation_returnsEarly_ifExists_ScheduledRun() {
		when(userGroupEvaluationScheduledRunDAO.findNextFutureScheduledRunForActiveOrInactiveGroup(anyLong())).thenReturn(mock(ScheduledRun.class));

		userGroupEvaluationScheduledRunService.turnOnAutomaticEvaluation(USER_GROUP_ID, INTERVAL);

		verify(scheduledRunService, never()).saveOrUpdate(any(ScheduledRun.class));
		verify(userGroupEvaluationScheduledRunDAO, never()).saveOrUpdate(any(UserGroupEvaluationScheduledRun.class));
	}

	@Test
	public void turnOnAutomaticEvaluation_createsScheduledRun() {
		when(userGroupEvaluationScheduledRunDAO.findNextFutureScheduledRunForActiveOrInactiveGroup(anyLong())).thenReturn(null);

		userGroupEvaluationScheduledRunService.turnOnAutomaticEvaluation(USER_GROUP_ID, INTERVAL);

		verify(scheduledRunService, times(1)).saveOrUpdate(any(ScheduledRun.class));
		verify(userGroupEvaluationScheduledRunDAO, times(1)).saveOrUpdate(any(UserGroupEvaluationScheduledRun.class));
	}

	@Test
	public void turnOffAutomaticEvaluation_returnsEarly_withoutScheduledRun() {
		when(userGroupEvaluationScheduledRunDAO.findNextFutureScheduledRunForActiveOrInactiveGroup(anyLong())).thenReturn(null);

		userGroupEvaluationScheduledRunService.turnOffAutomaticEvaluation(USER_GROUP_ID);

		verify(scheduledRunService, never()).saveOrUpdate(any(ScheduledRun.class));
	}

	@Test
	public void turnOffAutomaticEvaluation_updatesScheduledRun() {
		when(userGroupEvaluationScheduledRunDAO.findNextFutureScheduledRunForActiveOrInactiveGroup(anyLong())).thenReturn(mock(ScheduledRun.class));

		userGroupEvaluationScheduledRunService.turnOffAutomaticEvaluation(USER_GROUP_ID);

		verify(scheduledRunService, times(1)).saveOrUpdate(any(ScheduledRun.class));
	}

}
