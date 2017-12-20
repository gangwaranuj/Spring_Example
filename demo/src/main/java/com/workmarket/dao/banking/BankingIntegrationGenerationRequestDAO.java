package com.workmarket.dao.banking;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequest;

import java.util.List;

public interface BankingIntegrationGenerationRequestDAO extends DAOInterface<BankingIntegrationGenerationRequest> {

	BankingIntegrationGenerationRequest get(Long id, boolean fetchAssociations);

	List<BankingIntegrationGenerationRequest> findByType(String type);

	List<BankingIntegrationGenerationRequest> findByTypeAndStatus(String type, String status);
}
