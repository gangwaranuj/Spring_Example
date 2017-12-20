package com.workmarket.service.business.scheduler;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceNoShowAlertBackstopExecutorTest {

	@Mock UserNotificationService userNotificationService;
	@Mock WorkService workService;
	@Mock AuthenticationService authenticationService;
	@InjectMocks ResourceNoShowAlertBackstopExecutor resourceNoShowAlertBackstopExecutor;

	private List<Integer> workIds;
	private WorkResource workResource;
	private User user;

	@Before
	public void setUp() throws Exception {
		workIds = Lists.newArrayList(1,2,3,4,5);
		workResource = mock(WorkResource.class);
		user = mock(User.class);

		when(workService.findAssignmentsMissingResourceNoShow()).thenReturn(workIds);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(workResource.getUser()).thenReturn(user);
	}

	@Test
	public void execute() throws Exception {
		resourceNoShowAlertBackstopExecutor.execute();
		verify(workService, times(5)).findActiveWorkResource(anyLong());
		verify(userNotificationService, times(5)).onWorkResourceNotCheckedIn(any(WorkResource.class));
 	}
}
