package com.workmarket.service.infra.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.acl.AclClient;
import com.workmarket.acl.gen.Protos.AddPrivsToGroupRequest;
import com.workmarket.acl.gen.Protos.CreateGroupRequest;
import com.workmarket.acl.gen.Protos.MutationResponse;
import com.workmarket.acl.gen.Protos.RemovePrivsFromGroupRequest;
import com.workmarket.acl.gen.Protos.StatusCode;
import com.workmarket.common.core.RequestContext;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.acl.PermissionDAO;
import com.workmarket.dao.acl.UserAclRoleAssociationDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.RoleType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.acl.UserAclRoleAssociation;
import com.workmarket.domains.model.acl.UserCustomPermissionAssociation;
import com.workmarket.service.web.WebRequestContextProvider;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for user role service.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserRoleServiceTest {
	@Mock private MetricRegistry facade;
	@Mock private Meter meter;
	@Mock private PermissionDAO permissionDAO;
	@Mock private UserAclRoleAssociationDAO userAclRoleAssociationDAO;
	@Mock private User user;
	@Mock private Company company;
	@Mock private AclRole aclRole;
	@Mock private FeatureEvaluator featureEvaluator;
	@Mock private UserDAO userDAO;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@Mock private AclClient aclClient;

	@InjectMocks
	private UserRoleService service;
	private static final ImmutableSet<RoleType> SET_OF_ROLES = ImmutableSet.of(new RoleType("Role"));

	@Before
	public void setUp() {
		when(user.getUuid()).thenReturn("USER_UUID");
		when(user.getCompany()).thenReturn(company);
		when(company.getUuid()).thenReturn("COMPANY_UUID");
		when(facade.meter((String) Mockito.anyObject())).thenReturn(meter);
		when(userDAO.get(Mockito.anyLong())).thenReturn(user);
		when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
		when(webRequestContextProvider.getWebRequestContext(any(String.class), any(String.class))).thenCallRealMethod();
		when(webRequestContextProvider.getRequestContext()).thenCallRealMethod();
		webRequestContextProvider.getWebRequestContext("REQUEST_ID", "TENANT_ID");
		webRequestContextProvider.getWebRequestContext().setUserUuid("USER_UUID");
		service.init();
		service.setClient(aclClient);
	}

	@Test
	public void getRoles() {
		when(user.getRoles()).thenReturn(SET_OF_ROLES);
		final Set<RoleType> actual = service.getUserRoles(user);
		assertEquals(SET_OF_ROLES, actual);
	}

	@Test
	public void setRoles() {
		service.setRoles(user, SET_OF_ROLES);
		final ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
		verify(user).setRoles((Set<RoleType>) captor.capture());
		assertEquals(SET_OF_ROLES, captor.getValue());
	}

	@Test
	public void addRoles() {
		final Set<RoleType> roles = new HashSet<>();
		roles.add(new RoleType("Role"));

		when(user.getRoles()).thenReturn(roles);
		service.addRoles(user, new String[]{"newRole"});

		assertEquals(ImmutableSet.of(new RoleType("Role"), new RoleType("newRole")), roles);
	}

	@Test
	public void removeRoles() {
		final Set<RoleType> roles = new HashSet<>();
		roles.add(new RoleType("Role"));
		when(user.getRoles()).thenReturn(roles);
		service.removeRoles(user, new String[]{"Role"});
		assertEquals(ImmutableSet.of(), roles);
	}

	@Test
	public void hasRole() {
		when(user.hasRole("Role")).thenReturn(true);
		assertTrue(service.hasRole(user, "Role"));
	}

	@Test
	public void hasAnyAclRole() {
		when(user.hasAclRole(1L)).thenReturn(true);
		assertTrue(service.hasAnyAclRole(user, 1L, 2L, 3L));
		assertFalse(service.hasAnyAclRole(user, 2L, 3L));
	}

	@Test
	public void isInternal() {
		when(user.isInternalUser()).thenReturn(true);
		assertTrue(service.isInternalUser(user));
	}

	@Test
	public void isDispatcher() {
		when(user.isDispatcher()).thenReturn(true);
		assertTrue(service.isDispatcher(user));
	}

	@Test
	public void isController() {
		when(user.isController()).thenReturn(true);
		assertTrue(service.isController(user));
	}

	@Test
	public void isAdminOrManager() {
		when(user.isAdminOrManager()).thenReturn(true);
		assertTrue(service.isAdminOrManager(user));
	}

	@Test
	public void isAdmin() {
		when(user.isAdmin()).thenReturn(true);
		assertTrue(service.isAdmin(user));
	}

	@Test
	public void getWorkStatus() {
		when(user.getWorkStatus()).thenReturn(User.WorkStatus.PUBLIC);
		assertEquals(User.WorkStatus.PUBLIC, service.getWorkStatus(user));
	}

	@Test
	public void getUserRoleAssociations() {
		final ImmutableSet<UserAclRoleAssociation> expected = ImmutableSet.of(
			new UserAclRoleAssociation(user, aclRole));
		when(user.getUserRoleAssociations()).thenReturn(expected);
		assertEquals(expected, service.getUserRoleAssociations(user));
	}

	@Test
	public void addUserRoleAssociation_relationFound() throws Exception {
		when(user.getId()).thenReturn(1L);
		when(aclRole.getId()).thenReturn(1L);
		final UserAclRoleAssociation uara = mock(UserAclRoleAssociation.class);
		when(userAclRoleAssociationDAO.findUserRoleAssociation(1L, 1L))
			.thenReturn(uara);
		service.addUserRoleAssociation(user, aclRole);
		verify(uara).setDeleted(Boolean.FALSE);
	}

	@Test
	public void addUserRoleAssociation_newAssociation() throws Exception {
		when(user.getId()).thenReturn(1L);
		when(aclRole.getId()).thenReturn(1L);
		when(userAclRoleAssociationDAO.findUserRoleAssociation(1L, 1L))
			.thenReturn(null);
		final Set<UserAclRoleAssociation> roles = new HashSet<>();
		when(user.getUserRoleAssociations()).thenReturn(roles);
		service.addUserRoleAssociation(user, aclRole);
		verify(userAclRoleAssociationDAO).saveOrUpdate((UserAclRoleAssociation) anyObject());
		assertEquals(1, roles.size());
	}

	@Test
	public void findAllRolesByUser() {
		final List<UserAclRoleAssociation> roles = ImmutableList.of(
				new UserAclRoleAssociation(user, aclRole));
		when(userAclRoleAssociationDAO.findAllRolesByUser(1L, true)).thenReturn(roles);
		assertEquals(roles, service.findAllRolesByUser(1L, true));
	}

	@Test
	public void findUserRoleAssociation() {
		when(user.getId()).thenReturn(1L);
		final UserAclRoleAssociation role = new UserAclRoleAssociation(user, aclRole);
		when(userAclRoleAssociationDAO.findUserRoleAssociation(1L, 1L)).thenReturn(role);
		final UserAclRoleAssociation found = service.findUserRoleAssociation(1L, 1L);
		assertEquals(role.getUser().getId(), found.getUser().getId());
		assertEquals(role.getRole().getName(), found.getRole().getName());
	}

	@Test
	public void removeAclRoleAssociation() {
		final UserAclRoleAssociation role = mock(UserAclRoleAssociation.class);
		service.removeAclRoleAssociation(user, role);
		verify(role).setDeleted(true);
	}

	@Test
	public void userHasPermisssion() {
		when(user.getId()).thenReturn(1L);
		when(permissionDAO.findPermissionByUserAndPermissionCode(1L, "permission"))
			.thenReturn(new Permission("permission"));
		assertTrue(service.userHasPermission(user, "permission"));
	}

	@Test
	public void findPermissionByCode() {
		final Permission permission = new Permission("permission");
		when(permissionDAO.findPermissionByCode("permission"))
			.thenReturn(permission);
		assertEquals(permission, service.findPermissionByCode("permission"));
	}

	@Test
	public void findPermissionsByUser() {
		final ImmutableList<Permission> expected = ImmutableList.of(new Permission("permission"));
		when(permissionDAO.findPermissionsByUser(1L))
				.thenReturn(expected);
		assertEquals(expected, service.findPermissionsByUser(1L));
	}

	@Test
	public void hasCustomAccessSettingsSet() {
		service.setCachedUserCustomPermissions(ImmutableList.of(
			new UserCustomPermissionAssociation(user, new Permission("permission"), false)));
		assertTrue(service.hasCustomAccessSettingsSet(1L));
	}

	@Test
	public void hasPermissionsForCustomAuth() {
		final UserCustomPermissionAssociation permission = new UserCustomPermissionAssociation(user, new Permission("permission"), true);
		permission.setEnabled(true);
		service.setCachedUserCustomPermissions(ImmutableList.of(permission));
		assertTrue(service.hasPermissionsForCustomAuth(1L, "permission"));
		permission.setEnabled(false);
		assertFalse(service.hasPermissionsForCustomAuth(1L, "permission"));

		service.setCachedUserCustomPermissions(ImmutableList.<UserCustomPermissionAssociation>of());

		assertTrue(service.hasPermissionsForCustomAuth(1L, "permission"));
	}

	@Test
	public void hasProjectAccess() {
		final UserCustomPermissionAssociation permission = new UserCustomPermissionAssociation(user, new Permission(Permission.MANAGE_PROJECTS), true);
		permission.setEnabled(true);
		service.setCachedUserCustomPermissions(ImmutableList.of(permission));
		assertTrue(service.hasProjectAccess(1L));
		service.setCachedUserCustomPermissions(ImmutableList.<UserCustomPermissionAssociation>of());
		assertFalse(service.hasProjectAccess(1L));
	}

	@Test
	public void findAllCustomPermissionByUser() {
		final UserCustomPermissionAssociation permission = new UserCustomPermissionAssociation(user, new Permission("permission"), true);
		service.setCachedUserCustomPermissions(ImmutableList.of(permission));
		final List<UserCustomPermissionAssociation> allCustomPermissionsByUser = service.findAllCustomPermissionsByUser(1L);
		assertEquals(1, allCustomPermissionsByUser.size());
		assertEquals(permission.getPermission().getCode(), allCustomPermissionsByUser.get(0).getPermission().getCode());
	}

	@Test
	public void setCustomPermissionToUser_exists() {
		when(user.getId()).thenReturn(1L);
		final Permission permission = new Permission(Permission.ADD_FUNDS);
		final UserCustomPermissionAssociation userPermission = new UserCustomPermissionAssociation(user, permission, true);
		userPermission.setEnabled(false);
		service.setCachedUserCustomPermissions(ImmutableList.of(userPermission));
		when(aclClient.createGroup((CreateGroupRequest) anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(MutationResponse.newBuilder()
						.setStatus(StatusCode.ALREADY_EXISTS).build()));
		when(aclClient.addPrivsToGroup((AddPrivsToGroupRequest)anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(MutationResponse.newBuilder()
						.setStatus(StatusCode.OK).build()));
		when(aclClient.removePrivsFromGroup((RemovePrivsFromGroupRequest)anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(MutationResponse.newBuilder()
						.setStatus(StatusCode.OK).build()));
		when(permissionDAO.findPermissionByCode(Permission.ADD_FUNDS)).thenReturn(permission);

		service.setCustomPermissionsToUser(user.getId(), ImmutableList.of(Pair.of(Permission.ADD_FUNDS, true)));

		verify(aclClient, times(2)).addPrivsToGroup((AddPrivsToGroupRequest)anyObject(), (RequestContext) anyObject());
		verify(aclClient, times(2)).removePrivsFromGroup((RemovePrivsFromGroupRequest)anyObject(), (RequestContext) anyObject());
	}

	@Test
	public void setCustomPermissionToUser_new() {
		final Permission permission = new Permission(Permission.ADD_FUNDS);
		when(user.getId()).thenReturn(1L);
		service.setCachedUserCustomPermissions(ImmutableList.<UserCustomPermissionAssociation>of());
		when(permissionDAO.findPermissionByCode(Permission.ADD_FUNDS)).thenReturn(permission);
		when(aclClient.createGroup((CreateGroupRequest) anyObject(), (RequestContext) anyObject()))
		.thenReturn(Observable.just(MutationResponse.newBuilder()
				.setStatus(StatusCode.OK).build()));
		service.setCustomPermissionsToUser(user.getId(), ImmutableList.of(Pair.of(Permission.ADD_FUNDS, true)));

		verify(aclClient, times(4)).createGroup((CreateGroupRequest)anyObject(), (RequestContext) anyObject());
	}
}
