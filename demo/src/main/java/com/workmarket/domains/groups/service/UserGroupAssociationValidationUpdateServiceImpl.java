package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.groups.service.association.UserGroupAssociationService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserGroupAssociationValidationUpdateServiceImpl implements UserGroupAssociationValidationUpdateService {

	@Resource private UserGroupAssociationService userGroupAssociationService;
	@Resource private UserGroupService userGroupService;
	@Resource private RequestService requestService;

	protected enum AssociationUpdateType {
		MEETS_REQUIREMENTS_SET_VERIFIED,
		MEETS_REQUIREMENTS_SET_APPROVED,
		FAILS_REQUIREMENTS_SET_UNVERIFIED,
		FAILS_REQUIREMENTS_SET_PENDING
	}

	@Override
	public Set<AssociationUpdateType> getMetRequirementsAssociationUpdateTypes(UserUserGroupAssociation association) {
		Set<AssociationUpdateType> associationUpdateTypes = new HashSet<>();
		if (!association.getVerificationStatus().isVerified()) {
			associationUpdateTypes.add(AssociationUpdateType.MEETS_REQUIREMENTS_SET_VERIFIED);
		}

		ApprovalStatus approvalStatus = association.getApprovalStatus();
		UserGroup userGroup = association.getUserGroup();
		if (approvalStatus.isPending() && BooleanUtils.isFalse(userGroup.getRequiresApproval())) {
			associationUpdateTypes.add(AssociationUpdateType.MEETS_REQUIREMENTS_SET_APPROVED);
		}

		return associationUpdateTypes;
	}

	@Override
	public Set<AssociationUpdateType> getFailedRequirementsAssociationUpdateTypes(UserUserGroupAssociation association) {
		Set<AssociationUpdateType> associationUpdateTypes = new HashSet<>();
		if (!association.getVerificationStatus().isFailed()) {
			associationUpdateTypes.add(AssociationUpdateType.FAILS_REQUIREMENTS_SET_UNVERIFIED);
		}

		if (association.getApprovalStatus().isApproved()) {
			associationUpdateTypes.add(AssociationUpdateType.FAILS_REQUIREMENTS_SET_PENDING);
		}

		return associationUpdateTypes;
	}

	@Override
	public void updateAssociation(UserUserGroupAssociation association, Set<AssociationUpdateType> associationUpdateTypes, UserGroupInvitationType invitationType, double groupFit) {
		for (AssociationUpdateType type : associationUpdateTypes) {
			updateAssociation(association, type, invitationType, groupFit);
		}
	}

	private void updateAssociation(UserUserGroupAssociation association, AssociationUpdateType type, UserGroupInvitationType invitationType, double groupFit) {
		UserGroup userGroup = association.getUserGroup();
		Long userGroupId = userGroup.getId();
		Long userId = association.getUser().getId();
		Long associationId = association.getId();

		switch (type) {
			case MEETS_REQUIREMENTS_SET_VERIFIED:
				userGroupAssociationService.updateUserUserGroupAssociation(associationId, VerificationStatus.VERIFIED, groupFit);
				break;
			case MEETS_REQUIREMENTS_SET_APPROVED:
				// updateUserUserGroupAssociation is only called to update groupFit
				userGroupAssociationService.updateUserUserGroupAssociation(associationId, ApprovalStatus.PENDING, groupFit);
				userGroupService.approveUserAssociations(userGroupId, Lists.newArrayList(userId));
				break;
			case FAILS_REQUIREMENTS_SET_UNVERIFIED:
				userGroupAssociationService.updateUserUserGroupAssociation(associationId, VerificationStatus.FAILED, groupFit);
				break;
			case FAILS_REQUIREMENTS_SET_PENDING:
				userGroupAssociationService.updateUserUserGroupAssociation(associationId, ApprovalStatus.PENDING, groupFit);
				requestService.inviteUserToGroup(userGroup.getCreatorId(), userId, userGroupId, invitationType);
				break;
			default:
				break;
		}
	}
}
