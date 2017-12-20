package com.workmarket.service.business.scheduler;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeliverableDueReminderAlertTest {

	@Mock UserNotificationService userNotificationService;
	@Mock WorkService workService;
	@Mock AuthenticationService authenticationService;
	@Mock DeliverableService deliverableService;

	@InjectMocks DeliverableDueReminderAlertExecutor deliverableDueReminderAlertExecutor;

	private List<Work> workList;
	private List<ConstraintViolation> errors;
	private WorkResource workResource;
	private User user;

	@Before
	public void setUp() throws Exception {
		Work work = new Work();
		work.setId(1l);
		work.setWorkNumber("12345");
		workList = Lists.newArrayList(work);
		errors = Lists.newArrayList(new ConstraintViolation("key"));
		workResource = mock(WorkResource.class);
		user = mock(User.class);

		when(workService.findAssignmentsRequiringDeliverableDueReminder()).thenReturn(workList);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(workResource.getUser()).thenReturn(user);
	}

	@Test
	public void execute_sendReminderNotificationsAndDisableReminder() throws Exception {
		deliverableDueReminderAlertExecutor.execute();

		verify(workService, times(1)).findActiveWorkResource(anyLong());
		verify(deliverableService, times(1)).disableDeliverableReminder(anyString());
		verify(userNotificationService, times(1)).onDeliverableDueReminder(any(WorkResource.class));
	}
}
