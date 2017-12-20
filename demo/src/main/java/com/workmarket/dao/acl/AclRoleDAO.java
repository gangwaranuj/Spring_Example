package com.workmarket.dao.acl;

import java.util.List;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.acl.AclRole;


public interface AclRoleDAO extends DeletableDAOInterface<AclRole>{
	
	AclRole findRoleById(Long id);

	AclRole findSystemRoleByName(String roleName);
	
	//Return All SYSTEM and CUSTOM roles including the nonDisplayable roles (WORKER and SHARED WORKER)
	List<AclRole> findAllAclRoles();

	//Returns displayable SYSTEM roles and All CUSTOM roles for a specific company
	List<AclRole> findAclRoles(Long CompanyId);
	
	List<AclRole>  findAllInternalAclRoles();
	
	//Returns Only the CUSTOM roles for a specific company
	List<AclRole> findAllCustomRoles(Long companyId);
	
	String getRolesStringbyUser(Long userId);
}

