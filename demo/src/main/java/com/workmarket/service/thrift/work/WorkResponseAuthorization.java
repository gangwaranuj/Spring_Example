package com.workmarket.service.thrift.work;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.model.User;
import com.workmarket.thrift.work.AuthorizationContext;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.Set;

public class WorkResponseAuthorization {

	public static final Set<AuthorizationContext> RESOURCE_OR_ADMIN_CONTEXTS = ImmutableSet.of(
		AuthorizationContext.RESOURCE,
		AuthorizationContext.ACTIVE_RESOURCE,
		AuthorizationContext.ADMIN,
		AuthorizationContext.DISPATCHER
	);

	public static final Set<AuthorizationContext> ACTIVE_RESOURCE_OR_ADMIN_CONTEXTS = ImmutableSet.of(
		AuthorizationContext.ACTIVE_RESOURCE,
		AuthorizationContext.ADMIN,
		AuthorizationContext.DISPATCHER
	);

	private Set<AuthorizationContext> authorizationContexts;
	private User user;
	private boolean isInternalUser;

	public WorkResponseAuthorization(Set<AuthorizationContext> authorizationContexts, User user, boolean isInternalUser) {
		this.isInternalUser = isInternalUser;
		if (authorizationContexts == null) {
			this.authorizationContexts = Collections.EMPTY_SET;
		} else {
			this.authorizationContexts = authorizationContexts;
		}
		this.user = user;
	}

	public boolean isNonActiveResource() {
		return authorizationContexts.contains(AuthorizationContext.RESOURCE) &&
				!authorizationContexts.contains(AuthorizationContext.ACTIVE_RESOURCE);
	}

	public boolean isResourceOrAdmin() {
		return isInternalUser ||
			CollectionUtils.containsAny(authorizationContexts, RESOURCE_OR_ADMIN_CONTEXTS);
	}

	public boolean isActiveResourceOrAdmin() {
		return isInternalUser ||
			CollectionUtils.containsAny(authorizationContexts, ACTIVE_RESOURCE_OR_ADMIN_CONTEXTS);
	}

	public boolean isAdmin() {
		return isInternalUser || authorizationContexts.contains(AuthorizationContext.ADMIN);
	}

}
