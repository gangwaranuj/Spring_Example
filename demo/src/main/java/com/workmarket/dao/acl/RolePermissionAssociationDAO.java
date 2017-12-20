package com.workmarket.dao.acl;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.acl.RolePermissionAssociation;

public interface RolePermissionAssociationDAO extends DeletableDAOInterface<RolePermissionAssociation>{
	
	RolePermissionAssociation findRolePermissionAssociation(Long roleId, String permissionCode);
	
}
