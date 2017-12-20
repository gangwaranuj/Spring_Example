package com.workmarket.service.business.scheduler;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by rahul on 6/2/14
 */
@RunWith(MockitoJUnitRunner.class)
public class DeliverableDueExecutorTest {

	@Mock UserNotificationService userNotificationService;
	@Mock WorkService workService;
	@Mock AuthenticationService authenticationService;
	@Mock DeliverableService deliverableService;
	@Mock WorkValidationService workValidationService;

	@InjectMocks DeliverableDueExecutor deliverableDueExecutor;

	private List<String> workNumbers;
	private List<ConstraintViolation> errors;
	private WorkResource workResource;
	private User user;

	@Before
	public void setUp() throws Exception {
		workNumbers = Lists.newArrayList("1", "2", "3", "4", "5");
		errors = Lists.newArrayList(new ConstraintViolation("key"));
		workResource = mock(WorkResource.class);
		user = mock(User.class);

		when(workService.findAssignmentsWithDeliverablesDue()).thenReturn(workNumbers);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(workResource.getUser()).thenReturn(user);
		when(workValidationService.validateDeliverableRequirements(anyBoolean(), anyString())).thenReturn(errors);
	}

	@Test
	public void execute_noAssignmentsFulfilledRequirements_sendNotifications() throws Exception {
		deliverableDueExecutor.execute();

		verify(workService, times(5)).findActiveWorkResource(anyLong());
		verify(workValidationService, times(5)).validateDeliverableRequirements(anyBoolean(), anyString());
		verify(deliverableService, times(5)).disableDeliverableDeadline(anyString());
		verify(userNotificationService, times(5)).onDeliverableLate(any(WorkResource.class));
	}

	@Test
	public void execute_allAssignmentsFulfilledRequirements_disableDeadlineAndSendNoNotifications() throws Exception {
		errors.clear();
		when(workValidationService.validateDeliverableRequirements(anyBoolean(), anyString())).thenReturn(errors);

		deliverableDueExecutor.execute();

		verify(deliverableService, times(5)).disableDeliverableDeadline(anyString());
		verify(userNotificationService, never()).onDeliverableLate(any(WorkResource.class));
	}
}
