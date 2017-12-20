package com.workmarket.dao.tax;

import java.util.Calendar;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.TaxUpdateAuditTrail;

public interface TaxUpdateAuditTrailDAO extends DAOInterface<TaxUpdateAuditTrail> {

	void save(Company company, AccountServiceType serviceType, Calendar start, Calendar end);

}
