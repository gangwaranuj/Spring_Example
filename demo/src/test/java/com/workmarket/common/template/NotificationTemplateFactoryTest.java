package com.workmarket.common.template;

import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.velvetrope.dao.AdmissionDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificationTemplateFactoryTest {

	@Mock UserService userService;
	@Mock WorkService workService;
	@Mock CompanyService companyService;
	@Mock UserRoleService userRoleService;
	@Mock AdmissionDAO admissionDAO;
	@InjectMocks NotificationTemplateFactoryImpl notificationTemplateFactory = spy(new NotificationTemplateFactoryImpl());

	private static final Long
		WORKER_ID = 1L,
		WORK_ID = 2L;

	private static final String timeZoneString = "UTC";
	private static final String companyName = "companyName";

	private TimeZone timeZone;
	private Work work;
	private User user;
	private Company company;
	private Invitation invitation;

	@Before
	public void setup() {
		timeZone = mock(TimeZone.class);
		when(timeZone.getTimeZoneId()).thenReturn(timeZoneString);

		work = mock(Work.class);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getTimeZone()).thenReturn(timeZone);

		Map<String, Object> props = new HashMap<>();
		when(userService.getProjectionMapById(any(Long.class), any(String.class), any(String.class))).thenReturn(props);

		user = mock(User.class);
		company = mock(Company.class);
		invitation = mock(Invitation.class);

		when(user.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(1L);
		when(company.getEffectiveName()).thenReturn(companyName);
		when(company.isResourceAccount()).thenReturn(false);
		when(companyService.findCompanyById(anyLong())).thenReturn(company);
		when(invitation.getInvitationType()).thenReturn(InvitationType.EXCLUSIVE);
		when(invitation.getCompany()).thenReturn(company);
	}

	@Test
	public void buildAbstractWorkNegotiationDeclinedNotificationTemplate_workNegotiation_workNegotiationDeclinedNotificationTemplate() {
		WorkNegotiation workNegotiation = mock(WorkNegotiation.class);
		when(workNegotiation.getCreatorId()).thenReturn(WORKER_ID);

		notificationTemplateFactory.buildAbstractWorkNegotiationDeclinedNotificationTemplate(WORKER_ID, work, workNegotiation);

		verify(notificationTemplateFactory, times(1)).buildWorkNegotiationDeclinedNotificationTemplate(WORKER_ID, work, workNegotiation);
	}

	@Test
	public void buildAbstractWorkNegotiationDeclinedNotificationTemplate_workBudgetNegotiation_workBudgetNegotiationDeclinedNotificationTemplate() {
		WorkBudgetNegotiation workBudgetNegotiation = mock(WorkBudgetNegotiation.class);

		notificationTemplateFactory.buildAbstractWorkNegotiationDeclinedNotificationTemplate(WORKER_ID, work, workBudgetNegotiation);

		verify(notificationTemplateFactory, times(1)).buildWorkBudgetNegotiationDeclinedNotificationTemplate(WORKER_ID, work, workBudgetNegotiation);
	}

	@Test
	public void buildAbstractWorkNegotiationDeclinedNotificationTemplate_workExpenseNegotiation_workExpenseNegotiationDeclinedNotificationTemplate() {
		WorkExpenseNegotiation workExpenseNegotiation = mock(WorkExpenseNegotiation.class);

		notificationTemplateFactory.buildAbstractWorkNegotiationDeclinedNotificationTemplate(WORKER_ID, work, workExpenseNegotiation);

		verify(notificationTemplateFactory, times(1)).buildWorkExpenseNegotiationDeclinedNotificationTemplate(WORKER_ID, work, workExpenseNegotiation);
	}

	@Test
	public void buildAbstractWorkNegotiationDeclinedNotificationTemplate_workBonusNegotiation_workBonusNegotiationDeclinedNotificationTemplate() {
		WorkBonusNegotiation workBonusNegotiation = mock(WorkBonusNegotiation.class);

		notificationTemplateFactory.buildAbstractWorkNegotiationDeclinedNotificationTemplate(WORKER_ID, work, workBonusNegotiation);

		verify(notificationTemplateFactory, times(1)).buildWorkBonusNegotiationDeclinedNotificationTemplate(WORKER_ID, work, workBonusNegotiation);
	}

	@Test
	public void buildAbstractWorkNegotiationDeclinedNotificationTemplate_workRescheduleNegotiation_workRescheduleNegotiationDeclinedNotificationTemplate() {
		WorkRescheduleNegotiation workRescheduleNegotiation = mock(WorkRescheduleNegotiation.class);
		when(workService.findActiveWorkerId(any(Long.class))).thenReturn(WORKER_ID);

		notificationTemplateFactory.buildAbstractWorkNegotiationDeclinedNotificationTemplate(WORKER_ID, work, workRescheduleNegotiation);

		verify(notificationTemplateFactory, times(1)).buildWorkRescheduleNegotiationDeclinedNotificationTemplate(WORKER_ID, work, workRescheduleNegotiation);
	}

	@Test
	public void buildWelcomeNotificationTemplate_EmployeeWorker() {
		when(userRoleService.hasAnyAclRole(user, AclRole.ACL_EMPLOYEE_WORKER)).thenReturn(true);
		NotificationTemplate template = notificationTemplateFactory.buildWelcomeNotificationTemplate(user);
		Assert.assertTrue(template instanceof WelcomeEmployeeWorkerNotificationTemplate);
	}

	@Test
	public void buildWelcomeNotificationTemplate_Buyer() {
		NotificationTemplate template = notificationTemplateFactory.buildWelcomeNotificationTemplate(user);
		Assert.assertTrue(template instanceof WelcomeNotificationTemplate);
	}

	@Test
	public void buildWelcomeNotificationTemplate_ExclusiveWorker() {
		when(company.isResourceAccount()).thenReturn(true);
		when(user.getInvitation()).thenReturn(invitation);
		NotificationTemplate template = notificationTemplateFactory.buildWelcomeNotificationTemplate(user);
		Assert.assertTrue(template instanceof WelcomeExclusiveWorkerNotificationTemplate);
	}

	@Test
	public void buildWelcomeNotificationTemplate_Worker() {
		when(company.isResourceAccount()).thenReturn(true);
		NotificationTemplate template = notificationTemplateFactory.buildWelcomeNotificationTemplate(user);
		Assert.assertTrue(template instanceof WelcomeResourceNotificationTemplate);
	}
}
