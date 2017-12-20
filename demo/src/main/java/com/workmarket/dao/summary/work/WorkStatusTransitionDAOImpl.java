package com.workmarket.dao.summary.work;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.data.report.internal.BuyerSummary;
import com.workmarket.domains.model.summary.work.WorkStatusTransition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

@Repository
public class WorkStatusTransitionDAOImpl extends AbstractDAO<WorkStatusTransition> implements WorkStatusTransitionDAO {
	
	private static final Log logger = LogFactory.getLog(WorkStatusTransitionDAOImpl.class);

	private final static String WORK_STATUS_TRANSITION_SQL = "SELECT COALESCE(COUNT(work_id), 0) " +
			" FROM 	work_status_transition " +
			" INNER JOIN time_dimension ON time_dimension.id = work_status_transition.date_id " +
			" WHERE work_status_transition.work_status_type_code = :workStatusTypeCode " +
			" AND 	time_dimension.date >= :start AND time_dimension.date <= :end ";

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<WorkStatusTransition> getEntityClass() {
		return WorkStatusTransition.class;
	}

	@Override
	public WorkStatusTransition findWorkStatusTransition(Long workId, String workStatusTypeCode) {
		final Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("transitionId.workId", workId))
				.add(Restrictions.eq("transitionId.workStatusTypeCode", workStatusTypeCode));
		return	(WorkStatusTransition)criteria.uniqueResult();
	}

	@Override
	public List<WorkStatusTransition> findAllTransitionsByWork(Long workId) {
		final Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("transitionId.workId", workId));
		return	criteria.list();
	}

	@Override
	public void deleteWorkStatusTransition(Long workId, String workStatusTypeCode) {
		final Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("transitionId.workId", workId))
				.add(Restrictions.eq("transitionId.workStatusTypeCode", workStatusTypeCode));
		WorkStatusTransition transition = (WorkStatusTransition)criteria.uniqueResult();
		if (transition != null) {
			delete(transition);
		}
	}

	@Override
	public Integer countWorkStatusTransitions(String workStatusTypeCode, Calendar start, Calendar end) {
		return jdbcTemplate.queryForObject(WORK_STATUS_TRANSITION_SQL, getWorkStatusTransitionParameterMap(workStatusTypeCode, start, end), Integer.class);
	}

	@Override
	public Integer countUniqueCompaniesWithWorkStatusTransitions(String workStatusTypeCode, Calendar start, Calendar end) {
		String query = "SELECT COALESCE(COUNT(DISTINCT work_status_transition.company_id), 0) " +
				" FROM 	work_status_transition " +
				" INNER JOIN time_dimension ON time_dimension.id = work_status_transition.date_id " +
				" WHERE work_status_transition.work_status_type_code = :workStatusTypeCode " +
				" AND 	time_dimension.date >= :start AND time_dimension.date <= :end ";

		return jdbcTemplate.queryForObject(query, getWorkStatusTransitionParameterMap(workStatusTypeCode, start, end), Integer.class);
	}

	@Override
	public List<BuyerSummary> findUniqueBuyersSummary(Calendar start, Calendar end) {
		String sql = "SELECT company.id AS companyId, company.effective_name AS companyName, \n" +
				"SUM(CASE WHEN transition.work_status_type_code = 'sent' THEN theCount ELSE 0 END) AS routed,\n" +
				"SUM(CASE WHEN transition.work_status_type_code = 'draft' THEN theCount ELSE 0 END) AS created,\n" +
				"SUM(CASE WHEN transition.work_status_type_code = 'active' THEN theCount ELSE 0 END) AS active, \n" +
				"SUM(CASE WHEN transition.work_status_type_code = 'void' THEN theCount ELSE 0 END) AS void, \n" +
				"SUM(CASE WHEN transition.work_status_type_code = 'cancelled' THEN theCount ELSE 0 END) AS cancelled, \n" +
				"SUM(CASE WHEN transition.work_status_type_code = 'closed' THEN theCount ELSE 0 END) AS closed, \n" +
				"SUM(CASE WHEN transition.work_status_type_code = 'paymentPending' THEN theCount ELSE 0 END) AS paymentPending, \n" +
				"SUM(CASE WHEN transition.work_status_type_code = 'draft' THEN average ELSE 0 END) AS averageWorkPrice \n" +
				"FROM 	company INNER JOIN (" +

				"SELECT	COUNT(work_id) theCount, work_status_transition.work_status_type_code, company_id, \n" +
				"COALESCE(AVG(work_status_transition.work_price),0) AS average \n" +
				"FROM 	work_status_transition \n" +
				"INNER 	JOIN time_dimension ON time_dimension.id = work_status_transition.date_id \n" +
				"WHERE 	work_status_transition.work_status_type_code IN ('sent', 'draft', 'active', 'void', 'cancelled', 'closed', 'paymentPending')\n" +
				"AND 	time_dimension.date >= :start AND time_dimension.date <= :end \n" +
				"GROUP 	BY work_status_transition.work_status_type_code, company_id \n" +

				") transition ON transition.company_id = company.id \n" +
				"GROUP 	BY transition.company_id ORDER BY company.effective_name ASC ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("end", end);
		params.addValue("start", start);

		return this.jdbcTemplate.query(sql, params, new RowMapper(){

			public BuyerSummary mapRow(ResultSet rs, int rowNum) throws SQLException {

				BuyerSummary row = new BuyerSummary();
				row.setCompanyId(rs.getLong("companyId"));
				row.setCompanyName(rs.getString("companyName"));
				row.setActiveAssignments(rs.getInt("active"));
				row.setCreatedAssignments(rs.getInt("created"));
				row.setRoutedAssignments(rs.getInt("routed"));
				row.setAverageAssignmentPrice(rs.getBigDecimal("averageWorkPrice"));
				row.setCancelledAssignments(rs.getInt("cancelled"));
				row.setVoidAssignments(rs.getInt("void"));
				Integer closed = rs.getInt("closed");
				Integer paymentPending = rs.getInt("paymentPending");
				row.setClosedAssignments(closed + paymentPending);
				return row;
			}

		});
	}

	@Override
	public BigDecimal calculatePotentialRevenueByWorkStatusType(String workStatusTypeCode, Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(SUM(work_price), 0) AS total \n" +
				"FROM 	work_status_transition \n" +
				"INNER 	JOIN time_dimension ON time_dimension.id = work_status_transition.date_id \n" +
				"WHERE 	work_status_transition.work_status_type_code = :workStatusTypeCode " +
				"AND 	time_dimension.date >= :start AND time_dimension.date <= :end ";

		return jdbcTemplate.queryForObject(sql, getWorkStatusTransitionParameterMap(workStatusTypeCode, start, end), BigDecimal.class);
	}


	@Override
	public BigDecimal calculateAveragePriceByWorkStatusType(String workStatusTypeCode, Calendar start, Calendar end) {
		String sql = "SELECT COALESCE(AVG(work_price), 0) AS total \n" +
				"FROM 	work_status_transition \n" +
				"INNER 	JOIN time_dimension ON time_dimension.id = work_status_transition.date_id \n" +
				"WHERE 	work_status_transition.work_status_type_code = :workStatusTypeCode " +
				"AND 	time_dimension.date >= :start AND time_dimension.date <= :end ";

		return jdbcTemplate.queryForObject(sql, getWorkStatusTransitionParameterMap(workStatusTypeCode, start, end), BigDecimal.class);
	}

	private MapSqlParameterSource getWorkStatusTransitionParameterMap(String workStatusTypeCode, Calendar start, Calendar end) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("start", start);
		params.addValue("end", end);
		params.addValue("workStatusTypeCode", workStatusTypeCode);
		return params;
	}

	@Override
	public List<Object[]> findRoutedAssignmentsPerCompany(Calendar start, Calendar end) {
		String query = "SELECT company.id AS companyId, company.effective_name,  \n" +
				"COALESCE(SUM(work_status_transition.work_price),0) AS total \n" +
				"FROM 	work_status_transition \n" +
				"INNER 	JOIN time_dimension ON time_dimension.id = work_status_transition.date_id \n" +
				"INNER 	JOIN company on company.id = work_status_transition.company_id \n" +
				"WHERE 	work_status_transition.work_status_type_code = :workStatusTypeCode \n" +
				"AND 	time_dimension.date >= :start AND time_dimension.date <= :end \n" +
				"GROUP 	BY company_id ORDER BY company.effective_name ASC";

		return this.jdbcTemplate.query(query, getWorkStatusTransitionParameterMap(WorkStatusType.SENT, start, end), new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Object[] row = new Object[3];
				row[0] = rs.getString("companyId");
				row[1] = rs.getString("effective_name");
				row[2] = rs.getBigDecimal("total");
				return row;
			}
		});
	}
}