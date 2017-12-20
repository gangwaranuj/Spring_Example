package com.workmarket.dao.banking;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequestType;

public interface BankingIntegrationGenerationRequestTypeDAO extends DAOInterface<BankingIntegrationGenerationRequestType>{
	
	public List<BankingIntegrationGenerationRequestType> getBankingIntegreationGenerationRequestTypes();

}
