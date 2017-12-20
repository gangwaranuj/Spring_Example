package com.workmarket.dao.crm;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientContactLocationAssociation;
import com.workmarket.domains.model.crm.ClientLocation;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by arjun on 2014/8/2.
 */

@Repository
public class ClientContactLocationAssociationDAOImpl extends AbstractDAO<ClientContactLocationAssociation> implements ClientContactLocationAssociationDAO {

	protected Class<ClientContactLocationAssociation> getEntityClass() {
		return ClientContactLocationAssociation.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientContactLocationAssociation> findClientContactLocationAssociationByClientContact(Long clientContactId) {
		return (List<ClientContactLocationAssociation>) getFactory().getCurrentSession().createCriteria(ClientContactLocationAssociation.class)
				.setFetchMode("clientLocation", FetchMode.JOIN)
				.add(Restrictions.eq("clientContact.id", clientContactId))
				.add(Restrictions.eq("deleted", false))
				.list();
    }

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientContactLocationAssociation> findClientContactLocationAssociationByClientLocation(Long clientLocationId) {
		return (List<ClientContactLocationAssociation>) getFactory().getCurrentSession().createCriteria(ClientContactLocationAssociation.class)
				.setFetchMode("clientContact", FetchMode.JOIN)
				.add(Restrictions.eq("clientLocation.id", clientLocationId))
				.add(Restrictions.eq("deleted", false))
				.list();
    }

	@Override
	public ClientContactLocationAssociation findClientContactLocationAssociationByClientContactAndClientLocation(Long clientContactId, Long clientLocationId) {
		return (ClientContactLocationAssociation) getFactory().getCurrentSession().createCriteria(ClientContactLocationAssociation.class)
				.add(Restrictions.eq("clientContact.id", clientContactId))
				.add(Restrictions.eq("clientLocation.id", clientLocationId))
				.uniqueResult();
    }

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientLocation> findAllLocationsByClientContact(Long clientContactId) {
		return (List<ClientLocation>) getFactory().getCurrentSession().createCriteria(ClientContactLocationAssociation.class)
		.add(Restrictions.eq("clientContact.id", clientContactId))
		.add(Restrictions.eq("deleted", false))
		.setProjection(Projections.property("clientLocation"))
		.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientContact> findAllClientContactsByLocation(Long clientLocationId) {
		return (List<ClientContact>) getFactory().getCurrentSession().createCriteria(ClientContactLocationAssociation.class)
				.add(Restrictions.eq("clientLocation.id", clientLocationId))
				.add(Restrictions.eq("deleted", false))
				.setProjection(Projections.property("clientContact"))
				.list();
	}

	@Override
	public String findFirstLocationNameByClientContact(Long clientContactId) {
		return (String)getFactory().getCurrentSession().createCriteria(ClientContactLocationAssociation.class)
				.add(Restrictions.eq("clientContact.id", clientContactId))
				.add(Restrictions.eq("deleted", false))
				.createAlias("clientLocation", "cl")
				.setProjection(Projections.property("cl.name"))
				.setFirstResult(0)
				.setMaxResults(1)
				.list().get(0);
	}

	@Override
	public String findFirstContactNameByClientLocation(Long clientLocationId) {
		ClientContact c = (ClientContact) getFactory().getCurrentSession().createCriteria(ClientContactLocationAssociation.class)
				.add(Restrictions.eq("clientLocation.id", clientLocationId))
				.add(Restrictions.eq("deleted", false))
				.setProjection(Projections.property("clientContact"))
				.setFirstResult(0)
				.setMaxResults(1)
				.list().get(0);

		return c.getFullName();
	}

	@Override
	public int getLocationCountByClientContact(Long clientContactId) {
		return getFactory().getCurrentSession().createCriteria(ClientContactLocationAssociation.class)
				.add(Restrictions.eq("clientContact.id", clientContactId))
				.add(Restrictions.eq("deleted", false))
				.list().size();
	}

	@Override
	public int getContactCountByClientLocation(Long clientLocationId) {
		return getFactory().getCurrentSession().createCriteria(ClientContactLocationAssociation.class)
				.add(Restrictions.eq("clientLocation.id", clientLocationId))
				.add(Restrictions.eq("deleted", false))
				.list().size();
	}

}