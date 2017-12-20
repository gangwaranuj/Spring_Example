package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.BlockedCompanyUserAssociationPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.notification.UserDeviceAssociation;
import com.workmarket.domains.model.user.CompanyUserPagination;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.model.user.RecentUserPagination;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.search.model.SearchUser;
import com.workmarket.service.business.dto.NotificationPreferenceDTO;
import com.workmarket.service.business.dto.UserAvailabilityDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.UserIdentityDTO;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {

	List<User> findAllUsersByIds(Collection<Long> userIds);

	List<User> findAllUsersWithProfileAndCompanyByIds(Collection<Long> userIds);

	Set<User> findAllUsersByUserNumbers(List<String> userNumbers);

	Set<Long> findAllUserIdsByUserNumbers(Collection<String> userNumbers);

	Set<Long> findAllUserIdsByUuids(Collection<String> userUuids);

	Set<String> findAllUserUuidsByIds(Collection<Long> userIds);

	Set<String> findAllUserNumbersByUserIds(Collection<Long> userIds);

	Long findUserId(String userNumber);

	String findUserUuidById(final Long id);

	Long findUserIdByUuid(String userUuid);

	Long findUserIdByEmail(String email);

	String findUserNumber(Long id);

	/**
	 * @param id -
	 * @return - A minimal version of the user object, no associations.
	 */
	User getUser(Long id);

	SearchUser getSearchUser(Long id);

	User getUserWithRoles(Long id);

	/**
	 * Returns the entire user object with the full object graph.
	 *
	 * @param id
	 * @return
	 */
	User findUserById(long id);

	User findUserByUserNumber(String userNumber);

	User findUserByUuid(String uuid);

	List<UserDTO> findUserDTOsByUuids(final List<String> uuid);

	User findUserByEncryptedId(String id);

	User findUserByEmail(String email);

	User findCreatorByAssetId(Long assetId);

	User findModifierByAssetId(Long assetId);

	User findCreatorByWorkLabelAssociationId(Long workLabelId);

	User findModifierByWorkLabelAssociationId(Long workLabelId);

	User findCreatorByWorkNegotiationId(Long workNegotiationId);

	/**
	 * Returns TRUE if the email is already being used by another user.
	 * User in the case of an edit in which the users email address is being changed.
	 *
	 * @param email
	 * @param userId
	 * @return
	 */
	boolean emailExists(String email, Long userId);

	List<User> findUsersByPhoneNumber(String phoneNumber);

	UserPagination findAllPendingLane3Users(UserPagination pagination);

	UserPagination findAllSuspendedUsers(UserPagination pagination);

	void updateLane3ApprovalStatus(Long userId, ApprovalStatus approvalStatus);

	/**
	 * Change a user's on-hold status, an opt-in temporary disabling of their account.
	 * Hold the user. Coddle them. Let them know they're loved.
	 *
	 * @param userId
	 * @param holdFlag
	 */
	void holdUser(Long userId, Boolean holdFlag);

	/**
	 * Change a user's suspension status.
	 * Users are suspended as a result of a client services action.
	 *
	 * @param userId
	 * @param suspendFlag
	 */
	void suspendUser(Long userId, Boolean suspendFlag);

	/**
	 * Change a user's deactivation status.
	 * Deactivation is almost like being deleted. Almost.
	 * Used by a company to essentially remove an employee from the system,
	 * but preserve their visibility for reporting and leave room for them to
	 * potentially return to the system.
	 *
	 * @param userId
	 * @param newWorkOwnerId
	 */
	void deactivateUser(Long userId, Long newWorkOwnerId, Long newGroupOwnerId, Long newTestOwnerId);
	/**
	 * opposite to deactivate
	 * @param userId
	 */
	void reactivateUser(Long userId);
	/**
	 * Delete a user.
	 *
	 * @param userId
	 */
	void deleteUser(Long userId);

    /**
     * Delete a user if they have not confirmed their account.
     *
     * @param userId
     */
    boolean deleteUserIfNotConfirmed(Long userId);

	/**
	 * Assign a role to a user
	 *
	 * @param userId
	 * @param roleTypeCode
	 */
	void assignRole(Long userId, String roleTypeCode);

	/**
	 * Remove a role from a user
	 *
	 * @param userId
	 * @param roleTypeCode
	 */
	void removeRole(Long userId, String roleTypeCode);

	/**
	 * Finds all the blocked users at the individual level and company level.
	 *
	 * @param userId
	 * @return
	 * @
	 */
	Set<User> findBlockedUsers(Long userId);

	/**
	 * Finds all the blocked user ids at the individual level and company level.
	 *
	 * @param userId
	 * @return
	 * @
	 */
	List<Long> findAllBlockedUserIdsByBlockingUserId(Long userId);

	/**
	 * Finds all the companies a user has blocked, and all the companies that have blocked a user
	 *
	 * @param userId
	 * @
	 * @return
	 * @
	 */
	List<Long> findBlockedOrBlockedByCompanyIdsByUserId(Long userId);

	List<String> findAllBlockedUserNumbersByBlockingUserId(Long userId);

	/**
	 * Blocks a user. The user will be blocked only for the user who requested it unless scopeToCompanyFlag is set to True.
	 * To block a user from an entire company use blockUserFromCompany.
	 *
	 * @param userId
	 * @param blockedUserId
	 * @throws Exception
	 * @
	 */
	void blockUser(Long userId, Long blockedUserId);

	/**
	 * Blocks a user from an entire company.
	 *
	 * @param blockingUserId
	 * @param blockedUserId
	 * @param companyId
	 * @throws Exception
	 * @
	 */
	void blockUserFromCompany(Long blockingUserId, Long blockedUserId, Long companyId);

	/**
	 * Unblocks a user.
	 *
	 * @param userId
	 * @param blockedUserId
	 * @
	 */
	void unblockUser(Long userId, Long blockedUserId);

	/**
	 * Blocks a company. The company will be  only for the user who requested.
	 *
	 * @param userId
	 * @param blockedCompanyId
	 * @
	 */
	void blockCompany(Long userId, Long blockedCompanyId);

	/**
	 * Blocks a company to work for another company.
	 *
	 * @param userId
	 * @param blockedCompanyId
	 * @
	 */
	void blockCompanyFromCompany(Long userId, Long blockedCompanyId);

	long findBlockingUserId(Long blockedUserId, Long companyId);

	boolean isCompanyBlockingUser(
		Long blockedUserId,
		Long companyId);

	Calendar findDateWhenUserBlocked(Long userId, Long companyId);

	void unblockCompany(Long userId, Long blockedCompanyId);

	/**
	 * Check to see if a user is blocked by a company
	 *
	 * @param userId
	 * @param userCompanyId
	 * @param blockingCompanyId
	 * @return Whether or not the user is blocked by the company
	 */
	boolean isUserBlockedByCompany(Long userId, Long userCompanyId, Long blockingCompanyId);

	/**
	 * Gets a list of the companies blocked by the given blocking company
	 * @param blockingCompanyId The company doing the blocking
	 * @return List The list of blocked company ids
     */
	List<Long> ListBlockedCompanies(Long blockingCompanyId);

	/**
	 * Gets a list of the users blocked by the given blocking company
	 * @param blockingCompanyId The company doing the blocking
	 * @return List The list of blocked user ids
	 */
	List<Long> ListBlockedUsers(Long blockingCompanyId);

	/**
	 * Returns TRUE if the user (or the user's company) has blocked the company
	 *
	 * @param userId
	 * @param blockCompanyId
	 * @return boolean
	 */
	boolean isCompanyBlockedByUser(Long userId, Long blockCompanyId);

	/**
	 * Returns TRUE if the user (or the user's company) has blocked the company
	 *
	 * @param userId
	 * @param userCompanyId
	 * @param blockedCompanyId
	 * @return boolean
	 */
	boolean isCompanyBlockedByUser(Long userId, Long userCompanyId, Long blockedCompanyId);

	/**
	 * Returns TRUE for the following cases:
	 *
	 * 1) The user is blocked for the company with blockCompanyId id.
	 * 2) The user's company is blocked for the company with blockCompanyId id.
	 * 3) The user blocked the the company with blockCompanyId id.
	 *
	 * @param userId
	 * @param blockCompanyId
	 * @return boolean
	 */
	boolean isUserBlockedForCompany(Long userId, Long userCompanyId, Long blockCompanyId);

	/** Locks the user if {@code user.isActive}
	 * @param userId */
	void lockUser(Long userId);

	/** Unlocks the user {@code user.isLocked}. Does nothing to already unlocked users.
	 * @param userId */
	void unlockUser(Long userId);

	void updateUserWorkingHours(Long userId, List<UserAvailabilityDTO> workingHours);

	UserAvailability findActiveWorkingHoursByUserId(Long userId, Integer weekDay);

	List<UserAvailability> findWeeklyWorkingHours(Long userId);

	void updateUserNotificationHours(Long userId, List<UserAvailabilityDTO> notificationHourDTOs);
	void updateUserNotificationHours(Long userId, UserAvailabilityDTO notificationHourDTO);
	UserAvailability findActiveNotificationHoursByUserId(Long userId, Integer weekDay);
	List<UserAvailability> findActiveWeeklyNotificationHours(Long userId);
	List<UserAvailability> findWeeklyNotificationHours(Long userId);
	boolean isAvailableForNotification(Long userId);

	void saveOrUpdateUser(User user);

	BlockedCompanyUserAssociationPagination findAllBlockedCompanies(Long userId, BlockedCompanyUserAssociationPagination pagination);

	User updateUserProperties(Long userId, Map<String, String> properties) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

	List<User> findAllActiveEmployees(Long companyId);
	List<UserDTO> findUserDTOsOfAllActiveEmployees(final Long companyId, final boolean includeApiUsers);
	Map<Long, User> getSoleProprietorsByCompanyId(Collection<Long> companyIds);
	UserPagination findAllActiveEmployees(Long companyId, UserPagination pagination);

    UserPagination findAllActiveAdminUsers(Long companyId, UserPagination pagination);

	RecentUserPagination findAllRecentUsers(RecentUserPagination pagination);

	/**
	 * Custom lookup for Employees Dashboard
	 *
	 * @param companyId
	 * @param pagination
	 * @return pagination
	 */
	CompanyUserPagination findAllCompanyUsers(Long companyId, CompanyUserPagination pagination);

	CompanyUserPagination findAllCompanyUsersByCompanyNumber(String companyNumber, CompanyUserPagination pagination);


	ImmutableList<Map> getProjectedAllActiveCompanyUsers(String companyId, String[] fields) throws Exception;

	/**
	 * Centralized point to update the user status and apply validations.
	 * e.g. if user is SUSPENDED, can't be changed to APPROVED until is unsuspended before.
	 *
	 * @param userId
	 * @param userStatusType
	 * @return
	 */
	User updateUserStatus(Long userId, UserStatusType userStatusType);

	/**
	 * Returns the user avatars
	 *
	 * @param userId
	 * @return
	 */
	UserAssetAssociation findUserAvatars(Long userId);
	List<UserAssetAssociation> findUserAvatars(final Collection<Long> userIds);
	UserAssetAssociation findPreviousUserAvatars(Long userId);
	Asset findUserBackgroundImage(Long userId);

	Optional<PersonaPreference> getPersonaPreference(Long userId);
	PersonaPreference saveOrUpdatePersonaPreference(PersonaPreference preference);

	void registerDevice(Long userId, String regid, String type);
	boolean removeDevice(Long userId, String regid);
	boolean hasDevice(Long userId);

	int getMaxUserId();

	List<UserDeviceAssociation> findAllUserDevicesByUserId(long userId);

	Map<String, Object> getProjectionMapById(Long id, String... fields);

	Map<Long, Map<String, Object>> getProjectionMapByIds(List<Long> ids, String... fields);

	String getFullName(Long id);

	Map<Long, String> getFullNames(List<Long> ids);

	boolean existsBy(Object... objects);

	Integer findPromoDismissed();

	void updatePromoDismissed(Integer dismissed);

	boolean belongsToWorkerCompany();

	boolean isLastAdmin(String userNumber);

	boolean isLastDispatcher(String userNumber);

	boolean isEmployeeWorker(User user);

	List<UserIdentityDTO> findUserIdentitiesByUuids(Collection<String> uuids);

	Map<Long, String> findUserUuidsByIds(final Collection<Long> ids);
}

