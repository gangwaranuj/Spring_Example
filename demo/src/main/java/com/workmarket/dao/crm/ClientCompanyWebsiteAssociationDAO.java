package com.workmarket.dao.crm;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.crm.ClientCompanyWebsiteAssociation;

import java.util.List;

public interface ClientCompanyWebsiteAssociationDAO extends DAOInterface<ClientCompanyWebsiteAssociation>{

	List<ClientCompanyWebsiteAssociation> findAllByClientCompanyId(long clientCompanyId);

}
