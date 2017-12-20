package com.workmarket.service.infra.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import com.workmarket.dao.LoginInfoDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.acl.AclRoleDAO;
import com.workmarket.dao.acl.PermissionDAO;
import com.workmarket.dao.acl.UserAclRoleAssociationDAO;
import com.workmarket.dao.changelog.user.UserChangeLogDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.UserAclRoleAssociation;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.search.cache.HydratorCache;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserNotificationPreferencePojo;
import com.workmarket.service.business.UserNotificationPrefsService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.security.SecurityContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

	@Mock SecurityContext securityContext;
	@Mock EventRouter eventRouter;
	@Mock EventFactory eventFactory;
	@Mock UserService userService;
	@Mock RequestService requestService;
	@Mock LaneService laneService;
	@Mock WorkService workService;
	@Mock UserIndexer userIndexer;
	@Mock UserDAO userDAO;
	@Mock LoginInfoDAO loginInfoDAO;
	@Mock AclRoleDAO aclRoleDAO;
	@Mock PermissionDAO permissionDAO;
	@Mock UserAclRoleAssociationDAO userAclRoleAssociationDAO;
	@Mock WorkDAO workDAO;
	@Mock UserChangeLogDAO userChangeLogDAO;
	@Mock UserNotificationPrefsService userNotificationPrefsService;
	@Mock RedisAdapter redisAdapter;
	@Mock HydratorCache hydratorCache;
	@Mock UserRoleService userRoleService;
	@InjectMocks AuthenticationService authenticationService;

	Work work;
	Company company;
	User supportContact, buyer;
	UserNotificationPreferencePojo preference;
	AclRole role;
	UserAclRoleAssociation roleAssociation;

	private final static Long BUYER_ID = 2L, SUPPORT_CONTACT_ID = 1L;

	@Before
	public void setUp() throws Exception {
		work = mock(Work.class);
		company = mock(Company.class);
		supportContact = mock(User.class);
		buyer = mock(User.class);
		preference = mock(UserNotificationPreferencePojo.class);
		role = mock(AclRole.class);
		roleAssociation = mock(UserAclRoleAssociation.class);
		Set<User> USER_LIST = Sets.newHashSet();
		USER_LIST.add(supportContact);

		when(work.getCompany()).thenReturn(company);
		when(work.getBuyer()).thenReturn(buyer);
		when(work.getBuyer().getId()).thenReturn(BUYER_ID);
		when(work.getBuyerSupportUser()).thenReturn(supportContact);
		when(work.getBuyerSupportUser().getId()).thenReturn(SUPPORT_CONTACT_ID);
		when(supportContact.hasAclRole(AclRole.ACL_DEPUTY)).thenReturn(Boolean.TRUE);
		when(preference.getEmailFlag()).thenReturn(true);
		when(userNotificationPrefsService.findByUserAndNotificationType(SUPPORT_CONTACT_ID, NotificationType.INVOICE_CREATED_ON_ASSIGNMENT)).thenReturn(preference);
		when(userNotificationPrefsService.findByUserAndNotificationType(BUYER_ID, NotificationType.INVOICE_CREATED_ON_ASSIGNMENT)).thenReturn(preference);
		when(userDAO.findAllUsersByACLRoleAndCompany(1L, 1L)).thenReturn(ImmutableList.copyOf(USER_LIST));
		when(userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.INVOICE_DUE_REMINDER_MY_ACCOUNT)).thenReturn(USER_LIST);
		when(userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.MONEY_WITHDRAWN)).thenReturn(USER_LIST);
		when(userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.LOCKED_INVOICE_DUE_REMINDER_MY_ACCOUNT)).thenReturn(USER_LIST);
		when(userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.INVOICE_DUE_24_HOURS)).thenReturn(USER_LIST);
		when(userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.INVOICE_CREATED_ON_ASSIGNMENT)).thenReturn(USER_LIST);
		when(userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.STATEMENT_REMINDER)).thenReturn(USER_LIST);
		when(userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.SUBSCRIPTION_REMINDER)).thenReturn(USER_LIST);
		when(userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.INVOICE_CREATED_ON_ASSIGNMENT)).thenReturn(USER_LIST);
		when(userDAO.get(1L)).thenReturn(buyer);
		when(aclRoleDAO.findRoleById(AclRole.ACL_SHARED_WORKER)).thenReturn(role);
		when(userService.getUser(1L)).thenReturn(buyer);
		when(userRoleService.findUserRoleAssociation(1L, AclRole.ACL_SHARED_WORKER)).thenReturn(roleAssociation);
	}

	@Test
	public void findAllAdminAndControllerUsersByCompanyId_returnsNotNull() throws Exception {
		Set<User> users = authenticationService.findAllAdminAndControllerUsersByCompanyId(1L);
		Assert.notEmpty(users);
	}

	@Test
	public void findAllUsersSubscribedToPastDueInvoices_returnsNotNull() throws Exception {
    Set<User> users = userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.INVOICE_DUE_REMINDER_MY_ACCOUNT);
		Assert.notEmpty(users);
	}

	@Test
	public void findAllUsersSubscribedToInvoice_returnsNotNull() throws Exception {
		Set<User> users = authenticationService.findAllUsersSubscribedToInvoice(1L, new SubscriptionInvoice());
		Assert.notEmpty(users);
	}

	@Test
	public void findAllUsersSubscribedToFundsDepositedWithdrawn_returnsNotNull() throws Exception {
    Set<User> users = userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.MONEY_WITHDRAWN);
		Assert.notEmpty(users);
	}

	@Test
	public void findAllUsersSubscribedToLockedPastDueInvoices_returnsNotNull() throws Exception {
    Set<User> users = userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.LOCKED_INVOICE_DUE_REMINDER_MY_ACCOUNT);
		Assert.notEmpty(users);
	}

	@Test
	public void findAllUsersSubscribedToLockedCompanyAccount24HrsDueInvoices_returnsNotNull() throws Exception {
    Set<User> users = userNotificationPrefsService.findUsersByCompanyAndNotificationType(1L, NotificationType.INVOICE_DUE_24_HOURS);
		Assert.notEmpty(users);
	}

	@Test
	public void findAllUsersSubscribedToNewAssignmentInvoices_returnsNotNull() throws Exception {
		Set<User> users = authenticationService.findAllUsersSubscribedToNewAssignmentInvoices(work);
		Assert.notEmpty(users);
	}

	@Test
	public void assignAclToUser_SharedWorkerIsApproved() throws Exception {
		authenticationService.assignAclRoleToUser(1L, AclRole.ACL_SHARED_WORKER);
		verify(buyer, times(1)).setLane3ApprovalStatus(ApprovalStatus.APPROVED);
	}

	@Test
	public void removeAclRoleFromUser_SharedWorkerIsOptsOut() throws Exception {
		authenticationService.removeAclRoleFromUser(1L, AclRole.ACL_SHARED_WORKER);
		verify(buyer, times(1)).setLane3ApprovalStatus(ApprovalStatus.OPT_OUT);
	}
}
