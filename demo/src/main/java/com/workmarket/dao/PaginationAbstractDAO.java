package com.workmarket.dao;

import com.workmarket.domains.model.Pagination;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import java.util.Map;


public abstract class PaginationAbstractDAO<T> extends AbstractDAO<T> implements PaginatableDAOInterface<T> {

	public abstract void applySorts(Pagination<T> pagination, Criteria query, Criteria count);

	public abstract void applyFilters(Pagination<T> pagination, Criteria query, Criteria count);

	public abstract void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params);

	@SuppressWarnings(value="unchecked")
	public Pagination<T> paginationQuery(Pagination<T> pagination, Map<String, Object> params) {
		return paginationQuery((Class<T>)getEntityClass(), pagination, params);
	}

	@SuppressWarnings(value="unchecked")
	public Pagination<T> paginationQuery(Class<? extends T> clazz, Pagination<T> pagination, Map<String, Object> params) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(clazz);
		Criteria count = getFactory().getCurrentSession().createCriteria(clazz);
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());

		if (pagination.getResultsLimit() != null) {
			criteria.setMaxResults(pagination.getResultsLimit());
		}

		buildWhereClause(criteria, count, params);
		applySorts(pagination, criteria, count);
		applyFilters(pagination, criteria, count);

		pagination.setResults(criteria.list());
		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}

		return pagination;
	}

}
