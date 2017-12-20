package com.workmarket.dao.report.internal;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.reporting.DailySummary;
import com.workmarket.domains.model.reporting.DailySummaryPagination;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Repository
public class DailySummaryDAOImpl extends AbstractDAO<DailySummary> implements DailySummaryDAO {

	@Qualifier("jdbcTemplate") @Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<DailySummary> getEntityClass() {
		return DailySummary.class;
	}

	private Map<String, Calendar> returnStartAndEndParameters(Calendar start, Calendar end) {
		Map<String, Calendar> param = new HashMap<>();
		param.put("start", start);
		param.put("end", end);
		return param;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<DailySummary> findAllSummaries() {
		String query = "from daily_summary order by createdOn desc";
		return (List<DailySummary>) getFactory().getCurrentSession().createQuery(query).list();
	}

	@Override
	public Integer countNewUsers(Calendar start, Calendar end) {

		String query = "select coalesce(count(distinct company_id), 0) from user " +
				" where user.created_on > :start and user.created_on < :end";

		return jdbcTemplate.queryForObject(query, returnStartAndEndParameters(start, end), Integer.class);
	}

	@Override
	public Integer countDrugTests(Calendar start, Calendar end) {

		String query = "select coalesce(count(id), 0) from screening " +
				" where screening.request_date > :start and screening.request_date < :end and type = 'drug'";

		return jdbcTemplate.queryForObject(query, returnStartAndEndParameters(start, end), Integer.class);
	}

	@Override
	public Integer countBackgroundChecks(Calendar start, Calendar end) {

		String query = "select coalesce(count(id), 0) from screening " +
				" where screening.request_date > :start and screening.request_date < :end and type = 'background'";

		return jdbcTemplate.queryForObject(query, returnStartAndEndParameters(start, end), Integer.class);
	}

	@Override
	public Integer countPublicGroups() {

		String query = "select coalesce(count(id), 0) from user_group " +
				" where open_membership = true and deleted = false and active_flag = true and searchable = true";

		return jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Integer.class);
	}

	@Override
	public Integer countInviteOnlyGroups() {

		String query = "select coalesce(count(id), 0) from user_group " +
				" where open_membership = true and deleted = false and active_flag = true and searchable = false";

		return jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Integer.class);
	}

	@Override
	public Integer countPrivateGroups() {

		String query = "select coalesce(count(id), 0) from user_group " +
				" where open_membership = false and deleted = false and active_flag = true";

		return jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Integer.class);

	}

	@Override
	public Integer countInvitations(Calendar start, Calendar end) {

		String query = "select coalesce(count(id), 0) from invitation " +
				" where invitation_date > :start and invitation_date < :end";

		return jdbcTemplate.queryForObject(query, returnStartAndEndParameters(start, end), Integer.class);
	}

	@Override
	public Integer countNewBuyers(Calendar start, Calendar end) {
		String query = "SELECT COALESCE(COUNT(id), 0) " +
				" FROM 	company " +
				" WHERE first_created_assignment_on > :start " +
				" AND 	first_created_assignment_on < :end";

		return jdbcTemplate.queryForObject(query, returnStartAndEndParameters(start, end), Integer.class);
	}

	@Override
	public BigDecimal calculateTermsExpired() {
		String sql = " SELECT COALESCE(SUM(amount),0) expired_terms " +
		" FROM 		register_transaction rt " +
		" INNER 	JOIN work on work.id = rt.work_id " +
		" WHERE 	pending_flag = 'Y' " +
		" AND 		register_transaction_type_code = 'pytrmscmmt' " +
		" AND 		work.work_status_type_code = 'sent' " +
		" AND 		GREATEST(work.schedule_from, work.schedule_through) < DATE(now())";
		return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource() , BigDecimal.class);
	}

	@Override
	public BigDecimal calculateTermsOverdue() {
		String sql = " SELECT COALESCE(SUM(amount),0) pastDue_terms " +
				" FROM 		register_transaction rt " +
				" INNER 	JOIN work on work.id = rt.work_id " +
				" WHERE 	pending_flag = 'Y' " +
				" AND 		register_transaction_type_code = 'pytrmscmmt' " +
				" AND 		work.due_on IS NOT NULL " +
				" AND 		work.due_on < DATE(now())";
		return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource() , BigDecimal.class);
	}

	@Override
	public Integer countCampaigns(Calendar start, Calendar end) {

		String query = "select coalesce(count(id), 0) from recruiting_campaign " +
				" where created_on > :start and created_on < :end";

		return jdbcTemplate.queryForObject(query, returnStartAndEndParameters(start, end), Integer.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public DailySummaryPagination findAllSummaries(DailySummaryPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFirstResult(pagination.getStartRow());

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setProjection(Projections.rowCount());

		criteria.addOrder(Order.desc("createdOn"));

		if (null != pagination.getFromDate()){
			criteria.add(Restrictions.ge("createdOn", pagination.getFromDate()));
		}

		if (null != pagination.getToDate()){
			// add + 1 to the day to make it inclusive
			pagination.getToDate().add(Calendar.DAY_OF_YEAR, 1);
			criteria.add(Restrictions.le("createdOn", pagination.getToDate()));
		}

		pagination.setResults(criteria.list());
		int rowCount = 0;
		if (isNotEmpty(count.list())) {
			rowCount = ((Long) count.list().get(0)).intValue();
		}
		pagination.setRowCount(rowCount);
		return pagination;
	}
}
