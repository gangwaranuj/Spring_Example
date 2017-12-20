package com.workmarket.service.thrift.work;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.RequestContext;
import com.workmarket.thrift.work.WorkResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkResponseContextBuilderTest {

	@Captor private ArgumentCaptor<Set<RequestContext>> captor;
	@Mock UserService userService;
	@Mock VendorService vendorService;
	@Mock WorkResourceService workResourceService;
	@Mock WorkService workService;
	@Mock private UserRoleService userRoleService;
	@InjectMocks WorkResponseContextBuilder workResponseContextBuilder = new WorkResponseContextBuilder();

	private static final Long
		WORKER_ID = 1L,
		WORK_ID = 2L,
		BUYER_ID = 3L,
		COMPANY1_ID = 4L,
		COMPANY2_ID = 5L;

	private AbstractWork work;
	private Company company1;
	private Company company2;
	private Permission permission;
	private PersonaPreference personaPreference;
	private User buyer;
	private User currentUser;
	private User employeeWorker;
	private WorkResource viewingResource;
	private WorkResource activeResource;
	private WorkResponse workResponse;

	@Before
	public void setup() {
		company1 = mock(Company.class);
		when(company1.getId()).thenReturn(COMPANY1_ID);

		company2 = mock(Company.class);
		when(company2.getId()).thenReturn(COMPANY2_ID);

		buyer = mock(User.class);
		when(buyer.getId()).thenReturn(BUYER_ID);
		when(buyer.getCompany()).thenReturn(company1);

		currentUser = mock(User.class);
		when(currentUser.getId()).thenReturn(WORKER_ID);
		when(currentUser.getCompany()).thenReturn(company2);

		employeeWorker = mock(User.class);
		when(employeeWorker.getId()).thenReturn(WORKER_ID);
		when(employeeWorker.getCompany()).thenReturn(company1);
		when(userService.isEmployeeWorker(employeeWorker)).thenReturn(true);

		work = mock(AbstractWork.class);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getBuyer()).thenReturn(buyer);
		when(work.getCompany()).thenReturn(company1);
		when(work.isSent()).thenReturn(true);
		when(work.shouldOpenForWorkResource(any(WorkResource.class))).thenReturn(false);

		viewingResource = mock(WorkResource.class);
		when(workService.findWorkResource(any(Long.class), any(Long.class))).thenReturn(viewingResource);

		activeResource = mock(WorkResource.class);
		when(workService.findActiveWorkResource(any(Long.class))).thenReturn(activeResource);

		permission = mock(Permission.class);

		personaPreference = mock(PersonaPreference.class);
		when(personaPreference.isDispatcher()).thenReturn(false);
		Optional<PersonaPreference> preference = Optional.of(personaPreference);
		when(userService.getPersonaPreference(any(Long.class))).thenReturn(preference);

		workResponse = mock(WorkResponse.class);
	}

	@Test
	public void ownerRequestContext_buyerAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.OWNER);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.BUYER);
	}

	@Test
	public void ownerRequestContext_adminAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.OWNER);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.ADMIN);
	}

	@Test
	public void companyOwnedRequestContext_and_viewAndManageyMyCompanyAssignmentsPermission_adminAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.COMPANY_OWNED);
		when(userRoleService.userHasPermission(any(User.class), eq(Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS)))
			.thenReturn(true);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.ADMIN);
	}

	@Test
	public void noRequestContext_and_viewAndManageyMyCompanyAssignmentsPermission_adminAuthorizationContext() {
		Set<RequestContext> resourceContexts = Collections.emptySet();
		when(userRoleService.userHasPermission(any(User.class), eq(Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS)))
			.thenReturn(true);
		resourceContextSet_buildAuthorizationContexts_notContains(resourceContexts, AuthorizationContext.ADMIN);
	}

	@Test
	public void companyOwnedRequestContext_and_not_viewAndManageyMyCompanyAssignmentsPermission_adminAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.COMPANY_OWNED);
		when(userRoleService.userHasPermission(any(User.class), eq(Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS)))
			.thenReturn(false);
		resourceContextSet_buildAuthorizationContexts_notContains(resourceContexts, AuthorizationContext.ADMIN);
	}

	@Test
	public void ownerRequestContext_and_payAssignmentPermission_payAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.OWNER);
		when(userRoleService.userHasPermission(any(User.class), eq(Permission.PAY_ASSIGNMENT)))
			.thenReturn(true);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.PAY);
	}

	@Test
	public void ownerRequestContext_and_noPayAssignmentPermission_payAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.OWNER);
		when(userRoleService.userHasPermission(any(User.class), eq(Permission.PAY_ASSIGNMENT)))
			.thenReturn(false);
		resourceContextSet_buildAuthorizationContexts_notContains(resourceContexts, AuthorizationContext.PAY);
	}

	@Test
	public void companyOwnedRequestContext_and_payAssignmentPermission_payAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.COMPANY_OWNED);
		when(userRoleService.userHasPermission(any(User.class), eq(Permission.PAY_ASSIGNMENT)))
			.thenReturn(true);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.PAY);
	}

	@Test
	public void companyOwnedRequestContext_and_noPayAssignmentPermission_payAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.COMPANY_OWNED);
		when(userRoleService.userHasPermission(any(User.class), eq(Permission.PAY_ASSIGNMENT)))
			.thenReturn(false);
		resourceContextSet_buildAuthorizationContexts_notContains(resourceContexts, AuthorizationContext.PAY);
	}

	@Test
	public void invitedRequestContext_resourceAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.INVITED);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.RESOURCE);
	}

	@Test
	public void invitedInactiveRequestContext_resourceAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.INVITED_INACTIVE);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.RESOURCE);
	}

	@Test
	public void activeResourceRequestContext_resourceAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.ACTIVE_RESOURCE);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.RESOURCE);
	}

	@Test
	public void declinedResourceRequestContextWorkIsSent_resourceAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.DECLINED_RESOURCE);
		when(work.isSent()).thenReturn(true);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.RESOURCE);
	}

	@Test
	public void declinedResourceRequestContextWorkNotSent_not_resourceAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.DECLINED_RESOURCE);
		when(work.isSent()).thenReturn(false);
		resourceContextSet_buildAuthorizationContexts_notContains(resourceContexts, AuthorizationContext.RESOURCE);
	}

	@Test
	public void noneRequestContextWorkSent_not_resourceAuthorizationContext() {
		Set<RequestContext> resourceContexts = Collections.emptySet();
		when(work.isSent()).thenReturn(true);
		resourceContextSet_buildAuthorizationContexts_notContains(resourceContexts, AuthorizationContext.RESOURCE);
	}

	@Test
	public void activeResourceRequestContext_activeResourceAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.ACTIVE_RESOURCE);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.ACTIVE_RESOURCE);
	}

	@Test
	public void dispatcherResourceRequestContext_dispatcherAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.DISPATCHER);
		activeResource = null;
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.DISPATCHER);
	}

	@Test
	public void dispatcherActiveResourceRequestContextSameCompany_resourceAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.DISPATCHER);
		when(activeResource.getUser()).thenReturn(currentUser);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.RESOURCE);
	}

	@Test
	public void dispatcherActiveResourceRequestContextSameCompany_activeResourceAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.DISPATCHER);
		when(activeResource.getUser()).thenReturn(currentUser);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.ACTIVE_RESOURCE);
	}

	@Test
	public void invitedInactiveResourceRequestContext_readOnlyAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.INVITED_INACTIVE);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.READ_ONLY);
	}

	@Test
	public void adminInvited_not_readOnlyAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.OWNER, RequestContext.INVITED_INACTIVE);
		resourceContextSet_buildAuthorizationContexts_notContains(resourceContexts, AuthorizationContext.READ_ONLY);
	}

	@Test
	public void cancelledResourceRequestContext_readOnlyAuthorizationContext() {
		Set<RequestContext> resourceContexts = ImmutableSet.of(RequestContext.CANCELLED_RESOURCE);
		resourceContextSet_buildAuthorizationContexts_contains(resourceContexts, AuthorizationContext.READ_ONLY);
	}

	@Test
	public void adminCancelled_not_readOnlyAuthorizationContext() {
		Set<RequestContext> requestContexts = ImmutableSet.of(RequestContext.OWNER, RequestContext.CANCELLED_RESOURCE);
		resourceContextSet_buildAuthorizationContexts_notContains(requestContexts, AuthorizationContext.READ_ONLY);
	}

	@Test
	public void dispatcherRequestContext_not_readOnlyAuthorizationContext() {
		Set<RequestContext> requestContexts = ImmutableSet.of(RequestContext.DISPATCHER, RequestContext.CANCELLED_RESOURCE);
		when(activeResource.getUser()).thenReturn(currentUser);
		resourceContextSet_buildAuthorizationContexts_notContains(requestContexts, AuthorizationContext.READ_ONLY);
	}

	@Test
	public void employeeWorker_workFeed_isInvited() {
		when(workService.findWorkResource(any(Long.class), any(Long.class))).thenReturn(null);
		when(workService.findActiveWorkResource(any(Long.class))).thenReturn(null);
		when(work.shouldOpenForWorkResource(any(WorkResource.class))).thenReturn(true);

		workResponseContextBuilder.buildContext(workResponse, employeeWorker, work);

		verify(workResponse).setRequestContexts(captor.capture());
		assertTrue(captor.getValue().containsAll(ImmutableSet.of(RequestContext.COMPANY_OWNED, RequestContext.INVITED)));
	}

	@Test
	public void employeeWorker_notWorkFeed_isNotInvited() {
		when(workService.findWorkResource(any(Long.class), any(Long.class))).thenReturn(null);
		when(workService.findActiveWorkResource(any(Long.class))).thenReturn(null);

		workResponseContextBuilder.buildContext(workResponse, employeeWorker, work);

		verify(workResponse).setRequestContexts(captor.capture());
		assertFalse(captor.getValue().contains(RequestContext.INVITED));
		assertTrue(captor.getValue().contains(RequestContext.COMPANY_OWNED));
	}

	private void resourceContextSet_buildAuthorizationContexts_notContains(Set<RequestContext> requestContexts, AuthorizationContext expectedAuthorizationContext) {
		when(workResponse.getRequestContexts()).thenReturn(requestContexts);
		Set<AuthorizationContext> resultAuthorizationContexts = workResponseContextBuilder.buildAuthorizationContexts(workResponse, currentUser, work, activeResource);
		assertFalse(resultAuthorizationContexts.contains(expectedAuthorizationContext));
	}

	private void resourceContextSet_buildAuthorizationContexts_contains(Set<RequestContext> requestContexts, AuthorizationContext expectedAuthorizationContext) {
		when(workResponse.getRequestContexts()).thenReturn(requestContexts);
		Set<AuthorizationContext> resultAuthorizationContexts = workResponseContextBuilder.buildAuthorizationContexts(workResponse, currentUser, work, activeResource);
		assertTrue(resultAuthorizationContexts.contains(expectedAuthorizationContext));
	}
}
