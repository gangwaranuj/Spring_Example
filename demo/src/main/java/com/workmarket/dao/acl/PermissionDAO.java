package com.workmarket.dao.acl;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.acl.Permission;

import java.util.List;

public interface PermissionDAO extends DAOInterface<Permission>{

	Permission findPermissionByCode(String permissionCode);
	
	List<Permission> findAllPermissions();

	List<Permission> findPermissionsByUser(Long userId);
		
	Permission findPermissionByUserAndPermissionCode(Long userId, String permissionCode);
}
