package com.workmarket.dao.license;


import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociationPagination;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class UserLicenseAssociationDAOImpl extends AbstractDAO<UserLicenseAssociation> implements UserLicenseAssociationDAO {

	protected Class<UserLicenseAssociation> getEntityClass() {
		return UserLicenseAssociation.class;
	}

	@Override
	public UserLicenseAssociation findAssociationById(Long id) {
		Assert.notNull(id);
		UserLicenseAssociation userLicenseAssociation = (UserLicenseAssociation) getFactory().getCurrentSession()
				.get(UserLicenseAssociation.class, id);
		Hibernate.initialize(userLicenseAssociation.getAssets());
		return userLicenseAssociation;
	}

	@Override
	public UserLicenseAssociation findAssociationByLicenseIdAndUserId(Long licenseId, Long userId) {
		Assert.notNull(licenseId);
		Assert.notNull(userId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("license", FetchMode.JOIN)
				.add(Restrictions.eq("license.id", licenseId))
				.add(Restrictions.eq("user.id", userId));

		UserLicenseAssociation association = (UserLicenseAssociation) criteria.uniqueResult();
		if (association != null)
			Hibernate.initialize(association.getAssets());

		return association;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserLicenseAssociationPagination findAllUserLicenseAssociation(UserLicenseAssociationPagination pagination) {
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.createAlias("user", "user", Criteria.INNER_JOIN)
				.createAlias("license", "license", Criteria.INNER_JOIN)
				.setFetchMode("assets", FetchMode.JOIN);

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount())
				.createAlias("user", "user", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(UserLicenseAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString()) != null) {
				String status = pagination.getFilters().get(UserLicenseAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS.toString());

				if (VerificationStatus.valueOf(status).equals(VerificationStatus.UNVERIFIED)) {
					criteria.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
					count.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
				} else {
					criteria.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
					count.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
				}
			}

			if (pagination.getFilters().get(UserLicenseAssociationPagination.FILTER_KEYS.USER_NAME) != null) {
				String name = pagination.getFilters().get(UserLicenseAssociationPagination.FILTER_KEYS.USER_NAME);
				criteria.add(Restrictions.or(Restrictions.ilike("user.firstName", name, MatchMode.ANYWHERE), Restrictions.ilike("user.lastName", name, MatchMode.ANYWHERE)));
				count.add(Restrictions.or(Restrictions.ilike("user.firstName", name, MatchMode.ANYWHERE), Restrictions.ilike("user.lastName", name, MatchMode.ANYWHERE)));
			}
		}

		String sort = "id";
		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(UserLicenseAssociationPagination.SORTS.CREATED_DATE.toString())) {
				sort = "createdOn";
			} else if (pagination.getSortColumn().equals(UserLicenseAssociationPagination.SORTS.USER_FIRST_NAME.toString())) {
				sort = "user.firstName";
			} else if (pagination.getSortColumn().equals(UserLicenseAssociationPagination.SORTS.USER_LAST_NAME.toString())) {
				sort = "user.lastName";
			} else if (pagination.getSortColumn().equals(UserLicenseAssociationPagination.SORTS.STATE.toString())) {
				sort = "license.state";
			} else if (pagination.getSortColumn().equals(UserLicenseAssociationPagination.SORTS.VERIFICATION_STATUS.toString())) {
				sort = "verificationStatus";
			} else if (pagination.getSortColumn().equals(UserLicenseAssociationPagination.SORTS.LICENSE_NAME.toString())) {
				sort = "license.name";
			} else if (pagination.getSortColumn().equals(UserLicenseAssociationPagination.SORTS.LICENSE_NUMBER.toString())) {
				sort = "licenseNumber";
			} else if (pagination.getSortColumn().equals(UserLicenseAssociationPagination.SORTS.LAST_ACTIVITY_DATE.toString())) {
				sort = "lastActivityOn";
			}
		}

		if (pagination.getSortDirection() != null)
			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				criteria.addOrder(Order.desc(sort));
			} else {
				criteria.addOrder(Order.asc(sort));
			}
		else
			criteria.addOrder(Order.desc(sort));

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);

		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserLicenseAssociationPagination findAllAssociationsByUserIdInList(Long userId, List<Long> licenseIds, UserLicenseAssociationPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());
		criteria.setFetchMode("user", FetchMode.JOIN);
		criteria.setFetchMode("license", FetchMode.JOIN);
		criteria.setFetchMode("assets", FetchMode.JOIN);

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("id"));
		}

		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("deleted", false));

		count.add(Restrictions.eq("user.id", userId));
		count.add(Restrictions.eq("deleted", false));

		if (CollectionUtils.isNotEmpty(licenseIds)) {
			criteria.add(Restrictions.in("license.id", licenseIds));
			count.add(Restrictions.in("license.id", licenseIds));
		}

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(UserLicenseAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS) != null) {

				String status = pagination.getFilters().get(UserLicenseAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS);

				criteria.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
				count.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
			}
		}

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@Override
	public UserLicenseAssociationPagination findAllAssociationsByUserId(Long userId, UserLicenseAssociationPagination pagination) {
		return findAllAssociationsByUserIdInList(userId, null, pagination);
	}

	@Override
	public UserLicenseAssociationPagination findAllAssociationsByUserIds(Set<Long> userIds, UserLicenseAssociationPagination pagination) {
		Assert.notEmpty(userIds);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());
		criteria.setFetchMode("user", FetchMode.JOIN);
		criteria.setFetchMode("license", FetchMode.JOIN);


		criteria.add(Restrictions.in("user.id", userIds));
		criteria.add(Restrictions.eq("deleted", false));

		count.add(Restrictions.in("user.id", userIds));
		count.add(Restrictions.eq("deleted", false));

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(UserLicenseAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS) != null) {

				String status = pagination.getFilters().get(UserLicenseAssociationPagination.FILTER_KEYS.VERIFICATION_STATUS);

				criteria.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
				count.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
			}
		}

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<License> findAllLicensesByUserIdInList(long userId, List<Long> ids) {
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.ne("verificationStatus", VerificationStatus.FAILED))
				.add(Restrictions.in("license.id", ids))
				.setProjection(Projections.property("license"));

		return criteria.list();
	}

}
