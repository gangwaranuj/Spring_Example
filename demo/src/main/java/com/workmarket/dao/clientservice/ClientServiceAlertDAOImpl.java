package com.workmarket.dao.clientservice;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.clientservice.ClientServiceAlert;
import org.springframework.stereotype.Repository;



@Repository
public class ClientServiceAlertDAOImpl extends AbstractDAO<ClientServiceAlert> implements ClientServiceAlertDAO {

	protected Class<ClientServiceAlert> getEntityClass() {
		return ClientServiceAlert.class;
	}

	@Override
    public ClientServiceAlert findById(Long alertId)
    {
        return (ClientServiceAlert)getFactory().getCurrentSession().get(ClientServiceAlert.class, alertId);
    }
}