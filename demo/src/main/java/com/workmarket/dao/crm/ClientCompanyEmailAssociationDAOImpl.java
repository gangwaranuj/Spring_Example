package com.workmarket.dao.crm;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.crm.ClientCompanyEmailAssociation;

@Repository
public class ClientCompanyEmailAssociationDAOImpl extends AbstractDAO<ClientCompanyEmailAssociation> implements ClientCompanyEmailAssociationDAO {
		
	protected Class<ClientCompanyEmailAssociation> getEntityClass() {
        return ClientCompanyEmailAssociation.class;
    }

}
