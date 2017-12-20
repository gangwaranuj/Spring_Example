package com.workmarket.domains.groups.service;

import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.service.UserGroupAssociationValidationUpdateServiceImpl.AssociationUpdateType;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;

import java.util.Set;

public interface UserGroupAssociationValidationUpdateService {

	Set<AssociationUpdateType> getMetRequirementsAssociationUpdateTypes(UserUserGroupAssociation association);

	Set<AssociationUpdateType> getFailedRequirementsAssociationUpdateTypes(UserUserGroupAssociation association);

	void updateAssociation(UserUserGroupAssociation association, Set<AssociationUpdateType> associationUpdateTypes, UserGroupInvitationType invitationType, double groupFit);
}
