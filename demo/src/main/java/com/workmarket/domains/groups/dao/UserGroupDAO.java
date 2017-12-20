package com.workmarket.domains.groups.dao;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupHydrateData;
import com.workmarket.domains.groups.model.UserGroupLastRoutedDTO;
import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.domains.groups.model.UserGroupThroughputDTO;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.dto.SuggestionDTO;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserGroupDAO extends PaginatableDAOInterface<UserGroup> {

	List<Long> findGroupIdsByOwner(Long ownerId);

	UserPagination findAllUsersByVerificationStatusAndApprovalStatus(Long groupId, VerificationStatus verificationStatus,
			ApprovalStatus approvalStatus, UserPagination pagination);

	Integer countAllActiveGroupMembers(Long groupId);

	List<Long> getAllActiveGroupMemberIds(Long groupId);

	Integer countAllActiveGroupMembersByCompanyId(Long companyId);

	List<SuggestionDTO> suggest(String prefix, String property, Long companyId);

	int updateGroupOwner(final Long newOwnerId, final List<Long> groupIds);

	int deactivateGroupIds(List<Long> groupIds);

	UserGroup findUserGroupById(Long userGroupId);

	List<UserGroup> findUserGroupsByIds(List<Long> userGroupIds);

	UserGroup findUserGroupByName(Long companyId, String name);

	UserGroup findGroupByIdNoAssociations(Long groupId);

	UserGroupPagination findAllUserGroupsByCompanyId(Long companyId, UserGroupPagination pagination);

	List<UserGroup> findAllUserGroupsByCompanyIdAndUserIsMember(Long companyId, Long userId);

	List<UserUserGroupAssociation> findAllGroupAssociationByCompanyIdAndUser(Long companyId, Long userId);

	List<UserGroup> findAllUserGroupsByUserIsMember(Long userId);

	/**
	 * Return the aggregated work price sum since the last tine the UserGroupSummary was touched.
	 *
	 * @return
	 */
	List<UserGroupThroughputDTO> calculateThroughputSinceLastUpdate();

	/**
	 * Return all user group summaries that have received newly routed work since the last summary update.
	 *
	 * @return
	 */
	List<UserGroupLastRoutedDTO> findAllWithNewLastRoutedSinceLastUpdate();

	Set<User> findUsersFromCompanyUserGroups(List<UserGroup> listCompanyUserGroups);

	Integer countUserGroupMemberships(Long userId);

	Integer countCompanyUserGroups(Long companyId);

	Map<Long, UserGroupHydrateData> findAllCompanyUserGroupHydrateData();

	Map<Long, UserGroupHydrateData> findAllCompanyUserGroupHydrateDataByGroupIds(Collection<Long> groupIds);

	List<Long> getGroupIdsNeverRoutedToOlderThan(Date date);

	List<Long> getGroupIdsNotRoutedToSince(Date notRoutedToSince);

	List<Long> getGroupIdsInvitedToSince(Date date);

	List<Long> getUserGroupIdsWithAgreement(Long agreementId);

	List<Long> findAllUserGroupIds();

	List<Long> findDueForValidationUserGroupIds();

	Long findUserGroupIdByUuid(String uuid);

	String findUserGroupUuidById(Long groupId);

	/**
	 * @param userGroupId -
	 * @param userId -
	 * @return - True, if user belongs to company with:
	 * (1) An active, non-deleted association with a network, which...
	 * (2) Has an active, non-deleted association with the specified group
	 */
	boolean userCanViewSharedGroup(Long userGroupId, Long userId);

	boolean isGroupShared(Long groupId);

	Integer getMaxGroupId();

	List<Long> findGroupIdsBetween(long fromId, long toId);

	int countGroupsCreatedSince(long companyId, Calendar fromDate);

	List<Long> findUserGroupsRequiredInsuranceIds(long groupId);

	List<Long> findUserGroupsRequiredCertificationIds(long groupId);

	List<Long> findUserGroupsRequiredLicenseIds(long groupId);

	List<Long> findUserGroupsRequiredAgreementIds(long groupId);

	List<Long> findUserGroupsRequiredIndustryIds(long groupId);

	Map<String, Long> findUserGroupUuidIdPairsByUuids(Collection<String> uuids);
}
