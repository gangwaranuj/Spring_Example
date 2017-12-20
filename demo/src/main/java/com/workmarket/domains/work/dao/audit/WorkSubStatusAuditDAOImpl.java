package com.workmarket.domains.work.dao.audit;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.audit.WorkSubStatusAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class WorkSubStatusAuditDAOImpl extends AbstractDAO<WorkSubStatusAudit> implements WorkSubStatusAuditDAO {

	@Autowired @Resource(name="readOnlyJdbcTemplate") private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<?> getEntityClass() {
		return WorkSubStatusAudit.class;
	}

}
