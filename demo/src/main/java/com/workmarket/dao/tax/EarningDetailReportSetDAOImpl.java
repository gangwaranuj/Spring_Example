package com.workmarket.dao.tax;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: iloveopt
 * Date: 1/7/14
 */

@Component
@SuppressWarnings(value = "unchecked")
public class EarningDetailReportSetDAOImpl extends AbstractDAO<EarningDetailReportSet> implements TaxReportSetDAO<EarningDetailReportSet> {

	@Override
	protected Class<EarningDetailReportSet> getEntityClass() {
		return EarningDetailReportSet.class;
	}

	@Override
	public List<EarningDetailReportSet> findAllTaxReportSets() {
		return getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.desc("createdOn"))
				.list();
	}

	@Override
	public List<EarningDetailReportSet> findAllTaxReportSetsByYear(int year) {
		return getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("taxYear", year))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.desc("createdOn"))
				.list();
	}

	@Override
	public EarningDetailReportSet findPublishedTaxReportForYear(Integer year) {
		return (EarningDetailReportSet)getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("taxYear", year))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public EarningDetailReportSet findLatestPublishedTaxReport() {
		return (EarningDetailReportSet)getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.addOrder(Order.desc("taxYear"))
				.setMaxResults(1)
				.uniqueResult();
	}
}

