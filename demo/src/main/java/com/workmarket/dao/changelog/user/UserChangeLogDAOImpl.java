package com.workmarket.dao.changelog.user;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.changelog.user.UserChangeLog;
import com.workmarket.domains.model.changelog.user.UserChangeLogPagination;
import com.workmarket.utility.HibernateUtilities;
import com.workmarket.utility.ProjectionUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class UserChangeLogDAOImpl<T extends UserChangeLog> extends AbstractDAO<T> implements UserChangeLogDAO<T> {

	protected Class<UserChangeLog> getEntityClass() {
		return UserChangeLog.class;
	}

	@Override
	public UserChangeLogPagination findAllUserChangeLogByUserId(Long userId, UserChangeLogPagination pagination) throws Exception {
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		if (pagination.getSortColumn() != null) {
			String sort = "id";
			if (UserChangeLogPagination.SORTS.ID.toString().equals(pagination.getSortColumn())) {
				sort = "id";
			} else if (UserChangeLogPagination.SORTS.TYPE.toString().equals(pagination.getSortColumn())) {
				sort = "type";
			} else if (UserChangeLogPagination.SORTS.CREATED_ON.toString().equals(pagination.getSortColumn())) {
				sort = "createdOn";
			} else if (UserChangeLogPagination.SORTS.OLD_VALUE.toString().equals(pagination.getSortColumn())) {
				sort = "oldValue";
			} else if (UserChangeLogPagination.SORTS.NEW_VALUE.toString().equals(pagination.getSortColumn())) {
				sort = "newValue";
			}

			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				criteria.addOrder(Order.desc(sort));
			} else {
				criteria.addOrder(Order.asc(sort));
			}

		} else {
			criteria.addOrder(Order.asc("id"));
		}

		if (userId != null) {
			criteria.add(Restrictions.eq("user", userId));
			count.add(Restrictions.eq("user", userId));
		}

		pagination.setResults(criteria.list());

		if (pagination.getProjection().length > 0) {
			pagination.setProjectionResults(ProjectionUtilities.projectAsArray(pagination.getProjection(), pagination.getResults()));
		}

		pagination.setRowCount(HibernateUtilities.getRowCount(count));

		return pagination;
	}
}
