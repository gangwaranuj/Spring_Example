package com.workmarket.dao.tax;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import com.workmarket.domains.model.tax.TaxServiceReportSet;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zhe on 12/26/14.
 */

@Component
@SuppressWarnings(value = "unchecked")
public class TaxServiceReportSetDAOImpl extends AbstractDAO<TaxServiceReportSet> implements TaxReportSetDAO<TaxServiceReportSet> {

	@Override
	protected Class<TaxServiceReportSet> getEntityClass() {
		return TaxServiceReportSet.class;
	}

	@Override
	public List<TaxServiceReportSet> findAllTaxReportSets() {
		return getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.desc("createdOn"))
				.list();
	}

	@Override
	public List<TaxServiceReportSet> findAllTaxReportSetsByYear(int year) {
		return getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("taxYear", year))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.desc("createdOn"))
				.list();
	}

	@Override
	public TaxServiceReportSet findPublishedTaxReportForYear(Integer year) {
		return (TaxServiceReportSet)getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("taxYear", year))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public TaxServiceReportSet findLatestPublishedTaxReport() {
		return (TaxServiceReportSet)getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.addOrder(Order.desc("taxYear"))
				.setMaxResults(1)
				.uniqueResult();
	}
}
