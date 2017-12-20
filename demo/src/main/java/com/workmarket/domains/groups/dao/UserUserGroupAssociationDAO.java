package com.workmarket.domains.groups.dao;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.groups.model.UserUserGroupAssociationPagination;

import java.util.List;


public interface UserUserGroupAssociationDAO extends PaginatableDAOInterface<UserUserGroupAssociation> {

	UserUserGroupAssociation findUserUserGroupAssociationById(long id);

	UserUserGroupAssociation findUserUserGroupAssociationByUserGroupIdAndUserId(Long userGroupId, Long userId);

	List<UserUserGroupAssociation> findUserUserGroupAssociationByUserGroupIdAndUserId(Long userGroupId, List<Long> userIds);

	UserUserGroupAssociationPagination findAllUserUserAssociationsByUserGroupIdAndVerificationStatusAndApprovalStatus(long userGroup1Id, VerificationStatus verificationStatus, ApprovalStatus approvalStatus, UserUserGroupAssociationPagination pagination);

	List<User> findAllUsersOfGroup(long groupId);

	UserPagination findAllUserOfGroup(long groupId, UserPagination pagination);

	List<UserUserGroupAssociation> findAllActiveAssociations(final long userGroupId);

	List<UserUserGroupAssociation> findAllActiveAssociationsByUserIdAndCompanyId(Long userId, Long companyId);

	List<Long> findAllAssociationIdsBetweenCompanies(Long companyId1, Long companyId2);

	Integer removeAssociations(List<Long> ids);

	List<UserUserGroupAssociation> findCompanyOwnedGroupAssociationsHavingUserAsMember(Long companyId, Long userId);

	/*
	 * NOTE: this includes deleted associations
	 */
	List<UserUserGroupAssociation> findAllAssociationsByGroupAndUserIds(List<Long> userIds, Long groupId);

	List<UserUserGroupAssociation> findAllUserUserGroupAssociationsByGroupIdAndUsers(Long userGroupId, List<Long> invitedUserIds);

	List<Long> findAllUserIdsOfGroup(long groupId);

	List<Long> findAllAssociationsWithCertification(long userId, long certificationId, ApprovalStatus approvalStatus);

	List<Long> findAllAssociationsWithLicense(long userId, long licenseId, ApprovalStatus approvalStatus);

	List<Long> findAllAssociationsWithIndustry(long userId, ApprovalStatus approvalStatus);

	List<Long> findAllAssociationsWithInsurance(long userId, long insuranceId, ApprovalStatus approvalStatus);

	List<Long> findAllAssociationsWithDrugTest(long userId, ApprovalStatus approvalStatus);

	List<Long> findAllAssociationsWithBackgroundCheck(long userId, ApprovalStatus approvalStatus);

	Long findUserGroupAssociationByUserIdAndGroupId(long userId, long groupId, ApprovalStatus approvalStatus);

	List<Long> findAllAssociationsWithAssessment(long userId, long assessmentId, ApprovalStatus approvalStatus);

	List<Long> findAllAssociationsWithLaneRequirement(long userId, ApprovalStatus approvalStatus);

	List<Long> findAllAssociationsWithWorkingHours(long userId, ApprovalStatus approvalStatus);

	List<Long> findAllAssociationsWithLocationRequirements(long userId, ApprovalStatus approvalStatus);

	List<Long> findAllAssociationsWithRating(long userId, ApprovalStatus approvalStatus);

	List<Long> findAllUserGroupAssociationsByUserIdAndApprovalStatus(long userId, ApprovalStatus approvalStatus);
}
