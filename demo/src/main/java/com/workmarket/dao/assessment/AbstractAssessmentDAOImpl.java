package com.workmarket.dao.assessment;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentPagination;
import com.workmarket.domains.model.assessment.AssessmentStatistics;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.domains.model.assessment.GradedAssessment;
import com.workmarket.domains.model.assessment.SurveyAssessment;
import com.workmarket.utility.sql.SQLBuilder;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Repository
public class AbstractAssessmentDAOImpl extends PaginationAbstractDAO<AbstractAssessment> implements AbstractAssessmentDAO {

	@Autowired @Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	@Autowired @Resource(name = "jdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	
	@Override
	protected Class<AbstractAssessment> getEntityClass() {
		return AbstractAssessment.class;
	}

	@Override
	public Map<Long, String> findSurveysByCompany(Long companyId) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String sql = "SELECT id, name FROM assessment WHERE type = :survey " +
				" AND company_id = :companyId " +
				" AND assessment_status_type_code = :active " +
				" ORDER BY name ";

		params.addValue("survey", AbstractAssessment.SURVEY_ASSESSMENT_TYPE)
				.addValue("companyId", companyId)
				.addValue("active", AssessmentStatusType.ACTIVE);

		Map<Long, String> result = Maps.newHashMap();
		List<Map<String, Object>> results = readOnlyJdbcTemplate.queryForList(sql, params);
		for (Map<String, Object> row : results) {
			result.put(((Integer)row.get("id")).longValue(), (String)row.get("name"));
		}
		return result;
	}

	@Override
	public AssessmentPagination findByCompany(final Long companyId, AssessmentPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		
		return (AssessmentPagination)super.paginationQuery(getEntityClassFromFilter(pagination), pagination, ImmutableMap.<String, Object>of("company.id", companyId));
	}

	@Override
	public List<Long> findAssessmentIdsByUser(final Long userId) {
		Assert.notNull(userId);
		String sql = "select id from assessment where user_id = :userId";
		return readOnlyJdbcTemplate.queryForList(sql, ImmutableMap.of("userId", userId), Long.class);
	}


	@Override
	public AbstractAssessment findAssessmentById(Long assessmentId) {
		return (AbstractAssessment) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("items", FetchMode.JOIN)
				.add(Restrictions.eq("id", assessmentId))
				.uniqueResult();
	}

	@Override
	public Map<Long, String> findAllAssessmentNamesToHydrateSearchData(Set<Long> assessmentIdsInResponse) {
		if (isEmpty(assessmentIdsInResponse)) {
			return emptyMap();
		}

		Query q = getFactory().getCurrentSession().createQuery("select a.id, a.name from assessment a where a.id in (:assessmentIds)");
		q.setParameterList("assessmentIds", assessmentIdsInResponse);

		@SuppressWarnings("unchecked")
		List<Object> results = q.list();

		Map<Long, String> returnVal = newHashMapWithExpectedSize(results.size());
		for (Object result : results) {
			Object[] row = (Object[]) result;
			returnVal.put((Long) row[0], (String) row[1]);
		}
		return returnVal;
	}

	@Override
	public Map<Long, String> findAllAssessmentNamesToHydrateSearchData() {

		Query q = getFactory().getCurrentSession().createQuery("select a.id, a.name from assessment a");

		@SuppressWarnings("unchecked")
		List<Object> results = q.list();

		Map<Long, String> returnVal = newHashMapWithExpectedSize(results.size());
		for (Object result : results) {
			Object[] row = (Object[]) result;
			returnVal.put((Long) row[0], (String) row[1]);
		}
		return returnVal;
	}

	@Override
	public Integer countAssessmentsByCompanyCreatedSince(long companyId, Calendar dateFrom) {
		return ((Long)getFactory().getCurrentSession().createCriteria(GradedAssessment.class)
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.ge("createdOn", dateFrom))
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public Integer countAssessmentsByCompany(long companyId) {
		//Only count tests (not surveys)
		return ((Long)getFactory().getCurrentSession().createCriteria(GradedAssessment.class)
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("assessmentStatusType.code", AssessmentStatusType.ACTIVE))
			.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public List<String> getActiveAssessmentForGroup(Long companyId, Long assessmentId) {
		SQLBuilder builder =
			new SQLBuilder()
				.addColumn("ug.name")
				.addTable("user_group ug")
				.addJoin("INNER JOIN user_group_requirement_set_association ugrs on ugrs.user_group_id = ug.id")
				.addJoin("INNER JOIN requirement_set rs on rs.id = ugrs.requirement_set_id")
				.addJoin("INNER JOIN requirement r on r.requirement_set_id = rs.id")
				.addJoin("INNER JOIN test_requirement tr on tr.id = r.id")
				.addJoin("INNER JOIN assessment a ON a.id = tr.test_id")
				.addWhereClause("ug.deleted = false")
				.addWhereClause("ug.active_flag = true")
				.addWhereClause("a.company_id", "=", "companyId", companyId)
				.addWhereClause("tr.test_id", "=", "assessmentId", assessmentId);

		return readOnlyJdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	public List<String> getActiveAssessmentForAssignment(Long companyId, Long assessmentId) {
		SQLBuilder builder = new SQLBuilder()
				.addColumn("w.work_number")
				.addTable("work_assessment_association waa")
				.addJoin("INNER JOIN assessment a ON a.id = waa.assessment_id ")
				.addJoin("INNER JOIN work w ON w.id = waa.work_id")
				.addWhereClause("w.work_status_type_code IN ('active', 'accepted','complete','draft','sent','paymentPending')")
				.addWhereClause("a.company_id", "=", "companyId", companyId)
				.addWhereClause("a.id", "=", "assessmentId", assessmentId);

		return readOnlyJdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	public List<String> getActiveAssessmentForReqSet(Long companyId, Long assessmentId) {
		SQLBuilder builder = new SQLBuilder()
				.addColumn("rs.name")
				.addTable("test_requirement tr")
				.addJoin("INNER JOIN assessment a ON a.id = tr.test_id")
				.addJoin("INNER JOIN requirement r ON r.id = tr.id")
				.addJoin("INNER JOIN requirement_set rs on r.requirement_set_id = rs.id")
				.addWhereClause("rs.active = 1")
				.addWhereClause("a.company_id", "=", "companyId", companyId)
				.addWhereClause("a.id", "=", "assessmentId", assessmentId);

		return readOnlyJdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	public AssessmentStatistics getAssessmentStatistics(Long assessmentId) {
		SQLBuilder invited = new SQLBuilder()
			.addColumn("COUNT(*)")
			.addTable("request r")
			.addJoin("INNER JOIN request_assessment_invitation i ON r.id = i.id")
			.addWhereClause("i.assessment_id", "=", "assessmentId", assessmentId);
		
		SQLBuilder passed = new SQLBuilder()
			.addColumn("COUNT(*)")
			.addTable("assessment_user_association")
			.addWhereClause("attempt_status_type_code IN ('graded','complete')")
			.addWhereClause("passed_flag = true")
			.addWhereClause("assessment_id", "=", "assessmentId", assessmentId);
		
		SQLBuilder failed = new SQLBuilder()
			.addColumn("COUNT(*)")
			.addTable("assessment_user_association")
			.addWhereClause("attempt_status_type_code IN ('graded','complete')")
			.addWhereClause("passed_flag = false")
			.addWhereClause("assessment_id", "=", "assessmentId", assessmentId);
		
		SQLBuilder average = new SQLBuilder()
			.addColumn("AVG(score)")
			.addTable("assessment_user_association")
			.addWhereClause("attempt_status_type_code IN ('graded','complete')")
			.addWhereClause("assessment_id", "=", "assessmentId", assessmentId);
		
		SQLBuilder query = new SQLBuilder()
			.addColumn("(" + invited.build() + ") AS invited")
			.addColumn("(" + passed.build() + ") AS passed")
			.addColumn("(" + failed.build() + ") AS failed")
			.addColumn("(" + average.build() + ") AS average")
			.addParam("assessmentId", assessmentId);
		
		RowMapper<AssessmentStatistics> mapper = new RowMapper<AssessmentStatistics>() {
			@Override
			public AssessmentStatistics mapRow(ResultSet rs, int rowNum) throws SQLException {
				AssessmentStatistics row = new AssessmentStatistics();
				row.setNumberOfInvited(rs.getInt("invited"));
				row.setNumberOfPassed(rs.getInt("passed"));
				row.setNumberOfFailed(rs.getInt("failed"));
				row.setAverageScore(rs.getDouble("average"));
				return row;
			}
		};
		
		return readOnlyJdbcTemplate.queryForObject(query.buildProjectionClause(), query.getParams(), mapper);
	}

	@Override
	public void applySorts(Pagination<AbstractAssessment> pagination, Criteria query, Criteria count) {
		if (pagination.getSortColumn() == null) { return; }

		String sort = "name";
		if (pagination.getSortColumn().equals(AssessmentPagination.SORTS.NAME.toString())) {
			sort = "name";
		} else if (pagination.getSortColumn().equals(AssessmentPagination.SORTS.CREATED_BY.toString())) {
			sort = "user.lastName";
		} else if (pagination.getSortColumn().equals(AssessmentPagination.SORTS.CREATED_ON.toString())) {
			sort = "createdOn";
		} else if (pagination.getSortColumn().equals(AssessmentPagination.SORTS.STATUS.toString())) {
			sort = "assessmentStatusType.code";
		}

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			query.addOrder(Order.desc(sort));
		} else {
			query.addOrder(Order.asc(sort));
		}
	}

	@Override
	public void applyFilters(Pagination<AbstractAssessment> pagination, Criteria query, Criteria count) {
		if (pagination.getFilters() == null) return;
		
		if (pagination.hasFilter(AssessmentPagination.FILTER_KEYS.STATUS)) {
			String status = pagination.getFilter(AssessmentPagination.FILTER_KEYS.STATUS);
			query.add(Restrictions.eq("assessmentStatusType.code", status));
			count.add(Restrictions.eq("assessmentStatusType.code", status));
		}
		
		if (pagination.hasFilter(AssessmentPagination.FILTER_KEYS.NOT_REMOVED)) {
			query.add(Restrictions.ne("assessmentStatusType.code", AssessmentStatusType.REMOVED));
			count.add(Restrictions.ne("assessmentStatusType.code", AssessmentStatusType.REMOVED));
		}
	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
		
		query
			.createAlias("user", "user")
			.setFetchMode("user", FetchMode.JOIN);
		
		for (Map.Entry<String,Object> e : params.entrySet()) {
			query.add(Restrictions.eq(e.getKey(), e.getValue()));
			count.add(Restrictions.eq(e.getKey(), e.getValue()));
		}
	}

	@Override
	public int updateAssessmentOwner(final Long newOwnerId, final List<Long> testIds) {
		Assert.notNull(newOwnerId);
		
		if (CollectionUtils.isEmpty(testIds)) { return 0; }
		
		String sql = "update assessment set user_id = :newOwnerId where id in (:testIds)";

		return jdbcTemplate.update(sql, ImmutableMap.of("newOwnerId", newOwnerId, "testIds", testIds));
	}
	
	private Class<? extends AbstractAssessment> getEntityClassFromFilter(Pagination<?> pagination) {
		if (pagination.getFilters() == null) {
			return getEntityClass();
		}
		
		if (pagination.hasFilter(AssessmentPagination.FILTER_KEYS.TYPE)) {
			if (pagination.getFilter(AssessmentPagination.FILTER_KEYS.TYPE).equals(AbstractAssessment.GRADED_ASSESSMENT_TYPE)) {
				return GradedAssessment.class;
			}
			if (pagination.getFilter(AssessmentPagination.FILTER_KEYS.TYPE).equals(AbstractAssessment.SURVEY_ASSESSMENT_TYPE)) {
				return SurveyAssessment.class;
			}
		}
		
		return getEntityClass();
	}
}
