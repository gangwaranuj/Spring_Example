package com.workmarket.domains.groups.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.groups.model.ScheduledRun;
import com.workmarket.domains.groups.model.UserGroupEvaluationScheduledRun;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class
UserGroupEvaluationScheduledRunDAOImpl extends AbstractDAO<UserGroupEvaluationScheduledRun> implements UserGroupEvaluationScheduledRunDAO {

	@Resource private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	@Override
	protected Class<UserGroupEvaluationScheduledRun> getEntityClass() {
		return UserGroupEvaluationScheduledRun.class;
	}

	@Override
	public ScheduledRun findNextScheduledRunForActiveGroup(final long userGroupId) {
		final SQLBuilder builder =
			buildFindNextScheduledRun(userGroupId)
				.addWhereClause("scheduled_run.next_run <= current_date()")
				.addWhereClause("user_group.active_flag = 1");
		return getScheduledRun(builder);
	}

	@Override
	public ScheduledRun findNextFutureScheduledRunForActiveOrInactiveGroup(final long userGroupId) {
		final SQLBuilder builder = buildFindNextScheduledRun(userGroupId);
		return getScheduledRun(builder);
	}

	private ScheduledRun getScheduledRun(final SQLBuilder builder) {
		final RowMapper<ScheduledRun> mapper = buildScheduledRunRowMapper();
		final List<ScheduledRun> results = readOnlyJdbcTemplate.query(builder.build(), builder.getParams(), mapper);
		return results.isEmpty() ? null : results.get(0);
	}

	private SQLBuilder buildFindNextScheduledRun(long userGroupId) {
		return new SQLBuilder()
				.addColumn("scheduled_run.*")
				.addTable("scheduled_run")
				.addJoin("JOIN user_group_evaluation_scheduled_run ugesr ON scheduled_run.id = ugesr.scheduled_run_id")
				.addJoin("JOIN user_group on user_group.id = ugesr.user_group_id")
				.addWhereClause("ugesr.user_group_id = :userGroupId")
				.addWhereClause("scheduled_run.deleted = 0")
				.addWhereClause("user_group.deleted = 0")
				.addParam("userGroupId", userGroupId);
	}

	private RowMapper<ScheduledRun> buildScheduledRunRowMapper() {
		return new RowMapper<ScheduledRun>() {
			@Override
			public ScheduledRun mapRow(ResultSet resultSet, int i) throws SQLException {
				ScheduledRun scheduledRun = new ScheduledRun();
				scheduledRun.setId(resultSet.getLong("scheduled_run.id"));
				scheduledRun.setNextRun(DateUtilities.getCalendarFromDate(resultSet.getTimestamp("scheduled_run.next_run")));
				scheduledRun.setInterval(resultSet.getInt("scheduled_run.time_interval"));
				scheduledRun.setCreatorId(resultSet.getLong("scheduled_run.creator_id"));
				return scheduledRun;
			}
		};
	}
}
