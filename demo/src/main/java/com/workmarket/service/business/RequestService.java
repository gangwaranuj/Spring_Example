package com.workmarket.service.business;

import com.workmarket.domains.model.User;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.model.request.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RequestService {

	void acceptRequest(Request request);

	void deleteRequest(Long requestId);

	List<Request> findRequestsByRequestor(Long userId);

	List<Request> findRequestsByInvitedUser(Long userId);

	List<Request> findRequestsByInvitation(Long invitationId);

	List<AssessmentInvitation> findAssessmentInvitationRequestsByAssessment(Long assessmentId);

	RequestPagination findAssessmentInvitationRequestsByAssessment(Long assessmentId, RequestPagination pagination);

	int countUserGroupInvitationsByInvitedUser(Long userId);

	List<UserGroupInvitation> findUserGroupInvitationsByUser(Long userId);

	List<UserGroupInvitation> findUserGroupInvitationRequestsByUserGroup(Long groupId);

	List<UserGroupInvitation> findUserGroupInvitationRequestsByInvitedUserAndUserGroup(Long userId, Long groupId);

	UserGroupInvitation findLatestUserGroupInvitationRequestByInvitedUserAndUserGroup(Long userId, Long groupId);

	Map<Long, List<UserGroupInvitation>> findUserGroupInvitationRequestMapByInvitedUsersAndUserGroup(List<Long> userIds, Long groupId);

	List<UserGroupInvitation> findUserGroupInvitationRequestsAllStatusByInvitedUsersAndUserGroup(List<Long> userIds, Long groupId);

	UserGroupInvitationPagination findUserGroupInvitationsForInvitedUser(Long userId, UserGroupInvitationPagination pagination);

	Boolean userHasInvitationToGroup(Long userId, Long groupId);

	Boolean userHasInvitationToAssessment(Long userId, Long assessmentId);

	void inviteUsersToAssessment(Long userId, Collection<Long> inviteeUserIds, Long assessmentId);

	void inviteUserToAssessment(Long userId, Long inviteeUserId, Long assessmentId);

	void inviteUserToAssessments(Long userId, Long inviteeUserId, Long[] assessmentIds);

	void inviteUsersToGroup(long userId, long[] inviteeUserIds, long groupId);

	void inviteUserToGroup(Long userId, Long inviteeUserId, Long groupId);

	void inviteUserToGroup(Long userId, Long inviteeUserId, Long groupId, UserGroupInvitationType userGroupInvitationType);

	List<Request> saveInvitesForInviteUsersToGroup(List<User> users, User inviteeUser, UserGroup group, UserGroupInvitationType userGroupInvitationType);

	List<FlatRequest> createFlatInvitesForInviteUsersToGroup(List<Long> userIds, Long inviteeUserId, Long groupId, UserGroupInvitationType userGroupInvitationType);

	void saveInvitesForInviteUsersToGroup(List<FlatRequest> requests);

	void sendInvitesForAddUserToGroup(Long groupId, Long requester, List<Long> invitedUserIds);

	void deleteInvitationToGroup(Long userId, Long groupId);

	void deleteInvitationsToGroup(List<Long> userId, Long groupId);

	void extendInvitationToGroup(Long userId, Long invitationId, Long groupId);

	void declineInvitationToGroup(Long userId, Long groupId);

	PasswordResetRequest requestPasswordReset(Long requestorId, Long userId);

	PasswordResetRequest requestPasswordReset(Long requestorId, Long userId, Calendar expirationDate);

	PasswordResetRequest findLatestSentPasswordResetRequestByInvitedUser(Long userId);

	PasswordResetRequest findPasswordResetRequest(String encryptedId);

}
