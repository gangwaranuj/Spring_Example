package com.workmarket.dao.summary.work;

import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.summary.work.WorkHistorySummary;
import com.workmarket.utility.sql.SQLBuilder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

@Repository
public class WorkHistorySummaryDAOImpl extends AbstractDAO<WorkHistorySummary> implements WorkHistorySummaryDAO {

	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<WorkHistorySummary> getEntityClass() {
		return WorkHistorySummary.class;
	}

	@Override
	public Integer countWork(long companyId, String workStatusTypeCode, DateRange dateFilter) {
		Assert.hasText(workStatusTypeCode);

		SQLBuilder sql = new SQLBuilder();
		sql.addColumn("COALESCE(COUNT(DISTINCT work_history_summary.work_id),0) AS count")
				.addTable("work_history_summary")
				.addJoin("INNER JOIN time_dimension ON time_dimension.id = work_history_summary.date_id ")
				.addWhereClause("work_status_type_code = :workStatusTypeCode ")
				.addWhereClause("work_history_summary.company_id = :companyId ")
				.addParam("workStatusTypeCode", workStatusTypeCode)
				.addParam("companyId", companyId);

		if (dateFilter != null) {
			Assert.notNull(dateFilter.getFrom());
			Assert.notNull(dateFilter.getThrough());
			sql.addWhereClause("time_dimension.date BETWEEN :fromDate AND :toDate")
				.addParam("fromDate", dateFilter.getFrom())
				.addParam("toDate", dateFilter.getThrough());
		}
		return jdbcTemplate.queryForObject(sql.build(), sql.getParams(), Integer.class);
	}

	@Override
	public Integer countWorkWithLatePayment(long companyId, DateRange dateFilter) {
		String sql = "SELECT COALESCE(COUNT(DISTINCT work_history_summary.work_id),0) " +
				" FROM 	work_history_summary \n" +
				" INNER JOIN work_milestones ON work_milestones.work_id = work_history_summary.work_id " +
				" INNER JOIN time_dimension ON time_dimension.id = work_history_summary.date_id " +
				" WHERE	work_status_type_code = :workStatusTypeCode \n" +
				" AND 	work_history_summary.company_id = :companyId " +
				" AND 	time_dimension.date BETWEEN :fromDate AND :toDate " +
				" AND 	work_milestones.late_payment = true";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fromDate", dateFilter.getFrom());
		params.addValue("toDate", dateFilter.getThrough());
		params.addValue("workStatusTypeCode", WorkStatusType.PAID);
		params.addValue("companyId", companyId);

		return jdbcTemplate.queryForObject(sql, params, Integer.class);
	}

	@Override
	public Map<Long, Integer> countWorkForCompany(List<Long> workResourceUserIds, long companyId, String workStatusTypeCode) {
		if (isEmpty(workResourceUserIds) || isBlank(workStatusTypeCode)) return Collections.EMPTY_MAP;
		String sql = "SELECT COALESCE(COUNT(DISTINCT work_id),0) AS work, active_resource_user_id " +
				" FROM 	work_history_summary \n" +
				" WHERE	work_status_type_code = :workStatusTypeCode \n" +
				" AND	work_history_summary.active_resource_user_id IN (:userIds) \n" +
				" AND 	company_id = :companyId " +
				" GROUP BY active_resource_user_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userIds", workResourceUserIds);
		params.addValue("workStatusTypeCode", workStatusTypeCode);
		params.addValue("companyId", companyId);

		Map<Long, Integer> result = Maps.newLinkedHashMap();
		List<Map<String, Object>> transactionMap = jdbcTemplate.queryForList(sql, params);
		for (Map<String, Object> row : transactionMap) {
			result.put( (Long) row.get("active_resource_user_id"), (((Long) row.get("work"))).intValue());
		}
		return result;
	}

	@Override
	public Map<Long, Integer> countRepeatedClientsByUser(Calendar fromDate, List<Long> userIds) {
		if (isEmpty(userIds) || fromDate == null) return Collections.EMPTY_MAP;
		String sql = "SELECT COALESCE(COUNT(company_id),0) count, active_resource_user_id  "+
				" FROM 		( \n" +
				" SELECT 	active_resource_user_id, company_id, COUNT(work_id) times \n" +
				" FROM 		work_history_summary \n" +
				" INNER 	JOIN time_dimension ON time_dimension.id = work_history_summary.date_id \n" +
				" WHERE 	work_history_summary.work_status_type_code = :paid \n" +
				" AND 		time_dimension.date > :fromDate \n" +
				" AND 		active_resource_user_id IN (:userIds) \n " +
				" GROUP 	BY active_resource_user_id, company_id HAVING times > 1) repeated \n" +
				" GROUP 	BY active_resource_user_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("paid", WorkStatusType.PAID);
		params.addValue("fromDate", fromDate);
		params.addValue("userIds", userIds);

		Map<Long, Integer> result = Maps.newLinkedHashMap();
		List<Map<String, Object>> transactionMap = jdbcTemplate.queryForList(sql, params);
		for (Map<String, Object> row : transactionMap) {
			result.put( (Long) row.get("active_resource_user_id"), (((Long) row.get("count"))).intValue());
		}
		return result;
	}

}