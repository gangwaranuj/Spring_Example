package com.workmarket.dao.company;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.CompanySignUpInfo;

/**
 * User: iloveopt
 * Date: 8/28/14
 */
public interface CompanySignUpInfoDAO extends DAOInterface<CompanySignUpInfo> {

	String getCompanySignUpPricingPlan(long companyId);
}
