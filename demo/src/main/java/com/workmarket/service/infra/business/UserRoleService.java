package com.workmarket.service.infra.business;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.acl.AclClient;
import com.workmarket.acl.gen.Protos.AddPrivsToGroupRequest;
import com.workmarket.acl.gen.Protos.CreateGroupRequest;
import com.workmarket.acl.gen.Protos.FindAccessiblePrivsRequest;
import com.workmarket.acl.gen.Protos.FindAccessiblePrivsResponse;
import com.workmarket.acl.gen.Protos.GroupName;
import com.workmarket.acl.gen.Protos.GroupType;
import com.workmarket.acl.gen.Protos.MutationResponse;
import com.workmarket.acl.gen.Protos.PrivName;
import com.workmarket.acl.gen.Protos.RemovePrivsFromGroupRequest;
import com.workmarket.acl.gen.Protos.StatusCode;
import com.workmarket.acl.gen.Protos.UserSpec;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.acl.PermissionDAO;
import com.workmarket.dao.acl.UserAclRoleAssociationDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.RoleType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.User.WorkStatus;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.acl.UserAclRoleAssociation;
import com.workmarket.domains.model.acl.UserCustomPermissionAssociation;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.web.WebRequestContextProvider;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Monolith interface to ACL service.
 */
@Component
public class UserRoleService {
	private static final Logger logger = LoggerFactory.getLogger(UserRoleService.class);
	private static final String PERMISSION_NAMESPACE = "APP_PERM";
	private static final String CUSTOM_PERMISSION_NAMESPACE = "APP_CUSTOM_PERM";
	private static final String CUSTOM_NEG_NAMESPACE = CUSTOM_PERMISSION_NAMESPACE + "_NEG";
	private static final String CUSTOM_POS_NAMESPACE = CUSTOM_PERMISSION_NAMESPACE + "_POS";

	private static final String TRIAL_LOG_TOPIC = "acl-experiment";

	private static final AtomicReference<Pair<String, List<UserCustomPermissionAssociation>>> userCustomPermissionCache
			= new AtomicReference<>();

	@Autowired private UserAclRoleAssociationDAO userAclRoleAssociationDAO;
	@Autowired private PermissionDAO permissionDAO;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private UserDAO userDAO;

	private Meter getUserRoles;
	private Meter setRoles;
	private Meter addRoles;
	private Meter removeRoles;
	private Meter hasAnyAclRole;
	private Meter hasRole;
	private Meter isInternalUser;
	private Meter isDispatcher;
	private Meter isController;
	private Meter isAdminOrManager;
	private Meter isAdmin;
	private Meter getWorkStatus;
	private Meter addUserRoleAssociation;
	private Meter isValidAclRole;
	private Meter findAllRolesByUser;
	private Meter findUserRoleAssociation;
	private Meter removeAclRoleAssociation;
	private Meter userHasPermission;
	private Meter findPermissionByCode;
	private Meter findPermissionsByUser;
	private Meter hasCustomAccessSettingsSet;
	private Meter hasPermissionsForCustomAuth;
	private Meter hasProjectAccess;
	private Meter findAllCustomPermissionsByUser;
	private Meter setCustomPermissionToUser;
	private AclClient aclClient;
	private Meter userCustomPermCacheAttempts;
	private Meter userCustomPermCacheHits;
	private Meter userCustomPermCacheMisses;

	@PostConstruct
	void init() {
		final MetricRegistryFacade metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "user_role_service");
		this.getUserRoles = metricRegistryFacade.meter("get_user_roles");
		this.setRoles = metricRegistryFacade.meter("set_roles");
		this.addRoles = metricRegistryFacade.meter("add_roles");
		this.removeRoles = metricRegistryFacade.meter("remove_roles");
		this.hasAnyAclRole = metricRegistryFacade.meter("has_any_acl_role");
		this.hasRole = metricRegistryFacade.meter("has_role");
		this.isInternalUser = metricRegistryFacade.meter("is_internal_user");
		this.isDispatcher = metricRegistryFacade.meter("is_dispatcher");
		this.isController = metricRegistryFacade.meter("is_controller");
		this.isAdminOrManager = metricRegistryFacade.meter("is_admin_or_manager");
		this.isAdmin = metricRegistryFacade.meter("is_admin");
		this.getWorkStatus = metricRegistryFacade.meter("get_work_status");
		this.addUserRoleAssociation = metricRegistryFacade.meter("add_user_role_association");
		this.isValidAclRole = metricRegistryFacade.meter("is_valid_acl_role");
		this.findAllRolesByUser = metricRegistryFacade.meter("find_all_roles_by_user");
		this.findUserRoleAssociation = metricRegistryFacade.meter("find_user_role_association");
		this.removeAclRoleAssociation = metricRegistryFacade.meter("remove_acl_role_association");
		this.userHasPermission = metricRegistryFacade.meter("user_has_permission");
		this.findPermissionByCode = metricRegistryFacade.meter("find_permission_by_code");
		this.findPermissionsByUser = metricRegistryFacade.meter("find_permissions_by_user");
		this.hasCustomAccessSettingsSet = metricRegistryFacade.meter("has_custom_access_settings_set");
		this.hasPermissionsForCustomAuth = metricRegistryFacade.meter("has_permissions_for_custom_auth");
		this.hasProjectAccess = metricRegistryFacade.meter("has_project_access");
		this.findAllCustomPermissionsByUser = metricRegistryFacade.meter("find_all_custom_permissions_by_user");
		this.setCustomPermissionToUser = metricRegistryFacade.meter("set_custom_permission_to_user");

		this.userCustomPermCacheAttempts = metricRegistryFacade.meter("user_custom_perm_cache.attempts");
		this.userCustomPermCacheHits = metricRegistryFacade.meter("user_custom_perm_cache.hits");
		this.userCustomPermCacheMisses = metricRegistryFacade.meter("user_custom_perm_cache.misses");

		aclClient = new AclClient();
	}

	@VisibleForTesting
	void setClient(final AclClient client) {
		this.aclClient = client;
	}

	private UserSpec makeUserSpec(final String uuid) {
		return UserSpec.newBuilder()
			.setUserId(uuid)
			.addFederatedGroup(AclClient.ALL_USERS)
			.build();
	}

	private GroupName makeUserAcl(final String uuid, final String namespace) {
		return GroupName.newBuilder()
			.setNamespace(namespace + "_USER")
			.setName(uuid)
			.build();
	}

	private GroupName makeCompanyAcl(final String companyUuid, final String namespace) {
		return GroupName.newBuilder()
			.setNamespace(namespace + "_COMPANY")
			.setName(companyUuid)
			.build();
	}

	////////////////////////////////////////////////////////////////////////////
	// roles (non-acl role variety)

	public Set<RoleType> getUserRoles(final User user) {
		getUserRoles.mark();
		return user.getRoles();
	}

	public void setRoles(final User user, final Set<RoleType> roles) {
		setRoles.mark();
		user.setRoles(roles);
	}

	public void addRoles(final User user, final String[] roles) {
		addRoles.mark();

		for (final String role : roles) {
			boolean found = false;
			for (final RoleType currentRole : user.getRoles()) {
				if (currentRole.getCode().equals(role)) {
					found = true;
				}
			}
			if (!found) {
				user.getRoles().add(new RoleType(role));
			}
		}
	}

	public void removeRoles(final User user, final String[] roles) {
		removeRoles.mark();
		for (final String role : roles) {
			user.getRoles().remove(new RoleType(role));
		}
	}

	public boolean hasRole(final User user, final String role) {
		hasRole.mark();
		return user.hasRole(role);
	}

	public boolean isInternalUser(final User user) {
		isInternalUser.mark();
		return user.isInternalUser();
	}

	////////////////////////////////////////////////////////////////////////////
	// acl roles

	public boolean hasAclRole(final User user, final Long id) {
		return hasAnyAclRole(user, id);
	}

	public boolean hasAnyAclRole(final User user, final Long... aclIds) {
		hasAnyAclRole.mark();
		for (final Long id : aclIds) {
			if (user.hasAclRole(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDispatcher(final User user) {
		isDispatcher.mark();
		return user.isDispatcher();
	}

	public boolean isController(final User user) {
		isController.mark();
		return user.isController();
	}

	public boolean isAdminOrManager(final User user) {
		isAdminOrManager.mark();
		return user.isAdminOrManager();
	}

	public boolean isAdmin(final User user) {
		isAdmin.mark();
		return user.isAdmin();
	}

	public WorkStatus getWorkStatus(final User user) {
		getWorkStatus.mark();
		return user.getWorkStatus();
	}

	public Set<UserAclRoleAssociation> getUserRoleAssociations(final User user) {
		return user.getUserRoleAssociations();
	}

	public void addUserRoleAssociation(final User user, final AclRole role) throws InvalidAclRoleException {
		addUserRoleAssociation.mark();
		final UserAclRoleAssociation userRole = findUserRoleAssociation(user.getId(), role.getId());
		if (userRole != null) {
			userRole.setDeleted(Boolean.FALSE);
		} else {
			if (!isValidAclRole(user, role)) {
				throw new InvalidAclRoleException();
			}

			// Adding the new role if it doesn't exist
			final UserAclRoleAssociation newUserRole = new UserAclRoleAssociation(user, role);
			userAclRoleAssociationDAO.saveOrUpdate(newUserRole);

			// Update other side of relationship
			user.getUserRoleAssociations().add(newUserRole);
		}
	}

	private boolean isValidAclRole(final User user, final AclRole role) {
		isValidAclRole.mark();
		// Used to do a check to make sure that if it was a custom role, that it was appropriate.  As we have no
		// custom roles, it's now gone.

		// Internal roles can only be assigned to internal users
		return !role.isInternal() ||isInternalUser(user);
	}

	public List<UserAclRoleAssociation> findAllRolesByUser(final Long userId, final boolean includeVirtualRoles) {
		findAllRolesByUser.mark();
		return userAclRoleAssociationDAO.findAllRolesByUser(userId, includeVirtualRoles);
	}

	public UserAclRoleAssociation findUserRoleAssociation(final Long userId, final Long roleId) {
		findUserRoleAssociation.mark();
		return userAclRoleAssociationDAO.findUserRoleAssociation(userId, roleId);
	}

	public void removeAclRoleAssociation(final User user, final UserAclRoleAssociation userRole) {
		removeAclRoleAssociation.mark();
		userRole.setDeleted(Boolean.TRUE);
	}

	////////////////////////////////////////////////////////////////////////////
	// permissions

	// Ultimately, all calls should eventually go through here, as all callers up the chain I can find have the user
	// object, and since we'll want to get the uuid, that would make it transparent to callers that this happened.
	public boolean userHasPermission(final User user, final String permissionCode) {
		userHasPermission.mark();
		return permissionDAO.findPermissionByUserAndPermissionCode(user.getId(), permissionCode) != null;
	}

	public Permission findPermissionByCode(final String permissionCode) {
		findPermissionByCode.mark();
		return permissionDAO.findPermissionByCode(permissionCode);
	}

	public List<Permission> findPermissionsByUser(final Long userId) {
		findPermissionsByUser.mark();
		return permissionDAO.findPermissionsByUser(userId);
	}

	////////////////////////////////////////////////////////////////////////////
	// custom permissions

	public Boolean hasCustomAccessSettingsSet(final Long userId) {
		logger.debug("hasCustomAccessSettingsSet");
		hasCustomAccessSettingsSet.mark();
		final User user = userDAO.get(userId);
		final String userUuid = user.getUuid();
		final String companyUuid = user.getCompany().getUuid();
		return getCachedUserCustomPermissions(user, userUuid, companyUuid).size() > 0;
	}

	/**
	 * comment copied from AuthenticationSerivceImpl, from whence it came:
	 *
	 * Checks if you have a custom permission.
	 *
	 * TODO:
	 * This method is @Deprecated because it's returning TRUE by default. This is dangerous. No one should have
	 * permissions by default. I tried returning FALSE and the tests failed, so something is tied to this condition.
	 * We need to come back to this code and think and refactor it because we shouldn't be doing this. (ianha)
	 *
	 * @param userId
	 * @param permissionCode
	 * @return
	 */
	@Deprecated
	public Boolean hasPermissionsForCustomAuth(final Long userId, final String permissionCode) {
		logger.debug("haspermsforcustauth {}", permissionCode);
		hasPermissionsForCustomAuth.mark();
		final User user = userDAO.get(userId);
		final String userUuid = user.getUuid();
		final String companyUuid = user.getCompany().getUuid();

		for (final UserCustomPermissionAssociation perm : getCachedUserCustomPermissions(
				user, userUuid, companyUuid)) {
			if (permissionCode.equals(perm.getPermission().getCode())) {
				return perm.getEnabled();
			}
		}
		//Note: bizarrely, default access is to allow
		return true;
	}

	public Boolean hasProjectAccess(final Long userId) {
		logger.debug("hasProjectAccess");
		Assert.notNull(userId);
		hasProjectAccess.mark();
		final User user = userDAO.get(userId);
		final String userUuid = user.getUuid();
		final String companyUuid = user.getCompany().getUuid();

		for (final UserCustomPermissionAssociation perm : getCachedUserCustomPermissions(
				user, userUuid, companyUuid)) {
			if (Permission.MANAGE_PROJECTS.equals(perm.getPermission().getCode())) {
				return perm.getEnabled();
			}
		}
		//Note: default is no.
		return false;
	}

	private Observable<String> getAclServiceNamesByNamespace(
			final String uuid,
			final String companyUuid,
			final String namespace) {
		final RequestContext ctx = webRequestContextProvider.getRequestContext();
		ctx.setUserId(uuid);
		return aclClient.findAccessiblePrivs(
				FindAccessiblePrivsRequest.newBuilder()
					.setUser(makeUserSpec(uuid))
					.addAclName(makeUserAcl(uuid, namespace))
					.addAclName(makeCompanyAcl(companyUuid, namespace))
					.build(),
				ctx)
			.map(new Func1<FindAccessiblePrivsResponse, List<String>>() {
				@Override
				public List<String> call(final FindAccessiblePrivsResponse resp) {
					if (resp.getStatus() == StatusCode.NOT_FOUND) {
						return new ArrayList<>();
					}
					if (resp.getStatus() != StatusCode.OK) {
						throw new RuntimeException("failure getting privs - " + resp.getStatus() + " : "
							+ Joiner.on(',').join(resp.getFailureList()));
					}
					final List<String> permissions = new ArrayList<>();
					for (final PrivName name : resp.getPrivNameList()) {
						permissions.add(name.getName());
					}
					return permissions;
				}
			})
			.flatMap(new Func1<List<String>, Observable<String>>() {
				@Override
				public Observable<String> call(final List<String> iterable) {
					return Observable.from(iterable);
				}
			});
	}

	private Observable<List<UserCustomPermissionAssociation>> getUserCustomPermissions(
			final User user,
			final String userUuid,
			final String companyUuid) {
		final Observable<String> positive
			= getAclServiceNamesByNamespace(userUuid, companyUuid, CUSTOM_POS_NAMESPACE);
		final Observable<String> negative
			= getAclServiceNamesByNamespace(userUuid, companyUuid, CUSTOM_NEG_NAMESPACE);
		return Observable.zip(positive.toList(), negative.toList(),
			new Func2<List<String>, List<String>, List<UserCustomPermissionAssociation>>() {
				@Override
				public List<UserCustomPermissionAssociation> call(
						final List<String> posNames,
						final List<String> negNames) {
					logger.debug("POSNAMES = {}", Joiner.on(',').join(posNames));
					logger.debug("NEGNAMES = {}", Joiner.on(',').join(negNames));
					final HashSet<String> neg = new HashSet<>(negNames);
					final List<UserCustomPermissionAssociation> ret = new ArrayList<>();
					for (final String negName : negNames) {
						ret.add(new UserCustomPermissionAssociation(user, new Permission(negName), false));
					}
					for (final String posName : posNames) {
						if (!neg.contains(posName)) { // it should never contain it, but just in case
							ret.add(new UserCustomPermissionAssociation(user, new Permission(posName), true));
						}
					}
					return ret;
				}
			});
	}


	private List<UserCustomPermissionAssociation> getCachedUserCustomPermissions(
			final User user, // only to be used to make existing objecty things
			final String userUuid,
			final String companyUuid) {

		userCustomPermCacheAttempts.mark();
		final String cacheKey = makeUserCustomPermissionCacheKey();
		final Pair<String, List<UserCustomPermissionAssociation>> cached = userCustomPermissionCache.get();
		if (cached != null && cached.getLeft().equals(cacheKey)) {
			userCustomPermCacheHits.mark();
			return cached.getRight();
		}
		userCustomPermCacheMisses.mark();
		logger.debug("filling cache cached key {} -- for thiskey {}",
				cached == null ? "null" : cached.getLeft(), cacheKey);
		final List<UserCustomPermissionAssociation> permissions = getUserCustomPermissions(user, userUuid, companyUuid)
			.toBlocking().single();
		setCachedUserCustomPermissions(cacheKey, permissions);
		return permissions;
	}

	private String makeUserCustomPermissionCacheKey() {
		return webRequestContextProvider.getWebRequestContext().getRequestId() + webRequestContextProvider.getWebRequestContext().getUserUuid();
	}

	private void setCachedUserCustomPermissions(final String cacheKey, final List<UserCustomPermissionAssociation> permissions) {
		userCustomPermissionCache.set(Pair.of(cacheKey, permissions));
	}

	@VisibleForTesting
	void setCachedUserCustomPermissions(final List<UserCustomPermissionAssociation> permissions) {
		userCustomPermissionCache.set(Pair.of(makeUserCustomPermissionCacheKey(), permissions));
	}


	private void flushCachedUserCustomPermissions() {
		logger.debug("flushing custom permission CACHE");
		userCustomPermissionCache.set(null);
	}

	public List<UserCustomPermissionAssociation> findAllCustomPermissionsByUser(final Long userId) {
		logger.debug("findAllCustomPermsByUser");
		findAllCustomPermissionsByUser.mark();
		final User user = userDAO.get(userId);
		final String userUuid = user.getUuid();
		final String companyUuid = user.getCompany().getUuid();
		return getCachedUserCustomPermissions(user, userUuid, companyUuid);
	}

	public void setCustomPermissionsToUser(final Long userId, final List<Pair<String, Boolean>> settings) {
		final User user = userDAO.get(userId);
		final ImmutableList.Builder<Pair<Permission, Boolean>> permBuilder = ImmutableList.builder();
		for (final Pair<String, Boolean> permNameAndEnabled : settings) {
			logger.debug("sCPTU: perm is {} enabled = {}", permNameAndEnabled.getLeft(), permNameAndEnabled.getRight());
			final Permission permission = findPermissionByCode(permNameAndEnabled.getLeft());
			Assert.notNull(permission);
			permBuilder.add(Pair.of(permission, permNameAndEnabled.getRight()));
		}

		Assert.notNull(user);
		setCustomPermissionsToUser(user, permBuilder.build());
	}

	private void setCustomPermissionsToUser(final User user, final List<Pair<Permission, Boolean>> permissions) {
		setCustomPermissionToUser.mark();
		final String userUuid = user.getUuid();

		final ImmutableList.Builder<PrivName> enablePermissions = ImmutableList.builder();
		final ImmutableList.Builder<PrivName> disablePermissions = ImmutableList.builder();
		for (final Pair<Permission, Boolean> setting : permissions) {
			final PrivName privName = PrivName.newBuilder()
				.setName(setting.getLeft().getCode())
				.setNamespace(PERMISSION_NAMESPACE).build();
			if (setting.getRight()) {
				logger.debug("Enabling {}", setting.getLeft());
				enablePermissions.add(privName);
			} else {
				logger.debug("Disabling {}", setting.getLeft());
				disablePermissions.add(privName);
			}
		}

		final String negNs = CUSTOM_NEG_NAMESPACE  + "_USER";
		final String posNs = CUSTOM_POS_NAMESPACE + "_USER";
		// remove disabled permsissions from POS ns and add to NEG
		doCustomPrivRemovePerms(posNs, disablePermissions.build(), userUuid);
		doCustomPrivAddPerms(negNs, disablePermissions.build(), userUuid);
		// remove enabled permssions from NEG and add to POS
		doCustomPrivAddPerms(posNs, enablePermissions.build(), userUuid);
		doCustomPrivRemovePerms(negNs, enablePermissions.build(), userUuid);
		flushCachedUserCustomPermissions();
	}

	private void doCustomPrivAddPerms(final String addNs, final List<PrivName> privs, final String uuid) {
		final MutationResponse addGroupResp = aclClient.createGroup(
			CreateGroupRequest.newBuilder()
				.setUser(makeUserSpec(uuid))
				.setNamespace(addNs)
				.setName(uuid)
				.addAllPrivName(privs)
				.addUserId(uuid)
				.setGroupType(GroupType.ACL)
				.build(),
			webRequestContextProvider.getRequestContext()).toBlocking().single();
		if (addGroupResp.getStatus() != StatusCode.OK & addGroupResp.getStatus() != StatusCode.ALREADY_EXISTS) {
			throw new RuntimeException("Failed creating new ACL "
				+ Joiner.on(',').join(addGroupResp.getFailureList()));
		}
		if (addGroupResp.getStatus() == StatusCode.OK) {
			return;
		}
		final MutationResponse addObs = aclClient.addPrivsToGroup(
			AddPrivsToGroupRequest.newBuilder()
				.setUser(makeUserSpec(uuid))
				.setGroupName(GroupName.newBuilder().setName(uuid).setNamespace(addNs))
				.addAllPrivName(privs)
				.build(),
			webRequestContextProvider.getRequestContext()).toBlocking().single();
		if (addObs.getStatus() != StatusCode.OK) {
			throw new RuntimeException("Failed adding new acl bits "
					+ Joiner.on(',').join(addObs.getFailureList()));
		}
	}

	private void doCustomPrivRemovePerms(final String removeNs, final List<PrivName> privs, final String uuid) {
		final MutationResponse removeGroupResp = aclClient.createGroup(
			CreateGroupRequest.newBuilder()
				.setUser(makeUserSpec(uuid))
				.setNamespace(removeNs)
				.setName(uuid)
				.addUserId(uuid)
				.setGroupType(GroupType.ACL)
				.build(),
				webRequestContextProvider.getRequestContext()).toBlocking().single();
		if (removeGroupResp.getStatus() != StatusCode.OK & removeGroupResp.getStatus() != StatusCode.ALREADY_EXISTS) {
			throw new RuntimeException("Failed creating new ACL "
				+ Joiner.on(',').join(removeGroupResp.getFailureList()));
		}
		if (removeGroupResp.getStatus() == StatusCode.OK) {
			return;
		}
		final MutationResponse removeObs = aclClient.removePrivsFromGroup(
			RemovePrivsFromGroupRequest.newBuilder()
				.setUser(makeUserSpec(uuid))
				.setGroupName(GroupName.newBuilder().setName(uuid).setNamespace(removeNs))
				.addAllPrivName(privs)
				.build(), webRequestContextProvider.getRequestContext()).toBlocking().single();
		if (removeObs.getStatus() != StatusCode.OK) {
			throw new RuntimeException("Failed removing old acl bits "
				+ Joiner.on(',').join(removeObs.getFailureList()));
		}
	}
}
