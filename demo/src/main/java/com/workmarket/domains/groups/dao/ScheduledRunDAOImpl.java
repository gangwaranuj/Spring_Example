package com.workmarket.domains.groups.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.groups.model.ScheduledRun;
import com.workmarket.domains.groups.model.UserGroupEvaluationScheduledRun;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class ScheduledRunDAOImpl extends AbstractDAO<ScheduledRun> implements ScheduledRunDAO {

	@Override
	protected Class<ScheduledRun> getEntityClass() {
		return ScheduledRun.class;
	}

}
