package com.workmarket.dao.assessment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.data.report.assessment.AttemptReportPagination;
import com.workmarket.data.report.assessment.AttemptReportRow;
import com.workmarket.data.report.assessment.AttemptResponseReportRow;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;

import javax.annotation.Resource;

@Repository
public class AttemptReportDAOImpl extends AbstractDAO<AttemptReportRow> implements AttemptReportDAO {
	private static final Log logger = LogFactory.getLog(AttemptReportDAOImpl.class);

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<AttemptReportRow> getEntityClass() {
		return AttemptReportRow.class;
	}

	@Override
	public AttemptReportPagination generateReportForAssessment(Long assessmentId, AttemptReportPagination pagination) {

		// Get list of items for this assessment

		SQLBuilder itemBuilder = new SQLBuilder()
			.addColumns(new String[] {"id", "prompt"})
			.addTable("assessment_item")
			.addWhereClause("assessment_id", "=", "assessmentId", assessmentId);

		final List<Map<String,Object>> items = jdbcTemplate.queryForList(itemBuilder.build(), itemBuilder.getParams());

		// Build response table

		SQLBuilder responseBuilderTaken = new SQLBuilder()
			.addColumns("user.user_number", "user.first_name", "user.last_name", "company.effective_name")
			.addColumns("attempt.id AS assessment_attempt_id", "attempt.completed_on", "attempt.passed_flag", "attempt.score", "attempt.attempt_status_type_code")
			.addColumns("work.id AS workId", "work.title AS workTitle", "work.work_number")
			.addTable("assessment_user_association association")
			.addJoin("INNER JOIN assessment_attempt attempt ON attempt.assessment_user_association_id = association.id")
			.addJoin("LEFT JOIN assessment_attempt_response response ON response.assessment_attempt_id = attempt.id AND response.deleted = false")
			.addJoin("LEFT JOIN assessment_item item ON item.id = response.assessment_item_id and item.deleted = false")
			.addJoin("INNER JOIN user ON user.id = association.user_id")
			.addJoin("INNER JOIN company ON company.id = user.company_id")
			.addJoin("LEFT JOIN assessment_item_choice choice ON choice.id = response.assessment_item_choice_id AND choice.deleted = false")
			.addJoin("LEFT JOIN work ON attempt.work_id = work.id")
			.addGroupColumns("response.assessment_item_id", "response.assessment_attempt_id")
			.addParam("assessmentId", assessmentId)
			.addWhereClause("association.assessment_id = :assessmentId");

		for (Map<String,Object> i : items) {
			responseBuilderTaken.addColumn(String.format("IF(response.assessment_item_id = %d, GROUP_CONCAT(IF(response.value IS NOT NULL AND response.value != '', response.value, choice.value) SEPARATOR ', '), NULL) AS item_%s", i.get("id"), i.get("id")));
		}

		SQLBuilder responseBuilderInvited = new SQLBuilder()
			.addColumns("user.user_number", "user.first_name", "user.last_name", "company.effective_name")
			.addColumns("null AS assessment_attempt_id", "null AS completed_on", "false AS passed_flag", "null AS score", "'invited' AS attempt_status_type_code")
			.addColumns("null AS workId", "null AS workTitle", "null AS work_number")
			.addTable("request_assessment_invitation invitation")
			.addJoin("INNER JOIN request on invitation.id = request.id")
			.addJoin("INNER JOIN user ON request.invitee_user_id = user.id")
			.addJoin("INNER JOIN company ON user.company_id = company.id")
			.addWhereClause("(NOT EXISTS(select aua.id FROM assessment_user_association aua where aua.assessment_id = :assessmentId AND aua.user_id = user.id)) AND invitation.assessment_id = :assessmentId")
			.addParam("assessmentId", assessmentId);

		for (Map<String,Object> i : items) {
			responseBuilderInvited.addColumn("null AS item_" + i.get("id"));
		}

		// Build pivot table

		SQLBuilder pivotBuilder = new SQLBuilder()
			.addColumns("user_number", "first_name", "last_name", "effective_name AS company_name")
			.addColumns("assessment_attempt_id", "completed_on", "passed_flag", "score", "attempt_status_type_code")
			.addColumns("workId", "workTitle", "work_number")
			.addTable("(" + responseBuilderTaken.build() + " UNION " + responseBuilderInvited.build() + " ORDER BY NULL) AS pivot")
			.addGroupColumns("IFNULL(assessment_attempt_id, user_number)");

		for (Map<String,Object> i : items) {
			pivotBuilder.addColumn("MAX(item_" + i.get("id") + ") AS item_" + i.get("id"));
		}

		pivotBuilder.addOrderBy("completed_on", "DESC");
		pivotBuilder.addLimitClause(pagination.getStartRow(), pagination.getResultsLimit(), pagination.isLimitMaxRows());

		// Query!
		logger.debug(pivotBuilder.build());

		List<AttemptReportRow> rows = jdbcTemplate.query(pivotBuilder.build(), responseBuilderTaken.getParams(), new RowMapper<AttemptReportRow>() {
			@Override
			public AttemptReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
				AttemptReportRow row = new AttemptReportRow();
				row.setUserNumber(rs.getString("user_number"));
				row.setFirstName(rs.getString("first_name"));
				row.setLastName(rs.getString("last_name"));
				row.setCompanyName(rs.getString("company_name"));
				row.setAttemptId(rs.getLong("assessment_attempt_id"));
				row.setCompletedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("completed_on")));
				row.setPassedFlag(rs.getBoolean("passed_flag"));
				row.setScore(rs.getBigDecimal("score"));
				row.setWorkId(rs.getLong("workId"));
				row.setWorkTitle(rs.getString("workTitle"));
				row.setWorkNumber(rs.getString("work_number"));
				row.setStatus(rs.getString("attempt_status_type_code"));
				for (Map<String,Object> i : items) {
					AttemptResponseReportRow r = new AttemptResponseReportRow();
					r.setItemId((Integer)i.get("id"));
					r.setItemPrompt((String)i.get("prompt"));
					r.setResponseValue(rs.getString("item_" + i.get("id")));
					row.getResponses().add(r);
				}
				return row;
			}

		});

		pagination.setResults(rows);
		pagination.setRowCount(rows.size());

		for (Map<String,Object> i : items) {
			pagination.getColumnNames().add((String)i.get("prompt"));
		}

		return pagination;
	}
}
