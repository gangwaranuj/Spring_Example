package com.workmarket.domains.groups.service.association;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;

import java.util.List;

/**
 * Author: rocio
 */
public interface UserGroupAssociationService {

	UserUserGroupAssociation findUserUserGroupAssociationById(long id);

	Long findUserGroupAssociationByUserIdAndGroupId(long userId, long groupId);

	List<Long> findAllPendingAssociationsWithCertification(long userId, long certificationId);

	List<Long> findAllPendingAssociationsWithLicense(long userId, long licenseId);

	List<Long> findAllPendingAssociationsWithIndustry(long userId);

	List<Long> findAllPendingAssociationsWithInsurance(long userId, long insuranceId);

	List<Long> findAllPendingAssociationsWithDrugTest(long userId);

	List<Long> findAllPendingAssociationsWithBackgroundCheck(long userId);

	List<Long> findAllPendingAssociationsWithAssessment(long userId, long assessmentId);

	List<Long> findAllPendingAssociationsWithLaneRequirement(long userId);

	List<Long> findAllPendingAssociationsWithWorkingHours(long userId);

	List<Long> findAllPendingAssociationsWithLocationRequirements(long userId);

	List<Long> findAllPendingAssociationsWithRating(long userId);

	List<Long> findAllUserGroupAssociationsByUserId(long userId);

	void updateUserUserGroupAssociation(long userUserGroupAssociationId, VerificationStatus verificationStatus, double score);

	void updateUserUserGroupAssociation(long userUserGroupAssociationId, ApprovalStatus approvalStatus, double score);
}
