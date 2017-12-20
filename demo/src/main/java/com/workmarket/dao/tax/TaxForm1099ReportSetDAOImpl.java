package com.workmarket.dao.tax;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.tax.TaxForm1099Set;
import com.workmarket.domains.model.tax.TaxReportSetStatusType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings(value = "unchecked")
public class TaxForm1099ReportSetDAOImpl extends AbstractDAO<TaxForm1099Set> implements TaxReportSetDAO<TaxForm1099Set> {

	@Override
	protected Class<TaxForm1099Set> getEntityClass() {
		return TaxForm1099Set.class;
	}

	@Override
	public List<TaxForm1099Set> findAllTaxReportSets() {
		return getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.desc("createdOn"))
				.list();
	}

	@Override
	public List<TaxForm1099Set> findAllTaxReportSetsByYear(int year) {
		return getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("taxYear", year))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.desc("createdOn"))
				.list();
	}

	@Override
	public TaxForm1099Set findPublishedTaxReportForYear(Integer year) {
		return (TaxForm1099Set)getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("taxYear", year))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public TaxForm1099Set findLatestPublishedTaxReport() {
		return (TaxForm1099Set)getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("taxReportSetStatusType.code", TaxReportSetStatusType.PUBLISHED))
				.addOrder(Order.desc("taxYear"))
				.setMaxResults(1)
				.uniqueResult();
	}
}
