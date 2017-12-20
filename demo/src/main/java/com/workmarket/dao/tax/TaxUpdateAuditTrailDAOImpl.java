package com.workmarket.dao.tax;

import java.util.Calendar;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.TaxUpdateAuditTrail;

@Repository
public class TaxUpdateAuditTrailDAOImpl extends AbstractDAO<TaxUpdateAuditTrail>  implements TaxUpdateAuditTrailDAO {

	@Override
	protected Class<?> getEntityClass() {
		return TaxUpdateAuditTrail.class;
	}

	@Override
	public void save(Company company, AccountServiceType serviceType, Calendar startDate, Calendar endDate) {
		TaxUpdateAuditTrail auditTrail = new TaxUpdateAuditTrail();
		
		auditTrail.setCompany(company);
		auditTrail.setServiceType(serviceType);
		auditTrail.setStartDate(startDate);
		auditTrail.setEndDate(endDate);
		
		saveOrUpdate(auditTrail);
	}

}
