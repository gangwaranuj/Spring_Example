package com.workmarket.domains.work.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.work.model.WorkTemplatePagination;
import com.workmarket.utility.HibernateUtilities;
import com.workmarket.utility.NumberUtilities;

import org.apache.commons.collections.MapUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Repository
public class WorkTemplateDAOImpl extends PaginationAbstractDAO<WorkTemplate> implements WorkTemplateDAO {

	@Override
	protected Class<WorkTemplate> getEntityClass() {
		return WorkTemplate.class;
	}

	@Qualifier("jdbcTemplate") @Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public void applySorts(Pagination<WorkTemplate> pagination, Criteria query, Criteria count) {
		String sort = "templateName";
		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(WorkTemplatePagination.SORTS.NAME.toString())) {
				sort = "templateName";
			} else if (pagination.getSortColumn().equals(WorkTemplatePagination.SORTS.LATEST_CREATED_WORK_DATE.toString())) {
				sort = "latestCreatedWork";
			}
		}

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			query.addOrder(Order.desc(sort));
			count.addOrder(Order.desc(sort));
		} else {
			query.addOrder(Order.asc(sort));
			count.addOrder(Order.asc(sort));
		}
	}

	@Override
	public void applyFilters(Pagination<WorkTemplate> workTemplatePagination, Criteria query, Criteria count) {
	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public WorkTemplatePagination findAllActiveWorkTemplates(Long companyId, WorkTemplatePagination pagination) {
		Assert.notNull(companyId, "Invalid user companyId");
		Assert.notNull(pagination, "Invalid pagination");

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		// base restrictions
		HibernateUtilities.addRestrictionsEq(criteria, "company.id", companyId, "workStatusType.code", WorkStatusType.DRAFT, "deleted", Boolean.FALSE);
		HibernateUtilities.addRestrictionsEq(count, "company.id", companyId, "workStatusType.code", WorkStatusType.DRAFT, "deleted", Boolean.FALSE);

		Long rowCount = HibernateUtilities.getRowCount(count);
		applySorts(pagination, criteria, count);
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public WorkTemplatePagination findAllTemplatesByStatusCode(Long companyId, WorkTemplatePagination pagination, String workStatusType) {
		Assert.notNull(companyId, "Invalid user companyId");
		Assert.notNull(pagination, "Invalid pagination");
		Assert.notNull(workStatusType, "Invalid type");

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		// base restrictions
		HibernateUtilities.addRestrictionsEq(criteria, "company.id", companyId, "deleted", Boolean.FALSE, "workStatusType.code", workStatusType);
		HibernateUtilities.addRestrictionsEq(count, "company.id", companyId, "deleted", Boolean.FALSE, "workStatusType.code", workStatusType);

		Long rowCount = HibernateUtilities.getRowCount(count);
		applySorts(pagination, criteria, count);
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);
		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@Override
	public List<WorkTemplate> findAllActiveWorkTemplates(Long companyId) {
		Assert.notNull(companyId, "Invalid company id");
		WorkTemplatePagination pagination = new WorkTemplatePagination();
		pagination.setReturnAllRows();

		return findAllActiveWorkTemplates(companyId, pagination).getResults();
	}

	@Override
	public WorkTemplate findWorkTemplateById(Long workTemplateId) {
		Assert.notNull(workTemplateId, "Invalid work template id");
		return (WorkTemplate) getFactory().getCurrentSession().get(WorkTemplate.class, workTemplateId);
	}

	@Override
	public WorkTemplate findWorkTemplateByName(Long companyId, String name) {
		Assert.notNull(companyId, "Invalid company id");
		Assert.hasText(name, "Invalid template name");

		return (WorkTemplate) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.ilike("templateName", name, MatchMode.EXACT))
				.add(Restrictions.eq("company.id", companyId))
				.setMaxResults(1).uniqueResult();
	}

	@Override
	public void deleteWorkTemplate(Long workTemplateId) {
		Assert.notNull(workTemplateId, "Invalid work template id");

		WorkTemplate template = findWorkTemplateById(workTemplateId);

		Assert.notNull(template, "Unable to find work template before deletion");
		Assert.isTrue(!template.getDeleted(), "Work template already deleted");

		template.setDeleted(true);
	}

	@Override
	public Map<Long, String> findAllActiveWorkTemplatesIdNameMap(Long companyId) {
		Assert.notNull(companyId, "Invalid company id");
		String sql = "SELECT t.id, t.template_name FROM work t WHERE t.company_id = :companyId AND type = 'WT' AND deleted = false AND t.work_status_type_code = 'draft' ORDER BY t.template_name";
		MapSqlParameterSource params = new MapSqlParameterSource().addValue("companyId", companyId);
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
		Map<Long,String> resultMap = Maps.newLinkedHashMap();
		for (Map<String, Object> row : rows) {
			resultMap.put(((Integer)row.get("id")).longValue(), (String)row.get("template_name"));
		}
		return resultMap;
	}

	@Override
	public Map<Long,String> findAllActiveWorkTemplatesIdNumberMap(Long companyId) {
		Assert.notNull(companyId, "Invalid company id");
		String sql = "SELECT t.id, t.work_number, t.template_name FROM work t WHERE t.company_id = :companyId AND type = 'WT' AND deleted = false AND t.work_status_type_code = 'draft' ORDER BY t.template_name";
		MapSqlParameterSource params = new MapSqlParameterSource().addValue("companyId", companyId);
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
		Map<Long,String> resultMap = Maps.newLinkedHashMap();
		for (Map<String, Object> row : rows) {
			resultMap.put(((Integer)row.get("id")).longValue(), (String)row.get("work_number"));
		}
		return resultMap;
	}

	@Override
	public Map<String,Map<String, Object>> findAllActiveWorkTemplatesWorkNumberNameMap(Long companyId, Long clientId) {
		Assert.notNull(companyId, "Invalid company id");
		MapSqlParameterSource params = new MapSqlParameterSource().addValue("companyId", companyId);
		String sql = "SELECT t.work_number, t.template_name, t.client_company_id FROM work t WHERE t.company_id = :companyId AND type = 'WT' AND deleted = false AND t.work_status_type_code = 'draft'";
		if (clientId != null) {
			sql += " AND t.client_company_id = :clientId";
			params.addValue("clientId", clientId);
		}
		sql += " ORDER BY t.template_name";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
		Map<String,Map<String,Object>> resultMap = Maps.newLinkedHashMap();
		for (Map<String, Object> row : rows) {
			final Map<String, Object> map = Maps.newHashMap();
			map.put("template_name", row.get("template_name"));
			map.put("client_id", NumberUtilities.safeIntToLong((Integer) row.get("client_company_id")));
			resultMap.put((String)row.get("work_number"), map);
		}
		return resultMap;
	}
}
