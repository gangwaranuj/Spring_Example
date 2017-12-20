package com.workmarket.dao;

import java.util.List;

import com.workmarket.domains.model.*;
import com.workmarket.utility.HibernateUtilities;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class UserProfileModificationDAOImpl extends AbstractDAO<UserProfileModification> implements UserProfileModificationDAO {

	protected Class<UserProfileModification> getEntityClass() {
		return UserProfileModification.class;
	}


	@SuppressWarnings("unchecked")
	public ProfileModificationPagination findAllProfileModificationsByUserId(Long userId, ProfileModificationPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("user.id", userId));

		criteria.setFetchMode("profileModificationType", FetchMode.JOIN)
				.createAlias("profileModificationType", "profileModificationType")
				.setFetchMode("user", FetchMode.JOIN)
				.createAlias("user", "user")
				.setFetchMode("user.company", FetchMode.JOIN)
				.add(Restrictions.eq("user.id", userId));

		count.setProjection(Projections.rowCount());

		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());

		criteria.addOrder(Order.asc("id"));
		pagination.setResults(criteria.list());

		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);

		return pagination;

	}


	public UserProfileModification findUserProfileModificationById(Long id) {
		return (UserProfileModification) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("id", id))
				.setFetchMode("profileModificationType", FetchMode.JOIN)
				.setMaxResults(1)
				.uniqueResult();
	}


	@SuppressWarnings("unchecked")
	public ProfileModificationPagination findAllPendingProfileModifications(ProfileModificationPagination pagination) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		criteria.setFetchMode("profileModificationType", FetchMode.JOIN)
				.createAlias("profileModificationType", "profileModificationType")
				.setFetchMode("user", FetchMode.JOIN)
				.createAlias("user", "user")
				.setFetchMode("user.company", FetchMode.JOIN)
				.createAlias("user.company", "company");

		count.setProjection(Projections.rowCount())
				.setFetchMode("user", FetchMode.JOIN)
				.createAlias("user", "user");

		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		if (pagination.getFilters() != null) {
			String userName = pagination.getFilter(ProfileModificationPagination.FILTER_KEYS.NAME);
			if (!StringUtils.isBlank(userName)) {
				LogicalExpression restriction = Restrictions.or(
						Restrictions.like("user.firstName", String.format("%%%s%%", userName)),
						Restrictions.like("user.lastName", String.format("%%%s%%", userName)));
				criteria.add(restriction);
				count.add(restriction);
			}
		}

		String sortColumn = "id";
		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(ProfileModificationPagination.SORTS.COMPANY_NAME.name()))
				sortColumn = "company.name";
			 else if (pagination.getSortColumn().equals(ProfileModificationPagination.SORTS.MODIFIED_DATE.name()))
				sortColumn = "modifiedOn";
			 else if (pagination.getSortColumn().equals(ProfileModificationPagination.SORTS.NAME.name()))
				sortColumn = "user.firstName";

			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC))
				criteria.addOrder(Order.desc(sortColumn));
			else
				criteria.addOrder(Order.asc(sortColumn));
		} else
			criteria.addOrder(Order.desc(sortColumn));

		criteria.add(Restrictions.eq("userProfileModificationStatus", UserProfileModificationStatus.PENDING_APPROVAL));
		count.add(Restrictions.eq("userProfileModificationStatus", UserProfileModificationStatus.PENDING_APPROVAL));

		pagination.setResults(criteria.list());
		pagination.setRowCount(HibernateUtilities.getRowCount(count));

		return pagination;
	}


	@SuppressWarnings("unchecked")
	public List<UserProfileModification> findAllPendingProfileModificationsByUserId(Long userId) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("profileModificationType", FetchMode.JOIN)
				.setFetchMode("user", FetchMode.JOIN)
				.addOrder(Order.asc("id"))
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("userProfileModificationStatus", UserProfileModificationStatus.PENDING_APPROVAL));

		return criteria.list();
	}


	@SuppressWarnings("unchecked")
	public List<UserProfileModification> findAllPendingProfileModificationsByUserIdAndType(Long userId, ProfileModificationType type) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("profileModificationType", FetchMode.JOIN)
				.add(Restrictions.eq("profileModificationType.code", type.getCode()))
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("userProfileModificationStatus", UserProfileModificationStatus.PENDING_APPROVAL));

		return criteria.list();
	}


}

