package com.workmarket.dao.license;


import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.license.LicensePagination;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;

@Repository
public class LicenseDAOImpl extends AbstractDAO<License> implements LicenseDAO {

	protected Class<License> getEntityClass() {
		return License.class;
	}

	@Override
	public License findLicenseById(Long licenseId) {
		Assert.notNull(licenseId);
		return (License) getFactory().getCurrentSession().get(License.class, licenseId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public License findLicenseByName(String name) {
		Assert.hasText(name);
		return (License) DataAccessUtils.singleResult(
				getFactory().getCurrentSession().getNamedQuery("license.findLicenseByName")
						.setParameter("name", name).list());
	}

	@SuppressWarnings("unchecked")
	@Override
	public LicensePagination findAllLicensesByStateId(String stateId, LicensePagination pagination) {
		Assert.notNull(stateId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("name"));
		}

		criteria.add(Restrictions.eq("state", stateId));
		count.add(Restrictions.eq("state", stateId));

		criteria.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<License> findAll() {
		return getFactory().getCurrentSession().createQuery("select l from license l where l.deleted = false").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public LicensePagination findAll(LicensePagination pagination) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit());

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setProjection(Projections.rowCount());

		criteria.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(LicensePagination.FILTER_KEYS.VERIFICATION_STATUS.toString()) != null) {
				String status = pagination.getFilters().get(LicensePagination.FILTER_KEYS.VERIFICATION_STATUS.toString());

				if (VerificationStatus.valueOf(status).equals(VerificationStatus.UNVERIFIED)) {
					criteria.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
					count.add(Restrictions.in("verificationStatus", VerificationStatus.UNVERIFIED_STATUSES));
				} else {
					criteria.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
					count.add(Restrictions.eq("verificationStatus", VerificationStatus.valueOf(status)));
				}
			}
		}

		String sort = "id";
		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(LicensePagination.SORTS.CREATED_DATE.toString())) {
				sort = "createdOn";
			} else if (pagination.getSortColumn().equals(LicensePagination.SORTS.STATE.toString())) {
				sort = "state";
			} else if (pagination.getSortColumn().equals(LicensePagination.SORTS.VERIFICATION_STATUS.toString())) {
				sort = "verificationStatus";
			} else if (pagination.getSortColumn().equals(LicensePagination.SORTS.LICENSE_NAME.toString())) {
				sort = "name";
			} else if (pagination.getSortColumn().equals(LicensePagination.SORTS.LAST_ACTIVITY_DATE.toString())) {
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
	public List<License> findAllLicenseNamesByIds(Set<Long> licenseIdsInResponse) {
		Query q = getFactory().getCurrentSession().createQuery("select l.name, l.state, l.id from license l where l.deleted = false and l.verificationStatus = com.workmarket.domains.model.VerificationStatus.VERIFIED and l.id in (:setOfIds)");
		q.setParameterList("setOfIds", licenseIdsInResponse);
		List<Object> results = q.list();
		if (results == null) {
			return emptyList();
		}
		List<License> returnVal = Lists.newArrayListWithCapacity(results.size());
		for (Object result : results) {
			Object[] row = (Object[]) result;
			License license = new License();
			license.setName((String) row[0]);
			license.setState((String) row[1]);
			license.setId((Long) row[2]);
			returnVal.add(license);
		}
		return returnVal;
	}
}
