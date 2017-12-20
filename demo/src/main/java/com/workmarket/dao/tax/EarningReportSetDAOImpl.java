package com.workmarket.dao.tax;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.tax.EarningReportSet;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings(value = "unchecked")
public class EarningReportSetDAOImpl extends AbstractDAO<EarningReportSet> implements TaxReportSetDAO<EarningReportSet> {

	@Override
	protected Class<EarningReportSet> getEntityClass() {
		return EarningReportSet.class;
	}

	@Override
	public List<EarningReportSet> findAllTaxReportSets() {
		return getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.desc("createdOn"))
				.list();
	}

	@Override
	public List<EarningReportSet> findAllTaxReportSetsByYear(int year) {
		return getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("taxYear", year))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.desc("createdOn"))
				.list();
	}

	@Override
	public EarningReportSet findPublishedTaxReportForYear(Integer year) {
		return (EarningReportSet)getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("taxYear", year))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public EarningReportSet findLatestPublishedTaxReport() {
		return (EarningReportSet)getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.addOrder(Order.desc("taxYear"))
				.setMaxResults(1)
				.uniqueResult();
	}

}
