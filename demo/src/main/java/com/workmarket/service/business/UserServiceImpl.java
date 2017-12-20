package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.workmarket.api.exceptions.ForbiddenException;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.BlockedAssociationDAO;
import com.workmarket.dao.UserAvailabilityDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.dao.changelog.user.UserChangeLogDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.notification.UserDeviceAssociationDAO;
import com.workmarket.dao.user.PersonaPreferenceDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.BlockedCompanyUserAssociationPagination;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.Sort;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.block.BlockedCompanyCompanyAssociation;
import com.workmarket.domains.model.block.BlockedCompanyUserAssociation;
import com.workmarket.domains.model.block.BlockedUserCompanyAssociation;
import com.workmarket.domains.model.block.BlockedUserUserAssociation;
import com.workmarket.domains.model.changelog.user.UserBlockedByCompanyChangeLog;
import com.workmarket.domains.model.changelog.user.UserStatusChangeLog;
import com.workmarket.domains.model.company.CustomerType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.notification.DeviceType;
import com.workmarket.domains.model.notification.UserDeviceAssociation;
import com.workmarket.domains.model.user.CompanyUserPagination;
import com.workmarket.domains.model.user.NotificationAvailability;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.model.user.RecentUserPagination;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.domains.model.user.WorkAvailability;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.model.EmployeeWorkerRoleAdmitted;
import com.workmarket.domains.velvetrope.rope.EmployeeWorkerRoleRope;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.redis.RedisConfig;
import com.workmarket.search.model.SearchUser;
import com.workmarket.service.business.dto.UserAvailabilityDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.UserIdentityDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.EncryptionUtilities;
import com.workmarket.utility.ProjectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.Doorman;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;
import static com.workmarket.configuration.Constants.WORKMARKET_SYSTEM_USER_ID;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class UserServiceImpl implements UserService {

	@Autowired private CompanyDAO companyDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private BlockedAssociationDAO blockedAssociationDAO;
	@Autowired private UserAvailabilityDAO userAvailabilityDAO;
	@Autowired private UserAssetAssociationDAO userAssetAssociationDAO;
	@Autowired private UserChangeLogDAO userChangeLogDAO;
	@Autowired private PersonaPreferenceDAO personaDAO;
	@Autowired private EmailTemplateFactory emailTemplateFactory;
	@Autowired private NotificationService notificationService;
	@Autowired private ProfileService profileService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private UserGroupValidationService userGroupValidationService;
	@Autowired private UserIndexer userIndexer;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private SummaryService summaryService;
	@Autowired private WorkService workService;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private UserDeviceAssociationDAO userDeviceAssociationDAO;
	@Autowired private UserRoleService userRoleService;
	@Autowired private CompanyService companyService;
	@Autowired @Qualifier("employeeWorkerRoleDoorman") private Doorman employeeWorkerRoleRope;

	private static final Log logger = LogFactory.getLog(UserServiceImpl.class);
	public static final String BLOCKED_USER_IDS = RedisConfig.BLOCKED_USER_IDS;
	public static final String SEARCH_USER = RedisConfig.SEARCH_USER;

	@Override
	public List<User> findAllUsersByIds(Collection<Long> userIds) {
		return (List<User>) userDAO.findAllByUserIds(userIds);
	}

	@Override
	public List<User> findAllUsersWithProfileAndCompanyByIds(Collection<Long> userIds) {
		return (List<User>) userDAO.findAllWithProfileAndCompanyByUserIds(userIds);
	}

	@Override
	public Set<User> findAllUsersByUserNumbers(List<String> userNumbers) {
		return userDAO.findAllByUserNumbers(userNumbers);
	}

	@Override
	public Set<Long> findAllUserIdsByUserNumbers(Collection<String> userNumbers) {
		Assert.notEmpty(userNumbers);
		logger.debug("Finding userIds in UserService for userNumbers: " + userNumbers.toString());
		return userDAO.findAllUserIdsByUserNumbers(userNumbers);
	}

	@Override
	public Set<Long> findAllUserIdsByUuids(Collection<String> userUuids) {
		Assert.notEmpty(userUuids);
		logger.debug("Finding userIds in UserService for userUuids: " + userUuids.toString());
		return userDAO.findAllUserIdsByUuids(userUuids);
	}

	@Override
	public Set<String> findAllUserUuidsByIds(Collection<Long> userIds) {
		Assert.notEmpty(userIds);
		logger.debug("Finding userUuids in UserService for userIds: " + userIds.toString());
		return userDAO.findAllUserUuidsByIds(userIds);
	}

	@Override
	public Set<String> findAllUserNumbersByUserIds(Collection<Long> userIds) {
		Assert.notEmpty(userIds);
		logger.debug("Finding userNumbers in UserService for userIds: " + userIds.toString());
		return userDAO.findAllUserNumbersByUserIds(userIds);
	}

	@Override
	public Long findUserId(String userNumber) {
		return userDAO.findUserId(userNumber);
	}

	@Override
	public String findUserUuidById(final Long id) {
		return userDAO.findUserUuidById(id);
	}

	@Override
	public Long findUserIdByUuid(String userUuid) {
		return userDAO.findUserIdByUuid(userUuid);
	}

	@Override
	public Long findUserIdByEmail(String email) {
		return userDAO.findUserIdByEmail(email);
	}

	@Override
	public String findUserNumber(Long id) {
		return userDAO.findUserNumber(id);
	}

	@Override
	public User getUser(Long id) {
		Assert.notNull(id);
		return userDAO.findUserById(id);
	}

	@Override
	@Cacheable(
		value = SEARCH_USER,
		key = "#root.target.SEARCH_USER + #id"
	)
	public SearchUser getSearchUser(Long id) {
		Assert.notNull(id);
		return userDAO.getSearchUser(id);
	}

	@Override
	public User getUserWithRoles(Long id) {
		return userDAO.getUserWithRoles(id);
	}

	@Override
	public User findUserById(long id) {
		return userDAO.findUserById(id);
	}

	@Override
	public User findUserByUserNumber(String userNumber) {
		return userDAO.findUserByUserNumber(userNumber, true);
	}

	@Override
	public User findUserByUuid(String uuid) {
		return userDAO.findBy("uuid", uuid);
	}

	@Override
	public List<UserDTO> findUserDTOsByUuids(final List<String> uuids) {
		return userDAO.findUserDTOsByUuids(uuids);
	}

	@Override
	public User findUserByEncryptedId(String id) {
		Assert.notNull(id);
		Long userId;
		try {
			userId = EncryptionUtilities.decryptLong(id);
		} catch (EncryptionOperationNotPossibleException e) {
			logger.error(e.getMessage());
			return null;
		}

		return getUser(userId);
	}

	@Override
	public User findUserByEmail(String email) {
		return userDAO.findUserByEmail(email);
	}

	@Override
	public User findCreatorByAssetId(Long assetId) {
		Assert.notNull(assetId);
		return userDAO.findCreatorByAssetIdInHibernateTransaction(assetId);
	}

	@Override
	public User findModifierByAssetId(Long assetId) {
		Assert.notNull(assetId);
		return userDAO.findModifierByAssetId(assetId);
	}

	@Override
	public User findCreatorByWorkLabelAssociationId(Long id) {
		Assert.notNull(id);
		return userDAO.findCreatorByWorkLabelAssociationId(id);
	}

	@Override
	public User findModifierByWorkLabelAssociationId(Long workLabelId) {
		Assert.notNull(workLabelId);
		return userDAO.findModifierByWorkLabelAssociationId(workLabelId);
	}

	@Override
	public User findCreatorByWorkNegotiationId(Long workNegotiationId) {
		Assert.notNull(workNegotiationId);
		return userDAO.findCreatorByWorkNegotiationId(workNegotiationId);
	}

	@Override
	public boolean emailExists(String email, Long userId) {

		logger.debug("Checking if email exists for user" + email + "," + userId);
		User user = userDAO.findUser(email);
		return user != null && (!user.getId().equals(userId));
	}

	@Override
	public List<User> findUsersByPhoneNumber(String phoneNumber) {
		Assert.notNull(phoneNumber);
		return userDAO.findByPhoneNumber(phoneNumber);
	}

	@Override
	public UserPagination findAllPendingLane3Users(UserPagination pagination) {
		return userDAO.findAllPendingLane33Users(pagination);
	}

	@Override
	public UserPagination findAllSuspendedUsers(UserPagination pagination) {
		return userDAO.findAllUsersByUserStatusType(pagination, new UserStatusType(UserStatusType.SUSPENDED));
	}

	@Override
	public void updateLane3ApprovalStatus(Long userId, ApprovalStatus approvalStatus) {
		userDAO.updateLane3ApprovalStatus(userId, approvalStatus);
	}

	public void holdUser(Long userId, Boolean holdFlag) {
		if (holdFlag) {
			updateUserStatus(userId, new UserStatusType(UserStatusType.HOLD));
		} else {
			updateUserStatus(userId, new UserStatusType(UserStatusType.APPROVED));
		}
	}

	public void suspendUser(Long userId, Boolean suspendFlag) {
		UserStatusType newStatus = new UserStatusType(UserStatusType.APPROVED);
		if (suspendFlag) {
			// TODO Actions as result of suspension: cannot accept work, disinvite from active work, remove from search index, etc...
			newStatus = new UserStatusType(UserStatusType.SUSPENDED);
			updateUserStatus(userId, newStatus);
		} else {
			User user = getUser(userId);
			Assert.isTrue(authenticationService.isSuspended(user), "User is not suspended");
			// TODO Re-enable all the things that we disabled as a result of suspension
			userChangeLogDAO.saveOrUpdate(
				new UserStatusChangeLog(
					userId, authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId(),
						authenticationService.getUserStatus(user), newStatus
				)
			);
			authenticationService.setUserStatus(user, newStatus);
			summaryService.saveUserHistorySummary(user);
			authenticationService.refreshSessionForUser(userId);
		}
	}

	public void deactivateUser(Long userId, Long newWorkOwnerId, Long newGroupOwnerId, Long newAssessmentOwnerId) {
		if (!CollectionUtilities.containsAny(userId, newWorkOwnerId, newGroupOwnerId, newAssessmentOwnerId)) {
			eventRouter.sendEvent(
				eventFactory.buildUserReassignmentEvent(
					userId, newGroupOwnerId, newWorkOwnerId, newAssessmentOwnerId));
		}
		updateUserStatus(userId, new UserStatusType(UserStatusType.DEACTIVATED));
	}

	public void reactivateUser(Long userId) {
		updateUserStatus(userId, new UserStatusType(UserStatusType.APPROVED));
	}


	public void deleteUser(Long userId) {
		updateUserStatus(userId, new UserStatusType(UserStatusType.DELETED));
	}

	public boolean deleteUserIfNotConfirmed(Long userId) {
		Assert.notNull(userId);
		User user = this.findUserById(userId);
		Assert.notNull(user);
		if (authenticationService.getEmailConfirmed(user)) {
			return false;
		}

		updateUserStatus(userId, new UserStatusType(UserStatusType.DELETED));
		return true;
	}

	public void assignRole(final Long userId, final String roleTypeCode) { // appears unused
		Assert.notNull(userId);
		Assert.notNull(roleTypeCode);
		final User user = userDAO.get(userId);
		userRoleService.addRoles(user, new String[]{roleTypeCode});
	}

	public void removeRole(final Long userId, final String roleTypeCode) { // appears unused
		Assert.notNull(userId);
		Assert.notNull(roleTypeCode);
		final User user = userDAO.get(userId);
		userRoleService.removeRoles(user, new String[]{roleTypeCode});
	}

	@Override
	public Set<User> findBlockedUsers(Long userId) {
		Assert.notNull(userId);
		User user = userDAO.get(userId);

		Set<User> users = new HashSet<>();
		List<BlockedUserUserAssociation> associations = blockedAssociationDAO.findAllBlockedUsersByUser(userId, user.getCompany().getId());
		for (BlockedUserUserAssociation a : associations) {
			users.add(a.getBlockedUser().getUser());
		}

		return users;
	}

	@Override
	public List<Long> findBlockedOrBlockedByCompanyIdsByUserId(Long userId) {
		Assert.notNull(userId);
		return blockedAssociationDAO.findBlockedOrBlockedByCompanyIdsByUserId(userId);
	}

	@Override
	@Cacheable(
		value = BLOCKED_USER_IDS,
		key = "#root.target.BLOCKED_USER_IDS + #userId"
	)
	public List<Long> findAllBlockedUserIdsByBlockingUserId(Long userId) {
		Assert.notNull(userId);
		return blockedAssociationDAO.findAllBlockedUserIdsByBlockingUserId(userId);
	}

	@Override
	public List<String> findAllBlockedUserNumbersByBlockingUserId(Long userId) {
		Assert.notNull(userId);
		return blockedAssociationDAO.findAllBlockedUserNumbersByBlockingUserId(userId);
	}

	@Override
	@CacheEvict(
		value = BLOCKED_USER_IDS,
		key = "#root.target.BLOCKED_USER_IDS + #userId"
	)
	public void blockUser(Long userId, Long blockedUserId) {
		Assert.notNull(userId);
		Assert.notNull(blockedUserId);
		Assert.isTrue(!userId.equals(blockedUserId), "User " + userId + " can't block himself.");

		User blockingUser = getUser(userId);
		Assert.notNull(blockingUser);

		blockUserFromCompany(userId, blockedUserId, blockingUser.getCompany().getId());
	}

	@Override
	@CacheEvict(
		value = BLOCKED_USER_IDS,
		key = "#root.target.BLOCKED_USER_IDS + #userId"
	)
	public void blockUserFromCompany(Long userId, Long blockedUserId, Long companyId) {
		Assert.notNull(userId);
		Assert.notNull(blockedUserId);
		Assert.notNull(companyId);
		Assert.isTrue(!userId.equals(blockedUserId), "User " + userId + " can't block himself.");

		User blockedUser = userDAO.get(blockedUserId);
		User blockingUser = userDAO.get(userId);
		Company company = companyDAO.get(companyId);
		Assert.notNull(blockedUser);
		Assert.notNull(company);

		// Remove all the association for individual users in the company
		int deletedRelationships = blockedAssociationDAO.deleteAllBlockedUserUserAssociationByBlockedUserAndCompanyId(blockedUserId, companyId);
		logger.info("deleted blocked user associations " + deletedRelationships);

		BlockedUserCompanyAssociation block = blockedAssociationDAO.findByCompanyIdAndBlockedUser(companyId, blockedUserId);
		if (block == null) {
			block = new BlockedUserCompanyAssociation(blockingUser, blockedUser);
			block.setBlockingCompany(company);
		}

		block.setDeleted(false);
		block.setUser(blockingUser);
		blockedAssociationDAO.saveOrUpdate(block);

		summaryService.saveBlockedUserHistorySummary(block);

		// Remove blocked user from all of company's groups and remove all invitations
		userGroupService.removeAllAssociationsAndInvitationsByUserAndCompanyId(blockedUserId, companyId);

		userChangeLogDAO.saveOrUpdate(
			new UserBlockedByCompanyChangeLog(blockedUserId, authenticationService.getCurrentUserId(),
				authenticationService.getMasqueradeUserId(), company)
		);

		eventRouter.sendEvent(new UserSearchIndexEvent(userId));
		eventRouter.sendEvent(new UserSearchIndexEvent(blockedUserId));

		//WORK-1813 : Remove the user from all the company's groups
		userGroupService.removeAllAssociationsByUserAndCompanyId(blockedUserId, companyId);
	}

	@Override
	@CacheEvict(
		value = BLOCKED_USER_IDS,
		key = "#root.target.BLOCKED_USER_IDS + #userId"
	)
	public void unblockUser(Long userId, Long blockedUserId) {
		Assert.notNull(userId);
		Assert.notNull(blockedUserId);

		User user = findUserById(userId);
		if (userRoleService.hasAnyAclRole(user, AclRole.ACL_ADMIN, AclRole.ACL_MANAGER)) {
			unblockUserFromCompany(user.getCompany().getId(), blockedUserId);
		}

		BlockedUserUserAssociation block = blockedAssociationDAO.findActiveByUserAndBlockedUser(userId, blockedUserId);
		if (block != null) {
			block.setDeleted(true);
			summaryService.saveBlockedUserHistorySummary(block);
			userIndexer.reindexById(blockedUserId);
		}
		userIndexer.reindexById(userId);
	}

	private void unblockUserFromCompany(Long companyId, Long blockedUserId) {
		Assert.notNull(companyId);
		Assert.notNull(blockedUserId);

		BlockedUserUserAssociation block = blockedAssociationDAO.findByCompanyIdAndBlockedUser(companyId, blockedUserId);
		if (block != null) {
			block.setDeleted(true);
			summaryService.saveBlockedUserHistorySummary(block);
			userIndexer.reindexById(blockedUserId);
		}
	}

	@Override
	public void blockCompany(Long userId, Long blockedCompanyId) {
		blockCompanyFromCompany(userId, blockedCompanyId);
	}

	@Override
	public void blockCompanyFromCompany(Long userId, Long blockedCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(blockedCompanyId);
		User user = userDAO.get(userId);

		blockCompanyFromCompany(userId, user.getCompany().getId(), blockedCompanyId);
	}

	private void blockCompanyFromCompany(Long userId, Long blockingCompanyId, Long blockedCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(blockingCompanyId);
		Assert.notNull(blockedCompanyId);

		Assert.isTrue(!blockingCompanyId.equals(blockedCompanyId), "blockclient.company.owned");

		User user = userDAO.get(userId);
		Company blockedCompany = profileService.findCompanyById(blockedCompanyId);
		Company blockingCompany = profileService.findCompanyById(blockingCompanyId);

		List<String> paidOrCancelledStatusTypes = Lists.newArrayList(WorkStatusType.PAID_STATUS_TYPES);
		paidOrCancelledStatusTypes.add(WorkStatusType.CANCELLED);
		Assert.isTrue(!workService.doesWorkerHaveWorkWithCompany(blockedCompanyId, userId, paidOrCancelledStatusTypes), "blockclient.assignments.open");

		List<BlockedCompanyUserAssociation> blockedList = blockedAssociationDAO.findAllBlockedCompanyUserAssociationByBlockedCompanyAndBlockingCompany(blockingCompanyId, blockedCompanyId);
		for (BlockedCompanyUserAssociation a : blockedList) {
			a.setDeleted(true);
		}

		BlockedCompanyCompanyAssociation block = blockedAssociationDAO.findByCompanyIdAndBlockedCompanyId(blockingCompanyId, blockedCompanyId);
		if (block == null) {
			block = new BlockedCompanyCompanyAssociation(user, blockedCompany);
		}
		block.setDeleted(false);
		block.setUser(user);
		block.setBlockingCompany(blockingCompany);
		blockedAssociationDAO.saveOrUpdate(block);

		userIndexer.reindexById(userId);
		eventRouter.sendEvent(eventFactory.buildUserBlockCompanyEvent(blockedCompanyId, userId));

	}

	@Override
	public void unblockCompany(Long userId, Long blockedCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(blockedCompanyId);

		User user = findUserById(userId);
		if (userRoleService.hasAnyAclRole(user, AclRole.ACL_ADMIN, AclRole.ACL_MANAGER)) {
			unblockCompanyFromCompany(userId, user.getCompany().getId(), blockedCompanyId);
		}

		BlockedCompanyUserAssociation block = blockedAssociationDAO.findByUserIdAndBlockedCompanyId(userId, blockedCompanyId);
		if (block != null) {
			block.setDeleted(true);
			userIndexer.reindexById(userId);
		}
	}

	private void unblockCompanyFromCompany(Long userId, Long blockingCompanyId, Long blockedCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(blockedCompanyId);
		Assert.notNull(blockingCompanyId);

		BlockedCompanyCompanyAssociation block = blockedAssociationDAO.findByCompanyIdAndBlockedCompanyId(blockingCompanyId, blockedCompanyId);
		if (block != null) {
			block.setDeleted(true);
		}

	}

	@Override
	public boolean isUserBlockedByCompany(Long userId, Long userCompanyId, Long blockingCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(userCompanyId);
		Assert.notNull(blockingCompanyId);

		return blockedAssociationDAO.isUserBlockedByCompany(userId, userCompanyId, blockingCompanyId);
	}

	@Override
	public List<Long> ListBlockedCompanies(Long blockingCompanyId) {
		Assert.notNull(blockingCompanyId);
		return blockedAssociationDAO.listBlockedCompanies(blockingCompanyId);
	}

	@Override
	public List<Long> ListBlockedUsers(Long blockingCompanyId) {
		Assert.notNull(blockingCompanyId);
		return blockedAssociationDAO.listBlockedUsers(blockingCompanyId);
	}

	// User Availability: Working hours & notification hours

	@Override
	public void updateUserWorkingHours(Long userId, List<UserAvailabilityDTO> workingHours) {
		Assert.notNull(workingHours);
		Assert.notNull(userId);

		for (UserAvailabilityDTO workHoursDTO : workingHours) {
			updateUserWorkingHours(userId, workHoursDTO);
		}
	}

	private void updateUserWorkingHours(Long userId, UserAvailabilityDTO workingHoursDTO) {
		Assert.notNull(workingHoursDTO);
		Assert.notNull(userId);

		User user = getUser(userId);

		Integer weekDay = workingHoursDTO.getWeekDay();
		Assert.notNull(weekDay);
		Assert.isTrue(weekDay >= 0 && weekDay <= 6);

		WorkAvailability workHours = (WorkAvailability) userAvailabilityDAO.findWorkingHoursByUserId(user.getId(), weekDay);

		if (workHours == null) {
			workHours = new WorkAvailability(user, weekDay);
		}

		workHours.setAllDayAvailable(workingHoursDTO.isAllDayAvailable());
		workHours.setFromTime(workingHoursDTO.getFromTime());
		workHours.setToTime(workingHoursDTO.getToTime());
		workHours.setDeleted(workingHoursDTO.getDeleted());

		userAvailabilityDAO.saveOrUpdate(workHours);
		userIndexer.reindexById(userId);

		Map<String, Object> params = new HashMap<>();
		params.put(ProfileModificationType.WORKING_HOURS, null);

		userGroupValidationService.revalidateAllAssociationsByUserAsync(userId, params);
	}

	@Override
	public UserAvailability findActiveWorkingHoursByUserId(Long userId, Integer weekDay) {
		return userAvailabilityDAO.findActiveWorkingHoursByUserId(userId, weekDay);
	}

	public void updateUserNotificationHours(Long userId, List<UserAvailabilityDTO> notificationHourDTOs) {
		for (UserAvailabilityDTO dto : notificationHourDTOs) {
			updateUserNotificationHours(userId, dto);
		}
	}

	public void updateUserNotificationHours(Long userId, UserAvailabilityDTO notificationHoursDTO) {
		Assert.notNull(userId);
		Assert.notNull(notificationHoursDTO);

		User user = getUser(userId);

		Integer weekDay = notificationHoursDTO.getWeekDay();
		Assert.notNull(weekDay);
		Assert.isTrue(weekDay >= 0 && weekDay <= 6);

		NotificationAvailability hours = (NotificationAvailability) userAvailabilityDAO.findNotificationHoursByUserId(user.getId(), weekDay);

		if (hours == null) {
			hours = new NotificationAvailability(user, weekDay);
		}

		hours.setAllDayAvailable(notificationHoursDTO.isAllDayAvailable());
		hours.setFromTime(notificationHoursDTO.getFromTime());
		hours.setToTime(notificationHoursDTO.getToTime());
		hours.setDeleted(notificationHoursDTO.getDeleted());

		userAvailabilityDAO.saveOrUpdate(hours);
	}

	@Override
	public UserAvailability findActiveNotificationHoursByUserId(Long userId, Integer weekDay) {
		return userAvailabilityDAO.findActiveNotificationHoursByUserId(userId, weekDay);
	}

	@Override
	public List<UserAvailability> findActiveWeeklyNotificationHours(Long userId) {
		return userAvailabilityDAO.findActiveWeeklyNotificationHours(userId);
	}

	@Override
	public List<UserAvailability> findWeeklyWorkingHours(Long userId) {
		return mergeConfiguredWithDefaultAvailability(
			userId,
			userAvailabilityDAO.findWeeklyWorkingHours(userId),
			WorkAvailability.class);
	}

	@Override
	public List<UserAvailability> findWeeklyNotificationHours(Long userId) {
		return mergeConfiguredWithDefaultAvailability(
			userId,
			userAvailabilityDAO.findWeeklyNotificationHours(userId),
			NotificationAvailability.class);
	}

	private List<UserAvailability> mergeConfiguredWithDefaultAvailability(Long userId, List<UserAvailability> configuredHours, Class availabilityClass) {
		Profile profile = profileService.findProfile(userId);
		Assert.notNull(profile, "Unable to find profile");

		if (configuredHours.size() == 7) {
			return configuredHours;
		}

		// Return a list composed of configured availability for the entire week.
		// Set default values for any days not configured.

		UserAvailability[] hours = new UserAvailability[7];
		for (UserAvailability h : configuredHours) {
			hours[h.getWeekDay()] = h;
		}

		for (int i = 0; i < 7; i++) {
			if (hours[i] != null) {
				continue;
			}

			try {
				hours[i] = makeDefaultAvailability((UserAvailability) availabilityClass.newInstance(), i, profile.getTimeZone());
			} catch (InstantiationException | IllegalAccessException e) {
				return Lists.newArrayList();
			}
		}

		List<UserAvailability> hoursList = Lists.newArrayListWithCapacity(7);
		Collections.addAll(hoursList, hours);
		return hoursList;
	}

	private UserAvailability makeDefaultAvailability(UserAvailability availability, Integer weekday, TimeZone timeZone) {
		availability.setWeekDay(weekday);
		availability.setAllDayAvailable(false);
		availability.setFromTime(NotificationAvailability.getDefaultFromTime(timeZone.getTimeZoneId()));
		availability.setToTime(NotificationAvailability.getDefaultToTime(timeZone.getTimeZoneId()));
		availability.setDeleted((weekday == Calendar.SATURDAY - 1 || weekday == Calendar.SUNDAY - 1));
		return availability;
	}

	@Override
	public boolean isAvailableForNotification(Long userId) {
		TimeZone userTimeZone = profileService.findUserProfileTimeZone(userId);
		Assert.notNull(userTimeZone);

		//In the user timeZone
		Calendar now = DateUtilities.getCalendarNow(userTimeZone.getTimeZoneId());
		//Calendar Days are 1 index
		UserAvailability hours = findActiveNotificationHoursByUserId(userId, now.get(Calendar.DAY_OF_WEEK) - 1);

		if (hours == null) {
			int today = now.get(Calendar.DAY_OF_WEEK);
			if (today == Calendar.SATURDAY || today == Calendar.SUNDAY) {
				return false;
			}
		}

		int fromHour = (hours != null) && hours.getFromTime() != null ? hours.getFromTime().get(Calendar.HOUR_OF_DAY) : NotificationAvailability.DEFAULT_FROM_HOUR;
		int fromMinute = (hours != null) && hours.getFromTime() != null ? hours.getFromTime().get(Calendar.MINUTE) : 0;

		int toHour = (hours != null) && hours.getToTime() != null ? hours.getToTime().get(Calendar.HOUR_OF_DAY) : NotificationAvailability.DEFAULT_TO_HOUR;
		int toMinute = (hours != null) && hours.getToTime() != null ? hours.getToTime().get(Calendar.MINUTE) : 0;

		Calendar from = DateUtilities.getCalendarWithTime(fromHour, fromMinute, userTimeZone.getTimeZoneId());
		Calendar to = DateUtilities.getCalendarWithTime(toHour, toMinute, userTimeZone.getTimeZoneId());

		//Use the System timeZone to compare since the from/to have been converted
		now = DateUtilities.getCalendarNow();
		return (now.after(from) && now.before(to));
	}

	@Override
	public void saveOrUpdateUser(User user) {
		userDAO.saveOrUpdate(user);
	}

	public long findBlockingUserId(Long blockedUserId, Long companyId) {

		Assert.notNull(blockedUserId, "Invalid user id");
		User user = getUser(blockedUserId);
		Assert.notNull(user, "Unable to find a user");

		return blockedAssociationDAO.findByCompanyIdAndBlockedUser(companyId, blockedUserId).getUser().getId();
	}

	@Override
	public boolean isCompanyBlockingUser(Long blockedUserId, Long companyId) {

		Assert.notNull(blockedUserId, "Invalid user id");
		User user = getUser(blockedUserId);
		Assert.notNull(user, "Unable to find a user");

		BlockedUserCompanyAssociation association =
			blockedAssociationDAO.findByCompanyIdAndBlockedUser(companyId, blockedUserId);
		return association != null && !association.getDeleted();
	}

	public Calendar findDateWhenUserBlocked(Long userId, Long companyId) {

		Assert.notNull(userId, "Invalid user id");
		User user = getUser(userId);
		Assert.notNull(user, "Unable to find a user");

		return blockedAssociationDAO.findByCompanyIdAndBlockedUser(companyId, userId).getModifiedOn();
	}

	@SuppressWarnings("deprecation")
	@Override
	public User updateUserProperties(Long userId, Map<String, String> properties) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		Assert.notNull(userId, "User id must be provided");
		Assert.notNull(properties, "Properties must be provided");

		User user = getUser(userId);

		Assert.notNull(user, "Unable to find user");

		String firstName = properties.containsKey("firstName") ? properties.get("firstName") : null;
		String lastName = properties.containsKey("lastName") ? properties.get("lastName") : null;

		if (StringUtils.isNotBlank(firstName) || StringUtils.isNotBlank(lastName)) {
			if (!StringUtilities.same(firstName, user.getFirstName()) || !StringUtilities.same(lastName, user.getLastName())) {
				profileService.registerUserProfileModification(userId, new ProfileModificationType(ProfileModificationType.USER_NAME));
			}

			if (StringUtils.isNotBlank(firstName) && !StringUtilities.same(firstName, user.getFirstName())) {
				properties.put("firstNameOldValue", user.getFirstName());
			}

			if (StringUtils.isNotBlank(lastName) && !StringUtilities.same(lastName, user.getLastName())) {
				properties.put("lastNameOldValue", user.getLastName());
			}
		}

		String email = properties.containsKey("email") ? properties.get("email") : null;
		String emailOnUserObject = user.getEmail();
		boolean retainOldEmail = false;

		// email update logic: the existing email address will stay in effect until the
		// user confirms the email change

		if (StringUtils.isNotBlank(email) && !StringUtilities.same(email, user.getEmail())) {
			Assert.isTrue(!emailExists(email, userId), "Email is already being used.");
			retainOldEmail = true;
			notificationService.sendNotification(emailTemplateFactory.buildRegistrationConfirmUserEmailTemplate(user.getId(), email));
		}
		BeanUtilities.updateProperties(user, properties);

		logger.debug("Retain old email:" + retainOldEmail);
		logger.debug("Updating user properties email is:" + email);
		boolean emailSame = !StringUtilities.same(email, user.getEmail());
		logger.debug("Updating user properties, is email same as db record " + emailSame);

		// the BeanUtilities could have replaced the email field with
		// a new value; move it to the changedEmailAddressField
		if (retainOldEmail) {
			user.setEmail(emailOnUserObject);
			user.setChangedEmail(email);
		}

		userIndexer.reindexById(userId);
		logger.debug("User fields" + user.getEmail() + " " + user.getChangedEmail());

		authenticationService.refreshSessionForUser(user.getId());

		return user;
	}

	@Override
	public List<User> findAllActiveEmployees(Long companyId) {
		return userDAO.findAllActiveEmployees(companyId);
	}

	@Override
	public List<UserDTO> findUserDTOsOfAllActiveEmployees(final Long companyId, final boolean includeApiUsers) {
		return userDAO.findUserDTOsOfAllActiveEmployees(companyId, includeApiUsers);
	}

	@Override
	public Map<Long, User> getSoleProprietorsByCompanyId(Collection<Long> companyIds) {
		List<User> soleProprietors = userDAO.findSoleProprietors(companyIds);
		return index(soleProprietors, on(User.class).getCompany().getId());
	}

	@Override
	public UserPagination findAllActiveEmployees(Long companyId, UserPagination pagination) {
		return userDAO.findAllActiveEmployees(companyId, pagination);
	}

	@Override
	public UserPagination findAllActiveAdminUsers(Long companyId, UserPagination pagination) {
		return userDAO.findAllActiveAdminUsers(companyId, pagination);
	}

	@Override
	public RecentUserPagination findAllRecentUsers(RecentUserPagination pagination) {
		return userDAO.findAllRecentUsers(pagination);
	}

	@Override
	public CompanyUserPagination findAllCompanyUsers(Long companyId, CompanyUserPagination pagination) {
		User user = authenticationService.getCurrentUser();

		Calendar toDate = DateUtilities.getMidnightTodayRelativeToTimezone(user.getProfile() != null ? user.getProfile().getTimeZone().getTimeZoneId() : Constants.WM_TIME_ZONE);
		Calendar fromDate = (Calendar) toDate.clone();
		fromDate.add(Calendar.DAY_OF_YEAR, -1);
		return userDAO.findAllCompanyUsers(companyId, pagination, fromDate, toDate);
	}

	@Override
	public CompanyUserPagination findAllCompanyUsersByCompanyNumber(String companyNumber, CompanyUserPagination pagination) {
		Company company = companyService.findCompanyByNumber(companyNumber);
		Assert.notNull(company);
		return findAllCompanyUsers(company.getId(), pagination);
	}

	@Override
	public ImmutableList<Map> getProjectedAllActiveCompanyUsers(String companyId, String[] fields) throws Exception {

		if (!Objects.equals(authenticationService.getCurrentUser().getCompany().getCompanyNumber(), companyId)) {
			throw new ForbiddenException("Forbidden");
		}

		CompanyUserPagination companyUserPagination = new CompanyUserPagination();
		companyUserPagination.setSortColumn(CompanyUserPagination.SORTS.LAST_NAME);
		companyUserPagination.setSorts(ImmutableList.of(
			new Sort().setSortColumn(CompanyUserPagination.SORTS.LAST_NAME.toString()),
			new Sort().setSortColumn(CompanyUserPagination.SORTS.EMAIL.toString()),
			new Sort().setSortColumn(CompanyUserPagination.SORTS.ROLES_STRING.toString()),
			new Sort().setSortColumn(CompanyUserPagination.SORTS.LATEST_ACTIVITY.toString())
		));
		companyUserPagination.setSortDirection(Pagination.SORT_DIRECTION.ASC);
		companyUserPagination.addFilter(CompanyUserPagination.FILTER_KEYS.IS_INACTIVE, "0");
		companyUserPagination = findAllCompanyUsersByCompanyNumber(companyId, companyUserPagination);
		companyUserPagination.setProjection(fields);

		return ImmutableList.copyOf(
			ProjectionUtilities.projectAsArray(fields, findAllCompanyUsersByCompanyNumber(companyId, companyUserPagination).getResults())
		);
	}

	@Override
	public User updateUserStatus(Long userId, UserStatusType userStatusType) {
		User user = getUser(userId);
		UserStatusType oldStatus = authenticationService.getUserStatus(user);

		//validations
		if (authenticationService.isSuspended(user)) {
			Assert.state(userStatusType.isDeactivated() || userStatusType.isHold() ||
				userStatusType.isDeleted(), "User is suspended and his status can't be changed to " + userStatusType.getCode());
		}

		// Per product, below condition is removed because we want to lock unconfirmed user if max of failed login attempts is reached as well;
		/*if (!authenticationService.isActive(user)) {
			Assert.state(!userStatusType.isLocked(), "Cannot lock inactive users ");
		}*/

		authenticationService.setUserStatus(user, userStatusType);

		final Long currentUserId = authenticationService.getCurrentUserId();
		// Ah, we're not actually logged in yet, so we're anonymous for this.
		if (currentUserId == null) {
			authenticationService.setCurrentUser(WORKMARKET_SYSTEM_USER_ID); // AKA anonymous
		}
		userChangeLogDAO.saveOrUpdate(
			new UserStatusChangeLog(
				userId, authenticationService.getCurrentUserId(), authenticationService.getMasqueradeUserId(), oldStatus, userStatusType
			)
		);
		summaryService.saveUserHistorySummary(user);

		UserSearchIndexEvent event = new UserSearchIndexEvent(userId);
		if (userStatusType.isInactiveStatus()) {
			event.setDelete(true);
		}
		eventRouter.sendEvent(event);

		userIndexer.reindexById(userId);
		authenticationService.refreshSessionForUser(userId);
		return user;
	}

	@Override
	public UserAssetAssociation findUserAvatars(Long userId) {
		Assert.notNull(userId);
		return userAssetAssociationDAO.findUserAvatars(userId);
	}

	@Override
	public List<UserAssetAssociation> findUserAvatars(final Collection<Long> userIds) {
		Assert.notNull(userIds);
		return userAssetAssociationDAO.findUserAvatars(userIds);
	}

	@Override
	public UserAssetAssociation findPreviousUserAvatars(Long userId) {
		Assert.notNull(userId);
		return userAssetAssociationDAO.findPreviousUserAvatars(userId);
	}

	@Override
	public Asset findUserBackgroundImage(Long userId) {
		Assert.notNull(userId);

		UserAssetAssociation assoc = userAssetAssociationDAO.findBackgroundImage(userId);
		if (assoc != null) {
			return assoc.getAsset();
		}
		return null;
	}

	@Override
	public boolean isUserBlockedForCompany(Long userId, Long userCompanyId, Long blockCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(userCompanyId);
		Assert.notNull(blockCompanyId);

		return blockedAssociationDAO.isUserBlockedForCompany(userId, userCompanyId, blockCompanyId);
	}

	@Override
	public BlockedCompanyUserAssociationPagination findAllBlockedCompanies(Long userId, BlockedCompanyUserAssociationPagination pagination) {
		Assert.notNull(userId);
		User user = userDAO.get(userId);

		return blockedAssociationDAO.findAllBlockedCompaniesByUser(userId, user.getCompany().getId(), pagination);
	}

	@Override
	public boolean isCompanyBlockedByUser(Long userId, Long blockCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(blockCompanyId);

		User user = userDAO.getUser(userId);
		return blockedAssociationDAO.isCompanyBlockedByUser(userId, user.getCompany().getId(), blockCompanyId);
	}

	@Override
	public boolean isCompanyBlockedByUser(Long userId, Long companyId, Long blockedCompanyId) {
		Assert.notNull(userId);
		Assert.notNull(companyId);

		return blockedAssociationDAO.isCompanyBlockedByUser(userId, companyId, blockedCompanyId);
	}

	@Override
	public Optional<PersonaPreference> getPersonaPreference(Long userId) {
		PersonaPreference preference = personaDAO.get(userId);
		return Optional.fromNullable(preference);
	}


	@Override
	public PersonaPreference saveOrUpdatePersonaPreference(PersonaPreference preference) {
		personaDAO.saveOrUpdate(preference);
		return preference;
	}

	@Override
	public void lockUser(Long userId) {
		Assert.notNull(userId);
		updateUserStatus(userId, UserStatusType.LOCKED_STATUS);
	}

	@Override
	public void unlockUser(Long userId) {
		Assert.notNull(userId);
		User user = userDAO.getUser(userId);
		if (authenticationService.isLocked(user)) {
			if (!authenticationService.getEmailConfirmed(user)) {
				updateUserStatus(userId, UserStatusType.PENDING_STATUS);
			} else {
				updateUserStatus(userId, UserStatusType.APPROVED_STATUS);
			}
		}
	}

	@Override
	public void registerDevice(Long userId, final String regid, String type) {
		User user = userDAO.findUserById(userId);
		UserDeviceAssociation association = new UserDeviceAssociation();
		association.setDeviceUid(regid);
		association.setUser(user);

		if (type.equals(DeviceType.ANDROID.getCode()) ||
			type.equals(DeviceType.IOS.getCode())) {

			UserDeviceAssociation existingAssociation = userDeviceAssociationDAO.findByDeviceUIDAndUserId(regid, userId);
			if (existingAssociation == null) {
				association.setDeviceType(type);
				userDeviceAssociationDAO.saveOrUpdate(association);
			}
		}
	}

	@Override
	public boolean removeDevice(Long userId, final String regid) {
		Assert.notNull(userId);
		Assert.hasText(regid);
		UserDeviceAssociation existingAssociation = userDeviceAssociationDAO.findByDeviceUIDAndUserId(regid, userId);

		if (existingAssociation != null) {
			existingAssociation.setDeleted(true);
			userDeviceAssociationDAO.saveOrUpdate(existingAssociation);
			return true;
		}
		return false;
	}

	@Override
	public boolean hasDevice(Long userId) {
		return isNotEmpty(userDeviceAssociationDAO.findAllByUserId(userId));
	}

	@Override
	public int getMaxUserId() {
		return userDAO.getMaxUserId();
	}

	@Override
	public List<UserDeviceAssociation> findAllUserDevicesByUserId(long userId) {
		return userDeviceAssociationDAO.findAllByUserId(userId);
	}

	@Override
	public Map<String, Object> getProjectionMapById(Long id, String... fields) {
		return userDAO.getProjectionMapById(id, fields);
	}

	@Override
	public Map<Long, Map<String, Object>> getProjectionMapByIds(List<Long> ids, String... fields) {
		return userDAO.getProjectionMapByIds(ids, fields);
	}

	@Override
	public String getFullName(Long id) {
		Map<String, Object> prop = userDAO.getProjectionMapById(id, "firstName", "lastName");

		return StringUtilities.fullName((String) prop.get("firstName"), (String) prop.get("lastName"));
	}

	@Override
	public Map<Long, String> getFullNames(List<Long> ids) {
		Map<Long, Map<String, Object>> props = userDAO.getProjectionMapByIds(ids, "firstName", "lastName");
		Map<Long, String> result = Maps.newHashMap();

		for (Long key : props.keySet()) {
			result.put(key, StringUtilities.fullName((String) props.get(key).get("firstName"), (String) props.get(key).get("lastName")));
		}

		return result;
	}

	@Override
	public boolean existsBy(Object... objects) {
		return userDAO.existsBy(objects);
	}

	@Override
	public Integer findPromoDismissed() {
		User user = authenticationService.getCurrentUser();
		return user.getPromoDismissed();
	}

	@Override
	public void updatePromoDismissed(Integer dismissed) {
		User user = authenticationService.getCurrentUser();
		user.setPromoDismissed(dismissed);
		userDAO.saveOrUpdate(user);
	}

	/**
	 * Check if currentUser's company has 'resource' customerType
	 */
	@Override
	public boolean belongsToWorkerCompany() {
		User user = authenticationService.getCurrentUser();
		String customerType = null;
		if (user.getCompany() != null) {
			customerType = user.getCompany().getCustomerType();
		}
		return CustomerType.RESOURCE.value().equals(customerType);
	}

	@Override
	public boolean isLastAdmin(String userNumber) {
		User user = findUserByUserNumber(userNumber);
		if (!userRoleService.hasAclRole(user, AclRole.ACL_ADMIN)) {
			return false;
		}
		List<Long> adminUserIds = companyDAO.getUserIdsWithActiveRole(user.getCompany().getId(), AclRole.ACL_ADMIN);
		return adminUserIds.contains(user.getId()) && adminUserIds.size() == 1;
	}

	@Override
	public boolean isLastDispatcher(String userNumber) {
		User user = findUserByUserNumber(userNumber);
		if (!userRoleService.hasAclRole(user, AclRole.ACL_DISPATCHER)) {
			return false;
		}
		List<Long> dispatcherUserIds = companyDAO.getUserIdsWithActiveRole(user.getCompany().getId(), AclRole.ACL_DISPATCHER);
		return dispatcherUserIds.contains(user.getId()) && dispatcherUserIds.size() == 1;
	}

	@Override
	public boolean isEmployeeWorker(User user) {
		EmployeeWorkerRoleAdmitted workerRoleAdmitted = new EmployeeWorkerRoleAdmitted();
		employeeWorkerRoleRope.welcome(
			new UserGuest(user),
			new EmployeeWorkerRoleRope(workerRoleAdmitted)
		);

		return workerRoleAdmitted.getAdmitted()
			&& authenticationService.isLane1Active(user)
			&& userRoleService.hasAclRole(user, AclRole.ACL_EMPLOYEE_WORKER)
			&& userRoleService.hasAclRole(user, AclRole.ACL_WORKER);
	}

	@Override
	public List<UserIdentityDTO> findUserIdentitiesByUuids(Collection<String> uuids) {
		if (isEmpty(uuids)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		return userDAO.findUserIdentitiesByUuids(uuids);
	}

	@Override
	public Map<Long, String> findUserUuidsByIds(final Collection<Long> ids) {
		final HashMap<Long, String> userIdsToUuids = Maps.newHashMap();
		if (isEmpty(ids)) {
			return userIdsToUuids;
		}

		final List<UserIdentityDTO> userIdentities = userDAO.findUserIdentitiesByIds(ids);
		for (final UserIdentityDTO userIdentity : userIdentities) {
			userIdsToUuids.put(userIdentity.getUserId(), userIdentity.getUuid());
		}
		return userIdsToUuids;
	}
}
