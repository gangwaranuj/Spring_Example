package com.workmarket.dao.certification;


import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.certification.CertificationVendor;
import com.workmarket.domains.model.certification.CertificationVendorPagination;

@Repository
public class CertificationVendorDAOImpl extends AbstractDAO<CertificationVendor> implements CertificationVendorDAO {

	protected Class<CertificationVendor> getEntityClass() {
		return CertificationVendor.class;
	}

	@Override
	public CertificationVendor findCertificationVendorById(Long vendorId) {
		Assert.notNull(vendorId);

		return (CertificationVendor) getFactory().getCurrentSession().get(CertificationVendor.class, vendorId);
	}

	@Override
	public CertificationVendor findCertificationVendorByNameAndIndustryId(String name, Long industryId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("certificationType.id", industryId))
				.add(Restrictions.ilike("name", name, MatchMode.EXACT))
				.setMaxResults(1);

		return (CertificationVendor) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public CertificationVendorPagination findAllCertificationVendors(CertificationVendorPagination pagination) {
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());

		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());
		criteria.setFetchMode("instruction", FetchMode.JOIN)
				.createAlias("certificationType", "industry", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(CertificationVendorPagination.FILTER_KEYS.VERIFICATION_STATUS.toString()) != null) {
				String status = pagination.getFilters().get(CertificationVendorPagination.FILTER_KEYS.VERIFICATION_STATUS.toString());

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
			if (pagination.getSortColumn().equals(CertificationVendorPagination.SORTS.CREATED_DATE.toString())) {
				sort = "createdOn";
			} else if (pagination.getSortColumn().equals(CertificationVendorPagination.SORTS.INDUSTRY.toString())) {
				sort = "industry.name";
			} else if (pagination.getSortColumn().equals(CertificationVendorPagination.SORTS.VERIFICATION_STATUS.toString())) {
				sort = "verificationStatus";
			} else if (pagination.getSortColumn().equals(CertificationVendorPagination.SORTS.VENDOR_NAME.toString())) {
				 sort = "name";
			} else if (pagination.getSortColumn().equals(CertificationVendorPagination.SORTS.LAST_ACTIVITY_DATE.toString())) {
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

		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}

		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CertificationVendorPagination findCertificationVendorByTypeId(CertificationVendorPagination pagination, Long certificationTypeId) {
		Assert.notNull(pagination);
		Assert.notNull(certificationTypeId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());

		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());
		criteria.setFetchMode("instruction", FetchMode.JOIN);

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("name"));
		}

		criteria.add(Restrictions.eq("certificationType.id", certificationTypeId));
		criteria.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("verificationStatus", VerificationStatus.VERIFIED));

		count.add(Restrictions.eq("certificationType.id", certificationTypeId));
		count.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("verificationStatus", VerificationStatus.VERIFIED));


		pagination.setResults(criteria.list());

		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}

		return pagination;
	}
}


