package com.workmarket.service.network;

import com.workmarket.dao.acl.AclNetworkRoleAssociationDAO;
import com.workmarket.domains.model.acl.AclNetworkRoleAssociation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ant on 7/28/14.
 */
@Service
public class AclNetworkRoleAssociationServiceImpl implements AclNetworkRoleAssociationService {

	@Autowired private AclNetworkRoleAssociationDAO aclNetworkRoleAssociationDAO;

	@Override
	public List<AclNetworkRoleAssociation> findAllCompanyAclNetworkRoles() {
		return aclNetworkRoleAssociationDAO.findPossibleCompanyAclNetworkRoles();
	}

	@Override
	public List<AclNetworkRoleAssociation> findAclNetworkRoleAssociationsByCompanyId(Long companyId) {
		return aclNetworkRoleAssociationDAO.findAclNetworkRoleAssociationsByCompanyId(companyId);
	}

	@Override
	public void saveOrUpdate(AclNetworkRoleAssociation network) {
		aclNetworkRoleAssociationDAO.saveOrUpdate(network);
	}

	@Override
	public void softDeleteNetworkRoleAssociationByCompanyId(Long companyId) {
		aclNetworkRoleAssociationDAO.softDeleteNetworkRoleAssociationByCompanyId(companyId);
	}
}
