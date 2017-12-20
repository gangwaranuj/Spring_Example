package com.workmarket.domains.groups.service.association;

import com.workmarket.domains.groups.dao.UserUserGroupAssociationDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserGroupAssociationServiceImpl implements UserGroupAssociationService {

	@Autowired UserUserGroupAssociationDAO userGroupAssociationDAO;

	@Override
	public UserUserGroupAssociation findUserUserGroupAssociationById(long id) {
		return userGroupAssociationDAO.findUserUserGroupAssociationById(id);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithLocationRequirements(long userId) {
		return userGroupAssociationDAO.findAllAssociationsWithLocationRequirements(userId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithCertification(long userId, long certificationId) {
		return userGroupAssociationDAO.findAllAssociationsWithCertification(userId, certificationId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithLicense(long userId, long licenseId) {
		return userGroupAssociationDAO.findAllAssociationsWithLicense(userId, licenseId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithIndustry(long userId) {
		return userGroupAssociationDAO.findAllAssociationsWithIndustry(userId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithInsurance(long userId, long insuranceId) {
		return userGroupAssociationDAO.findAllAssociationsWithInsurance(userId, insuranceId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithDrugTest(long userId) {
		return userGroupAssociationDAO.findAllAssociationsWithDrugTest(userId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithBackgroundCheck(long userId) {
		return userGroupAssociationDAO.findAllAssociationsWithBackgroundCheck(userId, ApprovalStatus.PENDING);
	}

	@Override public Long findUserGroupAssociationByUserIdAndGroupId(long userId, long groupId) {
		return userGroupAssociationDAO.findUserGroupAssociationByUserIdAndGroupId(userId, groupId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithAssessment(long userId, long assessmentId) {
		return userGroupAssociationDAO.findAllAssociationsWithAssessment(userId, assessmentId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithLaneRequirement(long userId) {
		return userGroupAssociationDAO.findAllAssociationsWithLaneRequirement(userId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithWorkingHours(long userId) {
		return userGroupAssociationDAO.findAllAssociationsWithWorkingHours(userId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllPendingAssociationsWithRating(long userId) {
		return userGroupAssociationDAO.findAllAssociationsWithRating(userId, ApprovalStatus.PENDING);
	}

	@Override
	public List<Long> findAllUserGroupAssociationsByUserId(long userId) {
		return userGroupAssociationDAO.findAllUserGroupAssociationsByUserIdAndApprovalStatus(userId, ApprovalStatus.APPROVED);
	}

	@Override
	public void updateUserUserGroupAssociation(long userUserGroupAssociationId, VerificationStatus verificationStatus, double score) {
		UserUserGroupAssociation userGroupAssociation = findUserUserGroupAssociationById(userUserGroupAssociationId);
		if (userGroupAssociation != null) {
			userGroupAssociation.setVerificationStatus(verificationStatus);
			userGroupAssociation.setRequirementsFitScore(score);
		}
	}

	@Override
	public void updateUserUserGroupAssociation(long userUserGroupAssociationId, ApprovalStatus approvalStatus, double score) {
		UserUserGroupAssociation userGroupAssociation = findUserUserGroupAssociationById(userUserGroupAssociationId);
		if (userGroupAssociation != null) {
			userGroupAssociation.setApprovalStatus(approvalStatus);
			userGroupAssociation.setRequirementsFitScore(score);
		}
	}
}
