package com.workmarket.dao.tax;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxForm1099DownloadAudit;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaxForm1099DownloadAuditDAOImpl extends AbstractDAO<TaxForm1099DownloadAudit> implements TaxForm1099DownloadAuditDAO {
	
	protected Class<TaxForm1099DownloadAudit> getEntityClass() {
		return TaxForm1099DownloadAudit.class;
	}

	@Override
	public TaxForm1099DownloadAudit findByTaxForm1099Id(long taxForm1099Id) {
		Criteria query = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("taxForm1099Id", taxForm1099Id)).setMaxResults(1);
		return (TaxForm1099DownloadAudit)query.uniqueResult();
	}

	@Override
	public List<TaxForm1099> findAllUndownloadedTaxForm1099ByTaxForm1099SetId(long taxFormSetId) {
		DetachedCriteria subQuery = DetachedCriteria.forClass(TaxForm1099DownloadAudit.class)
				.setProjection(Projections.distinct(Projections.property("taxForm1099Id")));

		Criteria query = getFactory().getCurrentSession().createCriteria(TaxForm1099.class)
				.add(Restrictions.eq("taxForm1099Set.id", taxFormSetId))
				.add(Subqueries.propertyNotIn("id", subQuery));
		return query.list();
	}
}
