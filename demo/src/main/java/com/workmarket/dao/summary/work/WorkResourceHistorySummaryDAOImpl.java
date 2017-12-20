package com.workmarket.dao.summary.work;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.summary.work.WorkResourceHistorySummary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: rocio
 */
@Repository
public class WorkResourceHistorySummaryDAOImpl extends AbstractDAO<WorkResourceHistorySummary> implements WorkResourceHistorySummaryDAO {

	@Override
	protected Class<WorkResourceHistorySummary> getEntityClass() {
		return WorkResourceHistorySummary.class;
	}

	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	private final static String SELECT_WORK_BY_USER = "SELECT work_id FROM work_resource_history_summary " +
			" WHERE work_resource_status_type_code = :workResourceStatusType " +
			" AND 	user_id = :userId ";

	@Override
	public List<Long> getAllWorkIdsByWorkResourceUserIdAndStatus(long userId, WorkResourceStatusType workResourceStatusType) {
		Assert.notNull(workResourceStatusType);
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", userId)
				.addValue("workResourceStatusType", workResourceStatusType.getCode());
		return jdbcTemplate.queryForList(SELECT_WORK_BY_USER, params, Long.class);
	}
}
