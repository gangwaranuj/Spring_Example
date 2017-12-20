package com.workmarket.service.network;

import com.workmarket.domains.model.acl.AclNetworkRoleAssociation;

import java.util.List;

/**
 * Created by ant on 7/28/14.
 */
public interface AclNetworkRoleAssociationService {

	/**
	 * get all company acl network roles, ordered by name
	 *
	 * @return
	 */
	public List<AclNetworkRoleAssociation> findAllCompanyAclNetworkRoles();

	/**
	 * get company acl network roles by company id
	 *
	 * @param companyId
	 * @return
	 */
	public List<AclNetworkRoleAssociation> findAclNetworkRoleAssociationsByCompanyId(Long companyId);

	/**
	 * save or update a company network acl role
	 *
	 * @param network
	 */
	public void saveOrUpdate(AclNetworkRoleAssociation network);

	/**
	 * soft deleted a network acl role for a company by companyId
	 *
	 * @param companyId
	 */
	public void softDeleteNetworkRoleAssociationByCompanyId(Long companyId);

}
