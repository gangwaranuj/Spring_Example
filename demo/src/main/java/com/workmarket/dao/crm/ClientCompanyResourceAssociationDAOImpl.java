package com.workmarket.dao.crm;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.crm.ClientCompanyResourceAssociation;

@Repository
public class ClientCompanyResourceAssociationDAOImpl extends AbstractDAO<ClientCompanyResourceAssociation> implements ClientCompanyResourceAssociationDAO {

	protected Class<ClientCompanyResourceAssociation> getEntityClass() {
        return ClientCompanyResourceAssociation.class;
    }

}
