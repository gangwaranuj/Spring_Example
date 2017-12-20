package com.workmarket.service.business.tax;

import java.util.Calendar;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.AccountServiceType;

public interface TaxStatusUpgradeService {

	void upgradeCompanyTaxStatus(Company company, AccountServiceType serviceType, Calendar start, Calendar end);

}
