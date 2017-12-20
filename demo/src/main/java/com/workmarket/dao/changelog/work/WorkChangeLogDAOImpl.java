package com.workmarket.dao.changelog.work;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.changelog.work.WorkChangeLog;
import com.workmarket.domains.model.changelog.work.WorkChangeLogPagination;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WorkChangeLogDAOImpl extends AbstractDAO<WorkChangeLog> implements WorkChangeLogDAO {

	protected Class<WorkChangeLog> getEntityClass() {
		return WorkChangeLog.class;
	}

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@SuppressWarnings("unchecked")
	@Override
	public WorkChangeLogPagination findAllChangeLogByWorkId(Long workId, WorkChangeLogPagination pagination) {
		SQLBuilder builder =
			new SQLBuilder()
				.addTable("work_changelog")
				.addColumns("work.id workId", "work.title title", "user.last_name actorLastName",
						"user.first_name actorFirstName",  "user.user_number actorUserNumber",
						"user.email actorEmail",
						"work_changelog.actor_id actorId",
						"onbehalf.first_name onbehalfFirstName", "onbehalf.last_name onbehalfLastName", "work_changelog.created_on", "work_changelog.type",
						"work_changelog.id AS workChangeLogId")
				.addJoin("INNER JOIN work on work.id = work_changelog.work_id")
				.addJoin("INNER JOIN user on user.id = work_changelog.actor_id")
				.addJoin("LEFT JOIN user onbehalf ON onbehalf.id = work_changelog.on_behalf_of_actor_id");

		if (workId != null) {
			builder
				.addWhereClause("work_changelog.work_id = :workId")
				.addParam("workId", workId);
		}

		String sort = "work_changelog.created_on";
		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			builder.addDescOrderBy(sort);
		} else {
			builder.addAscOrderBy(sort);
		}

		if (pagination.hasFilter(WorkChangeLogPagination.FILTER_KEYS.TYPE)) {
			builder
				.addWhereClause("work_changelog.type = :type")
				.addParam("type", pagination.getFilter(WorkChangeLogPagination.FILTER_KEYS.TYPE));
		}

		List<WorkChangeLog> results = jdbcTemplate.query(builder.build(), builder.getParams(), new WorkChangeLogRowMapper());
		int count = jdbcTemplate.queryForObject(builder.buildCount("work_changelog.id"), builder.getParams(), Integer.class);

		pagination.setResults(results);
		pagination.setRowCount(count);

		return pagination;
	}

	private static final class WorkChangeLogRowMapper implements RowMapper<WorkChangeLog> {

		@Override
		public WorkChangeLog mapRow(ResultSet rs, int rowNum) throws SQLException {
			WorkChangeLog row = new WorkChangeLog();

			row.setId(rs.getLong("workChangeLogId"));
			row.setWorkId(rs.getLong("workId"));
			row.setWorkTitle(rs.getString("title"));
			row.setActorId(rs.getLong("actorId"));
			row.setActorFirstName(rs.getString("actorFirstName"));
			row.setActorLastName(rs.getString("actorLastName"));
			row.setActorUserNumber(rs.getString("actorUserNumber"));
			row.setActorEmail(rs.getString("actorEmail"));
			row.setOnBehalfOfActorFullName(StringUtilities.fullName(rs.getString("onbehalfFirstName"), rs.getString("onbehalfLastName")));
			row.setCreatedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("created_on")));
			row.setType(rs.getString("type"));
			return row;
		}
	}


}
