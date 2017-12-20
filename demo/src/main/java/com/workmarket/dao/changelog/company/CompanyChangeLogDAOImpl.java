package com.workmarket.dao.changelog.company;


import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.changelog.company.CompanyChangeLog;
import com.workmarket.domains.model.changelog.company.CompanyChangeLogPagination;
import com.workmarket.utility.HibernateUtilities;
import com.workmarket.utility.ProjectionUtilities;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class CompanyChangeLogDAOImpl extends AbstractDAO<CompanyChangeLog> implements CompanyChangeLogDAO {

	protected Class<CompanyChangeLog> getEntityClass() {
		return CompanyChangeLog.class;
	}

	@Override
	public CompanyChangeLogPagination findAllCompanyChangeLogsByCompanyId(Long companyId, CompanyChangeLogPagination pagination) throws Exception {
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		criteria.createAlias("company", "company");
		criteria.createAlias("actor", "actor");
		criteria.createAlias("masqueradeActor", "masqueradeActor", CriteriaSpecification.LEFT_JOIN);

		count.createAlias("company", "company");
		count.createAlias("actor", "actor");

		if (pagination.getSortColumn() != null) {
			String sort = "id";
			if (CompanyChangeLogPagination.SORTS.ID.toString().equals(pagination.getSortColumn()))
				sort = "id";
			else if (CompanyChangeLogPagination.SORTS.TYPE.toString().equals(pagination.getSortColumn()))
				sort = "type";
			else if (CompanyChangeLogPagination.SORTS.COMPANY_NAME.toString().equals(pagination.getSortColumn()))
				sort = "company.name";
			else if (CompanyChangeLogPagination.SORTS.CREATED_ON.toString().equals(pagination.getSortColumn()))
				sort = "createdOn";
			else if (CompanyChangeLogPagination.SORTS.ACTOR_LAST_NAME.toString().equals(pagination.getSortColumn()))
				sort = "actor.lastName";
			else if (CompanyChangeLogPagination.SORTS.MASQUERADE_ACTOR_LAST_NAME.toString().equals(pagination.getSortColumn()))
				sort = "masqueradeActor.lastName";
			else if (CompanyChangeLogPagination.SORTS.OLD_VALUE.toString().equals(pagination.getSortColumn()))
				sort = "oldValue";
			else if (CompanyChangeLogPagination.SORTS.NEW_VALUE.toString().equals(pagination.getSortColumn()))
				sort = "newValue";

			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				criteria.addOrder(Order.desc(sort));
			} else {
				criteria.addOrder(Order.asc(sort));
			}
		} else {
			criteria.addOrder(Order.asc("id"));
		}

		if (pagination.hasFilter(CompanyChangeLogPagination.FILTER_KEYS.MASQUERADED)) {
			if (BooleanUtils.toBoolean(pagination.getFilter(CompanyChangeLogPagination.FILTER_KEYS.MASQUERADED))) {
				criteria.add(Restrictions.isNotNull("masqueradeActor"));
				count.add(Restrictions.isNotNull("masqueradeActor"));
			}
		}

		if (companyId != null) {
			criteria.add(Restrictions.eq("company.id", companyId));
			count.add(Restrictions.eq("company.id", companyId));
		}

		pagination.setResults(criteria.list());

		if (pagination.getProjection().length > 0) // TODO AP Refactor
		{
			pagination.setProjectionResults(ProjectionUtilities.projectAsArray(pagination.getProjection(), pagination.getResults()));
		}

		pagination.setRowCount(HibernateUtilities.getRowCount(count));

		return pagination;
	}
}
