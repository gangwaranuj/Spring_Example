package com.workmarket.dao.tax;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.tax.TaxServiceReportDownloadAudit;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Created by zhe on 1/26/15.
 */
@Component
public class TaxServiceReportDownloadAuditDAOImpl extends AbstractDAO<TaxServiceReportDownloadAudit> implements TaxServiceReportDownloadAuditDAO {

	protected Class<TaxServiceReportDownloadAudit> getEntityClass() {
		return TaxServiceReportDownloadAudit.class;
	}

	@Override
	public TaxServiceReportDownloadAudit findByTaxServiceReportId(long taxServiceReportId) {
		Criteria query = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("taxServiceReportId", taxServiceReportId)).setMaxResults(1);
		return (TaxServiceReportDownloadAudit)query.uniqueResult();
	}
}
