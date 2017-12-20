package com.workmarket.service.thrift.work;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.model.User;
import com.workmarket.thrift.work.AuthorizationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkResponseAuthorizationTest {

	private User internalUser;
	private User nonInternalUser;

	@Before
	public void setup() {
		internalUser = mock(User.class);
		when(internalUser.isInternalUser()).thenReturn(true);

		nonInternalUser = mock(User.class);
		when(nonInternalUser.isInternalUser()).thenReturn(false);
	}

	@Test
	public void isNonActiveResource() {
		Set<AuthorizationContext> authorizationContexts = ImmutableSet.of(AuthorizationContext.RESOURCE);

		WorkResponseAuthorization workResponseAuthorization =
			new WorkResponseAuthorization(authorizationContexts, mock(User.class), false);

		assertTrue(workResponseAuthorization.isNonActiveResource());
	}

	@Test
	public void not_isNonActiveResource() {
		Set<AuthorizationContext> authorizationContexts = ImmutableSet.of(AuthorizationContext.ACTIVE_RESOURCE);

		WorkResponseAuthorization workResponseAuthorization =
			new WorkResponseAuthorization(authorizationContexts, mock(User.class), false);

		assertFalse(workResponseAuthorization.isNonActiveResource());
	}

	@Test
	public void internalUser_isResourceOrAdmin() {
		WorkResponseAuthorization workResponseAuthorization =
			new WorkResponseAuthorization(Collections.EMPTY_SET, internalUser, true);

		assertTrue(workResponseAuthorization.isResourceOrAdmin());
	}

	@Test
	public void nonInternalUser_not_isResourceOrAdmin() {
		WorkResponseAuthorization workResponseAuthorization =
			new WorkResponseAuthorization(Collections.EMPTY_SET, nonInternalUser, false);

		assertFalse(workResponseAuthorization.isResourceOrAdmin());
	}

	@Test
	public void resourceOrAdminAuthorizationContexts_isResourceOrAdmin() {
		for (AuthorizationContext authorizationContext : WorkResponseAuthorization.RESOURCE_OR_ADMIN_CONTEXTS) {
			WorkResponseAuthorization workResponseAuthorization =
				new WorkResponseAuthorization(ImmutableSet.of(authorizationContext), nonInternalUser, false);

			assertTrue(workResponseAuthorization.isResourceOrAdmin());
		}
	}

	@Test
	public void internalUser_isActiveResourceOrAdmin() {
		WorkResponseAuthorization workResponseAuthorization =
			new WorkResponseAuthorization(Collections.EMPTY_SET, internalUser, true);

		assertTrue(workResponseAuthorization.isActiveResourceOrAdmin());
	}

	@Test
	public void nonInternalUser_not_isActiveResourceOrAdmin() {
		WorkResponseAuthorization workResponseAuthorization =
			new WorkResponseAuthorization(Collections.EMPTY_SET, nonInternalUser, false);

		assertFalse(workResponseAuthorization.isActiveResourceOrAdmin());
	}

	@Test
	public void activeResourceOrAdminAuthorizationContexts_isActiveResourceOrAdmin() {
		for (AuthorizationContext authorizationContext : WorkResponseAuthorization.ACTIVE_RESOURCE_OR_ADMIN_CONTEXTS) {
			WorkResponseAuthorization workResponseAuthorization =
				new WorkResponseAuthorization(ImmutableSet.of(authorizationContext), nonInternalUser, false);

			assertTrue(workResponseAuthorization.isActiveResourceOrAdmin());
		}
	}

	@Test
	public void adminResourceContext_isAdmin() {
		Set<AuthorizationContext> authorizationContexts = ImmutableSet.of(AuthorizationContext.ADMIN);

		WorkResponseAuthorization workResponseAuthorization =
			new WorkResponseAuthorization(authorizationContexts, mock(User.class), false);

		assertTrue(workResponseAuthorization.isAdmin());
	}

	@Test
	public void internalUser_isAdmin() {
		WorkResponseAuthorization workResponseAuthorization =
			new WorkResponseAuthorization(Collections.EMPTY_SET, internalUser, true);

		assertTrue(workResponseAuthorization.isAdmin());
	}

	@Test
	public void nonInternalUser_not_isAdmin() {
		WorkResponseAuthorization workResponseAuthorization =
			new WorkResponseAuthorization(Collections.EMPTY_SET, nonInternalUser, false);

		assertFalse(workResponseAuthorization.isAdmin());
	}
}
