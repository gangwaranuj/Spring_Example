package com.workmarket.dao.acl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.acl.UserAclRoleAssociation;

@SuppressWarnings("unchecked")
@Repository
public class UserAclRoleAssociationDAOImpl extends DeletableAbstractDAO<UserAclRoleAssociation> implements UserAclRoleAssociationDAO {

	protected Class<UserAclRoleAssociation> getEntityClass() {
		return UserAclRoleAssociation.class;
	}
		
	@Override
	public List<UserAclRoleAssociation> findAllRolesByUser(Long userId, boolean includeVirtualRoles){
		
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());		
		criteria.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("role", FetchMode.JOIN)		
				.createAlias("role", "role")
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("deleted", Boolean.FALSE));
		
		if (!includeVirtualRoles) {
			criteria.add(Restrictions.eq("role.virtual", includeVirtualRoles));
		}
			
		return (List<UserAclRoleAssociation>) criteria.list();
		
	}
		
	@Override
	public UserAclRoleAssociation findUserRoleAssociation(Long userId, Long roleId){
		
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());		
		criteria.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("role", FetchMode.JOIN)
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("role.id", roleId))
				.setMaxResults(1);
						
		return (UserAclRoleAssociation) criteria.uniqueResult();
	}
	
}
