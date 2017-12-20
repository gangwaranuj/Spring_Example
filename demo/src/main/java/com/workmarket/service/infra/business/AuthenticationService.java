package com.workmarket.service.infra.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.api.exceptions.BadRequestApiException;
import com.workmarket.api.v2.worker.security.LoginTracker;
import com.workmarket.auth.AuthenticationClient;
import com.workmarket.auth.gen.Messages;
import com.workmarket.auth.gen.Messages.ChangePasswordResponse;
import com.workmarket.auth.gen.Messages.CredentialValidationResponse;
import com.workmarket.auth.gen.Messages.DeleteReason;
import com.workmarket.auth.gen.Messages.GetUserStatusResponse;
import com.workmarket.auth.gen.Messages.Status;
import com.workmarket.auth.gen.Messages.UserEmailVerified;
import com.workmarket.auth.gen.Messages.UserStatus;
import com.workmarket.auth.gen.Messages.ValidationResponse;
import com.workmarket.auth.gen.Messages.ValidationStatus;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.LoginInfoDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.acl.AclRoleDAO;
import com.workmarket.dao.acl.PermissionDAO;
import com.workmarket.dao.changelog.user.UserChangeLogDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.RoleDescription;
import com.workmarket.domains.model.RoleType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.acl.UserAclRoleAssociation;
import com.workmarket.domains.model.acl.UserCustomPermissionAssociation;
import com.workmarket.domains.model.changelog.user.UserAclRoleAddedChangeLog;
import com.workmarket.domains.model.changelog.user.UserAclRoleRemovedChangeLog;
import com.workmarket.domains.model.changelog.user.UserStatusChangeLog;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.request.PasswordResetRequest;
import com.workmarket.domains.model.request.RequestStatusType;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.helpers.WMCallable;
import com.workmarket.jan20.IsEqual;
import com.workmarket.jan20.IsEqualUtil;
import com.workmarket.jan20.Trial;
import com.workmarket.jan20.TrialResult;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.search.cache.HydratorCache;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserNotificationPreferencePojo;
import com.workmarket.service.business.UserNotificationPrefsService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.WebActivityAuditService;
import com.workmarket.service.exception.PasswordsDontMatchException;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.infra.security.SecurityContext;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.workmarket.common.kafka.KafkaUtil.getStringObjectMap;
import static com.workmarket.jan20.IsEqualUtil.checkNullity;
import static com.workmarket.jan20.IsEqualUtil.startCompare;
import static com.workmarket.service.infra.business.AuthTrialCommon.KAFKA_CLIENT;
import static com.workmarket.service.infra.business.AuthTrialCommon.TRIAL_LOG_TOPIC;
import static com.workmarket.service.infra.business.AuthTrialCommon.convertStatusType;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.springframework.transaction.support.TransactionSynchronization.STATUS_ROLLED_BACK;

@Service
public class AuthenticationService {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
	private static final Logger loginTracking = LoggerFactory.getLogger(LoginTracker.class);
	private static final String RECAPTCHA = "recaptcha";

	private static final AtomicReference<PerRequestCache<Pair<String, Calendar>, User>> userStatusCache = new AtomicReference<>();
	private static final AtomicReference<PerRequestCache<Pair<Boolean, Calendar>, User>> emailConfirmedCache = new AtomicReference<>();
	private Meter rollbackFailMeter;

	private static class CalendarIsEqual extends IsEqual<Calendar> {
		@Override
		public boolean apply(final Calendar control, final Calendar experiment) {
			if ((control == null) != (experiment == null)) {
				return false;
			}
			if (control == null) {
				return true;
			}
			final long controlMillis = control.getTimeInMillis();
			final long experimentMillis = experiment.getTimeInMillis();
			// as long as they're close -- meaning within 10 minutes
			return Math.abs(controlMillis - experimentMillis) < TimeUnit.MINUTES.toMillis(10);
		}
	}

	private static class StatusToBoolean implements Func1<Status, Boolean> {
		@Override
		public Boolean call(final Status status) {
			return status.getSuccess();
		}
	}

	@Value("${session.timeout.in.minutes}") private String SESSION_TIMEOUT_IN_MINUTES;

	@Autowired private SecurityContext securityContext;
	@Autowired private UserService userService;
	@Autowired private RequestService requestService;
	@Autowired private LaneService laneService;
	@Autowired private WorkService workService;
	@Autowired private UserIndexer userIndexer;
	@Autowired private UserDAO userDAO;
	@Autowired private LoginInfoDAO loginInfoDAO;
	@Autowired private AclRoleDAO aclRoleDAO;
	@Autowired private PermissionDAO permissionDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private UserChangeLogDAO userChangeLogDAO;
	@Autowired private UserNotificationPrefsService userNotificationPrefsService;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private HydratorCache hydratorCache;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private UserRoleService userRoleService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private WebActivityAuditService webActivityAuditService;
	@Autowired private AuthenticationClient authClient;
	@Autowired private AuthTrialCommon trialCommon;
	@Autowired private FeatureEvaluator featureEvaluator;

	private Meter loginMeter;

	@PostConstruct
	private void init() {
		final WMMetricRegistryFacade facade = new WMMetricRegistryFacade(
				metricRegistry, "authentication-service");
		loginMeter = facade.meter("login");
		rollbackFailMeter = facade.meter("create_rollback_fail");
		userStatusCache.set(new PerRequestCache<>(
			facade, "user_status", webRequestContextProvider,
			new Func2<String, User, Pair<String, Calendar>>() {
				@Override
				public Pair<String, Calendar> call(final String uuid, final User user) {
					return getUncachedUserStatusPair(user);
				}
			}
		));

		emailConfirmedCache.set(new PerRequestCache<>(
			facade, "email_confirmed", webRequestContextProvider,
			new Func2<String, User, Pair<Boolean, Calendar>>() {
				@Override
				public Pair<Boolean, Calendar> call(final String s, final User user) {
					return getUncachedEmailConfirmed(user);
				}
			}
		));
	}

	// @Metered(group="metrics", type="authn", name="auth", rateUnit=TimeUnit.MINUTES)
	public User auth(String emailAddress, String password) {
		return auth(emailAddress, password, null, null, null, false);
	}

	// @Metered(group="metrics", type="authn", name="auth", rateUnit=TimeUnit.MINUTES)
	public User auth(final String emailAddress, final String password, final String inetAddress,
		final String sessionId, final String recaptchaResponseToken, final boolean recaptchaExcluded) {
		final User user = userDAO.findUserByEmail(emailAddress);

		if (user == null || isBlank(password)) {
			loginTracking.warn("login attempt for email address {} - user does not exist", emailAddress);
			return null;
		}

		if (isLocked(user)) {
			loginTracking.warn("login attempt for email address {} - user account is locked", emailAddress);
			return user;
		}

		if (isDeleted(user)) {
			loginTracking.warn("login attempt for email address {} - user account is deleted", emailAddress);
			return null;
		}

		final boolean recaptchaEnabled = isRecaptchaEnabledOnUser(user) && !recaptchaExcluded;
		if (recaptchaEnabled && isBlank(recaptchaResponseToken)) {
			reportRecaptchaFailure(user, inetAddress);
			return user;
		}

		final RequestContext ctx = trialCommon.getApiContext();
		final Boolean credsCheckedOut = doCredentialValidation(ctx, inetAddress, emailAddress, password, sessionId)
			.map(new Func1<CredentialValidationResponse, Boolean>() {
				@Override
				public Boolean call(final CredentialValidationResponse passwordValidationResponse) {
					logger.debug("VALIDATION STATUS " + passwordValidationResponse.getStatus());
					return passwordValidationResponse.getStatus().equals(ValidationStatus.OK);
				}
			})
			.toBlocking().single();
		if (!credsCheckedOut) {
			loginTracking.warn("login attempt for email address {} - password is invalid", emailAddress);
			handleLoginResult(user, inetAddress, false);
			return null;
		}

		handleLoginResult(user, inetAddress, true);
		loginMeter.mark();

		return user;
	}


	private Observable<CredentialValidationResponse> doCredentialValidation(
			final RequestContext ctx,
			final String inetAddress,
			final String emailAddress,
			final String password,
			final String sessionId) {
		if (isBlank(inetAddress)) {
			return authClient.validateUserPasswordButNotIp(emailAddress, password, ctx);
		}
		if (isBlank(sessionId)) {
			return authClient.validateUserPasswordIp(emailAddress, password, inetAddress, ctx);
		}
		return authClient.loginWithUserPasswordIp(emailAddress, password, inetAddress, sessionId, ctx);
	}

	public User authLinkedIn(String emailAddress, String inetAddress) {
		User user = userDAO.findUserByEmail(emailAddress);

		if (user == null) {
			return null;
		}

		if (isLocked(user)) {
			return user;
		}

		handleLoginResult(user, inetAddress, true);
		loginMeter.mark();
		return user;
	}

	// @Metered(group="metrics", type="authn", name="masq", rateUnit=TimeUnit.MINUTES)
	public User masquerade(Long userId, Long targetUserId) {
		Assert.notNull(userId);
		Assert.notNull(targetUserId);
		User masquerader = userDAO.get(userId);

		User user = null;
		if (masquerader != null) {

			for (RoleType role : userRoleService.getUserRoles(masquerader)) {
				if (role.getCode().equals(RoleType.MASQUERADE)) {
					user = userDAO.get(targetUserId);
				}
			}
		}

		Assert.notNull(user, " User " + userId + " doesn't have the required role to masquerade as another user.");

		return user;
	}

	public User masquerade(Long userId, String email) {
		return masquerade(userId, userService.findUserByEmail(email).getId());
	}

	public void changePassword(final String currentPassword, final String newPassword)
			throws PasswordsDontMatchException {

		final Boolean changedOk = authClient
			.changePassword(getCurrentUser().getEmail(), currentPassword, newPassword, trialCommon.getApiContext())
			.map(new Func1<ChangePasswordResponse, Boolean>() {
				@Override
				public Boolean call(final ChangePasswordResponse resp) {
					return ValidationStatus.OK == resp.getStatus();
				}
			})
			.toBlocking().single();

		if (!changedOk) {
			throw new PasswordsDontMatchException("Current password does not match");
		}
	}

	public void createUser(final String uuid, final String email, final String password, final String companyUuid,
		   final UserStatusType status) {
		final RequestContext requestContext = trialCommon.getApiContext();
		// so the cache has data, even though the record hasn't been written yet.
		final AtomicBoolean didWrite = new AtomicBoolean(false);
		final Status result = authClient.createUser(uuid, email, password, companyUuid, convertStatusType(status), requestContext)
				.toBlocking().single();
		if (!result.getSuccess()) {
			throw new BadRequestApiException("Creating user failed: " + result.getMessage());
		}
		didWrite.set(true);
		logger.debug("createUser(): user '{}' created in authentication-service.");

		// In the case that commit actually failed, try to undo as best we can.
		trialCommon.getCommitHook().executeOnComplete(new Action1<Integer>() {
			@Override
			public void call(final Integer status) {
				if (status == STATUS_ROLLED_BACK && didWrite.get()) {
					logger.info("transaction rolled back, deleting user with email {}", email);
					deleteUser(uuid, email, requestContext);
				}
			}
		});
	}

	public Status deleteUser(final String uuid, final String email, final RequestContext requestContext) {
		return authClient.deleteUser(email, uuid, DeleteReason.ROLLED_BACK, requestContext)
			.map(new Func1<Status, Status>() {
				@Override
				public Status call(final Status resp) {
					logger.info("got response status {}:{} from auth service", resp.getSuccess(), resp.getMessage());
					if (!resp.getSuccess()) {
						rollbackFailMeter.mark();
					}
					return resp;
				}
			})
			.toBlocking().single();
	}

	public void doResetPassword(final String encryptedRequestId, final String newPassword) {
		final PasswordResetRequest request = requestService.findPasswordResetRequest(encryptedRequestId);

		// TODO Throw relevant exception: invalid request, etc.
		if (request == null || request.isExpired() || !request.getRequestStatusType().getCode().equals(RequestStatusType.SENT))
			return;

		userService.unlockUser(request.getInvitedUser().getId());
		request.setRequestStatusType(new RequestStatusType(RequestStatusType.ACCEPTED));
		authClient
			.resetPassword(request.getInvitedUser().getUuid(), newPassword, trialCommon.getApiContext())
			.toBlocking().single();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.service.infra#getRoles(java.lang.Long)
	 */
	public String[] getRoles(Long userId) {
		Assert.notNull(userId);

		List<String> list = new ArrayList<>();
		User user = userDAO.get(userId);

		if (user != null) {
			Set<RoleType> roles = userRoleService.getUserRoles(user);
			for (RoleType type : roles) {
				list.add(type.getCode());
			}
		}

		String results[] = new String[list.size()];
		list.toArray(results);

		return results;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.service.infra#approveUser(java.lang.Long)
	 */
	public void approveUser(Long userId) {

		User user = userDAO.get(userId);
		UserStatusType oldStatus = getUserStatus(user);

		setUserStatus(user, new UserStatusType(UserStatusType.APPROVED));
		userChangeLogDAO.saveOrUpdate(new UserStatusChangeLog(userId, getCurrentUserId(),
			getMasqueradeUserId(), oldStatus, new UserStatusType(UserStatusType.APPROVED)));

		if (user.isLane3Pending()) {
			user.setLane3ApprovalStatus(ApprovalStatus.APPROVED);
		}
		userIndexer.reindexById(userId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.service.infra#addRoles(java.lang.Long)
	 */
	public void addRoles(Long userId, String[] roles) {
		final User user = userDAO.get(userId);
		userRoleService.addRoles(user, roles);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.service.infra#removeRoles(java.lang.Long)
	 */
	public void removeRoles(Long userId, String[] roles) {
		final User user = userDAO.get(userId);
		userRoleService.removeRoles(user, roles);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.service.infra#getInternalRoles()
	 */
	public List<RoleDescription> getInternalRoles() {

		List<RoleDescription> roles = new ArrayList<>();

		roles.add(new RoleDescription(RoleType.INTERNAL, "Grants access to the admin site"));
		roles.add(new RoleDescription(RoleType.WM_ADMIN, "Gives access to EVERYTHING"));
		roles.add(new RoleDescription(RoleType.MASQUERADE, "Gives masquerade access to a user"));
		roles.add(new RoleDescription(RoleType.WM_EMAIL, "Gives access to mailing tools (invites)"));
		roles.add(new RoleDescription(RoleType.WM_USER_COMPANY_SEARCH, "Gives access to search users and companies"));
		roles.add(new RoleDescription(RoleType.WM_EMPLOYEE, "Gives access to employee management"));
		roles.add(new RoleDescription(RoleType.WM_QUEUES, "Gives access to all the various client service queues"));
		roles.add(new RoleDescription(RoleType.WM_GENERAL, "Gives access to daily reporting, real-time admin, who's online now"));
		roles.add(new RoleDescription(RoleType.WM_ACCOUNTING, "Gives access to accounting page in the internal admin site"));
		roles.add(new RoleDescription(RoleType.WM_ATS, "Gives access to ATS for posting jobs and managing applicants"));
		roles.add(new RoleDescription(RoleType.WM_CRM, "Gives access to CRM.  FUTURE - connect to CRITS"));
		roles.add(new RoleDescription(RoleType.WM_SUBSCRIPTION_APPROVAL, "Gives access to Subscription Approval function"));
		roles.add(new RoleDescription(RoleType.WM_SUBSCRIPTION_REPORTING, "Gives access to Subscription Reporting page"));

		return roles;
	}

	public Set<User> getActiveInternalUsers() {
		return userDAO.findActiveInternalUsers();

	}

	public Set<User> getInternalContractors() {
		return userDAO.findActiveInternalContractors();
	}

	public void setCurrentUser(User user) {
		// Authentication authentication = new PreAuthenticatedAuthenticationToken(user.getEmail(), "");
		// SecurityContextHolder.getContext().setAuthentication(authentication);
		securityContext.setCurrentUser(user);
		WebRequestContext webRequestContext = webRequestContextProvider.getWebRequestContext();
		webRequestContext.setUserUuid(user.getUuid());
		webRequestContext.setCompanyId(securityContext.getCurrentUserCompanyId());
		webRequestContext.setCompanyUuid(securityContext.getCurrentUserCompanyUuid());
	}

	public void setCurrentUser(Long userId) {
		setCurrentUser(userDAO.findUserById(userId));
	}

	public void unsetCurrentUser() {
		// SecurityContextHolder.getContext().setAuthentication(null);
		securityContext.setCurrentUser(null);
	}

	public User getCurrentUser() {

		if (securityContext.getCurrentUser() != null) {
			return userDAO.findUserById(securityContext.getCurrentUser().getId());
		}

		return securityContext.getCurrentUser();
	}

	public User getCurrentUserWithFallback() {
		User currentUser = getCurrentUser();
		if (currentUser == null) {
			final String userUuid = webRequestContextProvider.getWebRequestContext().getUserUuid();
			if (userUuid != null) {
				currentUser = userService.findUserByUuid(userUuid);
			}
		}
		Assert.notNull(currentUser);
		return currentUser;
	}

	public Long getCurrentUserId() {

		if (securityContext.getCurrentUser() != null) {
			return securityContext.getCurrentUser().getId();
		}

		return null;
	}

	public Long getCurrentUserCompanyId() {

		if (securityContext.getCurrentUser() != null) {
			return securityContext.getCurrentUserCompanyId();
		}

		return null;
	}

	public void setCurrentUser(String userNumber) {
		setCurrentUser(userDAO.findUserByUserNumber(userNumber, false));
	}

	public void clearCurrentUser() {
		securityContext.clearContext();
	}

	public AclRole findSystemRoleByName(String aclRoleName) {
		Assert.hasText(aclRoleName);

		return aclRoleDAO.findSystemRoleByName(aclRoleName);

	}

	public List<AclRole> findAllAclRoles() {
		return aclRoleDAO.findAllAclRoles();
	}

	public List<AclRole> findAllAvailableAclRolesByCompany(Long companyId) {
		Assert.notNull(companyId);

		return aclRoleDAO.findAclRoles(companyId);
	}

	public List<AclRole> findAllInternalAclRoles() {
		return aclRoleDAO.findAllInternalAclRoles();
	}

	public Permission findPermissionByCode(String permissionCode) {
		Assert.notNull(permissionCode);

		return userRoleService.findPermissionByCode(permissionCode);
	}

	public void updateUserAclRoles(Long userId, List<Long> aclRoleIds) throws InvalidAclRoleException {
		Assert.notNull(userId);
		Assert.notNull(aclRoleIds);

		List<Long> currentRoles = new ArrayList<>();

		List<UserAclRoleAssociation> associationList = userRoleService.findAllRolesByUser(userId, true);
		for (UserAclRoleAssociation association : associationList) {
			currentRoles.add(association.getRole().getId());
		}

		List<Long> newRoles = new ArrayList<>(currentRoles);
		currentRoles.removeAll(aclRoleIds);
		newRoles.removeAll(currentRoles);
		aclRoleIds.removeAll(newRoles);

		// Adding the new roles
		for (Long roleId : aclRoleIds) {
			assignAclRoleToUser(userId, roleId);
		}
		// Removing the roles
		for (Long roleId : currentRoles) {
			removeAclRoleFromUser(userId, roleId);
		}

		userIndexer.reindexById(userId);
	}

	public void assignAclRolesToUser(Long userId, Long[] roleIds) throws InvalidAclRoleException {
		Assert.notNull(userId);
		Assert.notNull(roleIds);

		for (Long roleId : roleIds) {
			assignAclRoleToUser(userId, roleId);
		}
	}

	private void setPaymentCenterAndEmailsNotificationPrefs(Long userId, Boolean giveAccess) {
		userNotificationPrefsService.setPaymentCenterAndEmailsNotificationPrefs(userId, giveAccess);
	}

	public Boolean hasCustomAccessSettingsSet(Long userId) {
		return userRoleService.hasCustomAccessSettingsSet(userId);

	}

	public Boolean hasProjectAccess(Long userId) {
		return userRoleService.hasProjectAccess(userId);
	}

	public Boolean hasPaymentCenterAndEmailsAccess(Long userId, Boolean checkPermissionIfNoSettings) {
		List<UserCustomPermissionAssociation> userCustomPermissions = userRoleService.findAllCustomPermissionsByUser(userId);
		List<Permission> permissions = new ArrayList<>();
		Boolean hasInvoicePermission = Boolean.FALSE;
		Boolean hasPayInvoicePermission = Boolean.FALSE;
		Boolean hasPayAssignmentPermission = Boolean.FALSE;
		Boolean hasPayablesPermission = Boolean.FALSE;

		if (CollectionUtils.isEmpty(userCustomPermissions)) {
			if (checkPermissionIfNoSettings) {
				// Settings was not touched. Check if his roles give him these permissions
				permissions = userRoleService.findPermissionsByUser(userId);
			} else {
				return Boolean.TRUE;
			}
		} else {
			for (UserCustomPermissionAssociation userCustomPermission : userCustomPermissions) {
				if (userCustomPermission.getEnabled()) {
					permissions.add(userCustomPermission.getPermission());
				}
			}
		}

		//check for 4 variables fails if size less than 4
		if (permissions.size() <= 3) {
			return Boolean.FALSE;
		}

		for (Permission permission : permissions) {
			if (permission.getCode().equals(Permission.INVOICES)) {
				hasInvoicePermission = Boolean.TRUE;
			} else if (permission.getCode().equals(Permission.PAYABLES)) {
				hasPayablesPermission = Boolean.TRUE;
			} else if (permission.getCode().equals(Permission.PAY_ASSIGNMENT)) {
				hasPayAssignmentPermission = Boolean.TRUE;
			} else if (permission.getCode().equals(Permission.PAY_INVOICE)) {
				hasPayInvoicePermission = Boolean.TRUE;
			}
		}

		if (hasInvoicePermission && hasPayablesPermission && hasPayAssignmentPermission && hasPayInvoicePermission) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	private void setManageBankAndFundsNotificationPrefs(Long userId, Boolean giveAccess) {
		userNotificationPrefsService.setManageBankAndFundsNotificationPrefs(userId, giveAccess);
	}

	public Boolean hasManageBankAndFundsAccess(Long userId, Boolean checkPermissionIfNoSettings) {
		List<UserCustomPermissionAssociation> userCustomPermissions = userRoleService.findAllCustomPermissionsByUser(userId);
		List<Permission> permissions = new ArrayList<>();
		Boolean hasAddFundsPermission = Boolean.FALSE;
		Boolean hasManageBankPermission = Boolean.FALSE;
		Boolean hasWithdrawFundsPermission = Boolean.FALSE;

		if (CollectionUtils.isEmpty(userCustomPermissions)) {
			if (checkPermissionIfNoSettings) {
				// Settings was not touched. Check if his roles give him these permissions
				permissions = userRoleService.findPermissionsByUser(userId);
			} else {
				return Boolean.TRUE;
			}
		} else {
			for (UserCustomPermissionAssociation userCustomPermission : userCustomPermissions) {
				if (userCustomPermission.getEnabled()) {
					permissions.add(userCustomPermission.getPermission());
				}
			}
		}

		if (permissions.size() <= 2) {
			return Boolean.FALSE;
		}

		for (Permission permission : permissions) {
			if (permission.getCode().equals(Permission.ADD_FUNDS)) {
				hasAddFundsPermission = Boolean.TRUE;
			} else if (permission.getCode().equals(Permission.MANAGE_BANK_ACCOUNTS)) {
				hasManageBankPermission = Boolean.TRUE;
			} else if (permission.getCode().equals(Permission.WITHDRAW_FUNDS)) {
				hasWithdrawFundsPermission = Boolean.TRUE;
			}
		}

		if (hasAddFundsPermission && hasManageBankPermission && hasWithdrawFundsPermission) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public void assignAclRoleToUser(Long userId, Long roleId) throws InvalidAclRoleException {
		Assert.notNull(userId);
		Assert.notNull(roleId);
		User user = userDAO.get(userId);
		AclRole role = aclRoleDAO.findRoleById(roleId);
		Assert.notNull(user);
		Assert.notNull(role);

		userRoleService.addUserRoleAssociation(user, role);

		// if the new Role is ACL_SHARED_WORKER we automatically approve them. APP-22233
		if (roleId.equals(AclRole.ACL_SHARED_WORKER)) {
			user.setLane3ApprovalStatus(ApprovalStatus.APPROVED);
			userService.updateUserStatus(userId, new UserStatusType(UserStatusType.APPROVED));
		}

		// Add user to Lane 1 if he can take on assignments
		// FIXME : This shouldn't be here
		if (roleId.equals(AclRole.ACL_WORKER) || roleId.equals(AclRole.ACL_EMPLOYEE_WORKER)) {
			if (!laneService.isUserPartOfLane123(user.getId(), user.getCompany().getId())) {
				laneService.addUserToCompanyLane1(user.getId(), user.getCompany().getId());
			}
		}

		userIndexer.reindexById(userId);
		userChangeLogDAO.saveOrUpdate(new UserAclRoleAddedChangeLog(user.getId(), getCurrentUserId(), getMasqueradeUserId(), role));
	}

	public void removeAclRoleFromUser(Long userId, Long roleId) {
		Assert.notNull(userId);
		Assert.notNull(roleId);

		UserAclRoleAssociation userRole = userRoleService.findUserRoleAssociation(userId, roleId);
		User user = userService.getUser(userId);

		if (userRole != null) {

			if (roleId.equals(AclRole.ACL_ADMIN)) {
				// Make sure there are more user with the admin role before delete it
				List<User> admins = findAllUsersByACLRoleAndCompany(user.getCompany().getId(), AclRole.ACL_ADMIN);
				Assert.isTrue(admins.size() > 1, "Administrator role can't be removed from User " + userId + ". Company " + user.getCompany().getId() + " has no other user with this role.");
			}

			if (roleId.equals(AclRole.ACL_SHARED_WORKER)) {
				user.setLane3ApprovalStatus(ApprovalStatus.OPT_OUT);
			}

			userRoleService.removeAclRoleAssociation(user, userRole);
			userIndexer.reindexById(userId);
			userChangeLogDAO.saveOrUpdate(new UserAclRoleRemovedChangeLog(userId, getCurrentUserId(), getMasqueradeUserId(), userRole.getRole()));
		}
	}

	public boolean userHasAclRole(Long userId, Long roleId) {
		Assert.notNull(userId);
		Assert.notNull(roleId);
		UserAclRoleAssociation userRole = userRoleService.findUserRoleAssociation(userId, roleId);

		return (userRole != null && !userRole.getDeleted());
	}

	public boolean userHasAclRoles(Long userId, Long[] roleIds, Boolean inclusive) {
		if (inclusive) {
			boolean passes = true;
			for (Long roleId : roleIds) {
				passes = (passes && userHasAclRole(userId, roleId));
			}
			return passes;
		}

		for (Long roleId : roleIds) {
			if (userHasAclRole(userId, roleId)) {
				return true;
			}
		}

		return false;
	}

	public List<AclRole> findAllAssignedAclRolesByUser(Long userId) {
		Assert.notNull(userId);

		return findAllAssignedAclRolesByUser(userId, true);
	}

	public List<AclRole> findAllAssignedAclRolesByUser(Long userId, boolean includeVirtualRoles) {
		Assert.notNull(userId);
		List<UserAclRoleAssociation> userRoleList = userRoleService.findAllRolesByUser(userId, includeVirtualRoles);
		List<AclRole> aclRoleList = new ArrayList<>();

		for (UserAclRoleAssociation association : userRoleList) {
			aclRoleList.add(association.getRole());
		}

		return aclRoleList;
	}

	public List<Permission> findAllAssignedPermissionsByUser(Long userId) {
		Assert.notNull(userId);

		return userRoleService.findPermissionsByUser(userId);
	}

	public Set<User> findAllUsersByPermissionAndCompany(Long companyId, String permissionCode) {
		Assert.notNull(permissionCode);
		Assert.notNull(companyId);

		return Sets.newHashSet(userDAO.findByAclPermissionCode(companyId, permissionCode));
	}

	public List<User> findAllUsersByACLRoleAndCompany(Long companyId, Long aclRoleId) {
		Assert.notNull(aclRoleId);
		Assert.notNull(companyId);
		Set<User> usersToReturn = Sets.newHashSet();
		usersToReturn.addAll(userDAO.findAllUsersByACLRoleAndCompany(companyId, aclRoleId));

		return Lists.newArrayList(usersToReturn);
	}

	public Set<User> findAllAdminAndControllerUsersByCompanyId(Long companyId) {
		Assert.notNull(companyId);
		Set<User> usersToReturn = Sets.newHashSet();
		usersToReturn.addAll(findAllUsersByACLRoleAndCompany(companyId, AclRole.ACL_CONTROLLER));
		usersToReturn.addAll(findAllUsersByACLRoleAndCompany(companyId, AclRole.ACL_ADMIN));

		return usersToReturn;
	}

	public <T extends AbstractInvoice> Set<User> findAllUsersSubscribedToInvoice(Long companyId, T invoice) {
		Assert.notNull(companyId);
		Set<User> users = Sets.newHashSet();

		if (invoice instanceof Statement) {
			users = new HashSet<>(userNotificationPrefsService.findUsersByCompanyAndNotificationType(companyId, NotificationType.STATEMENT_REMINDER));
		} else if (invoice instanceof SubscriptionInvoice) {
			users = new HashSet<>(userNotificationPrefsService.findUsersByCompanyAndNotificationType(companyId, NotificationType.SUBSCRIPTION_REMINDER));
		} else if (invoice instanceof AdHocInvoice) {
			users = new HashSet<>(userNotificationPrefsService.findUsersByCompanyAndNotificationType(companyId, NotificationType.INVOICE_CREATED_ON_ASSIGNMENT));
		}

		return users;
	}

	public Set<User> findAllUsersSubscribedToNewAssignmentInvoices(Work work) {
		Assert.notNull(work);

		Set<User> usersSubscribedToNewAssignmentInvoices = new HashSet<>(userNotificationPrefsService.findUsersByCompanyAndNotificationType(work.getCompany().getId(), NotificationType.INVOICE_CREATED_ON_ASSIGNMENT));

		if (work.getBuyerSupportUser() != null) {

			if (isUserNotificationPreferenceEmailTypeEnabled(work.getBuyerSupportUser().getId(), NotificationType.INVOICE_CREATED_ON_ASSIGNMENT)) {
				usersSubscribedToNewAssignmentInvoices.add(work.getBuyerSupportUser());
			}
		}

		if (isUserNotificationPreferenceEmailTypeEnabled(work.getBuyer().getId(), NotificationType.INVOICE_CREATED_ON_ASSIGNMENT)) {
			usersSubscribedToNewAssignmentInvoices.add(work.getBuyer());
		}

		return usersSubscribedToNewAssignmentInvoices;
	}

	public Boolean isUserNotificationPreferenceEmailTypeEnabled(Long userId, String notificationCode) {
		Assert.notNull(userId);

		UserNotificationPreferencePojo preference = userNotificationPrefsService.findByUserAndNotificationType(userId, notificationCode);
		Assert.notNull(preference);
		return preference.getEmailFlag();
	}

	public boolean authorizeUserForWork(Long workId) {
		Assert.notNull(workId);

		return authorizeUserForWork(workDAO.get(workId));
	}

	private <T extends AbstractWork> boolean authorizeUserForWork(T work) {
		Assert.notNull(work);
		User user = getCurrentUser();

		return ((user.getCompany().getId().equals(work.getCompany().getId())) || workService.isUserWorkResourceForWork(user.getId(), work.getId()));
	}

	public boolean authorizeUserByAclPermission(long userId, String permissionCode) {
		Assert.hasText(permissionCode);
		Permission permission = permissionDAO.findPermissionByUserAndPermissionCode(userId, permissionCode);

		return (permission != null);
	}

	public void startMasquerade(Long masqueradeUserId, Long userId) {
		Assert.notNull(masqueradeUserId);
		Assert.notNull(userId);

		User masqueradeUser = userService.findUserById(masqueradeUserId);
		User user = userService.findUserById(userId);

		securityContext.setCurrentAndMasqueradeUsers(user, masqueradeUser);
	}

	public boolean isMasquerading() {
		return securityContext.isMasquerading();
	}

	public User getMasqueradeUser() {
		return securityContext.getMasqueradeUser();
	}

	public void setMasqueradeUser(User currentLoggedInMasqUser) {
		securityContext.setMasqueradeUser(currentLoggedInMasqUser);
	}

	public Long getMasqueradeUserId() {
		if (securityContext.getMasqueradeUser() != null) {
			return securityContext.getMasqueradeUser().getId();
		}

		return null;
	}

	public boolean hasAccessToAllInvoicesAndStatementsAtCompany(User user) {
		// WORK-4641: For payables set the permission based on the role, not the permission
		return (userRoleService.hasAclRole(user, AclRole.ACL_CONTROLLER) || userRoleService.isAdminOrManager(user));
	}

	public void refreshSessionForUser(Long userId) {
		redisAdapter.set(
			RedisFilters.refreshSecurityContextKeyForUser(userId), String.valueOf(System.currentTimeMillis()),
			Long.valueOf(SESSION_TIMEOUT_IN_MINUTES) * 60);
	}

	public void refreshSessionForCompany(Long companyId) {
		redisAdapter.set(
			RedisFilters.refreshSecurityContextKeyForCompany(companyId), String.valueOf(System.currentTimeMillis()),
			Long.valueOf(SESSION_TIMEOUT_IN_MINUTES) * 60);
		hydratorCache.updateCompanyCache(companyId);
	}

	public void refreshSessionForAll() {
		redisAdapter.set(
			RedisFilters.refreshSecurityContextKeyForAll(), String.valueOf(System.currentTimeMillis()),
			Long.valueOf(SESSION_TIMEOUT_IN_MINUTES) * 60);
	}

	public void setCustomAccess(
			final Boolean hasPaymentAccess,
			final Boolean hasFundsAccess,
			final Boolean hasCounterOfferAccess,
			final Boolean hasEditPricingAccess,
			final Boolean hasWorkApproveAccess,
			final Boolean hasProjectAccess,
			final Long userId) {

		final Boolean canManageBankAndFunds = hasPaymentAccess && hasFundsAccess;
		final List<Pair<String, Boolean>> permSettings = ImmutableList.<Pair<String, Boolean>>builder()
			.add(Pair.of(Permission.ADD_FUNDS, canManageBankAndFunds))
			.add(Pair.of(Permission.WITHDRAW_FUNDS, canManageBankAndFunds))
			.add(Pair.of(Permission.MANAGE_BANK_ACCOUNTS, canManageBankAndFunds))
			.add(Pair.of(Permission.INVOICES, hasPaymentAccess))
			.add(Pair.of(Permission.PAY_INVOICE, hasPaymentAccess))
			.add(Pair.of(Permission.PAY_ASSIGNMENT, hasPaymentAccess))
			.add(Pair.of(Permission.PAYABLES, hasPaymentAccess))
			.add(Pair.of(Permission.COUNTEROFFER_AUTH, hasCounterOfferAccess))
			.add(Pair.of(Permission.EDIT_PRICING_AUTH, hasEditPricingAccess))
			.add(Pair.of(Permission.APPROVE_WORK_AUTH, hasWorkApproveAccess))
			.add(Pair.of(Permission.MANAGE_PROJECTS, hasProjectAccess))
			.build();
		userRoleService.setCustomPermissionsToUser(userId, permSettings);
		refreshSessionForUser(userId);

		userNotificationPrefsService.setPaymentCenterAndEmailsNotificationPrefs(userId, hasPaymentAccess);
		setManageBankAndFundsNotificationPrefs(userId, canManageBankAndFunds);
	}

	public User findUserByJsessionId(final String jsessionId) {
		final ValidationResponse result = authClient.validate(jsessionId, trialCommon.getApiContext())
			.toBlocking().single();
		final Status status = result.getStatus();
		if (status.getSuccess()) {
			return userDAO.findByUuid(result.getUserUuid());
		}
		return null;
	}

	public UserStatusType getUserStatus(final User user) {
		final Pair<String, Calendar> stringCalendarPair = userStatusCache.get().get(user.getUuid(), user);
		return new UserStatusType(stringCalendarPair.getLeft());
	}

	public Boolean getEmailConfirmed(final User user) {
		return emailConfirmedCache.get().get(user.getUuid(), user).getLeft();
	}

	public boolean isLane1Active(User user) {
		final UserStatusType status = getUserStatus(user);
		if (status == null) {
			return false;
		}
		if (UserStatusType.APPROVED_STATUS.equals(status)
				|| UserStatusType.PENDING_STATUS.equals(status)) {
			return userRoleService.hasAclRole(user, AclRole.ACL_WORKER) && getEmailConfirmed(user);
		}
		return false;
	}

	public boolean isLane2Active(User user) {
		if (!getEmailConfirmed(user)) {
			return false;
		}

		final UserStatusType status = getUserStatus(user);
		return status != null && (
				UserStatusType.APPROVED_STATUS.equals(status)
				|| UserStatusType.PENDING_STATUS.equals(status));
	}

	public boolean isLane3Active(User user) {
		if (!getEmailConfirmed(user)) {
			return false;
		}
		return user.isLane3Approved() && isActive(user) && userRoleService.hasAclRole(user, AclRole.ACL_SHARED_WORKER);
	}

	public boolean isLane4Active(User user) {
		if (!getEmailConfirmed(user)) {
			return false;
		}
		if (!user.isLane3Approved()) {
			return false;
		}
		return isActive(user) && userRoleService.hasAclRole(user, AclRole.ACL_SHARED_WORKER);
	}

	public boolean isSystemUser(final User user) {
		return Constants.SYSTEM_USER_IDS.contains(user.getId());
	}

	public boolean isApproved(final User user) {
		return getUserStatus(user).getCode().equals(UserStatusType.APPROVED);
	}

	// ROOT METHODS!
	private Pair<String, Calendar> getUncachedUserStatusPair(final User user) {
		final Callable<Observable<Pair<String, Calendar>>> control =
			new WMCallable<Observable<Pair<String, Calendar>>>(webRequestContextProvider) {
				@Override
				public Observable<Pair<String, Calendar>> apply() throws Exception {
					return Observable.just(Pair.of(user.getUserStatusType().getCode(), user.getUserStatusTypeModifiedOn()));
				}
			};

		final String uuid = user.getUuid();
		final RequestContext apiContext = trialCommon.getApiContext();
		final Callable<Observable<Pair<String, Calendar>>> experiment =
			new WMCallable<Observable<Pair<String, Calendar>>>(webRequestContextProvider) {
				@Override
				public Observable<Pair<String, Calendar>> apply() throws Exception {
					return authClient.getUserStatus(ImmutableList.of(user.getUuid()), apiContext)
						.map(new Func1<GetUserStatusResponse, Pair<String, Calendar>>() {
							@Override
							public Pair<String, Calendar> call(GetUserStatusResponse resp) {
								final Messages.UserStatusResult statuses = resp.getStatuses(0);
								final UserStatus status = statuses.getStatus();
								final UserStatusType statusType = new UserStatusType(
									convertUserStatusToUserStatusType(status));
								return Pair.of(statusType.getCode(), timestampToCalendar(statuses.getChangedOn()));
							}
						})
						.singleOrDefault(Pair.<String, Calendar>of(null, null));
				}
		};
		try {
			return trialCommon.getStateTrial().doTrial(control, experiment, userStatusPairIsEqual(uuid),
					"get_user_status").toBlocking().singleOrDefault(null);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String safeToString(final Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString();
	}

	private IsEqual<TrialResult<Pair<String, Calendar>>> userStatusPairIsEqual(final String userUuid) {
		return Trial.makeIsEqual(trialCommon.makeBothOrNeitherThrow("userStatusPairIsEqual"),
			new IsEqual<Pair<String, Calendar>>() {
				@Override
				public boolean apply(
						final Pair<String, Calendar> control,
						final Pair<String, Calendar> experiment) {
					final List<String> mismatches = new ArrayList<>();
					final IsEqualUtil.MismatchConsumer consumer = IsEqualUtil.consumeToList(mismatches);
					final boolean success = checkNullity(control, experiment, consumer)
						&& startCompare(consumer)
						.dotEquals(control.getLeft(), experiment.getLeft(), "user_status")
						.isEquals(control.getRight(), experiment.getRight(), new CalendarIsEqual(), "changed_on")
						.get();
					if (success) {
						return true;
					}
					KAFKA_CLIENT.send(TRIAL_LOG_TOPIC, getStringObjectMap(
							userUuid,
							Pair.of(safeToString(control.getRight()), control.getLeft()),
							Pair.of(safeToString(experiment.getRight()), experiment.getLeft()), "statusPairIsEqual",
							mismatches, "userStatusPairIsEqual"));
					return false;
				}
			}.pairwiseEqual()
		);
	}

	private String convertUserStatusToUserStatusType(UserStatus status) {
		switch (status) {
			case NOT:
				break;
			case DEACTIVATED:
				return UserStatusType.DEACTIVATED;
			case SUSPENDED:
				return UserStatusType.SUSPENDED;
			case LOCKED:
				return UserStatusType.LOCKED;
			case BREACHED:
				return UserStatusType.LOCKED;
			case PENDING:
				return UserStatusType.PENDING;
			case DELETED:
				return UserStatusType.DELETED;
			case APPROVED:
				return UserStatusType.APPROVED;
			case HOLD:
				return UserStatusType.HOLD;
			case UNRECOGNIZED:
				break;
		}
		return null;
	}

	private Calendar timestampToCalendar(long millis) {
		if (millis == 0) {
			return null;
		}
		final Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(millis);
		return instance;
	}

	private Pair<Boolean, Calendar> getUncachedEmailConfirmed(final User user) {
		final Callable<Observable<Pair<Boolean, Calendar>>> control =
			new WMCallable<Observable<Pair<Boolean, Calendar>>>(webRequestContextProvider) {
				@Override
				public Observable<Pair<Boolean, Calendar>> apply() throws Exception {
					return Observable.just(Pair.of(user.getEmailConfirmed(), user.getEmailConfirmedOn()));
				}
			};

		final String userUuid = user.getUuid();
		final RequestContext apiContext = trialCommon.getApiContext();

		logger.debug("getUncachedEmailConfirmed(): for UUID {}, RC {}", userUuid, apiContext.toString());

		final Callable<Observable<Pair<Boolean, Calendar>>> experiment =
			new WMCallable<Observable<Pair<Boolean, Calendar>>>(webRequestContextProvider) {
				@Override
				public Observable<Pair<Boolean, Calendar>> apply() throws Exception {
					return authClient
						.getEmailVerified(ImmutableList.of(user.getUuid()), apiContext)
						.map(new Func1<Messages.GetEmailVerifiedResponse, Pair<Boolean, Calendar>>() {
							@Override
							public Pair<Boolean, Calendar> call(Messages.GetEmailVerifiedResponse response) {
								final UserEmailVerified verification = response.getVerifications(0);
								return Pair.of(verification.getVerified(),
									timestampToCalendar(verification.getChangedOn()));
							}
						});
				}
			};

		try {
			return trialCommon.getStateTrial().doTrial(control, experiment, emailConfirmedPairIsEqual(userUuid),
					"get_email_confirmed").toBlocking().singleOrDefault(null);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private IsEqual<TrialResult<Pair<Boolean, Calendar>>> emailConfirmedPairIsEqual(final String userUuid) {
		return Trial.makeIsEqual(
				trialCommon.makeBothOrNeitherThrow("emailConfirmedIsEqual"),
				new IsEqual<Pair<Boolean, Calendar>>() {
					@Override
					public boolean apply(Pair<Boolean, Calendar> control, Pair<Boolean, Calendar> experiment) {
						final List<String> mismatches = new ArrayList<>();
						final IsEqualUtil.MismatchConsumer consumer = IsEqualUtil.consumeToList(mismatches);
						final boolean success = checkNullity(control, experiment, consumer)
							&& startCompare(consumer)
							.dotEquals(control.getLeft(), experiment.getLeft(), "email_verified")
							.isEquals(control.getRight(), experiment.getRight(), new CalendarIsEqual(), "changed_on")
							.get();
						if (success) {
							return true;
						}
						KAFKA_CLIENT.send(TRIAL_LOG_TOPIC, getStringObjectMap(userUuid,
							control.getLeft(), experiment.getLeft(),
							"emailConfirmedIsEqual", mismatches, "emailConfirmedIsEqual"));
						return false;
					}
				}.pairwiseEqual()
		);
	}

	public void setEmailConfirmed(final User user, final Boolean confirmed) {
		final RequestContext apiContext = trialCommon.getApiContext();
		trialCommon.runNoOpControlExperiment(new WMCallable<Observable<Boolean>>(webRequestContextProvider) {
			@Override
			public Observable<Boolean> apply() throws Exception {
				return authClient
					.setEmailVerified(user.getUuid(), confirmed, apiContext)
					.map(new StatusToBoolean());
			}
		}, "set_email_confirmed", trialCommon.getStateTrial());

		setCachedEmailConfirmed(confirmed, user.getUuid());
		user.setEmailConfirmed(confirmed);
	}

	private void setCachedEmailConfirmed(Boolean confirmed, String uuid) {
		emailConfirmedCache.get().set(uuid, Pair.of(confirmed, Calendar.getInstance()));
	}

	public void setUserStatus(final User user, final UserStatusType status) {
		final RequestContext apiContext = trialCommon.getApiContext();
		trialCommon.runNoOpControlExperiment(new WMCallable<Observable<Boolean>>(webRequestContextProvider) {
			@Override
			public Observable<Boolean> apply() throws Exception {
				return authClient.setUserStatus(user.getUuid(), convertStatusType(status), apiContext)
					.map(new StatusToBoolean());
			}
		}, "set_user_status", trialCommon.getStateTrial());

		setCachedUserStatus(user.getUuid(), status); // new UserStatusType(status.getCode()));
		user.setUserStatusType(status);
	}

	private void setCachedUserStatus(final String uuid, final UserStatusType status) {
		userStatusCache.get().set(uuid, Pair.of(status.getCode(), Calendar.getInstance()));
	}

	public Calendar getUserStatusTypeModifiedOn(final User user) {
		return userStatusCache.get().get(user.getUuid(), user).getRight();
	}

	@Deprecated // will go away once auth is authoratitive and old stuff turned down.
	public void setEmailConfirmedOn(final User user, final Calendar when) {
		user.setEmailConfirmedOn(when);
	}

	public Calendar getEmailConfirmedOn(final User user) {
		return emailConfirmedCache.get().get(user.getUuid(), user).getRight();
	}

	// END ROOT METHODS

	public boolean isSuspended(final User user) {
		return getUserStatus(user).getCode().equals(UserStatusType.SUSPENDED);
	}

	public boolean isDeactivated(final User user) {
		return getUserStatus(user).getCode().equals(UserStatusType.DEACTIVATED);
	}

	public boolean isDeleted(final User user) {
		return getUserStatus(user).getCode().equals(UserStatusType.DELETED);
	}

	public boolean isPending(final User user) {
		return getUserStatus(user).getCode().equals(UserStatusType.PENDING);
	}

	/**
	 * Only approved and internal users are active
	 *
	 * @return true iff user is internal or approved
	 */
	public boolean isActive(final User user) {
		return UserStatusType.APPROVED_STATUS.equals(getUserStatus(user));
	}

	public boolean isSearchable(final User user) {
		final UserStatusType userStatus = getUserStatus(user);
		return userStatus != null
			&& (UserStatusType.APPROVED_STATUS.equals(userStatus)
				|| UserStatusType.PENDING_STATUS.equals(userStatus));
	}

	public boolean isLocked(final User user) {
		final UserStatusType userStatus = getUserStatus(user);
		return (!(userStatus == null) && UserStatusType.LOCKED_STATUS.equals(userStatus));
	}


	public void reportRecaptchaFailure(User user, String inetAddress) {
		final RequestContext ctx = trialCommon.getApiContext();
		authClient.recaptchaFail(user.getUuid(), inetAddress, ctx).toBlocking().single();
		handleLoginResult(user, inetAddress, false);
	}

	private void handleLoginResult(User user, String inetAddress, boolean loginOk) {
		final Long userId = user.getId();
		final Long coId = user.getCompany() != null ? user.getCompany().getId() : null;

		webActivityAuditService.saveLoginInfo(userId, Calendar.getInstance(), coId,
			inetAddress, loginOk);

		 /*
		  * On unsuccessful login, check that user hasn't tried to login more than MAX_FAILED_LOGIN_ATTEMPTS
		  * If he has, lock his account.
		  */
		if (!loginOk && !isLocked(user)) {
			if (shouldLockUser(user.getUuid())) {
				userService.lockUser(userId);
			}
		}
	}

	private int getLastConsecutiveUnsuccessfulLoginsByUser(String uuid) {
		final RequestContext ctx = trialCommon.getApiContext();
		return authClient.getConsecutiveFailedLogins(uuid, ctx)
			.toBlocking()
			.single()
			.getCount();
	}

	private int getRelativeLastConsecutiveUnsuccessfulLoginsByUser(final String uuid) {
		return getLastConsecutiveUnsuccessfulLoginsByUser(uuid) % Constants.MAX_FAILED_LOGIN_ATTEMPTS;
	}

	private boolean shouldLockUser(final String uuid) {
		return getRelativeLastConsecutiveUnsuccessfulLoginsByUser(uuid) == 0;
	}

	public boolean isRecaptchaEnabledOnUser(final User user) {
		if (user == null) {
			return true;
		}
		return shouldEnableRecaptcha(user);
	}

	private boolean shouldEnableRecaptcha(final User user) {
		return featureEvaluator.hasGlobalFeature(RECAPTCHA) &&
			(isLocked(user) || getRelativeLastConsecutiveUnsuccessfulLoginsByUser(user.getUuid()) >= Constants.GOOGLE_RECAPTCHA_ENABLED_ON_FAILED_ATTEMPTS);
	}
}
