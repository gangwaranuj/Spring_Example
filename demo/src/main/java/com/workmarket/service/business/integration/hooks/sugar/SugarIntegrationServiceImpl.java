package com.workmarket.service.business.integration.hooks.sugar;

import com.workmarket.service.infra.sugar.BuyerSignUpAdapter;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: iloveopt
 * Date: 6/4/14
 */

@Service
public class SugarIntegrationServiceImpl implements SugarIntegrationService {

	@Autowired BuyerSignUpAdapter buyerSignUpAdapter;

	@Override
	public void createLead(long companyId) {
		buyerSignUpAdapter.createLead(companyId);
	}

	@Override
	public String getAccountOwner(String companyId) {
		if (StringUtilities.isNotEmpty(companyId)) {
			return buyerSignUpAdapter.getAccountOwner(companyId);
		}
		return "NA";
	}
}
