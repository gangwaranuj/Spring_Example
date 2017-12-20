package com.workmarket.dao.acl;

import com.google.common.collect.Maps;
import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.AclRoleType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AclRoleDAOImpl extends DeletableAbstractDAO<AclRole> implements AclRoleDAO {

	@Qualifier("jdbcTemplate") @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
	
	protected Class<AclRole> getEntityClass() {
        return AclRole.class;
    }
	
	
	@Override
	public AclRole findRoleById(Long id) {
		return (AclRole)getFactory().getCurrentSession().get(AclRole.class, id);
		
	}
	
	@Override
	public AclRole findSystemRoleByName(String roleName) {
		return (AclRole)getFactory().getCurrentSession().getNamedQuery("aclrole.findSystemRoleByName")
			.setParameter("roleName", roleName).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AclRole> findAllAclRoles() {
		return getFactory().getCurrentSession().getNamedQuery("aclrole.findAll").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AclRole> findAllInternalAclRoles() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("aclRoleType.code", AclRoleType.INTERNAL));
				
		return (List<AclRole>)criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AclRole> findAclRoles(Long companyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.or(Restrictions.and(
										Restrictions.eq("aclRoleType.code", AclRoleType.SYSTEM),
										Restrictions.eq("virtual", Boolean.FALSE)										), 
					Restrictions.eq("company.id", companyId)));
											    
		return (List<AclRole>)criteria.list();
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public List<AclRole> findAllCustomRoles(Long companyId) {
		
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("company.id", companyId));
				    
		return (List<AclRole>)criteria.list();
	}

	@Override
	public String getRolesStringbyUser(Long userId){
		StringBuilder sql = new StringBuilder("SELECT GROUP_CONCAT(ar.name order by ar.name SEPARATOR ', ')");
		sql.append(" FROM  user_acl_role uar ");
		sql.append(" INNER JOIN acl_role ar ON uar.acl_role_id = ar.id");
		sql.append(" WHERE uar.user_id = :userId AND uar.deleted = 0");
		sql.append(" GROUP BY uar.user_id");
		
		Map<String,Long> params  = Maps.newHashMap();
	
		params.put("userId", userId);
		try {			
			return jdbcTemplate.queryForObject(sql.toString(), params, String.class);
		}
		catch (EmptyResultDataAccessException e){
			return null;
		}
	}
}
