package com.workmarket.dao.crm;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.crm.ClientContactPhoneAssociation;

import java.util.List;

public interface ClientContactPhoneAssociationDAO extends DAOInterface<ClientContactPhoneAssociation>{

	ClientContactPhoneAssociation findByClientContactIdAndPhoneId(long clientContactId, long phoneId);

	List<ClientContactPhoneAssociation> findByClientContactId(long clientContactId);
}
