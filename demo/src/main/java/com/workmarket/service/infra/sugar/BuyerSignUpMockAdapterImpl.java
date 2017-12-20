package com.workmarket.service.infra.sugar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * User: iloveopt
 * Date: 6/4/14
 */
public class BuyerSignUpMockAdapterImpl implements BuyerSignUpAdapter {

	@Override
	public ResponseEntity<String> createLead(long companyId) {
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@Override
	public String getAccountOwner(String companyId) {
		return "";
	}

}
