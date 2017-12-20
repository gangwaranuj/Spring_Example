package com.workmarket.dao.acl;

import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.acl.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class PermissionDAOImpl extends AbstractDAO<Permission> implements PermissionDAO {

	@Qualifier("readOnlyJdbcTemplate") @Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	protected Class<Permission> getEntityClass() {
		return Permission.class;
	}
	
	public Permission findPermissionByCode(String permissionCode){
		return (Permission) getFactory().getCurrentSession().getNamedQuery("permission.find")
			.setParameter("code", permissionCode).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Permission> findAllPermissions(){
			return getFactory().getCurrentSession().getNamedQuery("permission.findAll").list();
	}

	@Override
	public List<Permission> findPermissionsByUser(Long userId){
		Assert.notNull(userId);
				
		StringBuilder sql = new StringBuilder()
				.append(" SELECT 	DISTINCT p.* FROM user_acl_role ar")
				.append(" INNER 	join role_permission role ")
				.append(" ON 		ar.acl_role_id = role.acl_role_id")
				.append(" INNER 	JOIN permission p")
				.append(" ON 		p.code = role.permission_code")
				.append(" WHERE 	ar.user_id = :userId ")
				.append(" AND 		ar.deleted = 0");
		
		Map<String,Object> params  = Maps.newHashMap();
        params.put("userId", userId);
        
        return jdbcTemplate.query(sql.toString(), params, mapper);	 
	}
	
	@Override
	public Permission findPermissionByUserAndPermissionCode(Long userId, String permissionCode) {
		Assert.notNull(userId);
		Assert.hasText(permissionCode);
						
		StringBuilder sql = new StringBuilder()
				.append(" SELECT 	p.* FROM user_acl_role ar")
				.append(" INNER 	join role_permission role ")
				.append(" ON 		ar.acl_role_id = role.acl_role_id")
				.append(" INNER 	JOIN permission p")
				.append(" ON 		p.code = role.permission_code")
				.append(" WHERE 	ar.user_id = :userId ")
				.append(" AND 		p.code = :permissionCode")
				.append(" AND 		ar.deleted = 0 LIMIT 1");

		Map<String, Object> params  = Maps.newHashMap();
		params.put("userId", userId);
		params.put("permissionCode", permissionCode);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), params, mapper);
		}
		catch (EmptyResultDataAccessException e){
			return null;
		}		
	}
	
	private final RowMapper<Permission> mapper = new RowMapper<Permission>() {
		public Permission mapRow(ResultSet rs, int rowNum) throws SQLException {
			Permission p = new Permission();

			p.setCode(rs.getString("code"));
			p.setDescription(rs.getString("description"));
			return p;
		}
	};
	
}
