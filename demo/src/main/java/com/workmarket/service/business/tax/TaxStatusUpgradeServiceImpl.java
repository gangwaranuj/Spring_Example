package com.workmarket.service.business.tax;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workmarket.dao.tax.TaxUpdateAuditTrailDAO;
import com.workmarket.dao.tax.TaxUpdateDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.tax.TaxUpdateAuditTrail;

@Service
public class TaxStatusUpgradeServiceImpl implements TaxStatusUpgradeService {

	@Autowired private TaxUpdateAuditTrailDAO taxUpdateAuditTrailDAO;
	@Autowired private TaxUpdateDAO taxUpdateDAO;
	
	@Override
	public void upgradeCompanyTaxStatus(Company company, AccountServiceType serviceType, Calendar start, Calendar end) {
		// save the audit trail
		taxUpdateAuditTrailDAO.save(company, serviceType, start, end);
		
		// save backup
		taxUpdateDAO.backup(company, serviceType, start, end);
		
		// perform upgrade
		taxUpdateDAO.upgrade(company, serviceType, start, end);
	}

}
