package com.workmarket.service.thrift;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.business.status.AcceptWorkStatus;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.service.thrift.transactional.work.WorkResponseBuilder;
import com.workmarket.thrift.work.AcceptWorkOfferRequest;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkActionResponse;
import com.workmarket.thrift.work.WorkActionResponseCodeType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TWorkProfileFacadeServiceTest {

	@Mock private TWorkService tWorkService;
	@Mock private WorkResponseBuilder workResponseBuilder;
	@Mock private AuthenticationService authenticationService;
	@Mock private UserService userService;
	@Mock private WorkService workService;
	@Mock private UserNotificationService userNotificationService;
	@Mock private EventRouter eventRouter;
	@Mock private WebHookEventService webHookEventService;
	@Mock private WorkResourceService workResourceService;
	@InjectMocks private TWorkFacadeService service = new TWorkFacadeServiceImpl();

	private static final long ACTIVE_WORK_ID = 1L;
	private static final long INACTIVE_WORK_ID = 4L;
	private static final long USER_ID = 2L;
	private static final long COMPANY_ID = 3L;
	private static final String ACTIVE_WORK_NUMBER = "123456789";
	private static final String INACTIVE_WORK_NUMBER = "987654321";

	@Before
	public void setUp() throws Exception {
		final ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);

		when(workService.findWork(longCaptor.capture())).thenAnswer(new Answer<Work>() {
			@Override public Work answer(InvocationOnMock invocationOnMock) throws Throwable {
				Work work = new Work();
				work.setId(longCaptor.getValue());
				return work;
			}
		});
		when(userService.findUserById(longCaptor.capture())).thenAnswer(new Answer<User>() {
			@Override public User answer(InvocationOnMock invocationOnMock) throws Throwable {
				User user = new User();
				user.setId(longCaptor.getValue());
				return user;
			}
		});
		when(workService.findActiveWorkResource(longCaptor.capture())).thenAnswer(new Answer<WorkResource>() {
			@Override public WorkResource answer(InvocationOnMock invocationOnMock) throws Throwable {
				WorkResource resource = new WorkResource();
				Work work = new Work();
				work.setId(longCaptor.getValue());
				resource.setWork(work);
				User user = new User();
				user.setId(USER_ID);
				resource.setUser(user);
				return resource;
			}
		});
		when(workService.findWorkByWorkNumber(ACTIVE_WORK_NUMBER)).thenAnswer(new Answer<Work>() {
			@Override public Work answer(InvocationOnMock invocationOnMock) throws Throwable {
				Work work = new Work();
				work.setId(ACTIVE_WORK_ID);
				work.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE));
				Company company = new Company();
				company.setId(COMPANY_ID);
				work.setCompany(company);
				return work;
			}
		});
		when(workService.findWorkByWorkNumber(INACTIVE_WORK_NUMBER)).thenAnswer(new Answer<Work>() {
			@Override public Work answer(InvocationOnMock invocationOnMock) throws Throwable {
				Work work = new Work();
				work.setId(ACTIVE_WORK_ID);
				work.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT));
				Company company = new Company();
				company.setId(COMPANY_ID);
				work.setCompany(company);
				return work;
			}
		});
	}

	@Test
	public void acceptWork_FailedResponse_NotificationNotSent() {
		when(workService.acceptWork(anyLong(), anyLong()))
				.thenReturn(new AcceptWorkResponse(AcceptWorkStatus.FAILURE));

		service.acceptWork(USER_ID, ACTIVE_WORK_ID);

		verify(userNotificationService, never()).onWorkAccepted(eq(ACTIVE_WORK_ID), anyLong());
	}

	@Test
	public void acceptWork_FailedResponse_ReindexEventNotSent() {
		when(workService.acceptWork(anyLong(), anyLong()))
				.thenReturn(new AcceptWorkResponse(AcceptWorkStatus.FAILURE));

		service.acceptWork(USER_ID, ACTIVE_WORK_ID);

		verify(eventRouter, never()).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void acceptWork_FailedResponse_WebhookNotSent() {
		when(workService.acceptWork(anyLong(), anyLong()))
				.thenReturn(new AcceptWorkResponse(AcceptWorkStatus.FAILURE));

		service.acceptWork(USER_ID, ACTIVE_WORK_ID);

		verify(webHookEventService, never()).onWorkAccepted(eq(ACTIVE_WORK_ID), anyLong(), anyLong());
	}

	@Test
	public void acceptWork_SuccessfulResponse_NotificationSent() {
		final Work work = new Work();
		work.setId(ACTIVE_WORK_ID);
		when(workService.acceptWork(anyLong(), anyLong()))
				.thenReturn(new AcceptWorkResponse(AcceptWorkStatus.SUCCESS)
						.setWork(work));

		service.acceptWork(USER_ID, ACTIVE_WORK_ID);

		verify(userNotificationService, times(1)).onWorkAccepted(eq(ACTIVE_WORK_ID), anyLong());
	}

	@Test
	public void acceptWork_SuccessfulResponse_ReindexEventSent() {
		final Work work = new Work();
		work.setId(ACTIVE_WORK_ID);
		when(workService.acceptWork(anyLong(), anyLong()))
				.thenReturn(new AcceptWorkResponse(AcceptWorkStatus.SUCCESS)
						.setWork(work));

		service.acceptWork(USER_ID, ACTIVE_WORK_ID);
	}

	@Test
	public void acceptWork_SuccessfulResponseAndActiveWork_WebhookSent() {
		final Work work = new Work();
		work.setId(ACTIVE_WORK_ID);
		work.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.ACTIVE));
		Company company = new Company();
		company.setId(COMPANY_ID);
		work.setCompany(company);

		when(workService.acceptWork(anyLong(), anyLong()))
				.thenReturn(new AcceptWorkResponse(AcceptWorkStatus.SUCCESS)
						.setWork(work));

		service.acceptWork(USER_ID, ACTIVE_WORK_ID);

		verify(webHookEventService, times(1)).onWorkAccepted(eq(ACTIVE_WORK_ID), anyLong(), anyLong());
	}

	@Test
	public void acceptWork_SuccessfulResponseAndNonActiveWork_WebhookNotSent() {
		final Work work = new Work();
		work.setId(INACTIVE_WORK_ID);
		work.setWorkStatusType(WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT));
		when(workService.acceptWork(anyLong(), anyLong()))
				.thenReturn(new AcceptWorkResponse(AcceptWorkStatus.SUCCESS)
						.setWork(work));

		service.acceptWork(USER_ID, INACTIVE_WORK_ID);

		verify(webHookEventService, never()).onWorkAccepted(eq(INACTIVE_WORK_ID), anyLong(), anyLong());
	}

	@Test
	public void acceptWorkOnBehalf_FailedResponse_NotificationNotSent() {
		try {
			when(workService.acceptWorkOnBehalf(any(AcceptWorkOfferRequest.class)))
					.thenReturn(new WorkActionResponse(WorkActionResponseCodeType.INVALID_REQUEST));

			service.acceptWorkOnBehalf(new AcceptWorkOfferRequest()
					.setWorkAction(new WorkActionRequest().setWorkNumber(ACTIVE_WORK_NUMBER)));

		} catch (WorkActionException e) {
			Assert.fail();
		}

		verify(userNotificationService, never()).onWorkAccepted(eq(ACTIVE_WORK_ID), anyLong());
	}

	@Test
	public void acceptWorkOnBehalf_FailedResponse_ReindexEventNotSent() {

		try {
			when(workService.acceptWorkOnBehalf(any(AcceptWorkOfferRequest.class)))
					.thenReturn(new WorkActionResponse(WorkActionResponseCodeType.INVALID_REQUEST));

			service.acceptWorkOnBehalf(new AcceptWorkOfferRequest()
					.setWorkAction(new WorkActionRequest(ACTIVE_WORK_NUMBER)));

		} catch (WorkActionException e) {
			Assert.fail();
		}

		verify(eventRouter, never()).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void acceptWorkOnBehalf_FailedResponse_WebhookNotSent() {
		try {
			when(workService.acceptWorkOnBehalf(any(AcceptWorkOfferRequest.class)))
					.thenReturn(new WorkActionResponse(WorkActionResponseCodeType.INVALID_REQUEST));

			service.acceptWorkOnBehalf(new AcceptWorkOfferRequest()
					.setWorkAction(new WorkActionRequest(ACTIVE_WORK_NUMBER)));

		} catch (WorkActionException e) {
			Assert.fail();
		}

		verify(webHookEventService, never()).onWorkAccepted(eq(ACTIVE_WORK_ID), anyLong(), anyLong());
	}


	@Test
	public void acceptWorkOnBehalf_SuccessfulResponse_NotificationSent() {
		try {
			when(workService.acceptWorkOnBehalf(any(AcceptWorkOfferRequest.class)))
					.thenReturn(new WorkActionResponse(WorkActionResponseCodeType.SUCCESS));

			service.acceptWorkOnBehalf(new AcceptWorkOfferRequest()
					.setWorkAction(new WorkActionRequest(ACTIVE_WORK_NUMBER)));

		} catch (WorkActionException e) {
			Assert.fail();
		}

		verify(userNotificationService, times(1)).onWorkAccepted(eq(ACTIVE_WORK_ID), anyLong());
	}

	@Test
	public void acceptWorkOnBehalf_SuccessfulResponse_ReindexEventSent() {
		try {
			when(workService.acceptWorkOnBehalf(any(AcceptWorkOfferRequest.class)))
					.thenReturn(new WorkActionResponse(WorkActionResponseCodeType.SUCCESS));

			service.acceptWorkOnBehalf(new AcceptWorkOfferRequest()
					.setWorkAction(new WorkActionRequest(ACTIVE_WORK_NUMBER)));

		} catch (WorkActionException e) {
			Assert.fail();
		}
	}

	@Test
	public void acceptWorkOnBehalf_SuccessfulResponseAndActiveWork_WebhookSent() {
		try {
			when(workService.acceptWorkOnBehalf(any(AcceptWorkOfferRequest.class)))
					.thenReturn(new WorkActionResponse(WorkActionResponseCodeType.SUCCESS));

			service.acceptWorkOnBehalf(new AcceptWorkOfferRequest()
					.setWorkAction(new WorkActionRequest(ACTIVE_WORK_NUMBER)));

		} catch (WorkActionException e) {
			Assert.fail();
		}

		verify(webHookEventService, times(1)).onWorkAccepted(eq(ACTIVE_WORK_ID), anyLong(), anyLong());
	}

	@Test
	public void acceptWorkOnBehalf_SuccessfulResponseAndNonActiveWork_WebhookNotSent() {
		try {
			when(workService.acceptWorkOnBehalf(any(AcceptWorkOfferRequest.class)))
					.thenReturn(new WorkActionResponse(WorkActionResponseCodeType.SUCCESS));

			service.acceptWorkOnBehalf(new AcceptWorkOfferRequest()
					.setWorkAction(new WorkActionRequest(INACTIVE_WORK_NUMBER)));

		} catch (WorkActionException e) {
			Assert.fail();
		}

		verify(webHookEventService, never()).onWorkAccepted(eq(INACTIVE_WORK_ID), anyLong(), anyLong());
	}

}
