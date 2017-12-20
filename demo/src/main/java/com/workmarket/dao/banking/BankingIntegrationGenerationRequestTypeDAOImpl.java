package com.workmarket.dao.banking;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequestType;

@Repository
public class BankingIntegrationGenerationRequestTypeDAOImpl extends AbstractDAO<BankingIntegrationGenerationRequestType> implements
		BankingIntegrationGenerationRequestTypeDAO {
	
	
	protected Class<BankingIntegrationGenerationRequestType> getEntityClass() {
        return BankingIntegrationGenerationRequestType.class;
    }
	
	@SuppressWarnings("unchecked")
	public List<BankingIntegrationGenerationRequestType> getBankingIntegreationGenerationRequestTypes(){
		
		return getFactory().getCurrentSession().createQuery("from banking_integration_generation_request_type").list();
		
	}
}
