package com.workmarket.dao.banking;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequest;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BankingIntegrationGenerationRequestDAOImpl extends AbstractDAO<BankingIntegrationGenerationRequest> implements BankingIntegrationGenerationRequestDAO {

	protected Class<BankingIntegrationGenerationRequest> getEntityClass() {
		return BankingIntegrationGenerationRequest.class;
    }

	@Override
	public BankingIntegrationGenerationRequest get(Long id, boolean fetchAssociations) {
		if (!fetchAssociations) {
			return get(id);
		}

		return (BankingIntegrationGenerationRequest)getFactory().getCurrentSession().getNamedQuery("bankingintegrationgenerationrequest.get")
				.setLong("id", id)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<BankingIntegrationGenerationRequest> findByType(String type){
		
		Query query = getFactory().getCurrentSession().getNamedQuery("bankingintegrationgenerationrequest.find")
			.setString("type", type);
		return query.list();
		
	}

	@SuppressWarnings("unchecked")
	public List<BankingIntegrationGenerationRequest> findByTypeAndStatus(String type, String status){
		Query query = getFactory().getCurrentSession().getNamedQuery("bankingintegrationgenerationrequest.findByTypeAndStatus")
			.setString("status", status)
			.setString("type", type);
		return query.list();
		
	}
}
