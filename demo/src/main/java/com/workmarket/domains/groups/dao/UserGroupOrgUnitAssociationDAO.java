package com.workmarket.domains.groups.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.groups.model.UserGroupOrgUnitAssociation;

import java.util.List;

public interface UserGroupOrgUnitAssociationDAO extends DAOInterface<UserGroupOrgUnitAssociation> {
	UserGroupOrgUnitAssociation findUserGroupOrgUnitAssociationByUserGroupAndOrgUnitUuid(final String userGroupUuid, final String orgUnitUuid);
	List<UserGroupOrgUnitAssociation> findOrgUnitAssociationsByGroupId(final String userGroupUuid);
	List<UserGroupOrgUnitAssociation> findAllOrgUnitAssociationsByGroupId(final String userGroupUuid);
	void setUserGroupOrgUnitAssociation(String userGroupUuid, final List<String> orgUnitUuids);
}
