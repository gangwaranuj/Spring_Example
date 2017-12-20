package com.workmarket.dao.summary.work;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.summary.work.WorkStatusTransitionHistorySummary;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * Author: rocio
 */
@Repository
public class WorkStatusTransitionHistorySummaryDAOImpl extends AbstractDAO<WorkStatusTransitionHistorySummary> implements WorkStatusTransitionHistorySummaryDAO {

	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<WorkStatusTransitionHistorySummary> getEntityClass() {
		return WorkStatusTransitionHistorySummary.class;
	}

	@Override
	public BigDecimal calculateAverageTransitionTimeByCompanyInSeconds(String fromWorkStatusType, String toWorkStatusType, long companyId, DateRange dateRange) {
		Assert.hasText(fromWorkStatusType);
		Assert.hasText(toWorkStatusType);
		SQLBuilder sql = new SQLBuilder();
		sql.addColumn("COALESCE(AVG(transition_time_in_seconds),0) value")
				.addTable("work_status_transition_history_summary")
				.addJoin("INNER JOIN time_dimension on time_dimension.id = work_status_transition_history_summary.date_id")
				.addWhereClause("from_work_status_type_code = :fromWorkStatusType")
				.addWhereClause("to_work_status_type_code = :toWorkStatusType")
				.addWhereClause("company_id = :companyId")
				.addParam("fromWorkStatusType", fromWorkStatusType)
				.addParam("toWorkStatusType", toWorkStatusType)
				.addParam("companyId", companyId);

		if (dateRange != null) {
			Assert.notNull(dateRange.getFrom());
			sql.addWhereClause("time_dimension.date >= :fromDate")
					.addParam("fromDate", dateRange.getFrom());
			if (dateRange.getThrough() != null) {
				sql.addWhereClause("time_dimension.date <= :toDate")
						.addParam("toDate", dateRange.getThrough());
			}
		}

		return jdbcTemplate.queryForObject(sql.build(), sql.getParams(), BigDecimal.class);
	}

	@Override
	public BigDecimal calculateAverageTimeToPayFromDueDateByCompanyInSeconds(long companyId, DateRange dateRange, boolean includeOverdueWork) {
		String sql;
		SQLBuilder sqlForPaidWork = buildAverageTimeToPayFromDueDateByCompanySqlBuilder(companyId, dateRange);
		if (includeOverdueWork) {
			SQLBuilder sqlForDueWork = buildAverageTimeToPayFromDueDateByCompanyForOverdueWorkSqlBuilder(companyId, dateRange);
			sqlForPaidWork.getParams().addValues(sqlForDueWork.getParams().getValues());
			sql = "SELECT AVG(seconds) FROM (" + sqlForPaidWork.build() + " UNION ALL " + sqlForDueWork.build() + ") allAssignments";
		} else {
			sql = "SELECT AVG(seconds) FROM (" + sqlForPaidWork.build() + ") allAssignments";
		}
		return jdbcTemplate.queryForObject(sql, sqlForPaidWork.getParams(), BigDecimal.class);
	}

	private SQLBuilder buildAverageTimeToPayFromDueDateByCompanySqlBuilder(long companyId, DateRange dateRange) {
		// @formatter:off
		/**
			SELECT		work.company_id,
						IF ((ABS(DATEDIFF(work_milestones.paid_on, work_milestones.due_on)) >= 30),
						(DATEDIFF(work_milestones.paid_on, work_milestones.due_on) * 24
							+ EXTRACT(HOUR FROM work_milestones.paid_on)
							- EXTRACT(HOUR FROM work_milestones.due_on)) * 3600, TIME_TO_SEC(TIMEDIFF(work_milestones.paid_on, work_milestones.due_on))) seconds
			FROM        work
			INNER		JOIN work_milestones ON work_milestones.work_id = work.id
			WHERE 		work_milestones.closed_on IS NOT NULL AND work_milestones.paid_on IS NOT NULL AND work_milestones.closed_on < work_milestones.paid_on
			AND			work_milestones.due_on IS NOT NULL
			AND 		work.work_status_type_code IN ('paid', 'cancelledWithPay')
			AND			work_milestones.due_on BETWEEN '2013-04-26' AND now()
			AND			work.company_id = 1;
		**/

		SQLBuilder sqlForPaidWork = new SQLBuilder();
		sqlForPaidWork.addColumn("work.company_id")
				.addColumn("IF ((ABS(DATEDIFF(work_milestones.paid_on, work_milestones.due_on)) >= 30),\n" +
						" (DATEDIFF(work_milestones.paid_on, work_milestones.due_on) * 24\n" +
						" + EXTRACT(HOUR FROM work_milestones.paid_on)\n" +
						" - EXTRACT(HOUR FROM work_milestones.due_on)) * 3600, TIME_TO_SEC(TIMEDIFF(work_milestones.paid_on, work_milestones.due_on))) seconds")
				.addTable("work")
				.addJoin("INNER JOIN work_milestones ON work_milestones.work_id = work.id")
				.addWhereClause(" work_milestones.closed_on IS NOT NULL AND work_milestones.paid_on IS NOT NULL AND work_milestones.closed_on <= work_milestones.paid_on")
				.addWhereClause(" work_milestones.due_on IS NOT NULL")
				.addWhereClause(" work.work_status_type_code IN ('paid', 'cancelledWithPay')")
				.addWhereClause(" work.company_id = :companyId")
				.addParam("companyId", companyId);

		if (dateRange != null) {
			Assert.notNull(dateRange.getFrom());
			sqlForPaidWork.addWhereClause("work_milestones.due_on >= :fromDate")
					.addParam("fromDate", dateRange.getFrom());

			if (dateRange.getThrough() != null) {
				sqlForPaidWork.addWhereClause("work_milestones.due_on <= :toDate")
						.addParam("toDate", dateRange.getThrough());
			}
		}
		return sqlForPaidWork;
	}

	private SQLBuilder buildAverageTimeToPayFromDueDateByCompanyForOverdueWorkSqlBuilder(long companyId, DateRange dateRange) {
		// @formatter:off
		/**
		 * 	SELECT		work.company_id,
		 	IF 			IF ((DATEDIFF(now(), work_milestones.due_on) >= 30),
		 				(DATEDIFF(now(), work_milestones.due_on) * 24
		 					+ EXTRACT(HOUR FROM now())
		 					- EXTRACT(HOUR FROM work_milestones.due_on)) * 3600, TIME_TO_SEC(TIMEDIFF(now(), work_milestones.due_on))) seconds
		 	FROM		work
		 	INNER		JOIN work_milestones ON work_milestones.work_id = work.id
		 	WHERE		work_milestones.closed_on IS NOT NULL  AND work_milestones.due_on < now()
		 	AND			work.work_status_type_code IN ('closed', 'paymentPending', 'cancelledPayPending')
		 	AND			work_milestones.due_on BETWEEN '2013-04-26' AND now()
		 	AND			work.company_id = 1;
		 */

		SQLBuilder sqlForDueWork = new SQLBuilder();
		sqlForDueWork.addColumn("work.company_id")
				.addColumn("IF ((DATEDIFF(:now, work_milestones.due_on) >= 30),\n" +
						" (DATEDIFF(:now, work_milestones.due_on) * 24\n" +
						" 	+ EXTRACT(HOUR FROM :now)\n" +
						"	- EXTRACT(HOUR FROM work_milestones.due_on)) * 3600, TIME_TO_SEC(TIMEDIFF(:now, work_milestones.due_on))) seconds")
				.addTable("work")
				.addJoin("INNER JOIN work_milestones ON work_milestones.work_id = work.id")
				.addWhereClause(" work_milestones.due_on IS NOT NULL")
				.addWhereClause(" work_milestones.closed_on IS NOT NULL  AND work_milestones.due_on < :now")
				.addWhereClause(" work.work_status_type_code IN ('closed', 'paymentPending', 'cancelledPayPending')")
				.addWhereClause(" work.company_id = :companyId")
				.addParam("now", DateUtilities.formatTodayForSQL())
				.addParam("companyId", companyId);

		if (dateRange != null) {
			Assert.notNull(dateRange.getFrom());
			sqlForDueWork.addWhereClause("work_milestones.due_on >= :fromDate");

			if (dateRange.getThrough() != null) {
				sqlForDueWork.addWhereClause("work_milestones.due_on <= :toDate");
			}
		}
		return sqlForDueWork;
	}
}
