package com.workmarket.dao.request;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.request.*;

import java.util.List;


public interface RequestDAO extends DAOInterface<Request>{

	List<Request> findByRequestor(Long userId);
	List<Request> findByInvitedUser(Long userId);
	List<Request> findByInvitation(Long invitationId);

	List<AssessmentInvitation> findAssessmentInvitationsByAssessment(Long assessmentId);
	RequestPagination findAssessmentInvitationsByAssessment(Long assessmentId, RequestPagination pagination);
	List<AssessmentInvitation> findAssessmentInvitationsByInvitedUserAndAsseessment(Long userId, Long assessmentId);

	int countUserGroupInvitationsByInvitedUser(Long userId);

	void deleteUsersInvitationsToAllCompanyGroups(Long userId, Long companyId);

	List<UserGroupInvitation> findUserGroupInvitationsByUser(Long userId);
	List<UserGroupInvitation> findUserGroupInvitationsByUserGroup(Long groupId);
	List<UserGroupInvitation> findUserGroupInvitationsByInvitedUserAndUserGroup(Long userId, Long groupId);
	UserGroupInvitation findLatestUserGroupInvitationByInvitedUserAndUserGroup(Long userId, Long groupId);
	List<UserGroupInvitation> findUserGroupInvitationsByInvitedUsersAndUserGroup(List<Long> userIds, Long groupId);
	List<UserGroupInvitation> findUserGroupInvitationsAllStatusByInvitedUsersAndUserGroup(List<Long> userIds, Long groupId);

	UserGroupInvitationPagination findUserGroupInvitationsForInvitedUser(Long userId, UserGroupInvitationPagination pagination);

	PasswordResetRequest findLatestSentPasswordResetRequestByInvitedUser(Long userId);

}
