package com.workmarket.service.business.planconfig;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.WorkFeeBand;
import com.workmarket.domains.model.account.WorkFeeConfiguration;
import com.workmarket.domains.model.planconfig.PlanConfigVisitor;
import com.workmarket.domains.model.planconfig.TransactionFeePlanConfig;
import com.workmarket.service.business.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: micah
 * Date: 9/2/14
 * Time: 9:31 PM
 */
@Component
public class PlanConfigVisitorImpl implements PlanConfigVisitor {
	@Autowired CompanyService companyService;

	@Override
	public void visit(TransactionFeePlanConfig transactionFeePlanConfig, Long companyId) {
		Company company = companyService.findCompanyById(companyId);
		if (company == null) { return; }
		// should only be one as it's only called during registration
		AccountRegister accountRegister = company.getAccountRegisters().iterator().next();
		WorkFeeConfiguration workFeeConfiguration = accountRegister.getWorkFeeConfigurations().iterator().next();
		WorkFeeBand workFeeBand = workFeeConfiguration.getWorkFeeBands().iterator().next();
		workFeeBand.setPercentage(transactionFeePlanConfig.getPercentage());
		accountRegister.setCurrentWorkFeePercentage(transactionFeePlanConfig.getPercentage());
	}
}
