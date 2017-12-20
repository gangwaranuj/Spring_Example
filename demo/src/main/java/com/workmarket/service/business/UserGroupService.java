package com.workmarket.service.business;

import com.workmarket.api.v2.employer.settings.models.SkillDTO;
import com.workmarket.domains.groups.model.GroupMembershipPagination;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserGroupLastRoutedDTO;
import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.domains.groups.model.UserGroupThroughputDTO;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.groups.model.UserUserGroupAssociationPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.domains.model.summary.group.UserGroupSummary;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.service.business.wrapper.DownloadProfilePhotosResponse;
import com.workmarket.service.infra.security.RequestContext;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserGroupService {

	/**
	 * this creates and downloads profile Images for groups.
	 */
	DownloadProfilePhotosResponse downloadGroupProfileImages(long groupId, long userId);

	// Group CRUD

	/**
	 * Save/update a company user group as defined by the DTO.
	 * If the <code>companyUserGroupId</code> property is set, the action will be an update;
	 * otherwise a new group will be created.
	 */
	UserGroup saveOrUpdateCompanyUserGroup(UserGroupDTO UserGroupDTO);

	/**
	 * Modify a group's <code>active</code> flag.
	 */
	void updateGroupActiveFlag(long groupId, boolean active);

	List<User> findAllUsersOfGroupById(Long groupId);

	UserGroup findGroupById(Long groupId);

	UserGroup getByUuid(String uuid);

	/**
	 *
	 * @param groupId -
	 * @return - a lightweight UserGroup object with no associations initialized
	 */
	UserGroup findGroupByIdNoAssociations(Long groupId);

	UserGroup findCompanyUserGroupByName(Long companyId, String name);

	UserGroup findOrCreateCompanyGroup(Long companyId, String groupName, User actor, String legacyGroupName);

	boolean isCompanyUserGroup(Long companyId, Long groupId);

	List<UserGroup> findCompanyOwnedGroupsHavingUserAsMember(Long companyId, Long id);

	Integer countAllGroupsByCompany(Long companyId);

	UserGroupPagination findAllGroupsByCompanyId(Long companyId, UserGroupPagination userGroupPagination);

	UserUserGroupAssociation buildUserUserGroupAssociation(final Long invitedUser, final UserGroup userGroup, List<UserGroupInvitation> invites);

	/**
	 * Find the sum of all newly paid work (assignments) of a user group since the last time
	 * that group's UserGroupSummary has been updated. Does not return groups
	 * with sums of zero since the last update.
	 */
	List<UserGroupThroughputDTO> calculateThroughputSinceLastUpdate();

	List<Long> getUserGroupIdsWithAgreement(Long agreementId);

	/**
	 * Find all groups that have received routed work since the last time
	 * that group's UserGroupSummary has been updated. Does not return groups
	 * with no new routed work since the last update.
	 */
	List<UserGroupLastRoutedDTO> findAllWithNewLastRoutedSinceLastUpdate();

		// Membership
	/**
	 * Add users to a group. If the group membership is open and the user is not a member, an invitation to join is sent. If the group is private, then the user is added as a member.
	 *
	 * @param userIds The user IDs
	 * @param groupId The group ID
	 */
	void addUsersToGroup(List<Long> userIds, Long groupId, Long invitedByUserId);


	// Apply on behalf of users to a group - only works if company has Lane 2 relationship with worker
	Map<String, List<String>> applyOnBehalfOfUsers(List<String> userNumbers, Long groupId, Long invitedByUserId, boolean suppressNotification, boolean override);

	/**
	 * Add users to a group by user number. If the group membership is open and the user is not a member, an invitation to join is sent. If the group is private, then the user is added as a member.
	 *
	 * @param userNumbers
	 * @param groupId
	 * @param invitedByUserId
	 */
	void addUsersToGroupByUserNumberAsync(List<String> userNumbers, Long groupId, Long invitedByUserId);

	/**
	 * User application to a group. Checks a user against the group requirements and updates their verified and approval status accordingly.
	 *
	 * @param userGroupId Group to apply
	 * @param userId User who is applying
	 * @return The user group association
	 */
	UserUserGroupAssociation applyToGroup(Long userGroupId, Long userId);

	/**
	 * Remove a user from a group. Use by both an admin who wants to remove a user, and a user who wants to leave a group.
	 *  @param userGroupId Group to remove
	 * @param userId User who is leaving group
	 */
	void removeAssociation(long userGroupId, long userId);

	void removeAssociations(Long userGroupId, Long[] userIds, Long companyId);

	void removeAllAssociationsAndInvitationsByUserAndCompanyId(Long userId, Long companyId);

	void removeAllAssociationsByUserAndCompanyId(Long userId, Long companyId);

	Integer removeAllAssociationsBetweenCompanies(long companyId1, long companyId2);

	/**
	 * Approve a user's membership in a group
	 */
	void approveUser(Long userGroupId, Long userId);

	void approveUsers(Long userGroupId, Long[] userIds);

	/**
	 * Use approveUser
	 */
	List<UserUserGroupAssociation> approveUserAssociations(long userGroupId, List<Long> userId);

	/**
	 * Decline a user's membership to a group
	 */
	void declineUser(Long userGroupId, Long userId);

	void declineUsers(Long userGroupId, Long[] userId);

	/**
	 * Find a user group association (i.e. membership) by user and group.
	 */
	UserUserGroupAssociation findAssociationByGroupIdAndUserId(long userGroupId, long userId);

	/**
	 * Find a groups's pending user associations (i.e. memberships).
	 */
	UserUserGroupAssociationPagination findAllPendingAssociationByGroupId(Long userGroupId, UserUserGroupAssociationPagination pagination);

	/**
	 * Find a groups's pending and failed user associations (i.e. memberships).
	 */
	UserUserGroupAssociationPagination findAllPendingAndFailedAssociationByGroupId(Long userGroupId, UserUserGroupAssociationPagination pagination);

	UserPagination findAllUsersOfGroup(Long userGroupUd, UserPagination pagination);

	UserPagination findAllInvitedUsers(Long groupId, UserPagination pagination);

	boolean isUserMemberOfGroup(Long groupId, Long userId);

	void revalidateAllAssociations(Long groupId);

	List<UserUserGroupAssociation> findAllActiveAssociations(long groupId);

	void reinviteAllGroupMembers(long userGroupId, UserGroupInvitationType userGroupInvitationType);

	Integer countAllActiveGroupMembers(Long groupId);

	List<Long> getAllActiveGroupMemberIds(Long groupId);

	Integer countAllActiveGroupMembersByCompany(Long companyId);

	List<Long> getDueForValidationUserGroupIds();

	// Requirements

	Eligibility reValidateRequirementSets(long groupId, long userId);

	Eligibility validateRequirementSets(long groupId, long userId);

	/**
	 * Is user authorized to be a member of this group?
	 */
	boolean authorizeUserForGroup(long userGroupId, long userId);

	/**
	 * Get the current user's authorization context for the requested entity.
	 */
	List<RequestContext> getRequestContext(Long groupId);

	/**
	 * Finds all my company's groups (in the context of the userId)
	 */
	ManagedCompanyUserGroupRowPagination findMyCompanyGroups(Long userId, ManagedCompanyUserGroupRowPagination pagination);

	ManagedCompanyUserGroupRowPagination findCompanyActiveGroups(Long userId, ManagedCompanyUserGroupRowPagination pagination);

	/**
	 *
	 * @param companyId -
	 * @return - All of a companies active/non-deleted groups (groups they own) and active/non-deleted shared groups (groups in their networks)
	 */
	List<ManagedCompanyUserGroupRow> findSharedAndOwnedGroups(Long companyId);

	/**
	 *
	 * @param userId -
	 * @param companyId -
	 * @param pagination -
	 * @return - A paginated response of a companies non-deleted groups (groups they own) and non-deleted shared groups (groups in their networks)
	 */
	ManagedCompanyUserGroupRowPagination findSharedAndOwnedGroups(final Long userId, final Long companyId, final ManagedCompanyUserGroupRowPagination pagination);

	/**
	 * Finds all my approved group memberships (in the context of the userId)
	 */
	ManagedCompanyUserGroupRowPagination findMyGroupMemberships(Long userId, ManagedCompanyUserGroupRowPagination pagination);

	/**
	 * Finds all my approved group memberships (in the context of the user)
	 */
	ManagedCompanyUserGroupRowPagination findMyGroupMemberships(User user, ManagedCompanyUserGroupRowPagination pagination);

	/**
	 * Finds all my approved and pending group applications (in the context of the userId)
	 */
	ManagedCompanyUserGroupRowPagination findMyGroupMembershipsAndApplications(Long userId, ManagedCompanyUserGroupRowPagination pagination);

	ManagedCompanyUserGroupRowPagination findVendorGroupMembershipsAndApplications(
		Long userId,
		Set<Long> groupIds,
		ManagedCompanyUserGroupRowPagination pagination
	);

	ManagedCompanyUserGroupRowPagination findCompanyGroupsActiveOpenMembership(long companyId, ManagedCompanyUserGroupRowPagination pagination);

	ManagedCompanyUserGroupRowPagination findCompanyGroupsOpenMembership(Long companyId, ManagedCompanyUserGroupRowPagination pagination);

	/**
	 *
	 * @param groupIds - any set of groupIds
	 * @param pagination -
	 * @return - returns groupId, groupName, group membership type (public or private), and group owner company name
	 */
	ManagedCompanyUserGroupRowPagination findGroupsActiveOpenMembershipByGroupIds(Set<Long> groupIds, ManagedCompanyUserGroupRowPagination pagination);

	void deleteGroup(Long userGroupId);

	GroupMembershipPagination findGroupMembersByUserGroupId(Long groupId, String groupMemberRequestTypeString, GroupMembershipPagination groupMembershipPagination);

	Integer countGroupMembersByUserGroupId(Long groupId, String groupMemberRequestTypeString);

	int countPendingMembershipsByCompany(Long companyId);

	int reassignGroupOwnership(Long fromId, Long toId);

	List<Long> getEligibleUserIdsForInvitationToGroup(List<Long> userIds, Long groupId);

	List<Long> findAllUserIdsOfGroup(Long userGroupId);

	List<Long> findAllUserGroupIds();

	UserGroup saveOrUpdateUserGroup(UserGroup userGroup);

	UserUserGroupAssociation saveOrUpdateUserUserGroupAssociation(UserUserGroupAssociation userUserGroupAssociation);

	UserGroupSummary findUserGroupSummaryByUserGroup(long userGroupId);

	int countGroupsCreatedSince(long companyId, Calendar fromDate);

	Long findUserGroupIdByUuid(String uuid);

	String findUserGroupUuidById(Long userGroupId);

	int countUserGroupMemberships(long userId);

	Map<String, Long> findUserGroupUuidIdPairsByUuids(Collection<String> uuids);

	List<SkillDTO> findUserGroupSkills(Long userGroupId);
}
