package com.workmarket.service.business.tax;

import com.google.common.base.Optional;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.tax.TaxVerificationRequest;

import java.util.List;

/**
 * Created by nick on 11/29/12 3:49 PM
 */
public interface TaxVerificationService {
	boolean isTaxVerificationAvailable();

	List<TaxVerificationRequest> findTaxVerificationRequests();

	Optional<TaxVerificationRequest> createUsaTaxVerificationBatch(Long userId);

	Optional<TaxVerificationRequest> findTaxEntityValidationRequest(Long id);

	void addConfirmationNumberToTaxEntityValidationRequest(Long requestId, String confirmationNumber);

	long validateRequestFromCsv(Long requestId, Asset asset) throws Exception;

	boolean cancelTaxVerificationRequest(Long requestId);
}
