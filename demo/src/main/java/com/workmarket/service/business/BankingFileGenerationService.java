package com.workmarket.service.business;

import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequest;
import com.workmarket.domains.model.postalcode.Country;

import java.util.List;

public interface BankingFileGenerationService {

	/**
	 * Creates a CSV for uploading to external banking system to process ACH inbound/outbound transactions
	 * @param 	requestorId, type, notes
	 */
	void initiateBankFileProcessing(Long requestorId, String type, String notes);
	
	/**
	 * Loads transactions attached to a processed banking integration request
	 */
	BankingIntegrationGenerationRequest loadTransactionsAttachedToBankingIntegration(long bankingIntegrationRequestId);

	BankingIntegrationGenerationRequest findBankingIntegrationGenerationRequest(long bankingIntegrationRequestId);

	void cancelBankingIntegrationGenerationRequest(long bankingIntegrationRequestId);
	
	/**
	 * Loads transactions attached to a processed banking integration request
	 */
	void markBankAccountTransactionNonPending(Long transactionId);
	
	/**
	 * Allows front end to check if bank file processing is in progress for type
	 * @param  type The type of file to generate
	 */
	boolean bankFileGenerationInProcess(String type);

	void updateBankTransactionStatus(Long userId, Long transactionId, String notes, String statusCode) throws Exception;

	void updateBankTransactionsStatusAsync(Long userId, List<Long> transactionIds, String notes, String statusCode);

	void markBankAccountTransactionProcessing(List<Long> transactionIds);

	/**
	 * Get the banking integrations requests submitted to the system
	 * @return List of banking integration requests
	 */
	List<BankingIntegrationGenerationRequest> findBankingIntegrationGenerationRequests(String type);

	void processPendingAch() throws Exception;
	void processPendingOutbound(Country country) throws Exception;
	void processPendingInbound() throws Exception;
	void processPendingPayPal() throws Exception;
	void processPendingGCC() throws Exception;
	
	void addBatchNumberToBankingFile(Long requestId, String batchNumber);

	List<RegisterTransaction> copyStatusToChildren(Long txId);

	List<RegisterTransaction> copyStatusToProjectChildren(Long txId);

}
