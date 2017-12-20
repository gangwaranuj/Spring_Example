package com.workmarket.service.thrift.work;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.workmarket.service.infra.business.UserRoleService;
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
import com.workmarket.utility.CollectionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class WorkResponseContextBuilder {

	@Autowired private UserRoleService userRoleService;
	@Autowired private UserService userService;
	@Autowired private VendorService vendorService;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private WorkService workService;

	public void buildContext(WorkResponse response, com.workmarket.domains.model.User currentUser, AbstractWork work) {
		Set<RequestContext> requestContexts = Sets.newHashSet();

		WorkResource viewingResource = workService.findWorkResource(currentUser.getId(), work.getId());
		WorkResource activeResource = workService.findActiveWorkResource(work.getId());

		if (work.getBuyer().getId().equals(currentUser.getId())) {
			requestContexts.add(RequestContext.OWNER);
		} else if (work.getCompany().getId().equals(currentUser.getCompany().getId())) {
			requestContexts.add(RequestContext.COMPANY_OWNED);
		}

		Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(currentUser.getId());
		boolean isDispatcher = personaPreferenceOptional.isPresent() &&
			personaPreferenceOptional.get().isDispatcher();

		if (isDispatcher) {
			Long currentUserCompanyId = currentUser.getCompany().getId();
			boolean isOnWorkFeedOrSomeoneFromMyCompanyWasInvited = work.isShownInFeed() ||
				workResourceService.isAtLeastOneWorkerFromCompanyInvitedToWork(currentUserCompanyId, work.getId()) ||
				vendorService.isVendorInvitedToWork(currentUserCompanyId, work.getId());

			boolean isNotAssignedOrIsAssignedToSomeoneFromMyCompany = activeResource == null ||
				activeResource.getUser().getCompany().getId().equals(currentUserCompanyId);

			if (isOnWorkFeedOrSomeoneFromMyCompanyWasInvited && isNotAssignedOrIsAssignedToSomeoneFromMyCompany) {
				requestContexts.add(RequestContext.DISPATCHER);
			}
		}

		if (viewingResource == null) {
			if (work.shouldOpenForWorkResource(activeResource) &&
				(!CollectionUtilities.contains(requestContexts, RequestContext.OWNER, RequestContext.COMPANY_OWNED) ||
					(CollectionUtilities.contains(requestContexts, RequestContext.COMPANY_OWNED) &&
						userService.isEmployeeWorker(currentUser)))) {
				if (!userRoleService.isInternalUser(currentUser) && work.isSent()) {
					requestContexts.add(RequestContext.INVITED);
				}
			}
		} else {
			if (viewingResource.isAssignedToWork()) {
				requestContexts.add(RequestContext.ACTIVE_RESOURCE);
			} else if (viewingResource.isCancelled()) {
				requestContexts.add(RequestContext.CANCELLED_RESOURCE);
			} else if (viewingResource.isDeclined()) {
				requestContexts.add(RequestContext.DECLINED_RESOURCE);
			} else if (activeResource != null) {
				requestContexts.add(RequestContext.INVITED_INACTIVE);
			} else {
				requestContexts.add(RequestContext.INVITED);
			}
		}

		if (requestContexts.isEmpty()) {
			if (isActiveResourceInCurrentUsersCompany(currentUser, activeResource)) {
				requestContexts.add(RequestContext.ASSIGNED_COMPANY);
			} else {
				requestContexts.add(RequestContext.UNRELATED);
			}
		}

		response.setRequestContexts(requestContexts);

		Set<AuthorizationContext> authContexts = buildAuthorizationContexts(response, currentUser, work, activeResource);
		response.setAuthorizationContexts(authContexts);
	}

	public Set<AuthorizationContext> buildAuthorizationContexts(WorkResponse response, User currentUser, AbstractWork work, WorkResource activeResource) {
		Set<AuthorizationContext> authContexts = Sets.newHashSet();
		if (CollectionUtilities.contains(response.getRequestContexts(), RequestContext.OWNER)) {
			authContexts.add(AuthorizationContext.BUYER);
		}

		if (response.getRequestContexts().contains(RequestContext.OWNER) || (
			response.getRequestContexts().contains(RequestContext.COMPANY_OWNED) &&
					userRoleService.userHasPermission(currentUser,  Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS))
			) {
			authContexts.add(AuthorizationContext.ADMIN);
		}

		if (CollectionUtilities.contains(response.getRequestContexts(), RequestContext.OWNER, RequestContext.COMPANY_OWNED) &&
				userRoleService.userHasPermission(currentUser, Permission.PAY_ASSIGNMENT)) {
			authContexts.add(AuthorizationContext.PAY);
		}

		if (CollectionUtilities.contains(response.getRequestContexts(), RequestContext.INVITED, RequestContext.ACTIVE_RESOURCE, RequestContext.INVITED_INACTIVE) ||
			(CollectionUtilities.contains(response.getRequestContexts(), RequestContext.DECLINED_RESOURCE) && work.isSent())) {
			authContexts.add(AuthorizationContext.RESOURCE);
		}

		if (CollectionUtilities.contains(response.getRequestContexts(), RequestContext.ACTIVE_RESOURCE)) {
			authContexts.add(AuthorizationContext.ACTIVE_RESOURCE);
		}

		if (CollectionUtilities.contains(response.getRequestContexts(), RequestContext.DISPATCHER)) {
			authContexts.add(AuthorizationContext.DISPATCHER);
		}

		if (isDispatcherRequestContextForActiveResourceInSameCompany(response.getRequestContexts(), currentUser, activeResource)) {
			authContexts.add(AuthorizationContext.RESOURCE);
			authContexts.add(AuthorizationContext.ACTIVE_RESOURCE);
		}

		if (! CollectionUtilities.contains(authContexts, AuthorizationContext.ADMIN) &&
			(CollectionUtilities.contains(response.getRequestContexts(), RequestContext.INVITED_INACTIVE, RequestContext.CANCELLED_RESOURCE) &&
			! isDispatcherRequestContextForActiveResourceInSameCompany(response.getRequestContexts(), currentUser, activeResource))) {
			authContexts.add(AuthorizationContext.READ_ONLY);
		}

		return authContexts;
	}

	public boolean isDispatcherRequestContextForActiveResourceInSameCompany(Set<RequestContext> requestContexts, User currentUser, WorkResource activeResource) {
		return CollectionUtilities.contains(requestContexts, RequestContext.DISPATCHER) &&
			isActiveResourceInCurrentUsersCompany(currentUser, activeResource);
	}

	private boolean isActiveResourceInCurrentUsersCompany(User currentUser, WorkResource activeResource) {
		return activeResource != null &&
			currentUser.getCompany().getId().equals(activeResource.getUser().getCompany().getId());
	}
}
