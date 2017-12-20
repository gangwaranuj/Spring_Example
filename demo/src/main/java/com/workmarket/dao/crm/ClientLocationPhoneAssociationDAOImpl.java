package com.workmarket.dao.crm;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.crm.ClientLocationPhoneAssociation;

@Repository
public class ClientLocationPhoneAssociationDAOImpl extends AbstractDAO<ClientLocationPhoneAssociation> implements ClientLocationPhoneAssociationDAO {
		
	protected Class<ClientLocationPhoneAssociation> getEntityClass() {
        return ClientLocationPhoneAssociation.class;
    }

}
