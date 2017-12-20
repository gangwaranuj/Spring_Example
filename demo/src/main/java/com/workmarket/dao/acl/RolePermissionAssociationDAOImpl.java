package com.workmarket.dao.acl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.acl.RolePermissionAssociation;

@Repository
public class RolePermissionAssociationDAOImpl extends DeletableAbstractDAO<RolePermissionAssociation> implements RolePermissionAssociationDAO {

	protected Class<RolePermissionAssociation> getEntityClass() {
		return RolePermissionAssociation.class;
	}
	
	public RolePermissionAssociation findRolePermissionAssociation(Long roleId, String permissionCode) {
		
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());		
		criteria.add(Restrictions.eq("role.id", roleId));
		criteria.add(Restrictions.eq("permission.code", permissionCode));
		
				
		return (RolePermissionAssociation) criteria.uniqueResult();
		
	}
}
