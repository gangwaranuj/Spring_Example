package com.workmarket.service.business;

import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkNegotiationDAO;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiationPagination;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkNegotiationServiceImpl;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceDetailCache;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.GoogleCalendarService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.DateUtilities;
import com.workmarket.velvetrope.Doorman;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyMapOf;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkNegotiationServiceImplTest {

	@Mock UserService userService;
	@Mock AuthenticationService authenticationService;
	@Mock WorkService workService;
	@Mock WorkBundleService workBundleService;
	@Mock WorkAuditService workAuditService;
	@Mock WorkSubStatusService workSubStatusService;
	@Mock UserNotificationService userNotificationService;
	@Mock WorkValidationService workValidationService;
	@Mock WebHookEventService webHookEventService;
	@Mock GoogleCalendarService googleCalendarService;
	@Mock EventRouter eventRouter;
	@Mock WorkChangeLogService workChangeLogService;
	@Mock WorkNegotiationDAO workNegotiationDAO;
	@Mock UserDAO userDAO;
	@Mock WorkDAO workDAO;
	@Mock BaseWorkDAO abstractWorkDAO;
	@Mock WorkResourceDetailCache workResourceDetailCache;
	@Mock WorkResourceDAO workResourceDAO;
	@Mock WorkResourceService workResourceService;
	@Mock VendorService vendorService;
	@Mock Doorman doorman;
	@Mock ServiceMessageHelper messageHelper;
	@InjectMocks WorkNegotiationServiceImpl workNegotiationService;

	Company company;
	User currentUser;
	User onBehalfOfUser;
	User buyer;
	WorkRescheduleNegotiation negotiationSpy;
	Work work;
	WorkResource workResource;
	WorkNegotiationDTO workNegotiationDTO;
	ManageMyWorkMarket manageMyWorkMarket;

	private static final Long CURRENT_USER_ID = 1L;
	private static final Long ON_BEHALF_OF_USER_ID = 2L;
	private static final Long NEGOTIATION_ID = 3L;
	private static final Long WORK_ID = 4L;
	private static final Long COMPANY_ID = 5L;

	@Before
	public void init() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		company = mock(Company.class);
		company.setId(COMPANY_ID);
		when(company.isLocked()).thenReturn(Boolean.FALSE);

		currentUser = mock(User.class);
		when(currentUser.hasAclRole(AclRole.ACL_DEPUTY)).thenReturn(Boolean.TRUE);
		when(currentUser.getId()).thenReturn(CURRENT_USER_ID);
		when(currentUser.getCompany()).thenReturn(company);

		onBehalfOfUser = mock(User.class);
		when(onBehalfOfUser.hasAclRole(AclRole.ACL_DEPUTY)).thenReturn(Boolean.TRUE);
		when(onBehalfOfUser.getId()).thenReturn(ON_BEHALF_OF_USER_ID);

		buyer = mock(User.class);

		workResource = mock(WorkResource.class);

		workNegotiationDTO = mock(WorkNegotiationDTO.class);
		when(workNegotiationDTO.getOnBehalfOfId()).thenReturn(null);

		WorkRescheduleNegotiation negotiation = new WorkRescheduleNegotiation();
		negotiation.setId(NEGOTIATION_ID);
		negotiation.setScheduleFrom(DateUtilities.getCalendarNow());
		negotiation.setScheduleRangeFlag(Boolean.FALSE);
		negotiation.setModifierId(currentUser.getId());

		manageMyWorkMarket = mock(ManageMyWorkMarket.class);
		when(manageMyWorkMarket.getAssignToFirstResource()).thenReturn(false);

		work = mock(Work.class);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.SENT));
		when(work.getCompany()).thenReturn(company);
		when(work.getManageMyWorkMarket()).thenReturn(manageMyWorkMarket);
		when(work.getBuyer()).thenReturn(buyer);

		negotiation.setWork(work);

		negotiationSpy = spy(negotiation);

		when(workNegotiationDAO.findById(NEGOTIATION_ID)).thenReturn(negotiationSpy);
		when(authenticationService.getCurrentUser()).thenReturn(currentUser);
		when(userService.getUser(CURRENT_USER_ID)).thenReturn(currentUser);
		when(workService.isUserActiveResourceForWork(CURRENT_USER_ID, WORK_ID)).thenReturn(Boolean.FALSE);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(userDAO.get(ON_BEHALF_OF_USER_ID)).thenReturn(onBehalfOfUser);
		when(userDAO.get(CURRENT_USER_ID)).thenReturn(currentUser);
		when(workBundleService.isAssignmentBundle(work)).thenReturn(false);
		when(workService.isAuthorizedToAdminister(WORK_ID, CURRENT_USER_ID)).thenReturn(Boolean.TRUE);
		when(workService.isUserWorkResourceForWork(anyLong(), anyLong())).thenReturn(true);
		when(workDAO.get(WORK_ID)).thenReturn(work);
		when(abstractWorkDAO.get(WORK_ID)).thenReturn(work);
		Mockito.doNothing().when(workService).updateWorkProperties(anyLong(), anyMapOf(String.class, String.class));
		Mockito.doNothing().when(workSubStatusService).resolveSystemSubStatusByAction(anyLong(), anyString());
		Mockito.doNothing().when(workSubStatusService).resolveRequiresRescheduleSubStatus(anyLong(), anyLong());
		Mockito.doNothing().when(webHookEventService).onNegotiationApproved(anyLong(), anyLong(), any(AbstractWorkNegotiation.class), BigDecimal.valueOf(anyLong()));
		Mockito.doNothing().when(googleCalendarService).updateCalendarEventSchedule(anyLong());

		when(workNegotiationDAO.findLatestByUserForWork(anyLong(), anyLong())).thenReturn(mock(WorkNegotiation.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void approveNegotiation_nullNegotiationId() throws Exception {
		workNegotiationService.approveNegotiation(null);
	}

	@Test
	public void approveNegotiation_setWorkerCheckOut() throws Exception {
		workNegotiationService.approveNegotiation(NEGOTIATION_ID);

		verify(workResource).setCheckedIn(false);
	}

	@Test
	public void approveNegotiation_workerApproves() throws Exception {
		negotiationSpy.setInitiatedByResource(Boolean.TRUE);

		workNegotiationService.approveNegotiation(NEGOTIATION_ID);

		verify(workValidationService).validateApproveWorkNegotiation(NEGOTIATION_ID, null);
		verify(userDAO, never()).findUserById(anyLong());
		verify(negotiationSpy).setApprovedBy(currentUser);
	}

	@Test
	public void approveNegotiation_onBehalfOfApproval() throws Exception {
		negotiationSpy.setInitiatedByResource(Boolean.FALSE);
		when(userDAO.findUserById(ON_BEHALF_OF_USER_ID)).thenReturn(onBehalfOfUser);

		workNegotiationService.approveNegotiation(NEGOTIATION_ID, ON_BEHALF_OF_USER_ID);

		verify(userDAO).findUserById(ON_BEHALF_OF_USER_ID);
		verify(negotiationSpy).setApprovedBy(onBehalfOfUser);
	}

	@Test
	public void approveNegotiation_rescheduleToRange() throws Exception {
		Calendar today = DateUtilities.getCalendarNow();
		Calendar tomorrow = DateUtilities.getCalendarNow();

		tomorrow.add(Calendar.DAY_OF_MONTH, 1);

		negotiationSpy.setScheduleRangeFlag(Boolean.TRUE);
		negotiationSpy.setScheduleFrom(today);
		negotiationSpy.setScheduleThrough(tomorrow);

		when(userDAO.findUserById(ON_BEHALF_OF_USER_ID)).thenReturn(onBehalfOfUser);

		workNegotiationService.approveNegotiation(NEGOTIATION_ID, ON_BEHALF_OF_USER_ID);

		verify(userDAO).findUserById(ON_BEHALF_OF_USER_ID);
		verify(negotiationSpy).setApprovedBy(onBehalfOfUser);

		ArgumentCaptor<DateRange> argument = ArgumentCaptor.forClass(DateRange.class);
		verify(workResource).setAppointment(argument.capture());

		assertEquals(today, argument.getValue().getFrom());
		assertEquals(tomorrow, argument.getValue().getThrough());
	}

	@Test
	public void resolveSubStatusByAction_noUnresolvedSubstatuses() {
		workSubStatusService.addSystemSubStatus(negotiationSpy.getWork().getId(), "some code", "some note");
		workSubStatusService.resolveSystemSubStatusByAction(negotiationSpy.getWork().getId(), "some code");

		assertEquals(workSubStatusService.findAllUnResolvedSubStatuses(negotiationSpy.getWork().getId()).size(), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createApplyNegotiation_nullWorkId_throwException() throws Exception {
		workNegotiationService.createApplyNegotiation(null, CURRENT_USER_ID, workNegotiationDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createApplyNegotiation_nullUserId_throwException() throws Exception {
		workNegotiationService.createApplyNegotiation(WORK_ID, null, workNegotiationDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createApplyNegotiation_nullDTO_throwException() throws Exception {
		workNegotiationService.createApplyNegotiation(WORK_ID, CURRENT_USER_ID, null);
	}

	@Test
	public void createApplyNegotiation_setDispatcherId() throws Exception {
		workNegotiationService.createApplyNegotiation(WORK_ID, CURRENT_USER_ID, workNegotiationDTO);

		verify(workResourceService).setDispatcherForWorkAndWorker(WORK_ID, CURRENT_USER_ID);
	}
	@Test
	public void createApplyNegotiation_shouldOpenForWorkResource_userIsNotWorkResource_createOpenWorkResource() throws Exception {
		when(work.shouldOpenForWorkResource()).thenReturn(true);
		when(workService.isUserWorkResourceForWork(currentUser.getId(), work.getId())).thenReturn(false);

		workNegotiationService.createApplyNegotiation(WORK_ID, CURRENT_USER_ID, workNegotiationDTO);

		verify(workResourceDAO).createOpenWorkResource(work, currentUser, false, false);
	}

	@Test
	public void cancelPendingNegotiation_success() throws Exception {
		WorkNegotiationPagination pagination = new WorkNegotiationPagination();
		List<AbstractWorkNegotiation> results = new ArrayList<>(1);
		AbstractWorkNegotiation negotiation = mock(AbstractWorkNegotiation.class);
		when(negotiation.getWork()).thenReturn(work);
		results.add(negotiation);
		pagination.setResults(results);
		when(workNegotiationDAO.findByUserForWork(anyLong(), anyLong(), any(WorkNegotiationPagination.class))).thenReturn(pagination);
		when(workNegotiationDAO.get(anyLong())).thenReturn(negotiation);
		workNegotiationService.cancelPendingNegotiationsByUserForWork(currentUser.getId(), work.getId());

		verify(negotiation).setApprovalStatus(ApprovalStatus.REMOVED);

	}
}
