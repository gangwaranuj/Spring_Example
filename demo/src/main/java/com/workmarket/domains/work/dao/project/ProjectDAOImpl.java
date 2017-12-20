package com.workmarket.domains.work.dao.project;

import com.workmarket.dto.SuggestionDTO;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectPagination;
import com.workmarket.utility.HibernateUtilities;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class ProjectDAOImpl extends AbstractDAO<Project> implements ProjectDAO {

	private static final Log logger = LogFactory.getLog(ProjectDAOImpl.class);

	@Qualifier("jdbcTemplate") @Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<Project> getEntityClass() {
		return Project.class;
	}

	public ProjectPagination findByCompany(Long companyId, ProjectPagination pagination) {
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("clientCompany", "clientCompany", Criteria.INNER_JOIN)
			  .createAlias("owner", "owner", Criteria.INNER_JOIN);

		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);
		HibernateUtilities.addRestrictionsEq(count, "company.id", companyId, "deleted", Boolean.FALSE);
		HibernateUtilities.addRestrictionsEq(criteria, "company.id", companyId, "deleted", Boolean.FALSE);

		if (pagination.getSortColumn() != null) {
			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				criteria.addOrder(Order.desc(ProjectPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
			} else {
				criteria.addOrder(Order.asc(ProjectPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
			}
		} else {
			criteria.addOrder(Order.desc("dueDate"));
			criteria.addOrder(Order.asc("name"));
		}

		pagination.setRowCount(HibernateUtilities.getRowCount(count));
		pagination.setResults(criteria.list());

		return pagination;
	}

	public ProjectPagination findReservedFundsEnabledProjectByCompany(Long companyId, ProjectPagination pagination) {
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("clientCompany", "clientCompany", Criteria.INNER_JOIN);

		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);
		HibernateUtilities.addRestrictionsEq(count, "company.id", companyId, "deleted", Boolean.FALSE);
		HibernateUtilities.addRestrictionsEq(criteria, "company.id", companyId, "deleted", Boolean.FALSE);

		if (pagination.getSortColumn() != null) {
			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				criteria.addOrder(Order.desc(ProjectPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
			} else {
				criteria.addOrder(Order.asc(ProjectPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
			}
		} else {
			criteria.addOrder(Order.desc("dueDate"));
			criteria.addOrder(Order.asc("name"));
		}

		if (pagination.hasFilter(ProjectPagination.FILTER_KEYS.RESERVED_FUNDS_ENABLED)) {
			boolean isReservedFundsEnabled = BooleanUtils.toBoolean(pagination.getFilter(ProjectPagination.FILTER_KEYS.RESERVED_FUNDS_ENABLED));
			HibernateUtilities.addRestrictionsEq(criteria, "reservedFundsEnabled", isReservedFundsEnabled);
			HibernateUtilities.addRestrictionsEq(count, "reservedFundsEnabled", isReservedFundsEnabled);
		}

		pagination.setRowCount(HibernateUtilities.getRowCount(count));
		pagination.setResults(criteria.list());

		return pagination;
	}

	@Override
	public Project findById(Long projectId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("clientCompany", FetchMode.JOIN)
				.add(Restrictions.eq("id", projectId));

		return (Project) criteria.uniqueResult();
	}

	@Override
	public Project findByNameAndCompanyId(String name, Long companyId) {
		return (Project) getFactory().getCurrentSession()
				.createQuery("select p from project p where name = :name and p.company.id = :companyId")
				.setParameter("name", name)
				.setParameter("companyId", companyId)
				.uniqueResult();
	}

	@Override
	public Project findByNameCompanyAndClient(String name, Long companyId, Long clientId) {
		return (Project) getFactory().getCurrentSession()
				.createQuery("select p from project p where name = :name and p.company.id = :companyId and p.clientCompany.id = :clientId")
				.setParameter("name", name)
				.setParameter("companyId", companyId)
				.setParameter("clientId", clientId)
				.uniqueResult();
	}

	@Override
	public Project findByWorkId(Long workId) {
		Project project = (Project) getFactory().getCurrentSession().createCriteria(AbstractWork.class)
				.add(Restrictions.eq("id", workId))
				.createAlias("project", "project", Criteria.LEFT_JOIN)
				.setFetchMode("project.clientCompany", FetchMode.JOIN)
				.setProjection(Projections.property("project"))
				.uniqueResult();

		if (project != null) {
			Hibernate.initialize(project.getClientCompany());
		}

		return project;
	}

	@Override
	public ProjectPagination findAllProjectsForClientCompany(Long companyId, Long clientCompanyId, ProjectPagination pagination) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(Project.class)
				.createAlias("clientCompany", "cc")
				.add(Restrictions.eq("cc.id", clientCompanyId))
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("deleted", Boolean.FALSE));
		Criteria count = getFactory().getCurrentSession().createCriteria(Project.class)
				.createAlias("clientCompany", "cc")
				.add(Restrictions.eq("cc.id", clientCompanyId))
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("deleted", Boolean.FALSE));

		if (pagination.hasFilter(ProjectPagination.FILTER_KEYS.ACTIVE)) {
			boolean isActive = BooleanUtils.toBoolean(pagination.getFilter(ProjectPagination.FILTER_KEYS.ACTIVE));
			criteria.add(Restrictions.eq("active", isActive));
			count.add(Restrictions.eq("active", isActive));
		}

		// count
		Long rowCount = HibernateUtilities.getRowCount(count);

		// sorts
		criteria.addOrder(Order.asc("name"));

		// pagination
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@Override
	public boolean doesProjectHaveImmediatePaymentWorkInProgress(Long projectId) {
		final String sql =
				" SELECT 	    count(*) "
				+ " FROM 		    project p "
				+ " INNER JOIN  project_work_association pwa "
				+ " ON 		      pwa.project_id = p.id "
				+ " INNER JOIN  work w"
				+ " ON          pwa.work_id = w.id"
				+ " WHERE		    w.payment_terms_enabled = 0 "
				+ " AND         w.work_status_type_code IN ('sent', 'active', 'complete')"
				+ " AND         w.type = 'W'"
				+ " AND         w.deleted = false"
				+ " AND         p.id = :projectId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("projectId", projectId);
		logger.debug(sql);

		return (jdbcTemplate.queryForObject(sql, params, Integer.class) > 0);
	}

	@Override
	public void resetAllProjectBudget(Long companyId) {
		getFactory().getCurrentSession()
				.createQuery("update project set budget_enabled_flag = 0, budget = 0.00, remaining_budget = 0.00 where company_id = :companyId")
				.setParameter("companyId", companyId)
				.executeUpdate();
	}

	@Override
	public List<SuggestionDTO> suggest(String prefix, Long userId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumns(
				"DISTINCT p.id AS id",
				"p.name AS value"
			)
			.addTable("project p")
			.addTable("user u")
			.addJoin("JOIN p.company c")
			.addWhereClause("u.company = c and u.id = :userId")
			.addWhereClause("LOWER(p.name) LIKE :prefix")
			.addWhereClause("p.deleted = false")
			.addAscOrderBy("p.name");

		Query query = getFactory().getCurrentSession().createQuery(builder.build());
		query.setParameter("userId", userId);
		query.setParameter("prefix", StringUtilities.processForLike(prefix.toLowerCase()));
		query.setMaxResults(10);

		return query.setResultTransformer(Transformers.aliasToBean(SuggestionDTO.class)).list();
	}
}
