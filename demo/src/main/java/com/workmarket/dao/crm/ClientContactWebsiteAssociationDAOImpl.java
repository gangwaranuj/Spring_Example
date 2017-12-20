package com.workmarket.dao.crm;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.crm.ClientContactWebsiteAssociation;

@Repository
public class ClientContactWebsiteAssociationDAOImpl extends AbstractDAO<ClientContactWebsiteAssociation> implements ClientContactWebsiteAssociationDAO {
		
	protected Class<ClientContactWebsiteAssociation> getEntityClass() {
        return ClientContactWebsiteAssociation.class;
    }

}
