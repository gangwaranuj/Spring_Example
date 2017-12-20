package com.workmarket.dao.acl;

import java.util.List;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.acl.UserAclRoleAssociation;

public interface UserAclRoleAssociationDAO extends DeletableDAOInterface<UserAclRoleAssociation>{
	
	List<UserAclRoleAssociation> findAllRolesByUser(Long userId, boolean includeVirtualRoles);
	
	UserAclRoleAssociation findUserRoleAssociation(Long userId, Long roleId);
	
}
