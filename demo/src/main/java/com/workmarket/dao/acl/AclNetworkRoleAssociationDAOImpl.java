package com.workmarket.dao.acl;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.acl.AclNetworkRoleAssociation;
import com.workmarket.domains.model.acl.AclRole;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class AclNetworkRoleAssociationDAOImpl extends DeletableAbstractDAO<AclNetworkRoleAssociation> implements AclNetworkRoleAssociationDAO {


	protected Class<AclRole> getEntityClass() {
		return AclRole.class;
	}


	// == read operations == //

	@Override
	public List<AclNetworkRoleAssociation> findPossibleCompanyAclNetworkRoles() {
		Query query = getFactory().getCurrentSession().getNamedQuery("networkacl.findPossibleAclNetworkRoleAssociations");
		return query.list();
	}

	@Override
	public List<AclNetworkRoleAssociation> findAclNetworkRoleAssociationsByCompanyId(Long companyId) {
		Assert.notNull(companyId);
		Query query = getFactory().getCurrentSession().getNamedQuery("networkacl.findAclNetworkRoleAssociationsByCompanyId");
		query.setParameter("companyId", companyId);
		return query.list();
	}

	public void softDeleteNetworkRoleAssociationByCompanyId(Long companyId) {
		Assert.notNull(companyId);
		Query query = getFactory().getCurrentSession().getNamedQuery("networkacl.softDeleteAclNetworkRoleAssociationsByCompanyId");
		query.setParameter("companyId", companyId);
		query.executeUpdate();
	}


}
