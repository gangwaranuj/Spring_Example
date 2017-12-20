package com.workmarket.service.infra.interceptor;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkerSearchInterceptorTest {

	@Mock EventFactory eventFactory;
	@Mock EventRouter eventRouter;
	@Mock FeatureEvaluator featureEvaluator;
	@Mock UserService userService;
	@InjectMocks WorkerSearchInterceptor workerSearchInterceptor;

	private ProceedingJoinPoint proceedingJoinPoint;
   	private PeopleSearchResponse peopleSearchResponse;
	private AssignmentResourceSearchRequest assignmentResourceSearchRequest;
   	private PeopleSearchRequest peopleSearchRequest;
	private Company company;
	private User user;

	@Before
	public void setup() throws Throwable {
		proceedingJoinPoint = mock(ProceedingJoinPoint.class);
	   	peopleSearchResponse = mock(PeopleSearchResponse.class);
		assignmentResourceSearchRequest = mock(AssignmentResourceSearchRequest.class);
		peopleSearchRequest = mock(PeopleSearchRequest.class);
		user = mock(User.class);
		company = mock(Company.class);

		when(user.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(3L);
		when(proceedingJoinPoint.proceed()).thenReturn(peopleSearchResponse);
		when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{assignmentResourceSearchRequest});
		when(peopleSearchRequest.getUserId()).thenReturn(1L);
		when(userService.getUser(anyLong())).thenReturn(user);
		when(featureEvaluator.hasFeature(anyLong(), anyString())).thenReturn(true);
	}

	@Test
	public void testLogSearch_withNullRequest_doesNothing() throws Throwable {
		workerSearchInterceptor.logSearch(proceedingJoinPoint);
		verify(eventRouter, never()).sendEvent(any(Event.class));
	}

	@Test
	public void testLogSearch_success() throws Throwable {
		when(assignmentResourceSearchRequest.getRequest()).thenReturn(peopleSearchRequest);
		workerSearchInterceptor.logSearch(proceedingJoinPoint);
		verify(eventRouter, times(1)).sendEvent(any(Event.class));
	}
}
