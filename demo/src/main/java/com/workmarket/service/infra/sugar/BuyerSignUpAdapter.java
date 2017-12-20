package com.workmarket.service.infra.sugar;

import org.springframework.http.ResponseEntity;

/**
 * Date: 5/19/14
 * Time: 4:13 PM
 */
public interface BuyerSignUpAdapter {

	ResponseEntity<String> createLead(long companyId);

	String getAccountOwner(String companyId);
}
