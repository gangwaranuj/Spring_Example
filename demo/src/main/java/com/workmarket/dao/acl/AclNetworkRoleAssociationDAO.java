package com.workmarket.dao.acl;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.acl.AclNetworkRoleAssociation;

import java.util.List;


public interface AclNetworkRoleAssociationDAO extends DeletableDAOInterface<AclNetworkRoleAssociation>{

	/**
	 * find all the possible company acl roles, ordered by name
	 *
	 * @return
	 */
	public List<AclNetworkRoleAssociation> findPossibleCompanyAclNetworkRoles();

	/**
	 * find all the roles for a company by company id
	 *
	 * @param CompanyId
	 * @return
	 */
	public List<AclNetworkRoleAssociation> findAclNetworkRoleAssociationsByCompanyId(Long CompanyId);


	/**
	 * soft delete a company network association by company id
	 *
	 * @param companyId
	 */
	public void softDeleteNetworkRoleAssociationByCompanyId(Long companyId);

}
