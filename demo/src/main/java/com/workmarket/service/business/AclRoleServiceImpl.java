package com.workmarket.service.business;

import com.workmarket.dao.acl.AclRoleDAO;
import com.workmarket.domains.model.acl.AclRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ianha on 8/11/14
 */
@Service
public class AclRoleServiceImpl implements AclRoleService {
	@Autowired private AclRoleDAO aclRoleDAO;

	@Override
	public AclRole findAclRoleById(long id) {
		return aclRoleDAO.findRoleById(id);
	}
}
