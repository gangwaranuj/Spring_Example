package com.workmarket.dao.crm;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.crm.ClientCompanyPhoneAssociation;

import java.util.List;

public interface ClientCompanyPhoneAssociationDAO extends DAOInterface<ClientCompanyPhoneAssociation>{

	List<ClientCompanyPhoneAssociation> findAllByClientCompanyId(long clientCompanyId);
}
