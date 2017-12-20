package com.workmarket.dao.tax;

import java.util.Calendar;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.AccountServiceType;

public interface TaxUpdateDAO {

	void backup(Company company, AccountServiceType serviceType, Calendar start, Calendar end);

	void upgrade(Company company, AccountServiceType serviceType, Calendar start, Calendar end);

}
