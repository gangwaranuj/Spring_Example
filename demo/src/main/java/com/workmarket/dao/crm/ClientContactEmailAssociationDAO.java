package com.workmarket.dao.crm;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.crm.ClientContactEmailAssociation;

import java.util.List;

public interface ClientContactEmailAssociationDAO extends DAOInterface<ClientContactEmailAssociation>{

	List<ClientContactEmailAssociation> findAllByClientContactId(long clientContactId);
}
