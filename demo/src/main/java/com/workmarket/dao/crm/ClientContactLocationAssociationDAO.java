package com.workmarket.dao.crm;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientContactLocationAssociation;
import com.workmarket.domains.model.crm.ClientLocation;

import java.util.List;

/**
 * Created by arjun on 2014/8/2.
 */
public interface ClientContactLocationAssociationDAO extends DAOInterface<ClientContactLocationAssociation> {

	List<ClientContactLocationAssociation> findClientContactLocationAssociationByClientContact(Long clientContactId);

	List<ClientContactLocationAssociation> findClientContactLocationAssociationByClientLocation(Long clientLocationId);

	ClientContactLocationAssociation findClientContactLocationAssociationByClientContactAndClientLocation(Long clientContactId, Long clientLocationId);

	List<ClientLocation> findAllLocationsByClientContact(Long clientContactId);

	List<ClientContact> findAllClientContactsByLocation(Long clientLocationId);

	String findFirstLocationNameByClientContact(Long clientContactId);

	String findFirstContactNameByClientLocation(Long clientLocationId);

	int getLocationCountByClientContact(Long clientContactId);

	int getContactCountByClientLocation(Long clientLocationId);
}
